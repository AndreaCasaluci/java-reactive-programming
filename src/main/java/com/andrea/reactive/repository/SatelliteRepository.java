package com.andrea.reactive.repository;

import com.andrea.reactive.entity.Satellite;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface SatelliteRepository extends ReactiveCrudRepository<Satellite, Integer> {

    Mono<Satellite> findByExtId(Integer extId);

    Mono<Satellite> findByGuid(UUID guid);

    @Query("SELECT * FROM satellite WHERE lower(name) LIKE lower(concat('%', :name, '%')) AND date::text LIKE concat('%', :date, '%')")
    Flux<Satellite> findByNameContainingIgnoreCaseAndDateContainingIgnoreCase(String name, String date, Pageable pageable);

    @Query("SELECT count(*) FROM satellite WHERE lower(name) LIKE lower(concat('%', :name, '%')) AND date::text LIKE concat('%', :date, '%')")
    Mono<Long> countByNameContainingIgnoreCaseAndDateContainingIgnoreCase(String name, String date);

}
