package com.irrigation.arrosage_service.repository;

import com.irrigation.arrosage_service.entity.ProgrammeArrosage;
import com.irrigation.arrosage_service.entity.ProgrammeArrosage.StatutProgramme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProgrammeArrosageRepository extends JpaRepository<ProgrammeArrosage, Long> {
    
    List<ProgrammeArrosage> findByParcelleId(Long parcelleId);
    
    List<ProgrammeArrosage> findByStatut(StatutProgramme statut);
    
    List<ProgrammeArrosage> findByDatePlanifieeBetween(LocalDateTime start, LocalDateTime end);
    
    List<ProgrammeArrosage> findByStationMeteoIdAndDatePlanifieeBetween(
            Long stationMeteoId, LocalDateTime start, LocalDateTime end);
}
