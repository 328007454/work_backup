package com.cnksi.bdzinspection.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

import java.util.UUID;

/**
 * standrad_switchover model
 * 
 * */
@Table(name = "standard_switchover")
public class StandardSwitchover extends BaseModel{

	public static final String DISPLAYNUM="displayNum";

	public static final String LEVELTOWDOWNCOPY="levelTwoDownCopy";

	// id
	public static final String ID = "id";
	@Id(column = ID)
	public String id=UUID.randomUUID().toString();

	// 类型
	public static final String KIND = "kind";
	@Column(column = KIND)
	public String kind;

	// 父ID
	public static final String PID = "pid";
	@Column(column = PID)
	public String pid;

	// 变电站id
	public static final String BDZID = "bdzid";
	@Column(column = BDZID)
	public String bdzid;

	// 工作空间
	public static final String WORKPACE = "work_pace";
	@Column(column = WORKPACE)
	public String work_pace;

	//
	public static final String IDENTIFICATION_PREVENT_MEASURES = "identification_prevent_measures";
	@Column(column = IDENTIFICATION_PREVENT_MEASURES)
	public String preventMeasures;

	// 描述
	public static final String DESCRIPTION = "description";
	@Column(column = DESCRIPTION)
	public String description;

	// 
	public static final String UNIT = "unit";
	@Column(column = UNIT)
	public String unit;
	
	public static final String ISCOPY = "is_copy";
	@Column(column = ISCOPY)
	public int  isCopy;

	public static final String LEVEL = "level";
	@Column(column = LEVEL)
	public int  level;

	public static final String CODE = "code";
	@Column(column = CODE)
	public String  code;
	//
	public static final String ORIGIN = "origin";
	@Column(column = ORIGIN)
	public String origin;
	
	public static final String SORT = "sort";
	@Column(column = SORT)
	public String  sort;
	
	public static final String REMARK = "remark";
	@Column(column = REMARK)
	public String remark;
	

	
	public static final String INSERTTIME= "insert_time";
	@Column(column = INSERTTIME)
	public String insertTime;
	
	public static final String UPDATE_TIME= "update_time";
	@Column(column = UPDATE_TIME)
	public String update_time;
	/**
	 * 选择维护类型id
	 */
	public static final String REPSWITCHOVERID = "rep_swithover_id";
	@Column(column = REPSWITCHOVERID)
	public String repSwithoverId;
	
}
