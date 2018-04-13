package com.cnksi.bdzinspection.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.adapter.base.SimpleBaseAdapter;
import com.cnksi.bdzinspection.databinding.XsAddPersonItemBinding;
import com.cnksi.bdzinspection.inter.ItemClickListener;
import com.cnksi.bdzinspection.model.Users;
import com.lidroid.xutils.db.table.DbModel;
import com.zhy.core.utils.AutoUtils;

import java.util.List;

public class AddPersonAdapter extends SimpleBaseAdapter {

	private ItemClickListener mClickListener;

	public AddPersonAdapter(Context context, List<? extends Object> dataList) {
		super(context, dataList);
	}


	public void setOnItemClickListener(com.cnksi.bdzinspection.inter.ItemClickListener clickListener){
		this.mClickListener = clickListener;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final DbModel model = (DbModel) getItem(position);
		XsAddPersonItemBinding itemBinding =null;
		if (convertView == null) {
			itemBinding = XsAddPersonItemBinding.inflate(LayoutInflater.from(parent.getContext()));
			AutoUtils.autoSize(itemBinding.getRoot());
		} else {
			itemBinding = DataBindingUtil.findBinding(convertView);
		}
		itemBinding.tvChildItem.setText(model.getString(Users.USERNAME));
		itemBinding.llContainer.setOnClickListener(view -> {
            if(mClickListener!=null){
                mClickListener.onItemClick(view,model,position);
            }
        });
		return itemBinding.getRoot();
	}


}
