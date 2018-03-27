package com.cnksi.inspe.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.inspe.R;
import com.cnksi.inspe.adapter.entity.TeamRole0Entity;
import com.cnksi.inspe.adapter.entity.TeamRoleEntity;

import java.util.List;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/26 08:24
 */

public class TeamRoleAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    public static final int TYPE_0 = 0;
    public static final int TYPE_1 = 1;


    public TeamRoleAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(TYPE_0, R.layout.inspeteam_item0);
        addItemType(TYPE_1, R.layout.inspeteam_item1);
    }

    @Override
    protected void convert(BaseViewHolder holder, MultiItemEntity item) {
        switch (holder.getItemViewType()) {
            case TYPE_0:
                TeamRole0Entity rule0 = (TeamRole0Entity) item;
                holder.setText(R.id.titleTxt, rule0.rule.getName());
                holder.setImageResource(R.id.imageView, rule0.isExpanded() ? R.mipmap.inspe_arrow_down : R.mipmap.inspe_arrow_up);
                holder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int pos = holder.getAdapterPosition();
                        if (rule0.isExpanded()) {
                            collapse(pos);
                            holder.setImageResource(R.id.imageView, R.mipmap.inspe_arrow_down);
                        } else {
                            expand(pos);
                            holder.setImageResource(R.id.imageView, R.mipmap.inspe_arrow_up);
                        }
                    }
                });


                break;
            case TYPE_1:
                TeamRoleEntity rule = (TeamRoleEntity) item;
                holder.setText(R.id.nameTxt, rule.rule.getName());
                holder.setText(R.id.scoreTxt, "(" + rule.rule.getScoreCount() + "/" + rule.rule.getScoreCount() + ")");
                break;
        }
    }
}
