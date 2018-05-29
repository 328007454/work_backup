package com.cnksi.bdzinspection.ywyth;

import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.cnksi.bdzinspection.R;
import com.cnksi.common.base.BaseActivity;
import com.cnksi.bdzinspection.databinding.XsVideoviewBinding;

public class VideoPlayer extends BaseActivity {
	String str;

	private XsVideoviewBinding binding;

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
		binding = DataBindingUtil.setContentView(mActivity,R.layout.xs_videoview);
		binding.video.setOnErrorListener((mp, what, extra) -> {
            VideoPlayer.this.finish();
            return false;
        });
		if (str != null) {
			binding.video.setMediaController(null);
			binding.video.setVideoPath(str);
			binding.video.start();
			binding.video.requestFocus();
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
