package com.irrigation.meteo_service.event;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangementConditionsEvent {
    
    private Long stationId;
    private String stationNom;
    private LocalDate date;
    private Double temperatureMax;
    private Double temperatureMin;
    private Boolean pluiePrevue;
    private Double vent;
    private LocalDateTime timestamp;
    private String message;
}
