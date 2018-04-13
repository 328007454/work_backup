package com.cnksi.bdzinspection.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.SimpleBaseAdapter;
import com.cnksi.bdzinspection.databinding.XsInspectionTypeChildItemBinding;
import com.cnksi.bdzinspection.model.Lookup;
import com.zhy.core.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;

public class XunJianTypeAdapter extends SimpleBaseAdapter {
    private String currentInspetionType;

    public XunJianTypeAdapter(Context context, List<? extends Object> dataList) {
        super(context, dataList);
    }

    public void setType(String currentInspectionType) {
        this.currentInspetionType = currentInspectionType;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Lookup mLookup = (Lookup) getItem(position);
        XsInspectionTypeChildItemBinding itemBinding ;
        if (convertView == null) {
            itemBinding = XsInspectionTypeChildItemBinding.inflate(LayoutInflater.from(mContext));
            AutoUtils.autoSize(itemBinding.getRoot());
        } else {
            itemBinding = DataBindingUtil.findBinding(convertView);
        }
        itemBinding.layoutContainer.setMinimumHeight(AutoUtils.getPercentHeightSizeBigger(159));
        itemBinding.tvChildContent.setVisibility(View.VISIBLE);
        itemBinding.tvChildItem.setText(mLookup.v == null ? "" : mLookup.v);
        itemBinding.tvChildItem.setCompoundDrawables(null, null, null, null);
        if (TextUtils.isEmpty(mLookup.remark)) {
            itemBinding.tvChildContent.setVisibility(View.GONE);
        }
        itemBinding.tvChildContent.setText(mLookup.remark == null ? "" : mLookup.remark);
        if (currentInspetionType.contains("maintenance") || currentInspetionType.contains("switchover")) {
            Drawable drawable = mContext.getDrawable(R.drawable.xs_type_copy_selector);
            if(switchMaintenances.contains(mLookup)){
                itemBinding.tvChildItem.setCompoundDrawablesWithIntrinsicBounds(mContext.getDrawable(R.drawable.xs_icon_select), null, null, null);
            }else{
                itemBinding.tvChildItem.setCompoundDrawablesWithIntrinsicBounds(mContext.getDrawable(R.drawable.xs_icon_unselect), null, null, null);
            }
            itemBinding.tvChildContent.setVisibility(View.GONE);
            itemBinding.tvChildItem.setTextSize(18f);
        }
        return itemBinding.getRoot();
    }

    private List<Lookup> switchMaintenances = new ArrayList<>();

    public void setSelectTypes(List<Lookup> maintenances) {
        this.switchMaintenances = maintenances;
        notifyDataSetChanged();
    }

}
