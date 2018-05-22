package com.cnksi.bdzinspection.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.SimpleBaseAdapter;
import com.cnksi.bdzinspection.databinding.XsXunshiSpaceAdapterItemBinding;
import com.cnksi.common.model.Spacing;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;

public class RoadMapAdapter extends SimpleBaseAdapter {
    private boolean isReport = false;
    private ArrayList<String> placedSpacing;

    public RoadMapAdapter(Context context, List<? extends Object> dataList) {
        super(context, dataList);
    }

    public void setIsReport(boolean isreport) {
        this.isReport = isreport;
        notifyDataSetChanged();
    }

    public void setSpacingRecord(ArrayList<String> placedSpacing) {
        this.placedSpacing = placedSpacing;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Spacing space = (Spacing) getItem(position);
        XsXunshiSpaceAdapterItemBinding itemBinding;
        if (convertView == null) {
            itemBinding = XsXunshiSpaceAdapterItemBinding.inflate(LayoutInflater.from(parent.getContext()));
            AutoUtils.autoSize(itemBinding.getRoot());
        } else {
            itemBinding = DataBindingUtil.findBinding(convertView);
        }
        itemBinding.tvSpaceNum.setBackgroundResource(R.drawable.xs_xunshi_spacing_record_background);
        if (isReport) {
            itemBinding.tvSpaceName.setText(space.name);
            if ((null != placedSpacing && placedSpacing.contains(space.spid))) {
                itemBinding.tvSpaceNum.setBackgroundResource(R.drawable.xs_xunshi_spacing_green);
            }
            itemBinding.tvSpaceNum.getLayoutParams().width = AutoUtils.getPercentHeightSizeBigger(33);
            itemBinding.tvSpaceNum.getLayoutParams().height = AutoUtils.getPercentHeightSizeBigger(33);

        } else {
            itemBinding.tvSpaceNum.getLayoutParams().width = AutoUtils.getPercentHeightSizeBigger(66);
            itemBinding.tvSpaceNum.getLayoutParams().height = AutoUtils.getPercentHeightSizeBigger(66);
            itemBinding.tvSpaceNum.setBackgroundResource(R.drawable.xs_xunshi_space_num);
            itemBinding.tvSpaceNum.setText((position + 1) + "");
            itemBinding.tvSpaceName.setText(space.name);
        }
        return itemBinding.getRoot();
    }

}
