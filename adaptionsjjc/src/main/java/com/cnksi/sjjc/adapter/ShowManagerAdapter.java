package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.view.View;

import com.cnksi.sjjc.databinding.ShowNameBinding;
import com.cnksi.sjjc.inter.ItemClickListener;

import java.util.Collection;

/**
 * Created by han on 2016/8/10.
 */
public class ShowManagerAdapter extends SimpleBindingAdatpter<String>{
    public static  String MANAGER_FLAG = "manager_flag";
    public static String PEOPLE_FLAG = "people_flag";

    private ItemClickListener itemClickListener;
    private ShowNameBinding nameBinding;
    public ShowManagerAdapter(Context context, Collection<String> list, int layoutId) {
        super(context, list, layoutId);
    }

    public void setClickWidget(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void convert(ViewDataBinding dataBinding, final String item, final int position) {
        setNameBinding((ShowNameBinding)dataBinding);
        nameBinding.getRoot().setTag(nameBinding);
        nameBinding.tvPeopleName.setText(item);
        nameBinding.deleteImag.setTag(MANAGER_FLAG);

        nameBinding.deleteImag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.itemClick(view,item,position);
            }
        });
    }

    private void setNameBinding(ShowNameBinding dataBinding) {
        this.nameBinding = dataBinding;
    }
}
