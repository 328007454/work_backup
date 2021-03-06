package com.cnksi.bdzinspection.model;

import com.cnksi.common.model.BaseModel;
import com.cnksi.common.model.Device;
import com.cnksi.core.utils.DateUtils;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * @version 1.0
 * @author wastrel
 * @date 2018/1/15 14:46
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
@Table(name = "device_standards_oper")
public class DeviceStandardsOper extends BaseModel {

    public static final String ID = "id";
    @Column(name = ID,isId = true)
    public String id;
    /**
     * LIb 库里面的标准ID
     */
    public static final String STAID = "staid";
    @Column(name = STAID)
    public String staid;

    /**
     * 设备的变电站ID
     */
    public static final String BDZID = "bdzid";
    @Column(name = BDZID)
    public String bdzid;
    /**
     * 设备ID
     */
    public static final String DEVICEID = "deviceid";
    @Column(name = DEVICEID)
    public String deviceid;
    /**
     * 标注（tag）、已删除（deleted）
     */
    public static final String OPER = "oper";
    @Column(name = OPER)
    public String oper;

    /**
     * 标注时间
     */
    public static final String CREATE_TIME = "create_time";
    @Column(name = CREATE_TIME)
    public String createTime;

    /**
     * 标注人
     */
    public static final String CREATE_PERSON = "create_person";
    @Column(name = CREATE_PERSON)
    public String createPerson;

    public boolean isMarkStandard() {
        return "tag".equals(oper);
    }

    public boolean isDeleted() {
        return "deleted".equals(oper);
    }


    public static DeviceStandardsOper createMark(Device device, String libStaid, String createPerson) {
        DeviceStandardsOper oper = new DeviceStandardsOper();
        oper.id = BaseModel.getPrimarykey();
        oper.deviceid = device.deviceid;
        oper.bdzid = device.bdzid;
        oper.staid = libStaid;
        oper.oper = "tag";
        oper.createTime = DateUtils.getCurrentLongTime();
        oper.createPerson = createPerson;
        return oper;
    }
}
