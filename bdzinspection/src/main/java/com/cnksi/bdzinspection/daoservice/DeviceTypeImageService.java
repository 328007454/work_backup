package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.model.DeviceTypeImage;
import com.cnksi.common.daoservice.BaseService;

import org.xutils.ex.DbException;

import java.util.List;

/**
 * @author Wastrel
 */
public class DeviceTypeImageService extends BaseService<DeviceTypeImage> {
    private static DeviceTypeImageService instance;

    protected DeviceTypeImageService() {
        super(DeviceTypeImage.class);
    }

    public static DeviceTypeImageService getInstance() {
        if (null == instance) {
            instance = new DeviceTypeImageService();
        }
        return instance;
    }

    /**
     * 查询设备类型图片
     *
     * @param deviceType
     * @return
     */
    public List<DeviceTypeImage> queryImage(String deviceType) throws DbException {
        return selector().and(DeviceTypeImage.TYPE_ID, "=", deviceType).orderBy(DeviceTypeImage.ID).findAll();
    }


}
