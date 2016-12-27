package com.cnksi.core.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.cnksi.core.R;
import com.cnksi.core.okhttp.OkHttpUtils;
import com.cnksi.core.okhttp.callback.FileCallBack;
import com.cnksi.core.utils.AppUtils;
import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.CoreConfig;
import com.cnksi.core.utils.FileUtils;
import com.cnksi.core.utils.IntentUtils;
import com.cnksi.core.utils.StringUtils;

import java.io.File;

import okhttp3.Call;
import okhttp3.Request;

/**
 * 专用下载APK文件Service工具类,通知栏显示进度,下载完成震动提示,并自动打开安装界面(配合xUtils快速开发框架) 需要添加权限：<br>
 * <uses-permission android:name="android.permission.INTERNET" /><br>
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" /><br>
 * <uses-permission android:name="android.permission.VIBRATE" /> <br>
 * 需要在<application></application>标签下注册服务<br>
 * 可以在142行代码：builder.setSmallIcon(R.drawable.ic_launcher);中修改自己应用的图标<br>
 */
public class DownLoadAPKService extends Service {

    private Context mContext = null;
    private final int NotificationID = 0x10000;
    private NotificationManager mNotificationManager = null;
    private NotificationCompat.Builder builder;
    private String mDownLoadUrl = "", mDownloadFileName = "", mDownloadFileFolder = "";
    private boolean isDownloadApk = true;
    private long currentUpdateTime = 0l;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            mDownloadFileName = intent.getStringExtra(CoreConfig.DOWNLOAD_FILE_NAME_KEY);
            mDownloadFileFolder = intent.getStringExtra(CoreConfig.DOWNLOAD_FILE_FOLDER_KEY);
            mDownLoadUrl = intent.getStringExtra(CoreConfig.DOWNLOAD_APK_URL_KEY);
            isDownloadApk = intent.getBooleanExtra(CoreConfig.IS_DOWNLOAD_APK, true);
            if (TextUtils.isEmpty(mDownLoadUrl) || !TextUtils.isEmpty(mDownloadFileName)) {
                if (!isDownloadApk && FileUtils.isFileExists(mDownloadFileFolder + mDownloadFileName)) {
                    IntentUtils.openDocumentFile(mContext, mDownloadFileFolder + mDownloadFileName);
                } else {
                    downLoadFile(mDownLoadUrl, mDownloadFileFolder, mDownloadFileName);
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void downLoadFile(String downloadUrl, String targetFolder, String target_name) {

        /**
         * OkHttp下载文件
         */
        OkHttpUtils.get().url(downloadUrl).tag(this).build().execute(new FileCallBack(targetFolder, target_name) {

            @Override
            public void onBefore(Request request) {
                super.onBefore(request);
                mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                builder = new NotificationCompat.Builder(mContext);
                builder.setSmallIcon(R.mipmap.ic_notification_app);
                builder.setTicker(mContext.getResources().getString(isDownloadApk ? R.string.download_tiker_str : R.string.download_file_str));
                builder.setContentTitle(AppUtils.getAppName(mContext));
                builder.setContentText(mContext.getResources().getString(R.string.download_loading_str));
                builder.setNumber(0);
                builder.setAutoCancel(true);
                mNotificationManager.notify(NotificationID, builder.build());
            }

            @Override
            public void inProgress(final long total, final long current) {
                int x = Long.valueOf(current).intValue();
                int totalS = Long.valueOf(total).intValue();
                builder.setProgress(totalS, x, false);
                builder.setContentInfo(StringUtils.getPercent(x, totalS));
                if (System.currentTimeMillis() - currentUpdateTime > 1000 || x >= totalS) {
                    currentUpdateTime = System.currentTimeMillis();
                    mNotificationManager.notify(NotificationID, builder.build());
                }
            }

            @Override
            public void onError(Call call, Exception e) {
                mNotificationManager.cancel(NotificationID);
                CToast.showShort(mContext, mContext.getText(R.string.download_failure_check_network_str));
                stopSelf();
            }

            @Override
            public void onResponse(File file) {
                if (isDownloadApk) {
                    if (AppUtils.verSignature(mContext, file.getAbsolutePath())) {
                        Intent installIntent = new Intent(Intent.ACTION_VIEW);
                        Uri uri = Uri.fromFile(file);
                        installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
                        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        PendingIntent mPendingIntent = PendingIntent.getActivity(DownLoadAPKService.this, 0, installIntent, 0);
                        builder.setContentText(mContext.getText(R.string.download_complete_click_install_str));
                        builder.setContentIntent(mPendingIntent);
                        mNotificationManager.notify(NotificationID, builder.build());
                        // 震动提示
                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(100L);// 参数是震动时间(long类型)
                        startActivity(installIntent);// 下载完成之后自动弹出安装界面
                    }
                } else {
                    IntentUtils.openDocumentFile(mContext, mDownloadFileFolder + mDownloadFileName);
                }
                stopSelf();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
        stopSelf();
    }
}
