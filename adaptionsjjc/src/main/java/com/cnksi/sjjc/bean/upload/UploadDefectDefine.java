package com.cnksi.sjjc.bean.upload;


import com.cnksi.sjjc.bean.BaseModel;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "defect_define")
public class UploadDefectDefine extends BaseModel {

	public UploadDefectDefine() {
		this.dlt = "0";
	}

	// 缺陷序号
	public static final String DEFECTID = "defectid";
	@Column(name = DEFECTID,isId = true)
	public String defectid;

	// 巡检标准序号
	public static final String STAID = "staid";
	@Column(name = STAID)
	public String staid;

	// 缺陷描述
	public static final String DESCRIPTION = "description";
	@Column(name = DESCRIPTION)
	public String description;

	// 参考标准
	public static final String REFERENCE = "reference";
	@Column(name = REFERENCE)
	public String reference;

	// 缺陷级别
	public static final String LEVEL = "level";
	@Column(name = LEVEL)
	public String level;

	// 缺陷来源
	public static final String ORIGIN = "origin";
	@Column(name = ORIGIN)
	public String origin;

	// 处理方法
	public static final String DEALMETHOD = "dealMethod";
	@Column(name = DEALMETHOD)
	public String dealMethod;

	// 备注
	public static final String REMARK = "remark";
	@Column(name = REMARK)
	public String remark;

	//
	public static final String CREATER = "creater";
	@Column(name = CREATER)
	public String creater;


}
