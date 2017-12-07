package com.cnksi.sjjc.adapter;

import android.support.v7.widget.RecyclerView;

import com.cnksi.sjjc.adapter.holder.ItemHolder;
import com.cnksi.sjjc.bean.BatteryInstrument;
import com.cnksi.sjjc.databinding.ItemBatteryDefdeviceBinding;
import com.cnksi.sjjc.inter.ItemClickListener;

import java.util.Collection;

/**
 * @author kkk on 2017/12/4.
 */

public class BatteryDefetcDeviceAdapter extends BaseRecyclerDataBindingAdapter<BatteryInstrument> {
    public BatteryDefetcDeviceAdapter(RecyclerView v, Collection datas, int itemLayoutId) {
        super(v, datas, itemLayoutId);
    }

    @Override
    public void convert(ItemHolder holder, BatteryInstrument item, int position, boolean isScrolling) {
        ItemBatteryDefdeviceBinding defdeviceBinding = (ItemBatteryDefdeviceBinding) holder.getDataBinding();
        defdeviceBinding.txtNum.setText("" + (position + 1));
        defdeviceBinding.txtDeviceName.setText(item.testName);
        defdeviceBinding.txtDeviceNum.setText(item.num);
    }
}

