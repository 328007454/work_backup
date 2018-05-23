package com.cnksi.bdzinspection.model;


import com.cnksi.common.model.BaseModel;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

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
	@Column(name = ID,isId = true)
	public String id=UUID.randomUUID().toString();

	// 类型
	public static final String KIND = "kind";
	@Column(name = KIND)
	public String kind;

	// 父ID
	public static final String PID = "pid";
	@Column(name = PID)
	public String pid;

	// 变电站id
	public static final String BDZID = "bdzid";
	@Column(name = BDZID)
	public String bdzid;

	// 工作空间
	public static final String WORKPACE = "work_pace";
	@Column(name = WORKPACE)
	public String work_pace;

	//
	public static final String IDENTIFICATION_PREVENT_MEASURES = "identification_prevent_measures";
	@Column(name = IDENTIFICATION_PREVENT_MEASURES)
	public String preventMeasures;

	// 描述
	public static final String DESCRIPTION = "description";
	@Column(name = DESCRIPTION)
	public String description;

	// 
	public static final String UNIT = "unit";
	@Column(name = UNIT)
	public String unit;
	
	public static final String ISCOPY = "is_copy";
	@Column(name = ISCOPY)
	public int  isCopy;

	public static final String LEVEL = "level";
	@Column(name = LEVEL)
	public int  level;

	public static final String CODE = "code";
	@Column(name = CODE)
	public String  code;
	//
	public static final String ORIGIN = "origin";
	@Column(name = ORIGIN)
	public String origin;
	
	public static final String SORT = "sort";
	@Column(name = SORT)
	public String  sort;
	
	public static final String REMARK = "remark";
	@Column(name = REMARK)
	public String remark;
	

	
	public static final String INSERTTIME= "insert_time";
	@Column(name = INSERTTIME)
	public String insertTime;
	
	public static final String UPDATE_TIME= "update_time";
	@Column(name = UPDATE_TIME)
	public String update_time;
	/**
	 * 选择维护类型id
	 */
	public static final String REPSWITCHOVERID = "rep_swithover_id";
	@Column(name = REPSWITCHOVERID)
	public String repSwithoverId;
	
}
