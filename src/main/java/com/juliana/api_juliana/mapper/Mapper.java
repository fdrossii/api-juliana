package com.juliana.api_juliana.mapper;

import com.juliana.api_juliana.dtos.AppointmentDto;
import com.juliana.api_juliana.dtos.ClientDto;
import com.juliana.api_juliana.dtos.TreatmentDto;
import com.juliana.api_juliana.entities.Appointment;
import com.juliana.api_juliana.entities.Client;
import com.juliana.api_juliana.entities.Treatment;

public class Mapper {

    //Map Client to ClientDto
    public static ClientDto toDto(Client client) {
        if (client == null) return null;

        return ClientDto.builder()
                .id(client.getId())
                .name(client.getName())
                .email(client.getEmail())
                .phone(client.getPhone())
                .build();
    }

    //Map Treatment to TreatmentDto
    public static TreatmentDto toDto(Treatment treatment) {
        if (treatment == null) return null;

        return TreatmentDto.builder()
                .id(treatment.getId())
                .name(treatment.getName())
                .price(treatment.getPrice())
                .duration(treatment.getDuration())
                .state(treatment.getState())
                .build();
    }

    //Map Appointment to AppointmentDto
    public static AppointmentDto toDto(Appointment appointment) {
        if (appointment == null) return null;

        return AppointmentDto.builder()
                .id(appointment.getId())
                .date(appointment.getDate())
                .time(appointment.getTime())
                .clientDto(Mapper.toDto(appointment.getClient()))
                .treatmentDto(Mapper.toDto(appointment.getTreatment()))
                .state(appointment.getState())
                .build();
    }
}
