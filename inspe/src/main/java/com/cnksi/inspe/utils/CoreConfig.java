package com.cnksi.inspe.utils;

import android.os.Environment;

/**
 * Created by kkk on 2018/3/19.
 *
 * @Deprecated sjjc拷贝，解决依赖问题，后可以根据需要删除
 */
@Deprecated
public class CoreConfig {

    /**
     * 手机存储目录
     */
    public static final String PHONE_DIRECTORY = Environment.getDataDirectory().getAbsolutePath();
    public static final String SDCARD_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath();
    /**
     * 音频后缀
     */
    public static final String AAC_POSTFIX = ".aac";
    /**
     * jpg图片后缀名 .jpg
     */
    public final static String IMAGE_JPG_POSTFIX = ".jpg";
    /**
     * png图片后缀名 .png
     */
    public final static String IMAGE_PNG_POSTFIX = ".png";
    /**
     * 日志后缀名 .txt
     */
    public final static String TXT_POSTFIX = ".txt";
    /**
     * apk后缀名 .apk
     */
    public final static String APK_POSTFIX = ".apk";
    /**
     * 服务器地址 http://ver.cnksi.com.cn
     */
    public static final String SERVER_URL = "http://ver.cnksi.com.cn";
    /**
     * 更新的url /api/client
     */
    public static final String UPDATE_URL = SERVER_URL + "/api/client";
    /**
     * 错误日志上传地址
     */
    public static final String CRASHLOG_UPLOAD_URL = SERVER_URL + "/elog";
    /**
     * 激活状态 可用状态 Y
     */
    public static final String ACTIVED = "Y";
    /**
     * 逗号分隔符 ,
     */
    public static final String COMMA_SEPARATOR = ",";
    /**
     * 短横线分隔符 -
     */
    public static final String DASH_SEPARATOR = "-";
    /**
     * 竖线 |
     */
    public static final String VERTICAL_SEPARATOR = "|";
    /**
     * 竖线转义字符 \\|
     */
    public static final String VERTICAL_SEPARATOR_ESCAPE = "\\|";
    /**
     * 下一行 \n
     */
    public static final String NEXT_LINE_SEPARATOR = "\n";
    /**
     * 多种符号分隔符 ，|,|、|；|;
     */
    public static final String MUTI_SYMBOL_SEPARATOR = "，|,|、|；|;";
    /**
     * yyyy-MM-dd
     */
    public static final String dateFormat1 = "yyyy-MM-dd";
    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static final String dateFormat2 = "yyyy-MM-dd HH:mm:ss";
    /**
     * yyyy-MM-dd-HH-mm-ss
     */
    public static final String dateFormat3 = "yyyy-MM-dd-HH-mm-ss";
    /**
     * yyyy/MM/dd HH:mm:ss
     */
    public static final String dateFormat4 = "yyyy/MM/dd HH:mm:ss";
    /**
     * MM-dd,HH:mm
     */
    public static final String dateFormat5 = "MM-dd,HH:mm";
    /**
     * yyyyMMddHHmmssSSS
     */
    public static final String dateFormat6 = "yyyyMMddHHmmssSSS";
    /**
     * yyyyMMdd_HHmmss
     */
    public static final String dateFormat7 = "yyyyMMdd_HHmmss";
    /**
     * yyyy-MM-dd HH:mm
     */
    public static final String dateFormat8 = "yyyy-MM-dd HH:mm";
    /**
     * yyyy-MM-dd HH:mm
     */
    public static final String dateFormat9 = "MM-dd HH:mm";
    /**
     * HHmmssSSS
     */
    public static final String dateFormat10 = "ssSSS";
    /**
     * yyyyMMdd_HHmmssSSS
     */
    public static final String dateFormat11 = "yyyyMMdd_HHmmssSSS";
    /**
     * yyyy-MM-dd HH:mm
     */
    public static final String dateFormat12 = "yyyy.MM.dd HH:mm";
    /**
     * sharedPreference name config
     */
    public static final String SHARED_PREFERENCE_NAME = "config";
    /**
     * 注册日期
     */
    public static final String REGISTERED_DATE = "registered_date";
    /**
     * 文件下载名称
     */
    public static final String DOWNLOAD_FILE_NAME_KEY = "download_file_name";
    /**
     * 是否是下载apk
     */
    public static final String IS_DOWNLOAD_APK = "is_download_apk";
    /**
     * 文件下载存放路径
     */
    public static final String DOWNLOAD_FILE_FOLDER_KEY = "download_file_folder";
    /**
     * 文件下载存放文件夹
     */
    public static final String DOWNLOAD_APK_URL_KEY = "download_apk_url";
    /**
     * VPNService包名
     */
    public static final String OPEN_VPN_PACKAGE_NAME = "de.blinkt.openvpn";
    /**
     *
     */
    public static final String PROGRAM_APP_CODE = "com.cnksi.core.PROGRAM_CODE";

    /**
     * ZBar
     */
    public static final int auto_focus = 111;
    public static final int decode = 112;
    public static final int decode_failed = 113;
    public static final int decode_succeeded = 114;
    public static final int restart_preview = 115;
    public static final int quit = 116;
    public static final String ZBAR_RESULT = "zbar_result";

    /**
     * 第一次加载数据
     */
    public static final int LOAD_DATA = 0x1;
    /**
     * 刷新数据
     */
    public static final int REFRESH_DATA = 0x2;
    /**
     * 保存数据
     */
    public static final int SAVE_DATA = 0x3;
    /**
     * 请求失败
     */
    public static final int ERROR_DATA = 0x4;
    /**
     * Session过期
     */
    public static final int OVER_TIME_DATA = 0x5;
    /**
     * 启动照相请求
     */
    public static final int ACTION_IMAGE = 0x6;
    /**
     * 网络不可用
     */
    public static final int NETWORK_UNVISIBLE = 0x7;
    /**
     * 加载更多
     */
    public static final int LOAD_MORE_DATA = 0x8;
    /**
     * 上传进度条
     */
    public static final int UPDATE_PROGRESS_BAR = 0x9;
    /**
     * 取消选择的图片
     */
    public static final int CANCEL_RESULT_LOAD_IMAGE = 0x10;
    /**
     * 裁剪图片
     */
    public static final int CROP_PICTURE = 0x11;
    public static final int UPLOAD_DEVICE_INFOR_CODE = CROP_PICTURE + 1;
    public static final int UPDATE_APP_CODE = UPLOAD_DEVICE_INFOR_CODE + 1;
    public static final int INSTALL_APP_CODE = UPDATE_APP_CODE + 1;
    /**
     * VPN成功返回
     */
    public static final int ICS_OPENVPN_PERMISSION = INSTALL_APP_CODE + 1;
    public static final int ICS_OPENVPN_MESSAGE_CODE = ICS_OPENVPN_PERMISSION + 1;
    public static final int START_PROFILE_BYUUID = ICS_OPENVPN_MESSAGE_CODE + 1;
}