package com.cnksi.sjjc.bean.upload;


import com.cnksi.common.model.BaseModel;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "spacing")
public class UploadSpacing extends BaseModel {

	// 间隔ID
	public static final String SPID = "spid";
	@Column(name = SPID,isId = true)
	public String spid;

	// 变电站编号
	public static final String BDZID = "bdzid";
	@Column(name = BDZID)
	public String bdzid;

	// 间隔名称
	public static final String NAME = "name";
	public static final String SNAME = "sname";
	@Column(name = NAME)
	public String name;

	// 排序
	public static final String SORT = "sort";
	@Column(name = SORT)
	public String sort;

	// 一次设备排序
	public static final String SORT_ONE = "sort_one";
	@Column(name = SORT_ONE)
	public int sort_one;
	// 二次设备排序
	public static final String SORT_SECOND = "sort_second";
	@Column(name = SORT_SECOND)
	public int sort_second;

	// 经度
	public static final String LONGITUDE = "longitude";
	@Column(name = LONGITUDE)
	public String longitude;

	// 纬度
	public static final String LATITUDE = "latitude";
	@Column(name = LATITUDE)
	public String latitude;

	public static final String Y = "y";
	@Column(name = Y)
	public String y;

	public static final String X = "x";
	@Column(name = X)
	public String x;



	// 电压等级从lookup表中查询type=voltage
	public static final String VOLTAGE = "voltage";
	@Column(name = VOLTAGE)
	public String voltage;
}
