package com.usach.mytingeso.repositories;

import com.usach.mytingeso.entities.EnrollmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<EnrollmentEntity, Long> {
    boolean existsByStudentId(Long studentId);

    List<EnrollmentEntity> findByStudentId(Long studentId);
}
