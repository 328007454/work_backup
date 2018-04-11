package com.cnksi.sync;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.cnksi.bdzinspection.BuildConfig;
import com.cnksi.bdzinspection.application.CustomApplication;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.xscore.xscommon.DeviceUtils;
import com.cnksi.xscore.xsutils.PreferencesUtils;
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
                Config.SYNC_URL_VALUE, deviceId, (SQLiteDatabase) CustomApplication.getDbUtils().getDatabase(), Config.BDZ_INSPECTION_FOLDER);
        config.configDebug(BuildConfig.DEBUG);
        config.configDynicParam("dept_id", PreferencesUtils.getString(context, Config.CURRENT_DEPARTMENT_ID, ""));
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
