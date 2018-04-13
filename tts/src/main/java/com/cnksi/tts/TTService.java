package com.cnksi.tts;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2017/4/1 11:16
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class TTService extends Service {
    private final static String TAG = "TTService";

    private SpeechSynthesizer mTts;
    private boolean isPrepare = false;
    private String speaker = "xiaoyan";
    ISpeakCallback DEFAULT = new ISpeakCallback.Stub() {
        @Override
        public void onSpeakBegin() throws RemoteException {
        }

        @Override
        public void onSpeakPaused() throws RemoteException {

        }

        @Override
        public void onSpeakResumed() throws RemoteException {

        }

        @Override
        public void onCompleted(String error) throws RemoteException {

        }
    };
    ISpeakInterface.Stub stub = new ISpeakInterface.Stub() {

        @Override
        public int speak(final String content, ISpeakCallback callback) throws RemoteException {
            if (isPrepare()) {
                final ISpeakCallback mCallback = callback != null ? callback : DEFAULT;
                if (mTts.isSpeaking()) mTts.stopSpeaking();
                mTts.startSpeaking(content, new SynthesizerListener() {
                    @Override
                    public void onSpeakBegin() {
                        try {
                            mCallback.onSpeakBegin();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onBufferProgress(int i, int i1, int i2, String s) {

                    }

                    @Override
                    public void onSpeakPaused() {
                        try {
                            mCallback.onSpeakPaused();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSpeakResumed() {
                        try {
                            mCallback.onSpeakResumed();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSpeakProgress(int i, int i1, int i2) {

                    }

                    @Override
                    public void onCompleted(SpeechError speechError) {
                        try {
                            mCallback.onCompleted(speechError == null ? null : speechError.getMessage());
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onEvent(int i, int i1, int i2, Bundle bundle) {

                    }
                });
            }
            return 0;
        }

        @Override
        public int stopSpeak() {
            if (isPrepare()) {
                if (mTts.isSpeaking()) {
                    mTts.stopSpeaking();
                }
            }
            return 0;
        }

        @Override
        public boolean setSpeaker(String speaker) throws RemoteException {
            TTService.this.speaker = speaker;
            if (isPrepare())
                return mTts.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath()) && mTts.setParameter(SpeechConstant.VOICE_NAME, speaker);
            return false;
        }

    };

    @Override
    public void onCreate() {
        super.onCreate();
        initTTS();
        log("onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    private boolean isPrepare() {
        if (!isPrepare) Log.w(TAG, "TTS服务还没有准备好！");
        return isPrepare;
    }

    private void initTTS() {
        isPrepare = false;
        StringBuffer param = new StringBuffer();
        param.append("appid=" + getString(R.string.app_id));
        param.append(',').append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
        param.append(',').append(SpeechConstant.FORCE_LOGIN + "=true");
        SpeechUtility.createUtility(getApplicationContext(), param.toString());
        mTts = SpeechSynthesizer.createSynthesizer(getApplicationContext(), new InitListener() {
            @Override
            public void onInit(int code) {
                if (code == ErrorCode.SUCCESS) {
                    // 清空参数
                    mTts.setParameter(SpeechConstant.PARAMS, null);
                    // 设置本地合成
                    mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
                    // 设置发音人资源路径
                    mTts.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
                    // 设置发音人 voicer为空默认通过语音+界面指定发音人。
                    mTts.setParameter(SpeechConstant.VOICE_NAME, speaker);
                    // 设置语速
                    mTts.setParameter(SpeechConstant.SPEED, "50");
                    // 设置音调
                    mTts.setParameter(SpeechConstant.PITCH, "50");
                    // 设置音量
                    mTts.setParameter(SpeechConstant.VOLUME, "100");
                    // 设置播放器音频流类型
                    mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
                    isPrepare = true;
                }
            }
        });
    }


    // 获取发音人资源路径
    private String getResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "tts/common.jet"));
        tempBuffer.append(";");
        //发音人资源
        tempBuffer.append(ResourceUtil.generateResourcePath(this, ResourceUtil.RESOURCE_TYPE.assets, "tts/" + speaker + ".jet"));
        return tempBuffer.toString();
    }

    private void log(String msg) {
        Log.i(TAG, msg);
    }

}