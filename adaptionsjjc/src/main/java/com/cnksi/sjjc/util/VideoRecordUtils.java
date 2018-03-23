package com.cnksi.sjjc.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;


public class VideoRecordUtils {

	/**
	 * 启动系统录像功能，按指定的文件名称存储到指定路径下
	 * 
	 * @param currentVideoName
	 *            当前照片文件名
	 */
	public static void takeVideo(Activity currentActivity, String currentVideoName, String defectImagePath, int requestCode) {
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		File imageFile = new File(defectImagePath, currentVideoName);
		intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
		currentActivity.startActivityForResult(intent, requestCode);
	}

	/**
	 * 启动系统播放视频功能(失败)
	 * 
	 * @param currentActivity
	 * @param currentVideoName
	 */
	public static void playVideo(Activity currentActivity, String currentVideoName) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse(currentVideoName), "video/mp4");
		currentActivity.startActivity(intent);
	}

	/**
	 * 返回以计划Id为前缀的图片名称
	 * 
	 * @param
	 * @return
	 */
	public static String getCurrentVideoName(String planId) {
		if (!TextUtils.isEmpty(planId)) {
			planId = planId + "_";
		}
		return planId + getCurrentVideoName();
	}

	/**
	 * 生成当前视频名称
	 * 
	 * @return
	 */
	public static String getCurrentVideoName() {
		SimpleDateFormat formatter = new SimpleDateFormat(CoreConfig.dateFormat6, Locale.CHINA);
		String uuidStr = UUID.randomUUID().toString().replace(CoreConfig.DASH_SEPARATOR, "");
		if (uuidStr.length() > 8) {
			uuidStr = uuidStr.substring(0, 8);
		}
		return String.valueOf(formatter.format(new Date()) + uuidStr + ".mp4");
	}

	/**
	 * 获取视频的缩略图
	 * 
	 * 先通过ThumbnailUtils来创建一个视频的缩略图，然后再利用ThumbnailUtils来生成指定大小的缩略图。 如果想要的缩略图的宽和高都小于MICRO_KIND，则类型要使用MICRO_KIND作为kind的值，这样会节省内存。
	 * 
	 * @param videoPath
	 *            视频的路径
	 * @param width
	 *            指定输出视频缩略图的宽度
	 * @param height
	 *            指定输出视频缩略图的高度度
	 * @param kind
	 *            参照MediaStore.Images.Thumbnails类中的常量MINI_KIND和MICRO_KIND。 其中，MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
	 * @return 指定大小的视频缩略图
	 */
	public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
		Bitmap bitmap = null;
		// 获取视频的缩略图
		bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	/**
	 * 获取视频缩略图
	 * 
	 * @param
	 * @return
	 */
	public static Bitmap getVideoThumbnail(String videoPath) {
		Bitmap bitmap = null;
		MediaMetadataRetriever mRetriever = new MediaMetadataRetriever();
		try {
			mRetriever.setDataSource(videoPath);
			bitmap = mRetriever.getFrameAtTime();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (RuntimeException e) {
			e.printStackTrace();
		} finally {
			try {
				mRetriever.release();
			} catch (RuntimeException e) {
				e.printStackTrace();
			}
		}
		return bitmap;
	}

	/**
	 * 获取视频长度
	 * 
	 * @param videoPath
	 * @return
	 */
	public static int getVideoLength(String videoPath) {
		MediaMetadataRetriever mRetriever = new MediaMetadataRetriever();
		mRetriever.setDataSource(videoPath);
		int duration = Integer.valueOf(mRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
		return duration;
	}

	/**
	 * 将毫秒转为HH：mm：ss格式
	 * 
	 * @param mss
	 * @return
	 */
	public static String getFormatTime(int mss) {
		int hours = (mss / (1000 * 60 * 60));
		int minutes = (mss - hours * (1000 * 60 * 60)) / (1000 * 60);
		int seconds = (mss - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / 1000;
		if (hours > 0) {
			return hours + ":" + minutes + ":" + seconds;
		} else {
			return minutes + ":" + seconds;
		}
	}

}
