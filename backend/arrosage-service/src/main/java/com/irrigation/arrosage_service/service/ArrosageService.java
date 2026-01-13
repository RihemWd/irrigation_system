package com.irrigation.arrosage_service.service;

import com.irrigation.arrosage_service.client.MeteoServiceClient;
import com.irrigation.arrosage_service.dto.ChangementConditionsEvent;
import com.irrigation.arrosage_service.dto.PrevisionDTO;
import com.irrigation.arrosage_service.entity.JournalArrosage;
import com.irrigation.arrosage_service.entity.ProgrammeArrosage;
import com.irrigation.arrosage_service.repository.JournalArrosageRepository;
import com.irrigation.arrosage_service.repository.ProgrammeArrosageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Slf4j
public class ArrosageService {

    private final ProgrammeArrosageRepository programmeRepository;
    private final JournalArrosageRepository journalRepository;
    private final MeteoServiceClient meteoServiceClient;

    public ArrosageService(ProgrammeArrosageRepository programmeRepository,
                          JournalArrosageRepository journalRepository,
                          MeteoServiceClient meteoServiceClient) {
        this.programmeRepository = programmeRepository;
        this.journalRepository = journalRepository;
        this.meteoServiceClient = meteoServiceClient;
    }

    // ===== GESTION DES PROGRAMMES =====

    public List<ProgrammeArrosage> getAllProgrammes() {
        return programmeRepository.findAll();
    }

    public ProgrammeArrosage getProgrammeById(Long id) {
        return programmeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Programme non trouvé avec l'id: " + id));
    }

    public List<ProgrammeArrosage> getProgrammesByParcelle(Long parcelleId) {
        return programmeRepository.findByParcelleId(parcelleId);
    }

    @Transactional
    public ProgrammeArrosage createProgramme(ProgrammeArrosage programme) {
        programme.setStatut(ProgrammeArrosage.StatutProgramme.PLANIFIE);
        return programmeRepository.save(programme);
    }

    @Transactional
    public ProgrammeArrosage createProgrammeAvecMeteo(Long parcelleId, Long stationMeteoId, LocalDate date) {
        // Communication synchrone avec le service météo
        log.info("Récupération des prévisions météo pour la station: {}", stationMeteoId);
        List<PrevisionDTO> previsions = meteoServiceClient.getPrevisions(stationMeteoId, date);
        
        if (previsions.isEmpty()) {
            log.warn("Aucune prévision trouvée. Création d'un programme par défaut.");
            return createProgrammeParDefaut(parcelleId, stationMeteoId, date);
        }

        PrevisionDTO prevision = previsions.get(0);
        
        // Calcul intelligent de la durée et du volume selon la météo
        int duree = calculerDuree(prevision);
        double volume = calculerVolume(prevision, duree);

        ProgrammeArrosage programme = ProgrammeArrosage.builder()
                .parcelleId(parcelleId)
                .datePlanifiee(LocalDateTime.of(date, LocalTime.of(6, 0))) // Arrosage à 6h du matin
                .duree(duree)
                .volumePrevu(volume)
                .statut(ProgrammeArrosage.StatutProgramme.PLANIFIE)
                .temperatureMax(prevision.getTemperatureMax())
                .temperatureMin(prevision.getTemperatureMin())
                .pluiePrevue(prevision.getPluiePrevue())
                .vent(prevision.getVent())
                .stationMeteoId(stationMeteoId)
                .build();

        ProgrammeArrosage saved = programmeRepository.save(programme);
        log.info("Programme d'arrosage créé avec succès: {}", saved.getId());
        
        return saved;
    }

    @Transactional
    public ProgrammeArrosage updateProgramme(Long id, ProgrammeArrosage programme) {
        ProgrammeArrosage existing = getProgrammeById(id);
        existing.setParcelleId(programme.getParcelleId());
        existing.setDatePlanifiee(programme.getDatePlanifiee());
        existing.setDuree(programme.getDuree());
        existing.setVolumePrevu(programme.getVolumePrevu());
        existing.setStatut(programme.getStatut());
        return programmeRepository.save(existing);
    }

    @Transactional
    public void deleteProgramme(Long id) {
        programmeRepository.deleteById(id);
    }

    // ===== GESTION DES JOURNAUX =====

    public List<JournalArrosage> getAllJournaux() {
        return journalRepository.findAll();
    }

    public JournalArrosage getJournalById(Long id) {
        return journalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Journal non trouvé avec l'id: " + id));
    }

    public List<JournalArrosage> getJournauxByProgramme(Long programmeId) {
        return journalRepository.findByProgrammeId(programmeId);
    }

    @Transactional
    public JournalArrosage createJournal(JournalArrosage journal) {
        journal.setDateExecution(LocalDateTime.now());
        return journalRepository.save(journal);
    }

    @Transactional
    public JournalArrosage executerProgramme(Long programmeId) {
        ProgrammeArrosage programme = getProgrammeById(programmeId);
        
        programme.setStatut(ProgrammeArrosage.StatutProgramme.EN_COURS);
        programmeRepository.save(programme);

        // Simulation de l'exécution
        JournalArrosage journal = JournalArrosage.builder()
                .programmeId(programmeId)
                .dateExecution(LocalDateTime.now())
                .volumeReel(programme.getVolumePrevu() * 0.95) // 95% du volume prévu
                .remarque("Exécution automatique réussie")
                .typeExecution(JournalArrosage.TypeExecution.AUTOMATIQUE)
                .build();

        JournalArrosage savedJournal = journalRepository.save(journal);

        programme.setStatut(ProgrammeArrosage.StatutProgramme.TERMINE);
        programmeRepository.save(programme);

        log.info("Programme {} exécuté avec succès", programmeId);
        return savedJournal;
    }

    // ===== AJUSTEMENT AUTOMATIQUE =====

    @Transactional
    public void ajusterProgrammesSelonMeteo(ChangementConditionsEvent event) {
        log.info("Ajustement des programmes selon les nouvelles conditions météo");
        
        LocalDateTime startDate = event.getDate().atStartOfDay();
        LocalDateTime endDate = event.getDate().atTime(23, 59);

        List<ProgrammeArrosage> programmes = programmeRepository
                .findByStationMeteoIdAndDatePlanifieeBetween(event.getStationId(), startDate, endDate);

        for (ProgrammeArrosage programme : programmes) {
            if (programme.getStatut() == ProgrammeArrosage.StatutProgramme.PLANIFIE) {
                ajusterProgramme(programme, event);
            }
        }
    }

    private void ajusterProgramme(ProgrammeArrosage programme, ChangementConditionsEvent event) {
        programme.setTemperatureMax(event.getTemperatureMax());
        programme.setTemperatureMin(event.getTemperatureMin());
        programme.setPluiePrevue(event.getPluiePrevue());
        programme.setVent(event.getVent());

        // Si pluie prévue, annuler l'arrosage
        if (Boolean.TRUE.equals(event.getPluiePrevue())) {
            programme.setStatut(ProgrammeArrosage.StatutProgramme.ANNULE);
            log.info("Programme {} annulé en raison de la pluie prévue", programme.getId());
        } else {
            // Recalculer la durée et le volume
            PrevisionDTO prevision = PrevisionDTO.builder()
                    .temperatureMax(event.getTemperatureMax())
                    .temperatureMin(event.getTemperatureMin())
                    .pluiePrevue(event.getPluiePrevue())
                    .vent(event.getVent())
                    .build();

            int nouvelleDuree = calculerDuree(prevision);
            double nouveauVolume = calculerVolume(prevision, nouvelleDuree);

            programme.setDuree(nouvelleDuree);
            programme.setVolumePrevu(nouveauVolume);
            programme.setStatut(ProgrammeArrosage.StatutProgramme.AJUSTE);
            
            log.info("Programme {} ajusté: durée={} min, volume={} L", 
                    programme.getId(), nouvelleDuree, nouveauVolume);
        }

        programmeRepository.save(programme);
    }

    // ===== CALCULS INTELLIGENTS =====

    private int calculerDuree(PrevisionDTO prevision) {
        int dureeBase = 30; // 30 minutes par défaut

        // Augmenter la durée si forte chaleur
        if (prevision.getTemperatureMax() != null && prevision.getTemperatureMax() > 30) {
            dureeBase += 15;
        }

        // Réduire si températures modérées
        if (prevision.getTemperatureMax() != null && prevision.getTemperatureMax() < 20) {
            dureeBase -= 10;
        }

        // Ajuster selon le vent (plus de vent = plus d'évaporation)
        if (prevision.getVent() != null && prevision.getVent() > 20) {
            dureeBase += 10;
        }

        // Pas d'arrosage si pluie prévue
        if (Boolean.TRUE.equals(prevision.getPluiePrevue())) {
            dureeBase = 0;
        }

        return Math.max(0, dureeBase);
    }

    private double calculerVolume(PrevisionDTO prevision, int duree) {
        // Calcul: environ 10 litres par minute
        double volumeBase = duree * 10.0;

        // Augmenter le volume si forte chaleur
        if (prevision.getTemperatureMax() != null && prevision.getTemperatureMax() > 30) {
            volumeBase *= 1.2;
        }

        return volumeBase;
    }

    private ProgrammeArrosage createProgrammeParDefaut(Long parcelleId, Long stationMeteoId, LocalDate date) {
        ProgrammeArrosage programme = ProgrammeArrosage.builder()
                .parcelleId(parcelleId)
                .datePlanifiee(LocalDateTime.of(date, LocalTime.of(6, 0)))
                .duree(30)
                .volumePrevu(300.0)
                .statut(ProgrammeArrosage.StatutProgramme.PLANIFIE)
                .stationMeteoId(stationMeteoId)
                .build();

        return programmeRepository.save(programme);
    }
}
