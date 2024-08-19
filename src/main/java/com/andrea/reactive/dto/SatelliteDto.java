package com.andrea.reactive.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class SatelliteDto {

    @Schema(example = "a0e4f423-4384-4a90-a4df-4c0f70085f66")
    private UUID guid;

    @Schema(example = "13405")
    private Integer extId;

    @Schema(example = "Exodus-A")
    private String name;

    @Schema(example = "2024-08-12 14:50:02.000 +0200")
    private OffsetDateTime date;

    @Schema(example = "1 33331U 08042A   24225.53475204  .00004025  00000+0  76423-3 0  9997")
    private String line1;

    @Schema(example = "2 33331  98.1268 297.1782 0006600 359.1991   0.9200 14.64731231851540")
    private String line2;

    @Schema(example = "2024-08-13 12:01:23.166 +0200")
    private OffsetDateTime createdAt;

    @Schema(example = "2024-08-13 12:01:23.166 +0200")
    private OffsetDateTime updatedAt;
}
