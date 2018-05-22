package com.cnksi.sjjc.bean;


import com.cnksi.common.model.BaseModel;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "switch_pic")
public class SwitchPic extends BaseModel {

	// ID
	public static final String ID = "id";
	@Column(name = ID,isId = true)
	public String id;

	// 报告编号
	public static final String REPORTID = "reportid";
	@Column(name = REPORTID)
	public String reportid;

	// 图片
	public static final String PIC = "pic";
	@Column(name = PIC)
	public String pic;

	// 录音文件
	public static final String VOICE = "voice";
	@Column(name = VOICE)
	public String voice;

	// 录音文件长度
	public static final String VOICELENGTH = "voice_length";

	// 标准id
	public static final String STANDID = "standid";
	@Column(name = STANDID)
	public String standid;

	// 变电站编号
	public static final String BDZID = "bdzid";
	@Column(name = BDZID)
	public String bdzid;

	// 定期维护巡视标准Idname
	public static final String STADIDSWICHERID = "stand_switch_id";
	@Column(name = STADIDSWICHERID)
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
