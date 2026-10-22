package com.usach.mytingeso.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StudentUpdateRequest(
    @NotBlank String fullName, //no modifico el ruy despues del registro
    @NotBlank @Email String email,
    @NotNull Long careerId,
    @NotNull Long studyPlanId
){}
