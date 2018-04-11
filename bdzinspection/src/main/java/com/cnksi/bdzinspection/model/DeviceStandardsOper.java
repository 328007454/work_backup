package com.cnksi.bdzinspection.model;

import com.cnksi.bdzinspection.utils.FunctionUtil;
import com.cnksi.xscore.xsutils.DateUtils;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/1/15 14:46
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
@Table(name = "device_standards_oper")
public class DeviceStandardsOper extends BaseModel {

    public static final String ID = "id";
    @Id(column = ID)
    public String id;
    /**
     * LIb 库里面的标准ID
     */
    public static final String STAID = "staid";
    @Column(column = STAID)
    public String staid;

    /**
     * 设备的变电站ID
     */
    public static final String BDZID = "bdzid";
    @Column(column = BDZID)
    public String bdzid;
    /**
     * 设备ID
     */
    public static final String DEVICEID = "deviceid";
    @Column(column = DEVICEID)
    public String deviceid;
    /**
     * 标注（tag）、已删除（deleted）
     */
    public static final String OPER = "oper";
    @Column(column = OPER)
    public String oper;

    /**
     * 标注时间
     */
    public static final String CREATE_TIME = "create_time";
    @Column(column = CREATE_TIME)
    public String createTime;

    /**
     * 标注人
     */
    public static final String CREATE_PERSON = "create_person";
    @Column(column = CREATE_PERSON)
    public String createPerson;

    public boolean isMarkStandard() {
        return "tag".equals(oper);
    }

    public boolean isDeleted() {
        return "deleted".equals(oper);
    }


    public static DeviceStandardsOper createMark(Device device, String libStaid, String createPerson) {
        DeviceStandardsOper oper = new DeviceStandardsOper();
        oper.id = FunctionUtil.getPrimarykey();
        oper.deviceid = device.deviceid;
        oper.bdzid = device.bdzid;
        oper.staid = libStaid;
        oper.oper = "tag";
        oper.createTime = DateUtils.getCurrentLongTime();
        oper.createPerson = createPerson;
        return oper;
    }
}
