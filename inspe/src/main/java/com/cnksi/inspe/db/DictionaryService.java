package com.cnksi.inspe.db;

import com.cnksi.inspe.base.BaseDbService;
import com.cnksi.inspe.db.entity.DeviceTypeEntity;
import com.cnksi.inspe.db.entity.SubStationEntity;

import org.xutils.ex.DbException;

import java.util.List;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/04/09 13:12
 */
public class DictionaryService extends BaseDbService {

    /**
     * 获取精益化评价设备类型,共计28类
     *
     * @return
     */
    public List<DeviceTypeEntity> getDeviceTypes() {
        try {
            return dbManager.selector(DeviceTypeEntity.class)
                    .where("dlt", "=", "0")
                    .and("iswt", "=", "Y")
                    .findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

}
