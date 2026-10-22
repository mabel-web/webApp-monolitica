package com.usach.mytingeso.repositories;

import com.usach.mytingeso.entities.SubjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<SubjectEntity, Long> {
    List<SubjectEntity> findByStudyPlanId(Long studyPlanId);
    Optional<SubjectEntity> findByStudyPlanIdAndCode(Long studyPlanId, String code);
    boolean existsByStudyPlanIdAndCode(Long studyPlanId, String code);
    boolean existsByPrerequisitesId(Long subjectId);
}
