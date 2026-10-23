package com.usach.mytingeso.repositories;

import com.usach.mytingeso.entities.ProfessorEntity;
import com.usach.mytingeso.entities.ProfessorStatus;
import com.usach.mytingeso.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfessorRepository extends JpaRepository<ProfessorEntity, Long> {
    List<ProfessorEntity> findByStatus(ProfessorStatus status);

}
