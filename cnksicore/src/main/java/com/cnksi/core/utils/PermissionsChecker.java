package com.cnksi.core.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

public class PermissionsChecker {
	private static Context mContext;

	private static PermissionsChecker ourInstance = new PermissionsChecker();

	public static PermissionsChecker getInstance(Context context) {
		mContext = context.getApplicationContext();
		return ourInstance;
	}

	private PermissionsChecker() {
		super();
	}

	public boolean lacksPermissions(String... permissions) {
		for (String permission : permissions) {
			if (lacksPermission(permission)) {
				return true;
			}
		}
		return false;
	}

	// 判断是否缺少权限
	private boolean lacksPermission(String permission) {
		return ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_DENIED;
	}
}