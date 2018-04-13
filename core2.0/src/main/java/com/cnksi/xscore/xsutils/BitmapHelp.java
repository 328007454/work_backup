package com.cnksi.xscore.xsutils;

import android.content.Context;

import com.lidroid.xutils.BitmapUtils;

/**
 * 利用 xUtils 中的BitmapUitls 加载图片
 * 
 */
public class BitmapHelp {

	/**
	 * 内存缓存默认为内存的60%
	 */
	private static float mMemCacheSizePercent = 0.6f;
	/**
	 * 线程数
	 */
	private static int threadPoolSize = 8;

	private static BitmapHelp mInstance;

	private BitmapHelp() {
	}

	private static BitmapUtils bitmapUtils;

	public static BitmapHelp getInstance() {
		if (mInstance == null) {
			synchronized (BitmapHelp.class) {
				if (mInstance == null) {
					mInstance = new BitmapHelp();
				}
			}
		}
		return mInstance;
	}

	/**
	 * BitmapUtils不是单例的 根据需要重载多个获取实例的方法
	 * 
	 * @param appContext
	 *            application context
	 * @return
	 */
	public BitmapUtils getBitmapUtils(Context mContext) {
		if (bitmapUtils == null) {
			bitmapUtils = new BitmapUtils(mContext, null, mMemCacheSizePercent);
			bitmapUtils.configThreadPoolSize(threadPoolSize);
		}
		return bitmapUtils;
	}

	/**
	 * 清除内存缓存
	 */
	public static void clearMemCache() {
		if (bitmapUtils != null) {
			bitmapUtils.clearMemoryCache();
		}
	}

	/**
	 * 清除磁盘缓存
	 */
	public static void clearDiskCache() {
		if (bitmapUtils != null) {
			bitmapUtils.clearDiskCache();
		}
	}

}
