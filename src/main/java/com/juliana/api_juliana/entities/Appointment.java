package com.juliana.api_juliana.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.juliana.api_juliana.enums.AppointmentState;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Table(name = "appointments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"date", "time"})
        })
@Entity
@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private LocalDate date;
    @Column(nullable = false)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime time;
    @ManyToOne(optional = true)
    @JoinColumn(name = "treatment_id")
    private Treatment treatment;
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentState state;
}
