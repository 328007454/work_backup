/*
 * Copyright 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cnksi.bdzinspection.fragment;

import android.app.Fragment;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.common.Config;
import com.cnksi.common.model.BaseModel;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("deprecation")
public class CameraVideoFragment extends Fragment implements Callback {

	/************* 摄像相关 **************/
	private SurfaceView mSurfaceView;
	private Camera mCamera;
	private MediaRecorder mMediaRecorder;
	private Camera.Parameters parameters;
	/**
	 * 文件名
	 */
	private String videoFileName = "";

	private boolean isRecording = false;

	public static CameraVideoFragment newInstance() {
		return new CameraVideoFragment();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.xs_fragment_camera_video, container, false);
	}

	@Override
	public void onViewCreated(final View view, Bundle savedInstanceState) {
		mSurfaceView = (SurfaceView) view.findViewById(R.id.sv_video);
	}

	@Override
	public void onResume() {
		super.onResume();
		initCamera();
	}

	@Override
	public void onPause() {
		super.onPause();
		stopRecordingVideo();
	}

	/************************************* 摄像相关 **************************************/
	private void initCamera() {
		mSurfaceView.getHolder().addCallback(this);
		// 后置摄像头
		mCamera = Camera.open(0);
		parameters = mCamera.getParameters();
		List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();
		int previewWidth = 0;
		int previewHeight = 0;
		for (int i = 0; i < previewSizeList.size() - 1; i++) {
			previewWidth = previewSizeList.get(i).width;
			previewHeight = previewSizeList.get(i).height;
			int nextWidth = previewSizeList.get(i + 1).width;
			int nextHeight = previewSizeList.get(i + 1).height;
			if (previewWidth < nextWidth) {
				previewWidth = nextWidth;
			}
			if (previewHeight < nextHeight) {
				previewHeight = nextHeight;
			}
		}
		previewWidth = previewWidth > previewSizeList.get(0).width ? previewWidth : previewSizeList.get(0).width;
		previewHeight = previewHeight > previewSizeList.get(0).height ? previewHeight : previewSizeList.get(0).height;
		parameters.setPreviewSize(previewWidth, previewHeight);

		List<Camera.Size> supportedPictureSizesList = parameters.getSupportedPictureSizes();
		int supportedPictureWidth = 0;
		int supportedPictureHeight = 0;
		for (int i = 0; i < supportedPictureSizesList.size() - 1; i++) {
			supportedPictureWidth = supportedPictureSizesList.get(i).width;
			supportedPictureHeight = supportedPictureSizesList.get(i).height;
			int nextWidth = supportedPictureSizesList.get(i + 1).width;
			int nextHeight = supportedPictureSizesList.get(i + 1).height;
			if (supportedPictureWidth < nextWidth) {
				supportedPictureWidth = nextWidth;
			}
			if (supportedPictureHeight < nextHeight) {
				supportedPictureHeight = nextHeight;
			}
		}
		supportedPictureWidth = supportedPictureWidth > supportedPictureSizesList.get(0).width ? supportedPictureWidth : supportedPictureSizesList.get(0).width;
		supportedPictureHeight = supportedPictureHeight > supportedPictureSizesList.get(0).height ? supportedPictureHeight : supportedPictureSizesList.get(0).height;
		parameters.setPictureSize(supportedPictureWidth, supportedPictureHeight);
		// parameters.setRotation(90);
		if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
			parameters.set("orientation", "portrait");
			mCamera.setDisplayOrientation(90);
			parameters.setRotation(90);
		} else {
			parameters.set("orientation", "landscape");
			mCamera.setDisplayOrientation(0);
			parameters.setRotation(0);
		}
		mCamera.setParameters(parameters);
	}

	public void startRecordingVideo() {
		try {
			mCamera.unlock();
			mMediaRecorder = new MediaRecorder();
			mMediaRecorder.setCamera(mCamera);
			mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
			mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			mMediaRecorder.setOrientationHint(90);
			mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
			videoFileName = BaseModel.getPrimarykey() + ".mp4";
			// mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			mMediaRecorder.setOutputFile(Config.VIDEO_FOLDER + videoFileName);
			mMediaRecorder.prepare();
			mMediaRecorder.start();
			isRecording = true;
		} catch (Exception e) {
			isRecording = false;
		}
	}

	/**
	 * 停止录像
	 */
	public String stopRecordingVideo() {
		if (isRecording) {
			isRecording = false;
			if (mMediaRecorder != null) {
				mMediaRecorder.stop();
				mMediaRecorder.reset();
				mMediaRecorder.release();
				mMediaRecorder = null;
				mCamera.setPreviewCallback(null);
				mCamera.lock();
			}
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
			return videoFileName;
		} else {
			return "";
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
			// 开始录制
			startRecordingVideo();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}
}
