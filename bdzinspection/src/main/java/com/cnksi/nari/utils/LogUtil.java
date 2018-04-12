/*
 * @(#)LogUtil.java		       Project: crash
 * Date:2014-5-27
 *
 * Copyright (c) 2014 CFuture09, Institute of Software, 
 * Guangdong Ocean University, Zhanjiang, GuangDong, China.
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cnksi.nari.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.xscore.xsutils.DateUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.cnksi.xscore.xsutils.IOUtils.closeQuietly;

/**
 *
 */
public class LogUtil {
    private LogUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    static String LOG_FILE_SUFFIX = ".txt";


    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());


    private static String deviceInfo;

    public static void writeLog(Throwable e) {
        writeLog(e.getClass().getSimpleName(), e);
    }


    public static synchronized void writeLog(String tag, Throwable e) {
        writeLog(tag, e.getMessage(), e);
    }


    private static File getLogFile(String tag) {
        return new File(Config.LOGFOLDER + tag + "-" + DateUtils.getCurrentTime("yyyyMMddHHmmssSSS") + LOG_FILE_SUFFIX);
    }

    /**
     * 将日志写入文件。
     *
     * @param tag     标签
     * @param message
     * @param tr
     */
    public static synchronized void writeLog(String tag, String message, Throwable tr) {
        writeLog(getLogFile(tag), tag, message, tr);
    }

    /**
     * 将日志写入文件。
     *
     * @param logFile
     * @param tag     标签
     * @param message
     * @param tr
     */
    public static synchronized void writeLog(File logFile, String tag, String message, Throwable tr) {
        logFile.getParentFile().mkdirs();
        if (logFile.exists()) {
            logFile.delete();
        }
        try {
            logFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String time = timeFormat.format(Calendar.getInstance().getTime());
        synchronized (logFile) {
            FileWriter fileWriter = null;
            BufferedWriter bufferWriter = null;
            PrintWriter printWriter = null;
            try {
                fileWriter = new FileWriter(logFile, true);
                bufferWriter = new BufferedWriter(fileWriter);
                printWriter = new PrintWriter(fileWriter);
                bufferWriter.append(time).append('\n').append(getDeviceInfo()).append("E").append('/').append(tag).append(" ").append(message).append('\n');
                bufferWriter.flush();
                tr.printStackTrace(printWriter);
                printWriter.flush();
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeQuietly(fileWriter);
                closeQuietly(bufferWriter);
                closeQuietly(printWriter);
            }
        }
    }

    public static String buildBody(Context context) {
        StringBuilder sb = new StringBuilder();
        sb.append("--------------APPLICATION INFORMATION------------").append('\n');
        PackageManager pm = context.getPackageManager();
        ApplicationInfo ai = context.getApplicationInfo();
        sb.append("Application : ").append(pm.getApplicationLabel(ai)).append('\n');
        try {
            PackageInfo pi = pm.getPackageInfo(ai.packageName, 0);
            sb.append("Version Code: ").append(pi.versionCode).append('\n');
            sb.append("Version Name: ").append(pi.versionName).append('\n');
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        sb.append("--------------DEVICE INFORMATION-----------------").append('\n');
        sb.append("Board: ").append(Build.BOARD).append('\n');
        sb.append("BOOTLOADER: ").append(Build.BOOTLOADER).append('\n');
        sb.append("BRAND: ").append(Build.BRAND).append('\n');
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String[] ABI = Build.SUPPORTED_ABIS;
            for (String s : ABI) {
                sb.append("CPU_ABI:").append(s).append('\n');
            }
        } else {
            sb.append("CPU_ABI: ").append(Build.CPU_ABI).append('\n');
            sb.append("CPU_ABI2: ").append(Build.CPU_ABI2).append('\n');
        }
        sb.append("DEVICE: ").append(Build.DEVICE).append('\n');
        sb.append("DISPLAY: ").append(Build.DISPLAY).append('\n');
        sb.append("FINGERPRINT: ").append(Build.FINGERPRINT).append('\n');
        sb.append("HARDWARE: ").append(Build.HARDWARE).append('\n');
        sb.append("HOST: ").append(Build.HOST).append('\n');
        sb.append("ID: ").append(Build.ID).append('\n');
        sb.append("MANUFACTURER: ").append(Build.MANUFACTURER).append('\n');
        sb.append("PRODUCT: ").append(Build.PRODUCT).append('\n');
        sb.append("TAGS: ").append(Build.TAGS).append('\n');
        sb.append("TYPE: ").append(Build.TYPE).append('\n');
        sb.append("USER: ").append(Build.USER).append('\n');
        return sb.toString();
    }

    /**
     * 构建设备信息
     *
     * @return
     */
    public static String buildBody() {
        return buildBody(XunshiApplication.getAppContext());
    }


    public static String getDeviceInfo() {
        if (deviceInfo == null)
            deviceInfo = buildBody();
        return deviceInfo;
    }


    public static void setLogFileSuffix(String logFileSuffix) {
        LOG_FILE_SUFFIX = logFileSuffix;
    }
}
