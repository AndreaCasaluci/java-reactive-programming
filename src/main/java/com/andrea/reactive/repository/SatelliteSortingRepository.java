package com.andrea.reactive.repository;

import com.andrea.reactive.entity.Satellite;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SatelliteSortingRepository extends ReactiveSortingRepository<Satellite, Integer> {

    Flux<Satellite> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Mono<Long> countByNameContainingIgnoreCase(String name);
}
