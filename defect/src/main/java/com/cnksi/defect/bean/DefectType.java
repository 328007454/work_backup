package com.cnksi.defect.bean;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.defect.adapter.DefectOriginAdapter;

import org.xutils.db.table.DbModel;

/**
 * @author Mr.K  on 2018/6/4.
 * @decrption 缺陷性质
 */

public class DefectType extends AbstractExpandableItem<DefectTypeChild> implements MultiItemEntity {
    /**
     * 缺陷性质
     */
    public String txtType;
    public DefectType(DbModel model) {
        this.txtType = model.getString("level");
    }

    @Override
    public int getItemType() {
        return DefectOriginAdapter.PARENT_ITEM;
    }

    @Override
    public int getLevel() {
        return 0;
    }
}
