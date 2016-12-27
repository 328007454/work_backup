package com.cnksi.core.utils;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.blinkt.openvpn.api.IOpenVPNAPIService;

public class VPNUtils {

	/**
	 * 启动VPNService
	 * 
	 * @param mContext
	 * @param mConnection
	 */
	public static void bindVpnAPIService(Context mContext, ServiceConnection mConnection) throws Exception {
		if (mConnection != null) {
			Intent icsopenvpnService = new Intent(IOpenVPNAPIService.class.getName());
			icsopenvpnService.setPackage(CoreConfig.OPEN_VPN_PACKAGE_NAME);
			mContext.bindService(icsopenvpnService, mConnection, Context.BIND_AUTO_CREATE);
		} else {
			throw new Exception("ServiceConnection can't null");
		}
	}

	/**
	 * UnBind VpnServie
	 * 
	 * @param mContext
	 * @param mService
	 * @param mConnection
	 */
	public static void unbindVpnAPIService(Context mContext, IOpenVPNAPIService mService, ServiceConnection mConnection) throws Exception {
		if (mService != null && mConnection != null) {
			mService.disconnect();
			mContext.unbindService(mConnection);
		} else {
			throw new Exception("IOpenVPNAPIService can't null");
		}
	}

	/**
	 * 启动VPN
	 * 
	 * @param context
	 * @param mService
	 * @param ovpnAssetsFile
	 * @throws Exception
	 */
	public static boolean startVPN(Context context, IOpenVPNAPIService mService, String ovpnAssetsFile) throws Exception {
		InputStream conf = context.getAssets().open(ovpnAssetsFile);
		InputStreamReader isr = new InputStreamReader(conf);
		BufferedReader br = new BufferedReader(isr);
		String config = "";
		String line;
		while (true) {
			line = br.readLine();
			if (line == null)
				break;
			config += line + "\n";
		}
		br.readLine();
		if (!TextUtils.isEmpty(config) && mService != null) {
			mService.startVPN(config);
			return true;
		} else {
			throw new Exception("配置文件读取为空");
		}
	}

}
