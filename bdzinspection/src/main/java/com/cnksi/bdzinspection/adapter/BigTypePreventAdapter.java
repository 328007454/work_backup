package com.cnksi.bdzinspection.adapter;

import android.content.Context;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.BaseAdapter;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.Collection;

/**
 * @author lyndon
 *
 */
public class BigTypePreventAdapter extends BaseAdapter<String> {

	public BigTypePreventAdapter(Context context, Collection<String> data, int layoutId) {
		super(context, data, layoutId);
	}

	@Override
	public void convert(ViewHolder holder, String item, int position) {
		holder.getView(R.id.layout_danger).setMinimumHeight(AutoUtils.getPercentHeightSizeBigger(120));
		holder.setText(R.id.tv_danger_point_num, String.valueOf(position+1));
		holder.setText(R.id.tv_danger_content, item);
	}

}
