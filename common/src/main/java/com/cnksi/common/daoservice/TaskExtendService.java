package com.cnksi.common.daoservice;

import com.cnksi.common.model.TaskExtend;

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
}
