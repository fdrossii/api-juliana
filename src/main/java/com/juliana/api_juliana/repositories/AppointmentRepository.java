package com.juliana.api_juliana.repositories;

import com.juliana.api_juliana.dtos.GlobalStatsDto;
import com.juliana.api_juliana.dtos.ServiceStatsDto;
import com.juliana.api_juliana.entities.Appointment;
import com.juliana.api_juliana.enums.AppointmentState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    // Retorna los turnos disponibles con fecha y hora posteriores al momento actual
    @Query("""
           SELECT a FROM Appointment a
           WHERE a.state = 'AVAILABLE'
           AND(
              a.date > :today
              OR (a.date = :today AND a.time > :now)
           )
           ORDER BY a.date, a.time
           """)
    List<Appointment> findAvailableFromNow(

            @Param("today") LocalDate date,
            @Param("now")LocalTime now
            );

    // Retorna todos los turnos que no estan cancelados ni finalizados
    @Query("""
            SELECT a FROM Appointment a
            WHERE a.state NOT IN ('CANCELED', 'FINISHED')
            ORDER BY a.date, a.time
            """)
    List<Appointment> findVisibleForAdmin();

    // Retorna todos los turnos que estan cancelados y finalizados
    @Query("""
            SELECT a FROM Appointment a
            WHERE a.state IN ('CANCELED', 'FINISHED')
            ORDER BY a.date, a.time
            """)
    List<Appointment> findArchivedAppointments();


    //Cancela los turnos en estado 'AVAILABLE','UNAVAILABLE' y que tengan fecha y horario anterior al actual
    @Query("""
            SELECT a FROM Appointment a
            WHERE a.state IN ('AVAILABLE', 'UNAVAILABLE')
            AND (
                a.date < :today
                OR (a.date = :today AND a.time < :now)
            )
            """)
    List<Appointment> findExpiredAutoCancelable(
            @Param("today") LocalDate today,
            @P("now") LocalTime now
    );

    //Stats por servicio
    @Query("""
    SELECT new com.juliana.api_juliana.dtos.ServiceStatsDto(
        a.treatment.id,
        a.treatment.name,
        COUNT(a),
        SUM(a.treatment.price)
    )
    FROM Appointment a
    WHERE a.state = :state
      AND a.date BETWEEN :startDate AND :endDate
    GROUP BY a.treatment.id, a.treatment.name
    """)
    List<ServiceStatsDto> getStatsByService(
            @Param("state") AppointmentState state,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
    SELECT new com.juliana.api_juliana.dtos.GlobalStatsDto(
        COUNT(a),
        SUM(a.treatment.price)
    )
    FROM Appointment a
    WHERE a.state = :state
      AND a.date BETWEEN :startDate AND :endDate
    """)
    GlobalStatsDto getGlobalStats(
            @Param("state") AppointmentState state,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    boolean existsByDateAndTime(LocalDate date, LocalTime time);


}
