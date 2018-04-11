package com.cnksi.bdzinspection.daoservice;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.application.CustomApplication;
import com.cnksi.bdzinspection.model.OperateTick;
import com.cnksi.bdzinspection.utils.Config.OperateTaskType;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;

/**
 * 操作票任务
 * 
 * @author terry
 * 
 */
public class OperateTicketService extends BaseService {

	public static OperateTicketService mInstance;

	private OperateTicketService() {
	}

	public static OperateTicketService getInstance() {
		if (mInstance == null) {
			mInstance = new OperateTicketService();
		}
		return mInstance;
	}

	/**
	 * 根据任务Task id 查询任务
	 * 
	 * @param idValue
	 * @return
	 */
	public OperateTick findById(Object idValue) {
		OperateTick result = null;
		try {
			result = CustomApplication.getDbUtils().findById(OperateTick.class, idValue);
		} catch (DbException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 查询所有的操作任务
	 * 
	 * @return
	 */
	public List<DbModel> getAllOperateTaskListByModel(String currentFunctionModel) {
		List<DbModel> list = null;
		try {
			if (OperateTaskType.DBRW.name().equalsIgnoreCase(currentFunctionModel)) {
				String sql = "select ot.*, c.item_count from operate_tick ot left join (select count(*) item_count,tid from operate_item group by tid) as c on c.tid = ot.id where (ot.status ='dsh' or ot.status ='wwc' or ot.status ='ytz' or ot.status ='yzt')";
				list = CustomApplication.getDbUtils().findDbModelAll(new SqlInfo(sql));
			} else if (OperateTaskType.BYRW.name().equalsIgnoreCase(currentFunctionModel)) {
				// TODO: 筛选本月任务
				String sql = "select ot.*, c.item_count from operate_tick ot left join (select count(*) item_count,tid from operate_item group by tid) as c on c.tid = ot.id where ot.status='ywc' and ot.time_operate_start > (select datetime('now','start of month'))";
				list = CustomApplication.getDbUtils().findDbModelAll(new SqlInfo(sql));
			} else if (OperateTaskType.BNRW.name().equalsIgnoreCase(currentFunctionModel)) {
				// TODO:筛选本年任务
				String sql = "select ot.*, c.item_count from operate_tick ot left join (select count(*) item_count,tid from operate_item group by tid) as c on c.tid = ot.id where ot.status='ywc' and ot.time_operate_start > (select datetime('now','start of year'))";
				list = CustomApplication.getDbUtils().findDbModelAll(new SqlInfo(sql));
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 得到分类任务的title
	 */
	public List<String> getTaskCount(Context mContext) {
		List<String> titleArray = new ArrayList<String>();
		try {
			CustomApplication.getDbUtils().createTableIfNotExist(OperateTick.class, true);
			List<DbModel> list = null;
			String sql = "select id from operate_tick where (status ='dsh' or status ='wwc' or status ='ytz' or status ='yzt')";
			list = CustomApplication.getDbUtils().findDbModelAll(new SqlInfo(sql));
			titleArray.add(mContext.getString(R.string.xs_czp_dbrw_format_str, (list == null ? "0" : String.valueOf(list.size()))));
			sql = "select id from operate_tick where time_operate_start > (select datetime('now','start of month')) and status='ywc'";
			list = CustomApplication.getDbUtils().findDbModelAll(new SqlInfo(sql));
			titleArray.add(mContext.getString(R.string.xs_czp_byrw_format_str, (list == null ? "0" : String.valueOf(list.size()))));
			sql = "select id from operate_tick where time_operate_start > (select datetime('now','start of year')) and status='ywc'";
			list = CustomApplication.getDbUtils().findDbModelAll(new SqlInfo(sql));
			titleArray.add(mContext.getString(R.string.xs_czp_bnrw_format_str, (list == null ? "0" : String.valueOf(list.size()))));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return titleArray;
	}
}
