package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.cnksi.core.utils.DateUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.Task;
import com.cnksi.sjjc.databinding.HomeTaskItemBinding;
import com.cnksi.sjjc.enmu.InspectionType;

import java.util.List;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2017/3/24 16:59
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class HomeTaskItemAdapter extends BaseLinearBindingAdapter<HomeTaskItemBinding, Task> {
    int colorDone = Color.parseColor("#01ce7f"), colorUndo = Color.parseColor("#fd5f54");

    public HomeTaskItemAdapter(Context context, List<Task> data, LinearLayout container) {
        super(context, data, container, R.layout.home_task_item);
    }

    @Override
    public void convert(HomeTaskItemBinding binding, Task item, int position) {
        binding.line.setVisibility(position == getCount() - 1 ? View.GONE : View.VISIBLE);

        binding.tvDate.setText(DateUtils.getFormatterTime(item.schedule_time));
        boolean isDone = Task.TaskStatus.done.name().equals(item.status);
        binding.tvStatus.setText(isDone ? "已完成" : "待执行");
        binding.tvStatus.setTextColor(isDone ? colorDone : colorUndo);
        binding.tvBdzShort.setText(getShortName(item.bdzname));

        Object[] result = getTaskObject(item.inspection);
        binding.tvBdzShort.setBackground((Drawable) result[0]);
        binding.tvBdz.setText(item.bdzname + result[1]);
    }

    private Object[] getTaskObject(String inspectionType) {
        Object[] result = new Object[2];
        int color = Color.RED;
        String zhInspectionName = "";
        if (TextUtils.isEmpty(inspectionType)) {
        } else if (inspectionType.contains(InspectionType.full.name())) {
            color = 0xff2db2ff;
            zhInspectionName = "("+InspectionType.full.value+")";
        } else if (inspectionType.contains(InspectionType.routine.name())) {
            color = 0xffff7356;
            zhInspectionName = "("+InspectionType.routine.value+")";
        } else if (inspectionType.contains(InspectionType.special_xideng.name())) {
            color = 0xff889fff;
            zhInspectionName ="("+ InspectionType.special_xideng.value+")";
        } else if (inspectionType.contains(InspectionType.special.name())) {
            color = 0xff89d300;
            zhInspectionName ="("+ InspectionType.special.value+")";
        } else if (inspectionType.contains(InspectionType.maintenance.name())) {
            color = 0xff01c3d5;
            zhInspectionName = "("+InspectionType.maintenance.value+")";
        } else if (inspectionType.contains(InspectionType.switchover.name())) {
            color = 0xffffb400;
            zhInspectionName ="("+ InspectionType.switchover.value+")";
        } else if (inspectionType.contains(InspectionType.operation.name())) {
            color = 0xffb962e2;
            zhInspectionName = "("+InspectionType.operation.value+")";
        } else if (inspectionType.contains("workticket")) {
            color = 0xfff8965b;
            zhInspectionName = "(倒闸操作)";
        }
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        result[0] = drawable;
        result[1] = zhInspectionName;
        return result;
    }


    private String getShortName(String bdzName) {
        if (TextUtils.isEmpty(bdzName)) {
            return "变";
        } else return String.valueOf(bdzName.charAt(0));
    }


}
