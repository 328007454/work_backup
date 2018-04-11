package com.cnksi.bdzinspection.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;

@Table(name = "tool")
public class Tool extends BaseModel{
	
	//工器具ID
	public static final String TOOLID = "toolid";
	@Id(column = TOOLID)
	public String toolid;
	
	//工器具名称
	public static final String NAME = "name";
	@Column(column = NAME)
	public String name;
	
	//单位
	public static final String UNIT = "unit";
	@Column(column = UNIT)
	public String unit;
	
	//注意提示
	public static final String TIPS = "tips";
	@Column(column = TIPS)
	public String tips;
	
	//检查标准
	public static final String CHECK_STANDARD = "check_standard";
	@Column(column = CHECK_STANDARD)
	public String check_standard;
	
	//巡检类型
	public static final String INSPECTION = "inspection";
	@Column(column = INSPECTION)
	public String inspection;
	
	//工器具数量
	@Transient
	public String toolCount;
	
}
