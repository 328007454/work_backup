package com.cnksi.bdzinspection.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.cnksi.bdzinspection.adapter.base.BaseRecyclerDataBindingAdapter;
import com.cnksi.bdzinspection.adapter.holder.ItemHolder;
import com.cnksi.bdzinspection.databinding.XsItemTaskUserBinding;
import com.cnksi.bdzinspection.inter.ItemClickListener;

import org.xutils.db.table.DbModel;

import java.util.Collection;

/**
 * @author kkk on 2017/12/29.
 */

public class SelectionPersonAdapter extends BaseRecyclerDataBindingAdapter {
    private ItemClickListener mClickListener;

    public void setOnItemClickListener(com.cnksi.bdzinspection.inter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public SelectionPersonAdapter(RecyclerView v, Collection datas, int itemLayoutId) {
        super(v, datas, itemLayoutId);
    }

    @Override
    public void convert(ItemHolder holder, Object item, final int position, boolean isScrolling) {
        XsItemTaskUserBinding taskUserBinding = holder.getDataBinding();
        final DbModel model = (DbModel) item;

        taskUserBinding.txtUsername.setText(model.getString("username"));
        taskUserBinding.ivDelete.setVisibility(TextUtils.equals("true", model.getString("delete")) ? View.VISIBLE : View.INVISIBLE);
        taskUserBinding.ivDelete.setOnClickListener(view -> {
            if (null != mClickListener) {
                mClickListener.onItemClick(view, model, position);
            }
        });
    }

}
