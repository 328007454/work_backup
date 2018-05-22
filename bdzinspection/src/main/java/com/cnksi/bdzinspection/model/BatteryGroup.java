package com.cnksi.bdzinspection.model;

import com.cnksi.common.model.BaseModel;
import com.cnksi.xscore.xsutils.DateUtils;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.UUID;

/**
 * Created by han on 2016/8/10.
 */
@Table(name = "sbjc_battery_group")
public class BatteryGroup extends BaseModel {
    //
    public static final String ID = "id";
    @Column(name = ID,isId = true)
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

    //实测全电池浮充电压
    public static final String FCDY_SC = "fcdy_sc";
    @Column(name = FCDY_SC)
    public String fcdySc;

    //直流系统监测全电池浮充电压
    public static final String FCDY_ZLXT = "fcdy_zlxt";
    @Column(name = FCDY_ZLXT)
    public String fcdyZlxt;

    //负荷电流
    public static final String FHDL = "fhdl";
    @Column(name = FHDL)
    public String loadElectricty;


    //浮充电流
    public static final String FCDL = "fcdl";
    @Column(name = FCDL)
    public String fcElectricty;

    //正母线对地电压值
    public static final String DYZ_ZMX = "dyz_zmx";
    @Column(name = DYZ_ZMX)
    public String zVoltage;


    //负母线对地电压值
    public static final String DYZ_FMX = "dyz_fmx";
    @Column(name = DYZ_FMX)
    public String fVoltage;


    //测试负责人
    public static final String CS_MANAGER = "cs_manager";
    @Column(name = CS_MANAGER)
    public String testManager;

    //测试成员
    public static final String CS_PERSONS = "cs_persons";
    @Column(name = CS_PERSONS)
    public String testPersons;

    //分析结论
    public static final String ANALYSIS_RESULT = "analysis_result";
    @Column(name = ANALYSIS_RESULT)
    public String analysisResult;

    //组端电压
    public static final String ZU_DUAN_VOLTAGE = "zddy";
    @Column(name = ZU_DUAN_VOLTAGE)
    public String zuDuanVoltage;

    //充电电流
    public static final String CHARGE_ELECTRICITY = "cddl";
    @Column(name = CHARGE_ELECTRICITY)
    public String chargeElectricity;

    //单只标压
    public static final String SINGLE_VOLTAGE = "dzbcdy";
    @Column(name = SINGLE_VOLTAGE)
    public String singleVoltage;


    // 备注
    public static final String REMARK = "remark";
    @Column(name = REMARK)
    public String remark;

    /**
     * 测试仪器
     */
    public static final String TEST_INSTRUMENT="csyqmc";
    @Column(name = TEST_INSTRUMENT)
    public  String testInstrument ;



    //更新时间
    public static final String LAST_MODIFY_TIME = "last_modify_time";
    @Column(name = LAST_MODIFY_TIME)
    public String last_modify_time;


    public BatteryGroup(){}
    public BatteryGroup(String currentReportId, String currentBdzId, String currentBdzName, String currentBatterId) {
        this.reportid = currentReportId;
        this.bdzid = currentBdzId;
        this.bdz = currentBdzName;
        this.battaryGroup = currentBatterId;
        this.dlt ="0";
        this.last_modify_time = DateUtils.getCurrentLongTime();
    }
}
