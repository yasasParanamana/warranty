package com.oxcentra.warranty.util.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CommonUtil {

    public CommonUtil() {
    }

    public static Date getMidnightOnDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTime();
    }

    public static Date getMidnightBeforeNextDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(11, 23);
        calendar.set(12, 59);
        calendar.set(13, 59);
        return calendar.getTime();
    }

    public static String getDateAsStringWithPattern(Date date, String pattern) {
        SimpleDateFormat sf = new SimpleDateFormat(pattern);
        return sf.format(date);
    }
}
