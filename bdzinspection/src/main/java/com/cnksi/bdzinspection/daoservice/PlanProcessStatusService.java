package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.model.PlanProcessStatus;
import com.cnksi.bdzinspection.model.Process;
import com.cnksi.common.daoservice.BaseService;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wastrel
 * @version 1.0
 * @date 2018/5/23 17:43
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class PlanProcessStatusService extends BaseService<PlanProcessStatus> {

    protected PlanProcessStatusService() {
        super(PlanProcessStatus.class);
    }

    final static PlanProcessStatusService instance = new PlanProcessStatusService();

    public static PlanProcessStatusService getInstance() {
        return instance;
    }


    @Override
    public boolean saveOrUpdate(PlanProcessStatus planProcessStatus) {
        try {
            return super.saveOrUpdate(planProcessStatus);
        } catch (DbException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<PlanProcessStatus> getProcessStatus(String taskid, String proid) {
        List<PlanProcessStatus> list = null;
        try {
            list = selector()
                    .and(PlanProcessStatus.TASK_ID, "=", taskid).orderBy("process_id", false).findAll();
            if (list == null || list.size() < 1) {
                List<Process> _t = ProcessService.getInstance().findWorkflowById(proid);
                list = new ArrayList<>();
                for (Process object : _t) {

                    list.add(new PlanProcessStatus(taskid, object.id, "0"));
                }
                saveOrUpdate(list);
                list = selector().and(PlanProcessStatus.TASK_ID, "=", taskid).findAll();
            }

        } catch (DbException e) {
            e.printStackTrace();
        }

        return list;
    }
}
