package com.cnksi.bdzinspection.adapter.addtask;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.BaseMapListExpandableAdapter;
import com.cnksi.bdzinspection.databinding.XsDialogContentChildItemBinding;
import com.cnksi.bdzinspection.databinding.XsInspectionTypeChildItemBinding;
import com.cnksi.common.model.Lookup;
import com.zhy.autolayout.utils.AutoUtils;

/**
 * 追加巡检任务  --> 选择巡检类型List Adapter
 *
 * @author Joe
 */
public class InspectionTypeAdapter extends BaseMapListExpandableAdapter<Lookup, Lookup> {

    public InspectionTypeAdapter(Context context) {
        super(context);
    }



    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        Lookup child = getChild(groupPosition, childPosition);
        XsInspectionTypeChildItemBinding itemBinding;
        if (convertView == null) {
            itemBinding = XsInspectionTypeChildItemBinding.inflate(LayoutInflater.from(parent.getContext()));
            AutoUtils.autoSize(itemBinding.getRoot());
        } else {
            itemBinding = DataBindingUtil.findBinding(convertView);
        }

       itemBinding.tvChildItem.setText(child.v);

        return itemBinding.getRoot();
    }



    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        Lookup group = getGroup(groupPosition);
        XsDialogContentChildItemBinding itemBinding;
        if (convertView == null) {
            itemBinding = XsDialogContentChildItemBinding.inflate(LayoutInflater.from(parent.getContext()));
            AutoUtils.autoSize(itemBinding.getRoot());
        } else {
            itemBinding = DataBindingUtil.findBinding(convertView);
        }

        itemBinding.tvChildItem.setText(group.v);
        itemBinding.tvChildItem.setTextColor(mContext.getResources().getColor(R.color.xs_green_color));

        if (getChildrenCount(groupPosition) > 0) {
            if (isExpanded) {
                itemBinding.tvChildItem.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.xs_ic_shrink, 0);
            } else {
                itemBinding.tvChildItem.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.xs_ic_open, 0);
            }
        } else {
            itemBinding.tvChildItem.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        return itemBinding.getRoot();
    }
}
