package com.cnksi.bdzinspection.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.SimpleBaseAdapter;
import com.cnksi.bdzinspection.databinding.XsAddDefectDefineItemBinding;
import com.cnksi.bdzinspection.model.DefectDefine;
import com.cnksi.bdzinspection.model.DevicePart;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.xscore.xsutils.StringUtils;
import com.lidroid.xutils.db.table.DbModel;
import com.zhy.core.utils.AutoUtils;

import java.util.List;


public class DefectDefineAdapter extends SimpleBaseAdapter {

	public DefectDefineAdapter(Context context, List<? extends Object> dataList) {
		super(context, dataList);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		DbModel mDbModel = (DbModel) getItem(position);
		XsAddDefectDefineItemBinding itemBinding;
		if (convertView == null) {
			itemBinding = XsAddDefectDefineItemBinding.inflate(LayoutInflater.from(parent.getContext()));
			AutoUtils.autoSize(itemBinding.getRoot());
			itemBinding.llContainer.setMinimumHeight(AutoUtils.getPercentHeightSizeBigger(120));
		} else {
			itemBinding = DataBindingUtil.findBinding(convertView);
		}

		if (Config.CRISIS_LEVEL.equalsIgnoreCase(mDbModel.getString(DefectDefine.LEVEL))) {
			itemBinding.tvDefectLevel.setTextColor(mContext.getResources().getColor(R.color.xs_red_color));
		} else if (Config.SERIOUS_LEVEL.equalsIgnoreCase(mDbModel.getString(DefectDefine.LEVEL))) {
			itemBinding.tvDefectLevel.setTextColor(mContext.getResources().getColor(R.color.xs_orange_color));
		} else {
			itemBinding.tvDefectLevel.setTextColor(mContext.getResources().getColor(R.color.xs_yellow_color));
		}
		itemBinding.tvDefectLevel.setText(mDbModel.getString(DefectDefine.LEVEL));
		itemBinding.tvDefectDefine.setText(StringUtils.changePartTextColor(mContext, mDbModel.getString(DevicePart.NAME) + " — " + mDbModel.getString(DefectDefine.DESCRIPTION), R.color.xs_green_color, 0, mDbModel.getString(DevicePart.NAME).length()));
		return itemBinding.getRoot();
	}
}
