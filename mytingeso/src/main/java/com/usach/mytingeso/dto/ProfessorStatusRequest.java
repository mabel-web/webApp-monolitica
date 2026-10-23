package com.usach.mytingeso.dto;

import com.usach.mytingeso.entities.ProfessorStatus;
import jakarta.validation.constraints.NotNull;

public record ProfessorStatusRequest(
        @NotNull ProfessorStatus status
        ) {}
