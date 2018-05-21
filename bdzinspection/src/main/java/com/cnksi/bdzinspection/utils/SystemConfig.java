package com.cnksi.bdzinspection.utils;


import android.util.Log;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.xscore.xsutils.StringUtils;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.HashMap;
import java.util.List;

/**
 * 系统流程 权限控制等
 *
 * @version 1.0
 * @auth wastrel
 * @date 2018/1/16 11:23
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class SystemConfig {

    /**
     * 巡检前准备，是否必须单步确认
     */
    private static final String NEED_CONFIRM_PRE_INSPECTION = "need_confirm_pre_inspection";
    /**
     * 到位情况（间隔、设备）
     */
    private static final String PLACED_WAY = "placed_way";
    /**
     * 关键设备是否必须拍照
     */
    private static final String NEED_PIC_IMPORTENT_DEVICE = "need_pic_importent_device";
    /**
     * 缺陷是否必须强制跟踪
     */
    private static final String NEED_TRACK_DEFECT = "need_track_defect";
    /**
     * 导出巡视报告，未到位设备是否默认打√
     */
    private static final String DEFAULT_VALUE_EXPORT_REPORT = "default_value_export_report";
    /**
     * 维护步骤是否必须逐条确认
     */
    private static final String SPECIAL_NEEED_COPY = "special_need_copy";

    /**
     * 是否可以自定义时间
     */
    public static final String CAN_DEFINE_REPORT_START_END_TIME = "can_define_report_start_end_time";

    /**
     * 二次设备小室需要抄录的巡视类型
     */
    public static final String CAN_COPY_XSWD_INSPECTION = "can_copy_xswd_inspection";

    /**
     * 实验维护确认的方式（简单/复杂（大步骤打勾）/更复杂（小步骤打勾）） "none","every_parent_step","every_step"
     */
    public static final String NEED_CONFIRM_EVERY_STEP = "need_confirm_every_step";

    public static final String NONE = "none", EVERY_PARENT_STEP = "every_parent_step", EVERY_STEP = "every_step";

    private static final HashMap<String, String> SYSTEM_CONFIG = new HashMap<>();
    static String TAG = SystemConfig.class.getSimpleName();

    static {
        // init();
    }

    public static void init() {
        SqlInfo sqlInfo = new SqlInfo("select * from system_config");
        try {
            List<DbModel> models = XunshiApplication.getDbUtils().findDbModelAll(sqlInfo);
            if (models != null) {
                for (DbModel model : models) {
                    SYSTEM_CONFIG.put(model.getString("k"), model.getString("v"));
                    Log.i(TAG, model.getString("k") + ":" + model.getString("v"));
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否必须强制检查巡检前准备工作
     *
     * @return
     */
    public static boolean isMustVerifyInspectionReady() {
        return "Y".equals(SYSTEM_CONFIG.get(NEED_CONFIRM_PRE_INSPECTION));
    }

    public static boolean isMustPicImportantDevice() {
        return "Y".equals(SYSTEM_CONFIG.get(NEED_PIC_IMPORTENT_DEVICE));
    }

    public static String getPlacedWay() {
        return StringUtils.nullTo(SYSTEM_CONFIG.get(PLACED_WAY), "spacing");
    }

    public static boolean isDevicePlaced() {
        return "device".equals(getPlacedWay());
    }

    public static boolean isDefineTimeBySelf() {
        return "Y".equalsIgnoreCase(SYSTEM_CONFIG.get(CAN_DEFINE_REPORT_START_END_TIME));
    }

    public static boolean isSpecialInspectionNeedCopy() {
        return "Y".equalsIgnoreCase(SYSTEM_CONFIG.get(SPECIAL_NEEED_COPY));
    }

    public static String getCopyInspection() {
        return SYSTEM_CONFIG.get(CAN_COPY_XSWD_INSPECTION);
    }

    public static String getSwitchMenuConfirmStyle() {
        return SYSTEM_CONFIG.get(NEED_CONFIRM_EVERY_STEP);
    }

}
