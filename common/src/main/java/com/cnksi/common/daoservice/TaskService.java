package com.cnksi.common.daoservice;

import android.text.TextUtils;

import com.cnksi.common.Config;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.enmu.TaskStatus;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.model.Report;
import com.cnksi.common.model.SwitchPic;
import com.cnksi.common.model.Task;
import com.cnksi.common.model.vo.TaskStatistic;
import com.cnksi.common.utils.CommonUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.StringUtils;

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
        if (null == instance) {
            instance = new TaskService();
        }
        return instance;
    }


    @Override
    public Task findById(Object id) {
        try {
            return super.findById(id);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
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

            count = getDbManager().selector(DefectRecord.class).where(DefectRecord.DLT,"=",0).expr(" and " + DefectRecord.VAL + " is not null").and(DefectRecord.REPORTID, "=", currentReportId).count();
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


    private String getUserExpr() {
        String currentAcounts = PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, "");
        String[] accoutArray = currentAcounts.split(",");
        String accountExpr;
        if (accoutArray.length > 1) {
            accountExpr = "and (pms_jh_source ='pms_pc' or  create_account like ('%" + accoutArray[0] + "%') or create_account like ('%" + accoutArray[1] + "%')or members_account like('%" + accoutArray[0] + "%')  or members_account like('%" + accoutArray[1] + "%') or create_account is NULL or create_account = '')";
        } else {
            accountExpr = "and ( pms_jh_source ='pms_pc'  or  create_account like ('%" + currentAcounts + "%') or members_account like('%" + currentAcounts + "%') or create_account is NULL or create_account = '')";
        }
        return accountExpr;
    }

    public TaskStatistic getTaskStatistic(String inspectionType) {
        TaskStatistic result = new TaskStatistic();
        String inspectionExpr = TextUtils.isEmpty(inspectionType) ? " and (inspection like '%" + InspectionType.professional.name() + "%' or inspection like '%" + InspectionType.routine.name() + "%' or inspection like '%" + InspectionType.full.name() + "%' or inspection like '%" + InspectionType.special.name() + "%') " : " and inspection like '%" + inspectionType + "%' ";
        String todayTimeExpr = " and schedule_time BETWEEN datetime('now','localtime','start of day') AND datetime('now','localtime','start of day','+1 day','-1 second') ";
        String mothTimeExpr = " and schedule_time BETWEEN datetime('now','localtime','start of month') AND datetime('now','localtime','start of month','+1 month','-1 second') ";
        String doneExpr = " and status='" + TaskStatus.done.name() + "' ";
        long count = 0;
        String accountExpr = getUserExpr();
        try {
            //今日维护任务
            count = selector().expr(inspectionExpr).expr(todayTimeExpr).expr(accountExpr).count();
            result.setTodayTaskCount(count);
        } catch (DbException e) {
            e.printStackTrace();
        }
        try {
            count = selector().expr(doneExpr).expr(inspectionExpr).expr(todayTimeExpr).expr(accountExpr).count();
            result.setTodayTaskFinish(count);
        } catch (DbException e) {
            e.printStackTrace();

        }
        try {
            //今日维护任务
            count = selector().expr(inspectionExpr).expr(mothTimeExpr).expr(accountExpr).count();
            result.setMonthTaskCount(count);
        } catch (DbException e) {
            e.printStackTrace();
        }
        try {
            count = selector().expr(doneExpr).expr(inspectionExpr).expr(mothTimeExpr).expr(accountExpr).count();
            result.setMonthTaskFinish(count);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Task> getUnDoTask(String inspectionType) {
        List<Task> tasks = null;
        try {
            tasks = selector().and(Task.INSPECTION, "=", inspectionType).and(Task.STATUS, "=", "undo").expr("and (pms_jh_source ='pms_pc' or " + buildWhereTaskContainMe() + " or create_account is NULL or create_account = '')").findAll();
            if (null == tasks) {
                tasks = new ArrayList<>();
            }
        } catch (DbException e) {
            e.printStackTrace();
            if (null == tasks) {
                tasks = new ArrayList<>();
            }
        }
        return tasks;
    }

    public static String buildWhereTaskContainMe() {
        String currentAcounts = PreferencesUtils.get(Config.CURRENT_LOGIN_ACCOUNT, "");
        String[] accounts = currentAcounts.split(",");
        if (accounts.length > 1) {
            return " ((','||create_account ||',') like ('%," + accounts[0]
                    + ",%') or (','||create_account ||',') like ('%," + accounts[1] + ",%')or (','||members_account ||',') like('%," + accounts[0]
                    + ",%')  or (','||members_account ||',') like ('%," + accounts[1] + ",%')) ";
        }
        if (accounts.length == 1) {
            return " ((','||create_account ||',') like ( '%," + accounts[0] + ",%') or (','||members_account ||',') like (',%" + accounts[0] + ",%')) ";
        } else {
            return " ";
        }
    }

    public List<Task> getUnDoSpecialTask(String inspectionType) {
        List<Task> tasks = null;
        try {
            tasks = selector().expr(" and inspection like '%special%' and  inspection <> 'special_xideng'").and(Task.STATUS, "=", "undo").expr("and (pms_jh_source ='pms_pc' or " + buildWhereTaskContainMe() + " or create_account is NULL or create_account = '')").findAll();
            if (null == tasks) {
                tasks = new ArrayList<>();
            }
        } catch (DbException e) {
            e.printStackTrace();
            if (null == tasks) {
                tasks = new ArrayList<>();
            }
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


    public List<Task> findTaskListByLimit(int limit, String... inspections) {
        StringBuilder expr = new StringBuilder();
        expr.append(" and (");
        for (String inspection : inspections) {
            expr.append(" inspection like '").append(inspection).append("%'  ").append("or");
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
            if (!TextUtils.isEmpty(str)) {
                return Float.valueOf(str);
            }
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

    /**
     * 查询同一天相同变电站，相同类型下的任务计划个数
     *
     * @param bdzId      变电站id
     * @param chooseTime 任务选择时间
     * @param name       巡视类型
     */

    public int getCountSameTaks(String name, String bdzId, String chooseTime) {
        try {
            List<Task> tasks = selector().and(Task.INSPECTION, "=", name).and(Task.STATUS, "=", "undo").and(Task.BDZID, "=", bdzId)
                    .expr("and (select datetime('" + chooseTime + "','+24 hour'))> schedule_time and schedule_time >= (select datetime('" + chooseTime + "'))").findAll();
            if (null != tasks) {
                return tasks.size();
            }
        } catch (DbException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    /**
     * 查询任务计划的状态
     *
     * @param currentTaskId
     * @return
     */
    public String getTaskStatus(String currentTaskId) {

        Task task = findById(currentTaskId);
        if (task != null && TaskStatus.done.name().equalsIgnoreCase(task.status)) {
            return "已完成";
        }
        return "未完成";
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
            tasks = selector().and(Task.INSPECTION, "=", name).and(Task.STATUS, "=", "undo").and(Task.BDZID, "=", bdzId)
                    .expr(extraSql + " and (select datetime('" + chooseTime + "','+24 hour'))> schedule_time and schedule_time >= (select datetime('" + chooseTime + "'))").findAll();
            if (null == tasks)
                return new ArrayList<Task>();
            return tasks;
        } catch (DbException e) {
            e.printStackTrace();
            return new ArrayList<Task>();
        }
    }

    /**
     * 查询任务计划的状态
     *
     * @param currentTaskId
     * @return true is finish
     */
    public boolean getTaskStatusForBoolean(String currentTaskId) {

        Task task = findById(currentTaskId);
        return task != null && TaskStatus.done.name().equalsIgnoreCase(task.status);
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
            execSql(sql);
            return true;
        } catch (Exception e) {

            return false;// TODO: handle exception
        }

    }

    public Task findTaskByTypeAndTodayTime(String bdzId,String typeValue, String currentShortTime) {
        Task task;
        try {
            task = selector().and(Task.BDZID,"=",bdzId).and(Task.INSPECTION, "=", typeValue).and(Task.SCHEDULE_TIME, ">", currentShortTime+" 00:00:00").and(Task.SCHEDULE_TIME,"<",currentShortTime+" 23:59:59").and(Task.STATUS,"<>","done").findFirst();
            return task;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return null;
    }
}
