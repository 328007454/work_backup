package com.cnksi.bdzinspection.model;


import com.cnksi.common.model.BaseModel;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "battery")
public class Battery extends BaseModel {

	// 蓄电池id
	public static final String BATTERYID = "battery";
	@Column(name = BATTERYID,isId = true)
	public String battery;

	// 变电站编号
	public static final String BDZID = "bdzid";
	@Column(name = BDZID)
	public String bdzid;

	// 名称
	public static final String NAME = "name";
	@Column(name = NAME)
	public String name;

	// 充电电流
	public static final String MA = "ma";
	@Column(name = MA)
	public String ma;

	// 电池电压
	public static final String VOLTAGE = "voltage";
	@Column(name = VOLTAGE)
	public String voltage;

	// 控制母线电压
	public static final String KVOLTAGE = "kvoltage";
	@Column(name = KVOLTAGE)
	public String kvoltage;

	// 数量
	public static final String NUMBER = "number";
	@Column(name = NUMBER)
	public String number;

}
