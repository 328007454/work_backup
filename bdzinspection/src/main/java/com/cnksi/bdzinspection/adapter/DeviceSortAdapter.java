package com.cnksi.bdzinspection.adapter;

import android.content.Context;
import android.view.View;

import com.cnksi.bdzinspection.R;
import com.cnksi.common.base.BaseBindingAdapter;
import com.cnksi.bdzinspection.databinding.XsSpaceSortItemBinding;
import com.cnksi.common.model.Device;

import java.util.List;

/**
 * @author Wastrel
 * @date 创建时间：2016年4月21日 上午11:45:44 TODO
 */
public class DeviceSortAdapter extends BaseBindingAdapter<XsSpaceSortItemBinding, Device> {

    public DeviceSortAdapter(Context context, List<Device> data) {
        super(context, data, R.layout.xs_space_sort_item);

    }

    @Override
    public void convert(XsSpaceSortItemBinding binding, final Device item, final int position) {
        binding.dragHandle.setText((position + 1) + "、");
        binding.text.setText(item.name);
        binding.btnOp.setVisibility(View.GONE);
        binding.btnSort.setVisibility(View.GONE);

    }

}
