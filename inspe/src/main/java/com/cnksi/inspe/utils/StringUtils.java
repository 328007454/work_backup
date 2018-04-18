package com.cnksi.inspe.utils;

import android.os.Build;
import android.text.TextUtils;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

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

    public static String getDeviceStandardsType(String bigId) {
        if (!TextUtils.isEmpty(bigId)) {
            List<String> bigIdList = com.cnksi.core.utils.StringUtils.stringToList(bigId, ",");
            if (bigIdList.size() == 1)
                return "(" + bigIdList.get(0) + ")";
            else {
                StringBuilder builder = new StringBuilder();
                for (String id : bigIdList) {
                    builder.append(id + ",");
                }
                return "(" + builder.toString().substring(0, builder.toString().length() - 1) + ")";
            }
        } else {
            return "";
        }
    }

    /**
     * 判断是否字段的值是否为空或null
     *
     * @param str
     * @return
     */
    public static String cleanString(String str) {
        if (TextUtils.isEmpty(str) || "null".equalsIgnoreCase(str)) {
            return "";
        } else {
            return str.trim();
        }
    }

    /**
     * 字符串分词
     *
     * @param sentence
     * @return
     */
    public static ArrayList<String> breakWord(final String sentence) {
        final ArrayList<String> result = new ArrayList<String>();
        if (!TextUtils.isEmpty(sentence)) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                final BreakIterator boundary = BreakIterator.getWordInstance();//目前仅使用分词，还可以根据单字、句进行分割
                boundary.setText(sentence);
                try {
                    int start = boundary.first();
                    for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next()) {
                        String word = sentence.substring(start, end);
                        if (!TextUtils.isEmpty(word)) {
                            result.add(word);
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    result.clear();
                }
            } else {
                //仅能在Android-O及以上版本使用
                final android.icu.text.BreakIterator boundary = android.icu.text.BreakIterator.getWordInstance();
                boundary.setText(sentence);
                try {
                    int start = boundary.first();
                    for (int end = boundary.next(); end != android.icu.text.BreakIterator.DONE; start = end, end = boundary.next()) {
                        String word = sentence.substring(start, end);
                        if (!TextUtils.isEmpty(word)) {
                            result.add(word);
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    result.clear();
                }
            }
        }
        return result;
    }
}
