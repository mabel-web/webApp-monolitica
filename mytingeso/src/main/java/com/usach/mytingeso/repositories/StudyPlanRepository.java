package com.usach.mytingeso.repositories;

import com.usach.mytingeso.entities.StudyPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyPlanRepository extends JpaRepository<StudyPlanEntity, Long> {
    Optional<StudyPlanEntity> findByCareerIdAndCurrentTrue(Long careerId); //consulta de la carrera con plan vigente
    Optional<StudyPlanEntity> findByIdAndCareerId(Long id, Long careerId);
    boolean existsByCareerId(Long careerId); //no seria code???

    List<StudyPlanEntity> findByCareerId(Long careerId);
    boolean existsByCareerIdAndCode(Long careerId, String code);
}
