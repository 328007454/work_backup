package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.model.InspectionPrepared;
import com.cnksi.common.daoservice.BaseService;

import org.xutils.ex.DbException;

import java.util.List;

/**
 * @author Wastrel
 * @version 1.0
 * @date 2018/5/23 18:32
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class InspectionPreparedService extends BaseService<InspectionPrepared> {
    protected InspectionPreparedService() {
        super(InspectionPrepared.class);
    }
    final static InspectionPreparedService instance=new InspectionPreparedService();

    public static InspectionPreparedService getInstance() {
        return instance;
    }

    public List<InspectionPrepared> findByTaskId(String taskId) throws DbException {
       return  selector().and(InspectionPrepared.TASKID, "=",taskId).findAll();
    }

}
