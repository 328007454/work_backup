package com.cnksi.core.application;

import android.app.Application;
import android.content.Context;

import org.xutils.DbManager;
import org.xutils.x;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Oliver on 2015/11/24.
 */
public class CoreApplication extends Application {

    // 线程池
    protected static ExecutorService mExcutorService = null;
    //数据库配置
    protected static DbManager.DaoConfig mDaoConfig = null;
    //数据库管理者
    private static DbManager mDbManager = null;

    private static Context mContext;

    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        mContext = this.getApplicationContext();
        int threadPoolSize = Runtime.getRuntime().availableProcessors();
        mExcutorService = Executors.newFixedThreadPool(threadPoolSize > 3 ? 3 : threadPoolSize);
    }


    /**
     * 返回线程池
     *
     * @return
     */
    public static ExecutorService getExcutorService() {
        return mExcutorService;
    }

    /**
     * 得到数据库管理者
     *
     * @return
     */
    public static DbManager getDbManager() {
        if (mDbManager == null) {
            mDbManager = x.getDb(getDaoConfig());
        }
        return mDbManager;
    }

    /**
     * 自定义数据库配置 需要重写
     *
     * @return
     */
    protected static DbManager.DaoConfig getDaoConfig() {
        return mDaoConfig;
    }


    public static Context getAppContext() {
        return mContext;
    }
}
