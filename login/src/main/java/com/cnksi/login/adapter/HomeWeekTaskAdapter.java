package com.cnksi.login.adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.enmu.TaskStatus;
import com.cnksi.common.model.Task;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.login.R;

import java.util.List;

/**
 * Created by Mr.K on 2018/7/5.
 */

public class HomeWeekTaskAdapter extends BaseQuickAdapter<Task, BaseViewHolder> {
    int colorDone = Color.parseColor("#78CE61"), colorUndo = Color.parseColor("#F96A6A"), colorDoing = Color.parseColor("#019BFB");

    public HomeWeekTaskAdapter(int layoutResId, @Nullable List<Task> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Task item) {
        TextView txtBdzShort = helper.getView(R.id.tv_bdz_short);
        TextView txtBdzName = helper.getView(R.id.tv_bdz);
        TextView txtTaskStatus = helper.getView(R.id.tv_status);
        TextView txtTaskStartDate = helper.getView(R.id.tv_date);
        View lineView = helper.getView(R.id.line);
        lineView.setVisibility(helper.getAdapterPosition() == getData().size() - 1 ? View.GONE : View.VISIBLE);
        txtBdzShort.setText(getBdzNameShort(item.bdzname));
        Object[] result = getTaskObject(item.inspection, item.status);
        txtBdzShort.setBackground((Drawable) result[0]);
        txtBdzName.setText(item.bdzname+(CharSequence) result[1]);
        txtTaskStatus.setText(getTaskStatus(item));
        txtTaskStartDate.setText("时间：" + DateUtils.getFormatterTime(item.schedule_time));
    }

    private CharSequence getTaskStatus(Task item) {
        CharSequence status = "";
        if (TaskStatus.done.name().equals(item.status)) {
            status = StringUtils.changeTextColor("已完成", colorDone);
        } else if (TaskStatus.doing.name().equals(item.status)) {
            status = StringUtils.changeTextColor("进行中", colorDoing);
        } else {
            status = StringUtils.changeTextColor("未开始", colorUndo);
        }
        return status;
    }

    private Object[] getTaskObject(String inspectionType, String status) {
        Object[] result = new Object[2];
        int color = Color.RED;
        String zhInspectionName = "";
        if (TextUtils.isEmpty(inspectionType)) {
        } else if (inspectionType.contains(InspectionType.full.name())) {
            zhInspectionName = "(" + InspectionType.full.value + ")";
        } else if (inspectionType.contains(InspectionType.professional.name())) {
            zhInspectionName = "(" + InspectionType.professional.value + ")";
        } else if (inspectionType.contains(InspectionType.routine.name())) {
            zhInspectionName = "(" + InspectionType.routine.value + ")";
        } else if (inspectionType.contains(InspectionType.special_xideng.name())) {
            zhInspectionName = "(" + InspectionType.special_xideng.value + ")";
        } else if (inspectionType.contains(InspectionType.special.name())) {
            zhInspectionName = "(" + InspectionType.special.value + ")";
        } else if (inspectionType.contains(InspectionType.maintenance.name())) {
            zhInspectionName = "(" + InspectionType.maintenance.value + ")";
        } else if (inspectionType.contains(InspectionType.switchover.name())) {
            zhInspectionName = "(" + InspectionType.switchover.value + ")";
        } else if (inspectionType.contains(InspectionType.operation.name())) {
            zhInspectionName = "(" + InspectionType.operation.value + ")";
        } else if (inspectionType.contains("workticket")) {
            zhInspectionName = "";
        }
        if (TextUtils.isEmpty(status)) {

        } else if (TextUtils.equals(status, TaskStatus.undo.name())) {
            color = colorUndo;
        } else if (TextUtils.equals(status, TaskStatus.doing.name())) {
            color = colorDoing;
        } else if (TextUtils.equals(status, TaskStatus.done.name())) {
            color = colorDone;
        }


        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(color);
        result[0] = drawable;
        result[1] = zhInspectionName;
        return result;
    }


    private String getBdzNameShort(String bdzName) {
        String bdzShortName = "";
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
