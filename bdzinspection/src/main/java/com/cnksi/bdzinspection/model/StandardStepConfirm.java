package com.cnksi.bdzinspection.model;

import com.cnksi.xscore.xsutils.DateUtils;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

import java.util.UUID;

@Table(name = "standard_step_confirm")
public class StandardStepConfirm  extends BaseModel{

	// ID
	public static final String ID = "id";
	@Id(column = ID)
	public String id = UUID.randomUUID().toString();



	// 标准id
	public static final String STANDID = "standid";
	@Column(column = STANDID)
	public String standid;

	// 变电站编号
	public static final String BDZID = "bdzid";
	@Column(column = BDZID)
	public String bdzid;

	// 报告编号
	public static final String REPORTID = "reportid";
	@Column(column = REPORTID)
	public String reportid;


	// 提交时间
	public static final String CONFIRMDATE = "confirm_date";
	@Column(column = CONFIRMDATE)
	public String confirm_date;



	public static final String CREATETIME = "create_time";
	@Column(column = CREATETIME)
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
