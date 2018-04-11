package com.cnksi.bdzinspection.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

import java.util.UUID;

@Table(name = "switch_pic")
public class SwitchPic extends BaseModel{

	// ID
	public static final String ID = "id";
	@Id(column = ID)
	public String id = UUID.randomUUID().toString();

	// 报告编号
	public static final String REPORTID = "reportid";
	@Column(column = REPORTID)
	public String reportid;

	// 图片
	public static final String PIC = "pic";
	@Column(column = PIC)
	public String pic;

	// 录音文件
	public static final String VOICE = "voice";
	@Column(column = VOICE)
	public String voice;

	// 录音文件长度
	public static String VOICELENGTH = "voice_length";

	// 标准id
	public static final String STANDID = "standid";
	@Column(column = STANDID)
	public String standid;

	// 变电站编号
	public static final String BDZID = "bdzid";
	@Column(column = BDZID)
	public String bdzid;

	// 定期维护巡视标准Id
	public static final String STADIDSWICHERID = "stand_switch_id";
	@Column(column = STADIDSWICHERID)
	public String standSwitchId;

	public SwitchPic() {
	}

	public SwitchPic(String id, String standid, String reportid, String bdzid, String pic, String voice) {
		this.id = id;
		this.reportid = reportid;
		this.standid = standid;
		this.pic = pic;
		this.voice = voice;
		this.bdzid = bdzid;
	}
}
