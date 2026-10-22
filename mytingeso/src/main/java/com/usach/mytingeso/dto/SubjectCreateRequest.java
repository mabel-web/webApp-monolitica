package com.usach.mytingeso.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record SubjectCreateRequest(
        @NotBlank String code,
        @NotBlank String name,
        @Min(1) @Max(4) int semester,
        @Min(1) int theoryHours,
        @Min(0) int exerciseHours,
        @Min(0) int labHours,
        @Min(1) @Max(7) int sct,
        List<String> prerequesiteCodes
) {
}
