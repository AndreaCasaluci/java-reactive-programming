package com.andrea.reactive.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenericPagedResponse {

    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;
    private List<?> content;
}
