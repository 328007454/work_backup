package com.cnksi.common.model;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "users")
public class Users extends BaseModel {

    // 用户账户
    public static final String ACCOUNT = "account";
    @Column(name = ACCOUNT, isId = true)
    public String account;

    // 用户名称
    public static final String USERNAME = "username";
    @Column(name = USERNAME)
    public String username;

    // 用户密码
    public static final String PWD = "pwd";
    @Column(name = PWD)
    public String pwd;

    // 用户密码旧密码
    public static final String OLDPWD = "old_pwd";
    @Column(name = OLDPWD)
    public String oldPwd;



    // 用户性别
    public static final String SEX = "sex";
    @Column(name = SEX)
    public String sex;

    // 是否添加
    public static final String ISADD = "isadd";
    @Column(name = ISADD)
    public String isadd;

    // 用户类型(super 超级管理员, tracker巡检人员)
    public static final String TYPE = "type";
    @Column(name = TYPE)
    public String type;

    /**
     * 部门
     */
    public static final String DEPT_ID = "dept_id";
    @Column(name = DEPT_ID)
    public String dept_id;

    //
    public static final String HEADPIC = "headpic";
    @Column(name = HEADPIC)
    public String headpic;

    public static final String CCP_KEY_SID = "ccp_key_sid";
    @Column(name = CCP_KEY_SID)
    public String ccp_key_sid;

    public static final String CCP_KEY_PWD = "ccp_key_pwd";
    @Column(name = CCP_KEY_PWD)
    public String ccp_key_pwd;

    public static final String CCP_KEY_SUBID = "ccp_key_subid";
    @Column(name = CCP_KEY_SUBID)
    public String ccp_key_subid;

    public static final String CCP_KEY_SUBPWD = "ccp_key_subpwd";
    @Column(name = CCP_KEY_SUBPWD)
    public String ccp_key_subpwd;

    public static final String DEPT_NAME = "dept_name";
    @Column(name = DEPT_NAME)
    public String deptName;

}
