package com.jwj.community.web.common.paging.request;

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

    public BoardSearchCondition(Integer page, Boolean tempSave, String keyword, BoardSearchType searchType) {
        this.page = page == null ? 0 : page;
        this.tempSave = tempSave != null && tempSave;
        this.keyword = keyword;
        this.searchType = searchType;
    }

    public Pageable getPageable(){
        return PageRequest.of(max(0, page), min(size, MAX_SIZE));
    }

 /**
  *     2. 글의 조회조건, 페이징 기능 구현, 정렬조건
  * 	1) 글 리스트 조회조건 -> 제목, 작성자닉네임을 검색어로 입력
  * 	2) 정렬조건 -> 최신순, 추천순, 조회순
  * */
}
