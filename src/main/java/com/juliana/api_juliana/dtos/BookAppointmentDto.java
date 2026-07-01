package com.juliana.api_juliana.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class BookAppointmentDto {

    @NotNull(message = "El treatmentId es obligatorio")
    private Integer treatmentId;

    @NotNull(message = "El appointmentId es obligatorio")
    private Integer appointmentId;

    @Valid
    @NotNull(message = "El cliente es obligatorio")
    private ClientDto client;
}