package com.juliana.api_juliana.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StatsResponseDto {

    private GlobalStatsDto global;
    private List<ServiceStatsDto> byService;
}
