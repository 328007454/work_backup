package com.cnksi.bdzinspection.model.tree;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.bdzinspection.adapter.DeviceAdapter;

import org.xutils.db.table.DbModel;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/1/25 12:13
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class DeviceItem extends DbModel implements MultiItemEntity, LinkItem<SpaceItem> {
    SpaceItem parent = null;

    public DeviceItem(DbModel model) {
        setDataMap(model.getDataMap());
    }

    @Override
    public int getItemType() {
        return DeviceAdapter.DEVICE_ITEM;
    }

    @Override
    public SpaceItem getParent() {
        return parent;
    }

    public void setParent(SpaceItem parent) {
        this.parent = parent;
    }
}
