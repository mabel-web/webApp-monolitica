package com.usach.mytingeso.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record PeriodCreateRequest(
        @Min(2000) @Max(2100) int year,
        @Min(1) @Max (2) int semester
) {
}
