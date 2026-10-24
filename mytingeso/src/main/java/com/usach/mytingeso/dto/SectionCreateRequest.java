package com.usach.mytingeso.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SectionCreateRequest(
        @NotNull Long subjectId,
        @NotNull Long professorId,
        @Min(1) @Max(60) int capacity,
        @NotEmpty List<String> schedules
) {
}
