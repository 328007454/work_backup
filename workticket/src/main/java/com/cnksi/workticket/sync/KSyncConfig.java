package com.cnksi.workticket.sync;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.cnksi.core.utils.DeviceUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.view.CustomerDialog;
import com.cnksi.ksynclib.KNConfig;
import com.cnksi.ksynclib.KSync;
import com.cnksi.ksynclib.model.SyncInfo;
import com.cnksi.workticket.BuildConfig;
import com.cnksi.workticket.Config;
import com.cnksi.workticket.db.WorkTicketDbManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.cnksi.ksynclib.activity.KSyncAJActivity.DELETE_FINISHED;

/**
 * @author kkk on 2018/1/2.
 */

public class KSyncConfig {
    final static KSyncConfig instance = new KSyncConfig();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private KSync ksync;
    private Context mActivity;
    private SyncHandler syncHandler;
    private SyncFailListener failListener;

    public interface SyncFailListener {
        void failCallBack(boolean syncSuccess);
    }

    public void setFailListener(SyncFailListener failListener) {
        this.failListener = failListener;
    }


    public static KSyncConfig getInstance() {
        return instance;
    }

    public KSyncConfig getKNConfig(Context context) {
        mActivity = context;
        String deviceId = DeviceUtils.getSerialNumber(context);
        KNConfig config = new KNConfig(context, Config.DATABASE_NAME, WorkTicketDbManager.getInstance().getDbFolder(), Config.SYNC_APP_ID_VALUE,
                Config.SYNC_URL_VALUE, deviceId, WorkTicketDbManager.getInstance().getTicketManager().getDatabase(), Config.BDZ_INSPECTION_FOLDER);
        config.configDebug(BuildConfig.DEBUG);
        config.configDynicParam("dept_id", Config.deptID);
        ksync = new KSync(config, new SyncHandler());
        return this;
    }


    public void upload() {
        executorService.execute(() -> ksync.uploadData());
    }

    public void downLoad() {
        executorService.execute(() -> ksync.download("users", "bdz", "workticket_order", "", "department"));
    }

    public class SyncHandler extends Handler {
        boolean syncSuccess = false;
        SyncInfo info = null;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case KSync.SYNC_ERROR:
                    info = new SyncInfo(String.valueOf(msg.obj), KSync.SYNC_ERROR);
                    syncSuccess = false;
                    if (failListener != null) {
                        failListener.failCallBack(syncSuccess);
                    }
                    break;
                case KSync.SYNC_INFO:

                    break;
                case KSync.SYNC_CONNECTING:
                case KSync.SYNC_START:
                    info = new SyncInfo(String.valueOf(msg.obj), KSync.SYNC_INFO);
                    break;
                case KSync.SYNC_SUCCESS:
                    info = new SyncInfo(String.valueOf(msg.obj), KSync.SYNC_SUCCESS);
                    CustomerDialog.dismissProgress();
                    syncSuccess = true;
                    if (failListener != null) {
                        failListener.failCallBack(syncSuccess);
                    }
                    break;
                case KSync.SYNC_PING:
                    return;
                case KSync.SYNC_SERVER_TIME:
                    //去设置时间
                    // setTime(String.valueOf(msg.obj));
                    return;
                case DELETE_FINISHED:
                    ToastUtils.showMessage(String.valueOf(msg.obj), Toast.LENGTH_SHORT);
                    CustomerDialog.dismissProgress();
                    break;
                case KSync.SYNC_FINISH:
                    ToastUtils.showMessage(String.valueOf(msg.obj), Toast.LENGTH_SHORT);
                    break;
                default:
                    break;
            }
        }
    }

}
