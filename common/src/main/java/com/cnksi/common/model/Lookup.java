package com.cnksi.common.model;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "lookup")
public class Lookup extends BaseModel {

    //
    public static final String ID = "id";
    @Column(name = ID,isId = true)
    public String id;

    //
    public static final String LOO_ID = "loo_id";
    @Column(name = LOO_ID)
    public String loo_id;

    // K-V 键
    public static final String K = "k";
    @Column(name = K)
    public String k;

    // K-V 值
    public static final String V = "v";
    @Column(name = V)
    public String v;

    // 同一类型下的不同值的排序
    public static final String SORT = "sort";
    @Column(name = SORT)
    public String sort;

    // K-V类型
    public static final String TYPE = "type";
    @Column(name = TYPE)
    public String type;

    // 备注
    public static final String REMARK = "remark";
    @Column(name = REMARK)
    public String remark;

    /**
     * 设备操作方式
     */
    public String deviceWay;
    /**
     * 试验维护类型id
     */
    public  String repSwitchOverId;

    public Lookup() {
    }

    public Lookup(String k, String v) {
        this.k = k;
        this.v = v;
    }

    public Lookup(String k, String v, String type) {
        this.k = k;
        this.v = v;
        this.type = type;
    }



}
