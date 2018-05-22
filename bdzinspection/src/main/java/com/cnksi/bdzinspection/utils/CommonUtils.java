package com.cnksi.bdzinspection.utils;

import android.text.TextUtils;
import android.util.Log;

import com.cnksi.common.Config;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.FileUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

    /**
     * 得到更换后的图片
     *
     * @param changePics 更换后的图片
     * @param defaultPic //默认图片
     */
    public static String getChangePicPath(String changePics, String defaultPic) {
        String[] changePicArray = TextUtils.isEmpty(changePics) ? new String[]{}
                : changePics.split(Config.COMMA_SEPARATOR);
        String changePicPath = "";
        if (changePicArray != null && changePicArray.length > 0) {
            if (FileUtils.isFileExists(Config.CUSTOMER_PICTURES_FOLDER + changePicArray[0])) {
                changePicPath = Config.CUSTOMER_PICTURES_FOLDER + changePicArray[0];
            } else if (FileUtils.isFileExists(Config.PICTURES_FOLDER + changePicArray[0])) {
                changePicPath = Config.PICTURES_FOLDER + changePicArray[0];
            } else {
                changePicPath = Config.PICTURES_FOLDER + (TextUtils.isEmpty(defaultPic) ? "part_pic.png" : defaultPic);
            }
        } else {
            if (FileUtils.isFileExists(
                    Config.PICTURES_FOLDER + (TextUtils.isEmpty(defaultPic) ? "part_pic.png" : defaultPic))) {
                changePicPath = Config.PICTURES_FOLDER + (TextUtils.isEmpty(defaultPic) ? "part_pic.png" : defaultPic);
            } else {
                changePicPath = Config.CUSTOMER_PICTURES_FOLDER
                        + (TextUtils.isEmpty(defaultPic) ? "part_pic.png" : defaultPic);
            }
        }
        return changePicPath;
    }

    /**
     * 得到图片路径
     *
     * @param changePics
     * @param defaultPic
     */
    public static String getDefaultPicPath(String changePics, String defaultPic) {
        String[] changePicArray = TextUtils.isEmpty(changePics) ? new String[]{}
                : changePics.split(Config.COMMA_SEPARATOR);
        String changePicPath = "";
        if (changePicArray != null && changePicArray.length > 0) {
            if (FileUtils.isFileExists(Config.CUSTOMER_PICTURES_FOLDER + changePicArray[0])) {
                changePicPath = Config.CUSTOMER_PICTURES_FOLDER + changePicArray[0];
            } else if (FileUtils.isFileExists(Config.PICTURES_FOLDER + changePicArray[0])) {
                changePicPath = Config.PICTURES_FOLDER + changePicArray[0];
            } else {
                changePicPath = Config.PICTURES_FOLDER + defaultPic;
            }
        } else {
            if (FileUtils.isFileExists(Config.PICTURES_FOLDER + defaultPic)) {
                changePicPath = Config.PICTURES_FOLDER + defaultPic;
            } else {
                changePicPath = Config.CUSTOMER_PICTURES_FOLDER + defaultPic;
            }
        }
        return changePicPath;
    }

    /**
     * 计算两个时间的差
     *
     * @param startTimeStr
     * @param endTimeStr
     */
    public static String getTimeDifference(String startTimeStr, String endTimeStr) {
        SimpleDateFormat formatter = new SimpleDateFormat(DateUtils.yyyy_MM_dd_HH_mm_ss, Locale.CHINA);
        String differenceTime = "";
        try {
            differenceTime = getTimeDifference(formatter.parse(startTimeStr), formatter.parse(endTimeStr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return differenceTime;
    }

    /**
     * 计算两个时间的差
     *
     * @param startTime
     * @param endTime
     */
    public static String getTimeDifference(Date startTime, Date endTime) {
        long l = endTime.getTime() - startTime.getTime();
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        StringBuilder sbTime = new StringBuilder();
        if (day > 0) {
            sbTime.append(day).append(":");
        }
        if (hour > 0) {
            if (hour < 10) {
                sbTime.append("0");
            }
            sbTime.append(hour).append(":");
        }
        if (min < 10) {
            sbTime.append("0");
        }
        sbTime.append(min).append(":");
        if (s < 10) {
            sbTime.append("0");
        }
        sbTime.append(s);
        return sbTime.toString();
    }

    /**
     * 判断是否显示抄录笔
     */
    public static boolean isShowCopyPen(String currentType) {
        return ("special_wind".equalsIgnoreCase(currentType) || "special_fog".equalsIgnoreCase(currentType)
                || "special_snow".equalsIgnoreCase(currentType) || "special_hyperthermia".equalsIgnoreCase(currentType)
                || "special_xideng".equalsIgnoreCase(currentType) || "special_nighttime".equalsIgnoreCase(currentType));
    }

//    /**
//     * 判断温度是否合法
//     */
//    public static boolean judgeTemperature(String temp) {
//        //匹配整数
//        Pattern pattern = Pattern.compile("^-?[1-9]\\d*$");
//        Matcher matcher = pattern.matcher(temp);
//        if (matcher.matches() || "0".equals(temp)) {
//            return true;
//        }
//        return false;
//    }

    /**
     * 判断温度是否合法
     */
    public static boolean judgeTemperature(String temp) {
        try {
            Float floatNum = new Float(temp);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getTransformTep(String temperature) {
        if (TextUtils.isEmpty(temperature)) return "";
        Double tempFloat = 0.0d;
        try {
            tempFloat = new Double(temperature);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
        if (tempFloat == 0.0f)
            return "0.0";

        if (!temperature.contains(".")) {
            return temperature;
        }
        Log.d("CommonUtils", tempFloat + "");
        return (float) (Math.round(tempFloat * 100)) / 100 + "";
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


    public static String buildWhereTaskContainMe(String[] accounts){
        if (accounts.length > 1) {
            return " ((','||create_account ||',') like ('%," + accounts[0]
                    + ",%') or (','||create_account ||',') like ('%," + accounts[1] + ",%')or (','||members_account ||',') like('%," + accounts[0]
                    + ",%')  or (','||members_account ||',') like ('%," + accounts[1] + ",%')) ";
        }
        if (accounts.length==1) {
            return " ((','||create_account ||',') like ( '%," + accounts[0] + ",%') or (','||members_account ||',') like (',%" + accounts[0] + ",%')) ";
        }else return " ";
    }
}