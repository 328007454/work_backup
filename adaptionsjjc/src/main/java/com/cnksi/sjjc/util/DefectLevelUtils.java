package com.cnksi.sjjc.util;

import android.content.Context;

import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;

/**
 * 判定缺陷等级的工具类
 * 
 * @author Oliver
 * 
 */
public class DefectLevelUtils {

	/**
	 * 根据抄录的数据得到当前的缺陷等级
	 * 
	 * @param theBig
	 *            最大值
	 * @param theLittle
	 *            最小值
	 * @param inputValue
	 *            抄录值
	 * @param isGreaterThan
	 *            是否大于最大值
	 * @return
	 * 
	 *         例如，正常数据范围为0.6-0.65 设抄录值为X，缺陷判定值为Y
	 * 
	 *         当X＜0.6（即小于最小值时） Y=0.6/X 1＜Y≤1.2 一般缺陷 1.2＜Y≤1.4 严重缺陷 1.4＜Y或Y=0 危急缺陷
	 * 
	 *         当X＞0.65（即大于最大值时） Y=X/0.65 1＜Y≤1.2 一般缺陷 1.2＜Y≤1.4 严重缺陷 1.4＜Y或Y=0 危急缺陷
	 */
	public static String getDefectLevel(float theBig, float theLittle, float inputValue, boolean isGreaterThan) {
		String defectLevel = Config.GENERAL_LEVEL;
		float value = 0.0f;
		if (isGreaterThan) {
			value = inputValue / theBig;
		} else {
			value = theLittle / inputValue;
		}
		if (1 < value && value <= 1.2f) {
			defectLevel = Config.GENERAL_LEVEL;
		} else if (1.2 < value && value <= 1.4) {
			defectLevel = Config.SERIOUS_LEVEL;
		} else if (1.4 < value || value == 0) {
			defectLevel = Config.CRISIS_LEVEL;
		}
		return defectLevel;
	}

	/**
	 * 转换缺陷等级为 2 4 6
	 * 
	 * @param defectLevel
	 * @return
	 */
	public static String convertDefectLevel2Code(String defectLevel) {
		if (Config.GENERAL_LEVEL.equalsIgnoreCase(defectLevel)) {
			defectLevel = Config.GENERAL_LEVEL_CODE;
		} else if (Config.SERIOUS_LEVEL.equalsIgnoreCase(defectLevel)) {
			defectLevel = Config.SERIOUS_LEVEL_CODE;
		} else {
			defectLevel = Config.CRISIS_LEVEL_CODE;
		}
		return defectLevel;
	}

	/**
	 * 转换缺陷等级为 一般 严重 危机
	 * 
	 * @param defectLevel
	 * @return
	 */
	public static String convert2DefectLevel(String defectLevel) {
		if (Config.GENERAL_LEVEL_CODE.equalsIgnoreCase(defectLevel)) {
			defectLevel = Config.GENERAL_LEVEL;
		} else if (Config.SERIOUS_LEVEL_CODE.equalsIgnoreCase(defectLevel)) {
			defectLevel = Config.SERIOUS_LEVEL;
		} else {
			defectLevel = Config.CRISIS_LEVEL;
		}
		return defectLevel;
	}
	public  static int getDefectColor(Context context,String defectLevel)
	{
		int color;
		if (Config.GENERAL_LEVEL_CODE.equalsIgnoreCase(defectLevel)) {
			color =context.getResources().getColor(R.color.level_one);
		} else if (Config.SERIOUS_LEVEL_CODE.equalsIgnoreCase(defectLevel)) {
			color =context.getResources().getColor(R.color.level_two);
		} else {
			color =context.getResources().getColor(R.color.level_three);
		}
		return  color;
	}

}
