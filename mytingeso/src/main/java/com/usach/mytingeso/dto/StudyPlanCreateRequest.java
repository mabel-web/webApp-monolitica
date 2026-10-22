package com.usach.mytingeso.dto;

import jakarta.validation.constraints.NotBlank;

public record StudyPlanCreateRequest(
        @NotBlank String code,
        @NotBlank String name,
        boolean current
) {
}
