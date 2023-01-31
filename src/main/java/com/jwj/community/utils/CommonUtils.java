package com.jwj.community.utils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.function.Supplier;

import static java.time.LocalDateTime.now;

public class CommonUtils {

    public static LocalDateTime relativeMinuteFromNow(int minutes) {
        return now().plusMinutes(minutes);
    }

    public static LocalDateTime relativeDayFromNow(int days){
        return now().plusDays(days);
    }

    public static LocalDateTime relativeMonthFromNow(int months){
        return now().plusMonths(months);
    }

    public static boolean isEmpty(String str){
        if(str == null){
            return true;
        }
        if("null".equals(str)){
            return true;
        }
        return ObjectUtils.isEmpty(str.trim());
    }

    public static BooleanBuilder nullSafeBuilder(Supplier<BooleanExpression> f) {
        try {
            return new BooleanBuilder(f.get());
        } catch (IllegalArgumentException e) {
            return new BooleanBuilder();
        }
    }
}
