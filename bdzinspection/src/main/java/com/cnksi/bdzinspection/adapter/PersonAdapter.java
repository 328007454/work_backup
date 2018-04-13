package com.cnksi.bdzinspection.adapter;

import android.support.v7.widget.RecyclerView;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.BaseRecyclerDataBindingAdapter;
import com.cnksi.bdzinspection.adapter.holder.ItemHolder;
import com.cnksi.bdzinspection.databinding.XsItemDeviceParamTypeBinding;
import com.cnksi.bdzinspection.model.Users;

import java.util.Collection;
import java.util.List;

/**
 * Created by kkk on 2017/9/25.
 */

public class PersonAdapter extends BaseRecyclerDataBindingAdapter<Users> {
    public PersonAdapter(RecyclerView v, Collection datas, int itemLayoutId) {
        super(v, datas, itemLayoutId);

    }


    @Override
    public void convert(ItemHolder holder, Users item, int position, boolean isScrolling) {
        XsItemDeviceParamTypeBinding userBinding = holder.getDataBinding();
        userBinding.tvTypeName.setText(item.username);
        userBinding.imgCheck.setImageResource((null != users && users.contains(item)) ? R.drawable.xs_icon_select : R.drawable.xs_icon_unselect);
    }


    private List<Users> users;

    public void setClickObject(Object data) {
        this.users = (List<Users>) data;
        notifyDataSetChanged();
    }
}
