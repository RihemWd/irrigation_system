package com.irrigation.meteo_service.repository;

import com.irrigation.meteo_service.entity.Prevision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PrevisionRepository extends JpaRepository<Prevision, Long> {

    List<Prevision> findByStationIdAndDate(Long stationId, LocalDate date);
}

