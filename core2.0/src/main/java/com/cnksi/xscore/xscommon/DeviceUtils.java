package com.cnksi.xscore.xscommon;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

public class DeviceUtils {

	/**
	 * 获得唯一的id 为DeviceId + AndroidID + SerialNumber + InstalltionId
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceUUID(Context context) {

		StringBuilder sbUUID = new StringBuilder();
		sbUUID.append(TextUtils.isEmpty(getDeviceId(context)) ? "" : getDeviceId(context));
		sbUUID.append("_");
		sbUUID.append(TextUtils.isEmpty(getAndroidId(context)) ? "" : getAndroidId(context));
		sbUUID.append("_");
		sbUUID.append(TextUtils.isEmpty(getSerialNumber(context)) ? "" : getSerialNumber(context));

		return sbUUID.toString();
	}

	public static DeviceInfor getPhoneInfor(Context context) {
		DeviceInfor deviceInfor = new DeviceInfor();
		// 应用的版本名称和版本号
		PackageManager pm = context.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
		} catch (NameNotFoundException e) {
		}

		deviceInfor.deviceid = getDeviceUUID(context).toUpperCase(Locale.CHINA);
		// 终端程序版本
		deviceInfor.ver = pi == null ? "" : pi.versionName;
		// android版本号
		deviceInfor.android = Build.VERSION.RELEASE + "_" + Build.VERSION.SDK_INT;
		// 手机制造商
		deviceInfor.factory = Build.MANUFACTURER + "_" + Build.PRODUCT;
		// 手机型号
		deviceInfor.model = Build.MODEL;
		// 手机定制商
		deviceInfor.brand = Build.BRAND;
		// 得到系统内存大小
		deviceInfor.memory = getTotalMemorys(context);

		String[] widthHeight = getWidthAndHeight(context);
		// 屏幕分辨率
		deviceInfor.resolution = (widthHeight == null || widthHeight.length < 2) ? "" : widthHeight[0];
		// 屏幕密度
		deviceInfor.density = (widthHeight == null || widthHeight.length < 2) ? "" : widthHeight[1];
		// 手机号码
		deviceInfor.phone = getPhoneNum(context);

		return deviceInfor;
	}

	public static String[] getWidthAndHeight(Context context) {
		String[] widthHeight = new String[2];
		// 获取手机屏幕高度
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels; // 宽
		int height = dm.heightPixels; // 高
		float density = dm.density; // 屏幕密度（0.75 / 1.0 / 1.5）
		widthHeight[0] = String.valueOf(width) + "*" + String.valueOf(height);
		widthHeight[1] = String.valueOf(density);
		return widthHeight;
	}

	/**
	 * 获取手机的内存大小
	 * 
	 * @return
	 */
	public static String getTotalMemorys(Context context) {

		long total_memory = 0l;
		String str1 = "/proc/meminfo";// 系统内存信息文件
		String str2;
		String[] arrayOfString;
		try {
			FileReader localFileReader = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			total_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;
			localBufferedReader.close();
		} catch (IOException e) {
		}
		return Formatter.formatFileSize(context, total_memory);
	}

	/**
	 * 获取设备的deviceId
	 * 
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getDeviceId();
	}

	/**
	 * 获取手机号码
	 * 
	 * @param context
	 * @return
	 */
	public static String getPhoneNum(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getLine1Number();
	}

	/**
	 * 获取AndroidId
	 * 
	 * @param context
	 * @return
	 */
	public static String getAndroidId(Context context) {
		return Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID).toUpperCase(Locale.CHINA);
	}

	/**
	 * 得到Serial Number android版本2.3及以上
	 * 
	 * @param context
	 * @return
	 */
	public static String getSerialNumber(Context context) {
		return Build.SERIAL;
	}

	/**
	 * 判断当前手机是否有ROOT权限
	 * 
	 * @return
	 */
	public static boolean isRoot() {
		boolean bool = false;
		try {
			if ((!new File("/system/bin/su").exists()) && (!new File("/system/xbin/su").exists())) {
				bool = false;
			} else {
				bool = true;
			}
		} catch (Exception e) {
		}
		return bool;
	}
}
