package com.juliana.api_juliana.services;

import com.juliana.api_juliana.dtos.*;

import java.time.LocalDate;
import java.util.List;

public interface IAppointmentService {

    List<AppointmentDto> getAllAppointments();
    AppointmentDto createAppointment(AppointmentCreateDto appointmentCreateDto);
    List<AppointmentDto> createBulkAppointment(AppointmentBulkRequestDto appointmentBulkRequestDto);
    AppointmentDto bookAppointment(BookAppointmentDto BookAppointmentDto);
    AppointmentDto updateAppointment(Integer Id, AppointmentDto appointmentDto);
    List<AppointmentDto> getAppointmentsForAdmin();
    List<AppointmentDto> getAppointmentsForClient();
    List<AppointmentDto> getAppointmentHistory();
    StatsResponseDto getStats(LocalDate start, LocalDate end);
    void deleteAppointment(Integer id);


}
