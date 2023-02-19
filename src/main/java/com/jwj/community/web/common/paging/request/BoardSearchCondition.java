package com.jwj.community.web.common.paging.request;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Data
@Builder
public class BoardSearchCondition {

    private static final int MAX_SIZE = 2000;

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 10;

    public Pageable getPageable(){
        return PageRequest.of(max(0, page), min(size, MAX_SIZE));
    }
}
