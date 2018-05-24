package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.model.DeviceStandardsOper;
import com.cnksi.common.daoservice.BaseService;

import org.xutils.db.Selector;
import org.xutils.ex.DbException;

import java.util.HashMap;
import java.util.List;

/**
 * @author Wastrel
 * @version 1.0
 * @date 2018/5/23 16:44
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class DeviceStandardOperService extends BaseService<DeviceStandardsOper> {
    protected DeviceStandardOperService() {
        super(DeviceStandardsOper.class);
    }
    final static DeviceStandardOperService instance=new DeviceStandardOperService();

    public static DeviceStandardOperService getInstance() {
        return instance;
    }

    public HashMap<String, DeviceStandardsOper> findStandardMark(String bdzId, String deviceId) {
        HashMap<String, DeviceStandardsOper> staidMap = new HashMap<>();
        try {
            Selector selector = selector()
                    .and(DeviceStandardsOper.BDZID, "=", bdzId).and(DeviceStandardsOper.DEVICEID, "=", deviceId);
            List<DeviceStandardsOper> list = selector.findAll();
            if (list != null) {
                for (DeviceStandardsOper oper : list) {
                    staidMap.put(oper.staid, oper);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return staidMap;
    }
}
