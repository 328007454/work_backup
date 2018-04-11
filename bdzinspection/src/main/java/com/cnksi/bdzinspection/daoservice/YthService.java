/**
 *
 */
package com.cnksi.bdzinspection.daoservice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cnksi.bdzinspection.application.CustomApplication;
import com.cnksi.bdzinspection.model.GQJ;
import com.cnksi.bdzinspection.model.PlanProcessStatus;
import com.cnksi.bdzinspection.model.Process;
import com.cnksi.bdzinspection.model.Report;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;

/**
 * @author ksi-android
 *
 */
public class YthService {
	public static YthService mInstance;

	private YthService() {
	}

	public static YthService getInstance() {
		if (mInstance == null) {
			mInstance = new YthService();
		}
		return mInstance;
	}

	/**
	 * 根据pro_id 查询工艺流程
	 *
	 * @param idValue
	 * @return
	 */
	public List<Process> findWorkflowById(String value) {
		List<Process> processes = null;

		try {
			processes = CustomApplication.getDbUtils()
					.findAll(Selector.from(Process.class).where(Process.PRO_ID, "=", value).orderBy("id", false));
		} catch (DbException e) {
			e.printStackTrace();
		}

		return processes;
	}

	public String findProParentID(String value) {

		String sql = "select ifnull(parent_id,id) as id from project where id='" + value + "'";
		try {
			return CustomApplication.getDbUtils().findDbModelFirst(new SqlInfo(sql)).getString("id");

		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 根据pro_id 查询工器具
	 *
	 * @param idValue
	 * @return
	 */
	public List<GQJ> findGqjById(String value) {
		List<GQJ> processes = null;

		try {
			processes = CustomApplication.getDbUtils()
					.findAll(Selector.from(GQJ.class).where(Process.PRO_ID, "=", findProParentID(value)));
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
			CustomApplication.getDbUtils().update(Report.class, WhereBuilder.b(Report.TASK_ID, "=", taskID),
					new String[] { Report.REMAIN_PROBLEMS, Report.ENDTIME },
					new String[] { problem, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) });
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void savePlanProcessStatus(PlanProcessStatus list) {
		if (list != null) {
			try {
				CustomApplication.getDbUtils().saveOrUpdate(list);
			} catch (DbException e) {
				// TODO: handle exception
			}
		}
	}

	public List<PlanProcessStatus> getProcessStatus(String taskid, String proid) {
		List<PlanProcessStatus> list = null;
		try {
			list = CustomApplication.getDbUtils().findAll(Selector.from(PlanProcessStatus.class)
					.where(PlanProcessStatus.TASK_ID, "=", taskid).orderBy("process_id", false));
			if (list == null || list.size() < 1) {
				List<Process> _t = findWorkflowById(proid);
				list = new ArrayList<PlanProcessStatus>();
				for (Process object : _t) {

					list.add(new PlanProcessStatus(taskid, object.id, "0"));
				}
				CustomApplication.getDbUtils().saveAll(list);
				list = CustomApplication.getDbUtils()
						.findAll(Selector.from(PlanProcessStatus.class).where(PlanProcessStatus.TASK_ID, "=", taskid));
			}

		} catch (DbException e) {
			e.printStackTrace();
		}

		return list;
	}
}
