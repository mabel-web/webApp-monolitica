package com.usach.mytingeso.repositories;

import com.usach.mytingeso.entities.MatriculationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatriculationRepository extends JpaRepository<MatriculationEntity, Long> {
    boolean existsByStudentIdAndPeriodId(Long studentId, Long periodId);
}
