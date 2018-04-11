package com.cnksi.bdzinspection.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "battery")
public class Battery extends BaseModel{

	// 蓄电池id
	public static final String BATTERYID = "battery";
	@Id(column = BATTERYID)
	public String battery;

	// 变电站编号
	public static final String BDZID = "bdzid";
	@Column(column = BDZID)
	public String bdzid;

	// 名称
	public static final String NAME = "name";
	@Column(column = NAME)
	public String name;

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

	// 数量
	public static final String NUMBER = "number";
	@Column(column = NUMBER)
	public String number;

}
