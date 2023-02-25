package com.jwj.community.web.common.result;

import com.jwj.community.web.common.paging.PageInfo;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

import static com.jwj.community.web.common.paging.PageInfo.of;


@Data
public class ListResult<T> {

    private final List<T> list;
    private final int size;
    private final PageInfo pageInfo;

    @Builder
    public ListResult(List<T> list, Page<?> page) {
        this.list = list;
        this.size = list.size();
        this.pageInfo = page == null ? null : of(page);
    }
}
