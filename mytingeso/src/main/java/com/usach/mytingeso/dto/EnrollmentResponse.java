package com.usach.mytingeso.dto;

public record EnrollmentResponse(Long id, String period,
                                 String subjectCode, String subjectName, int sct, Double finalGrade) {
}
