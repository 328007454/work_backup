package com.cnksi.bdzinspection.model;

import android.text.TextUtils;

import com.cnksi.xscore.xsutils.DateUtils;
import com.cnksi.xscore.xsutils.FunctionUtils;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;
import org.xutils.db.table.DbModel;


/**
 * @author Wastrel
 * @date 创建时间：2016年8月10日 上午9:33:33 TODO
 */
@Table(name = "report_signname")
public class ReportSignname extends BaseModel {
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
    public static final String ACCOUNT = "account";
    @Column(name = ACCOUNT)
    private String account;


    public ReportSignname() {
    }


    public ReportSignname(String reportId, String role, DbModel model) {
        this.reportId = reportId;
        this.signerRole = role;
        this.dpetId = model.getString(Users.DEPT_ID);
        this.dpetName = model.getString(Department.NAME);
        this.name = model.getString(Users.USERNAME);
        this.id = FunctionUtils.getPrimarykey();
        this.createTime = DateUtils.getCurrentLongTime();
        this.account = model.getString("account");
    }


    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
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
     * @param reportId the reportId to set
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
     * @param dpetId the dpetId to set
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
     * @param dpetName the dpetName to set
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
     * @param name the name to set
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
     * @param signName the signName to set
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
     * @param img the img to set
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
     * @param createTime the createTime to set
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the dlt
     */
    public String getDlt() {
        return dlt;
    }

    /**
     * @return the signerRole
     */
    public String getSignerRole() {
        return signerRole;
    }

    /**
     * @param signerRole the signerRole to set
     */
    public void setSignerRole(String signerRole) {
        this.signerRole = signerRole;
    }

    /**
     * @param dlt the dlt to set
     */
    public void setDlt(String dlt) {
        this.dlt = dlt;
    }

    public boolean isSign() {
        if (TextUtils.isEmpty(signName)) {
            return false;
        } else {
            return true;
        }
    }

    public String getAccount() {

        return  account;
    }
}
