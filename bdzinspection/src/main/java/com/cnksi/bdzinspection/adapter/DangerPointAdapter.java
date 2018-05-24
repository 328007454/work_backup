package com.cnksi.bdzinspection.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.SimpleBaseAdapter;
import com.cnksi.bdzinspection.databinding.XsDangerPointItemBinding;
import com.cnksi.bdzinspection.model.Dangpoint;
import com.cnksi.core.utils.StringUtils;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * 危险点
 *
 * @author terry
 */
public class DangerPointAdapter extends SimpleBaseAdapter {
    public DangerPointAdapter(Context context, List<? extends Object> dataList) {
        super(context, dataList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Dangpoint mDangerPoint = (Dangpoint) getItem(position);
        ViewHolder holder;
        XsDangerPointItemBinding itemBinding;

        if (convertView == null) {
            itemBinding = XsDangerPointItemBinding.inflate(LayoutInflater.from(parent.getContext()));
            AutoUtils.autoSize(itemBinding.getRoot());
        } else {
            itemBinding = DataBindingUtil.findBinding(convertView);

        }
        itemBinding.layoutDanger.setVisibility(View.VISIBLE);
        itemBinding.tvDangerType.setVisibility(View.GONE);
        itemBinding.tvDangerContent.setText(StringUtils.changePartTextColor(mContext, "[危险点] " + mDangerPoint.dpname, R.color.xs_red_color, 0, 5));
        itemBinding.tvControlWay.setText(StringUtils.changePartTextColor(mContext, "[控制措施] " + mDangerPoint.measures, R.color.xs_red_color, 0, 6));
        //大标题的dpid才会为空。
        if (mDangerPoint.dpid == null) {
            itemBinding.tvDangerType.setVisibility(View.VISIBLE);
            itemBinding.tvDangerType.setText(mDangerPoint.avoid_type);
            itemBinding.layoutDanger.setVisibility(View.GONE);
        }
        String sort = mDangerPoint.sort == null ? "0" : mDangerPoint.sort;
        itemBinding.tvDangerPointNum.setText(String.valueOf(sort.charAt(sort.length() - 1)));
        return itemBinding.getRoot();
    }

}
