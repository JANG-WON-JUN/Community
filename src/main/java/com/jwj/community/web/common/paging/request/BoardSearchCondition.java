package com.jwj.community.web.common.paging.request;

import com.jwj.community.web.enums.SearchOrder;
import com.jwj.community.web.enums.BoardSearchType;
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
    private final Integer size = 10;

    private Integer page;
    private Boolean tempSave;
    private String keyword;
    private BoardSearchType searchType;
    private SearchOrder searchOrder;

    public BoardSearchCondition(Integer page, Boolean tempSave, String keyword,
                                BoardSearchType searchType, SearchOrder searchOrder) {
        this.page = page == null ? 0 : page;
        this.tempSave = tempSave != null && tempSave;
        this.keyword = keyword;
        this.searchType = searchType;
        this.searchOrder = searchOrder;
    }

    public Pageable getPageable(){
        return PageRequest.of(max(0, page), min(size, MAX_SIZE));
    }
}
