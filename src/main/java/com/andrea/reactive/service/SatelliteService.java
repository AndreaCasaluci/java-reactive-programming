package com.andrea.reactive.service;

import com.andrea.reactive.constants.SatelliteConstants;
import com.andrea.reactive.dto.SatelliteDto;
import com.andrea.reactive.dto.TleDto;
import com.andrea.reactive.dto.enumerator.FetchSatelliteResult;
import com.andrea.reactive.dto.response.externalApi.ExternalSatelliteApiResponse;
import com.andrea.reactive.dto.response.externalApi.FetchSatelliteResponse;
import com.andrea.reactive.entity.Satellite;
import com.andrea.reactive.exception.DatabaseOperationException;
import com.andrea.reactive.exception.ExternalAPIException;
import com.andrea.reactive.exception.SatelliteNotFoundException;
import com.andrea.reactive.mapper.SatelliteMapper;
import com.andrea.reactive.repository.SatelliteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class SatelliteService {

    private final HttpService<ExternalSatelliteApiResponse> httpService;

    private final SatelliteRepository satelliteRepository;
    private final SatelliteMapper satelliteMapper;

    @Value("${satellite.api.url:https://tle.ivanstanojevic.me/api/tle/}")
    private String urlApi;

    public SatelliteService(HttpService<ExternalSatelliteApiResponse> httpService, SatelliteRepository satelliteRepository, SatelliteMapper satelliteMapper) {
        this.httpService = httpService;
        this.satelliteRepository = satelliteRepository;
        this.satelliteMapper = satelliteMapper;
    }

    public Mono<FetchSatelliteResponse> fetchAndUpdateSatellites(int size) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(SatelliteConstants.EXTERNAL_API_PAGE_PARAMETER_NAME, String.valueOf(SatelliteConstants.EXTERNAL_API_DEFAULT_PAGE_VALUE));
        queryParams.put(SatelliteConstants.EXTERNAL_API_PAGE_SIZE_PARAMETER_NAME, String.valueOf(size));

        AtomicInteger newCount = new AtomicInteger();
        AtomicInteger updatedCount = new AtomicInteger();

        return httpService.getMany(urlApi, ExternalSatelliteApiResponse.class, Optional.of(queryParams))
                .flatMap(apiResponse -> Flux.fromIterable(apiResponse.getMember())
                        .parallel()
                        .runOn(Schedulers.boundedElastic())
                        .flatMap(tleDto -> processSatellite(tleDto, newCount, updatedCount))
                        .sequential()
                )
                .then(Mono.fromCallable(() -> new FetchSatelliteResponse(newCount.get(), updatedCount.get())))
                .onErrorResume(e -> {
                    return Mono.error(new ExternalAPIException("Failed to fetch and update satellites"));
                });
    }

    private Mono<FetchSatelliteResult> processSatellite(TleDto tleDto, AtomicInteger newCount, AtomicInteger updatedCount) {
        return satelliteRepository.findByExtId(tleDto.getSatelliteId())
                .flatMap(existingSatellite -> {

                    existingSatellite.setDate(tleDto.getDate());
                    existingSatellite.setLine1(tleDto.getLine1());
                    existingSatellite.setLine2(tleDto.getLine2());

                    updatedCount.incrementAndGet();

                    return satelliteRepository.save(existingSatellite)
                            .then(Mono.just(FetchSatelliteResult.UPDATED));
                })
                .switchIfEmpty(
                        satelliteRepository.save(Satellite.builder()
                                        .extId(tleDto.getSatelliteId())
                                        .name(tleDto.getName())
                                        .line1(tleDto.getLine1())
                                        .line2(tleDto.getLine2())
                                        .date(tleDto.getDate())
                                        .build())
                                .doOnSuccess(savedSatellite -> newCount.incrementAndGet())
                                .then(Mono.just(FetchSatelliteResult.NEW))
                )
                .onErrorResume(e -> {
                    log.error("Error processing satellite", e);
                    return Mono.error(new DatabaseOperationException("Failed to process satellite with ExtID: " + tleDto.getSatelliteId()));
                });
    }

    public Mono<SatelliteDto> getSatelliteByGuid(UUID guid) {
        return satelliteRepository.findByGuid(guid)
                .map(satellite -> satelliteMapper.satelliteToSatelliteDto(satellite))
                .switchIfEmpty(Mono.error(new SatelliteNotFoundException("Satellite not found by GUID "+guid)));
    }
}
