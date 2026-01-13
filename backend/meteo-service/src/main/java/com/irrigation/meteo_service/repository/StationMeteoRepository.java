package com.irrigation.meteo_service.repository;

import com.irrigation.meteo_service.entity.StationMeteo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StationMeteoRepository extends JpaRepository<StationMeteo, Long> {
}

