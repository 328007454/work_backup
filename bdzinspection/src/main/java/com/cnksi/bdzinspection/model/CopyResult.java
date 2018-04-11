package com.cnksi.bdzinspection.model;

import android.text.TextUtils;

import com.cnksi.bdzinspection.application.CustomApplication;
import com.cnksi.bdzinspection.utils.CalcUtils;
import com.cnksi.xscore.xsutils.DateUtils;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;

import java.util.UUID;

@Table(name = "copy_result")
public class CopyResult extends BaseModel {

    // ID
    public static final String ID = "id";
    // 报告ID
    public static final String REPORTID = "reportid";
    // 抄录项目的ID
    public static final String ITEM_ID = "item_id";
    // 变电站ID
    public static final String BDZID = "bdzid";
    // 设备ID
    public static final String DEVICEID = "deviceid";
    // 设备名称
    public static final String DEVICE_NAME = "device_name";
    // 抄录类型的key
    public static final String TYPE_KEY = "type_key";
    // 抄录描述
    public static final String DESCRIPTION = "description";
    // 单位
    public static final String UNIT = "unit";
    // 安装位置
    public static final String INSTALL_PLACE = "install_place";
    // 默认抄录值
    public static final String VAL = "val";
    // 旧默认抄录值
    public static final String VAL_OLD = "val_old";
    // A项抄录值
    public static final String VAL_A = "val_a";
    // 旧A项抄录值
    public static final String VAL_A_OLD = "val_a_old";
    // B项抄录值
    public static final String VAL_B = "val_b";
    // 旧B项抄录值
    public static final String VAL_B_OLD = "val_b_old";
    // C项抄录值
    public static final String VAL_C = "val_c";
    // 旧C项抄录值
    public static final String VAL_C_OLD = "val_c_old";
    // O项抄录值
    public static final String VAL_O = "val_o";
    // 旧O项抄录值
    public static final String VAL_O_OLD = "val_o_old";
    // 本次动作次数
    public static final String DZCS = "dzcs";
    // 上次动作次数
    public static final String DZCS_OLD = "dzcs_old";
    // 累计动作次数
    public static final String DZCS_TOTAL = "dzcs_total";
    // remark
    public static final String REMARK = "remark";
    //
    public static final String CREATE_TIME = "create_time";
    //
    public static final String UPDATE_TIME = "update_time";
    public static final String VAL_SPECIAL = "val_special";
    @Id(column = ID)
    public String id = UUID.randomUUID().toString();
    @Column(column = REPORTID)
    public String reportid;
    @Column(column = ITEM_ID)
    public String item_id;
    @Column(column = BDZID)
    public String bdzid;
    @Column(column = DEVICEID)
    public String deviceid;
    @Column(column = DEVICE_NAME)
    public String device_name;
    @Column(column = TYPE_KEY)
    public String type_key;
    @Column(column = DESCRIPTION)
    public String description;
    @Column(column = UNIT)
    public String unit;
    @Column(column = INSTALL_PLACE)
    public String install_place;
    @Column(column = VAL)
    public String val;
    @Column(column = VAL_OLD)
    public String val_old;
    @Column(column = VAL_A)
    public String val_a;
    @Column(column = VAL_A_OLD)
    public String val_a_old;
    @Column(column = VAL_B)
    public String val_b;
    @Column(column = VAL_B_OLD)
    public String val_b_old;
    @Column(column = VAL_C)
    public String val_c;
    @Column(column = VAL_C_OLD)
    public String val_c_old;
    @Column(column = VAL_O)
    public String val_o;
    @Column(column = VAL_O_OLD)
    public String val_o_old;
    @Column(column = DZCS)
    public String dzcs;
    @Column(column = DZCS_OLD)
    public String dzcs_old;
    @Column(column = DZCS_TOTAL)
    public String dzcs_total;
    @Column(column = REMARK)
    public String remark;
    @Column(column = CREATE_TIME)
    public String create_time;
    @Column(column = UPDATE_TIME)
    public String update_time;
    @Column(column = VAL_SPECIAL)
    public String valSpecial;

    public CopyResult() {
    }

    public CopyResult(String reportid, String item_id, String bdzid, String deviceid, String device_name,
                      String type_key, String description, String unit, String install_place) {
        this.reportid = reportid;
        this.item_id = item_id;
        this.bdzid = bdzid;
        this.deviceid = deviceid;
        this.device_name = device_name;
        this.type_key = type_key;
        this.description = description;
        this.unit = unit;
        this.install_place = install_place;
        this.create_time = DateUtils.getCurrentLongTime();
    }

    public CopyResult(String reportid, String item_id, String bdzid, String device_name, String deviceid, String type_key, String description, String val, String valSpecial, String unit) {
        this.reportid = reportid;
        this.item_id = item_id;
        this.bdzid = bdzid;
        this.description = description;
        this.device_name = device_name;
        this.deviceid = deviceid;
        this.type_key = type_key;
        this.remark = valSpecial;
        this.valSpecial = val;
        this.unit = unit;
        this.create_time = DateUtils.getCurrentLongTime();
    }

    public void initArresterActionValue() {
        if ("blqdzcs_dzcs".contains(type_key)) {
            this.dzcs = CalcUtils.sub(val, val_old);
            SqlInfo sql = new SqlInfo(
                    "SELECT dzcs FROM copy_result where item_id=? and type_key=? and dlt<>1 AND  deviceid=? order by update_time DESC ",
                    item_id, type_key, deviceid);
            try {
                DbModel data = CustomApplication.getDbUtils().findDbModelFirst(sql);
                this.dzcs_old = data != null ? data.getString(DZCS) : null;
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isHasCopyData() {
        boolean hasData = false;
        if (!TextUtils.isEmpty(val) || !TextUtils.isEmpty(val_a)) {
            hasData = true;
        } else if (!TextUtils.isEmpty(val_b) || !TextUtils.isEmpty(val_c) || !TextUtils.isEmpty(val_o)) {
            hasData = true;
        } else if (!TextUtils.isEmpty(valSpecial)) {
            hasData = true;
        }
        return hasData;
    }
}
