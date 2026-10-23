package com.usach.mytingeso.services;

import com.usach.mytingeso.dto.SubjectCreateRequest;
import com.usach.mytingeso.dto.SubjectResponse;
import com.usach.mytingeso.dto.SubjectUpdateRequest;
import com.usach.mytingeso.entities.StudyPlanEntity;
import com.usach.mytingeso.entities.SubjectEntity;
import com.usach.mytingeso.repositories.EnrollmentRepository;
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
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final StudyPlanRepository studyPlanRepository;
    private final EnrollmentRepository enrollmentRepository;

    public List<SubjectResponse> findByPlan(Long planId){
        findPlan(planId);
        return subjectRepository.findByStudyPlanId(planId).stream().map(this::toResponse).toList();
    }
    public SubjectResponse findById(Long id){
        return toResponse(findSubject(id));
    }

    @Transactional
    public SubjectResponse create(Long planId, SubjectCreateRequest r){
        StudyPlanEntity plan = findPlan(planId);
        String code = r.code().trim().toUpperCase();
        if (subjectRepository.existsByStudyPlanIdAndCode(planId, code)){
            throw  new ResponseStatusException(HttpStatus.CONFLICT, "El codigo de esta asignatura ya existe en el plan");
        }

        validateHoursAndPrerequisiteCount(r.theoryHours(), r.exerciseHours(), r.labHours(), r.prerequisiteCodes());

        SubjectEntity s = new SubjectEntity();
        s.setCode(code);
        s.setStudyPlan(plan);
        apply(s, r.name(), r.semester(), r.theoryHours(), r.exerciseHours(), r.labHours(), r.sct(), r.prerequisiteCodes());
        return toResponse(subjectRepository.save(s));
    }

    @Transactional
    public SubjectResponse update(Long id, SubjectUpdateRequest r){
        SubjectEntity s = findSubject(id);
        validateHoursAndPrerequisiteCount(r.theoryHours(), r.exerciseHours(), r.labHours(), r.prerequisiteCodes());
        apply(s, r.name(), r.semester(), r.theoryHours(), r.exerciseHours(), r.labHours(), r.sct(), r.prerequisiteCodes());
        return toResponse(subjectRepository.save(s));
    }
    @Transactional
    public void delete(Long id) {
        SubjectEntity s = findSubject(id);
        if (isInAcademicHistory(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La asignatura no puede eliminarse por ser parte del historial academico");
        }
        if (subjectRepository.existsByPrerequisitesId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Esta asignatura es prerequisito de otras asignaturas.");
        }
        subjectRepository.delete(s);
    }

        private void validateHoursAndPrerequisiteCount(int t, int e, int l, List<String> prereqs) {
            if (t + e + l >= 8) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Total weekly hours must be less than 8");
            }
            if (prereqs != null && prereqs.size() > 3) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "A subject can have at most 3 prerequisites");
            }
        }

        private void apply(SubjectEntity s, String name, int semester, int t, int e, int l,
        int sct, List<String> prereqCodes) {
            s.setName(name.trim());
            s.setSemester(semester);
            s.setTheoryHours(t);
            s.setExerciseHours(e);
            s.setLabHours(l);
            s.setSct(sct);
            s.getPrerequisites().clear();
            if (prereqCodes == null) return;
            for (String pc : prereqCodes) {
                SubjectEntity p = subjectRepository
                        .findByStudyPlanIdAndCode(s.getStudyPlan().getId(), pc.trim().toUpperCase())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Prerequisite not found in this plan: " + pc));
                if (p.getSemester() >= semester) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Prerequisite must belong to an earlier semester: " + pc);
                }
                s.getPrerequisites().add(p);
            }
        }

        private boolean isInAcademicHistory(Long subjectId) {
            return enrollmentRepository.existsBySubjectId(subjectId);
        }

        private StudyPlanEntity findPlan(Long id) {
            return studyPlanRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Study plan not found"));
        }

        private SubjectEntity findSubject(Long id) {
            return subjectRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Subject not found"));
        }

        private SubjectResponse toResponse(SubjectEntity s) {
            return new SubjectResponse(s.getId(), s.getCode(), s.getName(), s.getSemester(),
                    s.getTheoryHours() + "-" + s.getExerciseHours() + "-" + s.getLabHours(), s.getSct(),
                    s.getPrerequisites().stream().map(SubjectEntity::getCode).sorted().toList());
        }


}
