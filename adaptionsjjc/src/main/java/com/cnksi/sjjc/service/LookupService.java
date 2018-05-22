package com.cnksi.sjjc.service;

import com.cnksi.common.Config;
import com.cnksi.common.daoservice.BaseService;
import com.cnksi.sjjc.bean.Lookup;
import com.cnksi.common.enmu.LookUpType;

import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;


/**
 * Lookup 数据查询
 *
 * @author terry
 *
 */
public class LookupService extends BaseService<Lookup> {

	public static LookupService mInstance;

	private LookupService() {
		super(Lookup.class);
	}

	public static LookupService getInstance() {
		if (mInstance == null) {
			mInstance = new LookupService();
		}
		return mInstance;
	}

	/**
	 * 根据Lookup类型查询所有
	 *
	 * @param type
	 * @return
	 */
	public List<String> getAllValueByLookupType(String type) {
		List<String> valueList = new ArrayList<String>();
		try {
			List<Lookup> lookups = selector().and(Lookup.TYPE, "=", type).and(Lookup.K, "<>", "other")
					.orderBy(Lookup.SORT, false).findAll();
			for (Lookup look : lookups) {
				valueList.add(look.v);
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		return valueList;

	}

	/**
	 * 根据LookupType查询
	 *
	 * @param type
	 * @return
	 */
	public List<Lookup> findLookupByType(String type) {
		return findLookupByType(type, false);
	}

	public Lookup findLookupByValue(String value) {
		Lookup lup = null;
		try {
			lup = selector().and(Lookup.V, "=", value).findFirst();
		} catch (DbException ex) {
			ex.printStackTrace();
		}
		return lup;
	}

	/**
	 * 根据LookupType查询
	 *
	 * @param type
	 * @return
	 */
	public List<Lookup> findLookupByType(String type, boolean hasSearch) {
		List<Lookup> lookups = null;
		try {
			lookups =selector().and(Lookup.TYPE, "=", type).and(Lookup.K, "<>", "other")
					.orderBy(Lookup.SORT, false).findAll();
		} catch (DbException e) {
			e.printStackTrace();
		}
		if (hasSearch) {
			if (lookups == null) {
				lookups = new ArrayList<Lookup>();
			}
			lookups.add(new Lookup(Config.SEARCH_DEVICE_KEY, Config.SEARCH_DEVICE_VALUE));
		}
		return lookups;
	}

	/**
	 * 按照Type来查询LookUp表中第一级的数据
	 *
	 * @param type
	 * @return
	 */
	public List<Lookup> findLookupByTypeWithOutLooId(String type) {
		List<Lookup> lookups = null;
		try {
			lookups = selector().and(Lookup.TYPE, "=", type).and(Lookup.K, "<>", "other")
					.expr("AND (" + Lookup.LOO_ID + " IS NULL OR " + Lookup.LOO_ID + "='')")
					.expr("AND (" + Lookup.K + " NOT LIKE 'exclusive' " + ") AND (" + Lookup.LOO_ID + " IS NULL OR "
							+ Lookup.LOO_ID + "='')")
					.orderBy(Lookup.SORT, false).findAll();
		} catch (DbException e) {
			e.printStackTrace();
		}
		return lookups;
	}

	/**
	 * 零时筛选全面巡检与日常巡检
	 *
	 * @param type
	 * @return
	 */
	public List<Lookup> findLookupTemp(String type) {
		List<Lookup> lookups = null;
		try {
			lookups = selector().and(Lookup.TYPE, "=", type)
					.and(WhereBuilder.b(Lookup.K, "in", new String[] { "full", "day" })).orderBy(Lookup.SORT, false).findAll();
		} catch (DbException e) {
			e.printStackTrace();
		}
		return lookups;
	}

	/**
	 * 按照Type,k来查询LookUp表中第一级的数据
	 *
	 * @param type
	 * @return
	 */
	public List<Lookup> findLookupByTypeWithOutLooId(String type, String k) {
		List<Lookup> lookups = null;
		try {
			lookups = selector().and(Lookup.TYPE, "=", type).and(Lookup.K, "<>", "other")
					.expr("AND (" + Lookup.K + " = '" + k + "' AND " + Lookup.K + " NOT LIKE 'exclusive' " + ") AND ("
							+ Lookup.LOO_ID + " IS NULL OR " + Lookup.LOO_ID + "='')")
					.orderBy(Lookup.SORT, false).findAll();
		} catch (DbException e) {
			e.printStackTrace();
		}
		return lookups;
	}

	/**
	 * 按照类型和looid来查询
	 *
	 * @param type
	 * @param looId
	 * @return
	 */
	public List<Lookup> findLookupByLooId(String type, String looId) {
		List<Lookup> lookups = null;
		try {
			lookups = selector().and(Lookup.TYPE, "=", type).and(Lookup.K, "<>", "other")
					.and(Lookup.LOO_ID, "=", looId).and(Lookup.K,"<>","special_guzhang").and(Lookup.K,"<>","special_xideng")
					.and(Lookup.K,"<>","special_nighttime").orderBy(Lookup.SORT, false).findAll();
		} catch (DbException e) {
			e.printStackTrace();
		}
		return lookups;
	}

	/**
	 * 根据lookup表中的k来查询 remark
	 *
	 * @param k
	 * @return
	 */
	public String getLookUpRemarkByK(String k) {
		String remark = "";
		try {
			Lookup mLookUp = selector().and(Lookup.K, "=", k).findFirst();
			if (mLookUp != null) {
				remark = mLookUp.remark;
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		return remark;
	}

	public List<String> getDeviceTypeList() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("变压器");
		list.add("断路器");
		list.add("间隔刀闸");
		list.add("电压互感器");
		list.add("电流互感器");
		list.add("避雷器");
		list.add("电容器");
		list.add("电抗器");
		list.add("阻波器");
		list.add("开关柜");
		list.add("保护屏");
		list.add("测控屏");
		return list;
	}

	/**
	 * 根据ParentId 查询缺陷原因
	 *
	 * @param parentId
	 * @return
	 */
	public List<Lookup> getDefectReasonListByParentId(String parentId) {
		List<Lookup> lookupList = null;
		try {
			lookupList =selector().and(Lookup.TYPE, "=", LookUpType.defect_reason.name())
					.and(Lookup.LOO_ID, "=", parentId).findAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lookupList;
	}

	/**
	 * 根据ParentId 查询缺陷原因
	 *
	 * @return
	 */
	public List<Lookup> getDefectReasonType() {
		List<Lookup> lookupList = null;
		try {
			lookupList =selector().and(Lookup.TYPE, "=", LookUpType.defect_reason.name())
					.and(WhereBuilder.b(Lookup.LOO_ID, "=", "").expr("OR " + Lookup.LOO_ID + " IS NULL")).findAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lookupList;
	}

}
