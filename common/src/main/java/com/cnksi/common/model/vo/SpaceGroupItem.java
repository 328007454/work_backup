package com.cnksi.common.model.vo;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.common.model.SpacingGroup;

import java.util.List;

/**
 * @version 1.0
 * @author wastrel
 * @date 2018/1/30 9:51
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class SpaceGroupItem extends AbstractExpandableItem<SpaceItem> implements MultiItemEntity {

    public final static int SPACE_GROUP_ITEM = 0;
    public final static int SPACE_ITEM = 1;
    public final static int DEVICE_ITEM = 2;
    public SpacingGroup group;

    public SpaceGroupItem(SpacingGroup group) {
        this.group = group;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    public void addAll(List<SpaceItem> spaceItems) {
        for (SpaceItem item : spaceItems) {
            addSubItem(item);
        }
    }

    @Override
    public void addSubItem(SpaceItem subItem) {
        subItem.setParent(this);
        super.addSubItem(subItem);
    }

    @Override
    public int getItemType() {
        return 0;
    }

    public int getSubSize() {
        return getSubItems() == null ? 0 : getSubItems().size();
    }

    public int findChildPosition(String spid) {
        List<SpaceItem> spaceItems = getSubItems();
        if (spaceItems == null) {
            return -1;
        }
        for (int i = 0; i < spaceItems.size(); i++) {
            if (spid.equals(spaceItems.get(i).getSpid())) {
                return i;
            }
        }
        return -1;
    }

    public void clear() {
        if (getSubItems() == null) {

        } else {
            getSubItems().clear();
        }
    }
}
