package com.juliana.api_juliana.entities;

import com.juliana.api_juliana.enums.TreatmentState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "treatments")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Treatment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Double price;
    @Column(nullable = false)
    private Integer duration;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TreatmentState state;
}
