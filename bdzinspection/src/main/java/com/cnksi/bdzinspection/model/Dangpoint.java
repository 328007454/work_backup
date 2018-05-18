package com.cnksi.bdzinspection.model;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "dangpoint")
public class Dangpoint extends BaseModel {

	
	//
	public static final String DPID = "dpid";
	@Column(name = DPID,isId = true)
	public String dpid;

	// 危险点
	public static final String DPNAME = "dpname";
	@Column(name = DPNAME)
	public String dpname;

	// 控制措施
	public static final String MEASURES = "measures";
	@Column(name = MEASURES)
	public String measures;

	//
	public static final String INSPECTION = "inspection";
	@Column(name = INSPECTION)
	public String inspection;

	// 变电站ID
	public static final String BDZID = "bdzid";
	@Column(name = BDZID)
	public String bdzid;

	// 设备编号
	public static final String DEVICEID = "deviceid";
	@Column(name = DEVICEID)
	public String deviceid;

	// 排序序号
	public static final String SORT = "sort";
	@Column(name = SORT)
	public String sort;


	public static final String INSTID = "instid";
	@Column(name = INSTID)
	public String instid;

	// 危险类型
	public static final String AVOID_TYPE = "avoid_type";
	@Column(name = AVOID_TYPE)
	public String avoid_type;

	public static final String AFFIRM = "affirm";
	@Column(name = AFFIRM)
	public String affirm;

	public Dangpoint() {
		// TODO Auto-generated constructor stub
	}
	
	public Dangpoint(String avoid_stype) {
		this.avoid_type = avoid_stype;
	}
}
