package com.cnksi.defect.defect_enum;

/**
 * Created by Mr.K on 2018/5/29.
 */

public enum DefectEnum {

    general("2"), serious("4"), critical("6"),problem("8"),hidden("10");

    public final String value;

    DefectEnum(String value) {
        this.value = value;
    }
    @Override
    public String toString() {
        return this.value;
    }
}
