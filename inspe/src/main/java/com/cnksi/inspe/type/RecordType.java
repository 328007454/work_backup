package com.cnksi.inspe.type;

/**
 * 标准检查状态
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/21 21:56
 */

public enum RecordType {
    /**
     * 正常
     */
    normal("正常"),
    /**
     * 问题
     */
    answer("问题"),
    /**
     * 检查中(仅针对大项)
     */
    ing("检查中"),
    /**
     * 未检查
     */
    def("未检查"),
    /** 已完成 检查 */
    finish("已完成"),;

    String desc;

    RecordType(String desc) {
        this.desc = desc;
    }


    RecordType() {

    }

    public String getDesc() {
        return desc;
    }

}
