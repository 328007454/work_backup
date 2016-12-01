package com.cnksi.sjjc.util;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @version 1.0
 * @auth luoxy
 * @date 16/4/24
 */
public class FileUtil {
    public static final String TAG = "FileUtil";

    public static String getPhoneCardPath() {
        return Environment.getDataDirectory().getPath();
    }

    public static String getNormalCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    public static String getOutCardPath() {
        String cmd = "cat /proc/mounts";
        Runtime runtime = Runtime.getRuntime();
        BufferedInputStream bufferedInputStream = null;
        BufferedReader bufferedReader = null;
        try {
            Process process = runtime.exec(cmd);
            bufferedInputStream = new BufferedInputStream(process.getInputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream));
            String lineResult;
            while (null != (lineResult = bufferedReader.readLine())) {
                Log.e(TAG, lineResult);
                if (lineResult.contains("/storage") && lineResult.contains("/dev") && !lineResult.contains("emulated")) {
                    String[] resultArray = lineResult.split(" ");
                    if (null != resultArray && resultArray.length >= 5) {
                        String result = resultArray[1];
                        File file = new File(result);
                        if(file.isDirectory()) {
                            File[] child = file.listFiles();
                            for (File c : child)
                                Log.e(TAG, c.getName());
                        }
                        return result;
                    }
                }
                if (process.waitFor() != 0 && process.exitValue() != 0) {
                    Log.e(TAG, "命令执行失败");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != bufferedReader) {
                    bufferedReader.close();
                }
                if (null != bufferedInputStream) {
                    bufferedInputStream.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return getNormalCardPath();
    }


}
