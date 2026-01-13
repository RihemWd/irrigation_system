package com.irrigation.meteo_service.messaging;

import com.irrigation.meteo_service.event.ChangementConditionsEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MeteoEventPublisher {

    private final StreamBridge streamBridge;

    public MeteoEventPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void publishChangementConditions(ChangementConditionsEvent event) {
        log.info("Publication de l'événement ChangementConditions: {}", event);
        streamBridge.send("meteoConditions-out-0", event);
    }
}
