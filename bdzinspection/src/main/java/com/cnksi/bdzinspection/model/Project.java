package com.cnksi.bdzinspection.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

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
	@Id(column = ID)
	public String id;

	public static final String PARENT_ID = "parent_id";
	@Column(column = PARENT_ID)
	public String parent_id;

	public static final String PRO_NAME = "pro_name";
	@Column(column = PRO_NAME)
	public String pro_name;

	public static final String LOOKUP_TYPE = "lookup_type";
	@Column(column = LOOKUP_TYPE)
	public String lookup_type;

	// 插入时间
	public static final String INSERT_TIME = "insert_time";
	@Column(column = INSERT_TIME)
	public String insert_time;

	public final static String LAST_MODIFY_TIME = "last_modify_time";
	@Column(column = LAST_MODIFY_TIME)
	public String last_modify_time;

	// 逻辑删除
	public final static String ENABLED = "enabled";
	@Column(column = ENABLED)
	public String enabled;

}
