package com.juliana.api_juliana.controllers;

import com.juliana.api_juliana.dtos.*;
import com.juliana.api_juliana.entities.Appointment;
import com.juliana.api_juliana.services.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService    appointmentService;

    @PostMapping()
    public ResponseEntity<AppointmentDto> createAppointment(
            @Valid @RequestBody AppointmentCreateDto dto) {

        return ResponseEntity.ok(
                appointmentService.createAppointment(dto)
        );
    }

    @PutMapping("/book")
    public ResponseEntity<AppointmentDto> book(
            @Valid @RequestBody BookAppointmentDto dto) {
        return ResponseEntity.ok(appointmentService.bookAppointment(dto));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<AppointmentDto>> createBulk(
            @Valid @RequestBody AppointmentBulkRequestDto dto) {

        return ResponseEntity.ok(
                appointmentService.createBulkAppointment(dto)
        );
    }

    @GetMapping("/all")
    public ResponseEntity<List<AppointmentDto>> getAllAppointments(){
        return ResponseEntity.ok(this.appointmentService.getAllAppointments());
    }

    @GetMapping("/available")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsForClient(){
        return ResponseEntity.ok(this.appointmentService.getAppointmentsForClient());
    }

    @GetMapping("/active")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsForAdmin(){
        return ResponseEntity.ok(this.appointmentService.getAppointmentsForAdmin());
    }

    @GetMapping("/history")
    public ResponseEntity<List<AppointmentDto>> getAppointmentHistory(){
        return ResponseEntity.ok(this.appointmentService.getAppointmentHistory());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Integer id){
        this.appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDto> updateAppointment(@PathVariable Integer id,@RequestBody AppointmentDto appointmentDto){
        return ResponseEntity.ok(this.appointmentService.updateAppointment(id, appointmentDto));
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsResponseDto> getStats(@RequestParam  LocalDate start, @RequestParam  LocalDate end){
        return ResponseEntity.ok(this.appointmentService.getStats(start, end));
    }
}
