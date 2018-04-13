package com.cnksi.xscore.xsutils;

public class CoreConfig {

	/** jpg图片后缀名 .jpg */
	public final static String IMAGE_JPG_POSTFIX = ".jpg";
	/** png图片后缀名 .png */
	public final static String IMAGE_PNG_POSTFIX = ".png";
	/** 日志后缀名 .txt */
	public final static String TXT_POSTFIX = ".txt";
	/** apk后缀名 .apk */
	public final static String APK_POSTFIX = ".apk";
	/** 服务器地址 http://ver.cnksi.com.cn */
	public static final String SERVER_URL = "http://ver.cnksi.com.cn";
	/** 更新的url /api/client */
	public static final String UPDATE_URL = SERVER_URL + "/api/client";
	/** 错误日志上传地址 */
	public static final String CRASHLOG_UPLOAD_URL = SERVER_URL + "/elog";
	/** 激活状态 可用状态 Y */
	public static final String ACTIVED = "Y";
	/** 逗号分隔符 , */
	public static final String COMMA_SEPARATOR = ",";
	/** 短横线分隔符 - */
	public static final String DASH_SEPARATOR = "-";
	/** 竖线 | */
	public static final String VERTICAL_SEPARATOR = "|";
	/** 竖线转义字符 \\| */
	public static final String VERTICAL_SEPARATOR_ESCAPE = "\\|";
	/** 下一行 \n */
	public static final String NEXT_LINE_SEPARATOR = "\n";
	/** 多种符号分隔符 ，|,|、|；|; */
	public static final String MUTI_SYMBOL_SEPARATOR = "，|,|、|；|;";
	/** yyyy-MM-dd */
	public static final String dateFormat1 = "yyyy-MM-dd";
	/** yyyy-MM-dd HH:mm:ss */
	public static final String dateFormat2 = "yyyy-MM-dd HH:mm:ss";
	/** yyyy-MM-dd-HH-mm-ss */
	public static final String dateFormat3 = "yyyy-MM-dd-HH-mm-ss";
	/** yyyy/MM/dd HH:mm:ss */
	public static final String dateFormat4 = "yyyy/MM/dd HH:mm:ss";
	/** MM-dd,HH:mm */
	public static final String dateFormat5 = "MM-dd,HH:mm";
	/** yyyyMMddHHmmssSSS */
	public static final String dateFormat6 = "yyyyMMddHHmmssSSS";
	/** yyyyMMdd_HHmmss */
	public static final String dateFormat7 = "yyyyMMdd_HHmmss";
	/** yyyy-MM-dd HH:mm */
	public static final String dateFormat8 = "yyyy-MM-dd HH:mm";
	/** yyyy-MM-dd HH:mm */
	public static final String dateFormat9 = "MM-dd HH:mm";
	/** HHmmssSSS */
	public static final String dateFormat10 = "ssSSS";
	/** yyyyMMdd_HHmmssSSS */
	public static final String dateFormat11 = "yyyyMMdd_HHmmssSSS";
	/** sharedPreference name config */
	public static final String SHARED_PREFERENCE_NAME = "config";
	/** 注册日期 */
	public static final String REGISTERED_DATE = "registered_date";
	/** 文件下载名称 */
	public static final String DOWNLOAD_FILE_NAME_KEY = "download_file_name";
	public static final String DOWNLOAD_FOLDER_NAME_KEY = "download_folder_name";
	/** 文件下载存放文件夹 */
	public static final String DOWNLOAD_APK_URL_KEY = "download_apk_url";
	/** QQ包名 */
	public static final String QQ_PACKAGE_NAME = "com.tencent.mobileqq";
	/** QQ应用名 */
	public static final String QQ_MAINACTIVITY_NAME = "com.tencent.mobileqq.activity.SplashActivity";
	/** QQ服务 */
	public static final String QQ_SERVICE_NAME = "com.tencent.mobileqq.msf.service.MsfService";
	/** skype包名 */
	public static final String SKYPE_PACKAGE_NAME = "com.skype.raider";
	/** skype应用名 */
	public static final String SKYPE_MAINACTIVITY_NAME = "com.skype.raider.Main";
	/** skype服务 */
	public static final String SKYPE_SERVICE_NAME = "com.skype.android.service.AccountService";
	/** VPNService包名 */
	public static final String OPEN_VPN_PACKAGE_NAME = "de.blinkt.openvpn";
	/** vpnService名 */
	public static final String OPEN_VPN_SERVICE_NAME = "de.blinkt.openvpn.core.OpenVPNService";
	public static final String PROGRAM_CODE_KEY = "com.cnksi.core.PROGRAM_CODE";

	/** ZBar */
	public static final int auto_focus = 111;
	public static final int decode = 112;
	public static final int decode_failed = 113;
	public static final int decode_succeeded = 114;
	public static final int restart_preview = 115;
	public static final int quit = 116;
	public static final String ZBAR_RESULT = "zbar_result";

}
