package com.usach.mytingeso.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ProfessorCreateRequest(
        @NotBlank String run,
        @NotBlank String fullName,
        @NotBlank @Email String email,
        @NotBlank String professionalTitle,
        @NotBlank String academicDegree
) {
}
