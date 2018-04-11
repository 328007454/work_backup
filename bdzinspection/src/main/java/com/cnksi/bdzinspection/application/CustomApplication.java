package com.cnksi.bdzinspection.application;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.view.WindowManager;

import com.cnksi.bdloc.LLog;
import com.cnksi.bdloc.LocationUtil;
import com.cnksi.bdzinspection.daoservice.UserService;
import com.cnksi.bdzinspection.model.Users;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.DisplayUtil;
import com.cnksi.bdzinspection.utils.PlaySound;
import com.cnksi.bdzinspection.utils.TTSUtils;
import com.cnksi.bdzinspection.utils.UnCaughtExceptionUtils;
import com.cnksi.bdzinspection.utils.XZip;
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

public class CustomApplication extends CoreApplication implements SaveDbListener {
    private static CustomApplication mInstance = null;
    /**
     * 工器具零时状态保存
     */
    private Map<String, Map<Integer, Boolean>> gqjcheck = null;
    private Users one, two;
    // 需要创建的文件夹
    private String[] filePathArray = {Config.BDZ_INSPECTION_FOLDER, Config.DATABASE_FOLDER, Config.SIGN_PICTURE_FOLDER, Config.VIDEO_FOLDER, Config.UPLOAD_DATABASE_FOLDER, Config.LOGFOLDER, Config.DOWNLOAD_APP_FOLDER, Config.DEFALUTFOLDER,
            // Config.PICTURES_FOLDER,
            // Config.RESULT_PICTURES_FOLDER,
            // Config.CUSTOMER_PICTURES_FOLDER,
            Config.AUDIO_FOLDER, Config.BAK_FOLDER, Config.NFC_FOLDER};
    // 创建全局变量 全局变量一般都比较倾向于创建一个单独的数据类文件，并使用static静态变量
    private WindowManager.LayoutParams mWindowLayoutParams = new WindowManager.LayoutParams();

    public static CustomApplication getInstance() {
        return mInstance;
    }

    public synchronized static DbUtils getDbUtils() {

//        if (mDbUtils == null) {
//            final DbUtils.DaoConfig config = new DbUtils.DaoConfig(mInstance);
//            config.setDbDir(Config.DATABASE_FOLDER);
//            config.setDbName(Config.DATABASE_NAME);
//            config.setDbVersion(getDbVersion());
//            config.setDbVersion(100);
//            // config.setPassword("cnksi.com");
//            config.setDbUpgradeListener((db, oldVersion, newVersion) -> {
//                try {
//                    db.addColumn(BDPackage.class, "start_time", "varchar");
//                } catch (DbException e) {
//                    e.printStackTrace();
//                }
//            });
//            mDbUtils = DbUtils.create(config);
//            mDbUtils.configAllowTransaction(true);
//            mDbUtils.configDebug(false);
//        }
        return DbUtils.create(getAppContext(),Config.DATABASE_FOLDER,Config.DATABASE_NAME);
    }

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



    @Override
    public void onCreate() {
        super.onCreate();
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return;
//        }
//        LeakCanary.install(this);
        AutoLayoutConifg.getInstance().useDeviceSize();
        UnCaughtExceptionUtils.getInstance().init(this);
        mInstance = this;
        // 初始化系统
        initFileSystem(filePathArray);
        // 加载声音资源
        PlaySound.getIntance(mInstance);
        copyAssetsToSDCard();
        // 错误日志收集
        CrashReportUploadHandler.init(mInstance, Config.LOGFOLDER).start();
        CrashHandler.getInstance().setsListener(this);
        // CLog.isDebug = true;

        CLog.init(true, "BdzInspection");
        DisplayUtil.getInstance().setStandHeight(1920).setStandWidth(1080).init(getApplicationContext());
        //初始化TTS语音服务
        TTSUtils.init(getAppContext());
        LocationUtil.init(mInstance);
        LLog.isLog = true;
        NariDataManager.init(Config.NARI_BASEFOLDER);
        Config.COPY_MAX_DISTANCE = PreferencesUtils.getFloat(getAppContext(), Config.COPY_DISTANCE_KEY, Config.COPY_MAX_DISTANCE);
    }

    public void setDbUtilsNull() {
//        if (mDbUtils != null) {
//            mDbUtils.close();
//        }
//        mDbUtils = null;
    }

    public WindowManager.LayoutParams getWindowManagerParams() {
        return mWindowLayoutParams;
    }

    /**
     * 拷贝参考图片到sdcard
     */
    private void copyAssetsToSDCard() {
        if (PreferencesUtils.getBoolean(mInstance, "isFirstLoad", true)) {
            FileUtils.copyAssetsToSDCard(mInstance, "reference_photo", Config.DEFALUTFOLDER);
            // Auto init database
            // FileUtils.copyAssetsToSDCard(mInstance, "database",
            // Config.DATABASE_FOLDER);
            // Unzip();
            PreferencesUtils.put(mInstance, "isFirstLoad", false);
        }
    }

    /**
     * @return the gqjcheck
     */
    public Map<String, Map<Integer, Boolean>> getGqjcheck() {
        if (gqjcheck == null) {
            gqjcheck = new HashMap<>();
        }
        return gqjcheck;
    }

    private void Unzip() {
        try {
            XZip.UnZipFolder(Config.DATABASE_FOLDER + File.separator + "bdzinspection.zip", Config.DATABASE_FOLDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void saveErrorDb(ErrorLogBean bean) {
//        try {
//            bean.id = Config.CURRENT_AREA + "-" + FunctionUtils.getPrimarykey();
//            getDbUtils().createTableIfNotExist(ErrorLogBean.class);
//            getDbUtils().save(bean);
//
//        } catch (DbException e) {
//            // e.printStackTrace();
//        }
    }

    public Users getUsers() {
        if (one != null) {
            return one;
        }
        if (two != null) {
            return two;
        } else {
            String[] account = PreferencesUtils.getString(this, Users.ACCOUNT, "").split(CoreConfig.COMMA_SEPARATOR);
            for (int i = 0; i < account.length; i++) {
                Users _t = null;
                if (!TextUtils.isEmpty(account[i])) {
                    {
                        _t = UserService.getInstance().findUserByAccount(account[i]);
                    }
                }
                if (one != null) {
                    two = _t;
                } else {
                    one = _t;
                }
            }
        }
        return one;
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
}