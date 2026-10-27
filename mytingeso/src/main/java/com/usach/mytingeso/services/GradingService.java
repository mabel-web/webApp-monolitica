package com.usach.mytingeso.services;

import com.usach.mytingeso.dto.EnrollmentResponse;
import com.usach.mytingeso.dto.GradeRequest;
import com.usach.mytingeso.dto.PeriodCloseResponse;
import com.usach.mytingeso.entities.*;
import com.usach.mytingeso.enums.AcademicState;
import com.usach.mytingeso.enums.EnrollmentResult;
import com.usach.mytingeso.enums.EnrollmentStatus;
import com.usach.mytingeso.enums.PeriodStatus;
import com.usach.mytingeso.repositories.AcademicPeriodRepository;
import com.usach.mytingeso.repositories.EnrollmentRepository;
import com.usach.mytingeso.repositories.SectionRepository;
import com.usach.mytingeso.repositories.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GradingService {
    private static final double PASSING_GRADE = 4.0;
    private final EnrollmentRepository enrollmentRepository;
    private final SectionRepository sectionRepository;
    private final AcademicPeriodRepository periodRepository;
    private final SubjectRepository subjectRepository;

    @Transactional
    public List<EnrollmentResponse> findEnrolled(Long sectionId){
        findSection(sectionId);
        return enrollmentRepository.findBySectionIdAndStatus(sectionId, EnrollmentStatus.ACTIVA)
                .stream().map(EnrollmentResponse::from).toList();
    }

    @Transactional
    public EnrollmentResponse grade(Long sectionId, Long enrollmentId, GradeRequest r){
        SectionEntity section = findSection(sectionId);

        if (!section.getProfessor().getId().equals(r.professorId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the responsible professor can grade this section.");
        }
        if (section.getPeriod().getStatus() != PeriodStatus.ABIERTO){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Academic period is CERRADO");

        }
        EnrollmentEntity e = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));
        if (!e.getSection().getId().equals(sectionId)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not enrolled in this section");
        }
        if (e.getStatus() != EnrollmentStatus.ACTIVA){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Enrollment is anulled");
        }
        e.setFinalGrade(r.finalGrade());
        e.setResult(r.finalGrade() >= PASSING_GRADE ? EnrollmentResult.APROBADA : EnrollmentResult.REPROBADA);
        return EnrollmentResponse.from(enrollmentRepository.save(e));
    }
    @Transactional
    public PeriodCloseResponse close (Long periodId){
        AcademicPeriodEntity period = periodRepository.findById(periodId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Academic period not found"));
        if (period.getStatus() != PeriodStatus.ABIERTO){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Academic period is already CERRADO");
        }
        List<EnrollmentEntity> active = enrollmentRepository.findByPeriodIdAndStatus(periodId, EnrollmentStatus.ACTIVA);

        //
        List<String> incomplete = active.stream()
                .filter(e -> e.getFinalGrade() == null)
                .map(e -> "section " + e.getSection().getId() + "(" + e.getSubjectCode() + ")")
                .distinct().sorted().toList();
        if (!incomplete.isEmpty()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot close the period, sections with pending grades:" + String.join(",", incomplete));
        }

        LocalDateTime now  = LocalDateTime.now();
        for (EnrollmentEntity e : active){
            e.setResult(e.getFinalGrade() >= PASSING_GRADE ? EnrollmentResult.APROBADA : EnrollmentResult.REPROBADA);
            e.setConsolidatedAt(now);
        }
        enrollmentRepository.saveAll(active);


        Map<Long, StudentEntity> students = new LinkedHashMap<>();
        active.forEach(e -> students.putIfAbsent(e.getStudent().getId(), e.getStudent()));

        int graduated = 0, eliminated = 0;
        for (StudentEntity s : students.values()){
            if (s.getAcademicState() == AcademicState.EGRESADO || s.getAcademicState() == AcademicState.ELIMINADO) continue;
            if (hasApprovedAllSubjects(s)){
                s.setAcademicState(AcademicState.EGRESADO);
                graduated++;
            } else if (hasFailedSameSubjectTwice(s, active)){
                s.setAcademicState(AcademicState.ELIMINADO);
                eliminated++;
            }
        }

        period.setStatus(PeriodStatus.CERRADO);
        periodRepository.save(period);
        return new PeriodCloseResponse(period.getCode(), active.size(), graduated, eliminated);
    }

    //
    private boolean hasApprovedAllSubjects(StudentEntity s){
        List<SubjectEntity> planSubjects = subjectRepository.findByStudyPlanId(s.getStudyPlan().getId());
        return !planSubjects.isEmpty() && planSubjects.stream().allMatch(sub ->
                enrollmentRepository.existsByStudentIdAndSubjectIdAndFinalGradeGreaterThanEqual(
                        s.getId(), sub.getId(), PASSING_GRADE));
    }

    private boolean hasFailedSameSubjectTwice(StudentEntity s, List<EnrollmentEntity> periodEnrollments){
        return periodEnrollments.stream()
                .filter(e -> e.getStudent().getId().equals(s.getId()))
                .filter(e -> e.getResult() == EnrollmentResult.REPROBADA)
                .anyMatch(e -> enrollmentRepository.countByStudentIdAndSubjectIdAndFinalGradeLessThan(
                        s.getId(), e.getSubject().getId(), PASSING_GRADE) >= 2);
    }

    private SectionEntity findSection(Long id){
        return sectionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Section not found"));
    }



}


