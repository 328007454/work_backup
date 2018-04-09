package com.cnksi.inspe.db.entity;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * 字典表
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/04/09 18:26
 */
@Table(name = "lookup_local")
public class DictionaryEntity {
     public DictionaryEntity(){
    }

    /***/
    @Column(name = "id",isId = true)
    public String id;

    /***/
    @Column(name = "bdzid")
    public String bdzid;

    /***/
    @Column(name = "k")
    public String k;

    /***/
    @Column(name = "v")
    public String v;

    /***/
    @Column(name = "xjnr")
    public String xjnr;

    /***/
    @Column(name = "xsjg")
    public String xsjg;

    /***/
    @Column(name = "sort")
    public int sort;

    /***/
    @Column(name = "type")
    public String type;

    /***/
    @Column(name = "remark")
    public String remark;

    /***/
    @Column(name = "insert_time")
    public String insert_time;

    /***/
    @Column(name = "update_time")
    public String update_time;

    /***/
    @Column(name = "last_modify_time")
    public String last_modify_time;

    /***/
    @Column(name = "dlt")
    public int dlt;

    public String getId(){
        return this.id;
    }

    public String getBdzid(){
        return this.bdzid;
    }

    public String getK(){
        return this.k;
    }

    public String getV(){
        return this.v;
    }

    public String getXjnr(){
        return this.xjnr;
    }

    public String getXsjg(){
        return this.xsjg;
    }

    public int getSort(){
        return this.sort;
    }

    public String getType(){
        return this.type;
    }

    public String getRemark(){
        return this.remark;
    }

    public String getInsert_time(){
        return this.insert_time;
    }

    public String getUpdate_time(){
        return this.update_time;
    }

    public String getLast_modify_time(){
        return this.last_modify_time;
    }

    public int getDlt(){
        return this.dlt;
    }

    public void setId(String id){
        this.id = id;
    }

    public void setBdzid(String bdzid){
        this.bdzid = bdzid;
    }

    public void setK(String k){
        this.k = k;
    }

    public void setV(String v){
        this.v = v;
    }

    public void setXjnr(String xjnr){
        this.xjnr = xjnr;
    }

    public void setXsjg(String xsjg){
        this.xsjg = xsjg;
    }

    public void setSort(int sort){
        this.sort = sort;
    }

    public void setType(String type){
        this.type = type;
    }

    public void setRemark(String remark){
        this.remark = remark;
    }

    public void setInsert_time(String insert_time){
        this.insert_time = insert_time;
    }

    public void setUpdate_time(String update_time){
        this.update_time = update_time;
    }

    public void setLast_modify_time(String last_modify_time){
        this.last_modify_time = last_modify_time;
    }

    public void setDlt(int dlt){
        this.dlt = dlt;
    }



}
