package com.cnksi.bdzinspection.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;

/**
 * @author pantao 部门
 */

@Table(name = "department")
public class Department extends BaseModel {

	public static final String ID = "id";
	@Id(column = ID)
	public String id;

	// 部门编号
	public static final String DEPT_ID = "dept_id";
	@Id(column = DEPT_ID)
	public String dept_id;

	public static final String PID = "pid";
	@Column(column = PID)
	public String pid;

	// 上级部门ID
	public static final String PARENT_ID = "parent_id";
	@Column(column = PARENT_ID)
	public String parent_id;

	// 部门名称
	public static final String DEPT_NAME = "dept_name";
	@Column(column = DEPT_NAME)
	public String dept_name;

	public static final String NAME = "name";
	@Column(column = NAME)
	public String name;

	public static final String COUNTRY_ID = "country_id";
	@Column(column = COUNTRY_ID)
	public String country_id;

	// 部门简称
	public static final String SHORT_NAME = "short_name";
	@Column(column = SHORT_NAME)
	public String short_name;

	// 级别
	public static final String DEPT_TYPE = "dept_type";
	@Column(column = DEPT_TYPE)
	public String dept_type;

	// 级别
	public static final String TYPE = "type";
	@Column(column = TYPE)
	public String type;

	// 部门分组（本部，基层单位，集体企业，其他单位）
	public static final String DEPT_GROUP = "dept_group";
	@Column(column = DEPT_GROUP)
	public String dept_group;

	// 标记为主业（zy）或集体（jt）
	public static final String ZY_OR_JT = "zy_or_jt";
	@Column(column = ZY_OR_JT)
	public String zy_or_jt;

	//
	public static final String TEL = "tel";
	@Column(column = TEL)
	public String tel;

	//
	public static final String SORT = "sort";
	@Column(column = SORT)
	public String sort;

	// 安全巡查、评价类型统计排序
	public static final String SORT2 = "sort2";
	@Column(column = SORT2)
	public String sort2;

	//
	public static final String DEPT_PINYIN = "dept_pinyin";
	@Column(column = DEPT_PINYIN)
	public String dept_pinyin;

	//
	public static final String LEVEL = "level";
	@Column(column = LEVEL)
	public String level;

	//
	public static final String PMS_ID = "pms_id";
	@Column(column = PMS_ID)
	public String pms_id;

	//
	public static final String PMS_NAME = "pms_name";
	@Column(column = PMS_NAME)
	public String pms_name;

	public final static String LAST_MODIFY_TIME = "last_modify_time";
	@Column(column = LAST_MODIFY_TIME)
	public String last_modify_time;

	// 逻辑删除
	public final static String ENABLED = "enabled";
	@Column(column = ENABLED)
	public String enabled;

	// 是否选择
	public static final String IS_SELECTED = "is_selected";
	@Transient
	public boolean isSelected = false;

	public String fullName = "";
}