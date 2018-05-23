package com.cnksi.sync;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import com.cnksi.bdzinspection.BuildConfig;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.common.Config;
import com.cnksi.core.utils.DeviceUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.ksynclib.KNConfig;
import com.cnksi.ksynclib.KSync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author kkk on 2018/1/2.
 */

public class KSyncConfig {
    final static KSyncConfig instance = new KSyncConfig();
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    private KSync ksync;
    Activity mActivity;

    public static KSyncConfig getInstance() {
        return instance;
    }

    public KSyncConfig getKNConfig(Context context, Handler handler) {
        mActivity = (Activity) context;
        String deviceId = DeviceUtils.getSerialNumber(context);
        KNConfig config = new KNConfig(context, Config.DATABASE_NAME, Config.DATABASE_FOLDER, Config.SYNC_APP_ID_VALUE,
                Config.SYNC_URL_VALUE, deviceId, XunshiApplication.getDbUtils().getDatabase(), Config.BDZ_INSPECTION_FOLDER);
        config.configDebug(BuildConfig.DEBUG);
        config.configDynicParam("dept_id", PreferencesUtils.get( Config.CURRENT_DEPARTMENT_ID, ""));
        ksync = new KSync(config, handler);
        return this;
    }


    public void upload() {
        executorService.execute(() -> ksync.uploadData());
    }

    public void downLoad() {
        executorService.execute(() -> ksync.download("report", "task", "copy_item", "copy_result", "defect_record", "placed"));
    }

}
