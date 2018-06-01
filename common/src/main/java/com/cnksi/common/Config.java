package com.cnksi.common;

import android.os.Environment;

import com.cnksi.core.utils.Cst;
import com.cnksi.core.utils.SDCardUtils;

/**
 * @author Wastrel
 * @author luoxy
 * @version 1.0
 * @date 16/4/24
 */
public class Config implements Cst {
    public static boolean DEBUG = BuildConfig.DEBUG;
    public static final int START_ACTIVITY_FORRESULT = 0x0;

    /**-------------------------------目录相关-BEGIN-------------------------------------*/


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
    /**
     * 如果有外置内存卡 则使用外置内存卡
     */
    public static final String BDZ_INSPECTION_FOLDER = (EXTERNAL_SDCARD_DIRECTORY != null ? EXTERNAL_SDCARD_DIRECTORY : ((SDCardUtils.isSDCardEnable() ? SDCARD_DIRECTORY : PHONE_DIRECTORY))) + "/BdzInspection/";

    public static final String DATABASE_FOLDER = BDZ_INSPECTION_FOLDER + "database/";
    public static final String UPLOAD_DATABASE_FOLDER = BDZ_INSPECTION_FOLDER + "upload_database/";
    public static final String LOG_FOLDER = BDZ_INSPECTION_FOLDER + "log/";
    public static final String DOWNLOAD_APP_FOLDER = BDZ_INSPECTION_FOLDER + "download/";
    public static final String DEFALUTFOLDER = BDZ_INSPECTION_FOLDER + "default/";
    public static final String GONG_QI_JU = "admin/";
    public static final String PICTURES_FOLDER = BDZ_INSPECTION_FOLDER + "";
    public static final String RESULT_PICTURES_FOLDER = BDZ_INSPECTION_FOLDER + "";
    public static final String CUSTOMER_PICTURES_FOLDER = BDZ_INSPECTION_FOLDER + "";
    public static final String WWW_ROOT_FOLDER = BDZ_INSPECTION_FOLDER + "www/";
    public static final String AUDIO_FOLDER = BDZ_INSPECTION_FOLDER + "audio/";
    public static final String VIDEO_FOLDER = BDZ_INSPECTION_FOLDER + "video/";
    public static final String WORKTICKIT_PIC = WWW_ROOT_FOLDER + "pic/";
    public static final String NARI_BASE_FOLDER = BDZ_INSPECTION_FOLDER + "package/";
    public final static String SIGN_PICTURE_FOLDER = BDZ_INSPECTION_FOLDER + "signimg/";



    public static final String DATABASE_NAME = "bdzinspection.db";
    public static final String ENCRYPT_DATABASE_NAME = "bdzinspection-encrypt.db";

    public static String[] FILE_PATH_ARRAY = {
            Config.BDZ_INSPECTION_FOLDER,
            Config.DATABASE_FOLDER,
            Config.SIGN_PICTURE_FOLDER,
            Config.VIDEO_FOLDER,
            Config.UPLOAD_DATABASE_FOLDER,
            Config.LOG_FOLDER,
            Config.DOWNLOAD_APP_FOLDER,
            Config.AUDIO_FOLDER,
            Config.WWW_ROOT_FOLDER};

    /**-------------------------------目录相关-END---------------------------------------*/


    /**
     * -------------------------------请求码相关-BEGIN---------------------------------------
     */

    public static final int LOAD_DATA = 0x01;
    /**
     * 加载更多
     */
    public static final int LOAD_MORE_DATA = 0x8;
    /**
     * 刷新数据
     */
    public static final int REFRESH_DATA = 0x2;
    /**
     * 保存数据
     */
    public static final int SAVE_DATA = 0x3;

    /**
     * 开启选择设备code  用于startActivityForResult
     */
    public static final int ACTIVITY_CHOSE_DEVICE = 0x200;
    public static final int ACTION_SELECT_IMAGE = 0x801;


    /**
     * 巡检前准备requestCode
     */
    public static final int TURN_SUCCESS = 0x111;

    public static final int SHAKE_SPACE = 0x135;

    /**
     * 取消选择的图片
     */
    public static final int CANCEL_RESULT_LOAD_IMAGE = LOAD_DATA + 1;
    /**
     * 画圈
     */
    public static final int DRAW_IMAGE = CANCEL_RESULT_LOAD_IMAGE + 1;

    /**-------------------------------请求码相关-END---------------------------------------*/



    /**-------------------------------KEY值-BEGIN---------------------------------------*/

    /**
     * 取消照片地址
     */
    public static final String CANCEL_IMAGE_URL_LIST_KEY = "cancelImageUrlList";


    /**
     * 获取当前变电站生成目录信息
     */
    public static final String CURRENT_FOLDER_KEY = "current_folder";


    /**
     * 根据变电站生成的图片前缀
     */
    public static final String PICTURE_PREFIX_KEY = "image_prefix";

    /**
     * 设备id
     */
    public static final String CURRENT_DEVICE_ID = "current_device_id";
    /**
     * 设备名称
     */
    public static final String CURRENT_DEVICE_NAME = "current_device_name";
    /**
     * 任务id
     */
    public static final String CURRENT_TASK_ID = "current_task_id";
    /**
     * 设备部件id
     */
    public static final String CURRENT_DEVICE_PART_ID = "current_device_part_id";
    /**
     * 设备部件名称
     */
    public static final String CURRENT_DEVICE_PART_NAME = "current_device_part_name";
    /**
     * 间隔id
     */
    public static final String CURRENT_SPACING_ID = "current_spacing_id";
    /**
     * 间隔名称
     */
    public static final String CURRENT_SPACING_NAME = "current_spacing_name";
    /**
     * 设备标准id
     */
    public static final String CURRENT_STANDARD_ID = "current_standard_id";
    /**
     * 是否是从电池巡检界面跳转过去的
     */
    public static final String IS_FROM_BATTERY = "is_from_battery";
    public static final String IS_FROM_TASK_REMIND = "is_from_task_remind";

    /**
     * 是否是特殊巡检
     */
    public static final String IS_PARTICULAR_INSPECTION = "is_particular_inspection";

    /**
     * 屏蔽WIFI开关
     */
    public static final String MASK_WIFI_SWITCH_KEY = "mask_wifi_switch_key";

    /**
     * 当前经纬度
     */
    public final static String CURRENT_LAT_LNG_KEY = "current_latlng";

    /**
     * 设置抄录的判断距离值
     */
    public final static String COPY_DISTANCE_KEY = "copy_distance";

    /**
     * 标题名称
     */

    public final static String LAST_CHOOSE_BDZ_NAME_KEY = "lasttime_choose_bdzname";


    /**
     * 巡视类型
     */
    public static final String CURRENT_INSPECTION_TYPE = "current_inspection_type";
    /**
     * 巡视类型名称
     */
    public static final String CURRENT_INSPECTION_TYPE_NAME = "current_inspection_type_name";

    /**
     * 缺陷数量
     */
    public static final String DEFECT_COUNT_KEY = "defect_count_key";


    /**
     * 登陆部门
     */
    public static final String CURRENT_DEPARTMENT_ID = "current_department_id";
    /**
     * 当前登录人员
     */
    public static final String CURRENT_LOGIN_USER = "current_login_user";
    /**
     * 当前登录人员帐号
     */
    public static final String CURRENT_LOGIN_ACCOUNT = "current_login_account";

    /**
     * 当前选中的FunctionModel
     */
    public static final String CURRENT_FUNCTION_MODEL = "current_function_model";

    /**
     * 当前操作变电站
     */
    public static final String CURRENT_BDZ_ID = "current_bdz_id";
    /**
     * 当前操作变电站名称
     */
    public static final String CURRENT_BDZ_NAME = "current_bdz_name";
    /**
     * 当前报告ID
     */
    public static final String CURRENT_REPORT_ID = "current_report_id";

    /**
     * 当前选择的任务类型
     */
    public static final String CURRENT_SELECT_TASK_TYPE_KEY = "current_select_task_type";

    public static final String IS_DEVICE_PART_KEY = "is_device_part";

    public static final String SELECT_PERSONS_KEY = "select_persons";

    public static final String IS_ADD_DEVICE_STANDARD_KEY = "is_add_device_standard";


    /**
     * 蓄电池
     */
    public static final String CURRENT_BATTERY_ZU = "current_battery_zu";
    public static final String CURRENT_TEST_TYPE = "current_test_type";
    public static final String CURRENT_BATTERY_NUM = "current_battery_num";
    public static final String CURRENT_MAINTANENCE_BATTERY = "current_maintanence_battery";


    /**
     * 电池组编号
     */
    public static final String CURRENT_BATTERY_ID = "current_battery_id";
    /**
     * 电池组名称
     */
    public static final String CURRENT_BATTERY_NAME = "current_battery_name";


    /**
     * 是否显示缺陷原因
     */
    public static final String IS_SHOW_DEFECT_REASON = "is_show_defect_reason";
    /**
     * 是否是跟踪缺陷
     */
    public static final String IS_TRACK_DEFECT = "is_track_defect";
    /**
     * 是否是跟踪缺陷
     */
    public static final String IS_NEED_SEARCH_DEFECT_REASON = "is_need_search_defect_reason";

    /**
     * 是否显示设备控件
     */
    public static final String IS_SHOW_DEVICE_WIDGET = "is_show_device_widget";
    /**
     * 跟踪缺陷的id
     */
    public static final String TRACK_DEFECT_RECORD_ID = "track_defect_record_id";
    public static final String ADD_NEW_DEFECT_RECORD = "add_new_defect_record";

    public static String CURRENT_DEPARTMENT_NAME = "current_department_name";
    public final static String KEY_SYNC_URL = "key_sync_url";
    public final static String KEY_SYNC_APP_ID = "key_sync_app_id";

    /**
     * 标题名称
     */
    public static final String TITLE_NAME_KEY = "title_name";
    public static final String LOCATION_BDZID = "location_bdzId";
    public static final String LOCATION_BDZNAME = "location_bdzName";
    public static final String IS_FROM_SJJC = "isFromSJJC";
    public static final String IS_FROM_PMS = "isFromPMS";

    public static final String OTHER_DEPT_USER = "other_dept_user";

    /**-------------------------------KEY值-END-----------------------------------------*/


    /**-------------------------------常量值-BEGIN---------------------------------------*/

    /**
     * 项目code
     */
    public static final String PCODE = "E6F70F55-48A6-4F8A-A5C1-4B3AE6135B85";
    public static String SYNC_APP_ID = BuildConfig.SYNC_APP_ID;
    public static String SYNC_BASE_FOLDER = BDZ_INSPECTION_FOLDER;
    public static String SYNC_URL = BuildConfig.SYNC_URL;

    /**
     * 抄录允许的最大距离
     */
    public static float COPY_MAX_DISTANCE = 100f;

    public static final String REPORT_SOURCE_REPORT = "report";
    public static final String REPORT_SOURCE_SBJC = "sbjc";


    /**
     * 一般缺陷
     */
    public static final String GENERAL_LEVEL = "一般缺陷";
    public static final String GENERAL_LEVEL_CODE = "2";
    /**
     * 严重缺陷
     */
    public static final String SERIOUS_LEVEL = "严重缺陷";
    public static final String SERIOUS_LEVEL_CODE = "4";
    /**
     * 危急缺陷
     */
    public static final String CRISIS_LEVEL = "危急缺陷";
    public static final String CRISIS_LEVEL_CODE = "6";

    public final static boolean PLACED_BY_COPY = true;

    /**
     * 未完成
     */
    public static final String UNFINISH_MODEL = "unfinish_model";
    /**
     * 已完成
     */
    public static final String FINISHED_MODEL = "finished_model";
    /**
     * 全部
     */
    public static final String ALL_TASK_MODEL = "all_task_model";
    /**
     * 逾期
     */
    public static final String OVER_DUE_MODEL = "over_due_model";


    /**
     * 跟踪缺陷
     */
    public static final String TRACK_DEFECT_MODEL = "track_defect_model";

    /**
     * 消除缺陷
     */
    public static final String ELIMINATE_DEFECT_MODEL = "eliminate_defect_model";
    /**
     * 记录缺陷
     */
    public static final String RECORD_DEFECT_MODEL = "record_defect_model";

    //差动保护差流记录查询的关键值key
    public static final String DIFFERENTIAL_RECORD_KEY = "dclz";
    //交直流分接开关key
    public static final String TANSFORMADJUSTMENT_KAIGUAN = "fjkgdzcs";
    //调整后档位key
    public static final String TANSFORMADJUSTMENT_DANGWEI = "fjkgdzhdw";


    public static final String DIANYA = "电压";
    public static final String NEIZU = "内阻";
    public static final String XUDIANCHI = "蓄电池";

    public static String DELETED = "1";

    public static final String SINGLE = "single";


    // 保存当前所有类型的id
    public static final String AMR_POSTFIX = ".mp3";
    public static final String MP4_POSTFIX = ".mp4";

    /**-------------------------------常量值-END-----------------------------------------*/


}
