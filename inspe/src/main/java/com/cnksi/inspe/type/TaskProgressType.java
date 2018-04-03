package com.cnksi.inspe.type;

/**
 * 任务进度
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/29 11:07
 */

public enum TaskProgressType {

    /**未开始*/
    todo("未开始"),
    /**进行中*/
    doing("进行中"),
    /**已完成*/
    done("已完成"),;

    String desc;

    TaskProgressType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

}
