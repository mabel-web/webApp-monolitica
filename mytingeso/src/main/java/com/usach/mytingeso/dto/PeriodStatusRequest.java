package com.usach.mytingeso.dto;

import com.usach.mytingeso.entities.PeriodStatus;
import jakarta.validation.constraints.NotNull;

public record PeriodStatusRequest(
        @NotNull PeriodStatus status
        ) {
}
