package com.cnksi.sjjc.bean;

import android.text.TextUtils;

import java.util.List;

/**
 * Created by han on 2017/2/10.
 * 差动保护差流抄录数据结构转换
 */

public class CdbhclValue {

    private static final String[] val = {"val", "vala", "valb", "valc", "valo"};

    private String id;
    private String deviceName;
    private String name;
    private String value;

    public static void addObject(CopyItem item, List<CdbhclValue> list) {
        CdbhclValue valueObject = null;

        if (item.val.equals("Y")) {
            valueObject = changeValue("val", item);
            list.add(valueObject);
        }
        if (item.val_a.equals("Y")) {
            valueObject = changeValue("val_a", item);
            list.add(valueObject);
        }
        if (item.val_b.equals("Y")) {
            valueObject = changeValue("val_b", item);
            list.add(valueObject);
        }
        if (item.val_c.equals("Y")) {
            valueObject = changeValue("val_c", item);
            list.add(valueObject);
        }
        if (item.val_o.equals("Y")) {
            valueObject = changeValue("val_o", item);
            list.add(valueObject);
        }
    }


    private static CdbhclValue changeValue(String str, CopyItem item) {
        CdbhclValue value = new CdbhclValue();
        value.id = item.deviceid;
        value.deviceName = item.device_name + str.substring(str.length() - 1).toUpperCase();
        value.name = str;
        return value;
    }

    public CdbhclValue() {
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void reportValue(ReportCdbhcl report, CdbhclValue value) {
        for (int count = 0; count < val.length; count++) {
            if (value.name.equals("val")) {
                value.setValue(report.dclz);
            }
            if (value.name.equals("val_a")) {
                value.setValue(report.dclzA);
            }
            if (value.name.equals("val_b")) {
                value.setValue(report.dclzB);
            }
            if (value.name.equals("val_c")) {
                value.setValue(report.dclzC);
            }
            if (value.name.equals("val_o")) {
                value.setValue(report.dclzO);
            }
        }
    }

    public static void reportChangeValue(ReportCdbhcl cdbhcl, List list){
        CdbhclValue cdbhclValue;
        if(!TextUtils.isEmpty(cdbhcl.dclz)){
            cdbhclValue = new CdbhclValue();
            cdbhclValue.setName(cdbhcl.device_name);
            cdbhclValue.setValue(cdbhcl.dclz);
            list.add(cdbhclValue);
        }
        if(!TextUtils.isEmpty(cdbhcl.dclzA)){
            cdbhclValue = new CdbhclValue();
            cdbhclValue.setName(cdbhcl.device_name+"A");
            cdbhclValue.setValue(cdbhcl.dclzA);
            list.add(cdbhclValue);
        }
        if(!TextUtils.isEmpty(cdbhcl.dclzB)){
            cdbhclValue = new CdbhclValue();
            cdbhclValue.setName(cdbhcl.device_name+"B");
            cdbhclValue.setValue(cdbhcl.dclzB);
            list.add(cdbhclValue);
        }
        if(!TextUtils.isEmpty(cdbhcl.dclzC)){
            cdbhclValue = new CdbhclValue();
            cdbhclValue.setName(cdbhcl.device_name+"C");
            cdbhclValue.setValue(cdbhcl.dclzC);
            list.add(cdbhclValue);
        }
        if(!TextUtils.isEmpty(cdbhcl.dclzO)){
            cdbhclValue = new CdbhclValue();
            cdbhclValue.setName(cdbhcl.device_name+"O");
            cdbhclValue.setValue(cdbhcl.dclzO);
            list.add(cdbhclValue);
        }
    }
}
