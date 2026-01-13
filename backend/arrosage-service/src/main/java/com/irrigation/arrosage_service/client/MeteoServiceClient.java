package com.irrigation.arrosage_service.client;

import com.irrigation.arrosage_service.dto.PrevisionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "meteo-service")
public interface MeteoServiceClient {

    @GetMapping("/api/meteo/previsions")
    List<PrevisionDTO> getPrevisions(
            @RequestParam Long stationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    );
}
