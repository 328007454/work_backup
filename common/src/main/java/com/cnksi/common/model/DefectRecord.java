package com.cnksi.common.model;

import android.text.TextUtils;


import com.cnksi.common.Config;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.PreferencesUtils;
import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

@Table(name = "defect_record")
public class DefectRecord extends BaseModel implements Serializable{

    // 结果ID
    public static final String DEFECTID = "defectid";
    @Column(name = DEFECTID, isId = true)
    public String defectid;

    // 报告ID
    public static final String REPORTID = "reportid";
    @Column(name = REPORTID)
    public String reportid;

    // 变电站编号
    public static final String BDZID = "bdzid";
    @Column(name = BDZID)
    public String bdzid;

    // 变电站名称
    public static final String BDZ = "bdz";
    @Column(name = BDZ)
    public String bdz;

    // 间隔编号
    public static final String SPID = "spid";
    @Column(name = SPID)
    public String spid;

    // 间隔名称
    public static final String SPNAME = "spname";
    @Column(name = SPNAME)
    public String spname;

    // 设备ID
    public static final String DEVICEID = "deviceid";
    @Column(name = DEVICEID)
    public String deviceid;

    // 设备名称
    public static final String DEVCIE = "devcie";
    @Column(name = DEVCIE)
    public String devcie;

    // 部件ID
    public static final String DUID = "duid";
    @Column(name = DUID)
    public String duid;

    // 部件名称
    public static final String DUNAME = "duname";
    @Column(name = DUNAME)
    public String duname;

    // 发现日期
    public static final String DISCOVERED_DATE = "discovered_date";
    @Column(name = DISCOVERED_DATE)
    public String discovered_date;

    // 发现归属
    public static final String DISCOVERED_BELONGTO = "discovered_belongto";
    @Column(name = DISCOVERED_BELONGTO)
    public String discovered_belongto;

    // 发现人
    public static final String DISCOVERER = "discoverer";
    @Column(name = DISCOVERER)
    public String discoverer;

    //
    public static final String DISCOVERER_UNIT = "discoverer_unit";
    @Column(name = DISCOVERER_UNIT)
    public String discoverer_unit;

    // 检修专业
    public static final String OVERHAUL = "overhaul";
    @Column(name = OVERHAUL)
    public String overhaul;

    // 是否停电
    public static final String ISOUTAGE = "isoutage";
    @Column(name = ISOUTAGE)
    public String isoutage;

    // 发现方式
    public static final String FINDWAY = "findway";
    @Column(name = FINDWAY)
    public String findway;

    // 检测技术
    public static final String DETECTION_MODE = "detection_mode";
    @Column(name = DETECTION_MODE)
    public String detection_mode;

    // 缺陷编号
    public static final String DEFECTCODE = "defectcode";
    @Column(name = DEFECTCODE)
    public String defectcode;

    // 缺陷级别
    public static final String DEFECTLEVEL = "defectlevel";
    @Column(name = DEFECTLEVEL)
    public String defectlevel;

    // 缺陷描述
    public static final String DESCRIPTION = "description";
    @Column(name = DESCRIPTION)
    public String description;

    // 缺陷图片
    public static final String PICS = "pics";
    @Column(name = PICS)
    public String pics;

    // 分类依据
    public static final String CLASSIFY = "classify";
    @Column(name = CLASSIFY)
    public String classify;

    // 缺陷原因
    public static final String REASON = "reason";
    @Column(name = REASON)
    public String reason;

    // 备注
    public static final String REMARK = "remark";
    @Column(name = REMARK)
    public String remark;

    // 抄录数据值
    public static final String VAL = "val";
    @Column(name = VAL)
    public String val;
    // 抄录数据值
    public static final String OLDVAL = "oldval";
    @Column(name = OLDVAL)
    public String oldval;

    // 抄录数据值
    public static final String DZCS = "dzcs";
    @Column(name = DZCS)
    public String dzcs;
    // 抄录数据值
    public static final String OLDDZCS = "old_dzcs";
    @Column(name = OLDDZCS)
    public String olddzcs;
    // 是否被跟踪
    public static final String HAS_TRACK = "has_track";
    @Column(name = HAS_TRACK)
    public String has_track = "N";

    // 更新时间
    public static final String UPDATE_TIME = "update_time";
    @Column(name = UPDATE_TIME)
    public String update_time;

    // 巡视标准Id
    public static final String STADID = "standid";
    @Column(name = STADID)
    public String standid;

    // 定期维护巡视标准Id
    public static final String STADIDSWICHERID = "stand_switch_id";
    @Column(name = STADIDSWICHERID)
    public String standSwitchId;

    // 抄录值单位
    public static final String UNIT = "unit";
    @Column(name = UNIT)
    public String unit;

    // 是否是抄录数据
    public static final String IS_COPY = "is_copy";
    @Column(name = IS_COPY)
    public String is_copy = "N";

    // 是否已消除
    public static final String HAS_REMOVE = "has_remove";
    @Column(name = HAS_REMOVE)
    public String has_remove = "N";


    public static final String REMOVE_DATE = "remove_date";
    @Column(name = REMOVE_DATE)
    public String removeDate;

    public static final String INSERT_TIME = "insert_time";
    @Column(name = INSERT_TIME)
    public String insertTime;

    public static final String CONFIRM_DATE = "confirm_date";
    @Column(name = CONFIRM_DATE)
    public String confirmDate;

    /**
     * 是否影响电网运行
     */
    public static final String HAS_INFLUENCE_DBZ = "has_influence_bdz";
    @Column(name = HAS_INFLUENCE_DBZ)
    public String hasInfluenceDbz;

    // 是否记录有红外测温问题
    public static final String INFRADE_TEMPERATURE_QUESTION = "has_infrade_temperature_question";
    public transient boolean has_infrade_temperature_question = false;

    public DefectRecord() {
    }

    /**
     * 缺陷构造函数
     *
     * @param reportid    报告ID
     * @param bdzid       变电站ID
     * @param bdz         变电站名称
     * @param spid        间隔ID
     * @param spname      间隔名称
     * @param deviceid    设备ID
     * @param device      设备名称
     * @param duid        部件ID
     * @param duname      部件名称
     * @param defectlevel 缺陷性质
     * @param description 缺陷描述
     * @param pics        缺陷图片
     */
    public DefectRecord(String reportid, String bdzid, String bdz, String spid, String spname, String deviceid, String device, String duid, String duname, String defectlevel, String description, String standid, String pics) {
        // defectid,discovered_date,discoverer ,defectcode
        this.reportid = reportid;
        this.bdzid = bdzid;
        this.bdz = bdz;
        this.spid = spid;
        this.spname = spname;
        this.deviceid = deviceid;
        this.devcie = device;
        this.duid = duid;
        this.duname = duname;
        this.defectlevel = defectlevel;
        this.description = description;
        this.pics = pics;
        this.defectid = getDefectId();
        this.discovered_date = DateUtils.getCurrentLongTime();
        this.discoverer = PreferencesUtils.get( Config.CURRENT_LOGIN_USER, "");
        this.defectcode = getDefectId();
        this.standid = standid;
        this.standSwitchId = standid;
        this.has_track = "N";
        this.has_remove = "N";
        this.insertTime = DateUtils.getCurrentLongTime();
    }

    /**
     * 缺陷构造函数
     *
     * @param reportid 报告ID
     * @param bdzid    变电站ID
     * @param bdz      变电站名称
     * @param spid     间隔ID
     * @param spname   间隔名称
     * @param deviceid 设备ID
     * @param device   设备名称
     * @param duid     部件ID
     * @param duname   部件名称
     */
    public DefectRecord(String reportid, String bdzid, String bdz, String spid, String spname, String deviceid, String device, String duid, String duname, String val, String unit) {
        this.reportid = reportid;
        this.bdzid = bdzid;
        this.bdz = bdz;
        this.spid = spid;
        this.spname = spname;
        this.deviceid = deviceid;
        this.devcie = device;
        this.duid = duid;
        this.duname = duname;
        this.val = val;
        this.defectid = getDefectId();
        this.discovered_date = DateUtils.getCurrentLongTime();
        this.discoverer = PreferencesUtils.get(Config.CURRENT_LOGIN_USER, "");
        this.discoverer_unit = PreferencesUtils.get(Config.CURRENT_DEPARTMENT_NAME, "");
        this.defectcode = getDefectId();
        this.has_track = "N";
        this.has_remove = "N";
        this.unit = unit;
        this.insertTime = DateUtils.getCurrentLongTime();
    }




    public DefectRecord(DefectRecord mDefectRecord) {
        this(mDefectRecord.reportid, mDefectRecord.bdzid, mDefectRecord.bdz, mDefectRecord.spid, mDefectRecord.spname, mDefectRecord.deviceid, mDefectRecord.devcie, mDefectRecord.duid, mDefectRecord.duname, "", "");
    }

    public DefectRecord(String reportid,String bdzid, String spaceId, String spaceName, String deviceId, String deviceName, String defectLevel, String pics, String defectContent,String hasInfluenceDbz,String userName,String department) {
       this.reportid = reportid;
        this.bdzid = bdzid;
       this.spid = spaceId;
       this.spname =  spaceName;
       this.deviceid = deviceId;
       this.devcie = deviceName;
       this.defectlevel = defectLevel;
       this.pics = pics;
       this.description = defectContent;
       this.discovered_date = DateUtils.getCurrentLongTime();
       this.defectid = getDefectId();
       this.defectcode = getDefectId();
       this.has_remove = "N";
       this.has_track = "N";
       this.hasInfluenceDbz = hasInfluenceDbz;
       this.discoverer = userName;
       this.discoverer_unit = department;
       this.insertTime = DateUtils.getCurrentLongTime();
    }

    public static String getDefectId() {
        return getPrimarykey();
    }


    public DefectRecord(String currentReportId, String currentBdzId, String currentBdzName, DefectRecord defectRecord) {
        this.reportid = currentReportId;
        this.bdzid = currentBdzId;
        this.bdz = currentBdzName;
        this.discovered_date = DateUtils.getCurrentLongTime();
        this.is_copy = "Y";
        this.standSwitchId = defectRecord.standSwitchId;
        this.val = defectRecord.val;
        this.oldval = defectRecord.oldval;
        if (TextUtils.isEmpty(defectRecord.defectid)) {
            this.defectid = getDefectId();
        } else {
            this.defectid = defectRecord.defectid;
        }
        this.insertTime = DateUtils.getCurrentLongTime();
    }

    public DefectRecord(String currentReportId, String currentBdzId, String currentBdzName) {
        this.reportid = currentReportId;
        this.bdzid = currentBdzId;
        this.bdz = currentBdzName;
        this.discovered_date = DateUtils.getCurrentLongTime();
        this.is_copy = "Y";
        this.defectid =getPrimarykey();
        this.insertTime = DateUtils.getCurrentLongTime();
    }
}
