package com.usach.mytingeso.repositories;

import com.usach.mytingeso.entities.PeriodStatus;
import com.usach.mytingeso.entities.SectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<SectionEntity, Long> {
    List<SectionEntity> findByPeriodId(Long periodId);
    boolean existsBySubjectIdAndPeriodId(Long subjectId, Long periodId);
    List<SectionEntity> findByProfessorIdAndPeriodId(Long professorId, Long periodId);
    boolean existsByProfessorId(Long professorId);
    boolean existsByProfessorIdAndPeriodStatus(Long professorId, PeriodStatus status);
}
