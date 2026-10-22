package com.usach.mytingeso.dto;

import jakarta.validation.constraints.NotBlank;

public record CareerUpdateRequest(
        @NotBlank String name,
        @NotBlank String description
) {
}
