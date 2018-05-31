package com.cnksi.bdzinspection.model;


import com.cnksi.common.model.BaseModel;
import com.cnksi.common.model.Lookup;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * @author yangjun
 * @date 201708.17
 * @introduction 特殊巡视类型model
 * */

@Table(name = "special_menu")
public class SpecialMenu extends BaseModel{

	//
	public static final String ID = "id";
	@Column(name = ID,isId = true)
	public String id;

	//
	public static final String LOO_ID = "loo_id";
	@Column(name = LOO_ID)
	public String loo_id;

	// K-V 键
	public static final String K = "k";
	@Column(name = K)
	public String k;

	// K-V 值
	public static final String V = "v";
	@Column(name = V)
	public String v;

	// 同一类型下的不同值的排序
	public static final String SORT = "sort";
	@Column(name = SORT)
	public String sort;

	// K-V类型
	public static final String TYPE = "type";
	@Column(name = TYPE)
	public String type;

	// 备注
	public static final String REMARK = "remark";
	@Column(name = REMARK)
	public String remark;

	// 是否删除
	public static final String DLT = "dlt";
	@Column(name = DLT)
	public int dlt;

	// 更新时间
	public static final String LASTMODIFYTIME = "last_modify_time";
	@Column(name = LASTMODIFYTIME)
	public String lastModifyTime;


	// PMS编号
	public static final String PMSCODE = "pms_code";
	@Column(name = PMSCODE)
	public String pmsCode;

	// PMS名称
	public static final String PMSCODENAME = "pms_code_name";
	@Column(name = PMSCODENAME)
	public String pmsCodeName;

	// 设备选择方式
	public static final String DEVICEWAY = "device_way";
	@Column(name = DEVICEWAY)
	public String deviceWay;

	// 工作区域
	public static final String WORKSPACE = "work_space";
	@Column(name = WORKSPACE)
	public String workSpace;

	// 更新时间
	public static final String UPDATETIME = "update_time";
	@Column(name = UPDATETIME)
	public String updateTime;

	// 更新时间
	public static final String INSERTTIME = "insert_time";
	@Column(name = INSERTTIME)
	public String insertTime;

	// 风险辨识与预警措施
	public static final String IDENTIFICATIONPREVETIONMEASURES = "indentification_prevent_measures";
	@Column(name = IDENTIFICATIONPREVETIONMEASURES)
	public String measureContents;

	// 巡视内容
	public static final String INSPECTIONCONTENT = "inspection_content";
	@Column(name = INSPECTIONCONTENT)
	public String inspectionContent;


	// 标准来源
	public static final String STANDARDSORIGIN = "standards_origin";
	@Column(name = STANDARDSORIGIN)
	public String standardsOrigin;



	public SpecialMenu() {
	}

	public static Lookup convertLookup(SpecialMenu menu) {
		Lookup lookup = new Lookup();
		lookup.id = menu.id;
		lookup.k = menu.k;
		lookup.loo_id = menu.loo_id;
		lookup.v = menu.v;
		lookup.remark = menu.remark;
		lookup.deviceWay = menu.deviceWay;
		return lookup;
	}
}
