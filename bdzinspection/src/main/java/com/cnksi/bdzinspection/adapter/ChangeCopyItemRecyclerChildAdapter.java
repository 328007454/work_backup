package com.cnksi.bdzinspection.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.BaseRecyclerAdapter;
import com.cnksi.bdzinspection.model.ChangeCopyItem;

import java.util.Collection;
import java.util.List;

/**
 * Created by han on 2017/5/18.
 */

public class ChangeCopyItemRecyclerChildAdapter extends BaseRecyclerAdapter {
    private ItemClickListener itemClickListener;
    private List<ChangeCopyItem> copyItems;

    public ChangeCopyItemRecyclerChildAdapter(RecyclerView v, Collection datas, int itemLayoutId) {
        super(v, datas, itemLayoutId);
        this.copyItems = (List<ChangeCopyItem>) datas;
    }

    public void setList(List<ChangeCopyItem> copyItems) {
        this.copyItems = copyItems;
    }

    private void setChildItem(final int position, RecyclerHolder holder, Object item1) {
        final ChangeCopyItem item = copyItems.get(position);
        String content = "";
        String value = "";
        if (item.getVal().equalsIgnoreCase("Y")) {
            content = "抄录" + item.getItem().description + (TextUtils.isEmpty(item.getItem().unit) ? "" : "(" + item.getItem().unit + ")");
            value = TextUtils.isEmpty(item.getResult().val) ? "" : item.getResult().val;
        } else if (item.getVal_a().equalsIgnoreCase("Y")) {
            content = "抄录A相" + item.getItem().description + (TextUtils.isEmpty(item.getItem().unit) ? "" : "(" + item.getItem().unit + ")");
            value = TextUtils.isEmpty(item.getResult().val_a) ? "" : item.getResult().val_a;
        } else if (item.getVal_b().equalsIgnoreCase("Y")) {
            content = "抄录B相" + item.getItem().description + (TextUtils.isEmpty(item.getItem().unit) ? "" : "(" + item.getItem().unit + ")");
            value = TextUtils.isEmpty(item.getResult().val_b) ? "" : item.getResult().val_b;
        } else if (item.getVal_c().equalsIgnoreCase("Y")) {
            content = "抄录C相" + item.getItem().description + (TextUtils.isEmpty(item.getItem().unit) ? "" : "(" + item.getItem().unit + ")");
            value = TextUtils.isEmpty(item.getResult().val_c) ? "" : item.getResult().val_c;
        } else if (item.getVal_o().equalsIgnoreCase("Y")) {
            content = "抄录O相" + item.getItem().description + (TextUtils.isEmpty(item.getItem().unit) ? "" : "(" + item.getItem().unit + ")");
            value = TextUtils.isEmpty(item.getResult().val_o) ? "" : item.getResult().val_o;
        }
        TextView tvCopyContent = holder.getView(R.id.tv_copy_content);
        TextView tvCopytValue = holder.getView(R.id.tv_copyt_value);
        ImageView itemDelete = holder.getView(R.id.item_delete);
        tvCopyContent.setText(content);
        tvCopytValue.setText(TextUtils.isEmpty(value) ? "" : value);
//        childBinding.tvCopytValue.setText(value);
        itemDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null)
                    itemClickListener.onClick(v, item, position);
            }
        });

    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void convert(RecyclerHolder holder, Object item, int position, boolean isScrolling) {
        setChildItem(position, holder, item);
    }
}