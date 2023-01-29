package com.jwj.community.web.common.paging;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

@Data
@Builder
public class PageInfo {

    private long pageNumber;
    private long pageSize;
    private long totalPage;
    private long totalSize;

    public static PageInfo of(Page page){
        return PageInfo.builder()
                .pageNumber(page.getPageable().getPageNumber())
                .pageSize(page.getPageable().getPageSize())
                .totalPage(page.getTotalPages())
                .totalSize(page.getTotalElements())
                .build();
    }
}
