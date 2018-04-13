package com.cnksi.bdzinspection.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.SimpleBaseAdapter;
import com.cnksi.bdzinspection.databinding.XsCheckboxTextListItemBinding;
import com.cnksi.bdzinspection.model.TestPerson;
import com.cnksi.xscore.xsutils.StringUtils;
import com.zhy.core.utils.AutoUtils;

import java.util.List;

/**
 * 上报领导
 * 
 * @author terry
 * 
 */
public class ReportLeaderAdapter extends SimpleBaseAdapter {

	public ReportLeaderAdapter(Context context, List<? extends Object> dataList) {
		super(context, dataList);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TestPerson mPerson = (TestPerson) getItem(position);
		XsCheckboxTextListItemBinding itemBinding;
		if (convertView == null) {
			itemBinding = XsCheckboxTextListItemBinding.inflate(LayoutInflater.from(mContext));
			AutoUtils.autoSize(itemBinding.getRoot());
		} else {
			itemBinding = DataBindingUtil.findBinding(convertView);
		}

		String content = mPerson.name + " [" + mPerson.position + "]";
		itemBinding.ctvContent.setText(StringUtils.changePartTextColor(mContext, content, R.color.xs_green_color, content.length() - mPerson.position.length() - 2, content.length()));
		itemBinding.ctvContent.setChecked(mPerson.isChecked);
		

		return itemBinding.getRoot();
	}

}
