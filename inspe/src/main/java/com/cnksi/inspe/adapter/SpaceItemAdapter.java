package com.cnksi.inspe.adapter;

import android.support.annotation.Nullable;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.inspe.R;

import org.xutils.db.table.DbModel;

import java.util.List;

/**
 * Created by Mr.K on 2018/4/17.
 */

public class SpaceItemAdapter extends BaseQuickAdapter<DbModel, BaseViewHolder> {

    public SpaceItemAdapter(int layoutResId, @Nullable List<DbModel> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DbModel item) {
        ((TextView) helper.getView(R.id.tv_device_name)).setText(item.getString("name"));
    }
}
