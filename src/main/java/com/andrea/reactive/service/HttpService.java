package com.andrea.reactive.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@Service
public class HttpService<T> implements GenericWebClientService<T> {

    private final WebClient webClient;

    public HttpService(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Flux<T> getMany(String uri, Class<T> responseType, Optional<Map<String, String>> queryParams) {

        StringBuilder uriBuilder = new StringBuilder(uri);

        if (queryParams.isPresent() && !queryParams.get().isEmpty()) {
            uriBuilder.append("?");
            queryParams.get().forEach((key, value) -> {
                uriBuilder.append(URLEncoder.encode(key, StandardCharsets.UTF_8))
                        .append("=")
                        .append(URLEncoder.encode(value, StandardCharsets.UTF_8))
                        .append("&");
            });

            uriBuilder.setLength(uriBuilder.length() - 1);
        }

        return webClient.get()
                .uri(uriBuilder.toString())
                .retrieve()
                .bodyToFlux(responseType);
    }

}
