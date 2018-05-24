package com.cnksi.common.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by han on 2016/6/17.
 * 蓄电池
 */
@Table(name = "battery")
public class Battery extends BaseModel implements Serializable {

    // 蓄电池id
    public static final String BATTERYID = "battery";
    @Column(name = BATTERYID,isId = true)
    public String battery;
    //电池组编号
    public static final String BID = "bid";
    @Column(name = BID)
    public String bid;
    //变电站编号
    public static final String BDZID = "bdzid";
    @Column(name = BDZID)
    public String bdzid;

    //模型
    public static final String MODEL = "model";
    @Column(name = MODEL)
    public String  model;

    //工厂
    public static final String MANUFACTURER = "manufacturer";
    @Column(name = MANUFACTURER)
    public String manufacturer;

    //生产日期
    public static final String MANUDATE = "manudate";
    @Column(name = MANUDATE)
    public String manudate;

    //环境温度
    public static final String ENVIRTEMP = "envirtemp";
    @Column(name = ENVIRTEMP)
    public String envirtemp;

    //室内温度
    public static final String INDOORTEMP = "indoortemp";
    @Column(name = INDOORTEMP)
    public String indoortemp;

    //端组电压
    public static final String VOLTAGE = "voltage";
    @Column(name = VOLTAGE)
    public String voltage;

    //充电电流
    public static final String DL = "dl";
    @Column(name = DL)
    public String dl;

    //电池数量
    public static final String AMOUNT = "amount";
    @Column(name = AMOUNT)
    public int amount;

    //电池名字
    public static final String NAME = "name";
    @Column(name = NAME)
    public String name;

    //单只标压
    public static final String SINGLE_VOLTAGE = "dzbcdy";
    @Column(name = SINGLE_VOLTAGE)
    public String singleVoltage;

    //
    public static final String HASESS = "hasess";
    @Column(name = HASESS)
    public int hasess;

    // 控制母线电压
    public static final String KVOLTAGE = "kvoltage";
    @Column(name = KVOLTAGE)
    public String kvoltage;

    // 数量
    public static final String NUMBER = "number";
    @Column(name = NUMBER)
    public String number;

}
