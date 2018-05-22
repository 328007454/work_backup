package com.cnksi.bdzinspection.adapter.defectcontrol;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.BaseMapListExpandableAdapter;
import com.cnksi.bdzinspection.databinding.XsDialogContentChildItemBinding;
import com.cnksi.bdzinspection.databinding.XsGroupItemBinding;
import com.cnksi.common.Config;
import com.zhy.core.utils.AutoUtils;

/**
 * 选择缺陷定义Dialog对话框
 * 
 * @author Joe
 * 
 */
public class BatteryDefectContentAdapter extends BaseMapListExpandableAdapter<String, String> {

	public BatteryDefectContentAdapter(Context context) {
		super(context);
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

		String child = getChild(groupPosition, childPosition);
		XsDialogContentChildItemBinding itemBinding = null;
		if (convertView == null) {
			itemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.xs_dialog_content_child_item, parent, false);
			AutoUtils.autoSize(itemBinding.getRoot());
		} else {
			itemBinding = DataBindingUtil.findBinding(convertView);
		}

		itemBinding.tvChildItem.setText(child);

		return itemBinding.getRoot();
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

		String group = getGroup(groupPosition);
		XsGroupItemBinding itemBinding=null;
		if (convertView == null) {
			itemBinding = XsGroupItemBinding.inflate(LayoutInflater.from(parent.getContext()));
			
			AutoUtils.autoSize(itemBinding.getRoot());
		} else {
			itemBinding = DataBindingUtil.findBinding(convertView);
		}

		if (Config.SERIOUS_LEVEL.equalsIgnoreCase(group)) {
			itemBinding.tvGroupItem.setTextColor(mContext.getResources().getColor(R.color.xs_orange_color));
		} else if (Config.CRISIS_LEVEL.equalsIgnoreCase(group)) {
			itemBinding.tvGroupItem.setTextColor(mContext.getResources().getColor(R.color.xs_red_color));
		} else {
			itemBinding.tvGroupItem.setTextColor(mContext.getResources().getColor(R.color.xs_yellow_color));
		}

		itemBinding.tvGroupItem.setText(group);

		return itemBinding.getRoot();
	}
}
