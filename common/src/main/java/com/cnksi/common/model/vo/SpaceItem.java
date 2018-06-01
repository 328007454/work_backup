package com.cnksi.common.model.vo;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.List;

import static com.cnksi.common.model.vo.SpaceGroupItem.SPACE_ITEM;

/**
 * @version 1.0
 * @author wastrel
 * @date 2018/1/25 12:11
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class SpaceItem extends AbstractExpandableItem<DeviceItem> implements MultiItemEntity, LinkItem<SpaceGroupItem> {
    public DbModel spacing;
    SpaceGroupItem parent = null;
    List<DbModel> deviceDbModels = new ArrayList<>();

    public SpaceItem(DbModel spacing) {
        DbModel dbModel = new DbModel();
        dbModel.setDataMap(spacing.getDataMap());
        this.spacing = dbModel;
    }

    @Override
    public int getLevel() {
        return 1;
    }

    @Override
    public int getItemType() {
        return SPACE_ITEM;
    }

    public void addAll(List<DbModel> models) {
        this.deviceDbModels = models;
        if (models != null) {
            for (DbModel model : models) {
                addSubItem(new DeviceItem(model));
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

    @Override
    public SpaceGroupItem getParent() {
        return parent;
    }

    public void setParent(SpaceGroupItem parent) {
        this.parent = parent;
    }

    public String getSpid() {
        return spacing.getString("spid");
    }

    public List<DbModel> getAllSubDevices(){
        if (null==deviceDbModels) {
            return new ArrayList<>();
        } else {
            return deviceDbModels;
        }
    }
}
