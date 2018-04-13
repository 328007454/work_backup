package com.cnksi.bdzinspection.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseIntArray;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.application.XunshiApplication;

public class PlaySound {

	private static PlaySound mInstance;

	private SparseIntArray soundMap = null;

	private Context context;

	private SoundPool mSoundPool = null;

	private int currentStreamID = -1;

	@SuppressWarnings("deprecation")
	private PlaySound(final Context context) {
		this.context = context;
		mSoundPool = new SoundPool(100, AudioManager.STREAM_MUSIC, 100);
		XunshiApplication.getFixedThreadPoolExecutor().execute(new Runnable() {

			@Override
			public void run() {
				initSoundMap(context);
			}
		});

	}

	public static PlaySound getIntance(Context context) {
		if (mInstance == null) {
			mInstance = new PlaySound(context);
		}
		return mInstance;
	}

	/**
	 * 初始化声音
	 */
	private void initSoundMap(Context context) {
		soundMap = new SparseIntArray();
		soundMap.put(R.raw.clear, mSoundPool.load(context, R.raw.clear, 1));
		soundMap.put(R.raw.click_center, mSoundPool.load(context, R.raw.click_center, 1)); // 点击转盘中间
		soundMap.put(R.raw.click_menu, mSoundPool.load(context, R.raw.click_menu, 1));
		soundMap.put(R.raw.control_click, mSoundPool.load(context, R.raw.control_click, 1));
		soundMap.put(R.raw.input, mSoundPool.load(context, R.raw.input, 1));
		soundMap.put(R.raw.printing, mSoundPool.load(context, R.raw.printing, 1));
		soundMap.put(R.raw.print_out, mSoundPool.load(context, R.raw.print_out, 1));
		soundMap.put(R.raw.record, mSoundPool.load(context, R.raw.record, 1));
		soundMap.put(R.raw.send, mSoundPool.load(context, R.raw.send, 1));
		soundMap.put(R.raw.swing_appear, mSoundPool.load(context, R.raw.swing_appear, 1));
		soundMap.put(R.raw.swing_record, mSoundPool.load(context, R.raw.swing_record, 1));
		soundMap.put(R.raw.track, mSoundPool.load(context, R.raw.track, 1));
		soundMap.put(R.raw.trun, mSoundPool.load(context, R.raw.trun, 1));
		soundMap.put(R.raw.up, mSoundPool.load(context, R.raw.up, 1));
		soundMap.put(R.raw.down, mSoundPool.load(context, R.raw.down, 1));
		soundMap.put(R.raw.delete, mSoundPool.load(context, R.raw.delete, 1));
		soundMap.put(R.raw.click, mSoundPool.load(context, R.raw.click, 1));
	}

	/**
	 * 播放音效
	 * 
	 * @param resId
	 *            声音的资源id
	 *            <p>
	 *            R.raw.clear 消除缺陷点“确定”
	 *            <p>
	 *            R.raw.click_center 点击转盘中间
	 *            <p>
	 *            R.raw.click_menu 点击转盘的圆圈
	 *            <p>
	 *            R.raw.control_click 缺陷管控点“报、跟、消”
	 *            <p>
	 *            R.raw.input 设备列表点击“笔” 设备巡检点击“笔”
	 *            <p>
	 *            R.raw.printer 打印巡检报告
	 *            <p>
	 *            R.raw.record 记录缺陷点“确定”
	 *            <p>
	 *            R.raw.send 上报缺陷点“上报”
	 *            <p>
	 *            R.raw.swing-appear 设备列表“摇一摇”
	 *            <p>
	 *            R.raw.swing_record 设备巡检“摇一摇”
	 *            <p>
	 *            R.raw.track 跟踪缺陷点“确定”
	 *            <p>
	 *            R.raw.trun 首页转动转盘
	 *            <p>
	 *            R.raw.shake_sound_male
	 *            <p>
	 *            R.raw.shake_match
	 * 
	 */
	public void play(int resId) {
		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float currentVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float rate = currentVolume / maxVolume;
		currentStreamID = mSoundPool.play(soundMap.get(resId), rate, rate, 1, 0, 1);
	}

	/**
	 * 停止播放
	 */
	public void stop() {
		mSoundPool.stop(currentStreamID);
	}
}
