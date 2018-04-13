package com.cnksi.bdzinspection.model;

import android.text.TextUtils;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.daoservice.CopyMapCache;
import com.cnksi.bdzinspection.utils.CalcUtils;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.xscore.xsutils.DateUtils;
import com.cnksi.xscore.xsutils.FunctionUtils;
import com.cnksi.xscore.xsutils.PreferencesUtils;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;

@Table(name = "defect_record")
public class DefectRecord extends BaseModel {

    // 结果ID
    public static final String DEFECTID = "defectid";
    @Id(column = DEFECTID)
    public String defectid;

    // 报告ID
    public static final String REPORTID = "reportid";
    @Column(column = REPORTID)
    public String reportid;

    // 变电站编号
    public static final String BDZID = "bdzid";
    @Column(column = BDZID)
    public String bdzid;

    // 变电站名称
    public static final String BDZ = "bdz";
    @Column(column = BDZ)
    public String bdz;

    // 间隔编号
    public static final String SPID = "spid";
    @Column(column = SPID)
    public String spid;

    // 间隔名称
    public static final String SPNAME = "spname";
    @Column(column = SPNAME)
    public String spname;

    // 设备ID
    public static final String DEVICEID = "deviceid";
    @Column(column = DEVICEID)
    public String deviceid;

    // 设备名称
    public static final String DEVCIE = "devcie";
    @Column(column = DEVCIE)
    public String devcie;

    // 部件ID
    public static final String DUID = "duid";
    @Column(column = DUID)
    public String duid;

    // 部件名称
    public static final String DUNAME = "duname";
    @Column(column = DUNAME)
    public String duname;

    // 发现日期
    public static final String DISCOVERED_DATE = "discovered_date";
    @Column(column = DISCOVERED_DATE)
    public String discovered_date;

    // 发现归属
    public static final String DISCOVERED_BELONGTO = "discovered_belongto";
    @Column(column = DISCOVERED_BELONGTO)
    public String discovered_belongto;

    // 发现人
    public static final String DISCOVERER = "discoverer";
    @Column(column = DISCOVERER)
    public String discoverer;

    //
    public static final String DISCOVERER_UNIT = "discoverer_unit";
    @Column(column = DISCOVERER_UNIT)
    public String discoverer_unit;

    // 检修专业
    public static final String OVERHAUL = "overhaul";
    @Column(column = OVERHAUL)
    public String overhaul;

    // 是否停电
    public static final String ISOUTAGE = "isoutage";
    @Column(column = ISOUTAGE)
    public String isoutage;

    // 发现方式
    public static final String FINDWAY = "findway";
    @Column(column = FINDWAY)
    public String findway;

    // 检测技术
    public static final String DETECTION_MODE = "detection_mode";
    @Column(column = DETECTION_MODE)
    public String detection_mode;

    // 缺陷编号
    public static final String DEFECTCODE = "defectcode";
    @Column(column = DEFECTCODE)
    public String defectcode;

    // 缺陷级别
    public static final String DEFECTLEVEL = "defectlevel";
    @Column(column = DEFECTLEVEL)
    public String defectlevel;

    // 缺陷描述
    public static final String DESCRIPTION = "description";
    @Column(column = DESCRIPTION)
    public String description;

    // 缺陷图片
    public static final String PICS = "pics";
    @Column(column = PICS)
    public String pics;

    // 分类依据
    public static final String CLASSIFY = "classify";
    @Column(column = CLASSIFY)
    public String classify;

    // 缺陷原因
    public static final String REASON = "reason";
    @Column(column = REASON)
    public String reason;

    // 备注
    public static final String REMARK = "remark";
    @Column(column = REMARK)
    public String remark;

    // 抄录数据值
    public static final String VAL = "val";
    @Column(column = VAL)
    public String val;
    // 抄录数据值
    public static final String OLDVAL = "oldval";
    @Column(column = OLDVAL)
    public String oldval;

    // 抄录数据值
    public static final String DZCS = "dzcs";
    @Column(column = DZCS)
    public String dzcs;
    // 抄录数据值
    public static final String OLDDZCS = "old_dzcs";
    @Column(column = OLDDZCS)
    public String olddzcs;
    // 是否被跟踪
    public static final String HAS_TRACK = "has_track";
    @Column(column = HAS_TRACK)
    public String has_track = "N";

    // 更新时间
    public static final String UPDATE_TIME = "update_time";
    @Column(column = UPDATE_TIME)
    public String update_time;

    // 巡视标准Id
    public static final String STADID = "standid";
    @Column(column = STADID)
    public String standid;

    // 定期维护巡视标准Id
    public static final String STADIDSWICHERID = "stand_switch_id";
    @Column(column = STADIDSWICHERID)
    public String standSwitchId;

    // 抄录值单位
    public static final String UNIT = "unit";
    @Column(column = UNIT)
    public String unit;

    // 是否是抄录数据
    public static final String IS_COPY = "is_copy";
    @Column(column = IS_COPY)
    public String is_copy = "N";

    // 是否已消除
    public static final String HAS_REMOVE = "has_remove";
    @Column(column = HAS_REMOVE)
    public String has_remove = "N";


    public static final String REMOVE_DATE = "remove_date";
    @Column(column = REMOVE_DATE)
    public String removeDate;

    public static final String INSERT_TIME = "insert_time";
    @Column(column = INSERT_TIME)
    public String insertTime;

    public static final String CONFIRM_DATE = "confirm_date";
    @Column(column = CONFIRM_DATE)
    public String confirmDate;

    /**
     * 是否影响电网运行
     */
    public static final String HAS_INFLUENCE_DBZ = "has_influence_bdz";
    @Column(column = HAS_INFLUENCE_DBZ)
    public String  hasInfluenceDbz;

    // 是否记录有红外测温问题
    public static final String INFRADE_TEMPERATURE_QUESTION = "has_infrade_temperature_question";
    @Transient
    public boolean has_infrade_temperature_question = false;

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
        this.discoverer = PreferencesUtils.getString(XunshiApplication.getInstance(), Config.CURRENT_LOGIN_USER, "");
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
        this.discoverer = PreferencesUtils.getString(XunshiApplication.getInstance(), Config.CURRENT_LOGIN_USER, "");
        this.discoverer_unit=PreferencesUtils.getString(XunshiApplication.getInstance(),Config.CURRENT_DEPARTMENT_NAME,"");
        this.defectcode = getDefectId();
        this.has_track = "N";
        this.has_remove = "N";
        this.unit = unit;
        this.insertTime = DateUtils.getCurrentLongTime();
    }

    /**
     * Standard DbModel 构造 抄录数据
     */
    public DefectRecord(String reportid, String bdzid, String bdz, DbModel model) {
        this.reportid = reportid;
        this.bdzid = bdzid;
        this.bdz = bdz;
        this.is_copy = "Y";
        this.spid = model.getString(Spacing.SPID);// 间隔ID
        this.spname = model.getString(Spacing.SNAME); // 间隔名称
        this.deviceid = model.getString(Device.DEVICEID);
        this.devcie = model.getString(Device.NAME);
        this.duid = model.getString(DeviceStandards.DUID);
        this.duname = model.getString(DevicePart.NAME); // 设备部件名称;
        this.unit = TextUtils.isEmpty(model.getString(UNIT)) ? "" : model.getString(UNIT);
        this.discoverer = PreferencesUtils.getString(XunshiApplication.getInstance(), Config.CURRENT_LOGIN_USER, "");
        this.description = model.getString(DESCRIPTION); // 巡视标准id
        this.standid = model.getString(DeviceStandards.STAID);
        this.val = model.getString(VAL);
        this.oldval = CopyMapCache.getValue(standid + "_" + deviceid);
        initArresterActionValue();
        this.discovered_date = DateUtils.getCurrentLongTime();
        if (TextUtils.isEmpty(model.getString(DEFECTID))) {
            this.defectid = getDefectId();
        } else {
            this.defectid = model.getString(DEFECTID);
        }
        this.insertTime = DateUtils.getCurrentLongTime();
    }

    public DefectRecord(DefectRecord mDefectRecord) {
        this(mDefectRecord.reportid, mDefectRecord.bdzid, mDefectRecord.bdz, mDefectRecord.spid, mDefectRecord.spname, mDefectRecord.deviceid, mDefectRecord.devcie, mDefectRecord.duid, mDefectRecord.duname, "", "");
    }

    public static String getDefectId() {
        return FunctionUtils.getPrimarykey();
    }

    public void initArresterActionValue() {
        if (this.devcie != null && this.devcie.contains("避雷器")) {
            if (this.description != null && this.description.contains("动作")) {

                this.dzcs = CalcUtils.sub(val, oldval);
                SqlInfo sql = new SqlInfo("SELECT dzcs FROM defect_record where reportid<>? and deviceid=? and standid=? and bdzid=? and dlt=0 ORDER BY discovered_date DESC LIMIT 1", reportid, deviceid, standid, bdzid);
                try {
                    DbModel data = XunshiApplication.getDbUtils().findDbModelFirst(sql);
                    this.olddzcs = data != null ? data.getString(DZCS) : null;
                } catch (DbException e) {
                    e.printStackTrace();
                }

            }
        }
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
        this.defectid = FunctionUtils.getPrimarykey();
        this.insertTime = DateUtils.getCurrentLongTime();
    }
}
