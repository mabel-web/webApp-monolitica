package com.usach.mytingeso.dto;

public record PeriodCloseResponse(
        String period, int enrollmentsConsolidated, int graduated, int eliminated
) {
}
