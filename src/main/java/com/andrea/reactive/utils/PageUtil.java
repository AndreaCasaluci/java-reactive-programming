package com.andrea.reactive.utils;

import com.andrea.reactive.dto.response.GenericPagedResponse;
import org.springframework.data.domain.Page;

public class PageUtil {

    public static GenericPagedResponse getGenericPage(Page<?> page) {
        return GenericPagedResponse.builder()
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .page(page.getPageable().getPageNumber())
                .size(page.getPageable().getPageSize())
                .content(page.getContent())
                .build();
    }

}
