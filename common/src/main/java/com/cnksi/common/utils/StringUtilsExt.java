package com.cnksi.common.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Wastrel
 * @version 1.0
 * @date 2018/5/22 10:27
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class StringUtilsExt {

    public static boolean isEmptys(String... strings) {
        for (String string : strings) {
            if (TextUtils.isEmpty(string)) {
                return true;
            }
        }
        return false;
    }

    private static Pattern pattern = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]");

    /**
     * emoj表情判断
     */
    public static boolean hasEmoji(String content) {

        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return true;
        }
        return false;
    }

    public static String nullTo(String string, String string1) {
        return TextUtils.isEmpty(string) ? string1 : string;
    }


    /**
     * 获取两位小数点
     */
    public static String getDecimalPoint(String temperature) {
        return getDecimalPoint(temperature, 2);
    }

    /**
     * 获取三位小数点
     */
    public static String getDecimalPoint(String temperature, int num) {
        Double tempFloat;
        int sum = 1;
        try {
            tempFloat = Double.valueOf(temperature);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
        if (tempFloat == 0.0f) {
            return "0.0";
        }
        if (!temperature.contains(".")) {
            return temperature;
        }

        for (int i = 1; i <= num; i++) {
            sum = sum * 10;
        }

        return String.valueOf((float) (Math.round(tempFloat * sum)) / sum);
    }


    /**
     * 检查输入的是否不符合数学意义上的数字
     */
    public static boolean checkTemprature(String value) {
        if (TextUtils.isEmpty(value)) {
            return true;
        } else {
            return testTemperature(value);
        }
    }

    /**
     * 判断是否是浮点数
     */
    public static boolean testTemperature(String temp) {
        Float tempFloat = 0f;
        String pattern = "[-]?[0-9]*\\.?[0-9]+";
        Pattern pt = Pattern.compile(pattern);
        try {
            tempFloat = new Float(temp);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        Matcher matcher = pt.matcher(String.valueOf(tempFloat));
        if (matcher.matches() || "0".equals(temp)) {
            return true;
        }
        return false;
    }


    public static boolean isHasOneEmpty(String... arg) {
        for (String s : arg) {
            if (TextUtils.isEmpty(s)) {
                return true;
            }
        }
        return false;
    }


}
