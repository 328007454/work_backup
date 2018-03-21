package com.cnksi.sjjc.activity;


import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.databinding.VideoviewBinding;


public class VideoPlayer extends BaseActivity {
	String str;

	private VideoviewBinding binding;
	
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
		
		binding = DataBindingUtil.setContentView(this,R.layout.videoview);
	
		binding.video.setOnErrorListener(new OnErrorListener() {

			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				finish();
				return false;
			}
		});
		if (str != null) {
			binding.video.setMediaController(null);
			binding.video.setVideoPath(str);
			binding.video.start();
			binding.video.requestFocus();
		}

	}

	@Override
	public void initUI() {
		
	}

	@Override
	public void initData() {

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
