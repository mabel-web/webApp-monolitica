package com.usach.mytingeso.repositories;

import com.usach.mytingeso.entities.StudyPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudyPlanRepository extends JpaRepository<StudyPlanEntity, Long> {
    Optional<StudyPlanEntity> findByCareerIdAndCurrentTrue(Long careerId); //consulta de la carrera con plan vigente
}
