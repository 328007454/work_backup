package com.cnksi.bdzinspection.application;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.view.WindowManager;

import com.cnksi.bdloc.LLog;
import com.cnksi.bdloc.LocationUtil;
import com.cnksi.bdzinspection.daoservice.UserService;
import com.cnksi.bdzinspection.inter.XunshiDatabaseProvider;
import com.cnksi.bdzinspection.model.Users;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.DisplayUtil;
import com.cnksi.bdzinspection.utils.PlaySound;
import com.cnksi.bdzinspection.utils.TTSUtils;
import com.cnksi.bdzinspection.utils.UnCaughtExceptionUtils;
import com.cnksi.bdzinspection.utils.XZip;
import com.cnksi.ksynclib.model.App;
import com.cnksi.xscore.xsapplication.CoreApplication;
import com.cnksi.xscore.xsutils.CLog;
import com.cnksi.xscore.xsutils.CoreConfig;
import com.cnksi.xscore.xsutils.FileUtils;
import com.cnksi.xscore.xsutils.PreferencesUtils;
import com.cnksi.xscore.xsutils.crash.CrashHandler;
import com.cnksi.xscore.xsutils.crash.CrashReportUploadHandler;
import com.cnksi.xscore.xsutils.crash.ErrorLogBean;
import com.cnksi.xscore.xsutils.crash.SaveDbListener;
import com.cnksi.nari.model.BDPackage;
import com.cnksi.nari.utils.NariDataManager;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.zhy.core.config.AutoLayoutConifg;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class XunshiApplication {
    private static Application mInstance = null;
    private static Context context;
    private int size ;
    public static Application getInstance() {
        return mInstance;
    }
    private static DbUtils dbUtils;
  static   XunshiDatabaseProvider xunshiDatabaseProvider;
    public void init(Application application, Context context, XunshiDatabaseProvider databaseProvider) {
        if (mInstance == null) {
            mInstance =  application;
            this.context = context;
        }
        XunshiApplication.xunshiDatabaseProvider=databaseProvider;

        Config.COPY_MAX_DISTANCE = PreferencesUtils.getFloat(mInstance, Config.COPY_DISTANCE_KEY, Config.COPY_MAX_DISTANCE);
        NariDataManager.init(Config.NARI_BASEFOLDER);
        PreferencesUtils.init(context);
    }


    public synchronized static DbUtils getDbUtils() {
        if (dbUtils==null)  dbUtils=DbUtils.create(xunshiDatabaseProvider.getDatabase(),xunshiDatabaseProvider.getDbName());
        return dbUtils;
    }
    // 线程池
    private static ExecutorService mFixedThreadPoolExecutor = null;
    public static ExecutorService getFixedThreadPoolExecutor() {
        if (mFixedThreadPoolExecutor ==null){
            mFixedThreadPoolExecutor = Executors.newFixedThreadPool(3);
        }
        return mFixedThreadPoolExecutor;
    }


    public static Context getAppContext (){
        return context;
    }


}