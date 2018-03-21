package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.cnksi.sjjc.R;
import com.cnksi.sjjc.enmu.InspectionType;
import com.cnksi.sjjc.inter.ItemClickListener;

import java.util.Collection;

/**
 * Created by lyndon on 2016/5/16.
 */
public class TypeAdapter extends BaseAdapter<String> {

    private InspectionType inspectionType;

    private ItemClickListener<String> itemClickListener;

    public void setItemClickListener(ItemClickListener<String> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public TypeAdapter(Context context, Collection<String> data, int layoutId, InspectionType inspectionType) {
        super(context, data, layoutId);
        this.inspectionType = inspectionType;
    }

    /**
     * @param holder
     * @param item
     * @param position [0-position)可以点击
     */
    public void setClickable(ViewHolder holder, final String item, final int maxPosition, final int position) {
        if (position >= maxPosition) {
//            context.getResources().getColor(R.color.global_gray_text_color)
            holder.getRootView().setBackgroundColor(ContextCompat.getColor(context, R.color.global_gray_text_color));
        } else {
            holder.getRootView().setBackgroundResource(R.drawable.task_item_background_selector);
        }
        holder.getRootView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != itemClickListener) {
                    //通过字符串传递maxPosition
                    itemClickListener.itemClick(v, item + " " + maxPosition, position);
                }
            }
        });
    }

    @Override
    public void convert(com.cnksi.sjjc.adapter.ViewHolder holder, String item, int position) {
        ImageView typeImage = holder.getView(R.id.type_image);
        switch (inspectionType) {
            case SBXS:
                typeImage.setImageResource(R.drawable.launcher_sbxs);
                typeImage.setBackgroundResource(R.drawable.launcher_sbxs_background);
                setClickable(holder, item, Integer.MAX_VALUE, position);
                break;
            case GZP:
                typeImage.setImageResource(R.drawable.launcher_gzp);
                typeImage.setBackgroundResource(R.drawable.launcher_gzp_background);
                setClickable(holder, item, 1, position);
                break;
            case SBJC:
                typeImage.setImageResource(R.drawable.launcher_sbjc);
                typeImage.setBackgroundResource(R.drawable.launcher_check_background);
                setClickable(holder, item, Integer.MAX_VALUE, position);
                break;
            case maintenance:
                typeImage.setImageResource(R.drawable.launcher_dqwh);
                typeImage.setBackgroundResource(R.drawable.launcher_maintain_background);
                break;
            case switchover:
                typeImage.setImageResource(R.drawable.launcher_dqsy);
                typeImage.setBackgroundResource(R.drawable.launcher_switch_background);
                break;
            case exclusive:
                typeImage.setImageResource(R.drawable.launcher_zxxs);
                typeImage.setBackgroundResource(R.drawable.launcher_zxxs_background);
                setClickable(holder, item, 0, position);
                break;
            case JYHPJ:
                typeImage.setImageResource(R.drawable.launcher_jyhpj);
                typeImage.setBackgroundResource(R.drawable.launcher_jyhpj_background);
                setClickable(holder, item, 1, position);
                break;
            case JYHYS:
                typeImage.setImageResource(R.drawable.launcher_jyhys);
                typeImage.setBackgroundResource(R.drawable.launcher_jyhys_background);
                setClickable(holder, item, 1, position);
                break;
            case TJWT: //图解五通
                typeImage.setImageResource(R.drawable.launcher_tjwt);
                typeImage.setBackgroundResource(R.drawable.launcher_tjwt_background);
                setClickable(holder, item, Integer.MAX_VALUE, position);
                break;
        }
        holder.setText(R.id.type_value, item);
    }
}
