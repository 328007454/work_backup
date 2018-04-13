package com.cnksi.xscore.xsutils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import com.cnksi.xscore.xsapplication.CoreApplication;

/**
 * Created by Oliver on 2016/8/18.
 */
public class EncryptUtils {

	private static final String APP_SIGN = "30820249308201b2a0030201020204516f694d300d06092a864886f70d01010505003068310b300906035504061302434e310f300d06035504080c06e59b9be5b79d310f300d06035504070c06e68890e983bd3111300f060355040a13086b696e6773746f6e3111300f060355040b13086b696e6773746f6e3111300f060355040313086b696e6773746f6e3020170d3133303431383033333232395a180f33303132303831393033333232395a3068310b300906035504061302434e310f300d06035504080c06e59b9be5b79d310f300d06035504070c06e68890e983bd3111300f060355040a13086b696e6773746f6e3111300f060355040b13086b696e6773746f6e3111300f060355040313086b696e6773746f6e30819f300d06092a864886f70d010101050003818d003081890281810081d06a7709819004f0760d64405862ab6caaeb9849d16a34ce79190629ee577c4bd2866c4354023f4a4a71a181f9cd50fe8655d0cded75579bae1192cb2befce2a5ef22a122fbc24f1a4467cae93a5ce09f3c58d974a32b4e593234a0a1a651f79b6c1dfe9ec6656f3b3c8e29253ee0803dad5d738aefd7a2f36bc8ba0061e750203010001300d06092a864886f70d01010505000381810022a4ab2b0a5e84def8982d664d17b5071ea945388f86dcee32e7cb88ad2195aab2dfabcd3b77bc9ba7c4961e41346107e521427391a735c5e145b0e5a89adfd4d596533bc9106cbade808e392f2c3a742eceb000fd35d61d2a082ed7bf8a645078e5b7540b79aa4c5b42628c747f333dd945af2e28336653fc901c705fe5aaf8";

	static {
		System.loadLibrary("encrypt");
	}

	private EncryptUtils() {
		/* cannot be instantiated */
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	public static void init() {
		boolean isOwnApp = EncryptUtils.isOwnApp();
		if (!isOwnApp) {
			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}

	/**
	 * 获取应用程序签名
	 *
	 * @return
	 */
	public static String getSignature() {
		try {
			Context mContext = CoreApplication.getAppContext();
			/** 通过包管理器获得指定包名包含签名的包信息 **/
			PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), PackageManager.GET_SIGNATURES);
			/******* 通过返回的包信息获得签名数组 *******/
			Signature[] signatures = packageInfo.signatures;
			/******* 循环遍历签名数组拼接应用签名 *******/
			StringBuilder builder = new StringBuilder();
			for (Signature signature : signatures) {
				builder.append(signature.toCharsString());
			}
			/************** 得到应用签名 **************/
			return builder.toString();
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static boolean isOwnApp() {
		String signStr = getSignature();
		return APP_SIGN.equals(signStr);
	}

	public static native boolean isEquals(String str);
}
