package com.cnksi.bdzinspection.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "dangpoint")
public class Dangpoint extends BaseModel {

	
	//
	public static final String DPID = "dpid";
	@Id(column = DPID)
	public String dpid;

	// 危险点
	public static final String DPNAME = "dpname";
	@Column(column = DPNAME)
	public String dpname;

	// 控制措施
	public static final String MEASURES = "measures";
	@Column(column = MEASURES)
	public String measures;

	//
	public static final String INSPECTION = "inspection";
	@Column(column = INSPECTION)
	public String inspection;

	// 变电站ID
	public static final String BDZID = "bdzid";
	@Column(column = BDZID)
	public String bdzid;

	// 设备编号
	public static final String DEVICEID = "deviceid";
	@Column(column = DEVICEID)
	public String deviceid;

	// 排序序号
	public static final String SORT = "sort";
	@Column(column = SORT)
	public String sort;


	public static final String INSTID = "instid";
	@Column(column = INSTID)
	public String instid;

	// 危险类型
	public static final String AVOID_TYPE = "avoid_type";
	@Column(column = AVOID_TYPE)
	public String avoid_type;

	public static final String AFFIRM = "affirm";
	@Column(column = AFFIRM)
	public String affirm;

	public Dangpoint() {
		// TODO Auto-generated constructor stub
	}
	
	public Dangpoint(String avoid_stype) {
		this.avoid_type = avoid_stype;
	}
}
