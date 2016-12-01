package com.cnksi.sjjc.bean;

import android.text.TextUtils;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

@Table(name = "report_hwcw_frd")
public class ReportHwcw implements Serializable {

	/***/
	public static final String ID = "id";
	@Column(name = ID, isId = true)
	public String id;

	/**类型*/
	public static final String TYPE = "type";
	@Column(name = TYPE)
	public String type;

	/**
	 * 计划id
	 */
	public static final String TASKID="id";
	@Column(name = TASKID)
	public  String taskid;

	/**报告id*/
	public static final String REPORT_ID = "report_id";
	@Column(name = REPORT_ID)
	public String report_id;

	/**变电站id*/
	public static final String BDZ_ID = "bdz_id";
	@Column(name = BDZ_ID)
	public String bdz_id;

	/**变电站名称*/
	public static final String BDZ_NAME = "bdz_name";
	@Column(name = BDZ_NAME)
	public String bdz_name;

	/**设备id*/
	public static final String DEVICE_ID = "device_id";
	@Column(name = DEVICE_ID)
	public String device_id;

	/**设备名称*/
	public static final String DEVICE_NAME = "device_name";
	@Column(name = DEVICE_NAME)
	public String device_name;

	/**红外热像检测*/
	public static final String HWRXJC = "hwrxjc";
	@Column(name = HWRXJC)
	public String hwrxjc;

	/**检测部位描述*/
	public static final String JCBWMS = "jcbwms";
	@Column(name = JCBWMS)
	public String jcbwms;

	/**缺陷性质*/
	public static final String QXXZ = "qxxz";
	@Column(name = QXXZ)
	public String qxxz;

	/**缺陷描述*/
	public static final String QXMS = "qxms";
	@Column(name = QXMS)
	public String qxms;

	/**分项结论*/
	public static final String FXJL = "fxjl";
	@Column(name = FXJL)
	public String fxjl;

	/**测试仪器*/
	public static final String CSYQ = "csyq";
	@Column(name = CSYQ)
	public String csyq;

	/**班次*/
	public static final String BC = "bc";
	@Column(name = BC)
	public String bc;

	/**发热部位*/
	public static final String FRBW = "frbw";
	@Column(name = FRBW)
	public String frbw;

	/**发热点温度*/
	public static final String FRDWD = "frdwd";
	@Column(name = FRDWD)
	public String frdwd;

	/**保护屏正面温度*/
	public static final String BHPZMWD = "bhpzmwd";
	@Column(name = BHPZMWD)
	public String bhpzmwd;

	/**保护屏背面温度*/
	public static final String BHPBMWD = "bhpbmwd";
	@Column(name = BHPBMWD)
	public String bhpbmwd;

	/**环境参照体温度*/
	public static final String HJCZTWD = "hjcztwd";
	@Column(name = HJCZTWD)
	public String hjcztwd;

	/**风速*/
	public static final String FS = "fs";
	@Column(name = FS)
	public String fs;

	/**温差*/
	public static final String WC = "wc";
	@Column(name = WC)
	public String wc;

	/**相对温差*/
	public static final String XDWC = "xdwc";
	@Column(name = XDWC)
	public String xdwc;

	/**负荷电流*/
	public static final String FHDL = "fhdl";
	@Column(name = FHDL)
	public String fhdl;

	/**额定电流/电压*/
	public static final String EDDL = "eddl";
	@Column(name = EDDL)
	public String eddl;

	/**备注*/
	public static final String BZ = "bz";
	@Column(name = BZ)
	public String bz;

	/**插入时间*/
	public static final String INSERT_TIME = "insert_time";
	@Column(name = INSERT_TIME)
	public String insert_time;

	/**最后更新时间*/
	public static final String LAST_MODIFY_TIME = "last_modify_time";
	@Column(name = LAST_MODIFY_TIME)
	public String last_modify_time;

	/**是否删除（0-可用，1-不可用）*/
	public static final String DLT = "dlt";
	@Column(name = DLT)
	public String dlt;
	/**红外测温图片，逗号分隔*/
	public static final String HWTX = "hwtx";
	@Column(name = HWTX)
	public String hwtx;
	/**可见光图片*/
	public static final String KJGTX = "kjgtx";
	@Column(name = KJGTX)
	public String kjgtx;


	public void FormatBean()
	{
		fs= TextUtils.isEmpty(fs)?null:fs;
		eddl= TextUtils.isEmpty(eddl)?null:eddl;
		fhdl= TextUtils.isEmpty(fhdl)?null:fhdl;
		wc= TextUtils.isEmpty(wc)?null:wc;
		frdwd= TextUtils.isEmpty(frdwd)?null:frdwd;
//		fhdl= TextUtils.isEmpty(fhdl)?null:eddl;
//		fhdl= TextUtils.isEmpty(fhdl)?null:eddl;
//		fhdl= TextUtils.isEmpty(fhdl)?null:eddl;

	}
	public String getBc() {
		return bc;
	}

	public void setBc(String bc) {
		this.bc = bc;
	}

	public String getBdz_id() {
		return bdz_id;
	}

	public void setBdz_id(String bdz_id) {
		this.bdz_id = bdz_id;
	}

	public String getBdz_name() {
		return bdz_name;
	}

	public void setBdz_name(String bdz_name) {
		this.bdz_name = bdz_name;
	}

	public String getBhpbmwd() {
		return bhpbmwd;
	}

	public void setBhpbmwd(String bhpbmwd) {
		this.bhpbmwd = bhpbmwd;
	}

	public String getBhpzmwd() {
		return bhpzmwd;
	}

	public void setBhpzmwd(String bhpzmwd) {
		this.bhpzmwd = bhpzmwd;
	}

	public String getBz() {
		return bz;
	}

	public void setBz(String bz) {
		this.bz = bz;
	}

	public String getCsyq() {
		return csyq;
	}

	public void setCsyq(String csyq) {
		this.csyq = csyq;
	}

	public String getDevice_id() {
		return device_id;
	}

	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}

	public String getDevice_name() {
		return device_name;
	}

	public void setDevice_name(String device_name) {
		this.device_name = device_name;
	}

	public String getDlt() {
		return dlt;
	}

	public void setDlt(String dlt) {
		this.dlt = dlt;
	}

	public String getEddl() {
		return eddl;
	}

	public void setEddl(String eddl) {
		this.eddl = eddl;
	}

	public String getFhdl() {
		return fhdl;
	}

	public void setFhdl(String fhdl) {
		this.fhdl = fhdl;
	}

	public String getFrbw() {
		return frbw;
	}

	public void setFrbw(String frbw) {
		this.frbw = frbw;
	}

	public String getFrdwd() {
		return frdwd;
	}

	public void setFrdwd(String frdwd) {
		this.frdwd = frdwd;
	}

	public String getFs() {
		return fs;
	}

	public void setFs(String fs) {
		this.fs = fs;
	}

	public String getFxjl() {
		return fxjl;
	}

	public void setFxjl(String fxjl) {
		this.fxjl = fxjl;
	}

	public String getHjcztwd() {
		return hjcztwd;
	}

	public void setHjcztwd(String hjcztwd) {
		this.hjcztwd = hjcztwd;
	}

	public String getHwrxjc() {
		return hwrxjc;
	}

	public void setHwrxjc(String hwrxjc) {
		this.hwrxjc = hwrxjc;
	}

	public String getHwtx() {
		return hwtx;
	}

	public void setHwtx(String hwtx) {
		this.hwtx = hwtx;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInsert_time() {
		return insert_time;
	}

	public void setInsert_time(String insert_time) {
		this.insert_time = insert_time;
	}

	public String getJcbwms() {
		return jcbwms;
	}

	public void setJcbwms(String jcbwms) {
		this.jcbwms = jcbwms;
	}

	public String getKjgtx() {
		return kjgtx;
	}

	public void setKjgtx(String kjgtx) {
		this.kjgtx = kjgtx;
	}

	public String getLast_modify_time() {
		return last_modify_time;
	}

	public void setLast_modify_time(String last_modify_time) {
		this.last_modify_time = last_modify_time;
	}

	public String getQxms() {
		return qxms;
	}

	public void setQxms(String qxms) {
		this.qxms = qxms;
	}

	public String getQxxz() {
		return qxxz;
	}

	public void setQxxz(String qxxz) {
		this.qxxz = qxxz;
	}

	public String getReport_id() {
		return report_id;
	}

	public void setReport_id(String report_id) {
		this.report_id = report_id;
	}

	public String getTaskid() {
		return taskid;
	}

	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getWc() {
		return wc;
	}

	public void setWc(String wc) {
		this.wc = wc;
	}

	public String getXdwc() {
		return xdwc;
	}

	public void setXdwc(String xdwc) {
		this.xdwc = xdwc;
	}
}
