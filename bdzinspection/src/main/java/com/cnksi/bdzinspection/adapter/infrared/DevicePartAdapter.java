package com.cnksi.bdzinspection.adapter.infrared;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.SimpleBaseAdapter;
import com.cnksi.bdzinspection.databinding.XsDevicePartItemBinding;
import com.cnksi.common.Config;
import com.cnksi.common.model.DevicePart;
import com.cnksi.core.utils.BitmapUtils;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

public class DevicePartAdapter extends SimpleBaseAdapter {

    public DevicePartAdapter(Context context, List<? extends Object> dataList) {
        super(context, dataList);
        initBitmapUtils(context);
        int height = mContext.getResources().getDimensionPixelSize(R.dimen.history_defect_photo_height);
        setBitmapSize(height, height);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DevicePart mDevicePart = (DevicePart) getItem(position);
        XsDevicePartItemBinding itemBinding = null;
        if (convertView == null) {
            itemBinding = XsDevicePartItemBinding.inflate(LayoutInflater.from(parent.getContext()));
            AutoUtils.autoSize(itemBinding.getRoot());

        } else {
            itemBinding = DataBindingUtil.findBinding(convertView);
        }
        itemBinding.tvDevicePartName.setText(mDevicePart.name);
        Bitmap bitmap = BitmapUtils.getImageThumbnailByWidth(Config.PICTURES_FOLDER + mDevicePart.pic,135);
        if (bitmap !=null){
            itemBinding.ivDevicePart.setImageBitmap(bitmap);
        }
        return itemBinding.getRoot();
    }
}
