package com.cnksi.sjjc.service;

import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.bean.TaskExtend;

import org.xutils.ex.DbException;

/**
 * @author luoxy
 * @date 2016/6/21
 * @copyRight
 */
public class TaskExtendService extends BaseService<TaskExtend> {
    private static TaskExtendService instance;

    public static TaskExtendService getInstance() {
        if (null == instance)
            instance = new TaskExtendService();
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
        return CustomApplication.getDbManager().selector(TaskExtend.class).where(TaskExtend.TASK_ID, "=", taskId).findFirst();
    }
}
