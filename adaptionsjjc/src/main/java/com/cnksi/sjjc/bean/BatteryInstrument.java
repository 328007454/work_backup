package com.cnksi.sjjc.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.UUID;

/**
 * @author kkk on 2017/12/4.
 */
@Table(name = "test_instrument")
public class BatteryInstrument extends BaseModel {


    public static final String ID = "id";
    @Column(name = ID)
    public String id;

    /**
     * 仪器编号
     */
    public static final String NUM = "num";
    @Column(name = NUM)
    public String num;

    /**
     * 测试仪器
     */

    public static final String CSYQMC = "csyqmc";
    @Column(name = CSYQMC)
    public String testName;

    /**
     * 选择次数
     */

    public static final String SELECT_NUM = "select_num";
    @Column(name = SELECT_NUM)
    public int selectNum;
}
