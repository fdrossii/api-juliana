package com.juliana.api_juliana.controllers;

import com.juliana.api_juliana.dtos.TreatmentDto;
import com.juliana.api_juliana.entities.Treatment;
import com.juliana.api_juliana.services.TreatmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/treatments")
public class TreatmentController {

    @Autowired
    TreatmentService treatmentService;

    @PostMapping()
    public ResponseEntity<TreatmentDto> createTreatment(@RequestBody TreatmentDto treatmentDto) {
        TreatmentDto created = treatmentService.createTreatment(treatmentDto);

        return ResponseEntity.created(URI.create("/api/treatments" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TreatmentDto> update(@PathVariable Integer id, @RequestBody TreatmentDto treatmentDto) {
        return ResponseEntity.ok(treatmentService.updateTreatment(id, treatmentDto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<TreatmentDto>> getAllTreatments() {
        return ResponseEntity.ok(treatmentService.getTreatments());
    }

    @GetMapping("/forClients")
    public ResponseEntity<List<TreatmentDto>> getAvailableTreatments() {
        return ResponseEntity.ok(treatmentService.getAvailableTreatments());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
        treatmentService.deleteTreatment(id);
        return ResponseEntity.noContent().build();
    }
}
