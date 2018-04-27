package com.cnksi.inspe.db.entity;


import android.text.TextUtils;

import com.cnksi.core.utils.DateUtils;
import com.cnksi.inspe.base.BaseDBEntity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;
import org.xutils.db.table.DbModel;

import java.util.UUID;

/**
 * 设备
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/04/10 14:20
 */
@Table(name = "device")
public class DeviceEntity extends BaseDBEntity {
    public DeviceEntity() {
    }

    /***/
    @Column(name = "instid")
    public String instid;

    /***/
    @Column(name = "dtid")
    public String dtid;

    /***/
    @Column(name = "spid")
    public String spid;

    /***/
    @Column(name = "bdzid")
    public String bdzid;

    /***/
    @Column(name = "is_important")
    public String is_important;

    /***/
    @Column(name = "deviceid", isId = true)
    public String deviceid;

    /***/
    @Column(name = "bigid")
    public String bigid;

    /***/
    @Column(name = "name")
    public String name;

    /***/
    @Column(name = "pms_id")
    public String pms_id;

    /***/
    @Column(name = "pms_name")
    public String pms_name;

    /***/
    @Column(name = "pms_jgid")
    public String pms_jgid;

    /***/
    @Column(name = "pms_azwzid")
    public String pms_azwzid;

    /***/
    @Column(name = "pms_azwz")
    public String pms_azwz;

    /***/
    @Column(name = "chk")
    public int chk;

    /***/
    @Column(name = "sort")
    public int sort;

    /***/
    @Column(name = "has_yb")
    public String has_yb;

    /***/
    @Column(name = "y")
    public String y;

    /***/
    @Column(name = "name_pinyin")
    public String name_pinyin;

    /***/
    @Column(name = "name_short")
    public String name_short;

    /***/
    @Column(name = "name_short_pinyin")
    public String name_short_pinyin;

    /***/
    @Column(name = "manufacturer")
    public String manufacturer;

    /***/
    @Column(name = "dlt")
    public int dlt;

    /***/
    @Column(name = "pms_spid")
    public String pms_spid;

    /***/
    @Column(name = "has_copy")
    public String has_copy;

    /***/
    @Column(name = "commissioning_date")
    public String commissioning_date;

    /***/
    @Column(name = "pic")
    public String pic;

    /***/
    @Column(name = "change_pic")
    public String change_pic;

    /***/
    @Column(name = "createtime")
    public String createtime = DateUtils.getCurrentLongTime();

    /***/
    @Column(name = "creater")
    public String creater;

    /***/
    @Column(name = "voltage")
    public String voltage;

    /***/
    @Column(name = "type")
    public String type;

    /***/
    @Column(name = "model")
    public String model;

    /***/
    @Column(name = "insert_time")
    public String insert_time;

    /***/
    @Column(name = "update_time")
    public String update_time;

    /***/
    @Column(name = "kind")
    public String kind;

    /***/
    @Column(name = "latitude")
    public String latitude;

    /***/
    @Column(name = "longitude")
    public String longitude;

    /***/
    @Column(name = "pms_device_type")
    public String pms_device_type;

    /***/
    @Column(name = "device_type")
    public String device_type;

    /***/
    @Column(name = "device_type_old")
    public String device_type_old;

    /***/
    @Column(name = "sf6qtedyl")
    public double sf6qtedyl;

    /***/
    @Column(name = "sf6qtbjyl")
    public double sf6qtbjyl;

    /***/
    @Column(name = "sblx")
    public String sblx;

    /***/
    @Column(name = "sblxmc")
    public String sblxmc;

    /***/
    @Column(name = "pms_remark")
    public String pms_remark;

    /***/
    @Column(name = "last_modify_time")
    public String last_modify_time;

    /***/
    @Column(name = "remark")
    public String remark;

    /***/
    @Column(name = "dydj")
    public String dydj;

    public String spaceName;

    public String getInstid() {
        return this.instid;
    }

    public String getDtid() {
        return this.dtid;
    }

    public String getSpid() {
        return this.spid;
    }

    public String getBdzid() {
        return this.bdzid;
    }

    public String getIs_important() {
        return this.is_important;
    }

    public String getDeviceid() {
        return this.deviceid;
    }

    public String getBigid() {
        return this.bigid;
    }

    public String getName() {
        return this.name;
    }

    public String getPms_id() {
        return this.pms_id;
    }

    public String getPms_name() {
        return this.pms_name;
    }

    public String getPms_jgid() {
        return this.pms_jgid;
    }

    public String getPms_azwzid() {
        return this.pms_azwzid;
    }

    public String getPms_azwz() {
        return this.pms_azwz;
    }

    public int getChk() {
        return this.chk;
    }

    public int getSort() {
        return this.sort;
    }

    public String getHas_yb() {
        return this.has_yb;
    }

    public String getY() {
        return this.y;
    }

    public String getName_pinyin() {
        return this.name_pinyin;
    }

    public String getName_short() {
        return this.name_short;
    }

    public String getName_short_pinyin() {
        return this.name_short_pinyin;
    }

    public String getManufacturer() {
        return this.manufacturer;
    }

    public int getDlt() {
        return this.dlt;
    }

    public String getPms_spid() {
        return this.pms_spid;
    }

    public String getHas_copy() {
        return this.has_copy;
    }

    public String getCommissioning_date() {
        return this.commissioning_date;
    }

    public String getPic() {
        return this.pic;
    }

    public String getChange_pic() {
        return this.change_pic;
    }

    public String getCreatetime() {
        return this.createtime;
    }

    public String getCreater() {
        return this.creater;
    }

    public String getVoltage() {
        return this.voltage;
    }

    public String getType() {
        return this.type;
    }

    public String getModel() {
        return this.model;
    }

    public String getInsert_time() {
        return this.insert_time;
    }

    public String getUpdate_time() {
        return this.update_time;
    }

    public String getKind() {
        return this.kind;
    }

    public String getLatitude() {
        return this.latitude;
    }

    public String getLongitude() {
        return this.longitude;
    }

    public String getPms_device_type() {
        return this.pms_device_type;
    }

    public String getDevice_type() {
        return this.device_type;
    }

    public String getDevice_type_old() {
        return this.device_type_old;
    }

    public double getSf6qtedyl() {
        return this.sf6qtedyl;
    }

    public double getSf6qtbjyl() {
        return this.sf6qtbjyl;
    }

    public String getSblx() {
        return this.sblx;
    }

    public String getSblxmc() {
        return this.sblxmc;
    }

    public String getPms_remark() {
        return this.pms_remark;
    }

    public String getLast_modify_time() {
        return this.last_modify_time;
    }

    public String getRemark() {
        return this.remark;
    }

    public String getDydj() {
        return this.dydj;
    }

    public void setInstid(String instid) {
        this.instid = instid;
    }

    public void setDtid(String dtid) {
        this.dtid = dtid;
    }

    public void setSpid(String spid) {
        this.spid = spid;
    }

    public void setBdzid(String bdzid) {
        this.bdzid = bdzid;
    }

    public void setIs_important(String is_important) {
        this.is_important = is_important;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public void setBigid(String bigid) {
        this.bigid = bigid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPms_id(String pms_id) {
        this.pms_id = pms_id;
    }

    public void setPms_name(String pms_name) {
        this.pms_name = pms_name;
    }

    public void setPms_jgid(String pms_jgid) {
        this.pms_jgid = pms_jgid;
    }

    public void setPms_azwzid(String pms_azwzid) {
        this.pms_azwzid = pms_azwzid;
    }

    public void setPms_azwz(String pms_azwz) {
        this.pms_azwz = pms_azwz;
    }

    public void setChk(int chk) {
        this.chk = chk;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public void setHas_yb(String has_yb) {
        this.has_yb = has_yb;
    }

    public void setY(String y) {
        this.y = y;
    }

    public void setName_pinyin(String name_pinyin) {
        this.name_pinyin = name_pinyin;
    }

    public void setName_short(String name_short) {
        this.name_short = name_short;
    }

    public void setName_short_pinyin(String name_short_pinyin) {
        this.name_short_pinyin = name_short_pinyin;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setDlt(int dlt) {
        this.dlt = dlt;
    }

    public void setPms_spid(String pms_spid) {
        this.pms_spid = pms_spid;
    }

    public void setHas_copy(String has_copy) {
        this.has_copy = has_copy;
    }

    public void setCommissioning_date(String commissioning_date) {
        this.commissioning_date = commissioning_date;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public void setChange_pic(String change_pic) {
        this.change_pic = change_pic;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setInsert_time(String insert_time) {
        this.insert_time = insert_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setPms_device_type(String pms_device_type) {
        this.pms_device_type = pms_device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public void setDevice_type_old(String device_type_old) {
        this.device_type_old = device_type_old;
    }

    public void setSf6qtedyl(double sf6qtedyl) {
        this.sf6qtedyl = sf6qtedyl;
    }

    public void setSf6qtbjyl(double sf6qtbjyl) {
        this.sf6qtbjyl = sf6qtbjyl;
    }

    public void setSblx(String sblx) {
        this.sblx = sblx;
    }

    public void setSblxmc(String sblxmc) {
        this.sblxmc = sblxmc;
    }

    public void setPms_remark(String pms_remark) {
        this.pms_remark = pms_remark;
    }

    public void setLast_modify_time(String last_modify_time) {
        this.last_modify_time = last_modify_time;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setDydj(String dydj) {
        this.dydj = dydj;
    }


    public void setSpace(DbModel spaceModel, String bigid) {
        if (TextUtils.isEmpty(this.deviceid))
            this.deviceid = UUID.randomUUID().toString();
        this.spid = spaceModel.getString("spid");
        this.bdzid = spaceModel.getString("bdzid");
        this.dlt = 1;
        this.bigid = bigid;
        this.spaceName = spaceModel.getString("name");
    }

    public void setDeviceInfo(String bdzid, String bigid) {
        if (TextUtils.isEmpty(this.deviceid))
            this.deviceid = UUID.randomUUID().toString();
        this.bdzid = bdzid;
        this.bigid = bigid;
        this.dlt = 1;
    }
}
