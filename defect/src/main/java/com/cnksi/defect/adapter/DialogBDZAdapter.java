package com.cnksi.defect.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v4.content.ContextCompat;

import com.cnksi.common.databinding.DialogContentChildItemBinding;
import com.cnksi.common.listener.ItemClickListener;
import com.cnksi.common.model.Bdz;
import com.cnksi.core.adapter.BaseAdapter;
import com.cnksi.defect.R;

import java.util.Collection;

/**
 * Created by luoxy on 16/4/29.
 */
public class DialogBDZAdapter extends BaseAdapter<Bdz> {

    private ItemClickListener<Bdz> itemClickListener;

    public void setItemClickListener(ItemClickListener<Bdz> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public DialogBDZAdapter(Context context, Collection<Bdz> data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(ViewDataBinding dataBinding, Bdz item, int position) {
        DialogContentChildItemBinding binding = (DialogContentChildItemBinding) dataBinding;
        binding.tvChildItem.setTextColor(ContextCompat.getColor(context, R.color.green_color));
        binding.tvChildItem.setText(item.name);
        binding.getRoot().setOnClickListener(v -> {
            if (null != itemClickListener) {
                itemClickListener.onClick(v, item, position);
            }
        });

    }

}
