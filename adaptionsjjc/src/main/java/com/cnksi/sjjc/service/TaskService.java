package com.cnksi.sjjc.service;

import android.text.TextUtils;

import com.cnksi.sjjc.bean.DefectRecord;
import com.cnksi.sjjc.bean.Report;
import com.cnksi.sjjc.bean.SwitchPic;
import com.cnksi.sjjc.bean.Task;
import com.cnksi.sjjc.bean.TaskStatistic;
import com.cnksi.sjjc.enmu.InspectionType;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by han on 2016/6/8.
 */
public class TaskService extends BaseService<Task> {

    private static TaskService instance;

    private TaskService() {
        super(Task.class);
    }

    public static TaskService getInstance() {
        if (null == instance) instance = new TaskService();
        return instance;
    }

    /* 删除计划任务 和已经巡视的报告数据
    *
    * @param idValue
    */
    public boolean deleteTaskAndReportById(Object idValue) {
        Class<?>[] tableClassArray = {Report.class, DefectRecord.class, SwitchPic.class};
        String[] tableClassId = {Report.REPORTID, DefectRecord.REPORTID, SwitchPic.REPORTID};
        boolean isSuccess = false;
        try {
            logicDeleteById(idValue);
            Report mReport = getInstance(Report.class).selector().and(Report.TASK_ID, "=", idValue).findFirst();
            if (mReport != null) {
                for (int i = 0, count = tableClassArray.length; i < count; i++) {
                    logicDelete(tableClassArray[i], WhereBuilder.b(tableClassId[i], "=", mReport.reportid));
                }
            }
            isSuccess = true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }


    /**
     * 查询数据抄录情况
     */
    public long queryCopyData(String currentReportId) {
        long count = 0;
        try {

            count = selector().expr(" and " + DefectRecord.VAL + " is not null").and(DefectRecord.REPORTID, "=", currentReportId).count();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 查询应抄录总数（避雷针部分）
     *
     * @param bdzId
     * @param filterName     设备过滤名字
     * @param filterStandard 标准过滤名字
     * @return
     */
    public long queryCopyDataTatal(String bdzId, String filterName, String filterStandard) {
        long count = 0;
        String sql = "select count(*) as rs from standards s left JOIN device_unit du on s.duid = du.duid " +
                "LEFT JOIN device_type dt on dt.dtid = du.dtid " +
                "left JOIN device d on d.dtid = dt.dtid " +
                " where  s.resulttype = '1' and s.dlt='0' and d.bdzid=? and d.device_type='one' ";
        if (!TextUtils.isEmpty(filterName)) {
            sql = sql + " and d.name like '%" + filterName + "%'";
        }
        if (!TextUtils.isEmpty(filterStandard)) {
            sql = sql + " and s.description like '%" + filterStandard + "%'";
        }
        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("bdzId", bdzId));
        try {
            DbModel mDbModel = findDbModelFirst(sqlInfo);
            if (mDbModel != null) {
                count = mDbModel.getLong("rs");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 查询应抄录总数（压力部分）
     *
     * @param bdzId
     * @param filter         设备过滤条件（sql格式）
     * @param filterStandard 标准过滤名字
     * @return
     */
    public long queryCopyDataTatalPress(String bdzId, String filter, String filterStandard) {
        long count = 0;
        String sql = "select count(*) as rs from standards s left JOIN device_unit du on s.duid = du.duid " +
                "LEFT JOIN device_type dt on dt.dtid = du.dtid " +
                "left JOIN device d on d.dtid = dt.dtid " +
                " where  s.resulttype = '1' and s.dlt='0' and d.bdzid=?  and d.device_type='one' ";
        if (!TextUtils.isEmpty(filter)) {
            sql = sql + " " + filter;
        }
        if (!TextUtils.isEmpty(filterStandard)) {
            sql = sql + " and s.description like '%" + filterStandard + "%'";
        }
        SqlInfo sqlInfo = new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("bdzId", bdzId));
        try {
            DbModel mDbModel = findDbModelFirst(sqlInfo);
            if (mDbModel != null) {
                count = mDbModel.getLong("rs");
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 标记是否不上传计划任务
     */
    public void isUploadCurrentTask(Task mTask, boolean IsUpload) {
        try {
            update(WhereBuilder.b(Report.TASK_ID, "=", mTask.taskid), new KeyValue(Report.IS_UPLOAD, mTask.isUpload = (IsUpload ? "Y" : "N")));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    public TaskStatistic getTaskStatistic(String inspectionType) {
        TaskStatistic result = new TaskStatistic();
        String inspectionExpr = TextUtils.isEmpty(inspectionType) ? " and (inspection like '%" + InspectionType.routine.name() + "%' or inspection like '%" + InspectionType.full.name() + "%' or inspection like '%" + InspectionType.special.name() + "%') " : " and inspection like '%" + inspectionType + "%' ";
        String todayTimeExpr = " and schedule_time BETWEEN datetime('now','localtime','start of day') AND datetime('now','localtime','start of day','+1 day','-1 second') ";
        String mothTimeExpr = " and schedule_time BETWEEN datetime('now','localtime','start of month') AND datetime('now','localtime','start of month','+1 month','-1 second') ";
        String doneExpr = " and status='" + Task.TaskStatus.done.name() + "' ";
        long count = 0;
        try {
            //今日维护任务
            count = selector().expr(inspectionExpr).expr(todayTimeExpr).count();
            result.setTodayTaskCount(count);
        } catch (DbException e) {
            e.printStackTrace();
        }
        try {
            count = selector().expr(doneExpr).expr(inspectionExpr).expr(todayTimeExpr).count();
            result.setTodayTaskFinish(count);
        } catch (DbException e) {
            e.printStackTrace();

        }
        try {
            //今日维护任务
            count = selector().expr(inspectionExpr).expr(mothTimeExpr).count();
            result.setMonthTaskCount(count);
        } catch (DbException e) {
            e.printStackTrace();
        }
        try {
            count = selector().expr(doneExpr).expr(inspectionExpr).expr(mothTimeExpr).count();
            result.setMonthTaskFinish(count);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Task> getUnDoTask(String inspectionType) {
        List<Task> tasks = null;
        try {
            tasks = selector().and(Task.INSPECTION, "=", inspectionType).and(Task.STATUS, "=", "undo").findAll();
            if (null == tasks)
                tasks = new ArrayList<>();
        } catch (DbException e) {
            e.printStackTrace();
            if (null == tasks)
                tasks = new ArrayList<>();
        }
        return tasks;
    }

    public List<Task> getUnDoSpecialTask(String inspectionType) {
        List<Task> tasks = null;
        try {
            tasks = selector().expr(" and inspection like '%special%' and  inspection <> 'special_xideng'").and(Task.STATUS, "=", "undo").findAll();
            if (null == tasks)
                tasks = new ArrayList<>();
        } catch (DbException e) {
            e.printStackTrace();
            if (null == tasks)
                tasks = new ArrayList<>();
        }
        return tasks;
    }

    public List<Task> getAllTask() {
        List<Task> tasks = new ArrayList<>();
        try {
            tasks = findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public List<Task> getFinishedTask() {
        List<Task> tasks = new ArrayList<>();
        try {
            tasks = selector().where(Task.STATUS, "=", "undo").findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public List<Task> findOperationTaskByLimit(int limit) {

        try {
            return selector().and(Task.TYPE, "=", InspectionType.operation.name()).orderBy(Task.SCHEDULE_TIME, true).limit(limit > 0 ? limit : 1).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Task> findTaskListByLimit(int limit, String... inspections) {
        StringBuilder expr = new StringBuilder();
        expr.append(" and (");
        for (String inspection : inspections) {
            expr.append(" inspection like '%").append(inspection).append("%'  ").append("or");
        }
        expr.delete(expr.length() - 2, expr.length());
        expr.append(") ");
        try {
            return selector().expr(inspections.length > 0 ? expr.toString() : "").orderBy(Task.SCHEDULE_TIME, true).limit(limit > 0 ? limit : 1).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }


    public float statisticProgress(String... inspections) {
        DbModel model;
        StringBuilder expr = new StringBuilder();
        expr.append(" and (");
        for (String inspection : inspections) {
            expr.append(" inspection like '%").append(inspection).append("%'  ").append("or");
        }
        expr.delete(expr.length() - 2, expr.length());
        expr.append(")");
        try {
            model = selector().select(" sum(case when `status`='done' then 1.0 else 0 end)/count(1) as progress ").expr(inspections.length > 0 ? expr.toString() : " ").findFirst();
            String str = model.getString("progress");
            if (!TextUtils.isEmpty(str))
                return Float.valueOf(str);
        } catch (DbException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0f;
    }

    public List<Task> findWorkTicketTask() {
        try {
            //电子化操作票属于PAD端功能，与服务器无任何关联。此处直接查询并组装成界面想要的Task数据。
            List<DbModel> models = getDbManager().findDbModelAll(new SqlInfo("SELECT * FROM operate_tick limit 3"));
            if (models != null & models.size() > 0) {
                List<Task> tasks = new ArrayList<>();
                for (DbModel model : models) {
                    Task t = new Task();
                    t.taskid = model.getString("id");
                    t.bdzname = model.getString("task");
                    t.schedule_time = model.getString("time_operate_start");
                    t.status = model.getString("status");
                    t.inspection_name = "倒闸操作";
                    t.inspection = "workticket";
                    tasks.add(t);
                }
                return tasks;
            }
        } catch (DbException e) {

        }
        return null;
    }
}
