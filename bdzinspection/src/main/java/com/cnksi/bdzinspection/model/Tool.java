package com.cnksi.bdzinspection.model;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "tool")
public class Tool extends BaseModel{
	
	//工器具ID
	public static final String TOOLID = "toolid";
	@Column(name = TOOLID,isId = true)
	public String toolid;
	
	//工器具名称
	public static final String NAME = "name";
	@Column(name = NAME)
	public String name;
	
	//单位
	public static final String UNIT = "unit";
	@Column(name = UNIT)
	public String unit;
	
	//注意提示
	public static final String TIPS = "tips";
	@Column(name = TIPS)
	public String tips;
	
	//检查标准
	public static final String CHECK_STANDARD = "check_standard";
	@Column(name = CHECK_STANDARD)
	public String check_standard;
	
	//巡检类型
	public static final String INSPECTION = "inspection";
	@Column(name = INSPECTION)
	public String inspection;
	
	//工器具数量
	public transient  String toolCount;
	
}
