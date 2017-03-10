package com.cnksi.sjjc.bean;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "modify_record")
public class ModifyRecord extends BaseModel{

	@Column( name= "id",isId = true)
	public int id;
	/**
	 * 修改的表的id
	 */
	public static final String MODIFY_ID = "modify_id";
	@Column(name = MODIFY_ID)
	public String modify_id;
	/**
	 * 修改的表的表名
	 */
	public static final String TABLE_NAME = "table_name";
	@Column(name = TABLE_NAME)
	public String table_name;
	/**
	 * 修改的表的主键id
	 */
	public static final String MODIFY_ID_NAME = "modify_id_name";
	@Column(name = MODIFY_ID_NAME)
	public String modify_id_name;
	/**
	 * 操作 增加 add/修改 update/删除 delete
	 */
	public static final String OPERATION = "operation";
	@Column(name = OPERATION)
	public String operation;
	/**
	 * 修改的表的id
	 */
	public static final String CREATE_TIME = "create_time";
	@Column(name = CREATE_TIME)
	public String create_time;
	
	public static final String MODIFY_CONTENT = "modify_content";
}
