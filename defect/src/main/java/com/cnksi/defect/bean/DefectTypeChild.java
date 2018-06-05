package com.cnksi.defect.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.defect.adapter.DefectOriginAdapter;

import org.xutils.db.table.DbModel;

/**
 * Created by Mr.K on 2018/6/4.
 */

public class DefectTypeChild implements MultiItemEntity {
    public  String defectContent;
    public DbModel model;

    public DefectTypeChild(DbModel model) {
        defectContent = model.getString("description");
        this.model = model;
    }

    @Override
    public int getItemType() {
        return DefectOriginAdapter.CHILD_ITEM;
    }
}
