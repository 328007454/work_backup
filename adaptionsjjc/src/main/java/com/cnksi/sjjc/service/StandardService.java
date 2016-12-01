package com.cnksi.sjjc.service;

import com.cnksi.sjjc.CustomApplication;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;

import java.util.List;


/**
 * 设备部件查询
 *
 * @author terry
 *
 */
public class StandardService {

	public static StandardService mInstance;

	private StandardService() {
	}

	public static StandardService getInstance() {
		if (mInstance == null) {
			mInstance = new StandardService();
		}
		return mInstance;
	}


	/**
	 * 根据DevicePartId 查询对应的抄录标准
	 *
	 * @param devicePartId
	 *            设备部件id
	 * @return
	 *
	 *             sname 间隔名称<br>
	 *             dname 设备名称<br>
	 *             name 设备部件名称 <br>
	 */
	public List<DbModel> findCopyStandardListByDevicePartId(String deviceId, String devicePartId, String reportid) {
		List<DbModel> mStandardList = null;
		try {


			String sql = "select dr.val,dr.defectid,s.*,d.deviceid,d.name,du.duname from standards s " +
					"left JOIN device_unit du on s.duid = du.duid " +
					"LEFT JOIN device_type dt on dt.dtid = du.dtid " +
					"left JOIN device d on d.dtid = dt.dtid " +
					"LEFT JOIN (select * from defect_record where reportid =? and deviceid =? and duid =? and val is not null and val <> '') dr on dr.standid = s.staid" +
					" where d.deviceid =? and s.duid =? and s.resulttype = '1' and s.dlt='0'";
			SqlInfo sqlInfo=new SqlInfo(sql);
			sqlInfo.addBindArg(new KeyValue("reportid",reportid));
			sqlInfo.addBindArg(new KeyValue("deviceId",deviceId));
			sqlInfo.addBindArg(new KeyValue("devicePartId",devicePartId));
			sqlInfo.addBindArg(new KeyValue("deviceId",deviceId));
			sqlInfo.addBindArg(new KeyValue("devicePartId",devicePartId));
			mStandardList = CustomApplication.getDbManager()
					.findDbModelAll(sqlInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mStandardList;
	}

}
