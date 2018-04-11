package com.cnksi.bdzinspection.model;

import com.cnksi.xscore.xsutils.DateUtils;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

import java.util.UUID;

@Table(name = "copy_item")
public class CopyItem extends BaseModel implements Cloneable {

    // ID
    public static final String ID = "id";
    @Id(column = ID)
    public String id;

    // 抄录类型的key
    public static final String TYPE_KEY = "type_key";
    @Column(column = TYPE_KEY)
    public String type_key;

    // 变电站ID
    public static final String BDZID = "bdzid";
    @Column(column = BDZID)
    public String bdzid;

    // 间隔ID
    public static final String SPID = "spid";
    @Column(column = SPID)
    public String spid;

    // 设备ID
    public static final String DEVICEID = "deviceid";
    @Column(column = DEVICEID)
    public String deviceid;

    // 设备名称
    public static final String DEVICE_NAME = "device_name";
    @Column(column = DEVICE_NAME)
    public String device_name;

    // 抄录描述
    public static final String DESCRIPTION = "description";
    @Column(column = DESCRIPTION)
    public String description;

    // 单位
    public static final String UNIT = "unit";
    @Column(column = UNIT)
    public String unit;

    // 安装位置
    public static final String INSTALL_PLACE = "install_place";
    @Column(column = INSTALL_PLACE)
    public String install_place;

    // 抄录上限
    public static final String MAX = "max";
    @Column(column = MAX)
    public String max;

    // 抄录下限
    public static final String MIN = "min";
    @Column(column = MIN)
    public String min;

    // 是否显示默认抄录：Y、N
    public static final String VAL = "val";
    @Column(column = VAL)
    public String val;

    // 是否显示A项抄录：Y、N
    public static final String VAL_A = "val_a";
    @Column(column = VAL_A)
    public String val_a;

    // 是否显示B项抄录：Y、N
    public static final String VAL_B = "val_b";
    @Column(column = VAL_B)
    public String val_b;

    // 是否显示C项抄录：Y、N
    public static final String VAL_C = "val_c";
    @Column(column = VAL_C)
    public String val_c;

    // 是否显示O项抄录：Y、N
    public static final String VAL_O = "val_o";
    @Column(column = VAL_O)
    public String val_o;

    // remark
    public static final String REMARK = "remark";
    @Column(column = REMARK)
    public String remark;

    //
    public static final String CREATE_TIME = "create_time";
    @Column(column = CREATE_TIME)
    public String create_time;

    //
    public static final String UPDATE_TIME = "update_time";
    @Column(column = UPDATE_TIME)
    public String update_time;

    //
    public static final String KIND = "kind";
    @Column(column = KIND)
    public String kind;

    public static final String DLT = "dlt";
    @Column(column = DLT)
    public int dlt = 0;
    public static final String VERSION = "version";
    @Column(column = VERSION)
    public int version;

    public static final String IS_UPLOAD = "is_upload";
    @Column(column = IS_UPLOAD)
    public String isUpLoad;


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

    public CopyItem() {
    }


    public CopyItem(String bdzid, String device_name, String deviceid, String description, String key, boolean isSingle, String kind) {
        this.id = UUID.randomUUID().toString();
        this.bdzid = bdzid;
        this.device_name = device_name;
        this.deviceid = deviceid;
        this.description = description;
        this.kind = kind;
        this.create_time = DateUtils.getCurrentLongTime();
        this.update_time = DateUtils.getCurrentLongTime();
        if (isSingle) {
            this.val = "Y";
            this.val_a = "N";
            this.val_b = "N";
            this.val_c = "N";
        } else {
            this.val_a = "Y";
            this.val_b = "Y";
            this.val_c = "Y";
            this.val = "N";
        }
        this.type_key = key;
        this.val_o = "N";
        this.isUpLoad = "N";
        this.version = -1;
        this.remark="new";
    }


}
