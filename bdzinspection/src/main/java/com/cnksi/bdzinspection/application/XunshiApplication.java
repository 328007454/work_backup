package com.cnksi.bdzinspection.application;

import android.app.Application;
import android.content.Context;

import com.cnksi.bdzinspection.inter.XunshiDatabaseProvider;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.DisplayUtil;
import com.cnksi.bdzinspection.utils.TTSUtils;
import com.cnksi.nari.utils.NariDataManager;
import com.cnksi.xscore.xsutils.CToast;
import com.cnksi.xscore.xsutils.PreferencesUtils;

import org.xutils.DbManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class XunshiApplication {
    private static Application mInstance = null;
    private static Context context;
    public static Application getInstance() {
        return mInstance;
    }
    static XunshiDatabaseProvider xunshiDatabaseProvider;

    public void init(Application application, Context context, XunshiDatabaseProvider databaseProvider) {
        if (mInstance == null) {
            mInstance = application;
            this.context = context;
        }
        XunshiApplication.xunshiDatabaseProvider = databaseProvider;

        Config.COPY_MAX_DISTANCE = PreferencesUtils.getFloat(mInstance, Config.COPY_DISTANCE_KEY, Config.COPY_MAX_DISTANCE);
        NariDataManager.init(Config.NARI_BASEFOLDER);
        PreferencesUtils.init(context);
        DisplayUtil.getInstance().init(context);
        TTSUtils.init(context);
        CToast.init(context);
    }


    public synchronized static DbManager getDbUtils() {
        return xunshiDatabaseProvider.getDbManager();
    }


    // 线程池
    private static ExecutorService mFixedThreadPoolExecutor = null;

    public static ExecutorService getFixedThreadPoolExecutor() {
        if (mFixedThreadPoolExecutor == null) {
            mFixedThreadPoolExecutor = Executors.newFixedThreadPool(3);
        }
        return mFixedThreadPoolExecutor;
    }


    public static Context getAppContext() {
        return context;
    }


}