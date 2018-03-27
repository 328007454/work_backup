package com.cnksi.inspe.adapter.entity;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.inspe.adapter.TeamRoleAdapter;
import com.cnksi.inspe.db.entity.TeamRuleEntity;

import java.util.List;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/26 08:23
 */

public class TeamRole0Entity extends AbstractExpandableItem<TeamRoleEntity> implements MultiItemEntity {

    public TeamRuleEntity rule;

    public TeamRole0Entity(TeamRuleEntity rule) {
        this.rule = rule;
    }

    @Override
    public int getLevel() {
        return TeamRoleAdapter.TYPE_0;
    }

    @Override
    public int getItemType() {
        return TeamRoleAdapter.TYPE_0;
    }
}
