package com.cnksi.sjjc.util;

import android.text.TextUtils;

import com.cnksi.core.utils.CoreConfig;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.FunctionUtils;
import com.cnksi.sjjc.Config;

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
            if (card_id.equals(cardId))
                return true;
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
        if (tempFloat == 0.0f)
            return "0.0";
        if (!temperature.contains("."))
            return temperature;

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
        if (tempFloat == 0.0f)
            return "0.0";
        if (!temperature.contains("."))
            return temperature;

        for (int i = 1; i <= num; i++) {
            sum = sum * 10;
        }

        return String.valueOf((float) (Math.round(tempFloat * sum)) / sum);
    }

    /**
     * emoj表情判断
     */
    public static  boolean hasEmoji(String content) {

        Pattern pattern = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return true;
        }
        return false;
    }
}
