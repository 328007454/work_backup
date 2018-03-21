package com.cnksi.sjjc.util;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.cnksi.core.R;
import com.cnksi.core.common.DeviceInfor;
import com.cnksi.core.common.UpdateInfor;
import com.cnksi.core.utils.AppUtils;
import com.cnksi.core.utils.DeviceUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.view.CustomerDialog;
import com.cnksi.core.view.CustomerDialog.DialogClickListener;


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
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(updateApkFile);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            context.startActivity(intent);
        }
    }

    /**
     * 检测是否有新的APK
     *
     * @param context
     * @param appFloder
     * @return
     */
    public static File hasUpdateApk(Context context, String appFloder) {
        return hasUpdateApk(context, null, appFloder);
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
    public static File hasUpdateApk(Context context, File file, String appFloder) {
        if (file == null) {
            PackageInfo localPackageInfo = com.cnksi.core.utils.AppUtils.getLocalPackageInfo(context);
            file = getTheNewApkFile(context, appFloder, localPackageInfo == null ? "" : localPackageInfo.packageName, localPackageInfo.versionCode);
        }
        if (file != null && file.exists()) {
            PackageInfo remotePackageInfo = com.cnksi.core.utils.AppUtils.getAPKPackageInfo(context, file);
            PackageInfo localPackageInfo = com.cnksi.core.utils.AppUtils.getLocalPackageInfo(context);
            int remoteVersionCode = 0;
            int localVersionCode = 0;
            if (remotePackageInfo != null && localPackageInfo != null) {
                remoteVersionCode = remotePackageInfo.versionCode;
                localVersionCode = localPackageInfo.versionCode;
            }
            if (remoteVersionCode > localVersionCode) {
                return file;
            } else {
                return null;
            }
        }
        return file;
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

            PackageInfo localPackageInfo = com.cnksi.core.utils.AppUtils.getLocalPackageInfo(context);
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
//        if (file != null && file.exists()) {
//            PackageInfo remotePackageInfo = AppUtils.getAPKPackageInfo(context, file);
//            PackageInfo localPackageInfo = AppUtils.getLocalPackageInfo(context);
//            int remoteVersionCode = 0;
//            int localVersionCode = 0;
//            if (remotePackageInfo != null && localPackageInfo != null) {
//                remoteVersionCode = remotePackageInfo.versionCode;
//                localVersionCode = localPackageInfo.versionCode;
//            }
//            if (remoteVersionCode > localVersionCode) {
//                return file;
//            } else {
//                return null;
//            }
//        }
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
        PackageInfo newAPkPackageInfo;
        List<File> apkFileList = new ArrayList<>();
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file != null && file.exists() && file.isDirectory()) {
                File[] children = file.listFiles();
                for (File f : children) {
                    if (f != null && f.exists() && f.isFile() && f.getName().toLowerCase(Locale.US).endsWith(CoreConfig.APK_POSTFIX)) {
                        apkFileList.add(f);
                    }
                }
            }
            // 找到升级版本中包名和本应用包名相同的最新的APK文件
            if (!apkFileList.isEmpty()) {
                ArrayList<File> realApkFileList = new ArrayList<File>();
                // 找到和本应用包名相同的APK文件
                for (File apkFile : apkFileList) {
                    PackageInfo packageInfor = com.cnksi.core.utils.AppUtils.getAPKPackageInfo(context, apkFile);
                    if (packageInfor != null && packageName.equalsIgnoreCase(packageInfor.packageName)) {
                        realApkFileList.add(apkFile);
                    }
                }
                // 从找到的APK文件中找到最新的APK文件
                if (!realApkFileList.isEmpty()) {
//                    newApkFile = realApkFileList.get(0);
//                    newAPkPackageInfo = AppUtils.getAPKPackageInfo(context, newApkFile);
                    for (File fileTemp : realApkFileList) {
                        PackageInfo tempAPkPackageInfo = com.cnksi.core.utils.AppUtils.getAPKPackageInfo(context, fileTemp);
                        if (tempAPkPackageInfo != null && tempAPkPackageInfo.versionCode > currentVersionCode) {
                            newAPkPackageInfo = tempAPkPackageInfo;
                            newApkFile = fileTemp;
                        }
                    }
                }
            }
        }
        apkFileList.clear();
        return newApkFile;
    }

//    /**
//     * 显示升级对话框
//     *
//     * @param downloadUrl
//     * @param updateContent
//     */
//    public static void showUpdateDialog(final Activity mContext, final String downloadUrl, String updateContent, final String targetFileFolder, final String targetFileName) {
//        mDialog = CustomerDialog.showUpdateDialog(mContext, R.string.version_update_str, updateContent, new DialogClickListener() {
//            @Override
//            public void confirm() {
//                mDialog.dismiss();
//                Intent intent = new Intent(mContext, DownLoadAPKService.class);
//                intent.putExtra(CoreConfig.DOWNLOAD_APK_URL_KEY, downloadUrl);
//                intent.putExtra(CoreConfig.DOWNLOAD_FILE_FOLDER_KEY, targetFileFolder);
//                intent.putExtra(CoreConfig.DOWNLOAD_FILE_NAME_KEY, targetFileName);
//                mContext.startService(intent);
//            }
//
//            @Override
//            public void cancel() {
//
//            }
//        });
//    }

//    /**
//     * 新版本已下载 提示安装dialog
//     *
//     * @param file
//     */
//    public static Dialog showInstallNewApkDialog(final Activity mContext, final File file) {
//        Dialog mDialog = CustomerDialog.showSelectDialog(mContext, R.string.find_newversion_str, new DialogClickListener() {
//            @Override
//            public void confirm() {
//                com.cnksi.core.utils.UpdateUtils.installNewApk(mContext, file);
//            }
//
//            @Override
//            public void cancel() {
//
//            }
//        }, R.string.install_now_str, R.string.cancel_install_str);
//        return mDialog;
//    }

//    /**
//     * 新版本已下载 提示安装dialog
//     *
//     * @param file
//     */
//
//    public static Dialog showInstallNewApkDialog(final Activity mContext, final File file, final boolean isPms, String updateContent) {
//        mDialog = CustomerDialog.showSelectDialog(mContext, "版本更新", isPms ? mContext.getResources().getString(R.string.install_now_str_pms) : updateContent, new DialogClickListener() {
//            @Override
//            public void confirm() {
//                com.cnksi.core.utils.UpdateUtils.installNewApk(mContext, file);
//                PreferencesUtils.put(mContext, com.cnksi.core.utils.AppUtils.IS_SJJC_AREADY_UPDATE, true);
//            }
//
//            @Override
//            public void cancel() {
//                if (!isPms)
//                    PreferencesUtils.put(mContext, com.cnksi.core.utils.AppUtils.IS_SJJC_AREADY_UPDATE, false);
//                if (isPms && PreferencesUtils.get(mContext, com.cnksi.core.utils.AppUtils.IS_SJJC_AREADY_UPDATE, false)) {
//                    System.exit(0);
//                }
//
//            }
//        }, R.string.install_now_str, R.string.cancel_install_str, isPms);
//        return mDialog;
//    }

    /**
     * 解析升级数据
     *
     * @param mContext
     * @param mHandler
     * @param result
     * @param requestCode
     */
    public static void resolveData(Context mContext, Handler mHandler, String result, int requestCode) {
        try {
            UpdateInfor updateInfo = JSON.toJavaObject(JSON.parseObject(result), UpdateInfor.class);
            if (updateInfo != null) {
                if (Integer.valueOf(updateInfo.vcode) > AppUtils.getLocalPackageInfo(mContext).versionCode) {
                    mHandler.sendMessage(mHandler.obtainMessage(requestCode, updateInfo));
                }
            }
        } catch (Exception e) {
        }
    }

//    /**
//     * 得到设备的信息
//     *
//     * @param mContext
//     * @param appCode
//     * @return
//     */
//    public static RequestParams getDeviceInforParams(Context mContext, String appCode) {
//        RequestParams params = new RequestParams();
//        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) == PermissionChecker.PERMISSION_GRANTED) {
//            DeviceInfor deviceInfor = DeviceUtils.getPhoneInfor(mContext);
//            params.addBodyParameter(DeviceInfor.PID, TextUtils.isEmpty(appCode) ? FunctionUtils.getMetaValue(mContext, "com.cnksi.core.PROGRAM_CODE") : appCode);
//            params.addBodyParameter(DeviceInfor.ANDROID, deviceInfor.android);
//            params.addBodyParameter(DeviceInfor.BRAND, deviceInfor.brand);
//            params.addBodyParameter(DeviceInfor.DENSITY, deviceInfor.density);
//            params.addBodyParameter(DeviceInfor.DEVICE_ID, deviceInfor.deviceid);
//            params.addBodyParameter(DeviceInfor.FACTORY, deviceInfor.factory);
//            params.addBodyParameter(DeviceInfor.LASTVIST, deviceInfor.lastvist);
//            params.addBodyParameter(DeviceInfor.MEMORY, deviceInfor.memory);
//            params.addBodyParameter(DeviceInfor.MODEL, deviceInfor.model);
//            params.addBodyParameter(DeviceInfor.PHONE_NUM, deviceInfor.phone);
//            params.addBodyParameter(DeviceInfor.RESOLUTION, deviceInfor.resolution);
//            params.addBodyParameter(DeviceInfor.VER, deviceInfor.ver);
//        }
//        return params;
//    }

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
