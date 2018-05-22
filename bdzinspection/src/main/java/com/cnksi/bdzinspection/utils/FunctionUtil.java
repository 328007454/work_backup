package com.cnksi.bdzinspection.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.cnksi.xscore.xsutils.CoreConfig;
import com.cnksi.xscore.xsutils.FileUtils;
import com.cnksi.xscore.xsutils.PreferencesUtils;

/**
 *
 *
 * @author lyndon
 *
 */
public class FunctionUtil{

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



	/**
	 * 以系统时间返回照相图片文件名称
	 *
	 * @return yyyyMMddHHmmssSSSadfdfdsfwei.jpg
	 */
	public static String getCurrentImageName() {
		SimpleDateFormat formatter = new SimpleDateFormat(CoreConfig.dateFormat6, Locale.CHINA);
		String uuidStr = UUID.randomUUID().toString().replace(CoreConfig.DASH_SEPARATOR, "");
		if (uuidStr.length() > 8) {
			uuidStr = uuidStr.substring(0, 8);
		}
		return String.valueOf(formatter.format(new Date()) + uuidStr + CoreConfig.IMAGE_JPG_POSTFIX);
	}


	/**
	 * 启动系统照相功能，按指定的文件名称存储到系统图片路径下
	 *
	 * @param activity         启动activity
	 * @param currentImageName 当前照片文件名
	 */
	public static void takePicture(Activity activity, String currentImageName, String defectImagePath) {
		takePicture(activity, currentImageName, defectImagePath, com.cnksi.common.Config.ACTION_IMAGE);
	}

	/**
	 * 启动系统照相功能，按指定的文件名称存储到系统图片路径下
	 *
	 * @param activity         启动activity
	 * @param currentImageName 当前照片文件名
	 */
	public static void takePicture(Activity activity, String currentImageName, String imageFolder, int requestCode) {
		if (!FileUtils.isFolderExists(imageFolder)) {
			FileUtils.makeDirectory(imageFolder);
		}
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		currentImageName = (null == currentImageName) ? getCurrentImageName() : currentImageName;
		File imageFile = new File(imageFolder, currentImageName);
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, imageFile.getAbsolutePath());
		values.put("_data", imageFile.getAbsolutePath());
		values.put(MediaStore.Images.Media.PICASA_ID, imageFile.getAbsolutePath());
		values.put(MediaStore.Images.Media.DISPLAY_NAME, imageFile.getAbsolutePath());
		values.put(MediaStore.Images.Media.DESCRIPTION, imageFile.getAbsolutePath());
		values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, imageFile.getAbsolutePath());
		Uri photoUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
		activity.startActivityForResult(intent, requestCode);
	}



	/**
	 * 得到以时间戳的字符串
	 *
	 * @return String HHmmssSSS+UUID
	 */
	public static String getPrimarykey() {
		SimpleDateFormat formatter = new SimpleDateFormat(CoreConfig.dateFormat10, Locale.CHINA);
		return formatter.format(new Date()) + UUID.randomUUID().toString().replace(CoreConfig.DASH_SEPARATOR, "");
	}
}
