package com.usach.mytingeso.repositories;

import com.usach.mytingeso.entities.StudentEntity;
import com.usach.mytingeso.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
    Optional<StudentEntity> findByIdAndCareerId(Long id, Long careerId);
    boolean existsByCareerId(Long careerId);
    boolean existsByStudyPlanId(Long id);
}
