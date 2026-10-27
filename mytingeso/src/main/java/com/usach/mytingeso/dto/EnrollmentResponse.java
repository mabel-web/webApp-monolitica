package com.usach.mytingeso.dto;

import com.usach.mytingeso.entities.EnrollmentEntity;
import com.usach.mytingeso.enums.EnrollmentResult;
import com.usach.mytingeso.enums.EnrollmentStatus;

import java.time.LocalDateTime;

public record EnrollmentResponse(
        Long id, Long studentId, String period, Long sectionId,
        String subjectCode, String subjectName, int sct, EnrollmentStatus status, Double finalGrade,
        EnrollmentResult result, LocalDateTime enrolledAt) {
    public static EnrollmentResponse from(EnrollmentEntity e){
        return new EnrollmentResponse(e.getId(), e.getStudent().getId(), e.getPeriod().getCode(), e.getSection().getId(), e.getSubjectCode(),e.getSubjectName(),
                e.getSubjectSct(), e.getStatus(), e.getFinalGrade(), e.getResult(), e.getEnrolledAt());
    }
}
