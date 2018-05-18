package com.cnksi.bdzinspection.model;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.lang.annotation.Retention;

@Table(name = "battery_details")
public class BatteryDetails extends BaseModel {

	// 蓄电池id
	public static final String ID = "id";
	@Column(name = ID,isId = true)
	public String id;

	// 关联的报告id
	public static final String REPORTID = "reportid";
	@Column(name = REPORTID)
	public String reportid;

	// 电池组id
	public static final String BATTERYID = "batteryid";
	@Column(name = BATTERYID)
	public String batteryid;

	// 电池编号
	public static final String BATTERY_NUMBER = "battery_number";
	@Column(name = BATTERY_NUMBER)
	public String battery_number;

	// 抄录的电压
	public static final String VOLTAGE = "voltage";
	@Column(name = VOLTAGE)
	public String voltage;

	// 抄录的内阻
	public static final String RESISTANCE = "resistance";
	@Column(name = RESISTANCE)
	public String resistance;

	// 最高缺陷级别
	public transient String defectLevel;

	// 是否抄录了数据

	public transient boolean hasCopyed = false;

	public BatteryDetails(String batteryNumber) {
		this.battery_number = batteryNumber;
	}
	
	public BatteryDetails(){}

}
