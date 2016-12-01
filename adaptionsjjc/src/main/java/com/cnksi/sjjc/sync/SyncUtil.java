package com.cnksi.sjjc.sync;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.cnksi.core.utils.CLog;

/**
 * 数据同步Util
 * 
 * @author dell
 * 
 */
public class SyncUtil {

	/**
	 * 读取命令
	 * 
	 * @param in
	 * @return
	 */
	public static String readCommand(InputStream in) {
		String command = "";
		byte[] tempbuffer = new byte[20];
		try {
			while (true) {
				int numReadedBytes = in.read(tempbuffer, 0, 1);

				String cmd = new String(tempbuffer, 0, numReadedBytes, "utf-8");

				if ("-".equals(cmd)) {
					break;
				} else {
					command += cmd;
				}
			}

			tempbuffer = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return command;
	}

	/**
	 * 读取文件长度
	 * 
	 * @param in
	 * @return
	 */
	public static String readLength(InputStream in) {
		String length = "";
		byte[] tempbuffer = new byte[20];
		try {
			while (true) {
				int numReadedBytes = in.read(tempbuffer, 0, 1);
				String split = new String(tempbuffer, 0, numReadedBytes, "utf-8");
				if ("-".equals(split)) {
					break;
				} else {
					length += split;
				}
			}
			tempbuffer = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return length;
	}

	/**
	 * 读取文件名称
	 * 
	 * @param in
	 * @return
	 */
	public static String readFileName(InputStream in) {

		String fileName = "";
		byte[] tempbuffer = new byte[20];
		try {
			while (true) {
				int numReadedBytes = in.read(tempbuffer, 0, 1);
				String split = new String(tempbuffer, 0, numReadedBytes, "utf-8");
				if ("-".equals(split)) {
					break;
				} else {
					fileName += split;
				}
			}
			tempbuffer = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileName;
	}

	/**
	 * 读取文件并保存
	 * 
	 * @param in
	 * @return
	 */
	public static boolean saveFile(InputStream in, String path, String fileName, int length) {

		String filePath = path.endsWith("/") ? path + fileName : path + "/" + fileName;
		try {
			File padfile = new File(filePath);
			int bufferSize = 8192;
			byte[] buf = new byte[bufferSize];
			int passedlen = 0;
			DataOutputStream fileOut = new DataOutputStream(new FileOutputStream(padfile));
			while (true) {
				int read = 0;
				int diff = length - passedlen;
				if (in != null) {
					if (diff < 1) {
						break;
					} else if (diff < bufferSize) {
						byte[] buf2 = new byte[length - passedlen];
						read = in.read(buf2);
						fileOut.write(buf2, 0, read);
					} else {
						read = in.read(buf);
						fileOut.write(buf, 0, read);
					}
					passedlen += read;
				}
			}
			CLog.d("接收完成，文件存为" + filePath + "\n");
			fileOut.flush();
			fileOut.close();

			System.gc();

			return true;
		} catch (Exception e) {
			e.printStackTrace();

		}
		return false;
	}
}
