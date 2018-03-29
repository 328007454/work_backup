package com.cnksi.inspe.db.entity;

import com.cnksi.inspe.base.BaseEntity;
import com.cnksi.inspe.type.RoleType;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 用户实体类
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/29 09:28
 */

@Table(name = "users")
public class UserEntity extends BaseEntity {

    /***/
    @Column(name = "id", isId = true)
    public String id;

    /***/
    @Column(name = "account")
    public String account;

    /***/
    @Column(name = "username")
    public String username;

    /***/
    @Column(name = "pwd")
    public String pwd;

    /***/
    @Column(name = "old_pwd")
    public String old_pwd;

    /***/
    @Column(name = "dept_id")
    public String dept_id;

    /***/
    @Column(name = "sex")
    public int sex;

    /***/
    @Column(name = "isadd")
    public int isadd;

    /***/
    @Column(name = "type")
    public String type;

    /***/
    @Column(name = "dlt")
    public int dlt;

    /***/
    @Column(name = "headpic")
    public String headpic;

    /***/
    @Column(name = "county_id")
    public int county_id;

    /***/
    @Column(name = "city_id")
    public String city_id;

    /***/
    @Column(name = "is_receive_message")
    public int is_receive_message;

    /***/
    @Column(name = "phone_number")
    public String phone_number;

    /***/
    @Column(name = "ccp_key_sid")
    public String ccp_key_sid;

    /***/
    @Column(name = "ccp_key_pwd")
    public String ccp_key_pwd;

    /***/
    @Column(name = "ccp_key_subid")
    public String ccp_key_subid;

    /***/
    @Column(name = "ccp_key_subpwd")
    public String ccp_key_subpwd;

    /***/
    @Column(name = "bz_user_id")
    public String bz_user_id;

    /***/
    @Column(name = "insert_time")
    public String insert_time;

    /***/
    @Column(name = "update_time")
    public String update_time;

    /***/
    @Column(name = "child_id")
    public String child_id;

    /***/
    @Column(name = "name")
    public String name;

    /***/
    @Column(name = "tel")
    public String tel;

    /***/
    @Column(name = "position")
    public String position;

    /***/
    @Column(name = "position_id")
    public String position_id;

    /***/
    @Column(name = "work_type")
    public String work_type;

    /***/
    @Column(name = "score")
    public int score;

    /***/
    @Column(name = "eaccount")
    public String eaccount;

    /***/
    @Column(name = "sort")
    public String sort;

    /***/
    @Column(name = "head_pic")
    public String head_pic;

    /***/
    @Column(name = "name_pinyin")
    public String name_pinyin;

    /***/
    @Column(name = "signature")
    public String signature;

    /***/
    @Column(name = "pms_id")
    public String pms_id;

    /***/
    @Column(name = "pms_name")
    public String pms_name;

    /***/
    @Column(name = "pms_pwd")
    public String pms_pwd;

    /***/
    @Column(name = "pms_account")
    public String pms_account;

    /***/
    @Column(name = "city_id_temp")
    public int city_id_temp;

    /***/
    @Column(name = "dept_id_temp")
    public int dept_id_temp;

    /***/
    @Column(name = "child_id_temp")
    public int child_id_temp;

    /***/
    @Column(name = "bz_user_id_temp")
    public int bz_user_id_temp;

    /***/
    @Column(name = "id_temp")
    public int id_temp;

    /***/
    @Column(name = "last_modify_time")
    public String last_modify_time;

    /***/
    @Column(name = "islocked")
    public String islocked;

    /***/
    @Column(name = "pwd_error_count")
    public int pwd_error_count;

    /***/
    @Column(name = "lastly_login_time")
    public String lastly_login_time;

    /***/
    @Column(name = "role_name")
    public String role_name;

    public RoleType roleType;

    public UserEntity() {
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getOld_pwd() {
        return old_pwd;
    }

    public void setOld_pwd(String old_pwd) {
        this.old_pwd = old_pwd;
    }

    public String getDept_id() {
        return dept_id;
    }

    public void setDept_id(String dept_id) {
        this.dept_id = dept_id;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getIsadd() {
        return isadd;
    }

    public void setIsadd(int isadd) {
        this.isadd = isadd;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDlt() {
        return dlt;
    }

    public void setDlt(int dlt) {
        this.dlt = dlt;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public int getCounty_id() {
        return county_id;
    }

    public void setCounty_id(int county_id) {
        this.county_id = county_id;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public int getIs_receive_message() {
        return is_receive_message;
    }

    public void setIs_receive_message(int is_receive_message) {
        this.is_receive_message = is_receive_message;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getCcp_key_sid() {
        return ccp_key_sid;
    }

    public void setCcp_key_sid(String ccp_key_sid) {
        this.ccp_key_sid = ccp_key_sid;
    }

    public String getCcp_key_pwd() {
        return ccp_key_pwd;
    }

    public void setCcp_key_pwd(String ccp_key_pwd) {
        this.ccp_key_pwd = ccp_key_pwd;
    }

    public String getCcp_key_subid() {
        return ccp_key_subid;
    }

    public void setCcp_key_subid(String ccp_key_subid) {
        this.ccp_key_subid = ccp_key_subid;
    }

    public String getCcp_key_subpwd() {
        return ccp_key_subpwd;
    }

    public void setCcp_key_subpwd(String ccp_key_subpwd) {
        this.ccp_key_subpwd = ccp_key_subpwd;
    }

    public String getBz_user_id() {
        return bz_user_id;
    }

    public void setBz_user_id(String bz_user_id) {
        this.bz_user_id = bz_user_id;
    }

    public String getInsert_time() {
        return insert_time;
    }

    public void setInsert_time(String insert_time) {
        this.insert_time = insert_time;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getChild_id() {
        return child_id;
    }

    public void setChild_id(String child_id) {
        this.child_id = child_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPosition_id() {
        return position_id;
    }

    public void setPosition_id(String position_id) {
        this.position_id = position_id;
    }

    public String getWork_type() {
        return work_type;
    }

    public void setWork_type(String work_type) {
        this.work_type = work_type;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getEaccount() {
        return eaccount;
    }

    public void setEaccount(String eaccount) {
        this.eaccount = eaccount;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getHead_pic() {
        return head_pic;
    }

    public void setHead_pic(String head_pic) {
        this.head_pic = head_pic;
    }

    public String getName_pinyin() {
        return name_pinyin;
    }

    public void setName_pinyin(String name_pinyin) {
        this.name_pinyin = name_pinyin;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getPms_id() {
        return pms_id;
    }

    public void setPms_id(String pms_id) {
        this.pms_id = pms_id;
    }

    public String getPms_name() {
        return pms_name;
    }

    public void setPms_name(String pms_name) {
        this.pms_name = pms_name;
    }

    public String getPms_pwd() {
        return pms_pwd;
    }

    public void setPms_pwd(String pms_pwd) {
        this.pms_pwd = pms_pwd;
    }

    public String getPms_account() {
        return pms_account;
    }

    public void setPms_account(String pms_account) {
        this.pms_account = pms_account;
    }

    public int getCity_id_temp() {
        return city_id_temp;
    }

    public void setCity_id_temp(int city_id_temp) {
        this.city_id_temp = city_id_temp;
    }

    public int getDept_id_temp() {
        return dept_id_temp;
    }

    public void setDept_id_temp(int dept_id_temp) {
        this.dept_id_temp = dept_id_temp;
    }

    public int getChild_id_temp() {
        return child_id_temp;
    }

    public void setChild_id_temp(int child_id_temp) {
        this.child_id_temp = child_id_temp;
    }

    public int getBz_user_id_temp() {
        return bz_user_id_temp;
    }

    public void setBz_user_id_temp(int bz_user_id_temp) {
        this.bz_user_id_temp = bz_user_id_temp;
    }

    public int getId_temp() {
        return id_temp;
    }

    public void setId_temp(int id_temp) {
        this.id_temp = id_temp;
    }

    public String getLast_modify_time() {
        return last_modify_time;
    }

    public void setLast_modify_time(String last_modify_time) {
        this.last_modify_time = last_modify_time;
    }

    public String getIslocked() {
        return islocked;
    }

    public void setIslocked(String islocked) {
        this.islocked = islocked;
    }

    public int getPwd_error_count() {
        return pwd_error_count;
    }

    public void setPwd_error_count(int pwd_error_count) {
        this.pwd_error_count = pwd_error_count;
    }

    public String getLastly_login_time() {
        return lastly_login_time;
    }

    public void setLastly_login_time(String lastly_login_time) {
        this.lastly_login_time = lastly_login_time;
    }

    public String getRole_name() {
        return role_name;
    }

    public void setRole_name(String role_name) {
        this.role_name = role_name;
    }
}
