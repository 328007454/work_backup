package com.cnksi.bdzinspection.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.adapter.base.SimpleBaseAdapter;
import com.cnksi.bdzinspection.databinding.XsImgAdapterBinding;
import com.cnksi.common.Config;
import com.cnksi.core.utils.BitmapUtils;
import com.zhy.core.utils.AutoUtils;

import java.util.List;

/**
 * @author Wastrel
 * @date 创建时间：2016年3月29日 下午8:37:47 TODO
 */
public class ImageAdapter extends SimpleBaseAdapter {

	/**
	 * @param context
	 * @param dataList
	 */
	public ImageAdapter(Context context, List<? extends Object> dataList) {
		super(context, dataList);
		initBitmapUtils(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		XsImgAdapterBinding itemBingding;
		if (convertView == null) {
			itemBingding = XsImgAdapterBinding.inflate(LayoutInflater.from(mContext));
			AutoUtils.autoSize(itemBingding.getRoot());

		} else {
			itemBingding = DataBindingUtil.findBinding(convertView);
		}
		Bitmap bitmap = BitmapUtils.getImageThumbnailByWidth(Config.CUSTOMER_PICTURES_FOLDER + (String) getItem(position),300);
		if (bitmap !=null){
			itemBingding.img.setImageBitmap(bitmap);
		}
		return itemBingding.getRoot();
	}


}
