package com.cnksi.inspe.type;

/**
 * 任务类型
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/28 09:43
 */

public enum TaskType {
    /***/
    bzjs("班组建设检查"),
    /***/
    @Deprecated
    sbpc("设备排查"),
    /***/
    @Deprecated
    sbjc("设备检查"),
    /***/
    jyhjc("精益化评价");

    String desc;

    private TaskType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

}
