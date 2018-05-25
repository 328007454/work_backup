package com.cnksi.common.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

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
        if (s == null) {
            return 0;
        }
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

    /**
     * 四舍五入
     *
     * @param number
     * @return
     */
    public static int convertFloatToInt(float number) {
        return Math.round(number);
    }

    /**
     * 将数字转换成指定格式字符串
     *
     * @param number
     * @param pattern
     * @return
     */
    public static String formatNumber(Number number, String pattern) {
        return new DecimalFormat(pattern).format(number);
    }

    /**
     * 使用java正则表达式去掉多余的.与0
     *
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");// 去掉多余的0
            s = s.replaceAll("[.]$", "");// 如最后一位是.则去掉
        }
        return s;
    }

    /**
     * 将数字转换成指定格式数字
     *
     * @param number
     * @param pattern
     * @return
     */
    public static Number toNumber(Number number, String pattern) {
        try {
            return new DecimalFormat().parse(formatNumber(number, pattern));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 计算当前SHA1的只
     *
     * @param context
     * @return
     */
    public static String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_SIGNATURES);

            byte[] cert = info.signatures[0].toByteArray();

            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i]).toUpperCase(Locale.US);
                if (appendString.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(appendString);
                hexString.append(":");
            }
            return hexString.toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static double parse(String s, double def) {
        if (TextUtils.isEmpty(s)) {
            return def;
        }
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            return def;
        }
    }
    public  static int parse(String s, int def) {
        if (TextUtils.isEmpty(s)) {
            return def;
        }
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }
    public static float parse(String s, float def) {
        if (TextUtils.isEmpty(s)) {
            return def;
        }
        try {
            return Float.parseFloat(s);
        } catch (Exception e) {
            return def;
        }
    }

}
