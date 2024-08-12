package com.andrea.reactive.repository;

import com.andrea.reactive.entity.Satellite;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface SatelliteRepository extends ReactiveCrudRepository<Satellite, Integer> {

    Mono<Satellite> findByExtId(Integer extId);

}
