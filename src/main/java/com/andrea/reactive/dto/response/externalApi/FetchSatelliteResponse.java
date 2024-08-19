package com.andrea.reactive.dto.response.externalApi;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FetchSatelliteResponse {
    @Schema(example = "50")
    private int newCount;
    @Schema(example = "49")
    private int updatedCount;
}
