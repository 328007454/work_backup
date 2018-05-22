package com.cnksi.bdzinspection.utils;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.util.Log;

import com.cnksi.core.utils.ToastUtils;

import java.io.File;
import java.io.IOException;

public class MediaRecorderUtils {

	private static MediaRecorderUtils mInstance;

	private MediaRecorderUtils() {
	}

	public static MediaRecorderUtils getInstance() {
		if (mInstance == null) {
			mInstance = new MediaRecorderUtils();
		}
		return mInstance;
	}

	/**
	 * 录音器
	 */
	private MediaRecorder mRecorder;
	/**
	 * 播放器
	 */
	private MediaPlayer mPlayer;
	/**
	 * 是否正在录音
	 */
	private boolean isRecording = false;
	/**
	 * 是否正在播放
	 */
	private boolean isPlaying = false;

	/**
	 * 开始录音
	 *
	 * @param fileName
	 */
	public void startRecordAudio(String fileName) {

		mRecorder = new MediaRecorder();
		// 第1步：设置音频来源（MIC表示麦克风）
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		// 第2步：设置音频输出格式
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		// 第3步：设置音频编码方式
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

		// 第4步：指定音频输出文件
		mRecorder.setOutputFile(fileName);
		try {
			mRecorder.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mRecorder.start();
		isRecording = true;
	}

	/**
	 * 停止录音
	 */
	public void stopRecordAudio() {
		if (mRecorder != null) {
			mRecorder.stop();

			mRecorder.release();
			mRecorder = null;
		}
		isRecording = false;
	}

	/**
	 * 播放录音
	 *
	 * @param fileName
	 */
	public void startPlayAudio(String fileName, OnCompletionListener listener) {
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setOnCompletionListener(listener);
			mPlayer.setDataSource(fileName);
			mPlayer.prepare();
			mPlayer.start();
			Log.e("Play Audio", fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		isPlaying = true;
		ToastUtils.showMessage("正在播放...");
	}

	public void stopPlayAudio() {
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
		}
		isPlaying = false;
	}

	public boolean isRecording() {
		return isRecording;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	/**
	 * getDuration获得音频长度
	 *
	 * @param path
	 * @return 秒
	 */
	public int getDurationSuc(Context context, String path) {
		int duration = 0;
		File file = new File(path);
		if (file.exists()) {
			MediaPlayer mPlayer = MediaPlayer.create(context, Uri.fromFile(file));
			if (mPlayer != null) {
				try {
					mPlayer.setDataSource(path);
					mPlayer.prepare();
				} catch (Exception e) {
					e.printStackTrace();
				}
				duration = mPlayer.getDuration();
			}
		}
		return duration / 1000;
	}

	/**
	 * getDuration获得视频频长度
	 *
	 * @param path
	 * @return hh:mm:ss
	 */
	public String getVedioDurationString(Context context, String path) {
		int d = getVideoDuration(context, path);
		if (d < 60) {
			return "0:" + String.valueOf(d);
		}
		if (d < 3600) {
			return String.valueOf(d / 60) + ":" + String.valueOf(d % 60);
		}
		if (d >= 3600) {
			return String.valueOf(d / 3600) + ":" + String.valueOf((d % 3600) / 60) + ":" + String.valueOf((d % 3600) % 60);
		} else
			return "0:00";
	}

	public int getVideoDuration(Context context, String path) {
		File file = new File(path);
		long timeInMillisec = 0;
		if (file.exists()) {
			MediaMetadataRetriever retriever = new MediaMetadataRetriever();
			// use one of overloaded setDataSource() functions to set your data source
			retriever.setDataSource(context, Uri.fromFile(file));
			String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
			timeInMillisec = Long.parseLong(time);
		}

		return (int) (timeInMillisec / 1000);
	}

	/**
	 * 得到音频的时常
	 *
	 * @param context
	 * @param path
	 * @return
	 */
	public String getAudioDuration(Context context, String path) {
		int d = getVideoDuration(context, path);
		if (d < 60) {
			return String.valueOf(d) + "″";
		}
		if (d >= 60) {
			return String.valueOf(d / 60) + "′" + String.valueOf(d % 60) + "″";
		} else
			return "0″";
	}
}
