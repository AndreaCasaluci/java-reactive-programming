package com.andrea.reactive.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class CreateSatelliteRequest {

    @NotBlank(message = "'name' field must not be empty.")
    @Size(max = 255, message = "Name must be at most 255 characters long")
    @Schema(example = "Exodus-A")
    private String name;

    @NotNull(message = "'date' must not be null")
    @Schema(example = "2024-08-12 14:50:02.000 +0200")
    private OffsetDateTime date;

    @NotBlank(message = "Line1 must not be empty")
    @Schema(example = "1 33331U 08042A   24225.53475204  .00004025  00000+0  76423-3 0  9997")
    private String line1;

    @NotBlank(message = "Line2 must not be empty")
    @Schema(example = "2 33331  98.1268 297.1782 0006600 359.1991   0.9200 14.64731231851540")
    private String line2;

}
