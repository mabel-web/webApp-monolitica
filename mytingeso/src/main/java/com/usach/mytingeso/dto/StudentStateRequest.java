package com.usach.mytingeso.dto;

import com.usach.mytingeso.enums.AcademicState;
import jakarta.validation.constraints.NotNull;

public record StudentStateRequest(
        @NotNull AcademicState state
){}