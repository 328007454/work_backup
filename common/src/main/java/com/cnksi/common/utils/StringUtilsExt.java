package com.cnksi.common.utils;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.cnksi.core.utils.StringUtils.isEmpty;

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
            if (isEmpty(string)) {
                return true;
            }
        }
        return false;
    }

    static Pattern pattern = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]");

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
    public static String getTransformTep(String temperature) {
        Double tempFloat = 0.0d;
        try {
            tempFloat = new Double(temperature);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
        if (tempFloat == 0.0f)
            return "0.0";
        if (!temperature.contains("."))
            return temperature;

        return String.valueOf((float) (Math.round(tempFloat * 100)) / 100);
    }
}
