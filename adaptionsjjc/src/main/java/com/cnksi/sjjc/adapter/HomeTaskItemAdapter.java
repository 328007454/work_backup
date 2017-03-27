package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.StringUtils;
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
    View noDataView;

    public HomeTaskItemAdapter(Context context, List<Task> data, LinearLayout container) {
        super(context, data, container, R.layout.home_task_item);
        noDataView = LayoutInflater.from(context).inflate(R.layout.home_no_task_layout, null, false);
    }

    @Override
    public void convert(HomeTaskItemBinding binding, Task item, int position) {
        binding.line.setVisibility(position == 2 ? View.GONE : View.VISIBLE);
        binding.tvDate.setText("时间：" + DateUtils.getFormatterTime(item.schedule_time));
        binding.tvStatus.setText(getTaskStatus(item));
        binding.tvBdzShort.setText(getShortName(item.bdzname));
        Object[] result = getTaskObject(item.inspection);
        binding.tvBdzShort.setBackground((Drawable) result[0]);
        binding.tvBdz.setText(item.bdzname + result[1]);
    }


    private CharSequence getTaskStatus(Task task) {
        CharSequence status = "";
        if ("workticket".equals(task.inspection)) {
            if ("wwc".equals(task.status))
                status = StringUtils.changeTextColor("未完成", colorUndo);
            else if ("yzt".equals(task.status))
                status = StringUtils.changeTextColor("已暂停", colorUndo);

            else if ("ywc".equals(task.status))
                status = StringUtils.changeTextColor("已完成", colorDone);
            else if ("ytz".equals(task.status))
                status = StringUtils.changeTextColor("已停止", colorUndo);
            else status = StringUtils.changeTextColor("待审核", colorUndo);
        } else {
            if (Task.TaskStatus.done.name().equals(task.status)) {
                status = StringUtils.changeTextColor("已完成", colorDone);
            } else status = StringUtils.changeTextColor("待执行", colorUndo);
        }

        return status;
    }

    private Object[] getTaskObject(String inspectionType) {
        Object[] result = new Object[2];
        int color = Color.RED;
        String zhInspectionName = "";
        if (TextUtils.isEmpty(inspectionType)) {
        } else if (inspectionType.contains(InspectionType.full.name())) {
            color = 0xff2db2ff;
            zhInspectionName = "(" + InspectionType.full.value + ")";
        } else if (inspectionType.contains(InspectionType.routine.name())) {
            color = 0xffff7356;
            zhInspectionName = "(" + InspectionType.routine.value + ")";
        } else if (inspectionType.contains(InspectionType.special_xideng.name())) {
            color = 0xff889fff;
            zhInspectionName = "(" + InspectionType.special_xideng.value + ")";
        } else if (inspectionType.contains(InspectionType.special.name())) {
            color = 0xff89d300;
            zhInspectionName = "(" + InspectionType.special.value + ")";
        } else if (inspectionType.contains(InspectionType.maintenance.name())) {
            color = 0xff01c3d5;
            zhInspectionName = "(" + InspectionType.maintenance.value + ")";
        } else if (inspectionType.contains(InspectionType.switchover.name())) {
            color = 0xffffb400;
            zhInspectionName = "(" + InspectionType.switchover.value + ")";
        } else if (inspectionType.contains(InspectionType.operation.name())) {
            color = 0xffb962e2;
            zhInspectionName = "(" + InspectionType.operation.value + ")";
        } else if (inspectionType.contains("workticket")) {
            color = 0xfff8965b;
            zhInspectionName = "";
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

    @Override
    public void notifyDataSetChanged() {
        container.removeView(noDataView);
        super.notifyDataSetChanged();
        if (container.getChildCount() == 0)
            container.addView(noDataView);
    }
}
