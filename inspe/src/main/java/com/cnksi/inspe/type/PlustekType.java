package com.cnksi.inspe.type;

/**
 * 精益化评价类型
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/04/09 09:05
 */
public enum PlustekType {
    /**
     * 查阅资料
     */
    cyzl("查阅资料"),
    /**
     * PMS检查
     */
    pmsjc("PMS检查"),
    /**
     * 现场检查
     */
    xcjc("现场检查");

    String desc;

    PlustekType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
