package com.cnksi.workticket.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.workticket.R;
import com.haibin.calendarview.BaseView;

import org.xutils.db.table.DbModel;

import java.util.List;

/**
 * Created by Mr.K on 2018/5/4.
 */

public class BdzAdapter extends BaseQuickAdapter<DbModel, BaseViewHolder> {


    public BdzAdapter(int layoutResId, @Nullable List<DbModel> data) {
        super(layoutResId, data);
    }
    OnItemClickListener itemClickListener;
    public interface  OnItemClickListener{
        void onItemClick(View v,DbModel item ,int position);
    }

    public void setItemClickListener(OnItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @Override
    protected void convert(BaseViewHolder helper, DbModel item) {
        helper.setText(R.id.txt_bdz_name, item.getString("name"));
        helper.itemView.setOnClickListener(v -> {
            if (itemClickListener!=null){
                itemClickListener.onItemClick(v,item,helper.getAdapterPosition());
            }
        });
    }
}
