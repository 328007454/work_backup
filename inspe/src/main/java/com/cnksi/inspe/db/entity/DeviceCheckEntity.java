package com.cnksi.inspe.db.entity;


import com.cnksi.inspe.base.BaseDBEntity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 设备检查记录表(仅本地使用)
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/04/16 11:04
 */
@Table(name = "device_check_temp")
public class DeviceCheckEntity extends BaseDBEntity {
    public DeviceCheckEntity() {
    }
    /***/
    @Column(name = "task_id")
    public String task_id;

    /***/
    @Column(name = "deviceid")
    public String deviceid;

    /***/
    @Column(name = "bdzid")
    public String bdzid;

    /***/
    @Column(name = "record_type")
    public String record_type;

    /***/
    @Column(name = "bigid")
    public String bigid;

      /***/
    @Column(name = "dlt")
    public int dlt;

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getBdzid() {
        return bdzid;
    }

    public void setBdzid(String bdzid) {
        this.bdzid = bdzid;
    }

    public String getRecord_type() {
        return record_type;
    }

    public void setRecord_type(String record_type) {
        this.record_type = record_type;
    }

    public String getBigid() {
        return bigid;
    }

    public void setBigid(String bigid) {
        this.bigid = bigid;
    }

    public int getDlt() {
        return dlt;
    }

    public void setDlt(int dlt) {
        this.dlt = dlt;
    }
}
