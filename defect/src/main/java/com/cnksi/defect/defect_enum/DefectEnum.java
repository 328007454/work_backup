package com.cnksi.defect.defect_enum;

/**
 * @decrption 缺陷枚举类
 * @author Mr.K  on 2018/5/29.
 */

public enum DefectEnum {

    general("2"), serious("4"), critical("6"),problem("8"),hidden("10"), general_text("一般缺陷"), serious_text("严重缺陷"), critical_text("危急缺陷"),problem_text("问题"),hidden_text("隐患");

    public final String value;

    DefectEnum(String value) {
        this.value = value;
    }
    @Override
    public String toString() {
        return this.value;
    }
}
