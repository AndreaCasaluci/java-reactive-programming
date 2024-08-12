package com.andrea.reactive.dto;

import lombok.Data;

@Data
public class HttpResult {
    private int status;
    private String body;
}
