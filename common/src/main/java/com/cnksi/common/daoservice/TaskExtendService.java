package com.cnksi.common.daoservice;

import com.cnksi.common.model.TaskExtend;

import org.xutils.ex.DbException;

/**
 * @author Wastrel
 * @version 1.0
 * @date 2018/5/23 16:16
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class TaskExtendService extends BaseService<TaskExtend> {
    protected TaskExtendService() {
        super(TaskExtend.class);
    }
    final static TaskExtendService instance=new TaskExtendService();

    public static TaskExtendService getInstance() {
        return instance;
    }

    /**
     * 查询当前任务扩展属性
     *
     * @param taskId
     * @return
     * @throws DbException
     */
    public TaskExtend findTaskExtendByTaskId(String taskId) throws DbException {
        return selector().and(TaskExtend.TASK_ID, "=", taskId).findFirst();
    }
}
