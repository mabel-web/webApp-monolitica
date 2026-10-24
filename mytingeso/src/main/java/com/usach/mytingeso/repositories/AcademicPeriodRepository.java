package com.usach.mytingeso.repositories;

import com.usach.mytingeso.entities.AcademicPeriodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcademicPeriodRepository extends JpaRepository<AcademicPeriodEntity, Long> {
    boolean existsByYearAndSemester(int year, int semester);
}
