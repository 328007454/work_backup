package com.cnksi.bdzinspection.adapter.infrared;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.SimpleBaseAdapter;
import com.cnksi.bdzinspection.databinding.XsDevicePartItemBinding;
import com.cnksi.bdzinspection.model.DevicePart;
import com.cnksi.bdzinspection.utils.Config;
import com.zhy.core.utils.AutoUtils;

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
        mBitmapUtils.display(itemBinding.ivDevicePart, Config.PICTURES_FOLDER + mDevicePart.pic, mBitmapConfig);
        return itemBinding.getRoot();
    }
}
