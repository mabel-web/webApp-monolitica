package com.usach.mytingeso.services;

import com.usach.mytingeso.dto.AvailableSubjectResponse;
import com.usach.mytingeso.dto.EnrollmentRequest;
import com.usach.mytingeso.dto.EnrollmentResponse;
import com.usach.mytingeso.dto.MatriculationResponse;
import com.usach.mytingeso.entities.*;
import com.usach.mytingeso.enums.AcademicState;
import com.usach.mytingeso.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.usach.mytingeso.enums.EnrollmentStatus;
import com.usach.mytingeso.enums.PeriodStatus;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private static final int MIN_SUBJECTS = 3;
    private static final int MAX_SUBJECTS = 6;
    private static final double PASSING_GRADE = 4.0;
    private final EnrollmentRepository enrollmentRepository;
    private final MatriculationRepository matriculationRepository;
    private final StudentRepository studentRepository;
    private final AcademicPeriodRepository academicPeriodRepository;
    private final SectionRepository sectionRepository;
    private final SubjectRepository subjectRepository;

    @Transactional
    public MatriculationResponse matriculate(Long periodId, Long studentId){
        AcademicPeriodEntity period = findPeriod(periodId);
        StudentEntity student = findStudent(studentId);
        if(matriculationRepository.existsByStudentIdAndPeriodId(studentId, periodId)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El/La estudiante ya esta matriculado en este periodo");
        }

        MatriculationEntity m = new MatriculationEntity();
        m.setStudent(student);
        m.setPeriod(period);
        m = matriculationRepository.save(m);
        return new MatriculationResponse(m.getId(), studentId, period.getCode());
    }

    @Transactional
    public List<AvailableSubjectResponse> findAvailable(Long studentId, Long periodId){
        StudentEntity student = findStudent(studentId);
        findPeriod(periodId);
        Long planId = student.getStudyPlan().getId();
        Set<Long> active = activeSubjectIds(studentId, periodId);

        return sectionRepository.findByPeriodId(periodId).stream()
                .filter(s -> s.getSubject().getStudyPlan().getId().equals(planId))
                .filter(s -> !isApproved(studentId, s.getSubject().getId()))
                .filter(s -> !active.contains(s.getSubject().getId()))
                .map(s -> new AvailableSubjectResponse(
                        s.getSubject().getId(), s.getSubject().getCode(), s.getSubject().getName(),
                        s.getSubject().getSemester(), s.getId(), s.getProfessor().getFullName(),
                        s.getSchedule().stream().sorted().toList(),
                        s.getCapacity() - countActive(s.getId()),
                        prerequisitesMet(s.getSubject(), studentId)))
                .toList();
    }
    @Transactional
    public List<EnrollmentResponse> enroll(Long studentId, Long periodId, EnrollmentRequest r){
        StudentEntity student = findStudent(studentId);
        AcademicPeriodEntity period = findPeriod(periodId);

        if (period.getStatus() != PeriodStatus.ABIERTO){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El periodo academico es CERRADO");
        }
        if (student.getAcademicState() != AcademicState.REGULAR){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Estudiante debe ser REGULAR para enrollarse.");
        }

        if (!matriculationRepository.existsByStudentIdAndPeriodId(studentId, periodId)){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Estudiante no matriculado en este periodo.");

        }
        List<Long> ids = r.subjectIds();
        if (new HashSet<>(ids).size() != ids.size()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Asignatura duplicada en la solicitud.");
        }
        Long planId = student.getStudyPlan().getId();
        List<EnrollmentEntity> active = enrollmentRepository
                .findByStudentIdAndSectionPeriodIdAndStatus(studentId, periodId, EnrollmentStatus.ACTIVA);
        Set<Long> activeIds = active.stream().map(e -> e.getSubject().getId()).collect(Collectors.toSet());

        List<SectionEntity> selected = new ArrayList<>();
        for (Long subjectId : ids) {
            SubjectEntity subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Subject not found: " + subjectId));
            // RN-02
            if (!subject.getStudyPlan().getId().equals(planId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Subject " + subject.getCode() + " is not in the student's study plan");
            }
            SectionEntity section = sectionRepository.findBySubjectIdAndPeriodId(subjectId, periodId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT,
                            "Subject " + subject.getCode() + " is not offered in this period"));

            if (activeIds.contains(subjectId)){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Already enrolled in" + subject.getCode() + "en este periodo");
            }

            if (isApproved(studentId, subjectId)){
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Asignatura " + subject.getCode() + " ya esta aprovada.");

            }
            for (SubjectEntity p : subject.getPrerequisites()){
                if (!isApproved(studentId, p.getId())){
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Pre requisitos no aprobados por " + subject.getCode() + ":"+ p.getCode());
                }
            }
            if (countActive(section.getId()) >= section.getCapacity()){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Section of " + subject.getCode() + " has no seats left");
            }
            selected.add(section);
    }
    List<SubjectEntity> pendingOffered = sectionRepository.findByPeriodId(periodId).stream()
            .map(SectionEntity::getSubject)
            .filter( s -> s.getStudyPlan().getId().equals(planId))
                .filter(s -> !isApproved(studentId, s.getId()))
                .toList();

            int total = active.size() + selected.size();
            int required = Math.min(MIN_SUBJECTS, pendingOffered.size());
            if (total > MAX_SUBJECTS){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Un estudiante puede inscribir maximo " + MAX_SUBJECTS + "asignaturas por periodo.");
            }
            if (total < required){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Un estudiante debe inscribir como minimo " + required + "asignaturas en este periodo");
            }

            Set<Long> chosenIds = new HashSet<>(ids);
            List<SubjectEntity> stillAvailable = pendingOffered.stream()
                    .filter(s -> !chosenIds.contains(s.getId()) && !activeIds.contains(s.getId()))
                    .filter(s -> prerequisitesMet(s, studentId))
                    .toList();
            for (SectionEntity sec : selected){
                for (SubjectEntity lower : stillAvailable){
                    if (lower.getSemester() < sec.getSubject().getSemester()){
                        throw  new ResponseStatusException(HttpStatus.CONFLICT, "No puede inscribir " + sec.getSubject().getCode() + "mientras " + lower.getCode() + "sigue disponible");
                    }
                }
            }
            Map<String, String> used = new HashMap<>();
            List<SectionEntity> all = new ArrayList<>(selected);
            active.forEach(e -> all.add(e.getSection()));
            for (SectionEntity sec : all){
                for (String code : sec.getSchedule()){
                    String previous = used.put(code, sec.getSubject().getCode());
                    if (previous != null){
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Conflicto de horario en " + code + "entre " + previous + "y " + sec.getSubject().getCode());
                    }
                }
            }
            List<EnrollmentResponse> result = new ArrayList<>();
            for (SectionEntity sec : selected){
                EnrollmentEntity e = new EnrollmentEntity();
                e.setStudent(student);
                e.setSubject(sec.getSubject());
                e.setSection(sec);
                e.setSubjectCode(sec.getSubject().getCode());
                e.setSubjectName(sec.getSubject().getName());
                e.setSubjectSct(sec.getSubject().getSct());
                e.setStatus(EnrollmentStatus.ACTIVA);
                result.add(EnrollmentResponse.from(enrollmentRepository.save(e)));
            }
            return result;
    }

    //
    @Transactional
    public EnrollmentResponse annul(Long studentId, Long enrollmentId){
        EnrollmentEntity e = findEnrollment(enrollmentId);
        if (!e.getStudent().getId().equals(studentId)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscripción no enontrada para este estudiante");
        }
        if (e.getPeriod().getStatus() != PeriodStatus.ABIERTO){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El estado del periodo academico es CERRADO");

        }
        if (e.getStatus() != EnrollmentStatus.ACTIVA){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Inscripcion ya fue anulada.");
        }
        if (e.getFinalGrade() != null){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Las inscripciones con notas no pueden ser anuladas");
        }
        e.setStatus(EnrollmentStatus.ANULADA);
        return EnrollmentResponse.from(enrollmentRepository.save(e));
    }

    //
    private boolean isApproved(Long studentId, Long subjectId){
        return enrollmentRepository.existsByStudentIdAndSubjectIdAndFinalGradeGreaterThanEqual(studentId, subjectId, PASSING_GRADE);
    }

    private boolean prerequisitesMet(SubjectEntity subject, Long studentId){
        return subject.getPrerequisites().stream().allMatch(p ->isApproved(studentId, p.getId()));
    }

    private long countActive(Long sectionId){
        return enrollmentRepository.countBySectionIdAndStatus(sectionId, EnrollmentStatus.ACTIVA);
    }
    private Set<Long> activeSubjectIds(Long studentId, Long periodId){
        return enrollmentRepository
                .findByStudentIdAndSectionPeriodIdAndStatus(studentId, periodId, EnrollmentStatus.ACTIVA)
                .stream().map(e -> e.getSubject().getId()).collect(Collectors.toSet());
    }

    private StudentEntity findStudent(Long id){
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no encontrado"));
    }

    private AcademicPeriodEntity findPeriod(Long id){
        return academicPeriodRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Periodo academico no encontrado"));
    }

    private EnrollmentEntity findEnrollment(Long id){
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscripción no encontrado"));
    }

}
