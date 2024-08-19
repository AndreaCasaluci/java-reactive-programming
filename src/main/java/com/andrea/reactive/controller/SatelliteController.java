package com.andrea.reactive.controller;

import com.andrea.reactive.constants.SatelliteConstants;
import com.andrea.reactive.dto.SatelliteDto;
import com.andrea.reactive.dto.request.CreateSatelliteRequest;
import com.andrea.reactive.dto.request.UpdateSatelliteRequest;
import com.andrea.reactive.dto.response.GenericPagedResponse;
import com.andrea.reactive.dto.response.externalApi.FetchSatelliteResponse;
import com.andrea.reactive.exception.ValidationException;
import com.andrea.reactive.service.SatelliteService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.swing.*;
import java.util.*;

@RestController
@RequestMapping(SatelliteConstants.SATELLITE_BASE_PATH)
@Slf4j
public class SatelliteController {

    private final SatelliteService satelliteService;

    public SatelliteController(SatelliteService satelliteService) {
        this.satelliteService = satelliteService;
    }

    @PostMapping(SatelliteConstants.FETCH_ENDPOINT)
    public Mono<ResponseEntity<FetchSatelliteResponse>> fetchSatellitesFromExternalSource(
            @RequestParam(name=SatelliteConstants.FETCH_SIZE_PARAM_NAME, defaultValue = SatelliteConstants.FETCH_DEFAULT_SIZE_VALUE) int size,
            @RequestParam(name=SatelliteConstants.FETCH_CHUNK_SIZE_PARAM_NAME) Integer chunkSize
    ) {

        log.info("Start method - fetchSatellitesFromExternalSource - size: {} - chunkSize: {}", size, chunkSize);

        if(!Objects.isNull(chunkSize) && (chunkSize < SatelliteConstants.FETCH_MIN_CHUNK_SIZE_VALUE || chunkSize > SatelliteConstants.FETCH_MAX_CHUNK_SIZE_VALUE))
            return Mono.error(new ValidationException("Invalid chunk-size parameter. It must be between "+SatelliteConstants.FETCH_MIN_CHUNK_SIZE_VALUE +" and "+SatelliteConstants.FETCH_MAX_CHUNK_SIZE_VALUE +"."));

        return satelliteService.fetchAndUpdateSatellites(size, Optional.of(chunkSize))
                .doOnSuccess(response -> log.info("End method - fetchSatellitesFromExternalSource - size: {} - chunkSize: {}", size, chunkSize))
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @GetMapping("/{guid}")
    public Mono<ResponseEntity<SatelliteDto>> getSatelliteById(@PathVariable UUID guid) {

        log.info("Start method - getSatelliteById - guid: {}", guid);

        return satelliteService.getSatelliteByGuid(guid)
                .doOnSuccess(response -> log.info("End method - getSatelliteById - guid: {}", guid))
                .map(response -> ResponseEntity.ok(response));
    }

    @PostMapping
    public Mono<ResponseEntity<SatelliteDto>> createSatellite(@Valid @RequestBody CreateSatelliteRequest request) {

        log.info("Start method - createSatellite");

        return satelliteService.createSatellite(request)
                .doOnSuccess(response -> log.info("End method - createSatellite"))
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @PutMapping("/{guid}")
    public Mono<ResponseEntity<SatelliteDto>> updateSatellite(
            @PathVariable UUID guid,
            @Valid @RequestBody UpdateSatelliteRequest request) {

        log.info("Start method - updateSatellite - guid: {}", guid);

        return satelliteService.updateSatellite(guid, request)
                .doOnSuccess(response -> log.info("End method - updateSatellite - guid: {}", guid))
                .map(response -> ResponseEntity.ok(response));
    }

    @DeleteMapping("/{guid}")
    public Mono<ResponseEntity<Void>> deleteSatelliteByGuid(@PathVariable UUID guid) {

        log.info("Start method - deleteSatelliteByGuid - guid: {}", guid);

        return satelliteService.deleteSatellite(guid)
                .doOnSuccess(response -> log.info("End method - deleteSatelliteByGuid - guid: {}", guid))
                .then(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).build()));
    }

    @GetMapping(SatelliteConstants.GET_LIST_ENDPOINT)
    public Mono<ResponseEntity<GenericPagedResponse>> getList(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false) SortOrder nameOrder,
            @RequestParam(required = false) SortOrder dateOrder
    ) {

        log.info("Start method - getList - page: {} - size: {} - name: {} - nameOrder: {} - dateOrder: {}", page, size, name, nameOrder, dateOrder);

        return satelliteService.getSatellites(name, page, size, nameOrder, dateOrder)
                .doOnSuccess(response ->  log.info("Start method - getList - page: {} - size: {} - name: {} - nameOrder: {} - dateOrder: {}", page, size, name, nameOrder, dateOrder))
                .map(response -> ResponseEntity.ok(response));

    }

}
