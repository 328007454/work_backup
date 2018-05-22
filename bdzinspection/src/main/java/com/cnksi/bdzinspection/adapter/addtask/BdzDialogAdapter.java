package com.cnksi.bdzinspection.adapter.addtask;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.SimpleBaseAdapter;
import com.cnksi.bdzinspection.databinding.XsDialogContentChildItemBinding;
import com.cnksi.common.model.Bdz;
import com.zhy.core.utils.AutoUtils;

import java.util.List;

/**
 * 追加巡检任务  --> 选择变电站 Dialog ListAdapter
 *
 * @author Joe
 */
public class BdzDialogAdapter extends SimpleBaseAdapter {

    public BdzDialogAdapter(Context context, List<? extends Object> dataList) {
        super(context, dataList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Bdz mBdz = (Bdz) getItem(position);
        XsDialogContentChildItemBinding itemBinding = null;
        if (convertView == null) {
            itemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.xs_dialog_content_child_item, parent, false);
            AutoUtils.autoSize(itemBinding.getRoot());
        } else {
            itemBinding = DataBindingUtil.findBinding(convertView);
        }
        itemBinding.tvChildItem.setTextColor(mContext.getResources().getColor(R.color.xs_green_color));
        itemBinding.tvChildItem.setText(mBdz.name);
        return  itemBinding.getRoot();
    }

}
