package com.cnksi.bdzinspection.utils;

import android.Manifest;
import android.os.Environment;

import com.cnksi.xscore.xsutils.CLog;
import com.cnksi.xscore.xsutils.DateUtils;
import com.cnksi.xscore.xsutils.SDCardUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public final class Config {

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
    // public static final String EXTERNAL_SDCARD_DIRECTORY = getOutCardPath();
    public static final String EXTERNAL_SDCARD_DIRECTORY = null;
    // public static final String EXTERNAL_SDCARD_DIRECTORY = null;
    /**
     * 如果有外置内存卡 则使用外置内存卡
     */
    public static final String BDZ_INSPECTION_FOLDER = (EXTERNAL_SDCARD_DIRECTORY != null ? EXTERNAL_SDCARD_DIRECTORY : ((SDCardUtils.isSDCardEnable() ? SDCARD_DIRECTORY : PHONE_DIRECTORY))) + "/BdzInspection/";

    public static final String DATABASE_FOLDER = BDZ_INSPECTION_FOLDER + "database/";
    public static final String UPLOAD_DATABASE_FOLDER = BDZ_INSPECTION_FOLDER + "upload_database/";
    public static final String LOGFOLDER = BDZ_INSPECTION_FOLDER + "log/";
    public static final String DEFALUTFOLDER = BDZ_INSPECTION_FOLDER + "default/";
    public static final String DOWNLOAD_APP_FOLDER = BDZ_INSPECTION_FOLDER + "download/";
    public static final String PICTURES_FOLDER = BDZ_INSPECTION_FOLDER + "";
    public static final String RESULT_PICTURES_FOLDER = BDZ_INSPECTION_FOLDER + "";
    public static final String GONG_QI_JU = "admin/";
    public static final String CUSTOMER_PICTURES_FOLDER = BDZ_INSPECTION_FOLDER + "";
    public static final String AUDIO_FOLDER = BDZ_INSPECTION_FOLDER + "audio/";
    public static final String VIDEO_FOLDER = BDZ_INSPECTION_FOLDER + "video/";
    public static final String NFC_FOLDER = BDZ_INSPECTION_FOLDER + "nfc/";
    // 签字目录
    public final static String SIGN_PICTURE_FOLDER = BDZ_INSPECTION_FOLDER + "signimg/";

    public static final String BAK_FOLDER = BDZ_INSPECTION_FOLDER + "bak/";
    // 下载的名称
    public static final String DOWNLOAD_FILE_NAME = "BdzInspection" + DateUtils.getCurrentShortTime() + ".apk";
    public static final String NARI_BASEFOLDER = BDZ_INSPECTION_FOLDER + "package/";
    public static final String CURRENT_AREA = "luzhou";

    /**
     * 项目code
     */
    public static final String PCODE = "E6F70F55-48A6-4F8A-A5C1-4B3AE6135B85";
    /**
     * 荣联的APP_Id 和 APP_TOKEN
     */
    public static final String RONGLIAN_APP_ID = "aaf98f89510f639f01511546146615dd";
    public static final String RONGLIAN_APP_TOKEN = "3e65ac581e1b5355c4ce59dc2d00d9e5";

    /**
     * 请求协助的账号和名称
     */
    public static final String CALL_ACCOUNT = "8005048600000004";
    public static final String CALL_USER_NAME = "肖珂";

    /**
     * PAD 端需要同步到服务器端的文件夹
     */
    public static final String PAD_SYNC_FOLDER = "audio,customer_pictures,upload_database,result_pictures,pictures,log";

    public static final String FACE_FOLDER = (SDCardUtils.isSDCardEnable() ? SDCARD_DIRECTORY : PHONE_DIRECTORY) + "/faces/";
    /**
     * 数据库名称
     */
    public static final String DATABASE_NAME = "bdzinspection.db";
    /**
     * 上传数据库名称
     */
    public static final String UPLOAD_DATABASE_NAME = "upload.db";
    /**
     * 数据库路径
     */
    public static final String DATABASE_PATH = DATABASE_FOLDER + DATABASE_NAME;
    /**
     * 照片名
     */
    public static final String CURRENT_IMAGE_NAME = "current_image_name";
    public static final String PICTURE_PREFIX = "picture_prefix";
    /**
     * 名称
     */
    public static final String CURRENT_FILENAME = "current_filename";
    /**
     * 照片地址
     */
    public static final String IMAGEURL_LIST = "imageUrlList";
    /**
     * 点击图片的位置
     */
    public static final String CURRENT_IMAGE_POSITION = "current_image_position";
    /**
     * 是否显示照片选中标志
     */
    public static final String IS_SHOW_PHOTO_FLAG = "isShowPhotoFlag";
    /**
     * 取消照片地址
     */
    public static final String CANCEL_IMAGEURL_LIST = "cancelImageUrlList";
    /**
     * 标题名称
     */
    public static final String TITLE_NAME = "title_name";
    /**
     * 危险点
     */
    public static final String DANGER_POINT_MODEL = "danger_point_model";
    /**
     * 工器具
     */
    public static final String TOOLS_MODEL = "tools_model";
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
     * 当前选中的FunctionModel
     */
    public static final String CURRENT_FUNCTION_MODEL = "current_function_model";
    public static final String CURRENT_POSITION = "current_position";
    public static final String IS_ADD_DEVICE_STANDARD = "is_add_device_standard";
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
     * 图片的内容
     */
    public static final String PICTURE_CONTENT = "picture_content";
    /**
     * 是否是特殊巡检
     */
    public static final String IS_PARTICULAR_INSPECTION = "is_particular_inspection";
    /**
     * 巡视类型
     */
    public static final String CURRENT_INSPECTION_TYPE = "current_inspection_type";

    /**
     * 巡视类型名称
     */
    public static final String CURRENT_INSPECTION_TYPE_NAME = "current_inspection_type_name";
    /**
     * 变电站编号
     */
    public static final String CURRENT_BDZ_ID = "current_bdz_id";
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
     * 变电站名称
     */
    public static final String CURRENT_BDZ_NAME = "current_bdz_name";
    /**
     * 电池组编号
     */
    public static final String CURRENT_BATTERY_ID = "current_battery_id";
    /**
     * 电池组名称
     */
    public static final String CURRENT_BATTERY_NAME = "current_battery_name";
    /**
     * 报告id
     */
    public static final String CURRENT_REPORT_ID = "current_report_id";
    public final static String KEY_SYNC_URL = "key_sync_url";
    public final static String KEY_SYNC_APP_ID = "key_sync_app_id";
    public static final String TASK_SELECT_PERSONS = "task_select_persons";
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
     * 巡检标准
     */
    public static final String REFERENCE_STANDARD_MODEL = "reference_standar_model";
    /**
     * 工作标准
     */
    public static final String WORK_STANDARD_MODEL = "work_standard_model";
    /**
     * 技术标准
     */
    public static final String TECH_STANDARD_MODEL = "tech_standard_model";
    /**
     * 管理标准
     */
    public static final String MANAGER_STANDARD_MODEL = "manager_standard_model";
    /**
     * 是否是从电池巡检界面跳转过去的
     */
    public static final String IS_FROM_BATTERY = "is_from_battery";
    public static final String IS_FROM_TASK_REMIND = "is_from_task_remind";
    public static final String IS_FROM_MAIN_MENU = "is_from_main_menu";
    /**
     * 跟踪缺陷的id
     */
    public static final String TRACK_DEFECT_RECORD_ID = "track_defect_record_id";
    /**
     * 签字的个数
     */
    public static final String SIGN_NAME_COUNT = "sign_name_count";
    /**
     * 签字的名称
     */
    public static final String SIGN_NAME_LIST = "sign_name_list";
    public static final String IS_ACCEPT_ACCIDENT_NEWPRODUCT = "isAcceptAccidentNewProduct";
    /**
     * 所有的设备id
     */
    public static final String ALL_DEVICE_ID_LIST = "all_device_list";
    /**
     * 抄录数据
     */
    public static final String COPY_DESCRIPTION = "copy_description";
    /**
     * 恢复抄录数据
     */
    public static final String IS_RECOVER_COPY_DATA = "is_recover_copy_data";
    /**
     * 定期维护蓄电池
     */
    public static final String CURRENT_MAINTANENCE_BATTERY = "current_maintanence_battery";
    /**
     * 筛选
     */
    public static final String SEARCH_DEVICE_KEY = "search_device_key";
    public static final String SEARCH_DEVICE_VALUE = "筛选";
    public static final String ADD_NEW_DEFECT_RECORD = "add_new_defect_record";
    public static final String DEVICE_HAS_COPY = "device_has_copy";
    /**
     * 缺陷数量
     */
    public static final String DEFECT_COUNT_KEY = "defect_count_key";
    /**
     * 上次选中的变电站名字
     */
    public final static String LASTTIEM_CHOOSE_BDZNAME = "lasttime_choose_bdzname";
    /**
     * 一般缺陷
     */
    public static final String GENERAL_LEVEL = "一般缺陷";
    public static final String GENERAL_LEVEL_CODE = "2";
    /** 最高缺陷等级 */
    // public static final String MAX_DEFECT_LEVEL = "max_defect_level";
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
    /**
     * 巡检前准备requestCode
     */
    public static final int TURN_SUCCESS = 0x111;
    // 0代表启动随时设备抄录1代表设备抄录需要和当前定位位置有关
    public static final int CURRENT_COPY_TYPE = 0;
    /**
     * 数据库删除标志
     */
    public static final String DELETED = "1";
    // 音频后缀
    // public static final String AMR_POSTFIX = ".amr";
    public static final String AMR_POSTFIX = ".mp3";
    public static final String MP4_POSTFIX = ".mp4";

    /*********************************************************************************/
    public static final String SPLIT = ",";
    public static final int ACTION_SELECT_IMAGE = 0x801;
    public static final String GQJMAP = "gqjmap";
    // 保存当前所有类型的id
    public static final String LOO_ID = "loo_id";
    public static final String IS_DEVICE_PART = "is_device_part";
    /**
     * 跳转是否是从sjjc过来的
     */
    public static final String IS_FROM_SJJC = "isFromSJJC";
    public static final String NOTCOPY = "notCopy";
    public static final String HASCOPIED = "hasCopied";
    public static final String SELECT_PERSONS = "select_persons";
    public static final String REPORT = "report";
    /**
     * 运维一体化工具器传递pro_id
     */
    public final static String YWYTHPROTYPE = "YWYTHPROTYPE";
    /**
     * 间隔到位是否启用抄录判断 0-不启用 1-启用
     */
    public final static boolean PLACED_BY_COPY = true;
    public final static String CURRENT_LATLNG = "current_latlng";
    // 设置抄录的判断距离值
    public final static String COPY_DISTANCE_KEY = "copy_distance";
    public static final int SHAKE_SPACE = 0x135;
    public static final String PERMISSION_STASTUS = "permission_status";
    public static final String XUDIANCHI = "蓄电池";
    public static final String DIANYA = "电压";
    public static final String NEIZU = "内阻";
    public static String SYNC_URL_VALUE = "";
    public static String SYNC_APP_ID_VALUE = "";
    /**
     * 部门名字
     */
    public static String CURRENT_DEPARTMENT_NAME = "current_department_name";
    public static String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA};
    public static float COPY_MAX_DISTANCE = 100f;

    /**
     * 获取外部内存卡路径
     *
     * @return
     */
    public static String getOutCardPath() {
        String cmd = "cat /proc/mounts";
        Runtime runtime = Runtime.getRuntime();
        BufferedInputStream bufferedInputStream = null;
        BufferedReader bufferedReader = null;
        try {
            Process process = runtime.exec(cmd);
            bufferedInputStream = new BufferedInputStream(process.getInputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream));
            String lineResult;
            while (null != (lineResult = bufferedReader.readLine())) {
                CLog.e(lineResult);
                if (lineResult.contains("/storage") && lineResult.contains("/dev") && !lineResult.contains("emulated")) {
                    String[] resultArray = lineResult.split(" ");
                    if (null != resultArray && resultArray.length >= 5) {
                        String result = resultArray[1];
                        return result;
                    }
                }
                if (process.waitFor() != 0 && process.exitValue() != 0) {
                    CLog.e("命令执行失败");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != bufferedReader) {
                    bufferedReader.close();
                }
                if (null != bufferedInputStream) {
                    bufferedInputStream.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 巡视类型
     */
    public enum InspectionType {
        SBXS("设备巡视"), GZP("工作票"), operation("运维一体化"), full("全面巡检"), day("日常巡检"), routine("例行巡视"), special("特殊巡检"),
        special_wind("大风天气特殊巡检"), battery("蓄电池测试"),
        special_fog("大雾天气特殊巡检"), special_snow("冰雪、冰雹雾霾特殊巡视"), special_newdevice("新投设备特殊巡视"), special_again("重新投运特殊巡视"),
        special_currentdefect("现存缺陷特殊巡视"), special_error("异常情况特殊巡视"), special_important("保电特殊巡视"), special_danger("事故风险时段特殊巡视"),
        special_thunderstorm("雷雨后特殊巡检"), special_hyperthermia("高温天气特殊巡检"), special_nighttime("夜间巡视"), special_xideng("熄灯巡视"),
        switchover("定期切换试验"), maintenance("定期维护"),

        SBJC("设备检测"), SBJC_01("红外测温"), SBJC_02("保护屏红外热像检测"), SBJC_03("室内温湿度记录"), SBJC_04("差动保护差流记录"), SBJC_05("交直流变压器分接开关调整记录"),

        JYHYS("精益化验收"), JYHPJ("精益化评价"), professional("专业巡视");

        public final String value;

        InspectionType(String value) {
            this.value = value;
        }

        public static InspectionType get(String name) {
            for (InspectionType type : values()) {
                if (type.name().equals(name)) {
                    return type;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return this.value;
        }

        public boolean equals(String s) {
            return name().equals(s);
        }
    }

    /**
     * 角色
     *
     * @author Wastrel
     * @date 创建时间：2016年8月17日 下午1:23:43 TODO
     */
    public enum Role {
        worker("工作人"), leader("负责人");
        private final String value;

        Role(String value) {
            this.value = value;
        }
    }

    /**
     * 工器具停用状态
     */
    public enum ToolStatus {
        normal("正常"), unNormal("不正常"), inTest("试验中"), stop("作废"), test("试验"), overdue("超期"), quarantine("待检");//normal,overdue,quarantine默认处理为正常

        public final String value;

        ToolStatus(String value) {
            this.value = value;
        }

        public static String getValue(String name) {
            if (normal.name().equalsIgnoreCase(name))
                return normal.value;
            else if (unNormal.name().equalsIgnoreCase(name))
                return unNormal.value;
            else if (inTest.name().equalsIgnoreCase(name))
                return inTest.value;
            else if (quarantine.name().equalsIgnoreCase(name))
                return quarantine.value;
            else if (overdue.name().equalsIgnoreCase(name))
                return overdue.value;
            else
                return "";

        }

        @Override
        public String toString() {
            return name();
        }
    }


    /**
     * Pms设备型号
     */
    public enum PmsDeviceType {
        one("一次设备"), second("二次设备"), auto("自动化屏"), secondP("二次屏");
        private final String value;

        PmsDeviceType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    /**
     * 电压等级
     */
    public enum LookUpType {
        inspection("巡检类型"), voltage("电压等级"), defect_reason("缺陷原因"), proType("运维一体化"), pmsDeviceType("设备型号");
        private final String value;

        LookUpType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    /**
     * 任务状态
     */
    public enum TaskStatus {
        undo("未巡视"), doing("巡视中"), done("已完成"), notdownload("未下载"), notfinish("未完成"), finished("已完成");
        public final String value;

        TaskStatus(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }

    }

    /**
     * 操作票类型
     */
    public enum OperateType {
        dr("单人操作"), jh("监护下操作"), jx("检修人员操作");
        private final String value;

        OperateType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    /**
     * 操作票任务类型
     */
    public enum OperateTaskType {
        DBRW("待办任务"), BYRW("本月任务"), BNRW("本年任务");
        private final String value;

        OperateTaskType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    /**
     * 操作票任务状态
     */
    public enum OperateTaskStatus {
        dsh("待审核"), wwc("未完成"), yzt("已暂停"), ywc("已完成"), ytz("已停止");
        private final String value;

        OperateTaskStatus(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    public enum SpaceType {
        spacing("间隔"),
        spacing_self("自定义间隔"),
        cabinet("屏柜");
        String zhName;

        SpaceType(String zhName) {
            this.zhName = zhName;
        }

        public static boolean isCabinet(String type) {
            return cabinet.name().equals(type);
        }
    }

}
