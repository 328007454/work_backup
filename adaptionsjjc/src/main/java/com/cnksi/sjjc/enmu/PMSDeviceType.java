package com.cnksi.sjjc.enmu;

public enum PMSDeviceType{
    one("一次设备"), second("二次设备"), auto("自动化屏"),secondP("二次屏");
    private final String value;

    PMSDeviceType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
