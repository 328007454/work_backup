package com.cnksi.bdzinspection.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.BaseRecyclerDataBindingAdapter;
import com.cnksi.bdzinspection.adapter.holder.ItemHolder;
import com.cnksi.bdzinspection.databinding.XsItemSpaceSplitBinding;
import com.cnksi.common.model.Device;

import java.util.Collection;
import java.util.HashSet;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/1/17 15:12
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class SpaceSplitAdapter extends BaseRecyclerDataBindingAdapter<Device> {

    HashSet<String> copyDeviceIds = new HashSet<>();

    HashSet<Device> selectDevices = new HashSet<>();

    public SpaceSplitAdapter(RecyclerView v, Collection<Device> datas) {
        super(v, datas, R.layout.xs_item_space_split);
    }

    public void setCopyDeviceIds(HashSet<String> copyDeviceIds) {
        this.copyDeviceIds = copyDeviceIds;
    }

    public HashSet<Device> getSelectDevices() {
        return selectDevices;
    }

    @Override
    public void convert(ItemHolder holder, final Device item, int position, boolean isScrolling) {
        final XsItemSpaceSplitBinding binding = holder.getDataBinding();
        binding.tvDeviceName.setText(item.name);
        binding.ibtCopyPen.setVisibility(copyDeviceIds.contains(item.deviceid) ? View.VISIBLE : View.GONE);
        binding.getRoot().setSelected(selectDevices.contains(item));
        binding.ivCheck.setVisibility(selectDevices.contains(item) ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public View.OnClickListener getOnClickListener(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Device item = realDatas.get(position);
                if (selectDevices.remove(item)) {
                    v.setSelected(false);
                    v.findViewById(R.id.iv_check).setVisibility(View.GONE);
                } else {
                    selectDevices.add(item);
                    v.setSelected(true);
                    v.findViewById(R.id.iv_check).setVisibility(View.VISIBLE);
                }
            }
        };
    }
}
