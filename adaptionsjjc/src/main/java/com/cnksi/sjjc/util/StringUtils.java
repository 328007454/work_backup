package com.cnksi.sjjc.util;

import android.text.TextUtils;

import com.cnksi.core.utils.DateUtils;
import com.cnksi.common.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符工具处理类
 *
 * @author pantao
 */
public class StringUtils {
    /**
     * cardid存在于cardIds中
     *
     * @param cardId
     * @param cardIds
     * @return
     */
    public static boolean existIn(String cardId, String cardIds) {
        String card_ids[] = cardIds.split(",");
        for (String card_id : card_ids) {
            if (card_id.equals(cardId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从指定字符串中移除指定字符
     *
     * @param cardId
     * @param cardIds
     * @return
     */
    public static String removeFrom(String cardId, String cardIds) {

        cardIds = "," + cardIds + ","; // 首尾加逗号: 1,2,3 -> ,1,2,3,
        cardId = "," + cardId + ","; // 首尾加逗号: 2 -> ,2,
        cardIds = cardIds.replace(cardId, ","); // 替换已经存在的值 : ,1,2,3, -> ,1,3,
        if (cardIds.length() > 1) {
            cardIds = cardIds.substring(1, cardIds.length() - 1); // 去掉首尾的逗号 : ,1,3, -> 1,3
        } else {
            cardIds = "";
        }
        return cardIds;
    }

    /**
     * 返回以计划Id为前缀的图片名称
     *
     * @param plan_id
     * @return
     */
    public static String getCurrentImageName(String plan_id) {
        if (!TextUtils.isEmpty(plan_id)) {
            plan_id = plan_id + "_";
        } else {
            plan_id = "";
        }
        return plan_id + FunctionUtils.getCurrentImageName();
    }

    /**
     * 返回以计划Id为前缀的录音名称
     *
     * @param plan_id
     * @return
     */
    public static String getCurrentAudioName(String plan_id) {
        if (!TextUtils.isEmpty(plan_id)) {
            plan_id = plan_id + "_";
        } else {
            plan_id = "";
        }
        return plan_id + FunctionUtils.getPrimarykey() + Config.AAC_POSTFIX;
    }

    /**
     * 返回以计划Id为前缀的视频名称
     *
     * @param plan_id
     * @return
     */
    public static String getCurrentVideoName(String plan_id) {
        if (!TextUtils.isEmpty(plan_id)) {
            plan_id = plan_id + "_";
        } else {
            plan_id = "";
        }
        String uuidStr = UUID.randomUUID().toString().replace(CoreConfig.DASH_SEPARATOR, "");
        if (uuidStr.length() > 8) {
            uuidStr = uuidStr.substring(0, 8);
        }
        return plan_id + DateUtils.getCurrentTime(CoreConfig.dateFormat6) + uuidStr + Config.VIDEO_TYPE_MP4;
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
        if (tempFloat == 0.0f) {
            return "0.0";
        }
        if (!temperature.contains(".")) {
            return temperature;
        }

        return String.valueOf((float) (Math.round(tempFloat * 100)) / 100);
    }

    /**
     * 获取三位小数点
     */
    public static String getTransformTep(String temperature, int num) {
        Double tempFloat = 0.0d;
        int sum = 1;
        try {
            tempFloat = new Double(temperature);
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
     * emoj表情判断
     */
    public static boolean hasEmoji(String content) {
        String str= "[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]";
        Pattern pattern = Pattern.compile(str);
        Matcher matcher = pattern.matcher(content);
        return matcher.find();
    }

    public static String BlankToDefault(String str, String... defaultValue) {
        String rs = str;
        if (TextUtils.isEmpty(str)) {
            for (String s : defaultValue) {
                if (!TextUtils.isEmpty(s)) {
                    return s;
                }
            }
            if (defaultValue.length > 0) {
                return defaultValue[defaultValue.length - 1];
            } else {
                return str;
            }
        } else {
            return rs;
        }
    }

    /**
     * 将List<String>转换成String
     *
     * @param strList
     * @return
     */
    public static String ArrayListToString(List<String> strList) {
        return ArrayListToString(strList, "", "");
    }

    /**
     * 将List<String>转换成String
     *
     * @param strList
     * @return
     */
    public static String ArrayListToString(List<String> strList, String replaceTarget) {
        return ArrayListToString(strList, replaceTarget, "");
    }

    /**
     * 将List<String>转换成String
     *
     * @param strList
     * @param target      需要替换的字符串
     * @param replacement 替换后的字符串
     * @return
     */
    public static String ArrayListToString(List<String> strList, CharSequence target, CharSequence replacement) {
        StringBuffer sb = new StringBuffer("");
        if (strList != null && !strList.isEmpty()) {
            for (int i = 0, count = strList.size(); i < count; i++) {
                String content = strList.get(i);
                if (!TextUtils.isEmpty(target) || !TextUtils.isEmpty(replacement)) {
                    content = content.replace(target, replacement);
                }
                sb.append(content);
                if (i != count - 1) {
                    sb.append(CoreConfig.COMMA_SEPARATOR);
                }
            }
        }
        return sb.toString();
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

    public static String NullToBlank(String str) {
        return BlankToDefault(str, "");
    }

    public static boolean isHasOneEmpty(String... arg) {
        for (String s : arg) {
            if (isEmpty(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(CharSequence str) {
        return (str == null || str.length() == 0);
    }

    /**
     * 在List<String> 集合中的每个item 前面添加 字符串
     *
     * @param strList   源集合 a.jpg,b.jpg
     * @param addTarget 添加的字符串 123
     */
    public static ArrayList<String> addStrToListItem(List<String> strList, String addTarget) {
        return addStrToListItem(strList, addTarget, true);
    }

    /**
     * 在List<String> 集合中的每个item 添加 字符串
     *
     * @param strList   源集合 a.jpg,b.jpg
     * @param addTarget 添加的字符串 123
     * @param isFront   是否在前面添加
     * @return 123a.jpg, 123b.jpg;
     */
    public static ArrayList<String> addStrToListItem(List<String> strList, String addTarget, boolean isFront) {
        ArrayList<String> targetList = new ArrayList<String>();
        if (strList != null && !strList.isEmpty()) {
            for (int i = 0, count = strList.size(); i < count; i++) {
                if (isFront) {
                    targetList.add(addTarget + strList.get(i));
                } else {
                    targetList.add(strList.get(i) + addTarget);
                }
            }
        }
        return targetList;
    }

}
