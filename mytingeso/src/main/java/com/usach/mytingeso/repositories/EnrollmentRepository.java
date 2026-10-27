package com.usach.mytingeso.repositories;

import com.usach.mytingeso.entities.EnrollmentEntity;
import com.usach.mytingeso.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<EnrollmentEntity, Long> {
    boolean existsByStudentId(Long studentId);
    boolean existsBySubjectId(Long subjectId);
    List<EnrollmentEntity> findByStudentId(Long studentId);
    boolean existsBySectionId(Long sectionId);
    long countBySectionIdAndStatus(Long sectionId, EnrollmentStatus status);
    List<EnrollmentEntity> findByStudentIdAndSectionPeriodIdAndStatus(
            Long studentId, Long periodId, EnrollmentStatus status);
    boolean existsByStudentIdAndSubjectIdAndFinalGradeGreaterThanEqual(
            Long studentId, Long subjectId, Double grade);
    List<EnrollmentEntity> findByPeriodIdAndStatus(Long periodId, EnrollmentStatus status);
    List<EnrollmentEntity> findBySectionIdAndStatus(Long sectionId, EnrollmentStatus status);
    long countByStudentIdAndSubjectIdAndFinalGradeLessThan(Long studentId, Long subjectId, Double grade);
}
