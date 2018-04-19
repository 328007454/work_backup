package com.cnksi.sjjc;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;

import com.cnksi.DebugDB;
import com.cnksi.bdloc.LLog;
import com.cnksi.bdloc.LocationUtil;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.inter.XunshiDatabaseProvider;
import com.cnksi.core.application.CoreApplication;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.common.ScreenManager;
import com.cnksi.core.utils.CLog;
import com.cnksi.core.utils.DisplayUtils;
import com.cnksi.core.utils.FileUtils;
import com.cnksi.core.utils.NetWorkUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.utils.crash.CrashReportUploadHandler;
import com.cnksi.sjjc.bean.TaskExtend;
import com.cnksi.sjjc.util.EssSafeUtil;
import com.cnksi.sjjc.util.PlaySound;
import com.cnksi.sjjc.util.TTSUtils;
import com.cnksi.sjjc.util.XZip;
import com.squareup.leakcanary.LeakCanary;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * @version 1.0
 * @auth luoxy
 * @date 16/4/20
 */
public class CustomApplication extends CoreApplication {
    //数据库管理者
    private static DbManager mDbManager = null;
    private static DbManager PJDbManager = null;
    private static DbManager yanShouDbManager = null;
    private static CustomApplication mInstance = null;
    private static int DataVersion = 13;
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
    private HashMap<String, String> copyedMap = new HashMap<>();
    XunshiApplication xunshiApplication;
    private String innerDateBaseFolder ;

    public static DbManager getPJDbManager() {
        if (PJDbManager == null) {
            PJDbManager = x.getDb(new DbManager.DaoConfig().setDbDir(new File(Config.DATABASE_FOLDER)).setDbName("evaluation.db").setDbVersion(1));
        }
        return PJDbManager;
    }

    public static CustomApplication getInstance() {
        return mInstance;
    }

    /**
     * 得到数据库管理者
     *
     * @return
     */
    public DbManager getDbManager() {
//        ||!mDbManager.getDatabase().isOpen()
        if (mDbManager == null) {
            mDbManager = x.getDb(getDaoConfig());
        }
        return mDbManager;
    }


    public static void closeDbConnection() {
//        if (mDbManager != null) {
//            try {
//                FileUtils.deleteAllFiles(new File(Config.DATABASE_FOLDER + Config.DATABASE_NAME + "-journal"));
//                mDbManager.close();
//                mDbManager = null;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
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

    /**
     * 自定义数据库配置 需要重写
     *
     * @return
     */
    DbManager.DaoConfig config;

    public DbManager.DaoConfig getDaoConfig() {
        int dbVersion = getDbVersion();
        innerDateBaseFolder = XunshiApplication.getAppContext().getFilesDir().getAbsolutePath() + "/database/";
        if (config == null) {
            config = new DbManager.DaoConfig().setDbDir(new File(innerDateBaseFolder)).setDbName(Config.ENCRYPT_DATABASE_NAME).setDbVersion(dbVersion)
                    .setDbOpenListener(db -> {
                        // 开启WAL, 对写入加速提升巨大
                        //db.getDatabase().enableWriteAheadLogging();
                        //此处不处理数据库版本更新  全权交给同步框架处理。
                        try {
                            db.addColumn(TaskExtend.class, "dlt");
                        } catch (DbException e) {

                        }
                    })
                    .setUseEncrypt(true)
                    .setKey("com.cnksi")
                    .setDbUpgradeListener((db, oldVersion, newVersion) -> {}).setAllowTransaction(true);
            return config;
        } else
            return config;
    }

    /**
     * 获得显示大图的图像配置，大图不适合内存缓存
     *
     * @return
     */
//    public static ImageOptions getLargeImageOptions() {
//        return new ImageOptions.Builder()
//                .setSize(0, 0).setUseMemCache(false).setImageScaleType(ImageView.ScaleType.CENTER_CROP)
//                // .setRadius(DensityUtil.dip2px(5))
//                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
//                //  .setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
//                // 加载中或错误图片的ScaleType
//                //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
//                // .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
//                //    .setLoadingDrawableId(R.mipmap.ic_default_pic)
//                .setFailureDrawableId(R.mipmap.ic_default_pic)
//                .build();
//    }

//    public static ImageOptions getImageOPtions() {
//        return new ImageOptions.Builder()
//                .setSize(0, 0)
//                //               .setRadius(DensityUtil.dip2px(5))
////                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
//                //设置加载过程中的图片
//                .setLoadingDrawableId(R.mipmap.ic_default_pic)
//                //设置加载失败后的图片
//                .setFailureDrawableId(R.mipmap.ic_default_pic)
//                //设置使用缓存
//                .setUseMemCache(false)
//                //设置支持gif
//                .setIgnoreGif(true)
//                //设置显示圆形图片
////      .setCircular(false)
//                .build();
//    }

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
                    if (outFile.exists()) {
                        outFile.delete();
                    }
                    InputStream in = null;
                    if (0 != assetDir.length()) {
                        in = context.getAssets().open(assetDir + "/" + fileName);
                    } else {
                        in = context.getAssets().open(fileName);
                    }
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

    /**
     * 从SDcard的配置文件中载入DbVersion  为了避免每次都要同步表结构的问题。
     *
     * @return
     */
    private static int getDbVersion() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(Config.DATABASE_FOLDER + "dbVersion.prop"));
            return Integer.parseInt(properties.getProperty("dbVersion", "1"));
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * 将数据库版本保存到SDCARD上
     *
     * @param dbVersion
     */
    public static void saveDbVersion(int dbVersion) {
        Properties properties = new Properties();
        try {
            properties.put("dbVersion", dbVersion + "");
            properties.store(new FileOutputStream(Config.DATABASE_FOLDER + "dbVersion.prop"), "数据库版本");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, String> getCopyedMap() {
        if (copyedMap == null) {
            copyedMap = new HashMap<>();
        }
        return copyedMap;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        LeakCanary.install(mInstance);

        PreferencesUtils.init(getApplicationContext());
        ToastUtils.init(getApplicationContext());
        DisplayUtils.getInstance().setStandHeight(1920).setStandWidth(1080).init(getApplicationContext());
        CLog.init(true);
        PlaySound.initPlay(this);
        CrashReportUploadHandler.init(mInstance, Config.LOGFOLDER).start();
        if (PreferencesUtils.get(Config.MASK_WIFI, true) && !BuildConfig.USE_NETWORK_SYNC) {
            NetWorkUtils.disableNetWork(this);
        }
        EssSafeUtil.checkInEmualtorOrDebuggable(getApplicationContext());
        initRuntimeVar();
        TTSUtils.init(getAppContext());
        LocationUtil.init(getAppContext());
        LLog.isLog = BuildConfig.LOG_DEBUG;
        initDebugDb();
        xunshiApplication = new XunshiApplication();
        xunshiApplication.init(mInstance, getApplicationContext(), new XunshiDatabaseProvider() {

            @Override
            public Object getDatabase() {
                return getDbManager().getDatabase();
            }

            @Override
            public String getDbName() {
                return getDaoConfig().getDbName();
            }
        });
    }

    private void initDebugDb() {
        if (BuildConfig.DEBUG) {
            HashMap<String, Pair<File, String>> stringPairHashMap = new HashMap<>();
            stringPairHashMap.put("bdzinspection", new Pair<>(new File(Config.DATABASE_FOLDER, Config.DATABASE_NAME), ""));
            DebugDB.setCustomDatabaseFiles(stringPairHashMap);
        }
    }


    public void initApp() {
        FileUtils.makeDirectory(filePathArray);
        copyAssetsToSDCard();
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

    private void copyAssetsToSDCard() {
        if (PreferencesUtils.get("DataVersion", 0) < DataVersion) {
            ExecutorManager.executeTaskSerially(() -> {
                if (BuildConfig.HAS_WEB_ASSETS) {
                    delAllFile(Config.WWWROOT_FOLDER);
                    if (copyAssetsToSDCard(mInstance, "src/main/assets-web/www", Config.WWWROOT_FOLDER)) {
                        try {
                            XZip.UnZipFolder(Config.WWWROOT_FOLDER + "www.zip", Config.WWWROOT_FOLDER);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    copyAssetsToSDCard(mInstance, "database", Config.DATABASE_FOLDER);
                }
                PreferencesUtils.put("DataVersion", DataVersion);
            });
        }
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
        Config.SYNC_URL = PreferencesUtils.get(Config.KEY_SYNC_URL, Config.SYNC_URL);
        Config.SYNC_APP_ID = PreferencesUtils.get(Config.KEY_SYNC_APP_ID, Config.SYNC_APP_ID);
    }


}
