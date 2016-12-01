package com.cnksi.sjjc.enmu;

/**
 * Created by wastrel on 2016/6/13.
 */
/**
 * 电压等级
 */
public enum LookUpType {
    inspection("巡检类型"), voltage("电压等级"), defect_reason("缺陷原因"), proType("运维一体化"), pmsDeviceType("设备型号");
    private final String value;

    LookUpType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}