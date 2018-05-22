package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.sjjc.R;
import com.cnksi.common.model.Task;
import com.cnksi.sjjc.inter.ItemClickListener;
import com.cnksi.sjjc.util.CoreConfig;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

/**
 * Created by luoxy on 16/4/28.
 */
public class TaskRemindAdapter extends BaseAdapter<Task> {

    private ItemClickListener<Task> itemClickListener;

    public void setItemClickListener(ItemClickListener<Task> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public TaskRemindAdapter(Context context, Collection<Task> data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, final Task item, final int position) {
        // 设置变电站第一个汉字
//        TextView tvType = holder.getView(R.id.tv_task_simple_name);
//        tvType.setText(item.inspection_name.substring(0, 1));

        // 设置上传状态
        ImageView imageUpload = holder.getView(R.id.iv_upload);
        if (item.isFinish()) {
            imageUpload.setVisibility(View.VISIBLE);
            if ("Y".equalsIgnoreCase(item.isUpload)) {
                imageUpload.setImageResource(R.drawable.ic_upload);
            } else {
                imageUpload.setImageResource(R.drawable.ic_no_upload);
            }
        } else {
            imageUpload.setVisibility(View.GONE);
        }
        // // 如果计划巡视时间大于当天则灰色显示
        Date now = Calendar.getInstance().getTime();
        LinearLayout mLLGrayTask = holder.getView(R.id.ll_gray_task);
        if (item.getScheduleTime().after(now)) {
            mLLGrayTask.setVisibility(View.VISIBLE);
            // 不显示文字右边的ic_report图标
        } else {
            mLLGrayTask.setVisibility(View.GONE);
        }
        // 设置巡视任务名称 变电站+巡视类型
        String taskName = item.bdzname + "(" + item.inspection_name + ")";
        holder.setText(R.id.tv_task_name, StringUtils.changePartTextColor(context, taskName, R.color.green_color, taskName.length() - item.inspection_name.length() - 2, taskName.length()).toString());
        // 设置巡视时间
        holder.setText(R.id.tv_inspection_time, DateUtils.getFormatterTime(item.schedule_time, CoreConfig.dateFormat1));

        holder.getRootView().setOnClickListener(v -> {
            if (null != itemClickListener) {
                itemClickListener.itemClick(v, item, position);
            }
        });
        holder.getRootView().setOnLongClickListener(view -> {
            if (null != itemClickListener) {
                itemClickListener.itemLongClick(view, item, position);
            }
            return false;
        });
        holder.setText(R.id.tv_task_simple_name,getFirstChineseChar(item.bdzname) );
    }

    String getFirstChineseChar(String str)
    {
        if (TextUtils.isEmpty(str)) {
            return "";
        } else {
            for (int i = 0, length = str.length(); i < length; i++) {
                if (str.charAt(i) > 0x4e00 && str.charAt(i) < 0x9fa5) {
                    return String.valueOf(str.charAt(i));
                }
            }
        }
        return "";
    }

}
