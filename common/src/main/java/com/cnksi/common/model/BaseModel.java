package com.cnksi.common.model;

/**
 * @version 1.0
 * @author wastrel
 * @date 2017/3/10 10:27
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */

import com.cnksi.core.utils.Cst;
import com.cnksi.core.utils.DateUtils;

import org.xutils.db.annotation.Column;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * @author Wastrel
 */
public class BaseModel {
    //用于支援同步的字段。如果实体类本身有 则不会覆盖。

    public static final String DLT = "dlt";
    @Column(name = DLT)
    public String dlt = "0";

    /**
     * 得到以时间戳的字符串
     *
     * @return String HHmmssSSS+UUID
     */
    public static String getPrimarykey() {
        SimpleDateFormat formatter = new SimpleDateFormat(DateUtils.yyyyMMddHHmmssSSS, Locale.CHINA);
        return formatter.format(new Date()) + UUID.randomUUID().toString().replace(Cst.DASH_SEPARATOR, "");
    }

}
