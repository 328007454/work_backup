package com.cnksi.workticket;

import android.os.Environment;

import com.cnksi.core.utils.SDCardUtils;

/**
 * @author Mr.K  on 2018/4/23.
 * @decrption 相关配置信息
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
     * 部门id 存储作为静态变量
     */
    public static String deptID;

    /**
     * 部门名字
     */
    public static String CURRENT_DEPARTMENT_NAME = "current_department_name";
    /**
     * 存储当前部门名字共享
     */
    public static String deptName;

    /**
     * 同步地址url
     */
    public final static String KEY_SYNC_URL = "key_sync_url";
    /**
     * 同步appid
     */
    public final static String KEY_SYNC_APP_ID = "key_sync_app_id";

    /**
     * 是否外来班组key
     */
    public static final String OTHER_DEPT_USER = "other_dept_user";
    /**
     * 成员角色
     */
    public static String otherDeptUser = "";

    /**
     * 手机存储目录
     */
    private static final String PHONE_DIRECTORY = Environment.getDataDirectory().getAbsolutePath();
    /**
     * 内置SD卡目录
     */
    private static final String SDCARD_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath();
    /**
     * bdzinspection 文件夹
     */
    public static final String BDZ_INSPECTION_FOLDER = ((SDCardUtils.isSDCardEnable() ? SDCARD_DIRECTORY : PHONE_DIRECTORY)) + "/BdzInspection/";

    /**
     * 数据库的名字
     */
    public static final String DATABASE_NAME = "worketicket.db";
    /**
     * 数据库所在文件夹
     */
    public static String DATABASE_FOLDER = "";
    /**
     * 存储appid
     */
    public static String SYNC_APP_ID_VALUE = "xj_dev";
    /**
     * 存储同步地址链接
     */
    public static String SYNC_URL_VALUE = "";
    /**
     * 部门id   key
     */
    public static final String CURRENT_DEPARTMENT_ID = "current_department_id";
}
