package com.cnksi.sjjc.adapter;

import android.content.Context;

import com.cnksi.sjjc.databinding.NewLaunchTaskItemBinding;

import org.xutils.db.table.DbModel;

import java.util.List;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2017/3/23 15:20
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class NewLaunchTaskAdapter extends BaseBindingAdapter<NewLaunchTaskItemBinding, DbModel> {
    public NewLaunchTaskAdapter(Context context, List<DbModel> data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(NewLaunchTaskItemBinding binding, DbModel item, int position) {

    }
}
