package com.cnksi.inspe.utils;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/23 00:24
 */

public class StringUtils {
    public static String getFirstChar(String value) {
        String str = value.replaceAll("[^\u4e00-\u9fa5]", "");
        if (str.length() > 0) {
            return str.substring(0, 1);
        }

        return value.substring(0, 1);
    }
}
