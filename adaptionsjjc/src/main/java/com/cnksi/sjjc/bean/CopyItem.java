package com.cnksi.sjjc.bean;

import com.cnksi.common.model.BaseModel;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

@Table(name = "copy_item")
public class CopyItem extends BaseModel implements Cloneable {

    // ID
    public static final String ID = "id";
    @Column(name = ID, isId = true)
    public String id;

    // 抄录类型的key
    public static final String TYPE_KEY = "type_key";
    @Column(name = TYPE_KEY)
    public String type_key;

    // 变电站ID
    public static final String BDZID = "bdzid";
    @Column(name = BDZID)
    public String bdzid;

    // 设备ID
    public static final String DEVICEID = "deviceid";
    @Column(name = DEVICEID)
    public String deviceid;

    // 设备名称
    public static final String DEVICE_NAME = "device_name";
    @Column(name = DEVICE_NAME)
    public String device_name;

    // 抄录描述
    public static final String DESCRIPTION = "description";
    @Column(name = DESCRIPTION)
    public String description;

    // 单位
    public static final String UNIT = "unit";
    @Column(name = UNIT)
    public String unit;

    // 安装位置
    public static final String INSTALL_PLACE = "install_place";
    @Column(name = INSTALL_PLACE)
    public String install_place;

    // 抄录上限
    public static final String MAX = "max";
    @Column(name = MAX)
    public String max;

    // 抄录下限
    public static final String MIN = "min";
    @Column(name = MIN)
    public String min;

    // 是否显示默认抄录：Y、N
    public static final String VAL = "val";
    @Column(name = VAL)
    public String val;

    // 是否显示A项抄录：Y、N
    public static final String VAL_A = "val_a";
    @Column(name = VAL_A)
    public String val_a;

    // 是否显示B项抄录：Y、N
    public static final String VAL_B = "val_b";
    @Column(name = VAL_B)
    public String val_b;

    // 是否显示C项抄录：Y、N
    public static final String VAL_C = "val_c";
    @Column(name = VAL_C)
    public String val_c;

    // 是否显示O项抄录：Y、N
    public static final String VAL_O = "val_o";
    @Column(name = VAL_O)
    public String val_o;

    // remark
    public static final String REMARK = "remark";
    @Column(name = REMARK)
    public String remark;

    //
    public static final String CREATE_TIME = "create_time";
    @Column(name = CREATE_TIME)
    public String create_time;

    //
    public static final String UPDATE_TIME = "update_time";
    @Column(name = UPDATE_TIME)
    public String update_time;

    @Override
    public CopyItem clone() {
        try {
            return (CopyItem) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setCopy(String val, String valA, String valB, String valC, String valO) {
        this.val = val;
        this.val_a = valA;
        this.val_b = valB;
        this.val_c = valC;
        this.val_o = valO;
    }
    public boolean focus = false;
    public void setFocus(boolean focus){
        this.focus = focus;
    }
}
