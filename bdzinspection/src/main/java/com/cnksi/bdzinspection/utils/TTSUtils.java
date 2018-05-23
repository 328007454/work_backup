package com.cnksi.bdzinspection.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.tts.ISpeakCallback;
import com.cnksi.tts.ISpeakInterface;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2017/4/1 13:36
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */

/**
 * 使用AIDL的方式进行远程调用TTS服务完成操作说话
 */
public class TTSUtils {
    private final static String TAG = "TTSUtils";
    private final static TTSUtils instance = new TTSUtils();

    public static TTSUtils getInstance() {
        return instance;
    }

    private ISpeakInterface speakInterface;
    private boolean isConnect = false;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
           instance.speakInterface = ISpeakInterface.Stub.asInterface(service);
            instance.isConnect = true;
            Log.i(TAG, "Connect to TTServer successful");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            instance.isConnect = false;
            instance.speakInterface = null;
            Log.i(TAG, "Disconnect to TTServer.");
        }
    };

    public static void init(Context context) {
        if (instance.isConnect) {
            Log.w(TAG, "TTServer is connect.Skip to init");
            return;
        }
        Intent intent = new Intent();
        intent.setPackage("com.cnksi.sjjc");
        intent.setAction("android.intent.action.TTService");
        context.bindService(intent, instance.connection, Context.BIND_AUTO_CREATE);
    }

    public void startSpeaking(String content) {
        startSpeaking(content, null);
    }


    public void startSpeaking(String content, ISpeakCallback callback) {
        if (isConnect()) {
            try {
                speakInterface.speak(content, callback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            init(XunshiApplication.getAppContext());
        }
    }

    public void stopSpeak() {
        if (!isConnect()) return;
        try {
            speakInterface.stopSpeak();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }


    public boolean isConnect() {
        if (!isConnect) {
            ToastUtils.showMessage("没有连接到TTS服务。");
        }
        return isConnect;
    }
}
