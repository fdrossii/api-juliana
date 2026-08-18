package com.juliana.api_juliana.services;

import com.juliana.api_juliana.dtos.*;
import com.juliana.api_juliana.entities.Appointment;
import com.juliana.api_juliana.entities.Client;
import com.juliana.api_juliana.entities.Treatment;
import com.juliana.api_juliana.enums.AppointmentState;
import com.juliana.api_juliana.enums.TreatmentState;
import com.juliana.api_juliana.exceptions.BadRequestException;
import com.juliana.api_juliana.exceptions.ResourceNotFoundException;
import com.juliana.api_juliana.mapper.Mapper;
import com.juliana.api_juliana.repositories.AppointmentRepository;

import com.juliana.api_juliana.repositories.ClientRepository;
import com.juliana.api_juliana.repositories.TreatmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService implements IAppointmentService {

    @Autowired
    EmailService emailService;
    @Autowired
    AppointmentRepository appointmentRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    TreatmentRepository treatmentRepository;

    @Override
    @Transactional
        public AppointmentDto bookAppointment(BookAppointmentDto dto) {
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
                    Client newClient = Client.builder()
                            .name(dto.getClient().getName())
                            .email(dto.getClient().getEmail())
                            .phone(dto.getClient().getPhone())
                            .build();

                    return clientRepository.save(newClient);
                });

        appointment.setClient(client);
        appointment.setTreatment(treatment);
        appointment.setState(AppointmentState.RESERVED);

        //this.emailService.sendConfirmedAppointmentEmail(appointment.getClient().getEmail(),Mapper.toDto(appointment));

        return Mapper.toDto(this.appointmentRepository.save(appointment));
    }

    @Override
    public List<AppointmentDto> getAllAppointments(){
        return this.appointmentRepository.findAll().stream().map(Mapper::toDto).toList();
    }

    @Override
    public List<AppointmentDto> getAppointmentsForClient(){
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        return this.appointmentRepository.findAvailableFromNow(today, now).stream().map(Mapper::toDto).toList();
    }

    @Override
    public List<AppointmentDto> getAppointmentsForAdmin(){
        return this.appointmentRepository.findVisibleForAdmin().stream().map(Mapper::toDto).toList();
    }

    @Override
    public List<AppointmentDto> getAppointmentHistory(){
        return this.appointmentRepository.findArchivedAppointments().stream().map(Mapper::toDto).toList();
    }

    private Appointment createOrReactivateAppointment(LocalDate date, LocalTime time) {

        Optional<Appointment> existingAppointment =
                appointmentRepository.findByDateAndTime(date, time);

        if (existingAppointment.isPresent()) {

            Appointment appointment = existingAppointment.get();

            if (appointment.getState() == AppointmentState.CANCELED) {

                appointment.setState(AppointmentState.AVAILABLE);
                appointment.setClient(null);
                appointment.setTreatment(null);

                return appointment;
            }

            throw new IllegalStateException(
                    "El turno del " + date + " a las " + time + " ya existe."
            );
        }

        return Appointment.builder()
                .date(date)
                .time(time)
                .state(AppointmentState.AVAILABLE)
                .build();
    }

    @Override
    public AppointmentDto createAppointment(AppointmentCreateDto dto) {

        Appointment appointment = createOrReactivateAppointment(
                dto.getDate(),
                dto.getTime()
        );

        return Mapper.toDto(
                appointmentRepository.save(appointment)
        );
    }

    @Override
    public List<AppointmentDto> createBulkAppointment(AppointmentBulkRequestDto dto) {

        List<Appointment> appointments = dto.getTimes().stream()
                .map(time -> createOrReactivateAppointment(dto.getDate(), time))
                .toList();

        return appointmentRepository.saveAll(appointments)
                .stream()
                .map(Mapper::toDto)
                .toList();
    }

    @Override
    public void deleteAppointment(Integer id){
        if(!appointmentRepository.existsById(id)){
            throw new ResourceNotFoundException(("El turno con id: " + id +" no fue econtrado"));
        }
        this.appointmentRepository.deleteById(id);
    }

    @Override
    public AppointmentDto updateAppointment(Integer id, AppointmentDto appointmentDto){
        Appointment exists = this.appointmentRepository.findById(id).
                orElseThrow(() -> new ResourceNotFoundException("Turno con id: " + id + " no encontrado"));

        exists.setTime(appointmentDto.getTime());
        exists.setDate(appointmentDto.getDate());
        exists.setState(appointmentDto.getState());


        if (appointmentDto.getClientDto() != null) {
            Client client = clientRepository.findById(
                    appointmentDto.getClientDto().getId()
            ).orElseThrow(() ->
                    new ResourceNotFoundException("Cliente no encontrado")
            );
            exists.setClient(client);
        } else {
            exists.setClient(null);
        }


        if (appointmentDto.getTreatmentDto() != null) {
            Treatment treatment = treatmentRepository.findById(
                    appointmentDto.getTreatmentDto().getId()
            ).orElseThrow(() ->
                    new ResourceNotFoundException("Servicio no encontrado")
            );
            exists.setTreatment(treatment);
        } else {
            exists.setTreatment(null);
        }

        return Mapper.toDto(this.appointmentRepository.save(exists));
    }

    @Override
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
