package com.cnksi.bdzinspection.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.SimpleBaseAdapter;
import com.cnksi.bdzinspection.databinding.XsInspectionTaskItemBinding;
import  com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.model.Task;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.StringUtils;
import com.zhy.core.utils.AutoUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 巡视任务Adapter
 *
 * @author Joe
 */
public class TaskRemindAdapter extends SimpleBaseAdapter {


    private Map<String, String> userHashMap = new HashMap<>();

    public TaskRemindAdapter(Context context, List<Task> tasks) {
        super(context,tasks);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Task mTask = (Task) getItem(position);
        XsInspectionTaskItemBinding itemBinding;
        if (convertView == null) {
            itemBinding = XsInspectionTaskItemBinding.inflate(LayoutInflater.from(mContext));
            AutoUtils.autoSize(itemBinding.getRoot());

        } else {
            itemBinding = DataBindingUtil.findBinding(convertView);
        }
        itemBinding.tvTaskSimpleName.setText(getShortName(mTask.bdzname));
        if (InspectionType.day.name().equals(mTask.inspection) || InspectionType.full.name().equals(mTask.inspection)
                || InspectionType.special_nighttime.name().equals(mTask.inspection)) {
            // 颜色一：变电站巡视（全面、日常、夜巡）
            itemBinding.tvTaskSimpleName.setBackgroundResource(R.drawable.xs_task_name_tips_day_background);
        } else if ((mTask.inspection.contains(InspectionType.maintenance.name()))) {
            // 颜色二：定期维护
            itemBinding.tvTaskSimpleName.setBackgroundResource(R.drawable.xs_task_name_tips_regular_maintain_background);
        } else if (mTask.inspection.contains(InspectionType.switchover.name())
                || InspectionType.battery.name().equals(mTask.inspection)) {
            // 颜色三：定期切换、试验
            itemBinding.tvTaskSimpleName.setBackgroundResource(R.drawable.xs_task_name_tips_regular_switch_background);
        } else {
            // 颜色五：其他巡视（特殊、验收、新投）
            itemBinding.tvTaskSimpleName.setBackgroundResource(R.drawable.xs_task_name_tips_other_background);
        }
        // 设置上传状态
        if (mTask.isFinish()) {
            if (itemBinding.ivUpload.getVisibility() == View.GONE) {
                itemBinding.ivUpload.setVisibility(View.VISIBLE);
            }
            itemBinding.ivUpload.setImageResource(
                    "Y".equalsIgnoreCase(mTask.isUpload) ? R.drawable.xs_ic_upload : R.drawable.xs_ic_no_upload);
        } else {
            itemBinding.ivUpload.setVisibility(View.GONE);
        }
        itemBinding.tvPeople.setText("");
        if (InspectionType.routine.name().equalsIgnoreCase(mTask.inspection) || InspectionType.full.name().equalsIgnoreCase(mTask.inspection) || mTask.inspection.contains(InspectionType.special.name()) || InspectionType.professional.name().equalsIgnoreCase(mTask.inspection)) {
            StringBuilder builder = new StringBuilder();
            builder.append(mTask.createAccount);
            if (!TextUtils.isEmpty(mTask.membersAccount)) {
                builder.append(",").append(mTask.membersAccount);
            }
            String[] peopleArray = builder.toString().split(",");
            StringBuilder nameBuilder = new StringBuilder();
            for (int i = 0; i < peopleArray.length; i++) {
                String name = userHashMap.get(peopleArray[i]);
                if (!TextUtils.isEmpty(name)) {
                    nameBuilder.append(name);
                }
                if (peopleArray.length > 1 && i != peopleArray.length - 1) {
                    nameBuilder.append(",");
                }
            }
            itemBinding.tvPeople.setText(TextUtils.isEmpty(nameBuilder.toString()) ? "" : nameBuilder.toString());
        }

        // // 如果计划巡视时间大于当天则灰色显示
        Date now = Calendar.getInstance().getTime();
        if (mTask.getScheduleTime().after(now)) {
            itemBinding.llGrayTask.setVisibility(View.VISIBLE);
            // 不显示文字右边的ic_report图标
        } else {
            itemBinding.llGrayTask.setVisibility(View.GONE);
        }

        // // 设置巡视任务名称 变电站+巡视类型
        String taskName = mTask.bdzname + "(" + mTask.inspection_name + ")";
        itemBinding.tvTaskName.setText(StringUtils.changePartTextColor(mContext, taskName, R.color.xs_green_color,
                taskName.length() - mTask.inspection_name.length() - 2, taskName.length()));
        // 设置巡视时间
        itemBinding.tvInspectionTime.setText(DateUtils.getFormatterTime(mTask.schedule_time, DateUtils.yyyy_MM_dd));
        itemBinding.tvTaskName.append(StringUtils.changeTextColor(mTask.taskOrgin(mTask), Color.RED));
        return itemBinding.getRoot();
    }

    public void setUserMap(Map<String, String> map) {
        this.userHashMap = map;
        notifyDataSetChanged();
    }

    private String getShortName(String bdzName) {
        if (TextUtils.isEmpty(bdzName)) {
            return "变";
        } else {
            for (char c : bdzName.toCharArray()) {
                if (c >= '\u4e00' && c <= '\u9fa5') {
                    return String.valueOf(c);
                }
            }
            return String.valueOf(bdzName.charAt(0));
        }
    }
}