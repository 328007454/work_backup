package com.cnksi.common.enmu;

import android.text.TextUtils;

/**
 * @author Mr.K  on 2018/7/7.
 */

public enum DeviceInfo {
    XCQX("现存缺陷"), YJLL("运检履历"), CLSJ("抄录数据"), GPS("GPS坐标"), ERROR("错误");

    public final String value;

    DeviceInfo(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }


    public static String getTypeFromValue(String value) {
        try {
            if (TextUtils.equals(value, DeviceInfo.XCQX.value)) {
                return DeviceInfo.XCQX.name();
            } else if (TextUtils.equals(value, DeviceInfo.YJLL.value)) {
                return DeviceInfo.YJLL.name();
            } else if (TextUtils.equals(value, DeviceInfo.CLSJ.value)) {
                return DeviceInfo.CLSJ.name();
            } else if (TextUtils.equals(value, DeviceInfo.GPS.value)) {
                return DeviceInfo.GPS.name();
            }
        } catch (Exception ex) {
            return (DeviceInfo.ERROR.name());
        }
        return (DeviceInfo.ERROR.name());
    }

}
