package com.cnksi.common.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.common.R;
import com.cnksi.common.model.Bdz;

import java.util.List;

/**
 * @author Mr.K on 2018/6/8.
 * @decrption 人员列表适配器
 */

public class BdzAdapter extends BaseQuickAdapter<Bdz, BaseViewHolder> {
    public BdzAdapter(int layoutResId, @Nullable List data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, Bdz item) {
        String name = TextUtils.isEmpty(item.name)?"":item.name;
        helper.setText(R.id.txt_name,name);
    }
}

