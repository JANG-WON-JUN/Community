package com.jwj.community.utils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.util.ObjectUtils;

import java.time.DateTimeException;
import java.time.LocalDate;
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
        if(str == null || "null".equals(str)){
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

    public static boolean isValidDate(Integer year, Integer month, Integer day){
        if (year == null || month == null || day == null){
            return false;
        }

        try {
            LocalDate.of(year, month, day);
            return true;
        }catch (DateTimeException ex) {
            return false;
        }
    }
}
