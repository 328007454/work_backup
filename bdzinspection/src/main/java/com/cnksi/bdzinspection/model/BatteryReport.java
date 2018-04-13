package com.cnksi.bdzinspection.model;

import com.cnksi.xscore.xsutils.FunctionUtils;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "battery_report")
public class BatteryReport  extends BaseModel{

	// 蓄电池报告id
	public static final String ID = "id";
	@Id(column = ID)
	public String id;

	// 关联的报告id
	public static final String REPORTID = "reportid";
	@Column(column = REPORTID)
	public String reportid;

	// 电池组id
	public static final String BATTERYID = "batteryid";
	@Column(column = BATTERYID)
	public String batteryid;

	// 电池组名称
	public static final String BATTERY_GROUPNAME = "battery_groupname";
	@Column(column = BATTERY_GROUPNAME)
	public String battery_groupname;

	// 充电电流
	public static final String MA = "ma";
	@Column(column = MA)
	public String ma;

	// 电池电压
	public static final String VOLTAGE = "voltage";
	@Column(column = VOLTAGE)
	public String voltage;

	// 控制母线电压
	public static final String KVOLTAGE = "kvoltage";
	@Column(column = KVOLTAGE)
	public String kvoltage;

	// 测试分类(抽查 普查）
	public static final String TEST_TYPE = "test_type";
	@Column(column = TEST_TYPE)
	public String test_type;

	// 蓄电池节数(108 20）
	public static final String NUMBER = "number";
	@Column(column = NUMBER)
	public String number;

	// 是否测试内阻
	public static final String HAS_RESISTANCE = "has_resistance";
	@Column(column = HAS_RESISTANCE)
	public String has_resistance;

	public BatteryReport() {
	}

	public BatteryReport(String reportid, String test_type, String has_resistance, Battery mBattery, String ma, String voltage, String kvoltage) {
		this.id = FunctionUtils.getPrimarykey();
		this.reportid = reportid;
		this.battery_groupname = mBattery.name;
		this.ma = ma;
		this.voltage = voltage;
		this.kvoltage = kvoltage;
		this.test_type = test_type;
		this.number = mBattery.number;
		this.batteryid = mBattery.battery;
		this.has_resistance = has_resistance;
	}

	public BatteryReport(String reportid) {
		this.id = FunctionUtils.getPrimarykey();
		this.reportid = reportid;
		this.number = "108";
		this.has_resistance = "Y";
	}
}