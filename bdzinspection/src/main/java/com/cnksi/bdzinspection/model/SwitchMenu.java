package com.cnksi.bdzinspection.model;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "switch_menu")
public class SwitchMenu extends BaseModel{

    //
    public static final String ID = "id";
    @Column(name = ID,isId = true)
    public String id;

    // 变电站编号
    public static final String BDZID = "bdzid";
    @Column(name = BDZID)
    public String bdzid;
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

    // 备注
    public static final String CYCLE = "cycle";
    @Column(name = CYCLE)
    public String cycle;

    public static  final  String LASTMODIFYTIME = "last_modify_time";
    @Column(name = LASTMODIFYTIME)
    public String modifyTime;

    /**
     * 选择维护类型id
     */
    public static final String REPSWITCHOVERID = "rep_swithover_id";
    @Column(name = REPSWITCHOVERID)
    public String repSwithoverId;

}
