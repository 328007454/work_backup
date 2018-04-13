package com.cnksi.bdzinspection.daoservice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.model.CopyType;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

public class CopyTypeService {
	public static CopyTypeService mInstance;

	private CopyTypeService() {
	}

	public static CopyTypeService getInstance() {
		if (mInstance == null) {
			mInstance = new CopyTypeService();
		}
		return mInstance;
	}
	
	public Map<String,String> getAllCopyType(){
		Map<String,String> typeMap=new HashMap<>();
		try {
			Selector selector=Selector.from(CopyType.class).where(CopyType.SELECTED_ABLE, "=", "Y");
			List<CopyType> list = XunshiApplication.getDbUtils().findAll(selector);
			if(null!=list&&!list.isEmpty()){
				for(CopyType type:list)
					typeMap.put(type.key, type.name);
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		return typeMap;
	}
	
}
