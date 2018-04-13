package com.cnksi.xscore.xsutils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.support.v4.content.PermissionChecker;
import android.text.TextUtils;

import com.cnksi.xscore.R;
import com.cnksi.xscore.xscommon.DeviceInfor;
import com.cnksi.xscore.xscommon.DeviceUtils;
import com.cnksi.xscore.xsview.CustomerDialog;
import com.cnksi.xscore.xsview.CustomerDialog.DialogClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UpdateUtils {

	private static Dialog mDialog = null;

	/**
	 * 安装APK
	 * 
	 * @param context
	 * @param updateApkFile
	 */
	public static void installNewApk(Context context, File updateApkFile) {
		if (updateApkFile != null && updateApkFile.exists()) {
			Intent intent = new Intent();
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setAction(Intent.ACTION_VIEW);
			Uri uri = Uri.fromFile(updateApkFile);
			intent.setDataAndType(uri, "application/vnd.android.package-archive");
			context.startActivity(intent);
		}
	}

	/**
	 * 检测是否有新的APK
	 * 
	 * @param context
	 * @param appFloder
	 * @return
	 */
	public static File hasUpdateApk(Context context, String appFloder) {
		return hasUpdateApk(context, null, appFloder);
	}

	/**
	 * 检测是否有新的APK
	 * 
	 * @param context
	 * @param file
	 * @param appFloder
	 *            apk包的路径
	 * @return
	 */
	public static File hasUpdateApk(Context context, File file, String appFloder) {
		if (file == null) {
			PackageInfo localPackageInfo = AppUtils.getLocalPackageInfo(context);
			file = getTheNewApkFile(context, appFloder, localPackageInfo == null ? "" : localPackageInfo.packageName);
		}
		if (file != null && file.exists()) {
			PackageInfo remotePackageInfo = AppUtils.getAPKPackageInfo(context, file);
			PackageInfo localPackageInfo = AppUtils.getLocalPackageInfo(context);
			int remoteVersionCode = 0;
			int localVersionCode = 0;
			if (remotePackageInfo != null && localPackageInfo != null) {
				remoteVersionCode = remotePackageInfo.versionCode;
				localVersionCode = localPackageInfo.versionCode;
			}
			if (remoteVersionCode > localVersionCode) {
				return file;
			} else {
				return null;
			}
		}
		return file;
	}

	/**
	 * 得到最新的升级apk包
	 * 
	 * @param context
	 * @param path
	 * @return
	 */
	public static File getTheNewApkFile(Context context, String path, String packageName) {
		File newApkFile = null;
		PackageInfo newAPkPackageInfo = null;
		List<File> apkFileList = new ArrayList<File>();
		if (!TextUtils.isEmpty(path)) {
			File file = new File(path);
			if (file != null && file.exists() && file.isDirectory()) {
				File[] children = file.listFiles();
				if (children != null) {
					for (File f : children) {
						if (f != null && f.exists() && f.isFile() && f.getName().toLowerCase(Locale.US).endsWith(CoreConfig.APK_POSTFIX)) {
							apkFileList.add(f);
						}
					}
				}
			}
			// 找到升级版本中包名和本应用包名相同的最新的APK文件
			if (!apkFileList.isEmpty()) {
				ArrayList<File> realApkFileList = new ArrayList<File>();
				// 找到和本应用包名相同的APK文件
				for (File apkFile : apkFileList) {
					PackageInfo packageInfor = AppUtils.getAPKPackageInfo(context, apkFile);
					if (packageInfor != null && packageName.equalsIgnoreCase(packageInfor.packageName)) {
						realApkFileList.add(apkFile);
					}
				}
				// 从找到的APK文件中找到最新的APK文件
				if (!realApkFileList.isEmpty()) {
					newApkFile = realApkFileList.get(0);
					newAPkPackageInfo = AppUtils.getAPKPackageInfo(context, newApkFile);
					for (File fileTemp : realApkFileList) {
						PackageInfo tempAPkPackageInfo = AppUtils.getAPKPackageInfo(context, fileTemp);
						if (tempAPkPackageInfo != null && tempAPkPackageInfo.versionCode > newAPkPackageInfo.versionCode) {
							newAPkPackageInfo = tempAPkPackageInfo;
							newApkFile = fileTemp;
						}
					}
				}
			}
		}
		apkFileList.clear();
		apkFileList = null;
		return newApkFile;
	}


	/**
	 * 新版本已下载 提示安装dialog
	 * 
	 * @param file
	 */
	public static Dialog showInstallNewApkDialog(final Activity mContext, final File file) {
		Dialog mDialog = CustomerDialog.showSelectDialog(mContext, R.string.xs_find_newversion_str, new DialogClickListener() {
			@Override
			public void confirm() {
				UpdateUtils.installNewApk(mContext, file);
			}

			@Override
			public void cancel() {

			}
		}, R.string.xs_install_now_str, R.string.xs_cancel_install_str);
		return mDialog;
	}


	/**
	 * 得到设备的信息
	 * 
	 * @param mContext
	 * @param appCode
	 * @return
	 */
	public static Map<String, String> getDeviceInforMapParams(Context mContext, String appCode) {
		HashMap<String, String> params = new HashMap<String, String>();
		if (PermissionChecker.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) == PermissionChecker.PERMISSION_GRANTED) {
			DeviceInfor deviceInfor = DeviceUtils.getPhoneInfor(mContext);
			params.put(DeviceInfor.PID, TextUtils.isEmpty(appCode) ? "" : appCode);
			params.put(DeviceInfor.ANDROID, TextUtils.isEmpty(deviceInfor.android) ? "" : deviceInfor.android);
			params.put(DeviceInfor.BRAND, TextUtils.isEmpty(deviceInfor.brand) ? "" : deviceInfor.brand);
			params.put(DeviceInfor.DENSITY, TextUtils.isEmpty(deviceInfor.density) ? "" : deviceInfor.density);
			params.put(DeviceInfor.DEVICE_ID, TextUtils.isEmpty(deviceInfor.deviceid) ? "" : deviceInfor.deviceid);
			params.put(DeviceInfor.FACTORY, TextUtils.isEmpty(deviceInfor.factory) ? "" : deviceInfor.factory);
			params.put(DeviceInfor.LASTVIST, TextUtils.isEmpty(deviceInfor.lastvist) ? "" : deviceInfor.lastvist);
			params.put(DeviceInfor.MEMORY, TextUtils.isEmpty(deviceInfor.memory) ? "" : deviceInfor.memory);
			params.put(DeviceInfor.MODEL, TextUtils.isEmpty(deviceInfor.model) ? "" : deviceInfor.model);
			params.put(DeviceInfor.PHONE_NUM, TextUtils.isEmpty(deviceInfor.phone) ? "" : deviceInfor.phone);
			params.put(DeviceInfor.RESOLUTION, TextUtils.isEmpty(deviceInfor.resolution) ? "" : deviceInfor.resolution);
			params.put(DeviceInfor.VER, TextUtils.isEmpty(deviceInfor.ver) ? "" : deviceInfor.ver);
		}
		return params;
	}
}
