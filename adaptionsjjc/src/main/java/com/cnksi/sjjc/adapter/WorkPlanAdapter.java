package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.cnksi.core.adapter.BaseAdapter;
import com.cnksi.core.adapter.ViewHolder;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.WorkPiao;

import java.util.Collection;

/**
 * Created by han on 2016/5/5.
 */
public class WorkPlanAdapter extends BaseAdapter<WorkPiao> {
    public WorkPlanAdapter(Context context, Collection<WorkPiao> data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, WorkPiao item, int position) {
        holder.setText(R.id.tv_work_bdzname, item.bzd_name);
        switch (Integer.parseInt(item.status)) {
            case 1:
                holder.setText(R.id.tv_wor_status, "待签发");
                holder.getView(R.id.ibt_up).setBackgroundResource(R.drawable.work_uploaddone_background);
                ImageView ibtn = holder.getView(R.id.ibt_up);
                ibtn.setImageResource(R.mipmap.ic_uploaddone);
                break;
            case 2:
                holder.setText(R.id.tv_wor_status, "待许可");
                holder.getView(R.id.tv_wor_status).setBackgroundResource(R.drawable.work_status_daixunke_background);
                break;
            case 3:
                holder.setText(R.id.tv_wor_status, "已许可");
                holder.getView(R.id.tv_wor_status).setBackgroundResource(R.drawable.work_status_yixuke_background);
                holder.getView(R.id.ibt_up).setBackgroundResource(R.drawable.work_uploaddone_background);
                ImageView ibtn1 = holder.getView(R.id.ibt_up);
                ibtn1.setImageResource(R.mipmap.ic_uploaddone);
                break;
            case 4:
                holder.setText(R.id.tv_wor_status, "间断中");
                holder.getView(R.id.tv_wor_status).setBackgroundResource(R.drawable.work_status_biangengzhong_background);
                holder.getView(R.id.ibt_up).setBackgroundResource(R.drawable.work_uploaddone_background);
                ImageView ibtn2 = holder.getView(R.id.ibt_up);
                ibtn2.setImageResource(R.mipmap.ic_uploaddone);
                break;
            case 5:
                holder.setText(R.id.tv_wor_status, "变更中");
                holder.getView(R.id.tv_wor_status).setBackgroundResource(R.drawable.work_status_biangengzhong_background);
                break;
            case 6:
                holder.setText(R.id.tv_wor_status, "已终结");
                holder.getView(R.id.tv_wor_status).setBackgroundResource(R.drawable.work_status_yixuke_background);
                break;
            default:
                break;

        }
        holder.setText(R.id.tv_number, "编\t\t\t\t号: " + item.bianhao);
        holder.setText(R.id.tv_piao_zhong, "票\t\t\t\t种: " + item.piaozhong);
        holder.setText(R.id.tv_start_time, "开始时间: " + item.start_work_time);
        holder.setText(R.id.tv_end_work_time, "结束时间: " + item.end_work_time);

    }
}
