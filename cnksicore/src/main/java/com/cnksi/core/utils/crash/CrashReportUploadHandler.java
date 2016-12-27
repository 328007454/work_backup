package com.cnksi.core.utils.crash;

import android.Manifest;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.text.TextUtils;

import com.cnksi.core.common.DeviceUtils;
import com.cnksi.core.okhttp.OkHttpUtils;
import com.cnksi.core.okhttp.callback.StringCallback;
import com.cnksi.core.utils.AppUtils;
import com.cnksi.core.utils.CLog;
import com.cnksi.core.utils.CoreConfig;
import com.cnksi.core.utils.FileUtils;
import com.cnksi.core.utils.FunctionUtils;
import com.cnksi.core.utils.NetWorkUtil;

import java.io.File;
import java.util.HashMap;

import okhttp3.Call;

/**
 * 错误日志上传功能
 *
 * @author Oliver
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
        if (file != null && file.exists()) {
            HashMap<String, String> params = new HashMap<>();
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) == PermissionChecker.PERMISSION_GRANTED) {
                params.put("logs.deviceid", TextUtils.isEmpty(DeviceUtils.getDeviceUUID(mContext)) ? "" : DeviceUtils.getDeviceUUID(mContext));
            }
            params.put("logs.versions", AppUtils.getVersionName(mContext) + "/" + AppUtils.getVersionCode(mContext));
            String appCode = FunctionUtils.getMetaValue(mContext, CoreConfig.PROGRAM_APP_CODE);
            params.put("logs.pcode", TextUtils.isEmpty(appCode) ? "" : appCode);
            params.put("logs.pname", TextUtils.isEmpty(AppUtils.getAppName(mContext)) ? "" : AppUtils.getAppName(mContext));
            OkHttpUtils.post().url(CoreConfig.CRASHLOG_UPLOAD_URL).addFile("files", file.getName(), file).params(params).build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e) {
                    CLog.d(e.getMessage());
                }

                @Override
                public void onResponse(String response) {
                    if (file != null) {
                        file.delete();
                    }
                }
            });
        }
    }
}
