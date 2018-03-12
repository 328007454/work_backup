package com.cnksi.sjjc.adapter;

import android.support.v7.widget.RecyclerView;

import com.cnksi.sjjc.adapter.holder.ItemHolder;
import com.cnksi.sjjc.bean.Device;
import com.cnksi.sjjc.databinding.DeviceItemBinding;
import com.cnksi.sjjc.inter.ItemClickListener;
import com.zhy.autolayout.utils.L;

import java.util.Collection;

/**
 * Created by kkk on 2018/3/9.
 */

public class BHDZJLDeviceAdapter extends BaseRecyclerDataBindingAdapter<Device> {

    public BHDZJLDeviceAdapter(RecyclerView v, Collection datas, int itemLayoutId) {
        super(v, datas, itemLayoutId);
    }

    @Override
    public void convert(ItemHolder holder, Device item, int position, boolean isScrolling) {
        DeviceItemBinding itemBinding = (DeviceItemBinding) holder.getDataBinding();
        itemBinding.tvDeviceName.setText(item.pmsName);
    }

}