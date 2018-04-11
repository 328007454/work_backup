package com.cnksi.bdzinspection.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;

@Table(name = "battery_details")
public class BatteryDetails extends BaseModel {

	// 蓄电池id
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

	// 电池编号
	public static final String BATTERY_NUMBER = "battery_number";
	@Column(column = BATTERY_NUMBER)
	public String battery_number;

	// 抄录的电压
	public static final String VOLTAGE = "voltage";
	@Column(column = VOLTAGE)
	public String voltage;

	// 抄录的内阻
	public static final String RESISTANCE = "resistance";
	@Column(column = RESISTANCE)
	public String resistance;

	// 最高缺陷级别
	@Transient
	public String defectLevel;

	// 是否抄录了数据
	@Transient
	public boolean hasCopyed = false;

	public BatteryDetails(String batteryNumber) {
		this.battery_number = batteryNumber;
	}
	
	public BatteryDetails(){}

}
