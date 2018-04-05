package com.cnksi.xscore.xsutils.crash;

import android.Manifest;
import android.content.Context;
import android.support.v4.content.PermissionChecker;
import android.text.TextUtils;

import com.cnksi.xscore.xscommon.DeviceUtils;
import com.cnksi.xscore.xsutils.AppUtils;
import com.cnksi.xscore.xsutils.CoreConfig;
import com.cnksi.xscore.xsutils.FileUtils;
import com.cnksi.xscore.xsutils.FunctionUtils;
import com.cnksi.xscore.xsutils.NetWorkUtil;

import java.io.File;
import java.util.HashMap;

/**
 * 错误日志上传功能
 *
 * @author Oliver
 *
 */
public class CrashReportUploadHandler extends AbstractCrashReportHandler {

	private Context mContext;
	private String logFolder;
	private boolean isOnlyWifiUpload = false;

	public CrashReportUploadHandler(Context context, String logFolder) {
		this(context, logFolder, false);
	}

	public CrashReportUploadHandler(Context context, String logFolder, boolean isOnlyWifiUpload) {
		super(context);
		this.mContext = context;
		this.logFolder = logFolder;
	}

	public static CrashReportUploadHandler init(Context context, String logFolder, boolean isOnlyWifiUpload) {
		return new CrashReportUploadHandler(context, logFolder, isOnlyWifiUpload);
	}

	public static CrashReportUploadHandler init(Context context, String logFolder) {
		return init(context, logFolder, false);
	}

	public static CrashReportUploadHandler init(Context context, String logFolder, SaveDbListener sListener) {
		return init(context, logFolder, false);
	}

	@Override
	protected void sendReport(String title, String body, final File file, boolean isAfterSaveCrash) {
		if (isAfterSaveCrash) {
			try {
				FileUtils.deleteFileByModifyTime(logFolder, System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L);
				String fileName = FunctionUtils.getPrimarykey() + CoreConfig.TXT_POSTFIX;
				FileUtils.copyFile(file.getAbsolutePath(), logFolder + fileName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			uploadCrashLogFile(isOnlyWifiUpload);
		}
	}

	/**
	 * 上传日志文件
	 *
	 * @param isOnlyWifi
	 */
	public void uploadCrashLogFile(boolean isOnlyWifi) {
		if (!NetWorkUtil.isNetworkConnected(mContext)) {
			return;
		}
		boolean isWifiMode = NetWorkUtil.isWifiConnected(mContext);
		if (isOnlyWifi && !isWifiMode) {
			return;
		}
		final File file = getLogFile(mContext);
		if (PermissionChecker.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) == PermissionChecker.PERMISSION_GRANTED) {
			HashMap<String, String> params = new HashMap<>();
			params.put("logs.deviceid", TextUtils.isEmpty(DeviceUtils.getDeviceUUID(mContext)) ? "" : DeviceUtils.getDeviceUUID(mContext));
			params.put("logs.versions", AppUtils.getVersionName(mContext) + "/" + AppUtils.getVersionCode(mContext));
			params.put("logs.pcode", TextUtils.isEmpty(FunctionUtils.getMetaValue(mContext, "com.cnksi.core.PROGRAM_CODE")) ? "" : FunctionUtils.getMetaValue(mContext, "com.cnksi.core.PROGRAM_CODE"));
			params.put("logs.pname", TextUtils.isEmpty(AppUtils.getAppName(mContext)) ? "" : AppUtils.getAppName(mContext));
		}
	}
}
