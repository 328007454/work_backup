package com.cnksi.inspe.entity.device;

import android.text.TextUtils;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.inspe.adapter.DeviceAdapter;

import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/1/25 12:11
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class SpaceItem extends AbstractExpandableItem<DeviceItem> implements MultiItemEntity {
    public DbModel spacing;
    List<DbModel> deviceDbModels = new ArrayList<>();

    public SpaceItem(DbModel spacing) {
        this.spacing = spacing;
    }

    @Override
    public int getLevel() {
        return 1;
    }

    @Override
    public int getItemType() {
        return DeviceAdapter.SPACE_ITEM;
    }

    public void addAll(List<DbModel> models) {
        this.deviceDbModels = models;
        String spid = spacing.getString("spid");
        for (DbModel dbModel : models) {
            if (TextUtils.equals(spid, dbModel.getString("spid"))) {
                addSubItem(new DeviceItem(dbModel));
            }
        }
    }

    @Override
    public void addSubItem(DeviceItem subItem) {
        subItem.setParent(this);
        super.addSubItem(subItem);
    }

    public int getSubSize() {
        return hasSubItem() ? getSubItems().size() : 0;
    }


    public String getSpid() {
        return spacing.getString("spid");
    }

    public List<DbModel> getAllSubDevices() {
        if (null == deviceDbModels)
            return new ArrayList<DbModel>();
        else
            return deviceDbModels;
    }
}
