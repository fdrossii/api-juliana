package com.juliana.api_juliana.dtos;

import com.juliana.api_juliana.enums.TreatmentState;
import jakarta.persistence.*;
import lombok.*;


@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TreatmentDto {

    private Integer id;
    private String name;
    private Double price;
    private Integer duration;
    private TreatmentState state;


}
