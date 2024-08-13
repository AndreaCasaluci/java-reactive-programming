package com.andrea.reactive.dto;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class SatelliteDto {

    private UUID guid;

    private Integer extId;

    private String name;

    private OffsetDateTime date;

    private String line1;

    private String line2;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}
