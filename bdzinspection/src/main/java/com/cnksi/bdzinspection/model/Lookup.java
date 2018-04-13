package com.cnksi.bdzinspection.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "lookup")
public class Lookup extends BaseModel {

    //
    public static final String ID = "id";
    @Id(column = ID)
    public String id;

    //
    public static final String LOO_ID = "loo_id";
    @Column(column = LOO_ID)
    public String loo_id;

    // K-V 键
    public static final String K = "k";
    @Column(column = K)
    public String k;

    // K-V 值
    public static final String V = "v";
    @Column(column = V)
    public String v;

    // 同一类型下的不同值的排序
    public static final String SORT = "sort";
    @Column(column = SORT)
    public String sort;

    // K-V类型
    public static final String TYPE = "type";
    @Column(column = TYPE)
    public String type;

    // 备注
    public static final String REMARK = "remark";
    @Column(column = REMARK)
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

    public Lookup(SwitchMenu menu) {
        this.id = menu.id;
        this.k = menu.k;
        this.loo_id = menu.loo_id;
        this.v = menu.v;
        this.repSwitchOverId = menu.repSwithoverId;
    }

    public Lookup(SpecialMenu menu) {
        this.id = menu.id;
        this.k = menu.k;
        this.loo_id = menu.loo_id;
        this.v = menu.v;
        this.remark = menu.remark;
        this.deviceWay = menu.deviceWay;
    }
}
