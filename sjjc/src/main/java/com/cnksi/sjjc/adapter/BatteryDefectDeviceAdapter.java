package com.cnksi.sjjc.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.BatteryInstrument;

import java.util.List;

/**
 * @author kkk on 2017/12/4.
 */

public class BatteryDefectDeviceAdapter extends BaseQuickAdapter<BatteryInstrument,BaseViewHolder> {


    public BatteryDefectDeviceAdapter(int layoutResId, @Nullable List<BatteryInstrument> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BatteryInstrument item) {
        helper.setText(R.id.txt_num,"" + (helper.getAdapterPosition() + 1));
        helper.setText(R.id.txt_device_name,item.testName);
        helper.setText(R.id.txt_device_num,item.num);

    }
}

