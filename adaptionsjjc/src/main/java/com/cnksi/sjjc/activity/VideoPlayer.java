package com.cnksi.sjjc.activity;


import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

import com.cnksi.sjjc.R;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class VideoPlayer extends BaseActivity {
	@ViewInject(R.id.video)
	private VideoView videoView;
	String str;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// 设置屏幕常亮
		this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
		window.setAttributes(params);
		str = getIntent().getStringExtra("video");
		setContentView(R.layout.videoview);
		x.view().inject(_this);
		videoView.setOnErrorListener(new OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				finish();
				return false;
			}
		});
		if (str != null) {
			videoView.setMediaController(null);
			videoView.setVideoPath(str);
			videoView.start();
			videoView.requestFocus();
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
		window.setAttributes(params);
		super.onConfigurationChanged(newConfig);
		// basePlayerManager.onConfigurationChanged(newConfig);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
