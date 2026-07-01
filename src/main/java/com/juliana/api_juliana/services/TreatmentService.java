package com.juliana.api_juliana.services;

import com.juliana.api_juliana.dtos.TreatmentDto;
import com.juliana.api_juliana.entities.Treatment;
import com.juliana.api_juliana.exceptions.BadRequestException;
import com.juliana.api_juliana.exceptions.ResourceNotFoundException;
import com.juliana.api_juliana.mapper.Mapper;
import com.juliana.api_juliana.repositories.TreatmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TreatmentService implements  ITreatmentService{

    @Autowired
    TreatmentRepository treatmentRepository;

    @Override
    public TreatmentDto createTreatment(TreatmentDto treatmentDto) {
        if(treatmentDto.getName() == null || treatmentDto.getName().isBlank() || treatmentDto.getPrice() == null){
            throw new BadRequestException("Campo nombre o precio vacío");
        }

        Treatment treatment = Treatment.builder()
                .name(treatmentDto.getName())
                .price(treatmentDto.getPrice())
                .duration(treatmentDto.getDuration())
                .state(treatmentDto.getState())
                .build();

        return Mapper.toDto(treatmentRepository.save(treatment));
    }

    @Override
    public List<TreatmentDto> getTreatments() {
        return treatmentRepository.findAll().stream().map(Mapper::toDto).toList();
    }

    public void deleteTreatment(Integer id) {
        if(!treatmentRepository.existsById(id)){
            throw new ResourceNotFoundException("El tratamiento con id " + id + " no fue encontrado");
        }

        treatmentRepository.deleteById(id);
    }

    @Override
    public TreatmentDto updateTreatment(Integer id, TreatmentDto treatmentDto) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El tratamiento con id " + id + " no fue encontrado"));

        treatment.setName(treatmentDto.getName());
        treatment.setPrice(treatmentDto.getPrice());
        treatment.setState(treatmentDto.getState());
        treatment.setDuration(treatmentDto.getDuration());

        return Mapper.toDto(treatmentRepository.save(treatment));
    }

    @Override
    public List<TreatmentDto> getAvailableTreatments() {
        return treatmentRepository.findAvailableTreatments().stream().map(Mapper::toDto).toList();
    }
}
