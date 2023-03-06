package com.jwj.community.web.common.paging.request;

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
public class CommentSearchCondition {

    private static final int MAX_SIZE = 2000;
    private final int size = 10;

    private int page;
    private Long boardId;

    public CommentSearchCondition(int page, Long boardId) {
        this.page = page;
        this.boardId = boardId;
    }

    public Pageable getPageable(){
        return PageRequest.of(max(0, page), min(size, MAX_SIZE));
    }
}
