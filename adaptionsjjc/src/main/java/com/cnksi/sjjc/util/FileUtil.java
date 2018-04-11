package com.cnksi.sjjc.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.cnksi.core.utils.FileUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;

import org.xutils.common.util.DatabaseUtils;
import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

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
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtil {

    private static final int BUFFER_SIZE = 2048;

    private static ArrayList<String> filelist = new ArrayList<String>();

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
    public static boolean makeDirectory(String directory, boolean createParents) {
        boolean created;
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

//    /**
//     * 获取文件夹下的所有文件夹和文件
//     *
//     * @param filePath
//     * @return
//     */
//    public static ArrayList<FileInfo> getAllFolderAndFiles(String filePath) {
//        if (!mFileLists.isEmpty()) mFileLists.clear();
//        File root = new File(filePath);
//        if (root != null && root.exists()) {
//            File[] files = root.listFiles();
//            if (files != null && files.length > 0) {
//                for (int i = 0; i < files.length; i++) {
//                    if (files[i].isHidden()) // 不显示隐藏文件
//                        continue;
//                    String fileAbsolutePath = files[i].getAbsolutePath();
//                    String fileName = files[i].getName();
//                    boolean isDirectory = false;
//                    if (files[i].isDirectory()) {
//                        isDirectory = true;
//                    }
//                    FileInfo fileInfo = new FileInfo(fileAbsolutePath, fileName, isDirectory);
//                    mFileLists.add(fileInfo);
//                }
//            }
//        }
//        return mFileLists;
//    }


    /**
     * 删除后缀名相同的文件
     *
     * @param folder
     * @param suffix
     */
    public static void deleteFileBySuffix(String folder, String suffix) {
        File rootFile = new File(folder);
        if (rootFile != null && rootFile.exists()) {
            File[] files = rootFile.listFiles();
            if (files != null && files.length > 0) {
                for (int i = 0, count = files.length; i < count; i++) {
                    String fileAbsolutePath = files[i].getAbsolutePath();
                    if (fileAbsolutePath.endsWith(suffix)) {
                        deleteFile(fileAbsolutePath);
                    }
                }
            }
        }
    }

    /**
     * 递归拷贝assets文件到SD卡
     *
     * @param context
     * @param targetFolder
     * @param assetsNames
     */
    public static boolean copyAssetsToSDCard(Context context, String targetFolder, String... assetsNames) {
        return copyAssetsToSDCard(context, targetFolder, false, assetsNames);
    }

    /**
     * 递归拷贝assets文件到SD卡
     *
     * @param context
     * @param targetFolder
     * @param isOverlay
     * @param assetsNames
     */
    public static boolean copyAssetsToSDCard(Context context, String targetFolder, boolean isOverlay, String... assetsNames) {
        boolean isSuccess = true;
        if (assetsNames != null && assetsNames.length > 0) {
            for (String name : assetsNames) {
                File file = new File(targetFolder, name);
                File folder = new File(targetFolder);
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                if (isOverlay) {
                    if (file.exists()) {
                        file.delete();
                    }
                } else {
                    if (file.exists()) {
                        continue;
                    }
                }
                try {
                    InputStream is = context.getApplicationContext().getClass().getClassLoader().getResourceAsStream("assets/" + name);
                    FileOutputStream fos = new FileOutputStream(file);
                    int len;
                    byte[] buffer = new byte[BUFFER_SIZE];
                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    fos.flush();
                    is.close();
                    fos.close();
                } catch (IOException e) {
                    isSuccess = false;
                    break;
                }
            }
        }
        return isSuccess;
    }

    /**
     * 解压文件到指定目录 文件中不能有中文名称
     *
     * @param zipFile
     * @param targetDir
     */
    public static void unZipFile(String zipFile, String targetDir) {
        try {
            BufferedInputStream bi;
            ZipFile zf = new ZipFile(zipFile);
            Enumeration e = zf.entries();
            while (e.hasMoreElements()) {
                ZipEntry ze2 = (ZipEntry) e.nextElement();
                String entryName = ze2.getName();
                String path = targetDir + "/" + entryName;
                if (ze2.isDirectory()) {
                    File decompressDirFile = new File(path);
                    if (!decompressDirFile.exists()) {
                        decompressDirFile.mkdirs();
                    }
                } else {
                    String fileDir = path.substring(0, path.lastIndexOf("/"));
                    File fileDirFile = new File(fileDir);
                    if (!fileDirFile.exists()) {
                        fileDirFile.mkdirs();
                    }
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(targetDir + "/" + entryName));
                    bi = new BufferedInputStream(zf.getInputStream(ze2));
                    byte[] readContent = new byte[BUFFER_SIZE];
                    int readCount = bi.read(readContent);
                    while (readCount != -1) {
                        bos.write(readContent, 0, readCount);
                        readCount = bi.read(readContent);
                    }
                    bos.close();
                }
            }
            zf.close();
        } catch (IOException e) {
            e.printStackTrace();
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
     * @param isOverlay   如果目标目录存在，是否覆盖
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
                    if (!flag) break;
                } else if (file.isDirectory()) {
                    flag = copyDirectory(file.getAbsolutePath(), destDirName + file.getName(), isOverlay);
                    if (!flag) break;
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
     * @param srcFileName  待复制的文件名
     * @param destFileName 目标文件名
     * @return 如果复制成功返回true，否则返回false
     */
    public static boolean copyFile(String srcFileName, String destFileName) {
        return copyFile(srcFileName, destFileName, true);
    }

    /**
     * 复制单个文件
     *
     * @param srcFileName  待复制的文件名
     * @param destFileName 目标文件名
     * @param isOverlay    如果目标文件存在，是否覆盖
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

}
