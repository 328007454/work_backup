package com.cnksi.bdzinspection.model;

import android.text.TextUtils;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.xscore.xsutils.DateUtils;
import com.cnksi.xscore.xsutils.StringUtils;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Table(name = "report_snwsd")
public class ReportSnwsd extends BaseModel {

    /***/
    public static final String ID = "id";
    @Column(name = "id",isId = true)
    public String id = UUID.randomUUID().toString();

    /**
     * 小室
     */
    public static final String GROUP_ID = "group_id";
    @Column(name = GROUP_ID)
    public String groupID;

    /**
     * 报告id
     */
    public static final String GROUP_NAME = "group_name";
    @Column(name = GROUP_NAME)
    public String groupName;

    /**
     * 是否是来自于report
     */
    public static final String IS_REPORT = "is_report";
    @Column(name = IS_REPORT)
    public String isReport;

    /**
     * 报告id
     */
    public static final String REPORT_ID = "report_id";
    @Column(name = REPORT_ID)
    public String report_id;

    /**
     * 变电站id
     */
    public static final String BDZ_ID = "bdz_id";
    @Column(name = BDZ_ID)
    public String bdz_id;

    /**
     * 变电站名称
     */
    public static final String BDZ_NAME = "bdz_name";
    @Column(name = BDZ_NAME)
    public String bdz_name;

    /**
     * 设备id
     */
    public static final String DEVICE_ID = "device_id";
    @Column(name = DEVICE_ID)
    public String device_id;

    /**
     * 设备名称
     */
    public static final String DEVICE_NAME = "device_name";
    @Column(name = DEVICE_NAME)
    public String device_name;

    /**
     * 温度
     */
    public static final String WD = "wd";
    @Column(name = WD)
    public String wd;

    /**
     * 湿度
     */
    public static final String SD = "sd";
    @Column(name = SD)
    public String sd;

    /**
     * 地域
     */
    public static final String LOCATION = "location";
    @Column(name = LOCATION)
    public String location;

    /**
     * 插入时间
     */
    public static final String INSERT_TIME = "insert_time";
    @Column(name = INSERT_TIME)
    public String insert_time;

    /**
     * 最后更新时间
     */
    public static final String LAST_MODIFY_TIME = "last_modify_time";
    @Column(name = LAST_MODIFY_TIME)
    public String last_modify_time;

    public ReportSnwsd() {
        this.insert_time = DateUtils.getCurrentLongTime();
        this.dlt = "0";
    }

    public ReportSnwsd(String currentReportId, String currentBdzId, String currentBdzName, SpacingGroup group, String wd, String sd) {
        this.report_id = currentReportId;
        this.bdz_id = currentBdzId;
        this.bdz_name = currentBdzName;
        this.groupID = group.id;
        this.groupName = group.name;
        this.isReport = "Y";
        this.last_modify_time = DateUtils.getCurrentLongTime();
        this.insert_time = DateUtils.getCurrentLongTime();
        this.location = group.name;
        this.dlt = "0";
        this.wd = wd;
        this.sd = sd;
    }

    public static HashSet<ReportSnwsd> getAllCopySNWSD(String reportID) {
        List<ReportSnwsd> snwsds = new ArrayList<ReportSnwsd>();
        try {
            snwsds = XunshiApplication.getDbUtils().selector(ReportSnwsd.class).where(ReportSnwsd.REPORT_ID, "=", reportID).and(ReportSnwsd.DLT, "=", "0").findAll();
        } catch (DbException e) {
            e.printStackTrace();
            return new HashSet<>(snwsds);
        }
        return new HashSet<>(snwsds);
    }

    public void updateCopyValue(String temp, String sd) {
        this.wd = temp;
        this.sd = sd;
        this.last_modify_time = DateUtils.getCurrentLongTime();
    }

    public boolean judgeValueNormal(String temp, String sd) {
        if (TextUtils.isEmpty(StringUtils.getTransformTep(wd))) {
            return false;
        } else if ((-99.9f > new Float(wd) || new Float(wd) > 99.99)) {
            return false;
        }
        if (TextUtils.isEmpty(StringUtils.getTransformTep(sd))) {
            return false;
        } else if ((0 > new Float(sd) || new Float(sd) > 100)) {
            return false;
        }
        return true;
    }
}
