package com.cnksi.bdzinspection.model;

import com.cnksi.common.model.BaseModel;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;


@Table(name = "operate_item")
public class OperateItem extends BaseModel {

	//
	public static final String ID = "id";
	@Column(name = ID,isId = true)
	public String id;

	// 操作票id
	public static final String TID = "tid";
	@Column(name = TID)
	public String tid;

	// 序号
	public static final String ORDER = "iorder";
	@Column(name = ORDER)
	public String iorder;

	// 操作内容
	public static final String CONTENT = "content";
	@Column(name = CONTENT)
	public String content;

	// 开始时间
	public static final String TIME_START = "time_start";
	@Column(name = TIME_START)
	public String time_start;

	// 结束时间
	public static final String TIME_END = "time_end";
	@Column(name = TIME_END)
	public String time_end;

	// 耗时
	public static final String SPEND_TIME = "spend_time";
	@Column(name = SPEND_TIME)
	public String spend_time;

	public OperateItem() {
	}

	public OperateItem(String tid, String order, String content) {
		this.id = BaseModel.getPrimarykey();
		this.tid = tid;
		this.iorder = order;
		this.content = content;
	}

}
