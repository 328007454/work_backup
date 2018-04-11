package com.cnksi.bdzinspection.daoservice;

import java.util.List;

import com.cnksi.bdzinspection.application.CustomApplication;
import com.cnksi.bdzinspection.model.DeviceTypeImage;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

public class DeviceTypeImageService extends BaseService {
	private static DeviceTypeImageService instance;

	public static DeviceTypeImageService getInstance() {
	if(null==instance)
		instance=new DeviceTypeImageService();
		return instance;
	}
	
	/**
	 * 查询设备类型图片
	 * 
	 * @param deviceType
	 * @return
	 */
	public List<DeviceTypeImage> queryImage(String deviceType) throws DbException{
		Selector selector=Selector.from(DeviceTypeImage.class).where(DeviceTypeImage.TYPE_ID,"=",deviceType).orderBy(DeviceTypeImage.ID);
		return CustomApplication.getDbUtils().findAll(selector);
	}
	
	
	
}
