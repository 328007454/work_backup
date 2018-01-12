package com.cnksi.sjjc.bean.hwcw;

import com.cnksi.core.utils.DateUtils;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.UUID;

/**
 * Created by kkk on 2017/12/11.
 */
@Table(name = "report_sbcw_cwwz")
public class HwcwLocation {

    /**
     * Id
     */
    public static final String ID = "id";
    @Column(name = ID, isId = true)
    public String id = UUID.randomUUID().toString();
    /**
     * Id
     */
    public static final String BASEINFO_ID = "baseinfo_id";
    @Column(name = BASEINFO_ID)
    public String baseInfoID;


    /**
     * 设备ID
     */
    public static final String DEVICE_ID = "device_id";
    @Column(name = DEVICE_ID)
    public String deviceID;

    /**
     * 设备名称
     */
    public static final String DEVICE_NAME = "device_name";
    @Column(name = DEVICE_NAME)
    public String deviceName;

    /**
     * 间隔ID
     */
    public static final String SPACINGID = "spacing_id";
    @Column(name = SPACINGID)
    public String spaceID;

    /**
     * 间隔名称
     */
    public static final String SPACING_NAME = "spacing_name";
    @Column(name = SPACING_NAME)
    public String spacingName;

    /**
     * 额定电流
     */
    public static final String EDDL = "eddl";
    @Column(name = EDDL)
    public String ratedCurrent;

    /**
     * 负荷电流
     */
    public static final String FHDL = "fhdl";
    @Column(name = FHDL)
    public String fhdl;

    /**
     * 发热部位，json格式{{"bw_name":"A相温度","wd":""},{"bw_name":"B相温度","wd":""}}
     */
    public static final String HOT_PART = "frbw";
    @Column(name = HOT_PART)
    public String hotPart;


    /**
     * 是否删除
     */
    public static final String DLT = "dlt";
    @Column(name = DLT)
    public int dlt;

    /**
     * 更新时间
     */
    public static final String LAST_MODIFY_TIME = "last_modify_time";
    @Column(name = LAST_MODIFY_TIME)
    public String modifyTime = DateUtils.getCurrentLongTime();


    public HwcwLocation() {
    }



    public HwcwLocation(String baseInfoID, String spaceID, String deviceID, String deviceName, String spacingName, String ratedCurrent, String loadCurrent, String hotPart) {
        this.baseInfoID = baseInfoID;
        this.spaceID = spaceID;
        this.deviceID = deviceID;
        this.deviceName = deviceName;
        this.spacingName = spacingName;
        this.fhdl = loadCurrent;
        this.ratedCurrent = ratedCurrent;
        this.hotPart = hotPart;
        this.dlt = 0;

    }
}
