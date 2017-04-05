package com.cnksi.sjjc.bean;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2017/3/10 10:27
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */

import org.xutils.db.annotation.Column;

public class BaseModel {
    //用于支援同步的字段。如果实体类本身有 则不会覆盖。
    public static final String DLT = "dlt";
    @Column(name = DLT)
    public String dlt="0";
}
