package com.cnksi.bdzinspection.utils;

import android.text.TextUtils;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.model.SwitchMenu;
import com.cnksi.common.enmu.TaskStatus;
import com.cnksi.common.model.Task;
import com.cnksi.common.utils.DateCalcUtils;
import com.cnksi.core.utils.DateUtils;

/**
 * Created by kkk on 2018/3/12.
 */

public class PushNewTaskUtil {
    private static PushNewTaskUtil taskUtil;

    public static PushNewTaskUtil getTaskUtilInstance() {

        if (null == taskUtil) {
            taskUtil = new PushNewTaskUtil();
        }
        return taskUtil;
    }


    public void createNewTaskByPeriod(String taskId, String type) {
        boolean goOn = !TextUtils.isEmpty(type) && (type.contains("maintenance") || type.contains("switchover"));
        if (goOn) {
            XunshiApplication.getFixedThreadPoolExecutor().execute(() -> {
                try {
                    Task task = XunshiApplication.getDbUtils().selector(Task.class).where(Task.TASKID, "=", taskId).and(Task.DLT, "<>", 1).findFirst();
                    String startTime = null==task ?DateUtils.getCurrentLongTime():(TextUtils.isEmpty(task.schedule_time)?DateUtils.getCurrentLongTime():task.schedule_time);
                    SwitchMenu menu = XunshiApplication.getDbUtils().selector(SwitchMenu.class).where(SwitchMenu.BDZID, "=", task.bdzid).and(SwitchMenu.DLT, "=", 0).and(SwitchMenu.K, "=", type).findFirst();
                    if (null != task && menu != null && !TextUtils.isEmpty(menu.cycle)) {
                        String nextTime = DateCalcUtils.getAfterMonth(DateUtils.getFormatterTime(startTime, DateUtils.yyyy_MM_dd_HH_mm_ss), Integer.valueOf(menu.cycle));
                        Task newTask = new Task(MyUUID.id(4), task.bdzid, task.bdzname, task.inspection, task.inspection_name, nextTime, TaskStatus.undo.name(), task.createAccount, null);
                        XunshiApplication.getDbUtils().saveOrUpdate(newTask);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            return;
        }
    }


}
