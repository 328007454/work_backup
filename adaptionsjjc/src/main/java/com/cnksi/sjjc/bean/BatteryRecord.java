package com.cnksi.sjjc.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by han on 2016/6/17.
 */
@Table(name = "sbjc_battery")
public class BatteryRecord extends BaseModel implements Serializable {

    //
    public static final String ID = "id";
    @Column(name = ID, isId = true)
    public String id = UUID.randomUUID().toString();


    // 报告ID
    public static final String REPORTID = "reportid";
    @Column(name = REPORTID)
    public String reportid;

    //变电站编号
    public static final String BDZID = "bdzid";
    @Column(name = BDZID)
    public String bdzid;

    // 变电站名称
    public static final String BDZ = "bdz_name";
    @Column(name = BDZ)
    public String bdz;

    // 电池组
    public static final String BATTARY_GROUP = "battary_group";
    @Column(name = BATTARY_GROUP)
    public String battaryGroup;

    // 电池编号
    public static final String BATTARY_CODE = "battary_code";
    @Column(name = BATTARY_CODE)
    public String battary_code;

    // 电压
    public static final String VOLTAGE = "voltage";
    @Column(name = VOLTAGE)
    public String voltage;

    // 内阻
    public static final String RESISTANCE = "resistance";
    @Column(name = RESISTANCE)
    public String resistance;

    // 电压异常照片
    public static final String VOLTAGE_IMAGES = "voltage_images";
    @Column(name = VOLTAGE_IMAGES)
    public String voltageImages;

    // 内阻异常照片
    public static final String RESISTANCE_IMAGES = "resistance_images";
    @Column(name = RESISTANCE_IMAGES)
    public String resistanceImages;

    // 检测类型
    public static final String INSPECTION = "inspection";
    @Column(name = INSPECTION)
    public String inspection;

    // 测试类型
    public static final String TYPE = "type";
    @Column(name = TYPE)
    public int type;

    // 备注
    public static final String REMARK = "remark";
    @Column(name = REMARK)
    public String remark;



    //更新时间
    public static final String LAST_MODIFY_TIME = "last_modify_time";
    @Column(name = LAST_MODIFY_TIME)
    public String last_modify_time;






    public BatteryRecord() {
    }

    public BatteryRecord(String reportid, String bdzid, String bdz, String battaryGroup, String battary_code, String inspection, int type) {
        this.reportid = reportid;
        this.bdzid = bdzid;
        this.bdz = bdz;
        this.battaryGroup = battaryGroup;
        this.battary_code = battary_code;
        this.inspection = inspection;
        this.type = type;
    }
}
