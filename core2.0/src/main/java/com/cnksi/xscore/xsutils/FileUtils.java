package com.cnksi.xscore.xsutils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.cnksi.xscore.xscommon.FileInfo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils {

    private static final int BUFFER_SIZE = 2048;

    private static ArrayList<String> filelist = new ArrayList<String>();
    private static ArrayList<FileInfo> mFileLists = new ArrayList<FileInfo>();

    public static void initFile(String[] pathArray) {
        for (int i = 0, count = pathArray.length; i < count; i++) {
            if (!isFolderExists(pathArray[i])) {
                makeDirectory(pathArray[i]);
            }
        }
    }

    /**
     * 创建文件夹
     *
     * @param directory
     * @return
     * @throws IOException
     */
    public static boolean makeDirectory(String directory) {
        return makeDirectory(directory, true);
    }

    /**
     * 创建文件夹
     *
     * @param directory
     * @param createParents
     * @return
     * @throws IOException
     */
    private static boolean makeDirectory(String directory, boolean createParents) {
        boolean created = false;
        try {
            File dir = new File(directory);
            if (createParents) {
                created = dir.mkdirs();
            } else {
                if (dir.isFile()) {
                    created = dir.createNewFile();
                } else {
                    created = dir.mkdir();
                }
            }
        } catch (Exception e) {
            created = false;
        }
        return created;
    }

    /**
     * @param filePath
     * @return
     */
    public static boolean isFileExists(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        return file.isFile() && file.exists();
    }

    /**
     * @param filePath
     * @return
     */
    public static boolean isFolderExists(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        return file.isDirectory() && file.exists();
    }

    /**
     * 递归删除文件夹下的所有文件
     *
     * @param f
     */
    public static void deleteAllFiles(File f) {
        if (f.exists()) {
            File[] files = f.listFiles();
            if (files != null) {
                for (File file : files)
                    if (file.isDirectory()) {
                        deleteAllFiles(file);
                        // 删除目录下的所有文件后，该目录变成了空目录，可直接删除
                        file.delete();
                    } else if (file.isFile()) {
                        file.delete();
                    }
            }
            // 删除最外层的目录
            f.delete();
        }
    }

    /**
     * 递归删除文件夹下的所有文件
     *
     * @param filePath 文件路径
     */
    public static void deleteAllFiles(String filePath) {
        deleteAllFiles(new File(filePath));
    }

    /**
     * 递归删除文件夹下的所有文件
     *
     * @param filePaths 文件路径集合
     */
    public static void deleteAllFiles( List<String> filePaths) {
        if (null != filePaths && !filePaths.isEmpty()) {
            for (String file : filePaths)
                deleteAllFiles(new File(file));
        }
    }

    /**
     * 删除单个文件
     *
     * @param filePath
     * @return
     */
    public static boolean deleteFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return true;
        } else {
            File file = new File(filePath);
            if (file.exists()) {
                return file.delete();
            } else {
                return true;
            }
        }
    }

    /**
     * 清除所有缓存
     *
     * @param context
     */
    public static void deleteAllCache(Context context) {
        if (context.getCacheDir() != null && context.getCacheDir().exists()) {
            deleteAllFiles(context.getCacheDir());
        }
        if (context.getExternalCacheDir() != null && context.getExternalCacheDir().exists()) {
            deleteAllFiles(context.getExternalCacheDir());
        }
    }

    /**
     * 删除多久之前的备份文件
     *
     * @param filePath 文件路径
     * @param timeAgo  多久之前的时间 long oneMonthAgoTime = Long.valueOf(((long) 7 * (long) 24 * (long) 60 * (long) 60 * (long) 1000));
     */
    public static void deleteBakFiles(String filePath, long timeAgo) {
        File bakFile = new File(filePath);
        long time = Calendar.getInstance().getTimeInMillis() - timeAgo;
        if (bakFile.exists()) {
            if (bakFile.isDirectory()) {
                for (File file : bakFile.listFiles()) {
                    if (file.lastModified() < time) {
                        FileUtils.deleteFile(file.getAbsolutePath());
                    }
                }
            } else {
                if (bakFile.lastModified() < time) {
                    FileUtils.deleteFile(bakFile.getAbsolutePath());
                }
            }
        }
    }

    /**
     * @param from 原文件名
     * @param to   新文件名
     */
    public static boolean reName(String from, String to) {
        boolean flag = false;
        File oldFile = new File(from); // 要重命名的文件或文件夹
        if (oldFile.exists()) {
            File newFile = new File(to); // 重命名为zhidian1
            if (newFile.exists()) {
                newFile.delete();
            }
            flag = oldFile.renameTo(newFile); // 执行重命名
        }
        return flag;
    }

    /**
     * 递归找出某个文件夹下的所有文件 默认递归
     *
     * @param filePath
     */
    public static ArrayList<String> getAllFiles(String filePath) {
        return getAllFiles(filePath, true, true);
    }

    /**
     * 递归找出某个文件夹下的所有文件
     *
     * @param filePath    文件路径
     * @param isRecursive 是否递归
     */
    public static ArrayList<String> getAllFiles(String filePath, boolean isRecursive) {
        return getAllFiles(filePath, isRecursive, true);
    }

    /**
     * 获取文件夹下面的所有文件
     *
     * @param filePath    文件路径
     * @param isRecursive 是否递归 是 获取所有子文件夹下的文件
     * @return
     */
    private static ArrayList<String> getAllFiles(String filePath, boolean isRecursive, boolean isCleanList) {
        if (isCleanList) {
            filelist.clear();
        }
        if (!TextUtils.isEmpty(filePath)) {
            File root = new File(filePath);
            if (root != null) {
                if (root.isDirectory()) {
                    File[] files = root.listFiles();
                    if (files != null && files.length > 0) {
                        for (File file : files) {
                            if (file.isDirectory()) {
                                if (isRecursive) {
                                    getAllFiles(file.getAbsolutePath(), true, false);// 递归调用
                                }
                            } else {
                                filelist.add(file.getAbsolutePath());
                            }
                        }
                    }
                } else {
                    filelist.add(root.getAbsolutePath());
                }
            }
        }
        return filelist;
    }

    /**
     * 获取文件夹下的所有文件夹和文件
     *
     * @param filePath
     * @return
     */
    public static ArrayList<FileInfo> getAllFolderAndFiles(String filePath) {
        if (!mFileLists.isEmpty())
            mFileLists.clear();
        File root = new File(filePath);
        if (root != null && root.exists()) {
            File[] files = root.listFiles();
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isHidden()) // 不显示隐藏文件
                        continue;
                    String fileAbsolutePath = files[i].getAbsolutePath();
                    String fileName = files[i].getName();
                    boolean isDirectory = false;
                    if (files[i].isDirectory()) {
                        isDirectory = true;
                    }
                    FileInfo fileInfo = new FileInfo(fileAbsolutePath, fileName, isDirectory);
                    mFileLists.add(fileInfo);
                }
            }
        }
        return mFileLists;
    }

    /**
     * 递归拷贝assets文件到SD卡
     *
     * @param context
     * @param assetDir
     * @param dir
     */
    public static boolean copyAssetsToSDCard(Context context, String assetDir, String dir) {
        boolean isSuccess = false;
        String[] files = null;
        try {
            files = context.getResources().getAssets().list(assetDir);
        } catch (IOException e1) {
            return isSuccess;
        }
        File mWorkingPath = new File(dir);
        if (!mWorkingPath.exists()) {
            if (!mWorkingPath.mkdirs()) {
                CLog.e("--CopyAssets--", "cannot create directory.");
            }
        }
        if (files != null) {
            for (String fileName : files) {
                try {
                    File file = new File(fileName);
                    if (file.isDirectory()) {
                        if (0 == assetDir.length()) {
                            copyAssetsToSDCard(context, fileName, dir + fileName + "/");
                        } else {
                            copyAssetsToSDCard(context, assetDir + "/" + fileName, dir + fileName + "/");
                        }
                        continue;
                    }
                    File outFile = new File(mWorkingPath, fileName);
                    if (outFile.exists())
                        outFile.delete();
                    InputStream in = null;
                    if (0 != assetDir.length())
                        in = context.getAssets().open(assetDir + "/" + fileName);
                    else
                        in = context.getAssets().open(fileName);
                    OutputStream out = new FileOutputStream(outFile);
                    byte[] buf = new byte[BUFFER_SIZE];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();
                    isSuccess = true;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    isSuccess = false;
                } catch (IOException e) {
                    e.printStackTrace();
                    isSuccess = false;
                }
            }
        }
        return isSuccess;
    }

    /**
     * 解压文件到指定目录
     *
     * @param zipFile
     * @param targetDir
     */
    public static void unZip(String zipFile, String targetDir) {
        String strEntry; // 保存每个zip的条目名称
        try {
            BufferedOutputStream dest = null; // 缓冲输出流
            FileInputStream fis = new FileInputStream(zipFile);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry = null; // 每个zip条目的实例
            while ((entry = zis.getNextEntry()) != null) {
                try {
                    int count;
                    byte data[] = new byte[BUFFER_SIZE];
                    strEntry = entry.getName();

                    File entryFile = new File(targetDir + strEntry);
                    File entryDir = new File(entryFile.getParent());
                    if (!entryDir.exists()) {
                        entryDir.mkdirs();
                    }
                    FileOutputStream fos = new FileOutputStream(entryFile);
                    dest = new BufferedOutputStream(fos, BUFFER_SIZE);
                    while ((count = zis.read(data, 0, BUFFER_SIZE)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            zis.close();
        } catch (Exception cwj) {
            cwj.printStackTrace();
        }
    }

    /**
     * 复制整个目录的内容 覆盖
     *
     * @param srcDirName  待复制目录的目录名
     * @param destDirName 目标目录名
     * @return 如果复制成功返回true，否则返回false
     */
    public static boolean copyDirectory(String srcDirName, String destDirName) {
        return copyDirectory(srcDirName, destDirName, true);
    }

    /**
     * 复制整个目录的内容
     *
     * @param srcDirName  待复制目录的目录名
     * @param destDirName 目标目录名
     * @return 如果复制成功返回true，否则返回false
     */
    public static boolean copyDirectory(String srcDirName, String destDirName, boolean isOverlay) {

        // 判断源目录是否存在
        File srcDir = new File(srcDirName);
        if (!srcDir.exists()) {
            return false;
        } else if (!srcDir.isDirectory()) {
            return false;
        }

        // 如果目标目录名不是以文件分隔符结尾，则加上文件分隔符
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        File destDir = new File(destDirName);
        // 如果目标文件夹存在
        if (destDir.exists()) {
            // 如果允许覆盖则删除已存在的目标目录
            if (isOverlay) {
                new File(destDirName).delete();
            } else {
                return true;
            }
        } else {
            // 创建目的目录
            if (!destDir.mkdirs()) {
                return false;
            }
        }

        boolean flag = true;
        File[] files = srcDir.listFiles();
        if (files != null) {
            for (File file : files) {
                // 复制文件
                if (file.isFile()) {
                    flag = copyFile(file.getAbsolutePath(), destDirName + file.getName(), isOverlay);
                    if (!flag)
                        break;
                } else if (file.isDirectory()) {
                    flag = copyDirectory(file.getAbsolutePath(), destDirName + file.getName(), isOverlay);
                    if (!flag)
                        break;
                }
            }
        }
        if (!flag) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 复制单个文件 覆盖
     *
     * @param srcFileName 目标文件名
     * @return 如果复制成功返回true，否则返回false
     */
    public static boolean copyFile(String srcFileName, String destFileName) {
        return copyFile(srcFileName, destFileName, true);
    }

    /**
     * 复制单个文件
     *
     * @param srcFileName 如果目标文件存在，是否覆盖
     * @return 如果复制成功返回true，否则返回false
     */
    public static boolean copyFile(String srcFileName, String destFileName, boolean isOverlay) {
        File srcFile = new File(srcFileName);
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            return false;
        } else if (!srcFile.isFile()) {
            return false;
        }
        // 判断目标文件是否存在
        File destFile = new File(destFileName);
        if (destFile.exists()) {
            // 如果目标文件存在并允许覆盖
            if (isOverlay) {
                // 删除已经存在的目标文件，无论目标文件是目录还是单个文件
                boolean isSuccess = new File(destFileName).delete();
                if (!isSuccess) {
                    return isSuccess;
                }
            } else {
                return true;
            }
        } else {
            // 如果目标文件所在目录不存在，则创建目录
            if (!destFile.getParentFile().exists()) {
                // 目标文件所在目录不存在
                if (!destFile.getParentFile().mkdirs()) {
                    // 复制文件失败：创建目标文件所在目录失败
                    return false;
                }
            }
        }
        // 文件通道的方式来进行文件复制
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(srcFile);
            outStream = new FileOutputStream(destFile);
            FileDescriptor fd = outStream.getFD();
            in = inStream.getChannel();
            out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
            fd.sync();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            close(inStream);
            close(in);
            close(outStream);
            close(out);
        }
        return true;

        // 复制文件
        // int byteread = 0; // 读取的字节数
        // FileInputStream in = null;
        // FileOutputStream out = null;
        // try {
        // in = new FileInputStream(srcFile);
        // out = new FileOutputStream(destFile);
        // FileDescriptor fd = out.getFD();
        // byte[] buffer = new byte[BUFFER_SIZE];
        // while ((byteread = in.read(buffer)) != -1) {
        // out.write(buffer, 0, byteread);
        // }
        // out.flush();
        // // 确保文件已复制到磁盘中
        // fd.sync();
        // return true;
        // } catch (FileNotFoundException e) {
        // return false;
        // } catch (IOException e) {
        // return false;
        // } finally {
        // close(out);
        // close(in);
        // }
    }

    /**
     * 关闭流
     *
     * @param obj
     */
    private static void close(Object obj) {
        try {
            if (obj != null) {
                if (obj instanceof InputStream) {
                    ((InputStream) obj).close();
                } else if (obj instanceof OutputStream) {
                    ((OutputStream) obj).close();
                } else if (obj instanceof FileChannel) {
                    ((FileChannel) obj).close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件的最后修改时间是否小于给定的时间
     *
     * @param file 文件
     * @param time 对比时间
     * @return
     */
    public static boolean isModifyThanTime(File file, long time) {
        if (file != null && file.exists()) {
            if (file.lastModified() < time) {
                return true;
            }
        }
        return false;
    }

    /**
     * 递归删除 某个文件夹下 最后修改时间小于time的文件 filePath 文件名 time 时间
     *
     * @param filePath
     * @param time     // 删除一个星期前的备份文件 long time = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L;
     */
    public static void deleteFileByModifyTime(String filePath, long time) {
        if (!TextUtils.isEmpty(filePath)) {
            File root = new File(filePath);
            if (root != null) {
                if (root.isDirectory()) {
                    File[] files = root.listFiles();
                    for (File file : files) {
                        if (file.isDirectory()) {
                            deleteFileByModifyTime(file.getAbsolutePath(), time);
                        }
                    }
                } else {
                    if (isModifyThanTime(root, time)) {
                        root.delete();
                    }
                }
            }
        }
    }

    /**
     * 打开PDF文件
     *
     * @param context
     * @param pdfPath
     */
    public static void openPdfFile(Context context, String pdfPath) {
        File ruleFile = new File(pdfPath);
        if (ruleFile != null && ruleFile.exists()) {
            Uri path = Uri.fromFile(ruleFile);
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(path, "application/pdf");
            pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            try {
                context.startActivity(pdfIntent);
            } catch (ActivityNotFoundException e) {
                CToast.showShort(context, "没有打开此文件的应用，请安装PDF阅读器");
            }
        } else {
            CToast.showShort(context, "文件不存在!");
        }
    }
}
