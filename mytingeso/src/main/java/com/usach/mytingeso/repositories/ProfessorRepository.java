package com.usach.mytingeso.repositories;

import com.usach.mytingeso.entities.ProfessorEntity;
import com.usach.mytingeso.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<ProfessorEntity, Long> {

}
