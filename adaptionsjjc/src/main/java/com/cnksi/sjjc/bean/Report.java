package com.cnksi.sjjc.bean;

import com.cnksi.common.model.BaseModel;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.sjjc.util.FunctionUtils;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "report")
public class Report extends BaseModel {

    // 报告ID
    public static final String REPORTID = "reportid";
    @Column(name = REPORTID, isId = true)
    public String reportid;

    // 变电站编号
    public static final String BDZID = "bdzid";
    @Column(name = BDZID)
    public String bdzid;

    // 变电站名称
    public static final String BDZ = "bdz";
    @Column(name = BDZ)
    public String bdz;

    //
    public static final String INSPECTION = "inspection";
    @Column(name = INSPECTION)
    public String inspection;

    // 巡检人员
    public static final String PERSONS = "persons";
    @Column(name = PERSONS)
    public String persons;

    // 温度
    public static final String TEMPERATURE = "temperature";
    @Column(name = TEMPERATURE)
    public String temperature;

    // 湿度
    public static final String HUMIDITY = "humidity";
    @Column(name = HUMIDITY)
    public String humidity;

    //
    public static final String TOOLS = "tools";
    @Column(name = TOOLS)
    public String tools;

    // 巡检开始时间
    public static final String STARTTIME = "starttime";
    @Column(name = STARTTIME)
    public String starttime;

    // 巡检结束时间
    public static final String ENDTIME = "endtime";
    @Column(name = ENDTIME)
    public String endtime;

    //
    public static final String SUBMITTIME = "submittime";
    @Column(name = SUBMITTIME)
    public String submittime;

    // 任务id
    public static final String TASK_ID = "taskid";
    @Column(name = TASK_ID)
    public String taskid;

    // 是否上传 默认 Y
    public static final String IS_UPLOAD = "is_upload";
    @Column(name = IS_UPLOAD)
    public String is_upload = "Y";

    // 运维一体化 遗留问题
    public static final String REMAIN_PROBLEMS = "remain_problems";
    @Column(name = REMAIN_PROBLEMS)
    public String remain_problems;

    //天气
    public static final String TQ = "tq";
    @Column(name = TQ)
    public String tq;

    //备注
    public static final String REMARK = "remark";
    @Column(name = REMARK)
    public String remark;
    /**
     * 检查情况
     */
    public static final String JCQK = "jcqk";
    @Column(name = JCQK)
    public String jcqk;
    /**
     * 是否普测(只适用于蓄电池测试、内阻测试)<br>
     * 0/普，1/抽
     */
    public static final String CSLX = "cslx";
    @Column(name = CSLX)
    public int checkType;

    public static final String SELECTED_DEVICEID = "selected_deviceid";
    @Column(name = SELECTED_DEVICEID)
    public String selected_deviceid;
    /**
     * 是否测试内阻(只适用于蓄电池测试)<br>
     * 0/不测，1/测试内阻
     */
    public static final String NZJC = "nzjc";
    @Column(name = NZJC)
    public int resistanceIsCheck;

    // 巡检内容
    public static final String INSPECTIONCONTENT = "inspection_content";
    @Column(name = INSPECTIONCONTENT)
    public String inspectionContent;

    // 巡检描述
    public static final String INSPECTIONREMARK = "inspection_remark";
    @Column(name = INSPECTIONREMARK)
    public String inspectionRemark;

    public static final String INSERT_TIME = "insert_time";
    @Column(name = INSERT_TIME)
    public String insertTime;

    public static final String INSPECTION_VALUE = "inspection_value";
    @Column(name = INSPECTION_VALUE)
    public String inspectionValue;

    public static final String REPORT_SOURCE = "report_source";
    @Column(name = REPORT_SOURCE)
    public String reportSource;

    public static final String DEPARTMENTID = "dept_id";
    @Column(name = DEPARTMENTID)
    public String departmentId;


    public static final String REPSWITCHOVERID = "rep_swithover_id";
    @Column(name = REPSWITCHOVERID)
    public String repSwithoverId;

    public Report() {
    }

    public Report(String reportid) {
        this.reportid = reportid;
        this.endtime = DateUtils.getCurrentLongTime();
        this.insertTime = DateUtils.getCurrentLongTime();
    }

    public Report(String taskid, String bdzid, String bdz, String inspection, String persons, String temperature, String humidity) {
        this.reportid = FunctionUtils.getPrimarykey();
        this.starttime = DateUtils.getCurrentLongTime();
        this.taskid = taskid;
        this.bdz = bdz;
        this.bdzid = bdzid;
        this.persons = persons;
        this.inspection = inspection;
        this.temperature = temperature;
        this.humidity = humidity;
        this.insertTime = DateUtils.getCurrentLongTime();
    }

    public Report(String taskid, String bdzid, String bdz, String inspection, String persons) {
        this.reportid = FunctionUtils.getPrimarykey();
        this.starttime = DateUtils.getCurrentLongTime();
        this.taskid = taskid;
        this.bdz = bdz;
        this.bdzid = bdzid;
        this.persons = persons;
        this.inspection = inspection;
        this.temperature = "";
        this.humidity = "";
        this.insertTime = DateUtils.getCurrentLongTime();
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }
}
