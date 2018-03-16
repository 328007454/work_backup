package com.cnksi.sjjc;

import android.os.Environment;

import com.cnksi.core.utils.CoreConfig;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.SDCardUtils;

/**
 * @version 1.0
 * @auth luoxy
 * @date 16/4/24
 */
public class Config extends CoreConfig {

    public static final int SUCCESS = 0x100;

    public static final int FAILURE = 0x101;

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
    public static final String PICTURES_FOLDER = BDZ_INSPECTION_FOLDER + "";
    public static final String WWWROOT_FOLDER = BDZ_INSPECTION_FOLDER + "www/";
    public static final String RESULT_PICTURES_FOLDER = BDZ_INSPECTION_FOLDER + "";
    public static final String CUSTOMER_PICTURES_FOLDER = BDZ_INSPECTION_FOLDER + "";
    public static final String AUDIO_FOLDER = BDZ_INSPECTION_FOLDER + "audio/";
    public static final String VIDEO_FOLDER = BDZ_INSPECTION_FOLDER + "video/";
    public static final String NFC_FOLDER = BDZ_INSPECTION_FOLDER + "nfc/";
    public static final String TJWT_FOLDER = BDZ_INSPECTION_FOLDER + "lib/wt/";
    public static final String WORKTICKIT_PIC = WWWROOT_FOLDER + "pic/";
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
    public static final String YANSHOUEDATABASE_NAME = "accept.db";

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
    public static final String SECOND_SPACE_AND_ONE_DEVICE ="second_space_and_one_device" ;


    public static String CURRENT_AREA = "lz";

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


    public static final String CURRENT_TASK_ID = "current_task_id";
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

    //验收类型
    public static final String TYPE_NAME = "type_name";
    public static final String VIDEO_NAME = "video_name";
    public static final String PIC_NAME = "pic_name";
    public static final String AUDIO_NAME = "audio_name";
    public static final String ACCPETCARD_ITEM = "accpetcard_item";
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
     * 取消选择的图片
     */
    public static final int CANCEL_RESULT_LOAD_IMAGE = PAIZHAO_LUXIANG_REQUSET + 1;

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

    /**
     * 操作类型
     */
    public enum ModifyOperation {
        add("增加"), update("修改"), delete("删除");
        private final String value;

        ModifyOperation(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }
}
