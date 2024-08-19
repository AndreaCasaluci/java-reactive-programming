package com.andrea.reactive.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class HttpService<T> implements GenericWebClientService<T> {

    private final WebClient webClient;

    public HttpService(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<T> getMany(String uri, Class<T> responseType, Optional<Map<String, String>> queryParams) {

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

        String finalUri = uriBuilder.toString();

        log.info("Start method - getMany - URI: {}", finalUri);

        return webClient.get()
                .uri(finalUri)
                .retrieve()
                .bodyToMono(responseType)
                .doOnSuccess(response -> log.info("End method - getMany - URI: {}", finalUri));
    }

}
