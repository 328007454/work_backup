package com.cnksi.inspe.utils;

import com.cnksi.inspe.type.RoleType;

import java.text.SimpleDateFormat;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/21 21:44
 */

public final class DateFormat {
    final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");

    private DateFormat() {
    }

    public static String formatYMD(Object date) {
        synchronized (sdf) {
            return sdf.format(date);
        }
    }
}
