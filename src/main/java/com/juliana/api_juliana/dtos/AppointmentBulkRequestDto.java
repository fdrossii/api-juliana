package com.juliana.api_juliana.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentBulkRequestDto {

    @NotNull(message = "La fecha es obligatoria")
    @FutureOrPresent(message = "No se pueden crear turnos en fechas pasadas")
    private LocalDate date;

    @NotEmpty(message = "Debe seleccionar al menos un horario")
    private List<@NotNull(message = "Horario inválido") LocalTime> times;

}