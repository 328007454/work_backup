package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.model.PlacedDevice;
import com.cnksi.common.daoservice.BaseService;
import com.cnksi.core.utils.DateUtils;

import org.xutils.db.Selector;
import org.xutils.ex.DbException;

import java.util.List;

/**
 * @author Wastrel
 * @version 1.0
 * @date 2018/5/23 14:07
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class PlacedDeviceService extends BaseService<PlacedDevice> {
    final static PlacedDeviceService instance = new PlacedDeviceService();

    protected PlacedDeviceService() {
        super(PlacedDevice.class);
    }

    public static PlacedDeviceService getInstance() {
        return instance;
    }

    /**
     * 查詢已到間隔
     *
     * @param reportId
     * @return
     */
    public List<PlacedDevice> findPlacedDevice(String reportId) {
        try {
            Selector selector = selector().and(PlacedDevice.REPORTID, "=", reportId);
            return selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean saveOrUpdate(PlacedDevice mObject) {
        mObject.update_time = DateUtils.getCurrentLongTime();
        try {
            super.saveOrUpdate(mObject);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public PlacedDevice findDevicePlaced(String reportId, String deviceId) {
        try {

            Selector selector = selector().and(PlacedDevice.REPORTID, "=", reportId)
                    .and(PlacedDevice.DEVICEID, "=", deviceId);

            return (PlacedDevice) selector.findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }
}
