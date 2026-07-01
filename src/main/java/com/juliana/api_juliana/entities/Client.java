package com.juliana.api_juliana.entities;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "clients")
@Entity
@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Client{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String phone;
}
