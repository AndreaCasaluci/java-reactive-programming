package com.andrea.reactive.service;

import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Optional;

public interface GenericWebClientService<T> {

    Flux<T> getMany(String uri, Class<T> responseType, Optional<Map<String, String>> queryParams);

}
