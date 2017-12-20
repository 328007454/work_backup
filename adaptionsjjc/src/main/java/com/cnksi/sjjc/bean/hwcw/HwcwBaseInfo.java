package com.cnksi.sjjc.bean.hwcw;

import com.cnksi.core.utils.DateUtils;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.UUID;

/**
 * Created by kkk on 2017/12/11.
 */

@Table(name = "report_sbcw_baseinfo")
public class HwcwBaseInfo {
    /**
     * Id
     */
    public static final String ID = "id";
    @Column(name = ID, isId = true)
    public String id = UUID.randomUUID().toString();


    /**
     * 报告Id
     */
    public static final String REPORT_ID = "report_id";
    @Column(name = REPORT_ID)
    public String reportId;

    public static final String BDZID = "bdz_id";
    @Column(name = BDZID)
    public String bdzID;

    public static final String BDZ_NAME = "bdz_name";
    @Column(name = BDZ_NAME)
    public String bdzName;

    /**
     * 测温类型
     */
    public static final String TYPE = "type";
    @Column(name = TYPE)
    public String type;

    /**
     * 是否是全站测温
     */
    public static final String IS_ALL_BDZ = "isallbdzcw";
    @Column(name = IS_ALL_BDZ)
    public String isAllBdz;

    /**
     * 湿度
     */
    public static final String SHIDU = "shidu";
    @Column(name = SHIDU)
    public String shidu;

    /**
     * 温度
     */
    public static final String WENDU = "wendu";
    @Column(name = WENDU)
    public String temp;

    /**
     * 风速
     */
    public static final String FENGSU = "fengsu";
    @Column(name = FENGSU)
    public String fengsu;

    /**
     * 测温仪器
     */
    public static final String TEMP_TEST = "cwyq";
    @Column(name = TEMP_TEST)
    public String testInstrument;

    /**
     * 测温范围
     */
    public static final String TEST_RANGE = "cwfw";
    @Column(name = TEST_RANGE)
    public String testRange;

    /**
     * 测温位置
     */
    public static final String TEST_LOCATION = "cwwz";
    @Column(name = TEST_LOCATION)
    public String testLocation;

    /**
     * 问题
     */
    public static final String PROBLEM = "fxwt";
    @Column(name = PROBLEM)
    public String problem;

    /**
     * 测温人
     */
    public static final String TEST_PERSON = "cwr";
    @Column(name = TEST_PERSON)
    public String testPerson;

    /**
     * 备注
     */
    public static final String REMARK = "remark";
    @Column(name = REMARK)
    public String remark;

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

    public void setData(String isAllBdz, String testType, String temp, String shidu, String fengsu, String testInstrument, String currentReportId, String currentBdzId, String currentBdzName) {
        this.reportId = currentReportId;
        this.bdzID = currentBdzId;
        this.bdzName = currentBdzName;
        this.isAllBdz = isAllBdz;
        this.type = testType;
        this.temp = temp;
        this.shidu = shidu;
        this.fengsu = fengsu;
        this.testInstrument = testInstrument;
    }

    public void setRecordData(String range, String location, String problem, String remark, String people) {
        this.testRange = range;
        this.testLocation = location;
        this.problem = problem;
        this.remark = remark;
        this.testPerson = people;
    }

    public HwcwBaseInfo(){}
}



