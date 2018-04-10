package com.cnksi.inspe.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.inspe.R;
import com.cnksi.inspe.adapter.entity.PlustekRule0Entity;
import com.cnksi.inspe.adapter.entity.PlustekRule1Entity;
import com.cnksi.inspe.adapter.entity.TeamRole0Entity;
import com.cnksi.inspe.adapter.entity.TeamRoleEntity;
import com.cnksi.inspe.entity.device.SpaceItem;
import com.cnksi.inspe.type.RecordType;

import java.util.List;

/**
 * 精益化评价标准适配器
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/04/09 20:38
 */

public class PlustekStandardAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    public static final int TYPE_0 = 0;
    public static final int TYPE_1 = 1;


    public PlustekStandardAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(TYPE_0, R.layout.inspeplustek_standard_item0);
        addItemType(TYPE_1, R.layout.inspeplustek_standard_item1);
    }

    @Override
    protected void convert(BaseViewHolder holder, MultiItemEntity item) {
        switch (holder.getItemViewType()) {
            case TYPE_0:
                PlustekRule0Entity rule0 = (PlustekRule0Entity) item;
                holder.setText(R.id.titleTxt, (rule0.index + 1) + "、" + rule0.rule.getName());
                holder.setImageResource(R.id.imageView, rule0.isExpanded() ? R.mipmap.inspe_arrow_down : R.mipmap.inspe_arrow_up);

                holder.itemView.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int pos = holder.getAdapterPosition();
                        if (rule0.isExpanded()) { //当前是展开
                            collapse(pos, true);//关闭
                        } else {
                            expand(pos, true);//展开
                        }
                    }
                });

                break;
            case TYPE_1:
                PlustekRule1Entity rule1 = (PlustekRule1Entity) item;
//                RecordType recordType = RecordType.valueOf(rule.rule.getRecord_type() != null ? rule.rule.getRecord_type() : RecordType.def.name());
//                if (recordType != null) {
//                    switch (recordType) {
//                        case answer://有问题
//                            holder.setBackgroundRes(R.id.itemView, R.drawable.inspe_itembg_orange_shape);
//                            break;
//                        case normal://无问题
//                            holder.setBackgroundRes(R.id.itemView, R.drawable.inspe_itembg_green_shape);
//                            break;
//                        case ing://进行中
//                        case def://默认(未开始)
//                        default:
//                            holder.setBackgroundRes(R.id.itemView, R.drawable.inspe_itembg_dark_shape);
//                            break;
//                    }
//                } else {//未开始
//                    holder.setBackgroundRes(R.id.itemView, R.drawable.inspe_itembg_dark_shape);
//                }

                holder.setText(R.id.titleTxt, ((char) (rule1.index + 97)) + "、" + rule1.rule.getName());
//                holder.setText(R.id.scoreTxt, "(" + String.format("%.1f", (rule.rule.getScoreCount() - rule.rule.getDeduct_score())) + "/" + String.format("%.1f", rule.rule.getScoreCount()) + ")");
                break;
        }
    }

    private MultiItemEntity lastExpandIndex = null;

    @Override
    public int expand(int position, boolean animate) {
        MultiItemEntity entity = getData().get(position);
        int index = super.expand(position);
        if (entity != lastExpandIndex) {
            if (lastExpandIndex == null) {
//                lastExpandIndex = getData().get(0);
            }
            if (lastExpandIndex != null && ((PlustekRule0Entity) lastExpandIndex).isExpanded()) {
                int p = getData().indexOf(lastExpandIndex);
                if (p >= 0) {
                    collapse(p);
                }
            }
        }
        lastExpandIndex = entity;

        return index;
    }

    @Override
    public int collapse(int position, boolean animate) {
        if (getData().get(position) == lastExpandIndex) lastExpandIndex = null;
        return super.collapse(position, animate);
    }

}
