package com.andrea.reactive.controller;

import com.andrea.reactive.constants.SatelliteConstants;
import com.andrea.reactive.dto.TleDto;
import com.andrea.reactive.dto.response.externalApi.FetchSatelliteResponse;
import com.andrea.reactive.exception.ExternalAPIException;
import com.andrea.reactive.service.SatelliteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(SatelliteConstants.SATELLITE_BASE_PATH)
@Slf4j
public class SatelliteController {

    private final SatelliteService satelliteService;

    public SatelliteController(SatelliteService satelliteService) {
        this.satelliteService = satelliteService;
    }

    @PostMapping
    public Mono<ResponseEntity<FetchSatelliteResponse>> fetchSatellitesFromExternalSource(
            @RequestParam(name="size", defaultValue = "10") int size
    ) {
        return satelliteService.fetchAndUpdateSatellites(size)
                .map(response -> ResponseEntity.ok(response));
    }
}
