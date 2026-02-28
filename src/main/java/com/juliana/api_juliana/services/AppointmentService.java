package com.juliana.api_juliana.services;

import com.juliana.api_juliana.dtos.*;
import com.juliana.api_juliana.entities.Appointment;
import com.juliana.api_juliana.entities.Client;
import com.juliana.api_juliana.entities.Treatment;
import com.juliana.api_juliana.enums.AppointmentState;
import com.juliana.api_juliana.enums.TreatmentState;
import com.juliana.api_juliana.exceptions.BadRequestException;
import com.juliana.api_juliana.exceptions.ResourceNotFoundException;
import com.juliana.api_juliana.repositories.AppointmentRepository;

import com.juliana.api_juliana.repositories.ClientRepository;
import com.juliana.api_juliana.repositories.TreatmentRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final EmailService emailService;
    private final AppointmentRepository appointmentRepository;
    private final ClientRepository clientRepository;
    private final TreatmentRepository treatmentRepository;

    @Transactional
    public Appointment reserveAppointment(BookingAppointmentDto dto) {
        Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado"));

        if (appointment.getState() != AppointmentState.AVAILABLE) {
            throw new BadRequestException("El turno ya no está disponible");
        }

        Treatment treatment = treatmentRepository.findById(dto.getTreatmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

        if (treatment.getState() != TreatmentState.AVAILABLE) {
            throw new BadRequestException("El servicio no está disponible");
        }

        Client client = clientRepository.findByEmail(dto.getClient().getEmail())
                .orElseGet(() -> {
                    Client newClient = new Client();
                    newClient.setName(dto.getClient().getName());
                    newClient.setEmail(dto.getClient().getEmail());
                    newClient.setPhone(dto.getClient().getPhone());
                    return clientRepository.save(newClient);
                });

        appointment.setClient(client);
        appointment.setTreatment(treatment);
        appointment.setState(AppointmentState.RESERVED);

        this.emailService.sendConfirmedAppointmentEmail(appointment.getClient().getEmail(), appointment);

        return this.appointmentRepository.save(appointment);
    }


    public List<Appointment> getAllAppointments(){
        return this.appointmentRepository.findAll();
    }

    public List<Appointment> getAppointmentsForClient(){
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        return this.appointmentRepository.findAvailableFromNow(today, now);
    }

    public List<Appointment> getAppointmentsForAdmin(){
        return this.appointmentRepository.findVisibleForAdmin();
    }

    public List<Appointment> getAppointmentHistory(){
        return this.appointmentRepository.findArchivedAppointments();
    }

    public Appointment createAppointment(AppointmentCreateDto dto){
        if (appointmentRepository.existsByDateAndTime(dto.getDate(), dto.getTime())) {
            throw new IllegalStateException("El turno ya existe");
        }

        Appointment appointment = new Appointment();
        appointment.setDate(dto.getDate());
        appointment.setTime(dto.getTime());
        appointment.setState(AppointmentState.AVAILABLE);

        return appointmentRepository.save(appointment);
    }

    public List<Appointment> createBulkAppointment(AppointmentBulkRequestDto appointments){

        List<Appointment> entities = appointments.getTimes().stream()
                .map(timeStr -> {
                   Appointment appointment = new Appointment();
                    appointment.setDate(appointments.getDate());
                    appointment.setTime(timeStr);
                    appointment.setState(AppointmentState.AVAILABLE);

                    return appointment;
                }).toList();

        return this.appointmentRepository.saveAll(entities);
    }

    public void deleteAppointment(Integer id){
        if(!appointmentRepository.existsById(id)){
            throw new ResourceNotFoundException(("El turno con id: " + id +" no fue econtrado"));
        }
        this.appointmentRepository.deleteById(id);
    }

    public Appointment updateAppointment(Integer id, Appointment appointment){
        Appointment exists = this.appointmentRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Turno con id: " + id + " no encontrado"));

        exists.setTime(appointment.getTime());
        exists.setDate(appointment.getDate());
        exists.setState(appointment.getState());
        // CLIENTE
        if (appointment.getClient() != null) {
            Client client = clientRepository.findById(
                    appointment.getClient().getId()
            ).orElseThrow(() ->
                    new ResourceNotFoundException("Cliente no encontrado")
            );
            exists.setClient(client);
        } else {
            exists.setClient(null);
        }

        // TRATAMIENTO
        if (appointment.getTreatment() != null) {
            Treatment treatment = treatmentRepository.findById(
                    appointment.getTreatment().getId()
            ).orElseThrow(() ->
                    new ResourceNotFoundException("Servicio no encontrado")
            );
            exists.setTreatment(treatment);
        } else {
            exists.setTreatment(null);
        }

        return this.appointmentRepository.save(exists);
    }

    public StatsResponseDto getStats(LocalDate start, LocalDate end) {
        GlobalStatsDto global = this.appointmentRepository.getGlobalStats(AppointmentState.FINISHED, start, end);
        if (global.getTotalRevenue() == null) {
            global.setTotalRevenue(0.0);
        }
        List<ServiceStatsDto> services = this.appointmentRepository.getStatsByService(AppointmentState.FINISHED, start, end);

        return new StatsResponseDto(global, services);
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void cancelExpiredAppointmentsAutomatically(){

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        List<Appointment> expiredAppointments =
                appointmentRepository.findExpiredAutoCancelable(today, now);

        if(expiredAppointments.isEmpty()){
            return;
        }

        expiredAppointments.forEach(appointment ->
            appointment.setState(AppointmentState.CANCELED)
        );

        appointmentRepository.saveAll(expiredAppointments);
    }
}
