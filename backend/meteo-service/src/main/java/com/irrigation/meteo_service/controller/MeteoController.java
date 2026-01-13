package com.irrigation.meteo_service.controller;

import com.irrigation.meteo_service.entity.Prevision;
import com.irrigation.meteo_service.entity.StationMeteo;
import com.irrigation.meteo_service.service.MeteoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/meteo")
public class MeteoController {

    private final MeteoService meteoService;

    public MeteoController(MeteoService meteoService) {
        this.meteoService = meteoService;
    }

    // ===== PREVISIONS =====
    
    @GetMapping("/previsions")
    public ResponseEntity<List<Prevision>> getPrevisions(
            @RequestParam Long stationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(meteoService.getPrevisions(stationId, date));
    }

    @GetMapping("/previsions/all")
    public ResponseEntity<List<Prevision>> getAllPrevisions() {
        return ResponseEntity.ok(meteoService.getAllPrevisions());
    }

    @GetMapping("/previsions/{id}")
    public ResponseEntity<Prevision> getPrevisionById(@PathVariable Long id) {
        return ResponseEntity.ok(meteoService.getPrevisionById(id));
    }

    @PostMapping("/previsions")
    public ResponseEntity<Prevision> createPrevision(@RequestBody Prevision prevision) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(meteoService.createPrevision(prevision));
    }

    @PutMapping("/previsions/{id}")
    public ResponseEntity<Prevision> updatePrevision(
            @PathVariable Long id,
            @RequestBody Prevision prevision
    ) {
        return ResponseEntity.ok(meteoService.updatePrevision(id, prevision));
    }

    @DeleteMapping("/previsions/{id}")
    public ResponseEntity<Void> deletePrevision(@PathVariable Long id) {
        meteoService.deletePrevision(id);
        return ResponseEntity.noContent().build();
    }

    // ===== STATIONS METEO =====

    @GetMapping("/stations")
    public ResponseEntity<List<StationMeteo>> getAllStations() {
        return ResponseEntity.ok(meteoService.getAllStations());
    }

    @GetMapping("/stations/{id}")
    public ResponseEntity<StationMeteo> getStationById(@PathVariable Long id) {
        return ResponseEntity.ok(meteoService.getStationById(id));
    }

    @PostMapping("/stations")
    public ResponseEntity<StationMeteo> createStation(@RequestBody StationMeteo station) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(meteoService.createStation(station));
    }

    @PutMapping("/stations/{id}")
    public ResponseEntity<StationMeteo> updateStation(
            @PathVariable Long id,
            @RequestBody StationMeteo station
    ) {
        return ResponseEntity.ok(meteoService.updateStation(id, station));
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        meteoService.deleteStation(id);
        return ResponseEntity.noContent().build();
    }
}
