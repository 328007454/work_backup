package com.cnksi.inspe.db.entity;


import com.cnksi.inspe.base.BaseDBEntity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 设备打类
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/04/09 09:10
 */
@Table(name = "device_bigtype")
public class DeviceTypeEntity extends BaseDBEntity {
    public DeviceTypeEntity() {
    }

    /***/
    @Column(name = "bigid")
    public int bigid;

    /***/
    @Column(name = "name")
    public String name;

    /***/
    @Column(name = "alias")
    public String alias;

    /***/
    @Column(name = "alias_pinyin")
    public String alias_pinyin;

    /***/
    @Column(name = "iswt")
    public String iswt;

    /***/
    @Column(name = "dlt")
    public int dlt;

    /***/
    @Column(name = "type")
    public String type;

    /***/
    @Column(name = "rules")
    public String rules;

    /***/
    @Column(name = "exception_deal_methods")
    public String exception_deal_methods;

    /***/
    @Column(name = "identification_prevent_measures")
    public String identification_prevent_measures;

    /***/
    @Column(name = "last_modify_time")
    public String last_modify_time;

    public int getBigid() {
        return this.bigid;
    }

    public String getName() {
        return this.name;
    }

    public String getAlias() {
        return this.alias;
    }

    public String getAlias_pinyin() {
        return this.alias_pinyin;
    }

    public String getIswt() {
        return this.iswt;
    }

    public int getDlt() {
        return this.dlt;
    }

    public String getType() {
        return this.type;
    }

    public String getRules() {
        return this.rules;
    }

    public String getException_deal_methods() {
        return this.exception_deal_methods;
    }

    public String getIdentification_prevent_measures() {
        return this.identification_prevent_measures;
    }

    public String getLast_modify_time() {
        return this.last_modify_time;
    }

    public void setBigid(int bigid) {
        this.bigid = bigid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setAlias_pinyin(String alias_pinyin) {
        this.alias_pinyin = alias_pinyin;
    }

    public void setIswt(String iswt) {
        this.iswt = iswt;
    }

    public void setDlt(int dlt) {
        this.dlt = dlt;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public void setException_deal_methods(String exception_deal_methods) {
        this.exception_deal_methods = exception_deal_methods;
    }

    public void setIdentification_prevent_measures(String identification_prevent_measures) {
        this.identification_prevent_measures = identification_prevent_measures;
    }

    public void setLast_modify_time(String last_modify_time) {
        this.last_modify_time = last_modify_time;
    }
}
