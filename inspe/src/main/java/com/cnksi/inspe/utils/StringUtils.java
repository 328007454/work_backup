package com.cnksi.inspe.utils;

import android.text.TextUtils;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/23 00:24
 */

public class StringUtils {
    public static String getFirstChar(String value) {
        if (TextUtils.isEmpty(value)) {
            return " ";
        }

        String str = value.replaceAll("[^\u4e00-\u9fa5]", "");
        if (str.length() > 0) {
            return str.substring(0, 1);
        }

        return value.substring(0, 1);
    }

    public static String getArrayToDbIn(String[] value) {
        if (value == null || value.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0, length = value.length; i < length; i++) {
            sb.append("'").append(value[i]).append("'").append(",");
        }

        return sb.substring(0, sb.length() - 1);
    }
}
