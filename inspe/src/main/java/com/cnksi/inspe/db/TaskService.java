package com.cnksi.inspe.db;

import com.cnksi.inspe.base.BaseDbService;
import com.cnksi.inspe.db.entity.InspecteTaskEntity;
import com.cnksi.inspe.db.entity.TaskExtendEntity;
import com.cnksi.inspe.type.RoleType;
import com.cnksi.inspe.type.TaskProgressType;
import com.cnksi.inspe.utils.DBUtils;
import com.cnksi.inspe.utils.DateFormat;

import org.xutils.db.Selector;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.ex.DbException;

import java.util.List;

/**
 * 任务服务
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/04/08 16:42
 */
public class TaskService extends BaseDbService {
    public boolean updateTask(InspecteTaskEntity entity) {
        try {
            //修改时间为判断条件
            entity.setLast_modify_time(DateFormat.dateToDbString(System.currentTimeMillis()));
            dbManager.saveOrUpdate(entity);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Deprecated()//精益化2后不再使用，因为创建任务在服务器，手机端仅对任务拓展表进行修改
    public boolean insert(InspecteTaskEntity entity) {
        try {
            entity.setUpdate_time(DateFormat.dateToDbString(System.currentTimeMillis()));
            dbManager.save(entity);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 根据ID获取任务
     * @param id
     * @return
     */
    public InspecteTaskEntity getTask(String id) {
        String sql_left_join = "SELECT extend.person_id,extend.person_name,extend.checked_device_bigid AS persion_device_bigid,task.* FROM xj_jyh_task AS task LEFT JOIN xj_jyh_task_extend AS extend ON task.id=extend.task_id";
        String sql = sql_left_join + " WHERE task.dlt='0' AND task.id='" + id + "';";

        try {
            return DBUtils.parseObject(dbManager.findDbModelFirst(new SqlInfo(sql)), InspecteTaskEntity.class);
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取-巡检任务(全部)
     * @return
     * @deprecated 仅开发使用
     */
    @Deprecated
    public List<InspecteTaskEntity> getTaskList() {

        String sql_left_join = "SELECT extend.person_id,extend.person_name,extend.checked_device_bigid AS persion_device_bigid,task.* FROM xj_jyh_task AS task LEFT JOIN xj_jyh_task_extend AS extend ON task.id=extend.task_id";
        String sql = sql_left_join + " WHERE task.dlt='0';";

        try {
            return DBUtils.parseObjectList(dbManager.findDbModelAll(new SqlInfo(sql)), InspecteTaskEntity.class);
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取-巡检任务<p/>
     * 完成的任务则不显示
     * @version v1.0
     * @auther Today(张军)
     * @date 2018/04/08 21:25
     */
    public List<InspecteTaskEntity> getTaskList(String[] userIds, String deptId, String[] taskTypes) {
        //任务表(1)与拓展展建立(N)管理
        //因此通过任务ID分组和拓展表userId过滤

        String sql_left_table = "(SELECT * FROM xj_jyh_task_extend WHERE dlt='0' ";
        if (userIds != null && userIds.length > 0) {
            //查询所有任务
            sql_left_table += "AND(person_id LIKE '%" + userIds[0] + "%' ";

            for (int i = 1, length = userIds.length; i < length; i++) {
                sql_left_table += "OR person_id '%" + userIds[i] + "%' ";
            }
            sql_left_table += ")";
        }

        sql_left_table += ") AS extend";

        String sql_left_join = "SELECT extend.person_id,extend.person_name,extend.checked_device_bigid AS persion_device_bigid,task.* FROM xj_jyh_task AS task LEFT JOIN " + sql_left_table + " ON task.id=extend.task_id";
        String sql = sql_left_join + " WHERE task.dlt='0' AND ((extend.progress IS NULL AND task.progress !='done') OR (extend.progress IS NOT NULL AND extend.progress!='done')) ";
        if (deptId != null) {
            sql += "AND dept_id='" + deptId + "' ";//查询本班任务
        }

        if (taskTypes != null && taskTypes.length > 0) {
            StringBuffer sb = new StringBuffer();
            for (String type : taskTypes) {
                sb.append("'").append(type).append("'").append(",");
            }
            sql += "AND type IN(" + sb.toString().substring(0, sb.length() - 1) + ") ";
        }

        if (userIds != null && userIds.length > 0) {
            //查询所有任务
            sql += "AND(checkuser_id LIKE '%" + userIds[0] + "%' ";

            for (int i = 1, length = userIds.length; i < length; i++) {
                sql += "OR checkuser_id LIKE '%" + userIds[i] + "%' ";
            }
            sql += ") ";
        }

        sql += "GROUP BY task.id;";

        try {
            return DBUtils.parseObjectList(dbManager.findDbModelAll(new SqlInfo(sql)), InspecteTaskEntity.class);
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;

//        //
//        try {
//            Selector selector = dbManager.selector(InspecteTaskEntity.class)
//                    .where("dlt", "=", "0")
//                    .and("progress", "!=", TaskProgressType.done.name());
//
//            if (deptId != null) {
//                //查询本班任务
//                selector.and("dept_id", "=", deptId);
//            }
//
//            if (taskTypes != null && taskTypes.length > 0) {
//                selector.and("type", "IN", taskTypes);
//            }
//
//            if (userIds != null && userIds.length > 0) {
//                //查询所有任务
//                selector.and("", "LIKE", "%" + userIds[0] + "%");
//                for (int i = 1, length = userIds.length; i < length; i++) {
//                    selector.or("checkuser_id", "LIKE", "%" + userIds[i] + "%");
//                }
//
//            }
//
//            list = selector.findAll();
//        } catch (DbException e) {
//            e.printStackTrace();
//        }
//        return list;
    }

    @Override
    public boolean update(Object object) {
        return super.update(object);
    }

    public TaskExtendEntity getTaskExtend(String taskId, String userId) {
        try {
            return dbManager.selector(TaskExtendEntity.class)
                    .where("dlt", "=", "0")
                    .and("task_id", "=", taskId)
                    .and("person_id", "=", userId)
                    .findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 更新任务拓张记录表
     * @param taskExtend
     * @return
     */
    public boolean updateTaskExtend(TaskExtendEntity taskExtend) {
        try {
            taskExtend.setLast_modify_time(DateFormat.dateToDbString(System.currentTimeMillis()));
            dbManager.saveOrUpdate(taskExtend);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
