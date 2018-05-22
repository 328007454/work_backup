package com.cnksi.sjjc.util;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * 时间格式化工具类
 *
 * @author terry
 */
public class DateUtils {
    /**
     * yyyy-MM-dd HH:mm:ss
     */
    private static String dateN = "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}";

    private DateUtils() {

    }


    /**
     * 将毫秒数转换为String
     *
     * @param time 毫秒数
     * @return String yyyy/MM/dd HH:mm:ss
     */
    public static String longTimeToDate(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat(CoreConfig.dateFormat4, Locale.CHINA);
        return formatter.format(new Date(time));
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
     * 得到当前时间
     *
     * @return String yyyy-MM-dd HH:mm:ss
     */
    public static String getCurrentLongTime() {
        SimpleDateFormat formatter = new SimpleDateFormat(CoreConfig.dateFormat2, Locale.CHINA);
        return formatter.format(new Date());
    }

    /**
     * 得到当前时间
     *
     * @return String yyyy-MM-dd
     */
    public static String getCurrentShortTime() {
        SimpleDateFormat formatter = new SimpleDateFormat(CoreConfig.dateFormat1, Locale.CHINA);
        return formatter.format(new Date());
    }

    /**
     * 得到当前时间
     *
     * @param format
     * @return String 时间字符串
     */
    public static String getCurrentTime(String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.CHINA);
        return formatter.format(new Date());
    }

    /**
     * 得到更新时间
     *
     * @return String
     */
    public static String getUpdateTime() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    /**
     * 得到当前时间
     *
     * @return String yyyy-MM-dd-HH-mm-ss
     */
    public static String getPageNameTime() {
        SimpleDateFormat formatter = new SimpleDateFormat(CoreConfig.dateFormat3, Locale.CHINA);
        return formatter.format(new Date());
    }

    /**
     * 格式化时间
     *
     * @param timeStr
     * @return "yyyy-MM-dd"
     */
    public static String getFormatterTime(String timeStr) {
        SimpleDateFormat formatter = new SimpleDateFormat(CoreConfig.dateFormat1, Locale.CHINA);
        try {
            timeStr = formatter.format(formatter.parse(timeStr));
        } catch (Exception e) {
            timeStr = "";
        }
        return timeStr;
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
        } catch (Exception e) {
            e.printStackTrace();
            timeStr = "";
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
    public static String getTimeDifference(String startTimeStr, String endTimeStr) {
        SimpleDateFormat formatter = new SimpleDateFormat(CoreConfig.dateFormat2, Locale.CHINA);
        String differenceTime = "";
        try {
            differenceTime = getTimeDifference(formatter.parse(startTimeStr), formatter.parse(endTimeStr));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return differenceTime;
    }

    /**
     * 计算两个时间的差
     *
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
     * 字符串转时间 将 yyyy-MM-dd HH:mm:ss 时间格式转换为 MM-dd,HH:mm
     *
     * @param dateStr
     * @return
     */
    public static String formatDateTime(String dateStr) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(CoreConfig.dateFormat2, Locale.CHINA);
            ParsePosition pos = new ParsePosition(0);
            SimpleDateFormat formatter2 = new SimpleDateFormat(CoreConfig.dateFormat5, Locale.CHINA);
            dateStr = formatter2.format(formatter.parse(dateStr, pos));
        } catch (Exception e) {
            dateStr = "";
        }
        return dateStr;
    }


    /**
     * 字符串转时间 将 yyyy-MM-dd HH:mm:ss
     *
     * @param dateStr
     * @return
     */
    public static String formatDateTime(String dateStr, String format) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(CoreConfig.dateFormat2, Locale.CHINA);
            ParsePosition pos = new ParsePosition(0);
            SimpleDateFormat formatter2 = new SimpleDateFormat(format, Locale.CHINA);
            dateStr = formatter2.format(formatter.parse(dateStr, pos));
        } catch (Exception e) {
            dateStr = "";
        }
        return dateStr;
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
     * 得到当前时间多少天之前的时间
     *
     * @param days
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getPreTime(int days) {
        return getPreTime(days, CoreConfig.dateFormat2);
    }

    /**
     * 得到当前时间多少天之前的时间
     *
     * @param days
     * @param formatter 时间格式
     * @return
     */
    public static String getPreTime(int days, String formatter) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -days);
        return getFormatterTime(calendar.getTime(), formatter);
    }

    /**
     * 得到当前时间多少天之后的时间
     *
     * @param days
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getAfterTime(int days) {
        return getAfterTime(days, CoreConfig.dateFormat2);
    }

    /**
     * 得到当前时间多少天之后的时间
     *
     * @param days
     * @param formatter 时间格式
     * @return
     */
    public static String getAfterTime(int days, String formatter) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, days);
        return getFormatterTime(calendar.getTime(), formatter);
    }

    /**
     * 得到某天时间之后的多少天
     *
     * @param currentTime
     * @param days
     * @param formatter
     * @return
     */
    public static String getAfterTime(String currentTime, int days, String formatter) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(formatter, Locale.CHINA);
            Date date = df.parse(currentTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, days);
            return getFormatterTime(calendar.getTime(), formatter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 上周一
     *
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String getFirstOfLastWeek() {
        SimpleDateFormat df = new SimpleDateFormat(CoreConfig.dateFormat2, Locale.CHINA);
        Calendar cal = Calendar.getInstance();
        //设置每周第一天为星期一 (默认是星期日)
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.add(Calendar.DAY_OF_MONTH, -7);
        return df.format(cal.getTime());
    }

    /**
     * 得到当前时间多少小时之前的时间
     *
     * @param hours
     * @param formatter 时间格式
     * @return
     */
    public static String getPreHour(int hours, String formatter) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -hours);
        return getFormatterTime(calendar.getTime(), formatter);
    }

    public static boolean timeNormal(String time) {
        boolean date = Pattern.compile(dateN).matcher(time).matches();
        return date;
    }

}
