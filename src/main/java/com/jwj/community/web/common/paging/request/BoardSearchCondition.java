package com.jwj.community.web.common.paging.request;

import com.jwj.community.domain.enums.BoardTypes;
import com.jwj.community.web.enums.BoardSearchType;
import com.jwj.community.web.enums.SearchOrder;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Data
@Builder
@NoArgsConstructor
public class BoardSearchCondition {

    private static final int MAX_SIZE = 2000;
    private final int size = 10;

    private int page;
    private boolean tempSave;
    private String keyword;
    private BoardTypes boardType;
    private BoardSearchType searchType;
    private SearchOrder searchOrder;

    public BoardSearchCondition(int page, boolean tempSave, String keyword,
                                BoardTypes boardTypes, BoardSearchType searchType,
                                SearchOrder searchOrder) {
        this.page = page;
        this.tempSave = tempSave;
        this.keyword = keyword;
        this.boardType = boardTypes;
        this.searchType = searchType;
        this.searchOrder = searchOrder;
    }

    public Pageable getPageable(){
        return PageRequest.of(max(0, page), min(size, MAX_SIZE));
    }
}
