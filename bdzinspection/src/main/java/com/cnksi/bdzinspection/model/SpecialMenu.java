package com.cnksi.bdzinspection.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * @author yangjun
 * @date 201708.17
 * @introduction 特殊巡视类型model
 * */

@Table(name = "special_menu")
public class SpecialMenu extends BaseModel{

	//
	public static final String ID = "id";
	@Id(column = ID)
	public String id;

	//
	public static final String LOO_ID = "loo_id";
	@Column(column = LOO_ID)
	public String loo_id;

	// K-V 键
	public static final String K = "k";
	@Column(column = K)
	public String k;

	// K-V 值
	public static final String V = "v";
	@Column(column = V)
	public String v;

	// 同一类型下的不同值的排序
	public static final String SORT = "sort";
	@Column(column = SORT)
	public String sort;

	// K-V类型
	public static final String TYPE = "type";
	@Column(column = TYPE)
	public String type;

	// 备注
	public static final String REMARK = "remark";
	@Column(column = REMARK)
	public String remark;

	// 是否删除
	public static final String DLT = "dlt";
	@Column(column = DLT)
	public int dlt;

	// 更新时间
	public static final String LASTMODIFYTIME = "last_modify_time";
	@Column(column = LASTMODIFYTIME)
	public String lastModifyTime;


	// PMS编号
	public static final String PMSCODE = "pms_code";
	@Column(column = PMSCODE)
	public String pmsCode;

	// PMS名称
	public static final String PMSCODENAME = "pms_code_name";
	@Column(column = PMSCODENAME)
	public String pmsCodeName;

	// 设备选择方式
	public static final String DEVICEWAY = "device_way";
	@Column(column = DEVICEWAY)
	public String deviceWay;

	// 工作区域
	public static final String WORKSPACE = "work_space";
	@Column(column = WORKSPACE)
	public String workSpace;

	// 更新时间
	public static final String UPDATETIME = "update_time";
	@Column(column = UPDATETIME)
	public String updateTime;

	// 更新时间
	public static final String INSERTTIME = "insert_time";
	@Column(column = INSERTTIME)
	public String insertTime;

	// 风险辨识与预警措施
	public static final String IDENTIFICATIONPREVETIONMEASURES = "indentification_prevent_measures";
	@Column(column = IDENTIFICATIONPREVETIONMEASURES)
	public String measureContents;

	// 巡视内容
	public static final String INSPECTIONCONTENT = "inspection_content";
	@Column(column = INSPECTIONCONTENT)
	public String inspectionContent;


	// 标准来源
	public static final String STANDARDSORIGIN = "standards_origin";
	@Column(column = STANDARDSORIGIN)
	public String standardsOrigin;



	public SpecialMenu() {
	}

	public SpecialMenu(String k, String v) {
		this.k = k;
		this.v = v;
	}

	public SpecialMenu(String k, String v, String type) {
		this.k = k;
		this.v = v;
		this.type = type;
	}

	public SpecialMenu(SwitchMenu menu) {
		this.id = menu.id;
		this.k = menu.k;
		this.loo_id = menu.loo_id;
		this.v = menu.v;
	}
}
