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
public class JournalArrosage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long programmeId;
    private LocalDateTime dateExecution;
    private Double volumeReel; // en litres
    private String remarque;

    @Enumerated(EnumType.STRING)
    private TypeExecution typeExecution;

    public enum TypeExecution {
        AUTOMATIQUE,
        MANUEL,
        URGENCE
    }
}
