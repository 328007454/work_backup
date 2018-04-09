package com.cnksi.inspe.entity.device;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.inspe.adapter.DeviceAdapter;

import org.xutils.db.table.DbModel;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/1/25 12:13
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class DeviceItem implements MultiItemEntity {
    SpaceItem parent = null;
    public DbModel dbModel;

    public DeviceItem(DbModel model) {
        this.dbModel = model;
    }

    @Override
    public int getItemType() {
        return DeviceAdapter.DEVICE_ITEM;
    }


    public void setParent(SpaceItem parent) {
        this.parent = parent;
    }
}
