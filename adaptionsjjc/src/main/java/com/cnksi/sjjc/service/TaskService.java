package com.cnksi.sjjc.service;

import android.text.TextUtils;

import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.bean.DefectRecord;
import com.cnksi.sjjc.bean.Report;
import com.cnksi.sjjc.bean.SwitchPic;
import com.cnksi.sjjc.bean.Task;
import com.cnksi.sjjc.bean.TaskExtend;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

/**
 * Created by han on 2016/6/8.
 */
public class TaskService {

    private static TaskService instance;

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
            CustomApplication.getDbManager().deleteById(Task.class, idValue);
            Report mReport = CustomApplication.getDbManager().selector(Report.class).where(Report.TASK_ID, "=", idValue).findFirst();
            if (mReport != null) {
                for (int i = 0, count = tableClassArray.length; i < count; i++) {
                    CustomApplication.getDbManager().delete(tableClassArray[i], WhereBuilder.b(tableClassId[i], "=", mReport.reportid));
                }
            }
            isSuccess = true;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    /**
     * 从当前的任务扩展表中查询当前检测类型
     */
    public TaskExtend getCurrentTask(String taskId) {
        TaskExtend task = null;
        try {
            task = CustomApplication.getDbManager().selector(TaskExtend.class).where(TaskExtend.TASK_ID, "=", taskId).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return task;
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
            task = CustomApplication.getDbManager().findById(Task.class, currentTaskId);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (task != null && Task.TaskStatus.done.name().equalsIgnoreCase(task.status)) {
            return "已完成";
        }
        return "未完成";
    }

    /**
     * 查询数据抄录情况
     */
    public long queryCopyData(String currentReportId) {
        long count = 0;
        try {

            count = CustomApplication.getDbManager().selector(DefectRecord.class).expr(DefectRecord.VAL + " is not null").and(DefectRecord.DLT, "<>", "1").and(DefectRecord.REPORTID, "=", currentReportId).count();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 查询应抄录总数（避雷针部分）
     * @param bdzId
     * @param filterName  设备过滤名字
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
            DbModel mDbModel = CustomApplication.getDbManager().findDbModelFirst(sqlInfo);
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
     * @param bdzId
     * @param filter  设备过滤条件（sql格式）
     * @param filterStandard 标准过滤名字
     * @return
     */
    public long queryCopyDataTatalPress(String bdzId,String filter,String filterStandard) {
        long count = 0;
        String sql="select count(*) as rs from standards s left JOIN device_unit du on s.duid = du.duid " +
                "LEFT JOIN device_type dt on dt.dtid = du.dtid " +
                "left JOIN device d on d.dtid = dt.dtid " +
                " where  s.resulttype = '1' and s.dlt='0' and d.bdzid=?  and d.device_type='one' ";
        if (!TextUtils.isEmpty(filter))
        {
            sql=sql+" "+filter;
        }
        if (!TextUtils.isEmpty(filterStandard))
        {
            sql=sql+" and s.description like '%"+filterStandard+"%'";
        }
        SqlInfo sqlInfo=new SqlInfo(sql);
        sqlInfo.addBindArg(new KeyValue("bdzId",bdzId));
        try {
            DbModel mDbModel = CustomApplication.getDbManager().findDbModelFirst(sqlInfo);
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
            CustomApplication.getDbManager().update(Report.class, WhereBuilder.b(Report.TASK_ID, "=", mTask.taskid), new KeyValue(Report.IS_UPLOAD, mTask.isUpload = (IsUpload ? "Y" : "N")));
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    public Task getTask(String taskId) throws DbException {
        return CustomApplication.getDbManager().findById(Task.class, taskId);
    }
}