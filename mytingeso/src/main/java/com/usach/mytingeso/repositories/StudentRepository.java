package com.usach.mytingeso.repositories;

import com.usach.mytingeso.entities.StudentEntity;
import com.usach.mytingeso.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
}
