package com.cnksi.sjjc.adapter;

import android.content.Context;

import com.cnksi.common.base.BaseBindingAdapter;
import com.cnksi.common.listener.ItemClickListener;
import com.cnksi.sjjc.databinding.ShowNameBinding;

import java.util.List;

/**
 * Created by han on 2016/8/10.
 */
public class ShowManagerAdapter extends BaseBindingAdapter<ShowNameBinding,String> {
    public static  String MANAGER_FLAG = "manager_flag";
    public static String PEOPLE_FLAG = "people_flag";

    private ItemClickListener itemClickListener;

    public ShowManagerAdapter(Context context, List<String> list, int layoutId) {
        super(context, list, layoutId);
    }

    public void setClickWidget(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void convert(ShowNameBinding nameBinding, final String item, final int position) {

        nameBinding.tvPeopleName.setText(item);
        nameBinding.deleteImag.setTag(MANAGER_FLAG);
        nameBinding.deleteImag.setOnClickListener(view -> itemClickListener.onClick(view,item,position));
    }

}
