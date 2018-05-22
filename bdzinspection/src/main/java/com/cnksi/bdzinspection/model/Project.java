package com.cnksi.bdzinspection.model;


import com.cnksi.common.model.BaseModel;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 运维一体化项目
 * 
 * @author Oliver
 *
 */
@Table(name = "project")
public class Project extends BaseModel {

	// id
	public static final String ID = "id";
	@Column(name = ID,isId = true)
	public String id;

	public static final String PARENT_ID = "parent_id";
	@Column(name = PARENT_ID)
	public String parent_id;

	public static final String PRO_NAME = "pro_name";
	@Column(name = PRO_NAME)
	public String pro_name;

	public static final String LOOKUP_TYPE = "lookup_type";
	@Column(name = LOOKUP_TYPE)
	public String lookup_type;

	// 插入时间
	public static final String INSERT_TIME = "insert_time";
	@Column(name = INSERT_TIME)
	public String insert_time;

	public final static String LAST_MODIFY_TIME = "last_modify_time";
	@Column(name = LAST_MODIFY_TIME)
	public String last_modify_time;

	// 逻辑删除
	public final static String ENABLED = "enabled";
	@Column(name = ENABLED)
	public String enabled;

}
