package com.cnksi.bdzinspection.model;

import com.lidroid.xutils.db.annotation.Id;

import java.io.Serializable;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2017/3/9 18:12
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */

public class BaseModel implements Serializable {
    //用于 同步的字段  仅用来支持Xutils自动插入。
    public static final String DLT = "dlt";
    @Id(column = DLT)
    public String dlt = "0";

}
