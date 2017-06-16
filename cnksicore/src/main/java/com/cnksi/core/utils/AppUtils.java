package com.cnksi.core.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.io.File;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 跟App相关的辅助类
 */
public class AppUtils {

    private AppUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }
    public static final String IS_SJJC_AREADY_UPDATE = "isSjjcAreadyUpdate";
    public static final String IS_XUNSHI_AREADY_UPDATE = "isXunshiAreadyUpdate";
    /**
     * 获取系统安装的所有程序
     *
     * @param context
     * @return
     */
    public static List<AppInfo> getInstalledAppInfo(Context context) {
        ArrayList<AppInfo> appList = new ArrayList<AppInfo>();
        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);

        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            AppInfo tmpInfo = new AppInfo();
            tmpInfo.appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
            tmpInfo.packageName = packageInfo.packageName;
            tmpInfo.versionName = packageInfo.versionName;
            tmpInfo.versionCode = packageInfo.versionCode;
            tmpInfo.activities = packageInfo.activities;
            tmpInfo.appIcon = packageInfo.applicationInfo.loadIcon(context.getPackageManager());
            appList.add(tmpInfo);
        }
        return appList;
    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * [获取应用程序版本名称信息]
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用程序名称
     */
    public static String getVersionCode(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return String.valueOf(packageInfo.versionCode);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取APK包的信息
     *
     * @param context
     * @param file
     * @return
     */
    public static PackageInfo getAPKPackageInfo(Context context, File file) {
        PackageInfo packageInfo = null;
        try {
            if (file != null && file.exists()) {
                packageInfo = context.getPackageManager().getPackageArchiveInfo(file.getAbsolutePath(), PackageManager.GET_ACTIVITIES);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return packageInfo;
    }

    /**
     * 获取本地版本
     *
     * @return
     */
    public static PackageInfo getLocalPackageInfo(Context context) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return packageInfo;
    }

    /**
     * 检查某个APP是否安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    /**
     * 获取已安装的apk的packageInfo
     *
     * @param context
     * @param packageName
     * @return
     */
    public static PackageInfo getInstallApkPackageInfo(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo;
    }

    /**
     * Install the app client through the market: URI scheme.
     **/
    public static void goToMarket(Context myContext, String packageName) {
        Uri marketUri = Uri.parse("market://details?id=" + packageName);
        Intent myIntent = new Intent(Intent.ACTION_VIEW, marketUri);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myContext.startActivity(myIntent);
        return;
    }

    /**
     * 打开其他应用
     *
     * @param context
     * @param packageName
     * @param activityName
     * @return
     */
    public static boolean openOtherApp(Context context, Intent launchIntent, String packageName, String activityName) {
        try {
            launchIntent.setComponent(new ComponentName(packageName, activityName));
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launchIntent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 安装APK
     *
     * @param mContext
     * @param filePath
     */
    public static void installAPK(Context mContext, String filePath) {
        if (FileUtils.isFileExists(filePath)) {
            Intent installIntent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(new File(filePath));
            installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(installIntent);
        }
    }

    /**
     * 判断某个程序是否正在运行
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAppRunning(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (packageName.equals(appProcess.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断某个程序是在后台运行还是前台运行
     *
     * @param context
     * @return
     */
    public static boolean isBackgroundRunning(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo appProcess : appProcesses) {
            if (packageName.equals(appProcess.processName)) {
                if (appProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 判断某个Service是否正在运行
     *
     * @param context
     * @param serviceClassName
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceClassName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> runningService = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (RunningServiceInfo runServiceInfo : runningService) {
            if (serviceClassName.equals(runServiceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void killRunningAppsExcludeSelf(Context context) {
        if (context == null) return;

        String selfPackageName = context.getPackageName();
        if (selfPackageName == null) {
            killRunningApps(context, null);
        } else {
            killRunningApps(context, new String[]{selfPackageName});
        }
    }

    public static void killRunningApps(Context context) {
        killRunningApps(context, null);
    }

    /**
     * <p>
     * 杀掉正在运行的App的后台进程<br/>
     * 需要如下权限：<br/>
     * android.permission.KILL_BACKGROUND_PROCESSES<br/>
     * android.permission.RESTART_PACKAGES
     * </p>
     *
     * @param context
     * @param excludePkgs 需要排除在外的包名
     */
    @SuppressWarnings("deprecation")
    public static void killRunningApps(Context context, String[] excludePkgs) {
        List<String> runningAppPackageList = getRunningAppPackageList(context, excludePkgs);
        if (runningAppPackageList == null) return;

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (String runningAppPackage : runningAppPackageList)
            am.restartPackage(runningAppPackage);
    }

    public static void deleteRunningAppCacheDirsExcludeSelf(Context context, boolean isExternalCacheDir) {
        if (context == null) return;

        String selfPackageName = context.getPackageName();
        if (selfPackageName == null) {
            deleteRunningAppCacheDirs(context, isExternalCacheDir);
        } else {
            deleteRunningAppCacheDirs(context, new String[]{selfPackageName}, isExternalCacheDir);
        }
    }

    public static void deleteRunningAppCacheDirs(Context context, boolean isExternalCacheDir) {
        deleteRunningAppCacheDirs(context, null, isExternalCacheDir);
    }

    /**
     * 删除正在运行的App的缓存目录
     *
     * @param context
     * @param excludePkgs        需要排除在外的包名
     * @param isExternalCacheDir 是否外置存储缓存目录
     */
    public static void deleteRunningAppCacheDirs(Context context, String[] excludePkgs, boolean isExternalCacheDir) {
        List<String> runningAppCacheDirList = getRunningAppCacheDirList(context, excludePkgs, isExternalCacheDir);
        if (runningAppCacheDirList == null) return;

        for (String runningAppCacheDir : runningAppCacheDirList) {
            FileUtils.deleteAllFiles(runningAppCacheDir);
        }
    }

    public static List<String> getRunningAppCacheDirListExcludeSelf(Context context, boolean isExternalCacheDir) {
        if (context == null) return null;

        String selfPackageName = context.getPackageName();
        if (selfPackageName == null) {
            return getRunningAppCacheDirList(context, isExternalCacheDir);
        } else {
            return getRunningAppCacheDirList(context, new String[]{selfPackageName}, isExternalCacheDir);
        }
    }

    public static List<String> getRunningAppCacheDirList(Context context, boolean isExternalCacheDir) {
        return getRunningAppCacheDirList(context, null, isExternalCacheDir);
    }

    /**
     * 获取正在运行的App的缓存目录列表
     *
     * @param context
     * @param excludePkgs        需要排除在外的包名
     * @param isExternalCacheDir 是否外置存储缓存目录
     * @return 缓存目录列表
     */
    public static List<String> getRunningAppCacheDirList(Context context, String[] excludePkgs, boolean isExternalCacheDir) {
        if (context == null) return null;

        List<String> runningAppPackageList = getRunningAppPackageList(context, excludePkgs);
        if (runningAppPackageList == null) return null;
        if (runningAppPackageList.isEmpty()) return null;

        String selfPackageName = context.getPackageName();
        File selfCacheDir = isExternalCacheDir ? context.getExternalCacheDir() : context.getCacheDir();
        String selfCacheDirPath = (selfCacheDir != null) ? selfCacheDir.getAbsolutePath() : null;
        if (selfPackageName == null || selfCacheDirPath == null) return null;

        List<String> runningAppCacheDirList = new LinkedList<String>();
        for (String runningAppPackage : runningAppPackageList) {
            // 替换包名一段字符串
            String runningAppCacheDir = selfCacheDirPath.replaceFirst(selfPackageName, runningAppPackage);
            if (runningAppCacheDir == null) continue;

            runningAppCacheDirList.add(runningAppCacheDir);
        }

        if (runningAppCacheDirList.isEmpty()) return null;
        return runningAppCacheDirList;
    }

    public static List<String> getRunningAppPackageList(Context context) {
        return getRunningAppPackageList(context, null);
    }

    /**
     * 获取正在运行的App的包名列表
     *
     * @param context
     * @param excludePkgs 需要排除在外的包名
     * @return 包名列表
     */
    public static List<String> getRunningAppPackageList(Context context, String[] excludePkgs) {
        if (context == null) return null;

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningAppProcessInfoList = am.getRunningAppProcesses();
        if (runningAppProcessInfoList == null) return null;

        Set<String> runningAppPackageSet = new HashSet<String>();
        List<String> runningAppPackageList = new LinkedList<String>();
        Set<String> excludePkgSet = new HashSet<String>();
        if (excludePkgs != null) {
            for (String p : excludePkgs)
                excludePkgSet.add(p);
        }

        for (RunningAppProcessInfo i : runningAppProcessInfoList) {
            String[] pkgList = i.pkgList;
            if (pkgList == null) continue;

            for (String p : pkgList) {
                if (excludePkgSet.contains(p)) continue;
                if (runningAppPackageSet.contains(p)) continue;

                runningAppPackageSet.add(p);
                runningAppPackageList.add(p);
            }
        }

        runningAppProcessInfoList.clear();
        runningAppProcessInfoList = null;
        runningAppPackageSet.clear();
        runningAppPackageSet = null;
        excludePkgSet.clear();
        excludePkgSet = null;

        if (runningAppPackageList.isEmpty()) return null;
        return runningAppPackageList;
    }

    /**
     * <p>
     * 获取正在运行的App的包名列表 <br/>
     * 需要取得权限android.permission.GET_TASKS <br/>
     * （从API level 21开始有明显限制，不推荐使用）
     * </p>
     *
     * @param context
     * @param excludePkgs 需要排除在外的包名
     * @return 包名列表
     */
    public static List<String> getRunningAppPackageList0(Context context, String[] excludePkgs) {
        if (context == null) return null;

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        @SuppressWarnings("deprecation") List<RunningTaskInfo> runningAppProcessInfoList = am.getRunningTasks(Integer.MAX_VALUE);
        if (runningAppProcessInfoList == null) return null;

        Set<String> runningAppPackageSet = new HashSet<String>();
        List<String> runningAppPackageList = new LinkedList<String>();
        Set<String> excludePkgSet = new HashSet<String>();
        if (excludePkgs != null) {
            for (String p : excludePkgs)
                excludePkgSet.add(p);
        }

        for (RunningTaskInfo i : runningAppProcessInfoList) {
            if (i.topActivity == null) continue;

            String p = i.topActivity.getPackageName();
            if (excludePkgSet.contains(p)) continue;
            if (runningAppPackageSet.contains(p)) continue;

            runningAppPackageSet.add(p);
            runningAppPackageList.add(p);
        }

        runningAppProcessInfoList.clear();
        runningAppProcessInfoList = null;
        runningAppPackageSet.clear();
        runningAppPackageSet = null;
        excludePkgSet.clear();
        excludePkgSet = null;

        if (runningAppPackageList.isEmpty()) return null;
        return runningAppPackageList;
    }

    public static class AppInfo {
        public String appName = "";
        public String packageName = "";
        public String versionName = "";
        public int versionCode = 0;
        public ActivityInfo[] activities;
        public Drawable appIcon = null;
    }


    /**
     * 验证签名
     *
     * @param context
     * @param filePath
     * @return
     */
    public static boolean verSignature(Context context, String filePath) {
        boolean isVer = false;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            int hasCode = sign.hashCode();
            String md5 = getMD5String(sign.toByteArray());

            packageInfo = context.getPackageManager().getPackageArchiveInfo(filePath, PackageManager.GET_SIGNATURES);
            signs = packageInfo.signatures;
            sign = signs[0];
            int hasCodeApk = sign.hashCode();
            String md5Str = getMD5String(sign.toByteArray());
            isVer = md5.equalsIgnoreCase(md5Str) && (hasCode == hasCodeApk);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isVer;
    }

    /**
     * 获取签名Md5
     *
     * @param paramArrayOfByte
     * @return
     */
    public static final String getMD5String(byte[] paramArrayOfByte) {
        char[] asciiTable = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102}; // ascii表对应的数字和字符的编码
        try {
            MessageDigest md5MessageDigest = MessageDigest.getInstance("MD5");
            md5MessageDigest.update(paramArrayOfByte);//
            byte[] tempByte = md5MessageDigest.digest();
            int i = tempByte.length;
            char[] tempChar = new char[i * 2];
            int j = 0;
            int k = 0;
            while (true) { // 将二进制数组转换成字符串
                if (j >= i) {
                    return new String(tempChar);
                }
                int m = tempByte[j];
                int n = k + 1;
                tempChar[k] = asciiTable[(0xF & m >>> 4)];
                k = n + 1;
                tempChar[n] = asciiTable[(m & 0xF)];
                j++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
