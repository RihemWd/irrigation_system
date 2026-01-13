package com.irrigation.arrosage_service.dto;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PrevisionDTO {
    private Long id;
    private LocalDate date;
    private Double temperatureMax;
    private Double temperatureMin;
    private Boolean pluiePrevue;
    private Double vent;
    private Long stationId;
    private String stationNom;
}
