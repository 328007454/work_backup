package com.cnksi.inspe.db.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 班组表
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/4/2 11:22
 */
@Table(name = "department")
public class UserGroupEntity {

    @Column(name = "id", isId = true)
    public String id;

    /***/
    @Column(name = "pid")
    public String pid;

    /***/
    @Column(name = "name")
    public String name;

    /***/
    @Column(name = "country_id")
    public String country_id;

    /***/
    @Column(name = "type")
    public String type;

    /***/
    @Column(name = "dlt")
    public int dlt;

    /***/
    @Column(name = "dept_id")
    public String dept_id;

    /***/
    @Column(name = "insert_time")
    public String insert_time;

    /***/
    @Column(name = "update_time")
    public String update_time;

    /***/
    @Column(name = "parent_id")
    public String parent_id;

    /***/
    @Column(name = "dept_name")
    public String dept_name;

    /***/
    @Column(name = "short_name")
    public String short_name;

    /***/
    @Column(name = "dept_type")
    public int dept_type;

    /***/
    @Column(name = "dept_group")
    public String dept_group;

    /***/
    @Column(name = "zy_or_jt")
    public String zy_or_jt;

    /***/
    @Column(name = "tel")
    public String tel;

    /***/
    @Column(name = "sort")
    public String sort;

    /***/
    @Column(name = "sort2")
    public int sort2;

    /***/
    @Column(name = "dept_pinyin")
    public String dept_pinyin;

    /***/
    @Column(name = "level")
    public String level;

    /***/
    @Column(name = "refernum")
    public String refernum;

    /***/
    @Column(name = "pms_id")
    public String pms_id;

    /***/
    @Column(name = "pms_name")
    public String pms_name;

    /***/
    @Column(name = "pms_json")
    public String pms_json;

    /***/
    @Column(name = "comp_id")
    public String comp_id;

    /***/
    @Column(name = "comp_name")
    public String comp_name;

    /***/
    @Column(name = "id_temp")
    public int id_temp;

    /***/
    @Column(name = "pid_temp")
    public int pid_temp;

    /***/
    @Column(name = "country_id_temp")
    public int country_id_temp;

    /***/
    @Column(name = "dept_id_temp")
    public int dept_id_temp;

    /***/
    @Column(name = "parent_id_temp")
    public int parent_id_temp;

    /***/
    @Column(name = "last_modify_time")
    public String last_modify_time;


    public UserGroupEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry_id() {
        return country_id;
    }

    public void setCountry_id(String country_id) {
        this.country_id = country_id;
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

    public String getDept_id() {
        return dept_id;
    }

    public void setDept_id(String dept_id) {
        this.dept_id = dept_id;
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

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getDept_name() {
        return dept_name;
    }

    public void setDept_name(String dept_name) {
        this.dept_name = dept_name;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }

    public int getDept_type() {
        return dept_type;
    }

    public void setDept_type(int dept_type) {
        this.dept_type = dept_type;
    }

    public String getDept_group() {
        return dept_group;
    }

    public void setDept_group(String dept_group) {
        this.dept_group = dept_group;
    }

    public String getZy_or_jt() {
        return zy_or_jt;
    }

    public void setZy_or_jt(String zy_or_jt) {
        this.zy_or_jt = zy_or_jt;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public int getSort2() {
        return sort2;
    }

    public void setSort2(int sort2) {
        this.sort2 = sort2;
    }

    public String getDept_pinyin() {
        return dept_pinyin;
    }

    public void setDept_pinyin(String dept_pinyin) {
        this.dept_pinyin = dept_pinyin;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getRefernum() {
        return refernum;
    }

    public void setRefernum(String refernum) {
        this.refernum = refernum;
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

    public String getPms_json() {
        return pms_json;
    }

    public void setPms_json(String pms_json) {
        this.pms_json = pms_json;
    }

    public String getComp_id() {
        return comp_id;
    }

    public void setComp_id(String comp_id) {
        this.comp_id = comp_id;
    }

    public String getComp_name() {
        return comp_name;
    }

    public void setComp_name(String comp_name) {
        this.comp_name = comp_name;
    }

    public int getId_temp() {
        return id_temp;
    }

    public void setId_temp(int id_temp) {
        this.id_temp = id_temp;
    }

    public int getPid_temp() {
        return pid_temp;
    }

    public void setPid_temp(int pid_temp) {
        this.pid_temp = pid_temp;
    }

    public int getCountry_id_temp() {
        return country_id_temp;
    }

    public void setCountry_id_temp(int country_id_temp) {
        this.country_id_temp = country_id_temp;
    }

    public int getDept_id_temp() {
        return dept_id_temp;
    }

    public void setDept_id_temp(int dept_id_temp) {
        this.dept_id_temp = dept_id_temp;
    }

    public int getParent_id_temp() {
        return parent_id_temp;
    }

    public void setParent_id_temp(int parent_id_temp) {
        this.parent_id_temp = parent_id_temp;
    }

    public String getLast_modify_time() {
        return last_modify_time;
    }

    public void setLast_modify_time(String last_modify_time) {
        this.last_modify_time = last_modify_time;
    }
}
