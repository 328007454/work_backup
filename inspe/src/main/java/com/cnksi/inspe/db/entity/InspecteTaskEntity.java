package com.cnksi.inspe.db.entity;

import com.cnksi.inspe.base.BaseEntity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 检查任务实体类
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/23 00:31
 */
@Table(name = "xj_jyh_task")
public class InspecteTaskEntity extends BaseEntity {

    /***/
    @Column(name = "id", isId = true)
    public String id;

    /***/
    @Column(name = "city_id")
    public String city_id;

    /***/
    @Column(name = "type")
    public String type;

    /***/
    @Column(name = "check_type")
    public String check_type;

    /***/
    @Column(name = "dept_id")
    public String dept_id;

    /***/
    @Column(name = "dept_name")
    public String dept_name;

    /***/
    @Column(name = "bdz_id")
    public String bdz_id;

    /***/
    @Column(name = "bdz_name")
    public String bdz_name;

    /***/
    @Column(name = "checkuser_id")
    public String checkuser_id;

    /***/
    @Column(name = "checkuser_name")
    public String checkuser_name;

    /***/
    @Column(name = "plan_check_time")
    public String plan_check_time;

    /***/
    @Column(name = "do_check_time")
    public String do_check_time;

    /***/
    @Column(name = "pcbz_id")
    public String pcbz_id;

    /***/
    @Column(name = "dlt")
    public int dlt;

    /***/
    @Column(name = "last_modify_time")
    public String last_modify_time;

    /***/
    @Column(name = "insert_time")
    public String insert_time;

    /***/
    @Column(name = "update_time")
    public String update_time;


    public InspecteTaskEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCheck_type() {
        return check_type;
    }

    public void setCheck_type(String check_type) {
        this.check_type = check_type;
    }

    public String getDept_id() {
        return dept_id;
    }

    public void setDept_id(String dept_id) {
        this.dept_id = dept_id;
    }

    public String getDept_name() {
        return dept_name;
    }

    public void setDept_name(String dept_name) {
        this.dept_name = dept_name;
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

    public String getCheckuser_id() {
        return checkuser_id;
    }

    public void setCheckuser_id(String checkuser_id) {
        this.checkuser_id = checkuser_id;
    }

    public String getCheckuser_name() {
        return checkuser_name;
    }

    public void setCheckuser_name(String checkuser_name) {
        this.checkuser_name = checkuser_name;
    }

    public String getPlan_check_time() {
        return plan_check_time;
    }

    public void setPlan_check_time(String plan_check_time) {
        this.plan_check_time = plan_check_time;
    }

    public String getDo_check_time() {
        return do_check_time;
    }

    public void setDo_check_time(String do_check_time) {
        this.do_check_time = do_check_time;
    }

    public String getPcbz_id() {
        return pcbz_id;
    }

    public void setPcbz_id(String pcbz_id) {
        this.pcbz_id = pcbz_id;
    }

    public int getDlt() {
        return dlt;
    }

    public void setDlt(int dlt) {
        this.dlt = dlt;
    }

    public String getLast_modify_time() {
        return last_modify_time;
    }

    public void setLast_modify_time(String last_modify_time) {
        this.last_modify_time = last_modify_time;
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
}
