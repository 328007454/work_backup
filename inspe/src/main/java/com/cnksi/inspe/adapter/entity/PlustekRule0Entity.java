package com.cnksi.inspe.adapter.entity;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.inspe.adapter.PlustekStandardAdapter;
import com.cnksi.inspe.adapter.TeamRoleAdapter;
import com.cnksi.inspe.db.entity.PlusteRuleEntity;
import com.cnksi.inspe.db.entity.TeamRuleEntity;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/26 08:23
 */

public class PlustekRule0Entity extends AbstractExpandableItem<PlustekRule1Entity> implements MultiItemEntity {

    public PlusteRuleEntity rule;
    public int index;

    public PlustekRule0Entity(PlusteRuleEntity rule, int index) {
        this.rule = rule;
        this.index = index;
    }
    public PlustekRule0Entity(PlusteRuleEntity rule) {
        this.rule = rule;
    }

    @Override
    public int getLevel() {
        return PlustekStandardAdapter.TYPE_0;
    }

    @Override
    public int getItemType() {
        return PlustekStandardAdapter.TYPE_0;
    }
}
