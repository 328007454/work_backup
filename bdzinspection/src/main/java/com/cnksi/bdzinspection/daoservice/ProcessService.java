/**
 *
 */
package com.cnksi.bdzinspection.daoservice;

import com.cnksi.bdzinspection.model.Process;
import com.cnksi.common.daoservice.BaseService;
import com.cnksi.common.daoservice.ReportService;
import com.cnksi.common.model.Report;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author ksi-android
 */
public class ProcessService extends BaseService<Process>{
    public static ProcessService mInstance;

    private ProcessService() {
        super(Process.class);
    }

    public static ProcessService getInstance() {
        if (mInstance == null) {
            mInstance = new ProcessService();
        }
        return mInstance;
    }

    /**
     * 根据pro_id 查询工艺流程
     *
     * @return
     */
    public List<Process> findWorkflowById(String value) {
        List<Process> processes = null;
        try {
            processes = selector()
                    .and(Process.PRO_ID, "=", value).orderBy("id", false).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }

        return processes;
    }




    /**
     * 保存遗留问题
     *
     * @param taskID
     * @param problem
     */
    public void saveRemain_Problem(String taskID, String problem) {
        try {
           ReportService.getInstance().update(WhereBuilder.b(Report.TASK_ID, "=", taskID),
                    new KeyValue(Report.REMAIN_PROBLEMS, problem), new KeyValue(Report.ENDTIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
