package com.usach.mytingeso.repositories;

import com.usach.mytingeso.entities.EnrollmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<EnrollmentEntity, Long> {
    boolean existsByStudentId(Long studentId);
    boolean existsBySubjectId(Long subjectId);
    List<EnrollmentEntity> findByStudentId(Long studentId);
}
