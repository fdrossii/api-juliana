package com.juliana.api_juliana.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GlobalStatsDto {
    private Long totalAppointments;
    private Double totalRevenue;
}
