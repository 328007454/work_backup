package com.cnksi.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间格式化工具类
 *
 * @author terry
 */
public class DateCalcUtils {


    /**
     * 得到本周周一
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getMondayOfThisWeek() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Calendar c = Calendar.getInstance();
        int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0) {
            day_of_week = 7;
        }
        c.add(Calendar.DATE, -day_of_week + 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return formatter.format(c.getTime());
    }

    /**
     * 得到本周周日
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getSundayOfThisWeek() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        Calendar c = Calendar.getInstance();
        int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0) {
            day_of_week = 7;
        }
        c.add(Calendar.DATE, -day_of_week + 7);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        return formatter.format(c.getTime());
    }



    /**
     * 将毫秒数转换为String
     *
     * @param time
     * @param format String yyyy/MM/dd HH:mm:ss
     */
    public static String longTimeToDate(long time, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.CHINA);
        return formatter.format(new Date(time));
    }





    /**
     * 格式化时间
     *
     * @param date
     * @param format
     * @return
     */
    public static String getFormatterTime(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.CHINA);
        return formatter.format(date);
    }

    /**
     * 格式化时间
     *
     * @param timeStr
     * @param format
     */
    public static String getFormatterTime(String timeStr, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.CHINA);
        try {
            timeStr = formatter.format(formatter.parse(timeStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStr;
    }

    /**
     * 得到当前时间是星期几
     *
     * @return 星期一
     */
    public static String getCurrentWeekDay() {
        SimpleDateFormat dateFm = new SimpleDateFormat("EEEE", Locale.CHINA);
        return dateFm.format(new Date());
    }

    /**
     * 计算两个时间的差
     *
     * @param startTimeStr
     * @param endTimeStr
     * @return
     */
    public static String getTimeDifference(String startTimeStr, String endTimeStr, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.CHINA);
        String differenceTime = "";
        try {
            differenceTime = getTimeDifference(formatter.parse(startTimeStr), formatter.parse(endTimeStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return differenceTime;
    }
    /**
     * 计算两个时间的差
     *
     * @param startTimeStr
     * @param endTimeStr
     * @return
     */
    public static String getTimeDifference(String startTimeStr, String endTimeStr) {
        SimpleDateFormat formatter = new SimpleDateFormat(com.cnksi.core.utils.DateUtils.yyyy_MM_dd_HH_mm_ss, Locale.CHINA);
        String differenceTime = "";
        try {
            differenceTime = getTimeDifference(formatter.parse(startTimeStr), formatter.parse(endTimeStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return differenceTime;
    }
    /**
     * 计算两个时间的差
     * @return
     */
    public static String getTimeDifference(Date startTime, Date endTime) {
        long l = endTime.getTime() - startTime.getTime();
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        // long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        StringBuilder sbTime = new StringBuilder();
        if (day > 0) {
            sbTime.append(day).append("天");
        }
        if (hour > 0) {
            sbTime.append(hour).append("小时");
        }
        sbTime.append(min).append("分钟");

        return sbTime.toString();
    }





    /**
     * 判断 时间1 是否大于 时间2 如果大于 返回true
     *
     * @param dateStr1
     * @param dateStr2
     * @return
     */
    public static boolean compareDate(String dateStr1, String dateStr2, String format) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(format, Locale.CHINA);
            Date dt1 = df.parse(dateStr1);
            Date dt2 = df.parse(dateStr2);
            return compareDate(dt1, dt2);
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * 判断 时间1 是否大于 时间2 如果大于 返回true
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean compareDate(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(date1);
        calendar2.setTime(date2);
        int result = calendar1.compareTo(calendar2);
        if (result >= 0) {
            return true;
        } else {
            return false;
        }
    }










    /**
     * @param currentTime
     * @param hours
     * @param formatter
     * @return
     */

    public static Calendar getAfterDay(String currentTime, int hours, String formatter) {
        SimpleDateFormat df = new SimpleDateFormat(formatter, Locale.CHINA);
        Date date = null;
        try {
            date = df.parse(currentTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, hours);
        return calendar;
    }

    /**
     * 上周一
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getFirstOfLastWeek() {
        SimpleDateFormat df = new SimpleDateFormat(com.cnksi.core.utils.DateUtils.yyyy_MM_dd_HH_mm_ss, Locale.CHINA);
        Calendar cal = Calendar.getInstance();
        //设置每周第一天为星期一 (默认是星期日)
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.add(Calendar.DAY_OF_MONTH, -7);
        return df.format(cal.getTime());
    }


    /**
     * 计算两个时间的差
     *
     * @param startTimeStr
     * @param endTimeStr
     * @return
     */
    public static long getTimeDifferenceDays(String startTimeStr, String endTimeStr, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.CHINA);
        long differenceTime = 0L;
        try {
            differenceTime = getTimeDifferenceDays(formatter.parse(startTimeStr), formatter.parse(endTimeStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return differenceTime;
    }

    /**
     * 计算两个时间的差
     *
     * @param startTimeStr
     * @param endTimeStr
     * @return
     */
    public static long getTimeDifferenceDays(String startTimeStr, String endTimeStr) {
        SimpleDateFormat formatter = new SimpleDateFormat(com.cnksi.core.utils.DateUtils.yyyy_MM_dd_HH_mm_ss, Locale.CHINA);
        long differenceTime = 0L;
        try {
            differenceTime = getTimeDifferenceDays(formatter.parse(startTimeStr), formatter.parse(endTimeStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return differenceTime;
    }

    /**
     * 计算两个时间的差
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static long getTimeDifferenceDays(Date startTime, Date endTime) {
        long l = endTime.getTime() - startTime.getTime();
        long day = l / (24 * 60 * 60 * 1000);
        return day;
    }

    public static String getAfterMonth(String date, int months) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(com.cnksi.core.utils.DateUtils.yyyy_MM_dd_HH_mm_ss);
        Date date1 = null;
        String afterTime = "";
        try {
            date1 = sdf.parse(com.cnksi.core.utils.DateUtils.getFormatterTime(date, com.cnksi.core.utils.DateUtils.yyyy_MM_dd_HH_mm_ss));
            calendar.setTime(date1);//设置日历时间
            calendar.add(Calendar.DATE, -1);
            calendar.add(Calendar.MONTH, months);//在日历的月份上增加6个月
            afterTime = sdf.format(calendar.getTime()).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return afterTime;
    }

    public static Calendar getAfterMinute(String currentTime, int minute, String formatter) {
        SimpleDateFormat df = new SimpleDateFormat(formatter, Locale.CHINA);
        Date date = null;
        try {
            date = df.parse(currentTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minute);
        return calendar;
    }

    public static Calendar getCalendar(String currentTime,String formatter) {
        SimpleDateFormat df = new SimpleDateFormat(formatter, Locale.CHINA);
        Date date = null;
        try {
            date = df.parse(currentTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
}
