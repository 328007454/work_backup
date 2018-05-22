package com.cnksi.bdzinspection.ywyth.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.adapter.base.SimpleBaseAdapter;
import com.cnksi.bdzinspection.databinding.XsVideoAdapterBinding;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.MediaRecorderUtils;
import com.cnksi.bdzinspection.ywyth.VideoPlayer;
import com.cnksi.xscore.xsutils.BitmapUtil;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * @author Wastrel
 * @date 创建时间：2016年3月29日 下午8:37:47 TODO
 */
public class VideoAdapter extends SimpleBaseAdapter {

	/**
	 * @param context
	 * @param dataList
	 */
	public VideoAdapter(Context context, List<? extends Object> dataList) {
		super(context, dataList);
		initBitmapUtils(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		XsVideoAdapterBinding adapterBinding ;
		if (convertView == null) {
			adapterBinding = XsVideoAdapterBinding.inflate(LayoutInflater.from(mContext));
			AutoUtils.autoSize(adapterBinding.getRoot());

		} else {
		adapterBinding = DataBindingUtil.findBinding(convertView);
		}
		final String path = Config.VIDEO_FOLDER + (String) getItem(position);
		adapterBinding.img.setImageBitmap(BitmapUtil.getVideoThumbnail(path, 300, 400));
		adapterBinding.tv.setText(MediaRecorderUtils.getInstance().getVedioDurationString(mContext, path));
		convertView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, VideoPlayer.class);
            intent.putExtra("video", path);
            mContext.startActivity(intent);
        });
		return adapterBinding.getRoot();
	}

}
