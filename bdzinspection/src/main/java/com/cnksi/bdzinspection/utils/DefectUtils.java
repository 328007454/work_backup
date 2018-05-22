package com.cnksi.bdzinspection.utils;

import android.graphics.Color;
import android.text.TextUtils;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.model.CopyItem;
import com.cnksi.bdzinspection.model.CopyResult;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.xscore.xsutils.DateUtils;
import com.cnksi.xscore.xsutils.StringUtils;

import java.util.List;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/1/22 10:08
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class DefectUtils {

    public static final int CRISIS_COLOR = XunshiApplication.getAppContext().getResources().getColor(R.color.xs_red_color);
    public static final int SERIOUS_COLOR = XunshiApplication.getAppContext().getResources().getColor(R.color.xs_orange_color);
    public static final int COMMON_COLOR = XunshiApplication.getAppContext().getResources().getColor(R.color.xs_yellow_color);

    /**
     * 转换缺陷等级为 一般 严重 危机
     *
     * @param item
     * @return
     */
    public static CharSequence convert2DefectDesc(DefectRecord item) {
        CharSequence result;
        if (Config.GENERAL_LEVEL_CODE.equalsIgnoreCase(item.defectlevel)) {
            result = StringUtils.changePartTextColor("[" + Config.GENERAL_LEVEL + "]" + item.description, COMMON_COLOR, 0, 6);
        } else if (Config.SERIOUS_LEVEL_CODE.equalsIgnoreCase(item.defectlevel)) {
            result = StringUtils.changePartTextColor("[" + Config.SERIOUS_LEVEL + "]" + item.description, SERIOUS_COLOR, 0, 6);
        } else {
            result = StringUtils.changePartTextColor("[" + Config.CRISIS_LEVEL + "]" + item.description, CRISIS_COLOR, 0, 6);
        }
        return result;
    }

    /**
     * 转换缺陷等级的右上角角标
     *
     * @param item
     * @return
     */
    public static int convert2ConnerMark(String item) {
        if (Config.CRISIS_LEVEL_CODE.equalsIgnoreCase(StringUtils.cleanString(item))) {
            return R.drawable.xs_ic_crisis_defect;
        } else if (Config.SERIOUS_LEVEL_CODE.equalsIgnoreCase(StringUtils.cleanString(item))) {
            return R.drawable.xs_ic_serious_defect;
        } else {
            return R.drawable.xs_ic_general_defect;
        }
    }


    public static int getDefectColor(String defectLevel) {
        if (Config.CRISIS_LEVEL.equalsIgnoreCase(defectLevel) || Config.CRISIS_LEVEL_CODE.equals(defectLevel)) {
            return CRISIS_COLOR;
        } else if (Config.SERIOUS_LEVEL.equalsIgnoreCase(defectLevel) || Config.SERIOUS_LEVEL.equals(defectLevel)) {
            return SERIOUS_COLOR;
        } else {
            return COMMON_COLOR;
        }
    }

    public static CharSequence calculateRemindTime(DefectRecord defectRecord) {
        if (defectRecord == null || TextUtils.isEmpty(defectRecord.discovered_date)) return "";
        int day;
        if (Config.CRISIS_LEVEL_CODE.equalsIgnoreCase(defectRecord.defectlevel) || Config.CRISIS_LEVEL.equals(defectRecord.defectlevel)) {
            day = 1;
        } else if (Config.SERIOUS_LEVEL_CODE.equalsIgnoreCase(defectRecord.defectlevel) || Config.SERIOUS_LEVEL.equals(defectRecord.defectlevel)) {
            day = 30;
        } else {
            day = 365;
        }
        String d=DateUtils.getAfterTime(defectRecord.discovered_date, day, "yyyy-MM-dd");
        String s ="到期时间："+ d;
        if (d.compareTo(DateUtils.getAfterTime(3)) > 0) {
            return s;
        } else return StringUtils.changeTextColor(s, Color.RED);
    }

    public static boolean calcCopyBound(CopyItem item, CopyResult copyResult, String val, List<DefectRecord> mExistDefectList, List<String> result) {
        boolean isDZCS = item.type_key.equals("blqdzcs_dzcs");
        if (!isDZCS && (TextUtils.isEmpty(item.max) && TextUtils.isEmpty(item.min))) {//判断抄录上下限值是否为空
            return false;
        }
        double min, max;
        if (isDZCS) {
            if (copyResult == null) return false;
            min = NumberUtil.parse(copyResult.val_old, -1);
            if (min == -1) return false;
            max = min + 5;
        } else {
            max = NumberUtil.parse(item.max, 99999d);
            min = NumberUtil.parse(item.min, -99999d);
        }

        double currentValue = Double.parseDouble(val);
        String defectContent;
        String descript = item.description;
        if (isDZCS) {
            if (currentValue > max) {
                defectContent = descript + "超过上一次记录值（" + min + "）" + String.valueOf(Math.abs(currentValue - max)) + "次";
            } else if (currentValue < min) {
                defectContent = descript + "低于上一次记录值（" + min + "）" + String.valueOf(Math.abs(currentValue - min)) + "次";
            } else return false;
        } else {
            if (currentValue >= max) {
                defectContent = descript + "大于等于预设最大值" + String.valueOf(Math.abs(currentValue - max) * 100 / max) + "%";
            } else if (currentValue <= min) {
                defectContent = descript + "小于等于预设最小值" + String.valueOf(Math.abs(currentValue - min) * 100 / min) + "%";
            } else {
                return false;
            }
        }
        boolean isContained = false;
        for (DefectRecord defectRecord : mExistDefectList) {
            if (!TextUtils.isEmpty(defectRecord.remark) && defectRecord.remark.contains(descript)) {
                isContained = true;
                break;
            }
        }
        if (!TextUtils.isEmpty(defectContent) && !isContained) {
            String tips;
            if (isDZCS) {
                if (currentValue > max) {
                    tips = descript + "的值大于上次抄录值（" + min + "次）五次,是否记录缺陷！";
                } else {
                    tips = descript + "的值小于上次抄录值(" + min + ")次,是否记录缺陷！";
                }
            } else {
                if (currentValue >= max) {
                    tips = String.format(XunshiApplication.getAppContext().getResources().getString(R.string.xs_copy_max_tips), descript, max + "");
                } else {
                    tips = String.format(XunshiApplication.getAppContext().getResources().getString(R.string.xs_copy_min_tips), descript, min + "");
                }
            }
            result.clear();
            result.add(defectContent);
            result.add(tips);
            return true;
        } else {
            return false;
        }
    }
}
