package com.cnksi.inspe.utils;

import java.util.Collection;
import java.util.List;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/27 17:14
 */

public final class ArrayInspeUtils {

    public static String toListString(Collection<String> list) {
        StringBuilder sb = new StringBuilder();

        for (String i : list) {
            sb.append(i).append(',');
        }

        return sb.substring(0, sb.length() > 0 ? sb.length() - 1 : 0);
    }

    public static String toListIntegerString(Collection<Integer> list) {
        StringBuilder sb = new StringBuilder();

        for (Integer i : list) {
            sb.append(i).append(',');
        }

        return sb.substring(0, sb.length() > 0 ? sb.length() - 1 : 0);
    }
}
