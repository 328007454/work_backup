package com.cnksi.core.utils;

import com.cnksi.core.utils.CLog;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * 
 * 检测手机电话
 * @author luoxy
 *
 */
public class TelephoneUtil {

	public static boolean isSIM(Context context){
		TelephonyManager  telephonyManager=(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getSimState()==TelephonyManager.SIM_STATE_READY;
	}
	
	
}
