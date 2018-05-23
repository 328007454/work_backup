package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.SwitchPic;
import com.cnksi.common.model.Task;
import com.cnksi.core.utils.StringUtils;

import org.xutils.common.util.KeyValue;
import org.xutils.db.Selector;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

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







}

