package com.cnksi.inspe.db.entity;


import com.cnksi.inspe.base.BaseDBEntity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 设备检查记录表(仅本地使用)
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/04/16 11:04
 */
@Table(name = "device_check_temp")
public class DeviceCheckEntity extends BaseDBEntity {
    public DeviceCheckEntity() {
    }

    /***/
    @Column(name = "id", autoGen = true, isId = true)
    public int id;

    /***/
    @Column(name = "task_id")
    public String task_id;

    /***/
    @Column(name = "device_id")
    public String device_id;

    /***/
    @Column(name = "plustek_type")
    public String plustek_type;

    /***/
    @Column(name = "record_type")
    public String record_type;

    /***/
    @Column(name = "dlt", property = "\0")
    public int dlt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getPlustek_type() {
        return plustek_type;
    }

    public void setPlustek_type(String plustek_type) {
        this.plustek_type = plustek_type;
    }

    public String getRecord_type() {
        return record_type;
    }

    public void setRecord_type(String record_type) {
        this.record_type = record_type;
    }

    public int getDlt() {
        return dlt;
    }

    public void setDlt(int dlt) {
        this.dlt = dlt;
    }
}
