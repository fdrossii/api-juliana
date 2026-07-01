package com.juliana.api_juliana.services;

import com.juliana.api_juliana.dtos.TreatmentDto;

import java.util.List;

public interface ITreatmentService {

    List<TreatmentDto> getTreatments();
    TreatmentDto createTreatment(TreatmentDto treatmentDto);
    TreatmentDto updateTreatment(Integer id, TreatmentDto treatmentDto);
    List<TreatmentDto> getAvailableTreatments();
}
