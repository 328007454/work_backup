package com.cnksi.bdzinspection.utils;

import android.text.TextUtils;

import com.cnksi.bdzinspection.daoservice.SwitchMenuService;
import com.cnksi.bdzinspection.model.SwitchMenu;
import com.cnksi.common.CommonApplication;
import com.cnksi.common.daoservice.BdzService;
import com.cnksi.common.daoservice.ReportService;
import com.cnksi.common.daoservice.TaskExtendService;
import com.cnksi.common.daoservice.TaskService;
import com.cnksi.common.enmu.TaskStatus;
import com.cnksi.common.model.Bdz;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.Task;
import com.cnksi.common.model.TaskExtend;
import com.cnksi.common.utils.DateCalcUtils;
import com.cnksi.common.utils.MyUUID;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DateUtils;

import org.xutils.ex.DbException;

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

    Task newTask;

    public void createNewTaskByPeriod(String taskId, String type, String inspectionName) {
        boolean goOn = !TextUtils.isEmpty(type) && (type.contains("maintenance") || type.contains("switchover"));
        if (goOn) {
            ExecutorManager.executeTask(() -> {
                try {
                    Task task = TaskService.getInstance().findById(taskId);
                    String startTime = null == task ? DateUtils.getCurrentLongTime() : (TextUtils.isEmpty(task.schedule_time) ? DateUtils.getCurrentLongTime() : task.schedule_time);
                    SwitchMenu menu = SwitchMenuService.getInstance().findByBdz(task.bdzid, type);
                    if (null != task && menu != null && !TextUtils.isEmpty(menu.cycle)) {
                        String nextTime = DateCalcUtils.getAfterMonth(DateUtils.getFormatterTime(startTime, DateUtils.yyyy_MM_dd_HH_mm_ss), Integer.valueOf(menu.cycle));
                        newTask = new Task(MyUUID.id(4), task.bdzid, task.bdzname, task.inspection, task.inspection_name, nextTime, TaskStatus.undo.name(), task.createAccount, null, task.deptId);
                        TaskService.getInstance().saveOrUpdate(newTask);
                    }
                    creatReport(taskId, newTask, type, inspectionName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            return;
        }
    }

    private void creatReport(String preTaskId, Task task, String currentInspectionType, String typeName) {

        Bdz bdz;
        TaskExtend taskExpand = null;
        SwitchMenu switchMenu = null;
        Report preReport = null;
        try {
            bdz = BdzService.getInstance().findById(task.bdzid);
            preReport = ReportService.getInstance().getReportByTask(preTaskId);
            TaskExtend taskExtend = TaskExtendService.getInstance().findTaskExtendByTaskId(preTaskId);
            switchMenu = SwitchMenuService.getInstance().findByBdz(bdz.bdzid, currentInspectionType);
            if (typeName.contains("电压") || typeName.contains("蓄电池") || typeName.contains("内阻")) {
                taskExpand = new TaskExtend(task.taskid);
                if (taskExtend != null) {
                    taskExpand.sbjcIsAllCheck = taskExtend.sbjcIsAllCheck;
                } else {
                    taskExpand.sbjcIsAllCheck = 0;
                }
            }
            Report report = new Report(task, bdz, switchMenu == null ? null : switchMenu.repSwithoverId);
            report.checkType = preReport.checkType;
            CommonApplication.getInstance().getDbManager().saveOrUpdate(report);
            CommonApplication.getInstance().getDbManager().saveOrUpdate(taskExpand);
        } catch (DbException e) {
            e.printStackTrace();
        }

    }


}
