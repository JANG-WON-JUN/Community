package com.jwj.community.utils;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

public class CommonUtils {

    public static LocalDateTime relativeDayFromNow(int days){
        return now().plusDays(days);
    }

    public static LocalDateTime relativeMonthFromNow(int months){
        return now().plusMonths(months);
    }

}
