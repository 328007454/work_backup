package com.cnksi.bdzinspection.model;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "switch_menu")
public class SwitchMenu extends BaseModel{

    //
    public static final String ID = "id";
    @Id(column = ID)
    public String id;

    // 变电站编号
    public static final String BDZID = "bdzid";
    @Column(column = BDZID)
    public String bdzid;
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

    // 备注
    public static final String CYCLE = "cycle";
    @Column(column = CYCLE)
    public String cycle;

    public static  final  String LASTMODIFYTIME = "last_modify_time";
    @Column(column = LASTMODIFYTIME)
    public String modifyTime;

    /**
     * 选择维护类型id
     */
    public static final String REPSWITCHOVERID = "rep_swithover_id";
    @Column(column = REPSWITCHOVERID)
    public String repSwithoverId;

}