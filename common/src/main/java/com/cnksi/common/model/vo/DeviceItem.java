package com.cnksi.common.model.vo;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import org.xutils.db.table.DbModel;

import java.io.Serializable;

import static com.cnksi.common.model.vo.SpaceGroupItem.DEVICE_ITEM;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/1/25 12:13
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class DeviceItem extends DbModel implements MultiItemEntity, LinkItem<SpaceItem>, Serializable {
    transient SpaceItem parent = null;

    public DeviceItem(DbModel model) {
        setDataMap(model.getDataMap());
    }

    @Override
    public int getItemType() {
        return DEVICE_ITEM;
    }

    @Override
    public SpaceItem getParent() {
        return parent;
    }

    public void setParent(SpaceItem parent) {
        this.parent = parent;
    }
}
