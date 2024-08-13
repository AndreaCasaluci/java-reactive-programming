package com.andrea.reactive.service;

import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

public interface GenericWebClientService<T> {

    Mono<T> getMany(String uri, Class<T> responseType, Optional<Map<String, String>> queryParams);

}
