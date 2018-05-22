package com.cnksi.bdzinspection.model;

import android.text.TextUtils;

import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.Config.InspectionType;
import com.cnksi.xscore.xsutils.DateUtils;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;import com.cnksi.common.model.BaseModel;

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
    @Column(name = STAID,isId = true)
    public String staid;
    @Column(name = DUID)
    public String duid;
    @Column(name = DEVICEID)
    public String deviceId;
    @Column(name = DESCRIPTION)
    public String description;
    @Column(name = RESULTTYPE)
    public String resulttype;
    @Column(name = PICS)
    public String pics;
    @Column(name = CHANGE_PIC)
    public String change_pic;
    @Column(name = SORT)
    public String sort;
    @Column(name = REFERENCE)
    public String reference;
    @Column(name = WTLL)
    public String wtll;
    @Column(name = WTC)
    public String wtc;
    @Column(name = UNIT)
    public String unit;
    @Column(name = KIND)
    public String kind;
    @Column(name = ISDZ)
    public String isdz;
    @Column(name = ORIGIN)
    public String origin;
    @Column(name = CREATETIME)
    public String createtime;
    @Column(name = REPORT_TYPE)
    public String report_type;
    @Column(name = FULL)
    public String full;
    @Column(name = DAYS)
    public String days;
    @Column(name = NEWCAST)
    public String newcast;
    @Column(name = ACCEPT)
    public String accept;
    @Column(name = CREATER)
    public String creater;
    @Column(name = COPYSTATE)
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
