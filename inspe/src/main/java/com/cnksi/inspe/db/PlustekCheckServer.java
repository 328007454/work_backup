package com.cnksi.inspe.db;

import com.cnksi.inspe.base.BaseDbService;
import com.cnksi.inspe.db.entity.DeviceCheckEntity;
import com.cnksi.inspe.type.PlustekType;

import org.xutils.ex.DbException;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/04/18 16:44
 */
public class PlustekCheckServer extends BaseDbService {

    /**
     * 设备详情页面使用
     * @param taskId
     * @param deviceId
     * @param plustekType
     * @return
     */
    public DeviceCheckEntity getDeviceEntity(String taskId, String deviceId, PlustekType plustekType) {
        try {
            return dbManager.selector(DeviceCheckEntity.class)
                    .where("task_id", "=", taskId)
                    .and("device_id", "=", deviceId)
                    .and("plustek_type", "=", plustekType.name())
                    .findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设备详情页面使用
     * @param entity
     * @return
     */
    public boolean updateDeviceEntity(DeviceCheckEntity entity) {
        try {
            dbManager.saveOrUpdate(entity);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
