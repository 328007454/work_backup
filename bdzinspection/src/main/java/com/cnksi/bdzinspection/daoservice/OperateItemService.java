package com.cnksi.bdzinspection.daoservice;

import java.util.List;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.model.OperateItem;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

/**
 * 操作项目
 * 
 * @author terry
 * 
 */
public class OperateItemService extends BaseService {

	public static OperateItemService mInstance;

	private OperateItemService() {
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
			result = XunshiApplication.getDbUtils().findAll(Selector.from(OperateItem.class).where(OperateItem.TID, "=", taskId));
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
			count = XunshiApplication.getDbUtils().count(Selector.from(OperateItem.class).where(OperateItem.TID, "=", taskId));
		} catch (DbException e) {
			e.printStackTrace();
			count = 0;
		}
		return String.valueOf(count);
	}

}
