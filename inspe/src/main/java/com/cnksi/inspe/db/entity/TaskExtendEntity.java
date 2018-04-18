package com.cnksi.inspe.db.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/04/18 09:10
 */
@Table(name = "xj_jyh_task_extend")
public class TaskExtendEntity {
    public TaskExtendEntity() {
    }

    /***/
    @Column(name = "id", isId = true)
    public String id;

    /***/
    @Column(name = "task_id")
    public String task_id;

    /***/
    @Column(name = "person_id")
    public String person_id;

    /***/
    @Column(name = "person_name")
    public String person_name;

    /***/
    @Column(name = "checked_device_bigid")
    public String checked_device_bigid;

    /***/
    @Column(name = "dlt")
    public int dlt;

    /***/
    @Column(name = "insert_time")
    public String insert_time;

    /***/
    @Column(name = "update_time")
    public String update_time;

    /***/
    @Column(name = "last_modify_time")
    public String last_modify_time;

    public String getId() {
        return this.id;
    }

    public String getTask_id() {
        return this.task_id;
    }

    public String getPerson_id() {
        return this.person_id;
    }

    public String getPerson_name() {
        return this.person_name;
    }

    public String getChecked_device_bigid() {
        return this.checked_device_bigid;
    }

    public int getDlt() {
        return this.dlt;
    }

    public String getInsert_time() {
        return this.insert_time;
    }

    public String getUpdate_time() {
        return this.update_time;
    }

    public String getLast_modify_time() {
        return this.last_modify_time;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public void setPerson_id(String person_id) {
        this.person_id = person_id;
    }

    public void setPerson_name(String person_name) {
        this.person_name = person_name;
    }

    public void setChecked_device_bigid(String checked_device_bigid) {
        this.checked_device_bigid = checked_device_bigid;
    }

    public void setDlt(int dlt) {
        this.dlt = dlt;
    }

    public void setInsert_time(String insert_time) {
        this.insert_time = insert_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public void setLast_modify_time(String last_modify_time) {
        this.last_modify_time = last_modify_time;
    }

}
