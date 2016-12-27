package com.cnksi.core.okhttp.callback;


import com.cnksi.core.okhttp.OkHttpUtils;
import com.cnksi.core.utils.IOUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;

/**
 * Created by zhy on 15/12/15.
 */
public abstract class FileCallBack extends Callback<File> {
    /**
     * 目标文件存储的文件夹路径
     */
    private String destFileDir;
    /**
     * 目标文件存储的文件名
     */
    private String destFileName;

    public abstract void inProgress(long progress, long total);

    public FileCallBack(String destFileDir, String destFileName) {
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
    }


    @Override
    public File parseNetworkResponse(Response response) throws Exception {
        return saveFile(response);
    }


    public File saveFile(Response response) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[10240];
        int len = 0;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            is = response.body().byteStream();
            final long total = response.body().contentLength();
            long sum = 0;

            File dir = new File(destFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, destFileName + ".tmp");
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos, 3 * 1024 * 1024);
            while ((len = is.read(buf)) != -1) {
                sum += len;
                bos.write(buf, 0, len);
                final long finalSum = sum;
                OkHttpUtils.getInstance().getDelivery().post(new Runnable() {
                    @Override
                    public void run() {
                        inProgress(total, finalSum);
                    }
                });
            }
            fos.flush();
            bos.flush();
            File newFile = new File(dir, destFileName);
            file.renameTo(newFile);
            return newFile;
        } finally {
            IOUtils.closeQuietly(bos);
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(fos);
        }
    }
}
