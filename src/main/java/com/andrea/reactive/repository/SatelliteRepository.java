package com.andrea.reactive.repository;

import com.andrea.reactive.entity.Satellite;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SatelliteRepository extends ReactiveCrudRepository<Satellite, Integer> {

    Mono<Satellite> findByExtId(Integer extId);

    Mono<Satellite> findByGuid(UUID guid);

}
