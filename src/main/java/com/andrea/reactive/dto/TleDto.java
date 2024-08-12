package com.andrea.reactive.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class TleDto {

    private int satelliteId;
    private String name;
    private OffsetDateTime date;
    private String line1;
    private String line2;

}
