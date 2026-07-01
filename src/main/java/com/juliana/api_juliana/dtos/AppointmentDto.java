package com.juliana.api_juliana.dtos;
import com.juliana.api_juliana.enums.AppointmentState;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentDto {

    private Integer id;
    private LocalDate date;
    private LocalTime time;
    private TreatmentDto treatmentDto;
    private ClientDto clientDto;
    private AppointmentState state;

}
