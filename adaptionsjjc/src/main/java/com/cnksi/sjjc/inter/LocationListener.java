package com.cnksi.sjjc.inter;

import com.baidu.location.BDLocation;

/**
 * 定位回调接口
 * 
 * @author lyndon
 *
 */
public interface LocationListener {
	/**
	 * 定位成功
	 * 
	 * @param location
	 */
	void locationSuccess(BDLocation location);

	/**
	 * 定位失败
	 * 
	 * @param code
	 * @param message
	 */
	void locationFailure(int code, String message);
}
