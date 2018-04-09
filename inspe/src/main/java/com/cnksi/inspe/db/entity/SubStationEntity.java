package com.cnksi.inspe.db.entity;


import com.cnksi.inspe.base.BaseDBEntity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 变电站实体对象
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/04/09 15:30
 */
@Table(name = "bdz")
public class SubStationEntity extends BaseDBEntity {
    public SubStationEntity() {
    }

    /***/
    @Column(name = "bdzid", isId = true)
    public String bdzid;

    /***/
    @Column(name = "power_company")
    public String power_company;

    /***/
    @Column(name = "name")
    public String name;

    /***/
    @Column(name = "pms_id")
    public String pms_id;

    /***/
    @Column(name = "addr")
    public String addr;

    /***/
    @Column(name = "contact")
    public String contact;

    /***/
    @Column(name = "phone")
    public String phone;

    /***/
    @Column(name = "scale")
    public int scale;

    /***/
    @Column(name = "roadmap")
    public String roadmap;

    /***/
    @Column(name = "dlt")
    public int dlt;

    /***/
    @Column(name = "dept_id")
    public String dept_id;

    /***/
    @Column(name = "county_id")
    public int county_id;

    /***/
    @Column(name = "scan_way")
    public String scan_way;

    /***/
    @Column(name = "insert_time")
    public String insert_time;

    /***/
    @Column(name = "update_time")
    public String update_time;

    /***/
    @Column(name = "level")
    public String level;

    /***/
    @Column(name = "isduty")
    public String isduty;

    /***/
    @Column(name = "pms_name")
    public String pms_name;

    /***/
    @Column(name = "publish_time")
    public String publish_time;

    /***/
    @Column(name = "city_id")
    public int city_id;

    /***/
    @Column(name = "maintenance_finish_way")
    public String maintenance_finish_way;

    /***/
    @Column(name = "folder_name")
    public String folder_name;

    /***/
    @Column(name = "category")
    public String category;

    /***/
    @Column(name = "last_modify_time")
    public String last_modify_time;

    public String getBdzid() {
        return this.bdzid;
    }

    public String getPower_company() {
        return this.power_company;
    }

    public String getName() {
        return this.name;
    }

    public String getPms_id() {
        return this.pms_id;
    }

    public String getAddr() {
        return this.addr;
    }

    public String getContact() {
        return this.contact;
    }

    public String getPhone() {
        return this.phone;
    }

    public int getScale() {
        return this.scale;
    }

    public String getRoadmap() {
        return this.roadmap;
    }

    public int getDlt() {
        return this.dlt;
    }

    public String getDept_id() {
        return this.dept_id;
    }

    public int getCounty_id() {
        return this.county_id;
    }

    public String getScan_way() {
        return this.scan_way;
    }

    public String getInsert_time() {
        return this.insert_time;
    }

    public String getUpdate_time() {
        return this.update_time;
    }

    public String getLevel() {
        return this.level;
    }

    public String getIsduty() {
        return this.isduty;
    }

    public String getPms_name() {
        return this.pms_name;
    }

    public String getPublish_time() {
        return this.publish_time;
    }

    public int getCity_id() {
        return this.city_id;
    }

    public String getMaintenance_finish_way() {
        return this.maintenance_finish_way;
    }

    public String getFolder_name() {
        return this.folder_name;
    }

    public String getCategory() {
        return this.category;
    }

    public String getLast_modify_time() {
        return this.last_modify_time;
    }

    public void setBdzid(String bdzid) {
        this.bdzid = bdzid;
    }

    public void setPower_company(String power_company) {
        this.power_company = power_company;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPms_id(String pms_id) {
        this.pms_id = pms_id;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public void setRoadmap(String roadmap) {
        this.roadmap = roadmap;
    }

    public void setDlt(int dlt) {
        this.dlt = dlt;
    }

    public void setDept_id(String dept_id) {
        this.dept_id = dept_id;
    }

    public void setCounty_id(int county_id) {
        this.county_id = county_id;
    }

    public void setScan_way(String scan_way) {
        this.scan_way = scan_way;
    }

    public void setInsert_time(String insert_time) {
        this.insert_time = insert_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setIsduty(String isduty) {
        this.isduty = isduty;
    }

    public void setPms_name(String pms_name) {
        this.pms_name = pms_name;
    }

    public void setPublish_time(String publish_time) {
        this.publish_time = publish_time;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }

    public void setMaintenance_finish_way(String maintenance_finish_way) {
        this.maintenance_finish_way = maintenance_finish_way;
    }

    public void setFolder_name(String folder_name) {
        this.folder_name = folder_name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setLast_modify_time(String last_modify_time) {
        this.last_modify_time = last_modify_time;
    }

}
