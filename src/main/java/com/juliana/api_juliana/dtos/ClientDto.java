package com.juliana.api_juliana.dtos;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class ClientDto {

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no es válido")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]+$", message = "El teléfono solo puede contener números")
    @Size(min = 4, message = "El teléfono debe tener al menos 4 dígitos")
    private String phone;
}