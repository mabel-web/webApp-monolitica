package com.usach.mytingeso.dto;

import com.usach.mytingeso.entities.CareerStatus;
import jakarta.validation.constraints.NotNull;

public record CareerStatusRequest(
        @NotNull CareerStatus status

        //no pide codigo de la carrera xq no se deberia modificar despues de crearla
) {
}
