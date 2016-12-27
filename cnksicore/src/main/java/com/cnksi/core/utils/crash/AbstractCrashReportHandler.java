/*
 * @(#)AbstractReportHandler.java		       Project: crash
 * Date:2014-5-27
 *
 * Copyright (c) 2014 CFuture09, Institute of Software, 
 * Guangdong Ocean University, Zhanjiang, GuangDong, China.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cnksi.core.utils.crash;

import java.io.File;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.cnksi.core.utils.CLog;
import com.cnksi.core.utils.CoreConfig;

/**
 * 抽象的日志报告类
 * 
 */
public abstract class AbstractCrashReportHandler implements CrashListener {
	private static Context mContext;

	private String mLogName;

	public AbstractCrashReportHandler(Context context) {
		this(context, "crashlog" + CoreConfig.TXT_POSTFIX);
	}

	/**
	 * 构造方法
	 * 
	 * @param context
	 * @param logName
	 *            保存日志的文件名
	 */
	public AbstractCrashReportHandler(Context context, String logName) {
		mContext = context;
		mLogName = logName;
		CrashHandler handler = CrashHandler.getInstance();
		final File file = getLogFile(context);
		handler.init(file, this);
	}

	public void start() {
		Thread.setDefaultUncaughtExceptionHandler(CrashHandler.getInstance());
		final File file = getLogFile(mContext);
		if (file.length() > 10) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					sendReport(buildTitle(mContext), buildBody(mContext), file,false);
				}
			}).start();
		}
	}

	protected File getLogFile(Context context) {
		return new File(context.getFilesDir(), mLogName);
	}

	/**
	 * 发送报告
	 * 
	 * @param title
	 *            报告标题
	 * @param body
	 *            报告正文，为设备信息及安装包的版本信息
	 * @param file
	 *            崩溃日志
	 */
	protected abstract void sendReport(String title, String body, File file,boolean isAfterSaveCrash);

	@Override
	public void afterSaveCrash(File file) {
		CLog.d("afterSaveCrash");
		sendReport(buildTitle(mContext), buildBody(mContext), file,true);
	}

	/**
	 * 构建标题
	 * 
	 * @param context
	 * @return
	 */
	public static String buildTitle(Context context) {
		return "Crash Log: " + context.getPackageManager().getApplicationLabel(context.getApplicationInfo());
	}

	/**
	 * 构建正文
	 * 
	 * @param context
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String buildBody(Context context) {
		StringBuilder sb = new StringBuilder();
		sb.append("--------------APPLICATION INFORMATION------------").append('\n');
		PackageManager pm = context.getPackageManager();
		ApplicationInfo ai = context.getApplicationInfo();
		sb.append("Application : ").append(pm.getApplicationLabel(ai)).append('\n');
		try {
			PackageInfo pi = pm.getPackageInfo(ai.packageName, 0);
			sb.append("Version Code: ").append(pi.versionCode).append('\n');
			sb.append("Version Name: ").append(pi.versionName).append('\n');
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		sb.append("--------------DEVICE INFORMATION-----------------").append('\n');
		sb.append("Board: ").append(Build.BOARD).append('\n');
		sb.append("BOOTLOADER: ").append(Build.BOOTLOADER).append('\n');
		sb.append("BRAND: ").append(Build.BRAND).append('\n');
		sb.append("CPU_ABI: ").append(Build.CPU_ABI).append('\n');
		sb.append("CPU_ABI2: ").append(Build.CPU_ABI2).append('\n');
		sb.append("DEVICE: ").append(Build.DEVICE).append('\n');
		sb.append("DISPLAY: ").append(Build.DISPLAY).append('\n');
		sb.append("FINGERPRINT: ").append(Build.FINGERPRINT).append('\n');
		sb.append("HARDWARE: ").append(Build.HARDWARE).append('\n');
		sb.append("HOST: ").append(Build.HOST).append('\n');
		sb.append("ID: ").append(Build.ID).append('\n');
		sb.append("MANUFACTURER: ").append(Build.MANUFACTURER).append('\n');
		sb.append("PRODUCT: ").append(Build.PRODUCT).append('\n');
		sb.append("TAGS: ").append(Build.TAGS).append('\n');
		sb.append("TYPE: ").append(Build.TYPE).append('\n');
		sb.append("USER: ").append(Build.USER).append('\n');
		return sb.toString();
	}

	/**
	 * 构建设备信息
	 * 
	 * @return
	 */
	public static String buildBody() {
		return buildBody(mContext);
	}
}
