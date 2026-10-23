package com.usach.mytingeso.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EnrollmentCreateRequest(
        @NotNull Long subjectId,
        @NotBlank String period,
        @DecimalMin("1.0") @DecimalMax("7.0") Double finalGrade
) {
}
