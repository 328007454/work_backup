package com.cnksi.common.enmu;

/**
 * @author Wastrel
 */

public enum PMSDeviceType{
    one("一次设备"), second("二次设备"), additional("辅助设施"),civil_works("土建设施");
    private final String value;

    PMSDeviceType(String value) {
        this.value = value;
    }

    public boolean equals(String s){
         return name().equals(s);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
