package com.cnksi.common.utils;

/**
 * Created by wastrel on 2016/8/9.
 * if String is null,then is zero;
 */
public class CalcUtils {

    public static String add(String... args) {
        float total = 0;
        for (String s : args) {
            total += String2Float(s);
        }
        return total + "";
    }

    public static float String2Float(String s) {
        if (s == null)
            return 0;
        try {
            return Float.valueOf(s);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @param s1
     * @param s2
     * @return s1-s2
     */
    public static String sub(String s1, String s2) {
        return (String2Float(s1) - String2Float(s2)) + "";
    }

    public static int toInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

}
