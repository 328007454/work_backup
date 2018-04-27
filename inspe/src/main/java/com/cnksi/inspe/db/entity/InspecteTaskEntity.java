package com.cnksi.inspe.db.entity;

import com.cnksi.inspe.base.BaseEntity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 检查任务实体类
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/23 00:31
 */
@Table(name = "xj_jyh_task")
public class InspecteTaskEntity extends BaseEntity {
    public InspecteTaskEntity() {
    }

    /***/
    @Column(name = "id", isId = true)
    public String id;

    /***/
    @Column(name = "city_id")
    public String city_id;

    /***/
    @Column(name = "template_id")
    public String template_id;

    /***/
    @Column(name = "type")
    public String type;

    /***/
    @Column(name = "progress")
    public String progress;

    /***/
    @Column(name = "check_type")
    public String check_type;

    /***/
    @Column(name = "checked_device_bigid")
    public String checked_device_bigid;

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
    @Column(name = "create_person_id")
    public String create_person_id;

    /***/
    @Column(name = "create_person_name")
    public String create_person_name;

    /***/
    @Column(name = "group_person_id")
    public String group_person_id;

    /***/
    @Column(name = "group_person_name")
    public String group_person_name;

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

    /** 检查人ID */
    public String person_id;
    /** 检查人Name */
    public String person_name;
    /** 检查项 */
    public String persion_device_bigid;

    public String getPersion_device_bigid() {
        return persion_device_bigid;
    }

    public void setPersion_device_bigid(String persion_device_bigid) {
        this.persion_device_bigid = persion_device_bigid;
    }

    public String getPerson_id() {
        return person_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }

    public String getPerson_name() {
        return person_name;
    }

    public void setPerson_name(String person_name) {
        this.person_name = person_name;
    }

    public String getId() {
        return this.id;
    }

    public String getCity_id() {
        return this.city_id;
    }

    public String getTemplate_id() {
        return this.template_id;
    }

    public String getType() {
        return this.type;
    }

    public String getProgress() {
        return this.progress;
    }

    public String getCheck_type() {
        return this.check_type;
    }

    public String getChecked_device_bigid() {
        return this.checked_device_bigid;
    }

    public String getDept_id() {
        return this.dept_id;
    }

    public String getDept_name() {
        return this.dept_name;
    }

    public String getBdz_id() {
        return this.bdz_id;
    }

    public String getBdz_name() {
        return this.bdz_name;
    }

    public String getCheckuser_id() {
        return this.checkuser_id;
    }

    public String getCheckuser_name() {
        return this.checkuser_name;
    }

    public String getPlan_check_time() {
        return this.plan_check_time;
    }

    public String getDo_check_time() {
        return this.do_check_time;
    }

    public String getPcbz_id() {
        return this.pcbz_id;
    }

    public String getCreate_person_id() {
        return this.create_person_id;
    }

    public String getCreate_person_name() {
        return this.create_person_name;
    }

    public String getGroup_person_id() {
        return this.group_person_id;
    }

    public String getGroup_person_name() {
        return this.group_person_name;
    }

    public int getDlt() {
        return this.dlt;
    }

    public String getLast_modify_time() {
        return this.last_modify_time;
    }

    public String getInsert_time() {
        return this.insert_time;
    }

    public String getUpdate_time() {
        return this.update_time;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public void setCheck_type(String check_type) {
        this.check_type = check_type;
    }

    public void setChecked_device_bigid(String checked_device_bigid) {
        this.checked_device_bigid = checked_device_bigid;
    }

    public void setDept_id(String dept_id) {
        this.dept_id = dept_id;
    }

    public void setDept_name(String dept_name) {
        this.dept_name = dept_name;
    }

    public void setBdz_id(String bdz_id) {
        this.bdz_id = bdz_id;
    }

    public void setBdz_name(String bdz_name) {
        this.bdz_name = bdz_name;
    }

    public void setCheckuser_id(String checkuser_id) {
        this.checkuser_id = checkuser_id;
    }

    public void setCheckuser_name(String checkuser_name) {
        this.checkuser_name = checkuser_name;
    }

    public void setPlan_check_time(String plan_check_time) {
        this.plan_check_time = plan_check_time;
    }

    public void setDo_check_time(String do_check_time) {
        this.do_check_time = do_check_time;
    }

    public void setPcbz_id(String pcbz_id) {
        this.pcbz_id = pcbz_id;
    }

    public void setCreate_person_id(String create_person_id) {
        this.create_person_id = create_person_id;
    }

    public void setCreate_person_name(String create_person_name) {
        this.create_person_name = create_person_name;
    }

    public void setGroup_person_id(String group_person_id) {
        this.group_person_id = group_person_id;
    }

    public void setGroup_person_name(String group_person_name) {
        this.group_person_name = group_person_name;
    }

    public void setDlt(int dlt) {
        this.dlt = dlt;
    }

    public void setLast_modify_time(String last_modify_time) {
        this.last_modify_time = last_modify_time;
    }

    public void setInsert_time(String insert_time) {
        this.insert_time = insert_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }


}
