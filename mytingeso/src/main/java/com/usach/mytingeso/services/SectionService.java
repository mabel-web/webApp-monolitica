package com.usach.mytingeso.services;

import com.usach.mytingeso.dto.SectionCreateRequest;
import com.usach.mytingeso.dto.SectionResponse;
import com.usach.mytingeso.dto.SectionUpdateRequest;
import com.usach.mytingeso.entities.*;
import com.usach.mytingeso.enums.EnrollmentStatus;
import com.usach.mytingeso.enums.PeriodStatus;
import com.usach.mytingeso.enums.ProfessorStatus;
import com.usach.mytingeso.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SectionService {
    private static final Pattern SCHEDULE_CODE = Pattern.compile("^[LMWJV][1-6]$");
    private final SectionRepository sectionRepository;
    private final AcademicPeriodRepository periodRepository;
    private final SubjectRepository subjectRepository;
    private final ProfessorRepository professorRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Transactional(readOnly = true)
    public List<SectionResponse> findByPeriod(Long periodId){
        findPeriod(periodId);
        return sectionRepository.findByPeriodId(periodId).stream().map(this::toResponse).toList();
    }
    @Transactional(readOnly = true)
    public SectionResponse findById(Long id){
        return toResponse(findSection(id));
    }

    @Transactional
    public SectionResponse create(Long periodId, SectionCreateRequest r){
        AcademicPeriodEntity period = findPeriod(periodId);
        requireOpen(period);

        SubjectEntity subject = subjectRepository.findById(r.subjectId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found"));
        //
        if (sectionRepository.existsBySubjectIdAndPeriodId(subject.getId(), periodId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Subject already has a section in this period");
        }
        ProfessorEntity professor = findActiveProfessor(r.professorId());
        //
        Set<String> schedules = normalizeSchedules(r.schedules(), subject);
        // R
        checkProfessorConflicts(professor.getId(), periodId, schedules, null);

        SectionEntity section = new SectionEntity();
        section.setSubject(subject);
        section.setPeriod(period);
        section.setProfessor(professor);
        section.setCapacity(r.capacity());
        section.setSchedule(schedules);
        return toResponse(sectionRepository.save(section));
    }

    @Transactional
    public SectionResponse update(Long id, SectionUpdateRequest r) {
        SectionEntity section = findSection(id);
        requireOpen(section.getPeriod());

        // RN-06: capacity cannot go below the enrolled students
        if (r.capacity() < countEnrolled(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Capacity cannot be lower than the number of enrolled students");
        }
        // RN-04: only validate ACTIVO when the professor actually changes
        ProfessorEntity professor = section.getProfessor();
        if (!professor.getId().equals(r.professorId())) {
            professor = findActiveProfessor(r.professorId());
        }
        Set<String> schedules = normalizeSchedules(r.schedules(), section.getSubject());
        checkProfessorConflicts(professor.getId(), section.getPeriod().getId(), schedules, id);

        section.setProfessor(professor);
        section.setCapacity(r.capacity());
        section.getSchedule().clear();
        section.getSchedule().addAll(schedules);
        return toResponse(sectionRepository.save(section));
    }

    @Transactional
    public void delete(Long id) {
        SectionEntity section = findSection(id);
        //
        if (countEnrolled(id) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La sección tiene estudiantes inscritos/as y no puede ser elimanda.");
        } if (enrollmentRepository.existsBySubjectId(id)){
            throw  new ResponseStatusException(HttpStatus.CONFLICT, "La sección tiene historial de inscripciones y no puede ser eliminada.");
        }
        sectionRepository.delete(section);
    }

    // ---- helpers ----

    private Set<String> normalizeSchedules(List<String> raw, SubjectEntity subject) {
        List<String> codes = raw.stream().map(b -> b.trim().toUpperCase()).toList();
        for (String code : codes) {
            if (!SCHEDULE_CODE.matcher(code).matches()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Invalid schedule block: " + code + " (day L,M,W,J,V + module 1-6)");
            }
        }
        Set<String> unique = new LinkedHashSet<>(codes);
        if (unique.size() != codes.size()) {                       // RN-09
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duplicate schedule blocks");
        }
        int totalHours = subject.getTheoryHours() + subject.getExerciseHours() + subject.getLabHours();
        if (totalHours % 2 != 0 || unique.size() != totalHours / 2) {   // RN-08
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Subject has " + totalHours + " weekly TEL hours: exactly "
                            + (totalHours / 2) + " blocks are required");
        }
        return unique;
    }

    private void checkProfessorConflicts(Long professorId, Long periodId,
                                         Set<String> newSchedules, Long excludeSectionId) {
        for (SectionEntity other : sectionRepository.findByProfessorIdAndPeriodId(professorId, periodId)) {
            if (excludeSectionId != null && other.getId().equals(excludeSectionId)) continue;
            for (String existing : other.getSchedule()) {
                if (newSchedules.contains(existing)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Professor already has section " + other.getSubject().getCode()
                                    + " at block " + existing);
                }
            }
        }
    }

    private ProfessorEntity findActiveProfessor(Long professorId) {
        ProfessorEntity p = professorRepository.findById(professorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor not found"));
        if (p.getStatus() != ProfessorStatus.ACTIVO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Professor is not ACTIVO");
        }
        return p;
    }

    private void requireOpen(AcademicPeriodEntity period) {
        if (period.getStatus() != PeriodStatus.ABIERTO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Academic period is CERRADO");
        }
    }

    private AcademicPeriodEntity findPeriod(Long id) {
        return periodRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Periodo academico no encontrado"));
    }

    private SectionEntity findSection(Long id) {
        return sectionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sección no encontrada."));
    }

    // epica 6)
    private int countEnrolled(Long sectionId) {
        return (int) enrollmentRepository.countBySectionIdAndStatus(sectionId, EnrollmentStatus.ACTIVA);
    }

    private SectionResponse toResponse(SectionEntity s) {
        return new SectionResponse(s.getId(), s.getPeriod().getCode(),
                s.getSubject().getCode(), s.getSubject().getName(),
                s.getProfessor().getId(), s.getProfessor().getFullName(),
                s.getCapacity(), countEnrolled(s.getId()),
                s.getSchedule().stream().sorted().toList());

    }

}
