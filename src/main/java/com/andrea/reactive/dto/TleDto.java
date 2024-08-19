package com.andrea.reactive.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class TleDto {

    private int satelliteId;
    private String name;
    private OffsetDateTime date;
    private String line1;
    private String line2;

}
