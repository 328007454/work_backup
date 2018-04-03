package com.cnksi.inspe.type;

/**
 * 进度
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/29 11:07
 */

public enum ProgressType {
    /**未分配*/
    wfp("未分配"),
    /**未整改*/
    wzg("未整改"),
    /**
     * 主任/专责未审核
     */
    zzwsh("未审核"),
    /**
     * 班组长未审核
     */
    bzzwsh("未审核"),
    /**审核未通过*/
    shwtg("审核未通过"),
    /**已闭环*/
    ybh("已闭环");

    String desc;

    ProgressType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

}
