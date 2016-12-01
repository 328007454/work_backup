package com.cnksi.sjjc.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

;

public class PlayVideoActivity extends Activity {
	
	@ViewInject(R.id.vv_audio)
	VideoView mVvAudio;
	
	private MediaController mController = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_video);
		x.view().inject(this);
		initUI();
		initData();
	}



	protected void initUI() {
		String videoPath = getIntent().getStringExtra(Config.VIDEO_PATH);
		
		initVideoView(videoPath);
	}

	protected void initData() {
		
	}
	
	/**
	 * 初始化视频播放控件
	 * @param videoPath
	 */
	private void initVideoView(String videoPath){
		mController = new MediaController(this);
		mVvAudio.setVideoPath(videoPath);
		mVvAudio.setMediaController(mController);
		mController.setMediaPlayer(mVvAudio);
		mVvAudio.requestFocus();
		mVvAudio.start();
		mVvAudio.setOnCompletionListener(new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				PlayVideoActivity.this.finish();
			}
		});
	}

}
