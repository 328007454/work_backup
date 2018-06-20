package com.cnksi.common;

import android.util.Pair;

import com.alibaba.android.arouter.launcher.ARouter;
import com.cnksi.DebugDB;
import com.cnksi.bdloc.LLog;
import com.cnksi.bdloc.LocationUtil;
import com.cnksi.common.utils.EssSafeUtil;
import com.cnksi.common.utils.PlaySound;
import com.cnksi.common.utils.TTSUtils;
import com.cnksi.core.application.CoreApplication;
import com.cnksi.core.utils.CLog;
import com.cnksi.core.utils.DisplayUtils;
import com.cnksi.core.utils.FileUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.utils.crash.CrashReportUploadHandler;
import com.cnksi.ksynclib.IKSync;
import com.cnksi.ksynclib.KSync;
import com.squareup.leakcanary.LeakCanary;

import net.sqlcipher.database.SQLiteDatabase;

import org.xutils.DbManager;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author luoxy
 * @version 1.0
 * @date 16/4/20
 */
public class CommonApplication extends CoreApplication implements IKSync {
    //数据库管理者
    private static CommonApplication mInstance = null;


    public static CommonApplication getInstance() {
        return mInstance;
    }


    /**
     * 自定义数据库配置 需要重写
     *
     * @return
     */
    DbManager.DaoConfig config;
    int dbVersion = 1;

    @Override
    public DbManager.DaoConfig getDaoConfig() {
        if (config == null) {
            config = new DbManager.DaoConfig(this)
                    .setDbDir(new File(CommonApplication.getAppContext().getFilesDir().getAbsolutePath() + "/database/"))
                    .setDbName(Config.ENCRYPT_DATABASE_NAME)
                    .setDbVersion(1)
                    .setDbOpenListener(db -> {
                    })
                    .setUseEncrypt(true)
                    .setKey("com.cnksi")
                    .setDbUpgradeListener((db, oldVersion, newVersion) -> {
                        dbVersion = oldVersion;
                    }).setAllowTransaction(true);
        }
        return config;
    }

    @Override
    public DbManager getDbManager() {
        if (dbVersion > 1) {
            /**
             * 由于同步会动态的更新数据库版本号，但是打开数据库的时候会重置为1，因此需要在第一次获取DbManger是恢复回去。
             */
            DbManager db = super.getDbManager();
            if (db.getDatabase() instanceof SQLiteDatabase) {
                ((SQLiteDatabase) db.getDatabase()).setVersion(dbVersion);
            } else if (db.getDatabase() instanceof android.database.sqlite.SQLiteDatabase) {
                ((android.database.sqlite.SQLiteDatabase) db.getDatabase()).setVersion(dbVersion);
            }
            dbVersion = 1;
            return db;
        } else {
            return super.getDbManager();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        LeakCanary.install(mInstance);
        PreferencesUtils.init(getApplicationContext());
        ToastUtils.init(getApplicationContext());
        DisplayUtils.getInstance().setStandHeight(1920).setStandWidth(1080).init(getApplicationContext());
        CLog.init(Config.DEBUG);
        PlaySound.initPlay(this);
        CrashReportUploadHandler.init(mInstance, Config.LOG_FOLDER).start();
        EssSafeUtil.checkInEmualtorOrDebuggable(getApplicationContext());
        initRuntimeVar();
        TTSUtils.init(getAppContext());
        LocationUtil.init(getAppContext());
        LLog.isLog = Config.DEBUG;
        initDebugDb();
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
            ARouter.openDebug();
        }
        ARouter.init(this);

    }

    private void initDebugDb() {
        if (Config.DEBUG) {
            HashMap<String, Pair<File, String>> stringPairHashMap = new HashMap<>();
            stringPairHashMap.put("bdzinspection", new Pair<>(new File(Config.DATABASE_FOLDER, Config.DATABASE_NAME), ""));
            DebugDB.setCustomDatabaseFiles(stringPairHashMap);
        }
    }


    public void initApp() {
        FileUtils.makeDirectory(Config.FILE_PATH_ARRAY);
    }


    /**
     * 结束应用
     */
    public void exitApplication() {
        try {
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
        } catch (Exception e) {
            Runtime.getRuntime().exit(-1);
        }
    }

    private void initRuntimeVar() {
        Config.SYNC_URL = PreferencesUtils.get(Config.KEY_SYNC_URL, Config.SYNC_URL);
        Config.SYNC_APP_ID = PreferencesUtils.get(Config.KEY_SYNC_APP_ID, Config.SYNC_APP_ID);
    }

    Map<String, KSync> map;

    @Override
    public Map<String, KSync> getKSyncMap() {
        if (map == null) {
            map = new HashMap<>();
            map.put(Config.SYNC_APP_ID, KSync.create(KSyncConfig.getInstance().getKNConfig(this)));
        }
        return map;
    }
}
