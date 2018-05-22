package com.cnksi.sjjc.service;

import com.cnksi.common.daoservice.BaseService;
import com.cnksi.sjjc.bean.DevicePart;
import com.cnksi.sjjc.bean.DeviceUnit;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备部件查询
 *
 * @author terry
 *
 */
public class DevicePartService extends BaseService<DeviceUnit> {

	public static DevicePartService mInstance;

	private DevicePartService() {
		super(DeviceUnit.class);
	}

	public static DevicePartService getInstance() {
		if (mInstance == null) {
			mInstance = new DevicePartService();
		}
		return mInstance;
	}



	public List<DevicePart> getDevicePartListFromDb(String dtid) {
		List<DevicePart> devicePart = new ArrayList<DevicePart>();
		try {
			List<DeviceUnit> list = selector().and(DeviceUnit.DTID, "=", dtid).and(DeviceUnit.DLT, "!=",
					"1").findAll();
            if (null != list && !list.isEmpty()) {
                for (DeviceUnit unit : list)
                    devicePart.add(new DevicePart(unit));
            }
		} catch (DbException e) {
			e.printStackTrace();
		}
		return devicePart;
	}
    /**
     * 根据设备ID查询设备部件
     *
     * @return
     */
    public List<DevicePart> getDevicePartList(String deviceid) {
        try {
            return getInstance(DevicePart.class).selector().and(DevicePart.DEVICEID, "=", deviceid)
                    .and(DevicePart.DLT, "!=", "1").findAll();
        } catch (DbException e) {
            e.printStackTrace();
            return null;
        }
    }
}
