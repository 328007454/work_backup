package com.cnksi.common;

import android.os.Environment;

import com.cnksi.core.utils.Cst;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.SDCardUtils;

/**
 * @author Wastrel
 * @version 1.0
 * @author luoxy
 * @date 16/4/24
 */
public class Config implements Cst {
    public static boolean DEBUG = BuildConfig.DEBUG;

    public static final int SUCCESS = 0x100;

    public static final int FAILURE = 0x101;
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
//    public static final String EXTERNAL_SDCARD_DIRECTORY = getOutCardPath();
    public static final String EXTERNAL_SDCARD_DIRECTORY = null;
    /**
     * 如果有外置内存卡 则使用外置内存卡
     */
    public static final String BDZ_INSPECTION_FOLDER = (EXTERNAL_SDCARD_DIRECTORY != null ? EXTERNAL_SDCARD_DIRECTORY : ((SDCardUtils.isSDCardEnable() ? SDCARD_DIRECTORY : PHONE_DIRECTORY))) + "/BdzInspection/";

    public static final String DATABASE_FOLDER = BDZ_INSPECTION_FOLDER + "database/";
    public static final String UPLOAD_DATABASE_FOLDER = BDZ_INSPECTION_FOLDER + "upload_database/";
    public static final String LOGFOLDER = BDZ_INSPECTION_FOLDER + "log/";
    public static final String DOWNLOAD_APP_FOLDER = BDZ_INSPECTION_FOLDER + "download/";
    public static final String DEFALUTFOLDER = BDZ_INSPECTION_FOLDER + "default/";
    public static final String GONG_QI_JU = "admin/";
    public static final String PICTURES_FOLDER = BDZ_INSPECTION_FOLDER + "";
    public static final String WWWROOT_FOLDER = BDZ_INSPECTION_FOLDER + "www/";
    public static final String RESULT_PICTURES_FOLDER = BDZ_INSPECTION_FOLDER + "";
    public static final String CUSTOMER_PICTURES_FOLDER = BDZ_INSPECTION_FOLDER + "";
    public static final String AUDIO_FOLDER = BDZ_INSPECTION_FOLDER + "audio/";
    public static final String VIDEO_FOLDER = BDZ_INSPECTION_FOLDER + "video/";
    public static final String NFC_FOLDER = BDZ_INSPECTION_FOLDER + "nfc/";
    public static final String TJWT_FOLDER = BDZ_INSPECTION_FOLDER + "lib/wt/";
    public static final String WORKTICKIT_PIC = WWWROOT_FOLDER + "pic/";
    public static final String NARI_BASEFOLDER = BDZ_INSPECTION_FOLDER + "package/";
    /**
     * 项目code
     */
    public static final String PCODE = "E6F70F55-48A6-4F8A-A5C1-4B3AE6135B85";
    public static final String PAD_SYNC_FOLDER = "audio,customer_pictures,upload_database,result_pictures,pictures,log";
    // 签字目录
    public final static String SIGN_PICTURE_FOLDER = BDZ_INSPECTION_FOLDER + "signimg/";

    public static final String BAK_FOLDER = BDZ_INSPECTION_FOLDER + "bak/";
    // 下载的名称
    public static final String DOWNLOAD_FILE_NAME = "BdzInspection" + DateUtils.getCurrentShortTime() + ".apk";

    public static final String DATABASE_NAME = "bdzinspection.db";
    public static final String ENCRYPT_DATABASE_NAME = "bdzinspection-encrypt.db";
    public static final String YANSHOUEDATABASE_NAME = "accept.db";
    public static final String DB_COPY = "dbcopy.db";
    public static String SYNC_URL_VALUE = "";
    public static String SYNC_APP_ID_VALUE = "";
    /**
     * 运维一体化工具器传递pro_id
     */
    public final static String YWYTHPROTYPE = "YWYTHPROTYPE";

    /**
     * 名称
     */
    public static final String CURRENT_FILENAME = "current_filename";
    /**
     * 上传数据库名称
     */
    public static final String UPLOAD_DATABASE_NAME = "upload.db";
    /**
     * 用于共同数据保存
     */
    public static final String DEVICE_DATA = "device_data";


    public static final String DATA = "data";
    public static final String DATA1 = "data1";
    /**
     * 开启选择设备code  用于startActivityForResult
     */
    public static final int ACTIVITY_CHOSE_DEVICE = 0x200;
    public static final int ACTION_SELECT_IMAGE = 0x801;

    /**
     * 签字的名称
     */
    public static final String SIGN_NAME_LIST = "sign_name_list";
    /**
     * 返回温度
     */
    public static final String RESULT_TEMPERTURE = "result_temp";
    /**
     * 返回图片
     */
    public static final String RESULT_PICTURES = "result_pictures";
    /**
     * 图片前缀
     */
    public static final String PICTURE_PREFIX = "image_prefix";
    /**
     * 照片名
     */
    public static final String CURRENT_IMAGE_NAME = "current_image_name";
    /**
     * 图片的内容
     */
    public static final String PICTURE_CONTENT = "picture_content";
    /**
     * 照片地址
     */
    public static final String IMAGEURL_LIST = "imageUrlList";
    /**
     * 签名名字
     */
    public static final String SIGN_FILENAME = "sign_filename";
    /**
     * 点击图片的位置
     */
    public static final String CURRENT_IMAGE_POSITION = "current_image_position";
    /**
     * 是否显示照片选中标志
     */
    public static final String IS_SHOW_PHOTO_FLAG = "isShowPhotoFlag";
    /**
     * 是否删除图片
     */
    public static final String IS_DELETE_FILE = "isDeleteFile";
    /**
     * 取消照片地址
     */
    public static final String CANCEL_IMAGEURL_LIST = "cancelImageUrlList";

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
     * 标题名称
     */
    public static final String TITLE_NAME = "title_name";
    public static final String LOCATION_BDZID = "location_bdzId";
    public static final String LOCATION_BDZNAME = "location_bdzName";
    public static final String IS_AREADY_UPDATE = "isAreadyUpdate";
    public static final String IS_FROM_SJJC = "isFromSJJC";
    public static final String IS_FROM_PMS = "isFromPMS";
    public static final String ADD_SQL_INDEX = "add_sql_index";
    public static final String SBJC = "sbjc";
    public static final String DEVICE_BIGID = "big_id";
    public static final String SECOND_SPACE_AND_ONE_DEVICE = "second_space_and_one_device";
    public static final String OTHER_DEPT_USER = "other_dept_user";

    public static final String SELECT_PERSONS = "select_persons";
    public static String CURRENT_AREA = "lz";
    public static float COPY_MAX_DISTANCE = 100f;
    public static final String REPORT = "report";
    /**
     * 巡检前准备requestCode
     */
    public static final int TURN_SUCCESS = 0x111;
    // 0代表启动随时设备抄录1代表设备抄录需要和当前定位位置有关
    public static final int CURRENT_COPY_TYPE = 0;

    /**
     * 危险点
     */
    public static final String DANGER_POINT_MODEL = "danger_point_model";
    /**
     * 工器具
     */
    public static final String TOOLS_MODEL = "tools_model";
    public final static boolean PLACED_BY_COPY = true;
    public final static String CURRENT_LATLNG = "current_latlng";
    // 设置抄录的判断距离值
    public final static String COPY_DISTANCE_KEY = "copy_distance";
    public static final int SHAKE_SPACE = 0x135;

    public static final String MASK_WIFI = "MASK_WIFI";

    /**
     * 标题名称
     */

    public final static String LASTTIEM_CHOOSE_BDZNAME = "lasttime_choose_bdzname";
    /**
     * 所有的设备id
     */
    public static final String ALL_DEVICE_ID_LIST = "all_device_list";
    /**
     * 筛选
     */
    public static final String SEARCH_DEVICE_KEY = "search_device_key";
    public static final String SEARCH_DEVICE_VALUE = "筛选";
    /**
     * 温度单位
     */
    public static final String TEMPPERATURE_POSTFIX = "℃";

    public static final String CURRENT_INSPECTION_TYPE = "current_inspection_type";
    public static final String PERMISSION_STASTUS = "permission_status";

    /**
     * 检测类型key
     */
//    public static final String CURRENT_INSPECTION_TYPE = "current_inspection_type";

    /**
     * 巡视类型名称
     */
    public static final String CURRENT_INSPECTION_TYPE_NAME = "current_inspection_type_name";
    /**
     * 检测类型value
     */
    public static final String CURRENT_INSPECTION_VALUE = "current_inspection_value";
    public static final String CURRENT_INSPECTION_NAME = "current_inspection_name";
    public static final String CURRENT_SELECT_TASK_TYPE_INSPECTION = "current_select_task_type";
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
     * 当前选中的FunctionModel
     */
    public static final String CURRENT_FUNCTION_MODEL = "current_function_model";


    public static final String CURRENT_BDZ_ID = "current_bdz_id";
    public static final String CURRENT_BDZ_NAME = "current_bdz_name";
    public static final String CURRENT_REPORT_ID = "current_report_id";

    public static final String CURRENT_REPORT = "current_report";
    public static final String STATUS = "status";
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
    // 视频后缀名
    public static final String VIDEO_TYPE_MP4 = ".mp4";
    public static final String VIDEO_PATH = "video_path";
    //设备id
    public static final String DEVICE_ID = "device_id";
    public static final String CURRENT_POSITION = "current_position";
    public static final String IS_ADD_DEVICE_STANDARD = "is_add_device_standard";
    /**
     * 缺陷数量
     */
    public static final String DEFECT_COUNT_KEY = "defect_count_key";
    // 保存当前所有类型的id
    public static final String LOO_ID = "loo_id";
    public static final String AMR_POSTFIX = ".mp3";
    public static final String MP4_POSTFIX = ".mp4";
    public static final String IS_DEVICE_PART = "is_device_part";
    public static final String TASK_SELECT_PERSONS = "task_select_persons";


    public static final String REPORT_ID = "report_id";

    public static final int LOAD_DATA = 0x01;
    public static final int QIAN_MING_REQUEST = LOAD_DATA + 1;
    public static final int PAIZHAO_LUXIANG_REQUSET = QIAN_MING_REQUEST + 1;

    /**
     * 蓄电池
     */
    public static final String CURRENT_BATTERY_ZU = "current_battery_zu";
    public static final String CURRENT_BATTERY_TYPE = "current_battery_type";
    public static final String CURRENT_BATTERY_RECORD = "current_battery_record";
    public static final String CURRENT_BUNDLE = "current_bundle";
    public static final String CURRENT_TEST_TYPE = "current_test_type";
    public static final String CURRENT_BATTERY_NUM = "current_battery_num";
    public static final String CURRENT_MAINTANENCE_BATTERY = "current_maintanence_battery";
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
    /**
     * 电池组编号
     */
    public static final String CURRENT_BATTERY_ID = "current_battery_id";
    /**
     * 电池组名称
     */
    public static final String CURRENT_BATTERY_NAME = "current_battery_name";


    /**
     * 保护压板核对
     */
    public static final String PROTECT_CLAMP_MODEL = "protect_clamp_model";
    /**
     * 定值核对
     */
    public static final String CHECK_VALUE_MODEL = "check_value_model";
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
    /**
     * 取消选择的图片
     */
    public static final int CANCEL_RESULT_LOAD_IMAGE = PAIZHAO_LUXIANG_REQUSET + 1;
    /**
     * 画圈
     */
    public static final int DRAW_IMAGE = CANCEL_RESULT_LOAD_IMAGE + 1;

    public static final String SYNC_COME_FROM = "sync_come_from";

    /**
     * 数据同步
     */
    public static final String LOGACTIVITY_TO_SYNC = "logactivity_to_sync";
    /**
     * launcher到数据同步
     */
    public static final String LAUNCHERACTIVITY_TO_SYNC = "launcheractivity_to_sync";
    //差动保护差流记录查询的关键值key
    public static final String DIFFERENTIAL_RECORD_KEY = "dclz";
    //交直流分接开关key
    public static final String TANSFORMADJUSTMENT_KAIGUAN = "fjkgdzcs";
    //调整后档位key
    public static final String TANSFORMADJUSTMENT_DANGWEI = "fjkgdzhdw";


    public static String SYNC_APP_ID = BuildConfig.SYNC_APP_ID;
    public static String SYNC_BASE_FOLDER = BDZ_INSPECTION_FOLDER;
    public static String SYNC_URL = BuildConfig.SYNC_URL;
    public final static String KEY_SYNC_URL = "key_sync_url";
    public final static String KEY_SYNC_APP_ID = "key_sync_app_id";
    public static final String DIANYA = "电压";
    public static final String NEIZU = "内阻";
    public static final String XUDIANCHI = "蓄电池";

    public static String CURRENT_DEPARTMENT_NAME = "current_department_name";

    public static String[] FILEPATHARRAY = {
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
    public static String DELETED="1";


}
