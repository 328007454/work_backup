package com.cnksi.workticket.bean;

import org.xutils.db.annotation.Column;

/**
 * @decrption 本地配置信息
 * @author Mr.K  on 2018/5/7.
 */

public class LookUpLocal {

    public static final String ID = "id";
    @Column(name = "id",isId =true)
    public  String id= "id";

    /**
     * key；
     */
    public static final String K = "k";
    @Column(name = K)
    public String k;

    /**
     * 值
     */
    public static final String V = "v";
    @Column(name = V)
    public String v;

    /**
     * 类型
     */
    public static final String TYPE = "type";
    @Column(name = TYPE)
    public String type;

}
