package com.cnksi.inspe.utils;

import com.cnksi.inspe.type.RoleType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/21 21:44
 * @Deprecated sjjc拷贝，解决依赖问题，后可以根据需要删除
 */

public final class DateFormat {
    final static SimpleDateFormat sdfymd = new SimpleDateFormat("yyyy年MM月dd日");

    final static SimpleDateFormat sdfymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private DateFormat() {
    }

//    public static String

    public static String formatYMD(long date) {
        synchronized (sdfymd) {
            return sdfymd.format(date);
        }
    }

    public static String formatYMD(Date date) {
        synchronized (sdfymd) {
            return sdfymd.format(date);
        }
    }

    /**
     * 将系统时间转为数据库存储的日期字符串
     *
     * @param datetime
     * @return
     */
    public static String dateToDbString(long datetime) {
        synchronized (sdfymdhms) {
            return sdfymdhms.format(datetime);
        }
    }

    /**
     * 将日期字符串转为long
     *
     * @param datetime 必须满足"yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static long dbdateToLong(String datetime) {
        synchronized (sdfymdhms) {
            try {
                return sdfymdhms.parse(datetime).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return 0;
    }
}
