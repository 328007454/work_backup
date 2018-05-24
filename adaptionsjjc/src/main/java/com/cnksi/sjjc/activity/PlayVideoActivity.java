package com.cnksi.sjjc.activity;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.widget.MediaController;

import com.cnksi.common.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.databinding.ActivityPlayVideoBinding;

public class PlayVideoActivity extends Activity {

	private MediaController mController = null;
	private ActivityPlayVideoBinding binding;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = DataBindingUtil.setContentView(this,R.layout.activity_play_video);
		initView();
		loadData();
	}



	protected void initView() {
		String videoPath = getIntent().getStringExtra(Config.VIDEO_PATH);
		
		initVideoView(videoPath);
	}

	protected void loadData() {
		
	}
	
	/**
	 * 初始化视频播放控件
	 * @param videoPath
	 */
	private void initVideoView(String videoPath){
		mController = new MediaController(this);
		binding.vvAudio.setVideoPath(videoPath);
		binding.vvAudio.setMediaController(mController);
		mController.setMediaPlayer(binding.vvAudio);
		binding.vvAudio.requestFocus();
		binding.vvAudio.start();
		binding.vvAudio.setOnCompletionListener(mp -> PlayVideoActivity.this.finish());
	}

}
