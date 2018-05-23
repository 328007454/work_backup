package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.model.OperateItem;
import com.cnksi.common.daoservice.BaseService;

import org.xutils.ex.DbException;

import java.util.List;

/**
 * 操作项目
 *
 * @author terry
 */
public class OperateItemService extends BaseService<OperateItem> {

    public static OperateItemService mInstance;

    private OperateItemService() {
        super(OperateItem.class);
    }

    public static OperateItemService getInstance() {
        if (mInstance == null) {
            mInstance = new OperateItemService();
        }
        return mInstance;
    }

    /**
     * 根据操作任务查询相关的操作项目
     *
     * @param taskId
     * @return
     */
    public List<OperateItem> findAllOperateItemByTaskId(String taskId) {
        List<OperateItem> result = null;
        try {
            result = selector().and(OperateItem.TID, "=", taskId).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 查询操作项目条数
     *
     * @param taskId
     * @return
     */
    public String getCountByOperateTick(String taskId) {
        long count = 0;
        try {
            count = selector().and(OperateItem.TID, "=", taskId).count();
        } catch (DbException e) {
            e.printStackTrace();
            count = 0;
        }
        return String.valueOf(count);
    }

}
