package com.usach.mytingeso.dto;

import com.usach.mytingeso.enums.ProfessorStatus;
import jakarta.validation.constraints.NotNull;

public record ProfessorStatusRequest(
        @NotNull ProfessorStatus status
        ) {}
