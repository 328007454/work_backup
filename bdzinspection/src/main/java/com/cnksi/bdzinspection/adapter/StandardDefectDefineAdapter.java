package com.cnksi.bdzinspection.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.BaseMapListExpandableAdapter;
import com.cnksi.bdzinspection.databinding.XsGroupItemBinding;
import com.cnksi.bdzinspection.databinding.XsStandardDefectDefineChildItemBinding;
import com.cnksi.bdzinspection.model.Defect;
import com.cnksi.common.Config;
import com.zhy.autolayout.utils.AutoUtils;

/**
 * 选择缺陷定义Dialog对话框
 * 
 */
public class StandardDefectDefineAdapter extends BaseMapListExpandableAdapter<String, Defect> {

	public StandardDefectDefineAdapter(Context context) {
		super(context);
	}

	private OnAdapterViewClickListener mOnAdapterViewClickListener;

	public interface OnAdapterViewClickListener {
		 void OnAdapterViewClick(View view, String defectLevel,Defect define);
	}

	public void setOnAdapterViewClickListener(OnAdapterViewClickListener mOnAdapterViewClickListener) {
		this.mOnAdapterViewClickListener = mOnAdapterViewClickListener;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

		Defect child = getChild(groupPosition, childPosition);
		XsStandardDefectDefineChildItemBinding itemBinding;
		if (convertView == null) {
			itemBinding = XsStandardDefectDefineChildItemBinding.inflate(LayoutInflater.from(mContext));
			AutoUtils.autoSize(itemBinding.getRoot());
		} else {
			itemBinding = DataBindingUtil.findBinding(convertView);
		}
		itemBinding.tvChildItem.setText(child.description);

		itemBinding.ibtnDelete.setOnClickListener(view -> {
            if (mOnAdapterViewClickListener != null) {
                mOnAdapterViewClickListener.OnAdapterViewClick(view, StandardDefectDefineAdapter.this.getGroup(groupPosition), child);
            }
        });
		return itemBinding.getRoot();
	}
	

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

		String group = getGroup(groupPosition);
		XsGroupItemBinding itemBinding ;
		if (convertView == null) {
			itemBinding = XsGroupItemBinding.inflate(LayoutInflater.from(mContext));
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
