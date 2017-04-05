package com.cnksi.sjjc.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;
import java.util.UUID;

/**
 * 设备检测-收发信机
 *
 * @author luoxy
 * @date 2016/6/15
 * @copyRight
 */
@Table(name = "sbjc_transceiver")
public class Transceiver extends BaseModel {

    public static final String ID = "id";
    @Column(name = ID, isId = true)
    public String id = UUID.randomUUID().toString();

    /**
     * 报告Id
     */
    public static final String REPORDID = "reportid";
    @Column(name = REPORDID)
    public String reportId;

    /**
     * 变电站id
     */
    public static final String BDZID = "bdzid";
    @Column(name = BDZID)
    public String bdzId;

    /**
     * 变电站
     */
    public static final String BDZ = "bdz_name";
    @Column(name = BDZ)
    public String bdz;

    /**
     * 设备Id
     */
    public static final String DEVICEID = "deviceid";
    @Column(name = DEVICEID)
    public String deviceId;
    /**
     * 设备
     */
    public static final String DEVICENAME = "device_name";
    @Column(name = DEVICENAME)
    public String deviceName;

    /**
     * 发信电平
     */
    public static final String SENDLEVEL = "send_level";
    @Column(name = SENDLEVEL)
    public String sendLevel;

    /**
     * 收信电平
     */
    public static final String RECEIVELEVEL = "receive_level";
    @Column(name = RECEIVELEVEL)
    public String receiveLevel;

    /**
     * 通道状态 (0-正常 1-异常)
     */
    public static final String CHANNELSTATUS = "channel_status";
    @Column(name = CHANNELSTATUS)
    public int channelStatus;

    /**
     * 异常拍照
     */
    public static final String IMAGES = "images";
    @Column(name = IMAGES)
    public String images;

    /**
     * 备注
     */
    public static final String REMARK = "remark";
    @Column(name = REMARK)
    public String remark;



    /**
     * 更新时间
     */
    public static final String LASTMODIFYTIME = "last_modify_time";
    @Column(name = LASTMODIFYTIME)
    public Date lastModifyTime = new Date();

    public Transceiver(String reportId, String bdzId, String bdz, String deviceId, String deviceName) {
        this.reportId = reportId;
        this.bdzId = bdzId;
        this.bdz = bdz;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
    }

    public Transceiver() {

    }
}
