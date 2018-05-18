package com.cnksi.bdzinspection.model;

import android.text.TextUtils;

import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.SystemConfig;
import com.cnksi.xscore.xsutils.DateUtils;
import com.cnksi.xscore.xsutils.FunctionUtils;
import com.cnksi.xscore.xsutils.PreferencesUtils;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;


@Table(name = "report")
public class Report extends BaseModel {

    // 报告ID
    public static final String REPORTID = "reportid";
    // 变电站编号
    public static final String BDZID = "bdzid";
    // 变电站名称
    public static final String BDZ = "bdz";
    //
    public static final String INSPECTION = "inspection";
    // 巡检人员
    public static final String PERSONS = "persons";
    // 温度
    public static final String TEMPERATURE = "temperature";
    // 湿度
    public static final String HUMIDITY = "humidity";
    //
    public static final String TOOLS = "tools";
    // 巡检开始时间
    public static final String STARTTIME = "starttime";
    // 巡检结束时间
    public static final String ENDTIME = "endtime";
    //
    public static final String SUBMITTIME = "submittime";
    // 任务id
    public static final String TASK_ID = "taskid";
    // 是否上传 默认 Y
    public static final String IS_UPLOAD = "is_upload";
    // 是否上传 默认 Y
    public static final String PLACED_WAY = "placed_way";
    // 运维一体化 遗留问题
    public static final String REMAIN_PROBLEMS = "remain_problems";
    // 天气
    public static final String TQ = "tq";
    // PMS 计划ID
    public static final String PMS_JHID = "pms_jhid";
    // 巡检内容
    public static final String INSPECTIONCONTENT = "inspection_content";
    // 巡检描述
    public static final String INSPECTIONREMARK = "inspection_remark";
    /**
     * 检查情况
     */
    public static final String JCQK = "jcqk";
    public static final String INSERT_TIME = "insert_time";
    /**
     * 选择设备
     */
    public static final String SELECTED_DEVICEID = "selected_deviceid";
    /**
     * 选择维护类型id
     */
    public static final String REPSWITCHOVERID = "rep_swithover_id";
    public static final String INSPECTION_VALUE = "inspection_value";
    public static final String REPORT_SOURCE = "report_source";
    public static final String DEPARTMENTID = "dept_id";
    public static final String AUDIO_URL = "audio_url";
    /**
     * 是否普测(只适用于蓄电池测试、内阻测试)<br>
     * 0/普，1/抽
     */
    public static final String CSLX = "cslx";
    @Column(name = REPORTID,isId = true)
    public String reportid;
    @Column(name = BDZID)
    public String bdzid;
    @Column(name = BDZ)
    public String bdz;
    @Column(name = INSPECTION)
    public String inspection;
    @Column(name = PERSONS)
    public String persons;
    @Column(name = TEMPERATURE)
    public String temperature;
    @Column(name = HUMIDITY)
    public String humidity;
    @Column(name = TOOLS)
    public String tools;
    @Column(name = STARTTIME)
    public String starttime;
    @Column(name = ENDTIME)
    public String endtime;
    @Column(name = SUBMITTIME)
    public String submittime;
    @Column(name = TASK_ID)
    public String taskid;
    @Column(name = IS_UPLOAD)
    public String is_upload = "Y";
    @Column(name = PLACED_WAY)
    public String placedWay;
    @Column(name = REMAIN_PROBLEMS)
    public String remain_problems;
    @Column(name = TQ)
    public String tq;
    @Column(name = PMS_JHID)
    public String pmsJhid;
    @Column(name = INSPECTIONCONTENT)
    public String inspectionContent;
    @Column(name = INSPECTIONREMARK)
    public String inspectionRemark;
    @Column(name = JCQK)
    public String jcqk;
    @Column(name = INSERT_TIME)
    public String insertTime;
    @Column(name = SELECTED_DEVICEID)
    public String selected_deviceid;
    @Column(name = REPSWITCHOVERID)
    public String repSwithoverId;
    @Column(name = INSPECTION_VALUE)
    public String inspectionValue;
    @Column(name = REPORT_SOURCE)
    public String reportSource;
    @Column(name = DEPARTMENTID)
    public String departmentId;
    @Column(name = CSLX)
    public int checkType;
    /**
     * 试验维护整个任务录音文件地址
     */
    @Column(name = AUDIO_URL)
    public String audioUrl;
    public static final String INSPECTION_RESULT = "inspection_result";
    @Column(name = INSPECTION_RESULT)
    public String inspectionResult;

    public Report() {
        placedWay = SystemConfig.getPlacedWay();
        departmentId = PreferencesUtils.getString(Config.CURRENT_DEPARTMENT_ID, "");
    }

    public Report(String reportid) {
        this();
        this.reportid = reportid;
        this.endtime = DateUtils.getCurrentLongTime();
    }


    public Report(String taskid, String bdzid, String bdz, String inspection, String persons, String temperature,
                  String humidity, String tq, String select_devices) {
        this();
        this.starttime = DateUtils.getCurrentLongTime();
        this.taskid = taskid;
        this.bdz = bdz;
        this.placedWay = SystemConfig.getPlacedWay();
        this.bdzid = bdzid;
        this.persons = persons;
        this.inspection = inspection;
        this.temperature = temperature;
        this.humidity = humidity;
        this.tq = tq;
        this.selected_deviceid = select_devices;
    }

    public Report(String taskid, String bdzid, String bdz, String inspection, String persons) {
        this();
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
        this.placedWay = SystemConfig.getPlacedWay();
    }

    public void setReport(String bdzid, String bdz, String inspection, String temperature,
                          String humidity, String tq, String select_devices) {
        this.bdz = bdz;
        this.bdzid = bdzid;
        this.inspection = inspection;
        this.temperature = temperature;
        this.humidity = humidity;
        this.tq = tq;
        this.selected_deviceid = select_devices;
        this.starttime = DateUtils.getCurrentLongTime();
    }

    public void setOtherReport(String bdzid, String bdz, String inspection, String persons) {
        this.starttime = TextUtils.isEmpty(starttime) ? DateUtils.getCurrentLongTime() : starttime;
        this.bdz = bdz;
        this.bdzid = bdzid;
        this.persons = persons;
        this.inspection = inspection;
        this.insertTime = TextUtils.isEmpty(insertTime) ? DateUtils.getCurrentLongTime() : insertTime;
    }

}
