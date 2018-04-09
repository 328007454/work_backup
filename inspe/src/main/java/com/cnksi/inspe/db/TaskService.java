package com.cnksi.inspe.db;

import com.cnksi.inspe.base.BaseDbService;
import com.cnksi.inspe.db.entity.InspecteTaskEntity;
import com.cnksi.inspe.type.RoleType;
import com.cnksi.inspe.type.TaskProgressType;

import org.xutils.db.Selector;
import org.xutils.ex.DbException;

import java.util.List;

/**
 * 任务服务
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/04/08 16:42
 */
public class TaskService extends BaseDbService {
    public boolean saveTask(InspecteTaskEntity entity) {
        try {
            dbManager.update(entity, "progress");
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 获取-巡检任务(全部)
     *
     * @return
     * @deprecated 仅开发使用
     */
    @Deprecated
    public List<InspecteTaskEntity> getTaskList() {

        //
        List<InspecteTaskEntity> list = null;
        try {
            Selector selector = dbManager.selector(InspecteTaskEntity.class)
                    .where("dlt", "=", "0");

            list = selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取-巡检任务<p/>
     * 完成的任务则不显示
     * @version v1.0
     * @auther Today(张军)
     * @date 2018/04/08 21:25
     */
    public List<InspecteTaskEntity> getTaskList(String[] userIds, String deptId, String[] taskTypes) {

        //
        List<InspecteTaskEntity> list = null;
        try {
            Selector selector = dbManager.selector(InspecteTaskEntity.class)
                    .where("dlt", "=", "0")
                    .and("progress", "!=", TaskProgressType.done.name());
            if (userIds != null && userIds.length > 0) {
                //查询所有任务
                selector.and("checkuser_id", "IN", userIds);
            }
            if (deptId != null) {
                //查询本班任务
                selector.and("dept_id", "=", deptId);
            }

            if(taskTypes!=null&&taskTypes.length>0){
                selector.and("type", "in", taskTypes);
            }

            list = selector.findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return list;
    }

}
