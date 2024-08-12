package com.andrea.reactive.dto.response.externalApi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FetchSatelliteResponse {
    private int newCount;
    private int updatedCount;
}
