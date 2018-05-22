/**
 *
 */
package com.cnksi.bdzinspection.ywyth.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.adapter.base.SimpleBaseAdapter;
import com.cnksi.bdzinspection.databinding.XsYwGqjAdapterBinding;
import com.cnksi.bdzinspection.model.GQJ;
import com.cnksi.core.utils.StringUtils;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ksi-android
 *
 */
public class YWGQJAdapter extends SimpleBaseAdapter {

	private Map<Integer, Boolean> mflag;

	/**
	 * @param context
	 */
	public YWGQJAdapter(Context context, List<? extends Object> datalist, Map<Integer, Boolean> mflag) {
		super(context, datalist);
		if (mflag == null) {
			this.mflag = new HashMap<>();
		} else {
			this.mflag = mflag;
		}
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		XsYwGqjAdapterBinding itemBinding;
		if (convertView == null) {
			itemBinding = XsYwGqjAdapterBinding.inflate(LayoutInflater.from(mContext));
			AutoUtils.autoSize(itemBinding.getRoot());
		} else {
			itemBinding = DataBindingUtil.findBinding(convertView);
		}
		final GQJ bean = (GQJ) getItem(position);
		itemBinding.tv1.setText(bean.name);
		if (StringUtils.isEmpty(bean.bz)) {
			itemBinding.tv2.setText("备注：无");
		} else {
			itemBinding.tv2.setText("备注：" + bean.bz);
		}
		if (mflag.get(position) == null || mflag.get(position) == false) {
			itemBinding.check.setChecked(false);
		} else {
			itemBinding.check.setChecked(true);
		}

		itemBinding.tv3.setText(TextUtils.isEmpty(bean.amount) ? "无" : bean.amount);
		return itemBinding.getRoot();
	}

}
