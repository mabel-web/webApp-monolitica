package com.usach.mytingeso.repositories;

import com.usach.mytingeso.entities.AcademicAdministratorEntity;
import com.usach.mytingeso.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademicAdministratorRepository extends JpaRepository<AcademicAdministratorEntity, Long> {
}
