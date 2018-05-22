package com.cnksi.bdzinspection.model;

import com.cnksi.bdzinspection.utils.Config.OperateTaskStatus;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import com.cnksi.bdzinspection.utils.FunctionUtil;
import com.cnksi.common.model.BaseModel;


@Table(name = "operate_tick")
public class OperateTick extends BaseModel {

	//
	public static final String ID = "id";
	@Column(name = ID,isId = true)
	public String id;

	// 单位
	public static final String UNIT = "unit";
	@Column(name = UNIT)
	public String unit;

	// 编号
	public static final String CODE = "code";
	@Column(name = CODE)
	public String code;

	// 发令人
	public static final String PERSON_FLR = "person_flr";
	@Column(name = PERSON_FLR)
	public String person_flr;

	// 受令人
	public static final String PERSON_SLR = "person_slr";
	@Column(name = PERSON_SLR)
	public String person_slr;

	// 法令时间
	public static final String TIME_FL = "time_fl";
	@Column(name = TIME_FL)
	public String time_fl;

	// 操作开始时间
	public static final String TIME_OPERATE_START = "time_operate_start";
	@Column(name = TIME_OPERATE_START)
	public String time_operate_start;

	// 操作结束时间
	public static final String TIME_OPERATE_END = "time_operate_end";
	@Column(name = TIME_OPERATE_END)
	public String time_operate_end;

	// 操作类型：监护下操作、单人操作、检修人员操作
	public static final String OPERATE_TYPE = "operate_type";
	@Column(name = OPERATE_TYPE)
	public String operate_type;

	// 操作任务
	public static final String TASK = "task";
	@Column(name = TASK)
	public String task;

	// 操作人签名
	public static final String PERSON_CZR = "person_czr";
	@Column(name = PERSON_CZR)
	public String person_czr;

	// 监护人签名
	public static final String PERSON_JHR = "person_jhr";
	@Column(name = PERSON_JHR)
	public String person_jhr;

	// 运维负责人签名
	public static final String PERSON_YWFZR = "person_ywfzr";
	@Column(name = PERSON_YWFZR)
	public String person_ywfzr;

	// 录像名称
	public static final String VIDEO = "video";
	@Column(name = VIDEO)
	public String video;

	// 录像地址（服务器使用）
	public static final String VIDEO_URL = "video_url";
	@Column(name = VIDEO_URL)
	public String video_url;

	// 录音名称
	public static final String VOICE = "voice";
	@Column(name = VOICE)
	public String voice;

	// 录音地址
	public static final String VOICE_URL = "voice_url";
	@Column(name = VOICE_URL)
	public String voice_url;

	// 图片
	public static final String PIC = "pic";
	@Column(name = PIC)
	public String pic;

	//
	public static final String PIC_URL = "pic_url";
	@Column(name = PIC_URL)
	public String pic_url;

	// 创建时间
	public static final String TIME_CREATE = "time_create";
	@Column(name = TIME_CREATE)
	public String time_create;

	// 状态：待审ds、未完成wwc、已完成ywc 、已停止ytz
	public static final String STATUS = "status";
	@Column(name = STATUS)
	public String status;

	// 备注
	public static final String REMARK = "remark";
	@Column(name = REMARK)
	public String remark;

	public OperateTick() {
	}

	public OperateTick(String taskName, String code, String unit) {
		this.id = FunctionUtil.getPrimarykey();
		this.task = taskName;
		this.code = code;
		this.unit = unit;
		this.status = OperateTaskStatus.dsh.name();
	}

}
