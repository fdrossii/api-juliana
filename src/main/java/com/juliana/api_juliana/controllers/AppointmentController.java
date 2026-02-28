package com.juliana.api_juliana.controllers;

import com.juliana.api_juliana.dtos.AppointmentBulkRequestDto;
import com.juliana.api_juliana.dtos.AppointmentCreateDto;
import com.juliana.api_juliana.dtos.BookingAppointmentDto;
import com.juliana.api_juliana.dtos.StatsResponseDto;
import com.juliana.api_juliana.entities.Appointment;
import com.juliana.api_juliana.services.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService    appointmentService;

    @PostMapping()
    public ResponseEntity<Appointment> createAppointment(
            @Valid @RequestBody AppointmentCreateDto dto) {

        return ResponseEntity.ok(
                appointmentService.createAppointment(dto)
        );
    }

    @PutMapping("/reserve")
    public ResponseEntity<Appointment> reserve(
            @Valid @RequestBody BookingAppointmentDto dto) {
        return ResponseEntity.ok(appointmentService.reserveAppointment(dto));
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<Appointment>> createBulk(
            @Valid @RequestBody AppointmentBulkRequestDto dto) {

        return ResponseEntity.ok(
                appointmentService.createBulkAppointment(dto)
        );
    }

    @GetMapping("/all")
    public ResponseEntity<List<Appointment>> getAllAppointments(){
        return ResponseEntity.ok(this.appointmentService.getAllAppointments());
    }

    @GetMapping("/available")
    public ResponseEntity<List<Appointment>> getAppointmentsForClient(){
        return ResponseEntity.ok(this.appointmentService.getAppointmentsForClient());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Appointment>> getAppointmentsForAdmin(){
        return ResponseEntity.ok(this.appointmentService.getAppointmentsForAdmin());
    }

    @GetMapping("/history")
    public ResponseEntity<List<Appointment>> getAppointmentHistory(){
        return ResponseEntity.ok(this.appointmentService.getAppointmentHistory());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Integer id){
        this.appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable Integer id,@RequestBody Appointment appointment){
        return ResponseEntity.ok(this.appointmentService.updateAppointment(id, appointment));
    }

    @GetMapping("/stats")
    public ResponseEntity<StatsResponseDto> getStats(@RequestParam  LocalDate start, @RequestParam  LocalDate end){
        return ResponseEntity.ok(this.appointmentService.getStats(start, end));
    }
}
