package com.cnksi.sjjc.bean;

import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.FunctionUtils;
import com.cnksi.sjjc.bean.Department;
import com.cnksi.sjjc.bean.Users;

import android.text.TextUtils;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;
import org.xutils.db.table.DbModel;

/**
 * @author Wastrel
 * @date 创建时间：2016年8月10日 上午9:33:33 TODO
 */
@Table(name = "report_signname")
public class ReportSignname {
	// ID
	public static final String ID = "id";
	@Column(name = ID,isId = true)
	private String id;
	// 报告ID
	public static final String REPORTID = "report_id";
	@Column(name = REPORTID)
	private String reportId;
	// 部门ID
	public static final String DEPTID = "dept_id";
	@Column(name = DEPTID)
	private String dpetId;
	// 部门名字
	public static final String DEPTNAME = "dept_name";
	@Column(name = DEPTNAME)
	private String dpetName;
	// 姓名
	public static final String NAME = "name";
	@Column(name = NAME)
	private String name;
	// 签名角色
	public static final String SIGNERROLE = "signer_role";
	@Column(name = SIGNERROLE)
	private String signerRole;
	// 签名
	public static final String SIGNNAME = "sign_name";
	@Column(name = SIGNNAME)
	private String signName;
	// 头像
	public static final String IMG = "img";
	@Column(name = IMG)
	private String img;
	//
	public static final String CREATETIME = "create_time";
	@Column(name = CREATETIME)
	private String createTime;
	//
	public static final String DLT = "dlt";
	@Column(name = DLT)
	private int dlt = 0;

	public ReportSignname() {

	}

	/**
	 * @param id
	 * @param reportId
	 * @param dpetId
	 * @param dpetName
	 * @param name
	 * @param signName
	 * @param img
	 * @param createTime
	 * @param dlt
	 */
	public ReportSignname(String id, String reportId, String dpetId, String dpetName, String name, String signName,
						  String img, String createTime, int dlt) {
		super();
		this.id = id;
		this.reportId = reportId;
		this.dpetId = dpetId;
		this.dpetName = dpetName;
		this.name = name;
		this.signName = signName;
		this.img = img;
		this.createTime = createTime;
		this.dlt = dlt;
		this.createTime = DateUtils.getCurrentLongTime();
	}

	/**
	 * @param reportId
	 * @param dpetId
	 * @param dpetName
	 * @param name
	 */
	public ReportSignname(String reportId, String dpetId, String dpetName, String name) {
		super();
		this.reportId = reportId;
		this.dpetId = dpetId;
		this.dpetName = dpetName;
		this.name = name;
		this.id = FunctionUtils.getPrimarykey();
		this.createTime = DateUtils.getCurrentLongTime();
	}

	public ReportSignname(String reportId, DbModel model) {
		this.reportId = reportId;
		this.dpetId = model.getString(Users.DEPT_ID);
		this.dpetName = model.getString(Department.NAME);
		this.name = model.getString(Users.USERNAME);
		this.id = FunctionUtils.getPrimarykey();
		this.createTime = DateUtils.getCurrentLongTime();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the reportId
	 */
	public String getReportId() {
		return reportId;
	}

	/**
	 * @param reportId
	 *            the reportId to set
	 */
	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	/**
	 * @return the dpetId
	 */
	public String getDpetId() {
		return dpetId;
	}

	/**
	 * @param dpetId
	 *            the dpetId to set
	 */
	public void setDpetId(String dpetId) {
		this.dpetId = dpetId;
	}

	/**
	 * @return the dpetName
	 */
	public String getDpetName() {
		return dpetName;
	}

	/**
	 * @param dpetName
	 *            the dpetName to set
	 */
	public void setDpetName(String dpetName) {
		this.dpetName = dpetName;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the signName
	 */
	public String getSignName() {
		return signName;
	}

	/**
	 * @param signName
	 *            the signName to set
	 */
	public void setSignName(String signName) {
		this.signName = signName;
	}

	/**
	 * @return the img
	 */
	public String getImg() {
		return img;
	}

	/**
	 * @param img
	 *            the img to set
	 */
	public void setImg(String img) {
		this.img = img;
	}

	/**
	 * @return the createTime
	 */
	public String getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime
	 *            the createTime to set
	 */
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	/**
	 * @return the dlt
	 */
	public int getDlt() {
		return dlt;
	}

	/**
	 * @param dlt
	 *            the dlt to set
	 */
	public void setDlt(int dlt) {
		this.dlt = dlt;
	}

	public boolean isSign() {
		if (TextUtils.isEmpty(signName) || TextUtils.isEmpty(img)) {
			return false;
		} else {
			return true;
		}
	}
}