package com.andrea.reactive.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class UpdateSatelliteRequest {

    @NotBlank(message = "'name' field must not be empty.")
    @Size(max = 255, message = "Name must be at most 255 characters long")
    private String name;

    @NotNull(message = "'date' must not be null")
    private OffsetDateTime date;

    @NotBlank(message = "Line1 must not be empty")
    private String line1;

    @NotBlank(message = "Line2 must not be empty")
    private String line2;

}

