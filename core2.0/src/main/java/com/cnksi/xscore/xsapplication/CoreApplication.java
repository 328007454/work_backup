package com.cnksi.xscore.xsapplication;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.cnksi.xscore.xsutils.CoreConfig;
import com.cnksi.xscore.xsutils.FileUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoreApplication extends Application {
	protected static SharedPreferences preference;
	// 线程池
	private static ExecutorService mFixedThreadPoolExecutor = null;
	private static Context mContext;

	public void onCreate() {
		super.onCreate();
		mContext = this.getApplicationContext();
		preference = this.getSharedPreferences(CoreConfig.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		int size = Runtime.getRuntime().availableProcessors();
		mFixedThreadPoolExecutor = Executors.newFixedThreadPool(size > 2 ? 3 : size);
	}

	protected void initFileSystem(final String[] filePathArray) {
		// 创建文件夹
		FileUtils.initFile(filePathArray);
	}

	public static ExecutorService getFixedThreadPoolExecutor() {
		return mFixedThreadPoolExecutor;
	}



	public static Context getAppContext() {
		return mContext;
	}
}
