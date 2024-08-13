package com.andrea.reactive.service;

import com.andrea.reactive.constants.SatelliteConstants;
import com.andrea.reactive.dto.SatelliteDto;
import com.andrea.reactive.dto.TleDto;
import com.andrea.reactive.dto.enumerator.FetchSatelliteResult;
import com.andrea.reactive.dto.request.CreateSatelliteRequest;
import com.andrea.reactive.dto.request.UpdateSatelliteRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.swing.*;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class SatelliteService {

    private final HttpService<ExternalSatelliteApiResponse> httpService;

    private final SatelliteRepository satelliteRepository;
    private final SatelliteMapper satelliteMapper;
    private final R2dbcEntityTemplate template;

    @Value("${satellite.api.url:https://tle.ivanstanojevic.me/api/tle/}")
    private String urlApi;

    public SatelliteService(HttpService<ExternalSatelliteApiResponse> httpService, SatelliteRepository satelliteRepository, SatelliteMapper satelliteMapper, R2dbcEntityTemplate template) {
        this.httpService = httpService;
        this.satelliteRepository = satelliteRepository;
        this.satelliteMapper = satelliteMapper;
        this.template = template;
    }

    public Mono<FetchSatelliteResponse> fetchAndUpdateSatellites(int size, Optional<Integer> chunkSizeOpt) {
        int defaultPageSize = SatelliteConstants.EXTERNAL_API_MAX_PAGE_SIZE;
        if(size < defaultPageSize) defaultPageSize = size;
        int pageSize = chunkSizeOpt.orElse(defaultPageSize);
        int totalPages = (int) Math.ceil((double) size / pageSize);
        List<Mono<ExternalSatelliteApiResponse>> apiCalls = new ArrayList<>();

        for (int i = 1; i <= totalPages; i++) {
            Map<String, String> queryParams = new HashMap<>();

            int currentPageSize = (i == totalPages) ? (size - (pageSize * (i - 1))) : pageSize;

            queryParams.put(SatelliteConstants.EXTERNAL_API_PAGE_PARAMETER_NAME, String.valueOf(i));
            queryParams.put(SatelliteConstants.EXTERNAL_API_PAGE_SIZE_PARAMETER_NAME, String.valueOf(currentPageSize));

            apiCalls.add(httpService.getMany(urlApi, ExternalSatelliteApiResponse.class, Optional.of(queryParams)));
        }

        ConcurrentMap<Integer, Mono<FetchSatelliteResult>> satelliteIdToProcessing = new ConcurrentHashMap<>();

        AtomicInteger newCount = new AtomicInteger();
        AtomicInteger updatedCount = new AtomicInteger();

        return Flux.merge(apiCalls)
                .flatMap(apiResponse -> Flux.fromIterable(apiResponse.getMember())
                        .concatMap(tleDto -> {
                            int satelliteId = tleDto.getSatelliteId();

                            return satelliteIdToProcessing
                                    .computeIfAbsent(satelliteId, id -> processSatellite(tleDto, newCount, updatedCount)
                                            .doFinally(signalType -> satelliteIdToProcessing.remove(satelliteId)));
                        })
                        .subscribeOn(Schedulers.boundedElastic())
                )
                .then(Mono.fromCallable(() -> new FetchSatelliteResponse(newCount.get(), updatedCount.get())))
                .onErrorResume(e -> Mono.error(new ExternalAPIException("Failed to fetch and update satellites: Service Unavailable or Resource Limit Exceeded")));

    }

    private Mono<FetchSatelliteResult> processSatellite(TleDto tleDto, AtomicInteger newCount, AtomicInteger updatedCount) {
        return satelliteRepository.findByExtId(tleDto.getSatelliteId())
                .flatMap(existingSatellite -> {

                    existingSatellite.setDate(tleDto.getDate());
                    existingSatellite.setLine1(tleDto.getLine1());
                    existingSatellite.setLine2(tleDto.getLine2());
                    existingSatellite.setUpdatedAt(OffsetDateTime.now());

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
                                        .createdAt(OffsetDateTime.now())
                                        .updatedAt(OffsetDateTime.now())
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

    public Mono<SatelliteDto> createSatellite(CreateSatelliteRequest request) {

        SatelliteDto satelliteDto = satelliteMapper.createSatelliteRequestToSatelliteDto(request);

        satelliteDto.setCreatedAt(OffsetDateTime.now());
        satelliteDto.setUpdatedAt(OffsetDateTime.now());

        Satellite convertedSatellite = satelliteMapper.satelliteDtoToSatellite(satelliteDto);

        return satelliteRepository.save(convertedSatellite)
                .flatMap(savedSatellite -> satelliteRepository.findById(savedSatellite.getId()))
                .map(retrievedSatellite -> satelliteMapper.satelliteToSatelliteDto(retrievedSatellite));


    }

    public Mono<SatelliteDto> updateSatellite(UUID guid, UpdateSatelliteRequest request) {
        return satelliteRepository.findByGuid(guid)
                .switchIfEmpty(Mono.error(new SatelliteNotFoundException("Satellite not found by GUID "+guid)))
                .flatMap(existingSatellite -> {
                    satelliteMapper.updateSatelliteFromRequest(request, existingSatellite);
                    existingSatellite.setUpdatedAt(OffsetDateTime.now());
                    return satelliteRepository.save(existingSatellite);
                })
                .map(satelliteMapper::satelliteToSatelliteDto);
    }

    public Mono<Void> deleteSatellite(UUID guid) {
        return satelliteRepository.findByGuid(guid)
                .switchIfEmpty(Mono.error(new SatelliteNotFoundException("Satellite not found by GUID "+guid)))
                .flatMap(satellite -> satelliteRepository.delete(satellite));
    }

    public Mono<Page<Satellite>> getSatellites(String name, String date, int page, int size, SortOrder nameOrder, SortOrder dateOrder) {
        PageRequest pageRequest = PageRequest.of(page, size, getSort(nameOrder, dateOrder));
        return this.satelliteRepository.findByNameContainingIgnoreCaseAndDateContainingIgnoreCase(name, date, pageRequest)
                .collectList()
                .zipWith(satelliteRepository.countByNameContainingIgnoreCaseAndDateContainingIgnoreCase(name, date))
                .map(t -> new PageImpl<>(t.getT1(), pageRequest, t.getT2()));
    }

    private Sort getSort(SortOrder nameOrder, SortOrder dateOrder) {
        List<Sort.Order> orders = new ArrayList<>();

        if (nameOrder != null) {
            orders.add(new Sort.Order(nameOrder == SortOrder.ASCENDING ? Sort.Direction.ASC : Sort.Direction.DESC, "name"));
        }
        if (dateOrder != null) {
            orders.add(new Sort.Order(dateOrder == SortOrder.DESCENDING ? Sort.Direction.ASC : Sort.Direction.DESC, "date"));
        }

        return Sort.by(orders);
    }

}
