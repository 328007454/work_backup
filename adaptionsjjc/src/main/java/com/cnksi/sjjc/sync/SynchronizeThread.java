package com.cnksi.sjjc.sync;

import android.os.Handler;
import android.text.TextUtils;

import com.cnksi.core.utils.CLog;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.service.BdzService;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * Pad作为服务器端,PAD同过USB与PC相连,IP地址为127.0.0.1
 * <p/>
 * 传输Pad上的巡检结果数据库及缺陷图片
 *
 * @author dell
 */
public class SynchronizeThread implements Runnable {

    private Socket socket = null;
    private ServerSocket serverSocket = null;
    private Handler mHandler = null;

    public SynchronizeThread(ServerSocket serverSocket, Handler mHandler) {
        this.serverSocket = serverSocket;
        this.mHandler = mHandler;
        getUploadPath();
    }

    public void run() {

        boolean acceptFlag = true;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                while (acceptFlag) {
                    // 标示下载数据库DB文件是否成功
                    CLog.d("------------监听中-----------");
                    String length = "";
                    String fileName = "";
                    socket = serverSocket.accept();
                    CLog.d("建立socket链接");

                    BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
                    BufferedOutputStream out = new BufferedOutputStream(socket.getOutputStream());

                    // 获取本次通信的命令（down_文件夹名称）
                    String command = SyncUtil.readCommand(in);
                    // 获取本次通信传递文件的长度
                    length = SyncUtil.readLength(in);
                    // 获取本次通信的文件名称
                    fileName = SyncUtil.readFileName(in);

                    // 初始化pad端文件目录
                    String folder = command.substring(command.indexOf("_") + 1);

                    File folder_file = new File(Config.BDZ_INSPECTION_FOLDER, folder);
                    if (Integer.parseInt(length) > 0 && !folder_file.exists()) {
                        mHandler.obtainMessage(DataSync.SYNC_MESSAGE, "初始化文件夹" + folder).sendToTarget();
                        folder_file.mkdirs();
                    }
                    // 测试Client是否与服务器处于联通状态
                    if ("is_connected".equals(command)) {
                        mHandler.obtainMessage(DataSync.SYNC_MESSAGE, "与服务器连接成功").sendToTarget();
                        out.write(("4").getBytes());
                        out.write("-".getBytes());
                        out.write("true".getBytes());
                    } else if (command.startsWith("down")) {
                        // 文件名为空时，PC获取PAD端Folder下的所有图片名称
                        if (TextUtils.isEmpty(fileName)) {
                            mHandler.obtainMessage(DataSync.SYNC_MESSAGE, "检索" + folder + "中文件信息").sendToTarget();
                            get_diff_names(Config.BDZ_INSPECTION_FOLDER + folder, out);
                        } else {
                            // PC传输文件到PAD端
                            mHandler.obtainMessage(DataSync.SYNC_MESSAGE, "下载文件" + fileName + "到" + folder).sendToTarget();
                            SyncUtil.saveFile(in, Config.BDZ_INSPECTION_FOLDER + folder, fileName, Integer.parseInt(length));
                            out.write("ok".getBytes());
                        }
                    } else if ("upload_folder".equals(command)) {
                        // PC获取PAD端需要上传的文件夹名称
                        String uploadFolders = getUploadPath();
                        mHandler.obtainMessage(DataSync.SYNC_MESSAGE, "获取上传的文件夹" + uploadFolders).sendToTarget();
                        out.write(("" + uploadFolders.length()).getBytes());
                        out.write("-".getBytes());
                        out.write(uploadFolders.getBytes());
                    } else if (command.startsWith("upload")) {
                        if (TextUtils.isEmpty((fileName))) {
                            mHandler.obtainMessage(DataSync.SYNC_MESSAGE, "检索" + folder + "中文件信息").sendToTarget();
                            get_diff_names(Config.BDZ_INSPECTION_FOLDER + folder, out);
                        } else {
                            // PAD传输文件到PC端
                            mHandler.obtainMessage(DataSync.SYNC_MESSAGE, "上传" + folder + File.separator + fileName).sendToTarget();
                            File file = new File(Config.BDZ_INSPECTION_FOLDER + folder, fileName);
                            writeFileToOutputStream(file, out);
                        }
                    } else if ("finish".equals(command)) {
                        // 同步基础数据成功，跳转到住界面（刷新mainActivity）
                        out.write(("" + "ok".length()).getBytes());
                        out.write("-".getBytes());
                        mHandler.sendEmptyMessage(DataSync.SYNC_FINISH);
                    }

                    // else if ("pc_send_db".equals(command)) {
                    // mHandler.obtainMessage(DataSync.SYNC_MESSAGE, "正在下载数据库文件").sendToTarget();
                    // // 下载PAD端的esspad.db数据库文件
                    // SyncUtil.saveFile(in, Config.DATABASE_FOLDER, fileName, Integer.parseInt(length));
                    // out.write("ok".getBytes());
                    // } else if ("pc_send_headpics".equals(command)) {
                    // mHandler.obtainMessage(DataSync.SYNC_MESSAGE, "正在同步" + fileName).sendToTarget();
                    // SyncUtil.saveFile(in, Config.DOWNLOAD_APP_FOLDER, fileName, Integer.parseInt(length));
                    // out.write("ok".getBytes());
                    // } else if ("pad_send_headpic_names".equals(command)) {
                    // // 返回pAD端所以的头像名称
                    // get_diff_names(Config.DOWNLOAD_APP_FOLDER, out);
                    // } else if ("pad_send_report_db".equals(command)) {
                    // mHandler.obtainMessage(DataSync.SYNC_MESSAGE, "正在上传数据库文件").sendToTarget();
                    // File file = new File(Config.DATABASE_FOLDER, Config.DATABASE_NAME);
                    // writeFileToOutputStream(file, out);
                    // } else if ("pad_send_signimg_name". equals(command)) {
                    // // 发送签字照片到服务器
                    // // get_diff_names(Config.SIGN_PICTURE_FOLDER, out);
                    // } else if ("pad_send_signimg".equals(command)) {
                    // mHandler.obtainMessage(DataSync.SYNC_MESSAGE, "正在上传" + fileName).sendToTarget();
                    // // File file = new File(Config.SIGN_PICTURE_FOLDER, fileName);
                    // // writeFileToOutputStream(file, out);
                    // // file.delete();
                    // } else if ("pad_send_resultimg_name".equals(command)) {
                    // get_diff_names(Config.RESULT_PICTURES_FOLDER, out);
                    // } else if ("pad_send_resultimg".equals(command)) {
                    // mHandler.obtainMessage(DataSync.SYNC_MESSAGE, "正在上传" + fileName).sendToTarget();
                    // File file = new File(Config.RESULT_PICTURES_FOLDER, fileName);
                    // writeFileToOutputStream(file, out);
                    // // file.delete();
                    // } else if ("pad_send_resultvideo_name".equals(command)) {
                    // // 发送视频到服务器
                    // // get_diff_names(Config.VIDEO_FOLDER, out);
                    // } else if ("pad_send_resultvideo".equals(command)) {
                    // mHandler.obtainMessage(DataSync.SYNC_MESSAGE, "正在上传" + fileName).sendToTarget();
                    // // File file = new File(Config.VIDEO_FOLDER, fileName);
                    // // writeFileToOutputStream(file, out);
                    // // file.delete();
                    // } else if ("pad_send_resultaudio_name".equals(command)) {
                    // // 发送音频
                    // get_diff_names(Config.AUDIO_FOLDER, out);
                    // } else if ("pad_send_resultaudio".equals(command)) {
                    // mHandler.obtainMessage(DataSync.SYNC_MESSAGE, "正在上传" + fileName).sendToTarget();
                    // File file = new File(Config.AUDIO_FOLDER, fileName);
                    // writeFileToOutputStream(file, out);
                    // // file.delete();
                    // } else if ("pad_exit_system".equals(command)) {
                    // // 上传数据完成
                    // out.write(("" + "OK".length()).getBytes());
                    // out.write("-".getBytes());
                    // mHandler.sendEmptyMessage(DataSync.SYNC_FINISH);

                    out.flush();
                    socket.close();
                }
            }
        } catch (Exception e) {
            acceptFlag = false;
            e.printStackTrace();
        }
    }

    /**
     * 获取pad端需要同步到服务器的文件名称
     */
    private void get_diff_names(String folder, BufferedOutputStream out) {
        try {
            // 获取pad端需要同步到服务器的照片
            File file = new File(folder);
            if (file.exists()) {
                File[] files = file.listFiles();
                StringBuffer filenameBuffer = new StringBuffer();
                for (File res : files) {
                    if (res.isFile() && !res.getName().endsWith("journal") && !res.getName().equalsIgnoreCase(Config.DATABASE_NAME)) {
                        filenameBuffer.append(res.getName() + ",");
                    }
                }
                out.write(("" + filenameBuffer.length()).getBytes());
                out.write("-".getBytes());
                out.write(filenameBuffer.toString().getBytes());
            } else {
                out.write(("5").getBytes());
                out.write("-".getBytes());
                out.write("error".getBytes());
            }
        } catch (Exception ex) {
            CLog.d("获取pad端需要同步到服务器的文件名称 " + folder + "  出错 ！！");
            ex.printStackTrace();
        }
    }

    /**
     * 发送Pad端文件到同步客户端
     *
     * @param file
     * @param out
     */
    public void writeFileToOutputStream2(File file, BufferedOutputStream out) {
        try {
            int len = 0;
            if (file.exists()) {
                len = (int) file.length();
            }
            byte[] tempbuffer = new byte[20480];
            if (file.exists()) {
                out.write(("" + len).getBytes());
                out.write("-".getBytes());
                FileInputStream instream = new FileInputStream(file);
                while (instream.read(tempbuffer) != -1) {
                    out.write(tempbuffer);
                    out.flush();
                }
                instream.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 发送Pad端文件到同步客户端
     *
     * @param file
     * @param out
     */
    private void writeFileToOutputStream(File file, BufferedOutputStream out) {
        try {
            int len = 0;
            if (file.exists()) {
                len = (int) file.length();
            }

            byte[] tempbuffer = new byte[len];
            if (file.exists()) {
                FileInputStream instream = new FileInputStream(file);
                // while(instream.read(tempbuffer)!=-1){
                // out.write(tempbuffer);
                // out.flush();
                // }
                int pos = 0;
                int rcvLen = 0;
                while ((rcvLen = instream.read(tempbuffer, pos, len - pos)) > 0) {
                    pos += rcvLen;
                }
                instream.close();
            }
            out.write(("" + len).getBytes());
            out.write("-".getBytes());
            out.write(tempbuffer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getUploadPath() {


        StringBuffer sb = new StringBuffer();
        //上传数据库
        sb.append(Config.UPLOAD_DATABASE_FOLDER.replace(Config.BDZ_INSPECTION_FOLDER, "")).append(",");
        //音频
        sb.append(Config.AUDIO_FOLDER.replace(Config.BDZ_INSPECTION_FOLDER, "")).append(",");
        //log
        sb.append(Config.LOGFOLDER.replace(Config.BDZ_INSPECTION_FOLDER, "")).append(",");
        //视频
        sb.append(Config.VIDEO_FOLDER.replace(Config.BDZ_INSPECTION_FOLDER, "")).append(",");
        //签名
        sb.append(Config.SIGN_PICTURE_FOLDER.replace(Config.BDZ_INSPECTION_FOLDER, "")).append(",");
        //图片目录
        List<String> pictureFolderList = BdzService.getInstance().findUpdloadPictureFolder();
        for (String folder : pictureFolderList) {
            sb.append(folder).append(",");
        }
        return sb.deleteCharAt(sb.length() - 1).toString();

    }
}
