package com.cnksi.bdzinspection.utils;

import android.text.TextUtils;

import com.cnksi.bdzinspection.daoservice.SwitchMenuService;
import com.cnksi.bdzinspection.model.SwitchMenu;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.enmu.TaskStatus;
import com.cnksi.common.model.Task;
import com.cnksi.common.utils.DateCalcUtils;
import com.cnksi.common.utils.MyUUID;
import com.cnksi.core.common.ExecutorManager;
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
           ExecutorManager.executeTask(() -> {
               try {
                   Task task = TaskService.getInstance().findById(taskId);
                   String startTime = null == task ? DateUtils.getCurrentLongTime() : (TextUtils.isEmpty(task.schedule_time) ? DateUtils.getCurrentLongTime() : task.schedule_time);
                   SwitchMenu menu = SwitchMenuService.getInstance().findByBdz(task.bdzid, type);
                   if (null != task && menu != null && !TextUtils.isEmpty(menu.cycle)) {
                       String nextTime = DateCalcUtils.getAfterMonth(DateUtils.getFormatterTime(startTime, DateUtils.yyyy_MM_dd_HH_mm_ss), Integer.valueOf(menu.cycle));
                       Task newTask = new Task(MyUUID.id(4), task.bdzid, task.bdzname, task.inspection, task.inspection_name, nextTime, TaskStatus.undo.name(), task.createAccount, null);
                       TaskService.getInstance().saveOrUpdate(newTask);
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
