package com.cnksi.login.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.common.model.DefectRecord;

import java.util.List;

/**
 * Created by Mr.K on 2018/7/5.
 */

public class HomeWeekDefectAdapter extends BaseQuickAdapter<DefectRecord, BaseViewHolder> {

    public HomeWeekDefectAdapter(int layoutResId, @Nullable List<DefectRecord> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DefectRecord item) {

    }


}
