package com.cnksi.bdzinspection.model;

import com.cnksi.core.utils.DateUtils;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;import com.cnksi.common.model.BaseModel;

import java.util.UUID;

@Table(name = "standard_step_confirm")
public class StandardStepConfirm  extends BaseModel{

	// ID
	public static final String ID = "id";
	@Column(name = ID,isId = true)
	public String id = UUID.randomUUID().toString();



	// 标准id
	public static final String STANDID = "standid";
	@Column(name = STANDID)
	public String standid;

	// 变电站编号
	public static final String BDZID = "bdzid";
	@Column(name = BDZID)
	public String bdzid;

	// 报告编号
	public static final String REPORTID = "reportid";
	@Column(name = REPORTID)
	public String reportid;


	// 提交时间
	public static final String CONFIRMDATE = "confirm_date";
	@Column(name = CONFIRMDATE)
	public String confirm_date;



	public static final String CREATETIME = "create_time";
	@Column(name = CREATETIME)
	public String createtime;



	public StandardStepConfirm() {
	}


	public static StandardStepConfirm create(String bdzid,String reportid,String standid)
	{
		StandardStepConfirm item=new StandardStepConfirm();
		item.bdzid=bdzid;
		item.reportid=reportid;
		item.standid=standid;
		item.dlt="0";
		item.createtime= DateUtils.getCurrentLongTime();
		return item;

	}

}
