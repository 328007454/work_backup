package com.cnksi.bdzinspection.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import android.content.Context;

import com.cnksi.xscore.xsutils.CoreConfig;
import com.cnksi.xscore.xsutils.FunctionUtils;
import com.cnksi.xscore.xsutils.PreferencesUtils;

/**
 *
 *
 * @author lyndon
 *
 */
public class FunctionUtil extends FunctionUtils {

	/**
	 * 以系统时间返回照相图片文件名称
	 *
	 * @return yyyyMMddHHmmssSSSadfdfdsfwei.jpg
	 */
	public static String getCurrentImageName(Context context) {
		String prefix = PreferencesUtils.getString(context, Config.PICTURE_PREFIX, "");
		SimpleDateFormat formatter = new SimpleDateFormat(CoreConfig.dateFormat6, Locale.CHINA);
		String uuidStr = UUID.randomUUID().toString().replace(CoreConfig.DASH_SEPARATOR, "");
		if (uuidStr.length() > 8) {
			uuidStr = uuidStr.substring(0, 8);
		}
		String imageName = String.valueOf(formatter.format(new Date()) + uuidStr + CoreConfig.IMAGE_JPG_POSTFIX);
		return prefix + imageName;

	}

	public static String getSignImageName(Context context, String name) {
		String prefix = PreferencesUtils.getString(context, Config.PICTURE_PREFIX, "");
		return prefix + "sign" + System.currentTimeMillis() + CoreConfig.IMAGE_PNG_POSTFIX;
	}

	public static String getSignImageHead(Context context, String name) {
		String prefix = PreferencesUtils.getString(context, Config.PICTURE_PREFIX, "");
		return prefix + "img" + System.currentTimeMillis() + CoreConfig.IMAGE_JPG_POSTFIX;
	}
}
