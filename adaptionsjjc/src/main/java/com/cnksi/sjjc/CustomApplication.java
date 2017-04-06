package com.cnksi.sjjc;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;

import com.baidu.mapapi.SDKInitializer;
import com.cnksi.core.application.CoreApplication;
import com.cnksi.core.common.ScreenManager;
import com.cnksi.core.utils.CLog;
import com.cnksi.core.utils.DisplayUtil;
import com.cnksi.core.utils.FileUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.crash.CrashHandler;
import com.cnksi.core.utils.crash.CrashReportUploadHandler;
import com.cnksi.sjjc.bean.HoleRecord;
import com.cnksi.sjjc.bean.PreventionRecord;
import com.cnksi.sjjc.bean.ReportCdbhcl;
import com.cnksi.sjjc.bean.ReportSnwsd;
import com.cnksi.sjjc.bean.TaskExtend;
import com.cnksi.sjjc.util.PlaySound;
import com.cnksi.sjjc.util.TTSUtils;
import com.cnksi.sjjc.util.XZip;
import com.tendcloud.tenddata.TCAgent;
import com.zhy.autolayout.config.AutoLayoutConifg;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

/**
 * @version 1.0
 * @auth luoxy
 * @date 16/4/20
 */
public class CustomApplication extends CoreApplication {
    private String[] filePathArray = {
            Config.BDZ_INSPECTION_FOLDER,
            Config.DATABASE_FOLDER,
            Config.SIGN_PICTURE_FOLDER,
            Config.VIDEO_FOLDER,
            Config.UPLOAD_DATABASE_FOLDER,
            Config.LOGFOLDER,
            Config.DOWNLOAD_APP_FOLDER,
            //Config.PICTURES_FOLDER,
            //Config.RESULT_PICTURES_FOLDER,
            Config.AUDIO_FOLDER,
            Config.BAK_FOLDER,
            Config.NFC_FOLDER,
            Config.WWWROOT_FOLDER};

    //数据库管理者
    private static DbManager mDbManager = null;
    private static DbManager PJDbManager = null;
    private static DbManager yanShouDbManager = null;


    public HashMap<String, String> getCopyedMap() {
        if (copyedMap == null)
            copyedMap = new HashMap<>();
        return copyedMap;
    }


    private HashMap<String, String> copyedMap = new HashMap<>();

    public static DbManager getPJDbManager() {
        if (PJDbManager == null) {
            PJDbManager = x.getDb(new DbManager.DaoConfig().setDbDir(new File(Config.DATABASE_FOLDER)).setDbName("evaluation.db").setDbVersion(1));
        }
        return PJDbManager;
    }

    public static CustomApplication getInstance() {
        return mInstance;
    }

    private static CustomApplication mInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();
//        if(LeakCanary.isInAnalyzerProcess(this)){
//            return;
//        }
//        LeakCanary.install(this);

        //初始化百度地图
        SDKInitializer.initialize(getApplicationContext());

        mInstance = this;
        AutoLayoutConifg.getInstance().useDeviceSize().init(this);
        TCAgent.LOG_ON = true;
        TCAgent.init(this, "70961CDA8A5045B89CB4215349CA8A78", "内部测试");
        TCAgent.setReportUncaughtExceptions(true);
        // 应用程序入口处调用,避免手机内存过小，杀死后台进程,造成SpeechUtility对象为null
        // 注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
        // 参数间使用“,”分隔。
        // 设置你申请的应用appid
//        StringBuffer param = new StringBuffer();
//        param.append("appid=" + getString(R.string.app_id));
//        param.append(",");
//        // 设置使用v5+
//        param.append(SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC);
//        SpeechUtility.createUtility(CustomApplication.this, param.toString());
        DisplayUtil.getInstance().setStandHeight(1920).setStandWidth(1080).init(getApplicationContext());
        CLog.init(true);
        PlaySound.initPlay(this);
        CrashReportUploadHandler.init(mInstance, Config.LOGFOLDER).start();
        if (PreferencesUtils.getBoolean(this, Config.MASK_WIFI, true) && !BuildConfig.USE_NETWORK_SYNC) {
            com.cnksi.core.utils.NetWorkUtil.disableNetWork(this);
        }
        initRuntimeVar();
        TTSUtils.init(getAppContext());
    }

    public void initApp() {
        FileUtils.initFile(filePathArray);
        copyAssetsToSDCard();
        TCAgent.onError(mInstance, CrashHandler.getInstance().getUncaughtException());
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

    public static void closeDbConnection() {
        if (mDbManager != null) {
            try {
                FileUtils.deleteAllFiles(new File(Config.DATABASE_FOLDER + Config.DATABASE_NAME + "-journal"));
                mDbManager.close();
                mDbManager = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 得到验收数据库管理
     */
    public static DbManager getYanShouDbManager() {
        if (yanShouDbManager == null) {
            yanShouDbManager = x.getDb(new DbManager.DaoConfig().setDbDir(new File(Config.DATABASE_FOLDER)).setDbName(Config.YANSHOUEDATABASE_NAME).setDbVersion(8)
                    .setDbOpenListener(new DbManager.DbOpenListener() {
                        @Override
                        public void onDbOpened(DbManager db) {
                            // 开启WAL, 对写入加速提升巨大
                            ///  db.getDatabase().enableWriteAheadLogging();
                        }
                    })
                    .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                        @Override
                        public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                            // TODO: ...
                            // db.addColumn(...);
                            // db.dropTable(...);
                            // ...
                            // or
                            // db.dropDb();
                        }
                    }));
        }
        return yanShouDbManager;
    }

    public void restartApp() {
        ScreenManager.getScreenManager().popAllActivityExceptOne(null);
        Intent intent = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mInstance.startActivity(intent);
        ActivityManager mActivityManager = (ActivityManager)
                this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo>
                mRunningProcess = mActivityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo amProcess :
                mRunningProcess) {
            if (amProcess.processName.equals("com.cnksi.bdzinspection")) {
                android.os.Process.killProcess(amProcess.pid);
                mActivityManager.killBackgroundProcesses(amProcess.processName);
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前

    }

    /**
     * 自定义数据库配置 需要重写
     *
     * @return
     */

    protected static DbManager.DaoConfig getDaoConfig() {
        DbManager.DaoConfig config = new DbManager.DaoConfig().setDbDir(new File(Config.DATABASE_FOLDER)).setDbName(Config.DATABASE_NAME).setDbVersion(5)
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        // 开启WAL, 对写入加速提升巨大
                        //db.getDatabase().enableWriteAheadLogging();
                        //此处不处理数据库版本更新  全权交给同步框架处理。
                        db.getDaoConfig().setDbVersion(db.getDatabase().getVersion());
                    }
                })
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        try {
                            db.addColumn(HoleRecord.class, "problem");
                            db.addColumn(PreventionRecord.class, "clear_info");
                            db.addColumn(PreventionRecord.class, "mousetrap_info");
                            db.addColumn(ReportCdbhcl.class, "dclz_a");
                            db.addColumn(ReportCdbhcl.class, "dclz_b");
                            db.addColumn(ReportCdbhcl.class, "dclz_c");
                            db.addColumn(ReportCdbhcl.class, "dclz_o");
                            db.addColumn(ReportSnwsd.class, "location");
                            db.addColumn(TaskExtend.class, "dlt");
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                }).setAllowTransaction(true);
        return config;
    }

    /**
     * 获得显示大图的图像配置，大图不适合内存缓存
     *
     * @return
     */
    public static ImageOptions getLargeImageOptions() {
        return new ImageOptions.Builder()
                .setSize(0, 0).setUseMemCache(false).setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                // .setRadius(DensityUtil.dip2px(5))
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                //  .setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                // .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                //    .setLoadingDrawableId(R.mipmap.ic_default_pic)
                .setFailureDrawableId(R.mipmap.ic_default_pic)
                .build();
    }

    public static ImageOptions getImageOPtions() {
        return new ImageOptions.Builder()
                .setSize(0, 0)
                //               .setRadius(DensityUtil.dip2px(5))
//                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                //设置加载过程中的图片
                .setLoadingDrawableId(R.mipmap.ic_default_pic)
                //设置加载失败后的图片
                .setFailureDrawableId(R.mipmap.ic_default_pic)
                //设置使用缓存
                .setUseMemCache(false)
                //设置支持gif
                .setIgnoreGif(true)
                //设置显示圆形图片
//      .setCircular(false)
                .build();
    }

    private static int DataVersion = 13;

    private void copyAssetsToSDCard() {
        if (PreferencesUtils.getInt(mInstance, "DataVersion", 0) < DataVersion)
            mExcutorService.execute(new Runnable() {
                @Override
                public void run() {
                    delAllFile(Config.WWWROOT_FOLDER);
                    if (copyAssetsToSDCard(mInstance, "www", Config.WWWROOT_FOLDER)) {
                        PreferencesUtils.put(mInstance, "DataVersion", DataVersion);
                        try {
                            XZip.UnZipFolder(Config.WWWROOT_FOLDER + "www.zip", Config.WWWROOT_FOLDER);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    copyAssetsToSDCard(mInstance, "database", Config.DATABASE_FOLDER);
                }
            });
    }


    /**
     * 递归拷贝assets文件到SD卡
     *
     * @param context
     * @param assetDir
     * @param dir
     */
    public static boolean copyAssetsToSDCard(Context context, String assetDir, String dir) {
        boolean isSuccess = false;
        String[] files = null;
        try {
            files = context.getResources().getAssets().list(assetDir);
        } catch (IOException e1) {
            return isSuccess;
        }
        File mWorkingPath = new File(dir);
        if (!mWorkingPath.exists()) {
            if (!mWorkingPath.mkdirs()) {
                CLog.e("--CopyAssets--", "cannot create directory.");
            }
        }
        if (files != null) {
            for (String fileName : files) {
                try {
                    File file = new File(fileName);
                    if (file.isDirectory()) {
                        if (0 == assetDir.length()) {
                            copyAssetsToSDCard(context, fileName, dir + fileName + "/");
                        } else {
                            copyAssetsToSDCard(context, assetDir + "/" + fileName, dir + fileName + "/");
                        }
                        continue;
                    }
                    File outFile = new File(mWorkingPath, fileName);
                    if (outFile.exists())
                        outFile.delete();
                    InputStream in = null;
                    if (0 != assetDir.length())
                        in = context.getAssets().open(assetDir + "/" + fileName);
                    else
                        in = context.getAssets().open(fileName);
                    OutputStream out = new FileOutputStream(outFile);
                    byte[] buf = new byte[2048];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                    isSuccess = true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    isSuccess = false;
                } catch (IOException e) {
                    e.printStackTrace();
                    isSuccess = false;
                }
            }
        }
        return isSuccess;
    }

    public boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
//              delFolder(path + "/" + tempList[i]);// 再删除空文件夹
                flag = true;
            }
        }
        return flag;
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
        Config.SYNC_URL = PreferencesUtils.getString(mInstance, Config.KEY_SYNC_URL, Config.SYNC_URL);
    }


}
