package com.usach.mytingeso.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


public record StudentCreateRequest(
    @NotBlank String run,
    @NotBlank String fullName,
    @NotBlank @Email String email,
    @NotNull Long careerId

) {}
