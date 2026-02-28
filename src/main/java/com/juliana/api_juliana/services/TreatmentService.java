package com.juliana.api_juliana.services;

import com.juliana.api_juliana.entities.Treatment;
import com.juliana.api_juliana.exceptions.BadRequestException;
import com.juliana.api_juliana.exceptions.ResourceNotFoundException;
import com.juliana.api_juliana.repositories.TreatmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TreatmentService {

    private final TreatmentRepository treatmentRepository;

    public Treatment saveTreatment(Treatment treatment) {
        if(treatment.getName() == null || treatment.getName().isBlank() || treatment.getPrice() == null){
            throw new BadRequestException("Campo nombre o precio vacío");
        }
        return treatmentRepository.save(treatment);
    }

    public List<Treatment> getTreatments() {
        return treatmentRepository.findAll();
    }

    public void deleteTreatment(Integer id) {
        if(!treatmentRepository.existsById(id)){
            throw new ResourceNotFoundException("El tratamiento con id " + id + " no fue encontrado");
        }

        treatmentRepository.deleteById(id);
    }

    public Treatment updateTreatment(Integer id, Treatment treatment) {
        Treatment exists = treatmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El tratamiento con id " + id + " no fue encontrado"));

        exists.setName(treatment.getName());
        exists.setPrice(treatment.getPrice());
        exists.setState(treatment.getState());
        exists.setDuration(treatment.getDuration());

        return treatmentRepository.save(exists);
    }

    public List<Treatment> getAvailableTreatments() {
        return this.treatmentRepository.findAvailableTreatments();
    }
}
