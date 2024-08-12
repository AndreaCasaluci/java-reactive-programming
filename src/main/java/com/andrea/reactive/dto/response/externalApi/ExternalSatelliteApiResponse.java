package com.andrea.reactive.dto.response.externalApi;

import com.andrea.reactive.dto.TleDto;
import lombok.Data;

import java.util.List;

@Data
public class ExternalSatelliteApiResponse {

    private String context;
    private String id;
    private String type;
    private int totalItems;
    private List<TleDto> member;
    private Parameters parameters;
    private View view;

    @Data
    public static class Parameters {
        private String search;
        private String sort;
        private String sortDir;
        private int page;
        private int pageSize;
    }

    @Data
    public static class View {
        private String id;
        private String type;
        private String first;
        private String next;
        private String last;
    }
}
