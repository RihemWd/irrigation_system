package com.irrigation.meteo_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prevision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private Double temperatureMax;
    private Double temperatureMin;
    private Boolean pluiePrevue;
    private Double vent;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private StationMeteo station;
}