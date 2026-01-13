package com.irrigation.arrosage_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgrammeArrosage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long parcelleId;
    private LocalDateTime datePlanifiee;
    private Integer duree; // en minutes
    private Double volumePrevu; // en litres
    
    @Enumerated(EnumType.STRING)
    private StatutProgramme statut;

    // Informations météo utilisées pour la décision
    private Double temperatureMax;
    private Double temperatureMin;
    private Boolean pluiePrevue;
    private Double vent;
    private Long stationMeteoId;

    public enum StatutProgramme {
        PLANIFIE,
        EN_COURS,
        TERMINE,
        ANNULE,
        AJUSTE
    }
}
