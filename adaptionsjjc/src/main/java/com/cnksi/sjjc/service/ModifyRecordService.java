package com.cnksi.sjjc.service;

import com.cnksi.core.utils.DateUtils;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.bean.DefectDefine;
import com.cnksi.sjjc.bean.Device;
import com.cnksi.sjjc.bean.DevicePart;
import com.cnksi.sjjc.bean.DeviceStandards;
import com.cnksi.sjjc.bean.ModifyRecord;
import com.cnksi.sjjc.bean.Spacing;

import org.xutils.db.Selector;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 巡检任务的数据查询
 * 
 * @author terry
 * 
 */
public class ModifyRecordService {

	public static ModifyRecordService mInstance;

	private ModifyRecordService() {
	}

	public static ModifyRecordService getInstance() {
		if (mInstance == null) {
			mInstance = new ModifyRecordService();
		}
		return mInstance;
	}

	/**
	 * 记录操作日志
	 * 
	 * @param modifyId
	 * @param tableName
	 * @param operation
	 */
	public void saveOrUpdateModifyRecord(String modifyId, String modifyIdName, String tableName, String operation) {
		try {
			ModifyRecord modifyRecord =null;
			if(CustomApplication.getDbManager().getTable(ModifyRecord.class).tableIsExist()) {
				Selector<ModifyRecord> selector = CustomApplication.getDbManager().selector(ModifyRecord.class).where(ModifyRecord.MODIFY_ID, "=", modifyId).and(ModifyRecord.TABLE_NAME, "=", tableName).and(ModifyRecord.OPERATION, "=", operation);
				modifyRecord= selector.findFirst();
			}
			if (modifyRecord == null) {
				modifyRecord = new ModifyRecord();
			}
			modifyRecord.modify_id = modifyId;
			modifyRecord.modify_id_name = modifyIdName;
			modifyRecord.table_name = tableName;
			modifyRecord.operation = operation;
			modifyRecord.create_time = DateUtils.getCurrentLongTime();

			CustomApplication.getDbManager().saveOrUpdate(modifyRecord);

		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查询所有的操作日志 按日期分类
	 * 
	 * @return
	 */
	public LinkedHashMap<String, ArrayList<DbModel>> getAllModifyRecord() {
		LinkedHashMap<String, ArrayList<DbModel>> mapData = new LinkedHashMap<String, ArrayList<DbModel>>();
		try {
			String sql = "select distinct(date(create_time)) create_time from modify_record order by create_time";
			List<DbModel> timeArrayList = CustomApplication.getDbManager().findDbModelAll(new SqlInfo(sql));
			List<ModifyRecord> modifyRecordList = CustomApplication.getDbManager().findAll(ModifyRecord.class);
			if (timeArrayList != null && !timeArrayList.isEmpty()) {
				for (DbModel timeStr : timeArrayList) {
					ArrayList<DbModel> recordModelList = new ArrayList<DbModel>();
					for (ModifyRecord modifyRecord : modifyRecordList) {
						if (modifyRecord.create_time.contains(timeStr.getString("create_time"))) {
							String tableNameStr = "";
							String xContent = "";
							if (CustomApplication.getDbManager().getTable(DefectDefine.class).getName().equalsIgnoreCase(modifyRecord.table_name)) {
								tableNameStr = "缺陷定义";
								xContent = DefectDefine.DESCRIPTION;
							} else if (CustomApplication.getDbManager().getTable(DevicePart.class).getName().equalsIgnoreCase(modifyRecord.table_name)) {
								tableNameStr = "设备部件";
								xContent = DevicePart.NAME;
							} else if (CustomApplication.getDbManager().getTable(DeviceStandards.class).getName().equalsIgnoreCase(modifyRecord.table_name)) {
								tableNameStr = "巡视标准";
								xContent = DeviceStandards.DESCRIPTION;
							} else if (CustomApplication.getDbManager().getTable(Device.class).getName().equalsIgnoreCase(modifyRecord.table_name)) {
								tableNameStr = "设备";
								xContent = Device.NAME;
							} else if (CustomApplication.getDbManager().getTable(Spacing.class).getName().equalsIgnoreCase(modifyRecord.table_name)) {
								tableNameStr = "间隔";
								xContent = Spacing.NAME;
							}
							sql = "select '" + tableNameStr + "' as table_name, x." + xContent + " as modify_content, m.create_time,m.operation,m.id from modify_record m left join " + modifyRecord.table_name + " x on x." + modifyRecord.modify_id_name + "=m.modify_id where id='" + modifyRecord.id + "'";

							DbModel model = CustomApplication.getDbManager().findDbModelFirst(new SqlInfo(sql));
							recordModelList.add(model);
						}
					}
					mapData.put(timeStr.getString("create_time"), recordModelList);
				}
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		return mapData;
	}

	/**
	 * 删除所有记录
	 */
	public boolean deleteAllModifyRecord() {
		try {
			CustomApplication.getDbManager().delete(ModifyRecord.class);
			return true;
		} catch (DbException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 删除所有记录
	 */
	public boolean deleteModifyRecordById(int idValue) {
		try {
			CustomApplication.getDbManager().deleteById(ModifyRecord.class, idValue);
			return true;
		} catch (DbException e) {
			e.printStackTrace();
			return false;
		}
	}

}
