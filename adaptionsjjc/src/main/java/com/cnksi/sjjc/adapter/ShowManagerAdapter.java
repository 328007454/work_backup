package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;

import com.cnksi.common.listener.ItemClickOrLongClickListener;
import com.cnksi.sjjc.databinding.ShowNameBinding;

import java.util.Collection;

/**
 * Created by han on 2016/8/10.
 */
public class ShowManagerAdapter extends SimpleBindingAdatpter<String>{
    public static  String MANAGER_FLAG = "manager_flag";
    public static String PEOPLE_FLAG = "people_flag";

    private ItemClickOrLongClickListener itemClickListener;
    private ShowNameBinding nameBinding;
    public ShowManagerAdapter(Context context, Collection<String> list, int layoutId) {
        super(context, list, layoutId);
    }

    public void setClickWidget(ItemClickOrLongClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void convert(ViewDataBinding dataBinding, final String item, final int position) {
        setNameBinding((ShowNameBinding)dataBinding);
        nameBinding.getRoot().setTag(nameBinding);
        nameBinding.tvPeopleName.setText(item);
        nameBinding.deleteImag.setTag(MANAGER_FLAG);

        nameBinding.deleteImag.setOnClickListener(view -> itemClickListener.onClick(view,item,position));
    }

    private void setNameBinding(ShowNameBinding dataBinding) {
        this.nameBinding = dataBinding;
    }
}
