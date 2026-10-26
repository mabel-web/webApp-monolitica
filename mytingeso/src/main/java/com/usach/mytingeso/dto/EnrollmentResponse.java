package com.usach.mytingeso.dto;

import com.usach.mytingeso.entities.EnrollmentEntity;
import com.usach.mytingeso.entities.EnrollmentStatus;

public record EnrollmentResponse(
        Long id, String period, Long sectionId,
        String subjectCode, String subjectName, int sct, EnrollmentStatus status, Double finalGrade) {
    public static EnrollmentResponse from(EnrollmentEntity e){
        return new EnrollmentResponse(e.getId(), e.getSection().getPeriod().getCode(),
                e.getSection().getId(), e.getSubjectCode(), e.getSubjectName(),
                e.getSubjectSct(), e.getStatus(), e.getFinalGrade());
    }
}
