package com.cnksi.sjjc;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.HashMap;
import java.util.List;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/1/17 8:42
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
    private static final String NEED_CONFIRM_EVERY_STEP = "need_confirm_every_step";

    private static final HashMap<String, String> SYSTEM_CONFIG = new HashMap<>();

    static {
        SqlInfo sqlInfo = new SqlInfo("select * from system_config");
        try {
            List<DbModel> models = CustomApplication.getDbManager().findDbModelAll(sqlInfo);
            if (models != null) {
                for (DbModel model : models) {
                    SYSTEM_CONFIG.put(model.getString("k"), model.getString("v"));
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

}
