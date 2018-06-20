package com.cnksi.common.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.common.R;
import com.cnksi.common.listener.ItemClickListener;
import com.cnksi.common.model.Users;

import org.xutils.db.table.DbModel;

import java.util.List;

/**
 * @author kkk on 2017/12/29.
 */

public class SelectionPersonAdapter extends BaseQuickAdapter<DbModel, BaseViewHolder> {
    private ItemClickListener mClickListener;

    public SelectionPersonAdapter(int layoutResId, @Nullable List<DbModel> data) {
        super(layoutResId, data);
    }


    public void setOnItemClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    @Override
    protected void convert(BaseViewHolder helper, DbModel item) {
        TextView txtUserName = helper.getView(R.id.txt_username);
        ImageView ivDelete = helper.getView(R.id.iv_delete);
        txtUserName.setText(item.getString(Users.USERNAME));
        ivDelete.setVisibility(TextUtils.equals("true", item.getString("delete")) ? View.VISIBLE : View.INVISIBLE);
        ivDelete.setOnClickListener(v -> {
            if (mClickListener != null) {
                mClickListener.onClick(v, item, helper.getAdapterPosition());
            }
        });
    }
}
