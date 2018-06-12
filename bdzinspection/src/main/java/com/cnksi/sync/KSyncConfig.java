package com.cnksi.sync;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.cnksi.common.CommonApplication;
import com.cnksi.common.Config;
import com.cnksi.ksynclib.KSync;

import java.util.concurrent.Executor;

/**
 * @author kkk on 2018/1/2.
 */

public class KSyncConfig {
    final static KSyncConfig instance = new KSyncConfig();
    Executor executorService = AsyncTask.THREAD_POOL_EXECUTOR;
    private KSync ksync;
    private SyncListener listener;

    public static KSyncConfig getInstance() {
        return instance;
    }

    public KSyncConfig getKNConfig(SyncListener listener) {
        ksync = CommonApplication.getInstance().getKSyncMap().get(Config.SYNC_APP_ID);
        ksync.configHandler(new SyncHandler());
        this.listener = listener;
        return this;
    }

    public interface SyncListener {
        void start();

        void onSuccess(Type errorType);

        void onError(Type errorType, String errorMsg);
    }

    public enum Type {
        upload,
        download,
        network,
    }


    public void upload() {
        executorService.execute(() -> ksync.uploadData());
    }

    public void downLoad() {
        executorService.execute(() -> ksync.download(new String[]{"report", "task", "copy_item", "copy_result", "defect_record", "placed"}));
    }

    public class SyncHandler extends Handler {


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case KSync.SYNC_START:
                    if (listener != null) {
                        listener.start();
                    }
                    break;
                case KSync.SYNC_ERROR_DATA_DOWNLOAD:
                    if (listener != null) {
                        listener.onError(Type.download, String.valueOf(msg.obj));
                    }
                    break;
                case KSync.SYNC_ERROR_DATA_UPLOAD:
                    if (listener != null) {
                        listener.onError(Type.upload, String.valueOf(msg.obj));
                    }
                    break;
                case KSync.SYNC_ERROR_CONNECT_FAILED:
                case KSync.SYNC_ERROR_CONNECT_TIMEOUT:
                case KSync.SYNC_NETWORK_NOT_CONNECTED:
                    if (listener != null) {
                        listener.onError(Type.network, String.valueOf(msg.obj));
                    }
                    break;
                case KSync.SYNC_DOWN_DATA_SUCCESS:
                    if (listener != null) {
                        listener.onSuccess(Type.download);
                    }
                    break;
                case KSync.SYNC_UP_DATA_SUCCESS:
                    if (listener != null) {
                        listener.onSuccess(Type.upload);
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
