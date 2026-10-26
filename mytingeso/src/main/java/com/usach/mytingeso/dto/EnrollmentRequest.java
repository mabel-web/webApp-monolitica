package com.usach.mytingeso.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record EnrollmentRequest(
        @NotEmpty @Size(max = 6)
        List<@NotNull Long> subjectIds
) {
}
