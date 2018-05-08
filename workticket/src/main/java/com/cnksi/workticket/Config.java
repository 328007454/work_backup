package com.cnksi.workticket;

import android.os.Environment;

import com.cnksi.core.utils.SDCardUtils;

/**
 * Created by Mr.K on 2018/4/23.
 */

public class Config {

    /**
     * 当前登录人员
     */
    public static final String CURRENT_LOGIN_USER = "current_login_user";
    public static String userName;
    /**
     * 当前登录人员帐号
     */
    public static final String CURRENT_LOGIN_ACCOUNT = "current_login_account";
    public static String userAccount;
    /**
     * 登陆部门
     */
    public static final String DEPARTMENT_ID = "current_department_id";
    public static String deptID;

    public static String CURRENT_DEPARTMENT_NAME = "current_department_name";
    public static String deptName;

    public final static String KEY_SYNC_URL = "key_sync_url";
    public final static String KEY_SYNC_APP_ID = "key_sync_app_id";


    /**
     * 手机存储目录
     */
    public static final String PHONE_DIRECTORY = Environment.getDataDirectory().getAbsolutePath();
    /**
     * 内置SD卡目录
     */
    public static final String SDCARD_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath();
    /**
     * 外置SD卡目录
     */
    public static final String EXTERNAL_SDCARD_DIRECTORY = null;
    public static final String BDZ_INSPECTION_FOLDER = (EXTERNAL_SDCARD_DIRECTORY != null ? EXTERNAL_SDCARD_DIRECTORY : ((SDCardUtils.isSDCardEnable() ? SDCARD_DIRECTORY : PHONE_DIRECTORY))) + "/BdzInspection/";
    public static final String LOG = BDZ_INSPECTION_FOLDER + "log/";

    public static final String DATABASE_NAME = "worketicket.db";
    public static final String DATABASE_FOLDER = "";
    public static String SYNC_APP_ID_VALUE = "xj_dev";
    public static String SYNC_URL_VALUE = "";
    public static final String CURRENT_DEPARTMENT_ID = "current_department_id";
}
