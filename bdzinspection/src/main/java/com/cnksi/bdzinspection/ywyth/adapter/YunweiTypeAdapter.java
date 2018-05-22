package com.cnksi.bdzinspection.ywyth.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.BaseMapListExpandableAdapter;
import com.cnksi.bdzinspection.databinding.XsDialogContentChildItemBinding;
import com.cnksi.bdzinspection.databinding.XsYwTypeChildItemBinding;
import com.cnksi.bdzinspection.model.Project;
import com.zhy.autolayout.utils.AutoUtils;

public class YunweiTypeAdapter extends BaseMapListExpandableAdapter<Project, Project> {

	public YunweiTypeAdapter(Context context) {
		super(context);
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		Project child = getChild(groupPosition, childPosition);
		XsYwTypeChildItemBinding itemBinding;
		if (convertView == null) {
			itemBinding = XsYwTypeChildItemBinding.inflate(LayoutInflater.from(mContext));
			AutoUtils.autoSize(itemBinding.getRoot());
		} else {
			itemBinding = DataBindingUtil.findBinding(convertView);
		}

		itemBinding.tvChildItem.setText(child.pro_name);

		return itemBinding.getRoot();
	}


	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		Project group = getGroup(groupPosition);
		XsDialogContentChildItemBinding itemBinding;
		if (convertView == null) {
			itemBinding = XsDialogContentChildItemBinding.inflate(LayoutInflater.from(mContext));
			AutoUtils.autoSize(itemBinding.getRoot());
		} else {
			itemBinding = DataBindingUtil.findBinding(convertView);
		}

		itemBinding.tvChildItem.setText(group.pro_name);
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
