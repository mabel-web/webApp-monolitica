package com.usach.mytingeso.dto;

import jakarta.validation.constraints.NotNull;

public record MatriculationRequest(
        @NotNull Long studentId
) {
}
