package com.cnksi.sjjc.bean.upload;

import android.text.TextUtils;

import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.FileUtils;
import com.cnksi.common.Config;
import com.cnksi.common.model.BaseModel;
import com.cnksi.common.enmu.InspectionType;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 巡检标准
 *
 * @author Oliver
 */
@Table(name = "device_standards")
public class UploadDeviceStandards extends BaseModel {

    public UploadDeviceStandards() {
        this.dlt = "0";
        this.createtime = DateUtils.getCurrentLongTime();
        this.resulttype = "0";
        this.sort = "0";
        this.accept = "N";
        this.full = "N";
        this.days = "N";
        this.newcast = "N";
        this.copystate = "0";
        this.report_type = "0";
    }

    // 巡检标准序号
    public static final String STAID = "staid";
    @Column(name = STAID, isId = true)
    public String staid;

    // 设备部件编号
    public static final String DUID = "duid";
    @Column(name = DUID)
    public String duid;

    // 描述
    public static final String DESCRIPTION = "description";
    @Column(name = DESCRIPTION)
    public String description;

    // 巡检结果类型 0正确与不正确 1手动输入
    public static final String RESULTTYPE = "resulttype";
    @Column(name = RESULTTYPE)
    public String resulttype;

    // 巡检标准图片
    public static final String PICS = "pics";
    @Column(name = PICS)
    public String pics;

    // 更换的照片
    public static final String CHANGE_PIC = "change_pic";
    @Column(name = CHANGE_PIC)
    public String change_pic;

    // 排序
    public static final String SORT = "sort";
    @Column(name = SORT)
    public String sort;

    // 参考标准
    public static final String REFERENCE = "reference";
    @Column(name = REFERENCE)
    public String reference;

    // 预警下限
    public static final String WTLL = "wtll";
    @Column(name = WTLL)
    public String wtll;

    // 预警上限
    public static final String WTC = "wtc";
    @Column(name = WTC)
    public String wtc;

    // 单位
    public static final String UNIT = "unit";
    @Column(name = UNIT)
    public String unit;

    // 标准类型
    public static final String KIND = "kind";
    @Column(name = KIND)
    public String kind;



    // 是否是定值核对
    public static final String ISDZ = "isdz";
    @Column(name = ISDZ)
    public String isdz;

    // 缺陷来源
    public static final String ORIGIN = "origin";
    @Column(name = ORIGIN)
    public String origin;

    // 创建时间
    public static final String CREATETIME = "createtime";
    @Column(name = CREATETIME)
    public String createtime;

    public static final String REPORT_TYPE = "report_type";
    @Column(name = REPORT_TYPE)
    public String report_type;

    public static final String FULL = "full";
    @Column(name = FULL)
    public String full;

    public static final String DAYS = "days";
    @Column(name = DAYS)
    public String days;

    public static final String NEWCAST = "newcast";
    @Column(name = NEWCAST)
    public String newcast;

    public static final String ACCEPT = "accept";
    @Column(name = ACCEPT)
    public String accept;

    public static final String CREATER = "creater";
    @Column(name = CREATER)
    public String creater;

    public static final String COPYSTATE = "copystate";
    @Column(name = COPYSTATE)
    public String copystate;

    // 标准对应的缺陷数量
    public static final String DEFECT_COUNT = "defect_count";

    // 是否已记录缺陷
    public static final String HAS_RECORD_DEFECT = "has_record_defect";

    /**
     * 是否是日常巡检标准(重点关注标准)
     *
     * @return
     */
    public boolean isImportant() {
        return !TextUtils.isEmpty(kind) && kind.contains(InspectionType.day.name());
    }

    // 判断设备部件的图片是否存在，不存在采用默认的图片
    public static String getPic(String pic) {
        if (!FileUtils.isFileExists(Config.PICTURES_FOLDER + pic)){
            pic = "default.png";
        }
        return pic;
    }

}
