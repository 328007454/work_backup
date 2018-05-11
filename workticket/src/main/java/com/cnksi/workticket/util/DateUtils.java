package com.cnksi.workticket.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * decritption 时间类
 * Created by Mr.K on 2018/5/10.
 */

public class DateUtils {

    public static List<String> getWeekDay() {
        Calendar calendar = Calendar.getInstance();
        // 获取本周的第一天
        int firstDayOfWeek = calendar.getFirstDayOfWeek();
        List<String> list = new ArrayList<>();
        int weekDays = 7;
        for (int i = 0; i < weekDays; i++) {
            calendar.set(Calendar.DAY_OF_WEEK, firstDayOfWeek + i);
            String date = new SimpleDateFormat("yyyy-MM-dd 00:00:00").format(calendar.getTime());
            list.add(date);
        }

        return list;
    }

    public static int get2TimeDifferenceHours(String date, String date2) {
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        String fromDate = simpleFormat.format(date);
        String toDate = simpleFormat.format(date2);
        long from = 0;
        try {
            from = simpleFormat.parse(fromDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long to = 0;
        try {
            to = simpleFormat.parse(toDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return (int) Math.abs((to - from)) / (1000 * 60 * 60);
    }

    public static int get2TimeDifferenceMinutes(String date, String date2) {
        SimpleDateFormat formatterFrom = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        SimpleDateFormat formatterTo = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        long from = 0;
        try {
            from = formatterFrom.parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long to = 0;
        try {
            to = formatterTo.parse(date2).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (to - from < 0) {
            return 99999999;
        }

        return (int) Math.abs((to - from)) / (1000 * 60);
    }


}
