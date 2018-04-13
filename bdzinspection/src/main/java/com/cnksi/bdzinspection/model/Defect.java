package com.cnksi.bdzinspection.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "defect_define")
public class Defect extends BaseModel {

	public Defect() {
		this.dlt = "0";
	}

	// 缺陷序号
	public static final String DEFECTID = "defectid";
	@Id(column = DEFECTID)
	public int defectid;

	// 巡检标准序号
	public static final String STAID = "staid";
	@Column(column = STAID)
	public String staid;

	// 缺陷描述
	public static final String DESCRIPTION = "description";
	@Column(column = DESCRIPTION)
	public String description;

//	// 参考标准
//	public static final String REFERENCE = "reference";
//	@Column(column = REFERENCE)
//	public String reference;

	// 缺陷级别
	public static final String LEVEL = "level";
	@Column(column = LEVEL)
	public String level;

	// 缺陷来源
	public static final String ORIGIN = "origin";
	@Column(column = ORIGIN)
	public String origin;

	// 处理方法
	public static final String DEALMETHOD = "dealMethod";
	@Column(column = DEALMETHOD)
	public String dealMethod;

	// 备注
	public static final String REMARK = "remark";
	@Column(column = REMARK)
	public String remark;

	//
	public static final String CREATER = "creater";
	@Column(column = CREATER)
	public String creater;



}
