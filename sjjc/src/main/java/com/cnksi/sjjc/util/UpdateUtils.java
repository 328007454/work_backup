package com.cnksi.sjjc.util;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.PermissionChecker;
import android.text.TextUtils;

import com.cnksi.common.Config;
import com.cnksi.core.common.DeviceInfor;
import com.cnksi.core.utils.DeviceUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.sjjc.BuildConfig;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.view.CustomerDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UpdateUtils {

    private static Dialog mDialog = null;

    /**
     * 安装APK
     *
     * @param context
     * @param updateApkFile
     */
    public static void installNewApk(Context context, File updateApkFile) {
        if (updateApkFile != null && updateApkFile.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            //判断是否是AndroidN以及更高的版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileProvider", updateApkFile);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(updateApkFile), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        }


    }


    public static File hasUpdateApk(Context context, String appFloder, boolean isPms) {
        return hasUpdateApk(context, null, appFloder, isPms);
    }



    /**
     * 检测是否有新的APK
     *
     * @param context
     * @param file
     * @param appFloder apk包的路径
     * @return
     */
    public static File hasUpdateApk(Context context, File file, String appFloder, boolean isPms) {
        if (file == null) {

            PackageInfo localPackageInfo = AppUtils.getLocalPackageInfo(context);
            if (localPackageInfo==null){
                return  null;
            }
            if (isPms) {
                PackageManager manager = context.getPackageManager();
                try {
                    PackageInfo info = manager.getPackageInfo("com.cnksi.bdzinspection", 0);
                    file = getTheNewApkFile(context, appFloder, "com.cnksi.bdzinspection", info == null ? 1000 : info.versionCode);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }

            } else {
                file = getTheNewApkFile(context, appFloder, localPackageInfo == null ? "" : localPackageInfo.packageName, localPackageInfo.versionCode);
            }
        }
        return file;
    }

    /**
     * 得到最新的升级apk包
     *
     * @param context
     * @param path
     * @return
     */
    public static File getTheNewApkFile(Context context, String path, String packageName, int currentVersionCode) {
        File newApkFile = null;
        List<File> apkFileList = new ArrayList<>();
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file != null && file.exists() && file.isDirectory()) {
                File[] children = file.listFiles();
                for (File f : children) {
                    if (f != null && f.exists() && f.isFile() && f.getName().toLowerCase(Locale.US).endsWith(Config.APK_POSTFIX)) {
                        apkFileList.add(f);
                    }
                }
            }
            // 找到升级版本中包名和本应用包名相同的最新的APK文件
            if (!apkFileList.isEmpty()) {
                ArrayList<File> realApkFileList = new ArrayList<File>();
                // 找到和本应用包名相同的APK文件
                for (File apkFile : apkFileList) {
                    PackageInfo packageInfor = AppUtils.getAPKPackageInfo(context, apkFile);
                    if (packageInfor != null && packageName.equalsIgnoreCase(packageInfor.packageName)) {
                        realApkFileList.add(apkFile);
                    }
                }
                // 从找到的APK文件中找到最新的APK文件
                if (!realApkFileList.isEmpty()) {
                    for (File fileTemp : realApkFileList) {
                        PackageInfo tempAPkPackageInfo = AppUtils.getAPKPackageInfo(context, fileTemp);
                        if (tempAPkPackageInfo != null && tempAPkPackageInfo.versionCode > currentVersionCode) {
                            newApkFile = fileTemp;
                        }
                    }
                }
            }
        }
        apkFileList.clear();
        return newApkFile;
    }


    /**
     * 新版本已下载 提示安装dialog
     *
     * @param file
     */

    /**
     * 新版本已下载 提示安装dialog
     *
     * @param file
     */

    public static Dialog showInstallNewApkDialog(final Activity mContext, final File file, final boolean isPms, String updateContent) {
        mDialog = CustomerDialog.showSelectDialog(mContext, "版本更新", isPms ? mContext.getResources().getString(R.string.install_now_str_pms) : updateContent, new CustomerDialog.DialogClickListener() {
            @Override
            public void confirm() {
                UpdateUtils.installNewApk(mContext, file);
                PreferencesUtils.put(AppUtils.IS_SJJC_AREADY_UPDATE, true);
            }

            @Override
            public void cancel() {
                if (!isPms) {
                    PreferencesUtils.put(AppUtils.IS_SJJC_AREADY_UPDATE, false);
                }
                if (isPms && PreferencesUtils.get(AppUtils.IS_SJJC_AREADY_UPDATE, false)) {
                    System.exit(0);
                }

            }
        }, R.string.install_now_str, R.string.cancel_install_str, isPms);
        return mDialog;
    }



    /**
     * 得到设备的信息
     *
     * @param mContext
     * @param appCode
     * @return
     */
    public static Map<String, String> getDeviceInforMapParams(Context mContext, String appCode) {
        HashMap<String, String> params = new HashMap<>();
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) == PermissionChecker.PERMISSION_GRANTED) {
            DeviceInfor deviceInfor = DeviceUtils.getPhoneInfor(mContext);
            params.put(DeviceInfor.PID, TextUtils.isEmpty(appCode) ? FunctionUtils.getMetaValue(mContext, "com.cnksi.core.PROGRAM_CODE") : appCode);
            params.put(DeviceInfor.ANDROID, TextUtils.isEmpty(deviceInfor.android) ? "" : deviceInfor.android);
            params.put(DeviceInfor.BRAND, TextUtils.isEmpty(deviceInfor.brand) ? "" : deviceInfor.brand);
            params.put(DeviceInfor.DENSITY, TextUtils.isEmpty(deviceInfor.density) ? "" : deviceInfor.density);
            params.put(DeviceInfor.DEVICE_ID, TextUtils.isEmpty(deviceInfor.deviceid) ? "" : deviceInfor.deviceid);
            params.put(DeviceInfor.FACTORY, TextUtils.isEmpty(deviceInfor.factory) ? "" : deviceInfor.factory);
            params.put(DeviceInfor.LASTVIST, TextUtils.isEmpty(deviceInfor.lastvist) ? "" : deviceInfor.lastvist);
            params.put(DeviceInfor.MEMORY, TextUtils.isEmpty(deviceInfor.memory) ? "" : deviceInfor.memory);
            params.put(DeviceInfor.MODEL, TextUtils.isEmpty(deviceInfor.model) ? "" : deviceInfor.model);
            params.put(DeviceInfor.PHONE_NUM, TextUtils.isEmpty(deviceInfor.phone) ? "" : deviceInfor.phone);
            params.put(DeviceInfor.RESOLUTION, TextUtils.isEmpty(deviceInfor.resolution) ? "" : deviceInfor.resolution);
            params.put(DeviceInfor.VER, TextUtils.isEmpty(deviceInfor.ver) ? "" : deviceInfor.ver);
        }
        return params;
    }

}
