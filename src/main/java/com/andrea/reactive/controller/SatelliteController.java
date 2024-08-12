package com.andrea.reactive.controller;

import com.andrea.reactive.constants.SatelliteConstants;
import com.andrea.reactive.dto.SatelliteDto;
import com.andrea.reactive.dto.response.externalApi.FetchSatelliteResponse;
import com.andrea.reactive.exception.ValidationException;
import com.andrea.reactive.service.SatelliteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

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
            @RequestParam(name=SatelliteConstants.FETCH_SIZE_PARAM_NAME, defaultValue = SatelliteConstants.FETCH_DEFAULT_SIZE_VALUE) int size
    ) {

        if(size < SatelliteConstants.FETCH_MIN_SIZE_VALUE || size > SatelliteConstants.FETCH_MAX_SIZE_VALUE)
            return Mono.error(new ValidationException("Invalid size parameter. Size must be between "+SatelliteConstants.FETCH_MIN_SIZE_VALUE+" and "+SatelliteConstants.FETCH_MAX_SIZE_VALUE+"."));

        return satelliteService.fetchAndUpdateSatellites(size)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @GetMapping("/{guid}")
    public Mono<ResponseEntity<SatelliteDto>> getSatelliteById(@PathVariable UUID guid) {
        return satelliteService.getSatelliteByGuid(guid)
                .map(response -> ResponseEntity.ok(response));
    }
}
