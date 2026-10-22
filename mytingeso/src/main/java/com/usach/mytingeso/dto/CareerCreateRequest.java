package com.usach.mytingeso.dto;

import jakarta.validation.constraints.NotBlank;

public record CareerCreateRequest(
        @NotBlank String code,
        @NotBlank String name,
        @NotBlank String description
) {
}
