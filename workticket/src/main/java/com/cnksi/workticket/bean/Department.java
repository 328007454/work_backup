package com.cnksi.workticket.bean;

import android.support.annotation.IdRes;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Mr.K on 2018/5/7.
 */

@Table(name="department")
public class Department {

    public static final String ID = "id";
    @Column(name = "id",isId =true)
    public  String id= "id";


    /**
     * 部门名称
     *
     */
    public static final String NAME = "name";
    @Column(name = NAME)
    public String name;

    /**
     * 部门组数
     */

    public static final String COUNT_CANWORK_GROUP ="count_canwork_group";
    @Column(name = COUNT_CANWORK_GROUP)
    public String groupCount;


}
