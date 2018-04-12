package com.cnksi.bdzinspection.daoservice;
/**
* @author  Wastrel
* @date 创建时间：2016年9月7日 上午9:37:11
* TODO
*/

import java.util.HashMap;
import java.util.List;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;

public class CopyMapCache {
	final static HashMap<String, String> mCopyMap = new HashMap<>();

	public static void initData(String bdzid, String reportId) {
		mCopyMap.clear();
		String sql = "select deviceid,standid,val from defect_record where val <> '' and reportId<>? and val is not null and bdzid =?  GROUP BY deviceid,standid order by discovered_date desc";
		SqlInfo sqlInfo = new SqlInfo(sql, reportId, bdzid);
		try {
			List<DbModel> dataList = XunshiApplication.getDbUtils().findDbModelAll(sqlInfo);
			if (dataList != null) {
				for (DbModel model : dataList) {
					// 标准+设备确保抄录唯一性
					String key = model.getString("standid") + "_" + model.getString("deviceid");
					String value = model.getString("val");
					mCopyMap.put(key, value);
				}
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	public static String getValue(String key) {
		return mCopyMap.get(key);
	}
}
