package com.jwj.community.web.common.result;

import lombok.Builder;
import lombok.Data;

@Data
public class Result<T> {

    public T data;

    @Builder
    public Result(T data) {
        this.data = data;
    }
}
