package com.usach.mytingeso.services;

import com.usach.mytingeso.dto.StudyPlanCreateRequest;
import com.usach.mytingeso.dto.SubjectResponse;
import com.usach.mytingeso.entities.CareerEntity;
import com.usach.mytingeso.entities.StudyPlanEntity;
import com.usach.mytingeso.entities.SubjectEntity;
import com.usach.mytingeso.repositories.CareerRepository;
import com.usach.mytingeso.repositories.StudentRepository;
import com.usach.mytingeso.repositories.StudyPlanRepository;
import com.usach.mytingeso.repositories.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyPlanService {

    private final StudyPlanRepository studyPlanRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final CareerRepository careerRepository;

    public List<StudyPlanEntity> findByCareer(Long careerId){
        careerRepository.findById(careerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carrera no encontrada"));
        return studyPlanRepository.findByCareerId(careerId);
    }

    public StudyPlanEntity findById(Long id){
        return studyPlanRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plan de estudios no encontrada."));
    }

    @Transactional
    public StudyPlanEntity create(Long careerId, StudyPlanCreateRequest request){
        CareerEntity career = careerRepository.findById(careerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Carrera no encontrada"));

    String code = request.code().trim();
    //regla
    if (studyPlanRepository.existsByCareerIdAndCode(careerId, code)){
        throw new ResponseStatusException(HttpStatus.CONFLICT, "El codigo del plan de estudios ya existe en la carrera");
    }
    if (request.current()){
        unsetCurretPlan(careerId);
    }

    StudyPlanEntity plan = new StudyPlanEntity();
    plan.setCode(code);
    plan.setName(request.name().trim());
    plan.setCurrent(request.current());
    plan.setCareer(career);
    return studyPlanRepository.save(plan);

    }
    @Transactional
    public StudyPlanEntity setCurrent(Long planId) {
        StudyPlanEntity plan = findById(planId);
        unsetCurrentPlan(plan.getCareer().getId());   // RN-09
        plan.setCurrent(true);
        return studyPlanRepository.save(plan);

}
    @Transactional
    public void delete(Long planId) {
        StudyPlanEntity plan = findById(planId);
        // RN-07
        if (studentRepository.existsByStudyPlanId(planId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El plan de estudios aun tiene estudiantes y no puede ser eliminado.");
        }
        subjectRepository.deleteAll(subjectRepository.findByStudyPlanId(planId));
        studyPlanRepository.delete(plan);

}

    @Transactional
    public SubjectResponse addSubject(Long planId, SubjectCreateRequest request) {
        StudyPlanEntity plan = findById(planId);
        String code = request.code().trim().toUpperCase();

        if (subjectRepository.existsByStudyPlanIdAndCode(planId, code)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Subject code already exists in this plan");
        }

        SubjectEntity subject = new SubjectEntity();
        subject.setCode(code);
        subject.setName(request.name().trim());
        subject.setSemester(request.semester());
        subject.setTheoryHours(request.theoryHours());
        subject.setExerciseHours(request.exerciseHours());
        subject.setLabHours(request.labHours());
        subject.setSct(request.sct());
        subject.setStudyPlan(plan);

        if (request.prerequisiteCodes() != null) {
            for (String prereqCode : request.prerequisiteCodes()) {
                SubjectEntity prereq = subjectRepository
                        .findByStudyPlanIdAndCode(planId, prereqCode.trim().toUpperCase())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Prerequisite not found in this plan: " + prereqCode));
                // assumption: a prerequisite must belong to an earlier semester
                if (prereq.getSemester() >= request.semester()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Prerequisite must belong to an earlier semester: " + prereqCode);
                }
                subject.getPrerequisites().add(prereq);
            }
        }
        return toResponse(subjectRepository.save(subject));
    }

    @Transactional(readOnly = true)
    public List<SubjectResponse> findSubjects(Long planId) {
        findById(planId);
        return subjectRepository.findByStudyPlanId(planId).stream()
                .map(this::toResponse)
                .toList();
    }

    // ---- helpers ----

    private void unsetCurrentPlan(Long careerId) {
        studyPlanRepository.findByCareerIdAndCurrentTrue(careerId).ifPresent(old -> {
            old.setCurrent(false);
            studyPlanRepository.save(old);
        });
    }

    private SubjectResponse toResponse(SubjectEntity s) {
        return new SubjectResponse(
                s.getId(), s.getCode(), s.getName(), s.getSemester(),
                s.getTheoryHours() + "-" + s.getExerciseHours() + "-" + s.getLabHours(),
                s.getSct(),
                s.getPrerequisites().stream().map(SubjectEntity::getCode).sorted().toList());
    }

}