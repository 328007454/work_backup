package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.model.DefectRecord;
import com.cnksi.bdzinspection.model.Report;
import com.cnksi.bdzinspection.model.StandardSwitchover;
import com.cnksi.bdzinspection.model.SwitchPic;
import com.cnksi.bdzinspection.model.Task;
import com.cnksi.bdzinspection.utils.CommonUtils;
import com.cnksi.bdzinspection.utils.Config.TaskStatus;
import com.cnksi.xscore.xsutils.StringUtils;

import org.xutils.common.util.KeyValue;
import org.xutils.db.Selector;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import static com.cnksi.bdzinspection.application.XunshiApplication.getDbUtils;

/**
 * 巡检任务的数据查询
 *
 * @author terry
 */
public class TaskService {

    public static TaskService mInstance;

    private TaskService() {
    }

    public static TaskService getInstance() {
        if (mInstance == null) {
            mInstance = new TaskService();
        }
        return mInstance;
    }


    /**
     * 根据任务Task id 查询任务
     *
     * @param idValue
     * @return
     */
    public Task findById(Object idValue) {
        Task task = null;
        try {
            task = getDbUtils().findById(Task.class, idValue);
        } catch (DbException e) {
            e.printStackTrace();
        }

        return task;
    }

    /**
     * 新建任务
     *
     * @param task
     */
    public void save(Task task) {
        try {
            getDbUtils().save(task);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    /**
     * 查询任务计划的状态
     *
     * @param currentTaskId
     * @return
     */
    public String getTaskStatus(String currentTaskId) {

        Task task = null;
        try {
            task = getDbUtils().findById(Task.class, currentTaskId);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (task != null && TaskStatus.done.name().equalsIgnoreCase(task.status)) {
            return "已完成";
        }
        return "未完成";
    }

    /**
     * 查询任务计划的状态
     *
     * @param currentTaskId
     * @return true is finish
     */
    public boolean getTaskStatusForBoolean(String currentTaskId) {

        Task task = null;
        try {
            task = getDbUtils().findById(Task.class, currentTaskId);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (task != null && TaskStatus.done.name().equalsIgnoreCase(task.status)) {
            return true;
        }
        return false;
    }

    /**
     * 删除计划任务 和已经巡视的报告数据
     *
     * @param idValue
     */
    public boolean deleteTaskAndReportById(Object idValue) {
        Class<?>[] tableClassArray = {Report.class, DefectRecord.class, SwitchPic.class};
        String[] tableClassId = {Report.REPORTID, DefectRecord.REPORTID, SwitchPic.REPORTID};
        boolean isSuccess = false;
        try {
//            getDbUtils().deleteById(Task.class, idValue);
            getDbUtils().update(Task.class, WhereBuilder.b(Task.TASKID, "=", idValue), new KeyValue(Task.DLT, "1"));
            Report mReport = getDbUtils().selector(Report.class).where(Report.TASK_ID, "=", idValue).findFirst();
            if (mReport != null) {
                for (int i = 0, count = tableClassArray.length; i < count; i++) {
                    getDbUtils().delete(tableClassArray[i], WhereBuilder.b(tableClassId[i], "=", mReport.reportid));
                }
            }
            isSuccess = true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    /**
     * 标记是否不上传计划任务
     */
    public void isUploadCurrentTask(Task mTask, boolean IsUpload) {
        try {
            getDbUtils().update(Report.class, WhereBuilder.b(Report.TASK_ID, "=", mTask.taskid), new KeyValue(Report.IS_UPLOAD, (mTask.isUpload = (IsUpload ? "Y" : "N"))));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询数据抄录情况
     */
    public long queryCopyData(String currentReportId) {
        long count = 0;
        try {
            Selector selector = XunshiApplication.getDbUtils().selector(DefectRecord.class).expr(DefectRecord.VAL + " is not null").and(DefectRecord.REPORTID, "=", currentReportId);
            count = selector.count();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 查询变电站抄录总数
     */
    public long queryCopyDataTatal(String bdzId) {
        long count = 0;
        SqlInfo sqlInfo = new SqlInfo("select count(1) as allCopyCount from device_standards ds left join device_part dp on ds.duid = dp.duid left join device d on d.deviceid = dp.deviceid where d.bdzid = '" + bdzId + "' and ds.resulttype='1' and ds.dlt<>'1' and dp.dlt<>'1' and d.dlt<>'1'");
        try {
            DbModel mDbModel = getDbUtils().findDbModelFirst(sqlInfo);
            if (mDbModel != null) {
                count = mDbModel.getLong("allCopyCount");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return count + queryCopuDataTotalDB(bdzId);
    }

    public long queryCopuDataTotalDB(String bdzId) {
        long count = 0;
        String sql = "SELECT count(1) as allCopyCount FROM device d LEFT JOIN device_unit du ON d.dtid = du.dtid LEFT JOIN standards s ON s.duid = du.duid WHERE d.bdzid = ? AND s.resulttype = 1 AND d.dlt <> 1 AND du.dlt <> 1 AND s.dlt <> 1";
        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("", bdzId));
        try {
            DbModel mDbModel = getDbUtils().findDbModelFirst(sqlInfo);
            if (mDbModel != null) {
                count = mDbModel.getLong("allCopyCount");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return count;
    }


    /**
     * 完成任务
     */
    public boolean finishTask(String taskid) {

        if (StringUtils.isEmpty(taskid)) {
            return false;
        }
        String sql = "update task set STATUS='done' where taskid='" + taskid + "'";
        try {
            getDbUtils().execNonQuery(sql);
            return true;
        } catch (Exception e) {

            return false;// TODO: handle exception
        }

    }

    /**
     * 完成任务
     */
    public long getSwitchOverCopyTotal(String type, String bdzId) {
        long total = 0;
        List<StandardSwitchover> standardSwitchOvers = null;
        try {
            standardSwitchOvers = getDbUtils().selector(StandardSwitchover.class).where(StandardSwitchover.BDZID, "=", bdzId).and(StandardSwitchover.KIND, "=", type).and(StandardSwitchover.ISCOPY, "=", "1").and(StandardSwitchover.DLT, "!=", "1").findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (null == standardSwitchOvers) {
            total = 0;
        } else {
            total = standardSwitchOvers.size();
        }
        return total;
    }

    /**
     * 查询同一天相同变电站，相同类型下的任务计划个数
     *
     * @param bdzId      变电站id
     * @param chooseTime 任务选择时间
     * @param name       巡视类型
     */


    public List<Task> getCountSameTask(String name, String bdzId, String chooseTime, String loginCounts) {
        String[] accounts = loginCounts.split(",");
        String extraSql = null;
        List<Task> tasks = null;
        if (accounts.length > 0) {
            extraSql = " and " + CommonUtils.buildWhereTaskContainMe(accounts);
        } else {
            extraSql = "";
        }
        try {
            tasks = XunshiApplication.getDbUtils().selector(Task.class).where(Task.DLT, "=", "0").and(Task.INSPECTION, "=", name).and(Task.STATUS, "=", "undo").and(Task.BDZID, "=", bdzId)
                    .expr(extraSql + " and (select datetime('" + chooseTime + "','+24 hour'))> schedule_time and schedule_time >= (select datetime('" + chooseTime + "'))").findAll();
            if (null == tasks)
                return new ArrayList<Task>();
            return tasks;
        } catch (DbException e) {
            e.printStackTrace();
            return new ArrayList<Task>();
        }
    }

}

