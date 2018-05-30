package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.view.View;

import com.cnksi.common.base.BaseBindingAdapter;
import com.cnksi.common.listener.ItemClickListener;
import com.cnksi.sjjc.databinding.ShowNameBinding;

import java.util.List;

/**
 * Created by han on 2016/8/10.
 */
public class ShowPeopleAdapter extends BaseBindingAdapter<ShowNameBinding, String> {
    public static String MANAGER_FLAG = "manager_flag";
    public static String PEOPLE_FLAG = "people_flag";

    private ItemClickListener itemClickListener;

    private int totoalUserCount;

    public ShowPeopleAdapter(Context context, List<String> list, int layoutId) {
        super(context, list, layoutId);
    }

    public void setClickWidget(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void convert(ShowNameBinding nameBinding, final String item, final int position) {
        nameBinding.tvPeopleName.setText(item);
        nameBinding.deleteImag.setTag(PEOPLE_FLAG);
        nameBinding.deleteImag.setOnClickListener(view -> itemClickListener.onClick(view, item, position));
        if (position < totoalUserCount) {
            nameBinding.deleteImag.setVisibility(View.GONE);
        } else {
            nameBinding.deleteImag.setVisibility(View.VISIBLE);
        }
    }


    public void setUserCount(int user) {
        totoalUserCount = user;
        notifyDataSetChanged();
    }
}
