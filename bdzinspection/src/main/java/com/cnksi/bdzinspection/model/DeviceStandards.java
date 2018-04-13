package com.cnksi.bdzinspection.model;

import android.text.TextUtils;

import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.Config.InspectionType;
import com.cnksi.xscore.xsutils.DateUtils;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

import java.io.File;

/**
 * 巡检标准
 *
 * @author Oliver
 */
@Table(name = "device_standards")
public class DeviceStandards extends BaseModel {

    // 巡检标准序号
    public static final String STAID = "staid";
    // 设备部件编号
    public static final String DUID = "duid";
    // 设备部件编号
    public static final String DEVICEID = "device_id";
    // 描述
    public static final String DESCRIPTION = "description";
    // 巡检结果类型 0正确与不正确 1手动输入
    public static final String RESULTTYPE = "resulttype";
    // 巡检标准图片
    public static final String PICS = "pics";
    // 更换的照片
    public static final String CHANGE_PIC = "change_pic";
    // 排序
    public static final String SORT = "sort";
    // 参考标准
    public static final String REFERENCE = "reference";
    // 预警下限
    public static final String WTLL = "wtll";
    // 预警上限
    public static final String WTC = "wtc";
    // 单位
    public static final String UNIT = "unit";
    // 标准类型
    public static final String KIND = "kind";
    // 是否是定值核对
    public static final String ISDZ = "isdz";
    // 缺陷来源
    public static final String ORIGIN = "origin";
    // 创建时间
    public static final String CREATETIME = "createtime";
    public static final String REPORT_TYPE = "report_type";
    public static final String FULL = "full";
    public static final String DAYS = "days";
    public static final String NEWCAST = "newcast";
    public static final String ACCEPT = "accept";
    public static final String CREATER = "creater";
    public static final String COPYSTATE = "copystate";
    // 标准对应的缺陷数量
    public static final String DEFECT_COUNT = "defect_count";
    // 是否已记录缺陷
    public static final String HAS_RECORD_DEFECT = "has_record_defect";
    @Id(column = STAID)
    public String staid;
    @Column(column = DUID)
    public String duid;
    @Column(column = DEVICEID)
    public String deviceId;
    @Column(column = DESCRIPTION)
    public String description;
    @Column(column = RESULTTYPE)
    public String resulttype;
    @Column(column = PICS)
    public String pics;
    @Column(column = CHANGE_PIC)
    public String change_pic;
    @Column(column = SORT)
    public String sort;
    @Column(column = REFERENCE)
    public String reference;
    @Column(column = WTLL)
    public String wtll;
    @Column(column = WTC)
    public String wtc;
    @Column(column = UNIT)
    public String unit;
    @Column(column = KIND)
    public String kind;
    @Column(column = ISDZ)
    public String isdz;
    @Column(column = ORIGIN)
    public String origin;
    @Column(column = CREATETIME)
    public String createtime;
    @Column(column = REPORT_TYPE)
    public String report_type;
    @Column(column = FULL)
    public String full;
    @Column(column = DAYS)
    public String days;
    @Column(column = NEWCAST)
    public String newcast;
    @Column(column = ACCEPT)
    public String accept;
    @Column(column = CREATER)
    public String creater;
    @Column(column = COPYSTATE)
    public String copystate;

    public DeviceStandards() {
        this.dlt = "0";
        this.createtime = DateUtils.getCurrentLongTime();
        this.resulttype = "0";
        this.sort = "-1";
        this.accept = "N";
        this.full = "N";
        this.days = "N";
        this.newcast = "N";
        this.copystate = "0";
        this.report_type = "0";
    }

    private DeviceStandards(Standards standard) {
        this.accept = standard.accept;
        this.copystate = standard.copystate;
        this.creater = standard.creater;
        this.createtime = standard.createtime;
        this.days = standard.days;
        this.description = standard.description;
        this.dlt = standard.dlt;
        this.duid = standard.duid;
        this.full = standard.full;
        this.kind = standard.kind;
        this.newcast = standard.newcast;
        this.origin = standard.origin;
        this.pics = standard.pics;
        this.report_type = standard.report_type;
        this.resulttype = standard.resulttype;
        this.sort = standard.sort;
        this.staid = standard.staid;
        this.unit = standard.unit;
        this.wtc = standard.wtc;
        this.wtll = standard.wtll;

    }

    private DeviceStandards(StandardSpecial standard) {
        this.description = standard.description;
        this.dlt = standard.dlt;
        this.kind = standard.kind;
        this.origin = standard.origin;
        this.sort = standard.sort;
    }

    // 判断设备部件的图片是否存在，不存在采用默认的图片
    public static String getPic(String pic) {
        if (!TextUtils.isEmpty(pic) && new File(Config.BDZ_INSPECTION_FOLDER + pic).exists()) {
            pic = Config.BDZ_INSPECTION_FOLDER + pic;
        } else {
            pic = Config.DEFALUTFOLDER + "device_pic.png";
        }
        return pic;
    }

    public static DeviceStandards createFromLibStandards(Standards standards) {
        return new DeviceStandards(standards);
    }

    public static DeviceStandards createFromStandardSpecial(StandardSpecial special) {
        return new DeviceStandards(special);
    }

    /**
     * 是否是日常巡检标准(重点关注标准)
     *
     * @return
     */
    public boolean isImportant() {
        if (!TextUtils.isEmpty(kind) && kind.contains(InspectionType.day.name())) {
            return true;
        } else {
            return false;
        }
    }

}
