package com.juliana.api_juliana.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceStatsDto {

    private Integer treatmentId;
    private String treatmentName;
    private Long totalAppointments;
    private Double totalRevenue;
}
