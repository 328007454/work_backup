package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.view.View;

import com.cnksi.common.listener.ItemClickListener;
import com.cnksi.sjjc.databinding.ShowNameBinding;

import java.util.Collection;

/**
 * Created by han on 2016/8/10.
 */
public class ShowPeopleAdapter extends SimpleBindingAdatpter<String> {
    public static String MANAGER_FLAG = "manager_flag";
    public static String PEOPLE_FLAG = "people_flag";

    private ItemClickListener itemClickListener;
    private ShowNameBinding nameBinding;
    private int totoalUserCount;

    public ShowPeopleAdapter(Context context, Collection<String> list, int layoutId) {
        super(context, list, layoutId);
    }

    public void setClickWidget(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void convert(ViewDataBinding dataBinding, final String item, final int position) {
        setNameBinding((ShowNameBinding) dataBinding);
        nameBinding.getRoot().setTag(nameBinding);
        nameBinding.tvPeopleName.setText(item);
        nameBinding.deleteImag.setTag(PEOPLE_FLAG);

        nameBinding.deleteImag.setOnClickListener(view -> itemClickListener.itemClick(view, item, position));
        if (position < totoalUserCount) {
            nameBinding.deleteImag.setVisibility(View.GONE);
        } else {
            nameBinding.deleteImag.setVisibility(View.VISIBLE);
        }
    }

    private void setNameBinding(ShowNameBinding dataBinding) {
        this.nameBinding = dataBinding;
    }

    public void setUserCount(int user) {
        totoalUserCount = user;
        notifyDataSetChanged();
    }
}
