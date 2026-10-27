package com.usach.mytingeso.repositories;

import com.usach.mytingeso.enums.PeriodStatus;
import com.usach.mytingeso.entities.SectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<SectionEntity, Long> {
    List<SectionEntity> findByPeriodId(Long periodId);
    boolean existsBySubjectIdAndPeriodId(Long subjectId, Long periodId);
    List<SectionEntity> findByProfessorIdAndPeriodId(Long professorId, Long periodId);
    boolean existsByProfessorId(Long professorId);
    boolean existsByProfessorIdAndPeriodStatus(Long professorId, PeriodStatus status);
    Optional<SectionEntity> findBySubjectIdAndPeriodId(Long subjectId, Long periodId);
}
