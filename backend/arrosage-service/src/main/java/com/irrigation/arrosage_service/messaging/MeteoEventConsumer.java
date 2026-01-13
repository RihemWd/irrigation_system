package com.irrigation.arrosage_service.messaging;

import com.irrigation.arrosage_service.dto.ChangementConditionsEvent;
import com.irrigation.arrosage_service.service.ArrosageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class MeteoEventConsumer {

    private final ArrosageService arrosageService;

    public MeteoEventConsumer(ArrosageService arrosageService) {
        this.arrosageService = arrosageService;
    }

    @Bean
    public Consumer<ChangementConditionsEvent> meteoConditions() {
        return event -> {
            log.info("🌤️ Événement météo reçu: {}", event.getMessage());
            log.info("Station: {}, Date: {}, Temp Max: {}°C, Pluie: {}", 
                    event.getStationNom(), 
                    event.getDate(), 
                    event.getTemperatureMax(), 
                    event.getPluiePrevue());
            
            try {
                arrosageService.ajusterProgrammesSelonMeteo(event);
                log.info("✅ Programmes ajustés avec succès selon les nouvelles conditions météo");
            } catch (Exception e) {
                log.error("❌ Erreur lors de l'ajustement des programmes: {}", e.getMessage(), e);
            }
        };
    }
}
