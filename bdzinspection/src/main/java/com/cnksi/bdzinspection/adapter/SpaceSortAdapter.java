package com.cnksi.bdzinspection.adapter;

import android.content.Context;
import android.view.View;

import com.cnksi.bdzinspection.R;
import com.cnksi.common.base.BaseBindingAdapter;
import com.cnksi.bdzinspection.databinding.XsSpaceSortItemBinding;
import com.cnksi.common.listener.ItemClickListener;
import com.cnksi.common.model.Spacing;

import java.util.HashMap;
import java.util.List;

/**
 * @author Wastrel
 * @date 创建时间：2016年4月21日 上午11:45:44 TODO
 */
public class SpaceSortAdapter extends BaseBindingAdapter<XsSpaceSortItemBinding, Spacing> {

    HashMap<String, Integer> deviceCountMap;
    private ItemClickListener<Spacing> clickListener, sortListener;

    public SpaceSortAdapter(Context context, List<Spacing> data, HashMap<String, Integer> map) {
        super(context, data, R.layout.xs_space_sort_item);
        this.deviceCountMap = map;
    }

    public void setClickListener(ItemClickListener<Spacing> clickListener) {
        this.clickListener = clickListener;
    }

    public void setSortListener(ItemClickListener<Spacing> clickListener) {
        this.sortListener = clickListener;
    }

    public HashMap<String, Integer> getDeviceCountMap() {
        return deviceCountMap;
    }

    @Override
    public void convert(XsSpaceSortItemBinding binding, final Spacing item, final int position) {
        Integer deviceCount = deviceCountMap.get(item.spid);
        deviceCount = deviceCount == null ? 0 : deviceCount;
        binding.dragHandle.setText((position + 1) + "、");
        binding.text.setText(item.name + "(" + deviceCount + ")");
        binding.btnOp.setVisibility(View.VISIBLE);
        if (item.isSubPlace()) {
            binding.btnOp.setText("恢复");
        } else {
            if (deviceCount > 1) {
                binding.btnOp.setText("拆分");
            } else {
                binding.btnOp.setVisibility(View.GONE);
            }
        }
        binding.btnOp.setText(item.isSubPlace() ? "恢复" : "拆分");
        binding.btnOp.setBackgroundResource(item.isSubPlace() ? R.drawable.xs_green_background_selector : R.drawable.xs_red_button_background_selector);
        binding.btnOp.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onClick(v, item, position);
            }
        });
        binding.btnSort.setVisibility(deviceCount > 1 ? View.VISIBLE : View.GONE);
        binding.btnSort.setOnClickListener(v -> {
            if (sortListener != null) {
                sortListener.onClick(v, item, position);
            }
        });
    }

}
