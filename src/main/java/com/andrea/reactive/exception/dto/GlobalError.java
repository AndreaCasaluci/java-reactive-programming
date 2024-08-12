package com.andrea.reactive.exception.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Data
@Builder
public class GlobalError {
    private HttpStatus status;
    private String message;
    private ZonedDateTime timestamp;
}
