package com.cnksi.bdzinspection.utils;


import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import com.cnksi.core.utils.CLog;

/**
 * Created by han on 2016/10/27.
 */

public class UnCaughtExceptionUtils implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "UnCaughtExceptionUtils";

    // 系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    // CrashHandler实例
    private static UnCaughtExceptionUtils instance;
    // 程序的Context对象
    private Context mContext;


    /**
     * 保证只有一个CrashHandler实例
     */
    private UnCaughtExceptionUtils() {
    }

    public synchronized static UnCaughtExceptionUtils getInstance() {
        if (null == instance) {
            instance = new UnCaughtExceptionUtils();
        }
        return instance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当未处理异常发生时在这里处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleExeption(thread, ex) && null != mDefaultHandler) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                CLog.e(TAG, e.toString());
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    private boolean handleExeption(Thread thread, Throwable ex) {
        if (null == ex) {
            return false;
        }
        submitException(thread, ex);
        return true;
    }

    private void submitException(Thread thread, Throwable ex) {
        //获取发生异常的界面
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = manager.getRunningTasks(1).get(0).topActivity;
        final String error = "ClassName:    " + cn.getClassName() + "\n " + "Thread:  " + thread + "\n " + "Name:  " + thread.getName() + "\n " + "Id:  " + thread.getId() + "Exception:    " + ex.toString();
        Log.e(TAG, error);
        ex.printStackTrace();
    }
}
