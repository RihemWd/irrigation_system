package com.irrigation.meteo_service.service;

import com.irrigation.meteo_service.entity.Prevision;
import com.irrigation.meteo_service.entity.StationMeteo;
import com.irrigation.meteo_service.event.ChangementConditionsEvent;
import com.irrigation.meteo_service.messaging.MeteoEventPublisher;
import com.irrigation.meteo_service.repository.PrevisionRepository;
import com.irrigation.meteo_service.repository.StationMeteoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class MeteoService {

    private final PrevisionRepository previsionRepository;
    private final StationMeteoRepository stationMeteoRepository;
    private final MeteoEventPublisher eventPublisher;

    public MeteoService(PrevisionRepository previsionRepository, 
                       StationMeteoRepository stationMeteoRepository,
                       MeteoEventPublisher eventPublisher) {
        this.previsionRepository = previsionRepository;
        this.stationMeteoRepository = stationMeteoRepository;
        this.eventPublisher = eventPublisher;
    }

    public List<Prevision> getPrevisions(Long stationId, LocalDate date) {
        return previsionRepository.findByStationIdAndDate(stationId, date);
    }

    public List<Prevision> getAllPrevisions() {
        return previsionRepository.findAll();
    }

    public Prevision getPrevisionById(Long id) {
        return previsionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prévision non trouvée avec l'id: " + id));
    }

    @Transactional
    public Prevision createPrevision(Prevision prevision) {
        Prevision savedPrevision = previsionRepository.save(prevision);
        
        // Publier un événement pour les changements de conditions
        publishChangementConditions(savedPrevision);
        
        return savedPrevision;
    }

    @Transactional
    public Prevision updatePrevision(Long id, Prevision prevision) {
        Prevision existingPrevision = getPrevisionById(id);
        existingPrevision.setDate(prevision.getDate());
        existingPrevision.setTemperatureMax(prevision.getTemperatureMax());
        existingPrevision.setTemperatureMin(prevision.getTemperatureMin());
        existingPrevision.setPluiePrevue(prevision.getPluiePrevue());
        existingPrevision.setVent(prevision.getVent());
        existingPrevision.setStation(prevision.getStation());
        
        Prevision updatedPrevision = previsionRepository.save(existingPrevision);
        
        // Publier un événement pour les changements
        publishChangementConditions(updatedPrevision);
        
        return updatedPrevision;
    }

    public void deletePrevision(Long id) {
        previsionRepository.deleteById(id);
    }

    // Gestion des stations météo
    public List<StationMeteo> getAllStations() {
        return stationMeteoRepository.findAll();
    }

    public StationMeteo getStationById(Long id) {
        return stationMeteoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Station non trouvée avec l'id: " + id));
    }

    public StationMeteo createStation(StationMeteo station) {
        return stationMeteoRepository.save(station);
    }

    public StationMeteo updateStation(Long id, StationMeteo station) {
        StationMeteo existingStation = getStationById(id);
        existingStation.setNom(station.getNom());
        existingStation.setLatitude(station.getLatitude());
        existingStation.setLongitude(station.getLongitude());
        existingStation.setFournisseur(station.getFournisseur());
        return stationMeteoRepository.save(existingStation);
    }

    public void deleteStation(Long id) {
        stationMeteoRepository.deleteById(id);
    }

    private void publishChangementConditions(Prevision prevision) {
        ChangementConditionsEvent event = ChangementConditionsEvent.builder()
                .stationId(prevision.getStation().getId())
                .stationNom(prevision.getStation().getNom())
                .date(prevision.getDate())
                .temperatureMax(prevision.getTemperatureMax())
                .temperatureMin(prevision.getTemperatureMin())
                .pluiePrevue(prevision.getPluiePrevue())
                .vent(prevision.getVent())
                .timestamp(LocalDateTime.now())
                .message("Nouvelles conditions météo disponibles")
                .build();
        
        eventPublisher.publishChangementConditions(event);
        log.info("Événement de changement de conditions publié pour la station: {}", 
                prevision.getStation().getNom());
    }
}
