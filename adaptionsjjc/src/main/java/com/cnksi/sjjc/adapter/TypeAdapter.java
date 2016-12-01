package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.cnksi.core.adapter.BaseAdapter;
import com.cnksi.core.adapter.ViewHolder;
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

    @Override
    public void convert(ViewHolder holder, final String item, final int position) {

        ImageView typeImage = holder.getView(R.id.type_image);
        switch (inspectionType) {
            case SBXS:
                typeImage.setImageResource(R.mipmap.launcher_sbxs);
                typeImage.setBackgroundResource(R.drawable.launcher_sbxs);
                setClickable(holder, item, Integer.MAX_VALUE, position);
                break;
            case GZP:
                typeImage.setImageResource(R.mipmap.launcher_gzp);
                typeImage.setBackgroundResource(R.drawable.launcher_gzp);
                setClickable(holder, item, 1, position);
                break;
            case SBJC:
                typeImage.setImageResource(R.mipmap.launcher_sbjc);
                typeImage.setBackgroundResource(R.drawable.launcher_check);
                setClickable(holder, item, Integer.MAX_VALUE, position);
                break;
            case maintenance:
                typeImage.setImageResource(R.mipmap.launcher_dqwh);
                typeImage.setBackgroundResource(R.drawable.launcher_maintain);
                break;
            case switchover:
                typeImage.setImageResource(R.mipmap.launcher_dqsy);
                typeImage.setBackgroundResource(R.drawable.launcher_switch);
                break;
            case exclusive:
                typeImage.setImageResource(R.mipmap.launcher_zxxs);
                typeImage.setBackgroundResource(R.drawable.launcher_zxxs);
                setClickable(holder, item, 0, position);
                break;
            case JYHPJ:
                typeImage.setImageResource(R.mipmap.launcher_jyhpj);
                typeImage.setBackgroundResource(R.drawable.launcher_jyhpj);
                setClickable(holder, item, 1, position);
                break;
            case JYHYS:
                typeImage.setImageResource(R.mipmap.launcher_jyhys);
                typeImage.setBackgroundResource(R.drawable.launcher_jyhys);
                setClickable(holder, item, 1, position);
                break;
        }
        holder.setText(R.id.type_value, item);
    }

    /**
     * @param holder
     * @param item
     * @param position [0-position)可以点击
     */
    public void setClickable(ViewHolder holder, final String item, final int maxPosition, final int position) {
        if (position >= maxPosition) {
            holder.getRootView().setBackgroundColor(context.getResources().getColor(R.color.global_gray_text_color));
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
}
