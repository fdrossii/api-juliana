package com.juliana.api_juliana.controllers;

import com.juliana.api_juliana.entities.Treatment;
import com.juliana.api_juliana.services.TreatmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/treatments")
public class TreatmentController {

    private final TreatmentService treatmentService;

    @PostMapping()
    public ResponseEntity<Treatment> create(@RequestBody Treatment treatment) {
        return ResponseEntity.ok(treatmentService.saveTreatment(treatment));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Treatment> update(@PathVariable Integer id, @RequestBody Treatment treatment) {
        return ResponseEntity.ok(treatmentService.updateTreatment(treatment.getId(),treatment));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Treatment>> getAllTreatments() {
        return ResponseEntity.ok(treatmentService.getTreatments());
    }

    @GetMapping("/forClients")
    public ResponseEntity<List<Treatment>> getAvailableTreatments() {
        return ResponseEntity.ok(treatmentService.getAvailableTreatments());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Integer id) {
        treatmentService.deleteTreatment(id);
        return ResponseEntity.noContent().build();
    }



}
