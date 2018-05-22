package com.cnksi.common.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Debug;

import com.cnksi.common.Config;
import com.cnksi.core.utils.CLog;
import com.cnksi.core.utils.ToastUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/***
 * 检测应用是否被更改，是否连接模拟器、调试器、DEX文件是否被更改，签名是否被更改工具类
 * @author libo
 */
public class EssSafeUtil {
    /***
     * 检测APK运行环境，禁止模拟器运行，禁止Debug调试模式运行
     */
    public static void checkInEmualtorOrDebuggable(Context context) {
        try {
            isRunningInEmualtor();
            checkIsDebuggable();
            if (checkGetRootAuth()) {
                ToastUtils.showMessage("手机已被Root,退出程序");
                //退出程序
                System.exit(0);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 检测应用是否在模拟器中运行
     */
    private static void isRunningInEmualtor() {
        boolean qemuKernel = false;
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("getprop ro.kernel.qemu");
            os = new DataOutputStream(process.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(), "GBK"));
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            qemuKernel = (Integer.valueOf(in.readLine()) == 1);
            CLog.d("检测到模拟器:" + qemuKernel);
        } catch (Exception e) {
            qemuKernel = false;
            CLog.d("未检测到模拟器:" + String.valueOf(qemuKernel));
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                CLog.e(e);
            }
        }
        if (qemuKernel && !Config.DEBUG) {
            //退出程序
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**
     * 检测当前应用是否连接有调试器，若已连接直接退出应用
     */
    private static void checkIsDebuggable() {
        //检测当前应用是否连接有调试器，若已连接直接退出
        if (Debug.isDebuggerConnected() && Config.DEBUG) {
            CLog.i("连接了调试器... ");
        }
        if (Debug.isDebuggerConnected() && !Config.DEBUG) {
            //退出程序
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**
     * 检测DEX文件是否被更改
     *
     * @param context
     * @param crc
     * @return
     */
    public static void DexIsRight(Context context, long crc) {
        boolean beModified = false;
        //此值可以存在服务器，也可以存在本地
        //long crc = Long.parseLong(context.getString(R.string.crc));
        ZipFile zf;
        try {
            zf = new ZipFile(context.getApplicationContext().getPackageCodePath());
            ZipEntry ze = zf.getEntry("classes.dex");
            //存储在此处
//            SharedPreferenceUtil.saveString(context,"zcxvc",String.valueOf(ze.getCrc()));
            if (ze.getCrc() == crc) {
                beModified = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            beModified = false;
        }
        CLog.i("DEX>>> " + beModified);
        if (!beModified && !Config.DEBUG) {
            //退出程序
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        if (!beModified && Config.DEBUG) {
            ToastUtils.showMessage("APK校验错误（crc）。");
        }
        if (beModified && Config.DEBUG) {
            ToastUtils.showMessage("APK校验成功（crc）。");
        }
        //return beModified;
    }

    /***
     * 检测应用签名是否正确，是否被重新打包
     */
    public static void signIsRight(Context context, String sha1) {
        boolean isRight = false;
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            Signature[] signatures = info.signatures;
            for (Signature sign :
                    signatures) {
                byte[] signBytes = sign.toByteArray();
                String currentSign = calcSha1(signBytes);
                CLog.i("Sign>>>" + currentSign);
                isRight = sha1.equalsIgnoreCase(currentSign);
            }
        } catch (Exception e) {
            isRight = false;
        }
        CLog.i("Sign>>>" + isRight);
        if (!isRight && !Config.DEBUG) {
            //退出程序
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        if (!isRight && Config.DEBUG) {
            ToastUtils.showMessage("APK校验错误(sha1)。");
        }
        if (isRight && Config.DEBUG) {
            ToastUtils.showMessage("APK校验成功(sha1)。");
        }
    }

    private static String calcSha1(byte[] sign) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA1");
        digest.update(sign);
        byte[] signHash = digest.digest();
        return bytesToHex(signHash);
    }

    /***
     * 将字节数组转换为十六进制字符串
     * @param bytes
     * @return
     */
    private static String bytesToHex(byte[] bytes) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for (int i = 0; i < bytes.length; i++) {
            v = bytes[i] & 0xFF;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[i * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


    /**
     * 检测是否获取Root权限
     *
     * @return
     */
    public static synchronized boolean checkGetRootAuth() {
        Process process = null;
        DataOutputStream os = null;
        try {
            CLog.i("to exec su");
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes("exit\n");
            os.flush();
            int exitValue = process.waitFor();
            CLog.i("exitValue=" + exitValue);
            if (exitValue == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            CLog.i("Unexpected error - Here is what I know: " + e.getMessage());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
