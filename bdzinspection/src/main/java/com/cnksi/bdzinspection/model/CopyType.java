package com.cnksi.bdzinspection.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "copy_type")
public class CopyType extends BaseModel {
	
	//ID
	public static final String ID = "id";
	@Id(column = ID)
	public String id;
	
	//程序使用的编码
	public static final String KEY = "key";
	@Column(column = KEY)
	public String key;
	
	//父ID
	public static final String PID = "pid";
	@Column(column = PID)
	public String pid;
	
	//名称
	public static final String NAME = "name";
	@Column(column = NAME)
	public String name;
	
	//是否可以被选择：Y、N
	public static final String SELECTED_ABLE = "selected_able";
	@Column(column = SELECTED_ABLE)
	public String selected_able;
	
	//上传PMS的url
	public static final String PMS_UPLOAD_URL = "pms_upload_url";
	@Column(column = PMS_UPLOAD_URL)
	public String pms_upload_url;
	
	//pms上传的类型id
	public static final String PMS_TYPE_ID = "pms_type_id";
	@Column(column = PMS_TYPE_ID)
	public String pms_type_id;
	
	//remark
	public static final String REMARK = "remark";
	@Column(column = REMARK)
	public String remark;
	
	//
	public static final String CREATE_TIME = "create_time";
	@Column(column = CREATE_TIME)
	public String create_time;
	
	//
	public static final String UPDATE_TIME = "update_time";
	@Column(column = UPDATE_TIME)
	public String update_time;
	
}
