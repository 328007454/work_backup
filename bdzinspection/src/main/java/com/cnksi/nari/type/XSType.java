package com.cnksi.nari.type;

/**
 * 巡视类型
 * @version 1.0
 * @auth wastrel
 * @date 2017/7/28 15:53
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public enum XSType {
    正常巡视("01", "正常巡视", false),
    special("02", "特殊巡视", false),
    routine("03", "例行巡视", true),
    夜间巡视("04", "夜间巡视", false),
    故障巡视("05", "故障巡视", false),
    full("06", "全面巡视", true),
    professional("07", "专业巡视", true),
    special_xideng("08", "熄灯巡视", true),
    监察巡视("09", "监察巡视", false),
    unKnown("100", "未知类型", false);

    public String bm;
    public String zhName;
    public boolean isValid;

    XSType(String bm, String zhName, boolean isValid) {
        this.bm = bm;
        this.zhName = zhName;
        this.isValid = isValid;
    }

    public static XSType find(String code) {
        for (XSType xsType : XSType.values()) {
            if (xsType.bm.equals(code)) return xsType;
        }
        return unKnown;
    }


}
