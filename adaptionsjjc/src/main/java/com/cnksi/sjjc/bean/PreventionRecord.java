package com.cnksi.sjjc.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by han on 2016/6/14.
 */

@Table(name = "sbjc_prevention")
public class PreventionRecord extends BaseModel implements Serializable {
    /**
     * Id
     */
    public static final String ID = "id";
    @Column(name = ID, isId = true)
    public String id = UUID.randomUUID().toString();
    /**
     * 报告Id
     */
    public static final String REPORT_ID = "reportid";
    @Column(name = REPORT_ID)
    public String reportId;
    /**
     * 变电站id
     */
    public static final String BDZ_ID = "bdzid";
    @Column(name = BDZ_ID)
    public String bdzid;

    /**
     * 变电站名字
     */
    public static final String BDZ = "bdz_name";
    @Column(name = BDZ)
    public String bdz;

    /**
     * 主控制到位
     */
    public static final String MAIN_CONTROLL_IMAGES = "main_controll_images";
    @Column(name = MAIN_CONTROLL_IMAGES)
    public String main_controll_images;

    /**
     * 高压室到位
     */
    public static final String HYPERBARIC_IMAGES = "hyperbaric_images";
    @Column(name = HYPERBARIC_IMAGES)
    public String hyperbaric_images;

    /**
     * 一次设备到位
     */
    public static final String ONE_DEVICE_IMAGES = "one_device_images";
    @Column(name = ONE_DEVICE_IMAGES)
    public String one_device_images;
    /**
     * 保护室到位
     */
    public static final String PROTECT_IMAGES = "protect_images";
    @Column(name = PROTECT_IMAGES)
    public String protect_images;
    /**
     * 电缆层到位
     */
    public static final String CABLE_IMAGES = "cable_images";
    @Column(name = CABLE_IMAGES)
    public String cable_images;
    /**
     * 二次设备到位
     */
    public static final String SECOND_DEVICE_IMAGES = "second_device_images";
    @Column(name = SECOND_DEVICE_IMAGES)
    public String second_device_images;
    /**
     * 其他到位
     */
    public static final String OTHER_IMAGES = "other_images";
    @Column(name = OTHER_IMAGES)
    public String other_images;
    /**
     * 开关柜孔洞情况
     */
    public static final String SWITCH_STATUS = "switch_status";
    @Column(name = SWITCH_STATUS)
    public int switchStatus;
    /**
     * 室内孔洞情况
     */
    public static final String INROOM_STATUS = "inroom_status";
    @Column(name = INROOM_STATUS)
    public int inroomStatus;
    /**
     * 室外孔洞情况
     */
    public static final String OUTROOM_STATUS = "outroom_status";
    @Column(name = OUTROOM_STATUS)
    public int outroomStatus;
    /**
     * 门窗严密情况
     */
    public static final String DOOR_WINDOW_STATUS = "door_window_status";
    @Column(name = DOOR_WINDOW_STATUS)
    public int doorWindowStatus;
    /**
     * 鼠药器放置情况
     */
    public static final String RATSBANE_STATUS = "ratsbane_status";
    @Column(name = RATSBANE_STATUS)
    public int ratsbaneStatus;
    /**
     * 捕鼠器放置情况
     */
    public static final String MOUSETRAP_STATUS = "mousetrap_status";
    @Column(name = MOUSETRAP_STATUS)
    public int mousetrapStatus;
    /**
     * 清除情况
     */
    public static final String CLEAR_INFO = "clear_info";
    @Column(name = CLEAR_INFO)
    public String clearInfo;
    /**
     * 鼠药及捕鼠器具放置情况
     */
    public static final String MOUSETRAP_INFO = "mousetrap_info";
    @Column(name = MOUSETRAP_INFO)
    public String mousetrapInfo;
    /**
     * 备注
     */
    public static final String REMARK = "remark";
    @Column(name = REMARK)
    public String remark;

    /**
     * 更新时间
     */
    public static final String LAST_MODIFY_TIME = "last_modify_time";
    @Column(name = LAST_MODIFY_TIME)
    public String last_modify_time;

    public PreventionRecord() {
    }

    public PreventionRecord(String currentReportId, String currentBdzId, String currentBdzName) {
        this.reportId = currentReportId;
        this.bdz = currentBdzName;
        this.bdzid = currentBdzId;
    }
}
