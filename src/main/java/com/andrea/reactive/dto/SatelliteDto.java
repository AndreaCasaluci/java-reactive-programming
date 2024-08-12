package com.andrea.reactive.dto;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class SatelliteDto {
    private UUID guid;

    @Column("ext_id")
    private Integer extId;

    private String name;

    private OffsetDateTime date;

    private String line1;

    private String line2;
}
