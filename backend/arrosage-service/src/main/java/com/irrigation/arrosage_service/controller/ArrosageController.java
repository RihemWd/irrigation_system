package com.irrigation.arrosage_service.controller;

import com.irrigation.arrosage_service.entity.JournalArrosage;
import com.irrigation.arrosage_service.entity.ProgrammeArrosage;
import com.irrigation.arrosage_service.service.ArrosageService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/arrosage")
public class ArrosageController {

    private final ArrosageService arrosageService;

    public ArrosageController(ArrosageService arrosageService) {
        this.arrosageService = arrosageService;
    }

    // ===== PROGRAMMES D'ARROSAGE =====

    @GetMapping("/programmes")
    public ResponseEntity<List<ProgrammeArrosage>> getAllProgrammes() {
        return ResponseEntity.ok(arrosageService.getAllProgrammes());
    }

    @GetMapping("/programmes/{id}")
    public ResponseEntity<ProgrammeArrosage> getProgrammeById(@PathVariable Long id) {
        return ResponseEntity.ok(arrosageService.getProgrammeById(id));
    }

    @GetMapping("/programmes/parcelle/{parcelleId}")
    public ResponseEntity<List<ProgrammeArrosage>> getProgrammesByParcelle(@PathVariable Long parcelleId) {
        return ResponseEntity.ok(arrosageService.getProgrammesByParcelle(parcelleId));
    }

    @PostMapping("/programmes")
    public ResponseEntity<ProgrammeArrosage> createProgramme(@RequestBody ProgrammeArrosage programme) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(arrosageService.createProgramme(programme));
    }

    @PostMapping("/programmes/avec-meteo")
    public ResponseEntity<ProgrammeArrosage> createProgrammeAvecMeteo(
            @RequestParam Long parcelleId,
            @RequestParam Long stationMeteoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(arrosageService.createProgrammeAvecMeteo(parcelleId, stationMeteoId, date));
    }

    @PutMapping("/programmes/{id}")
    public ResponseEntity<ProgrammeArrosage> updateProgramme(
            @PathVariable Long id,
            @RequestBody ProgrammeArrosage programme
    ) {
        return ResponseEntity.ok(arrosageService.updateProgramme(id, programme));
    }

    @DeleteMapping("/programmes/{id}")
    public ResponseEntity<Void> deleteProgramme(@PathVariable Long id) {
        arrosageService.deleteProgramme(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/programmes/{id}/executer")
    public ResponseEntity<JournalArrosage> executerProgramme(@PathVariable Long id) {
        return ResponseEntity.ok(arrosageService.executerProgramme(id));
    }

    // ===== JOURNAUX D'ARROSAGE =====

    @GetMapping("/journaux")
    public ResponseEntity<List<JournalArrosage>> getAllJournaux() {
        return ResponseEntity.ok(arrosageService.getAllJournaux());
    }

    @GetMapping("/journaux/{id}")
    public ResponseEntity<JournalArrosage> getJournalById(@PathVariable Long id) {
        return ResponseEntity.ok(arrosageService.getJournalById(id));
    }

    @GetMapping("/journaux/programme/{programmeId}")
    public ResponseEntity<List<JournalArrosage>> getJournauxByProgramme(@PathVariable Long programmeId) {
        return ResponseEntity.ok(arrosageService.getJournauxByProgramme(programmeId));
    }

    @PostMapping("/journaux")
    public ResponseEntity<JournalArrosage> createJournal(@RequestBody JournalArrosage journal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(arrosageService.createJournal(journal));
    }
}
