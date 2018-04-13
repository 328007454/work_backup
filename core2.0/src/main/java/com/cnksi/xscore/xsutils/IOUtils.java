package com.cnksi.xscore.xsutils;

import android.database.Cursor;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * 文件流操作工具类
 * 
 * @author Oliver
 * 
 */
public class IOUtils {

	private IOUtils() {
	}

	public static void closeQuietly(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Throwable ignored) {
			}
		}
	}

	public static void closeQuietly(Cursor cursor) {
		if (cursor != null) {
			try {
				cursor.close();
			} catch (Throwable ignored) {
			}
		}
	}

	public static byte[] readBytes(InputStream in) throws IOException {
		if (!(in instanceof BufferedInputStream)) {
			in = new BufferedInputStream(in);
		}
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
		} finally {
			closeQuietly(out);
		}
		return out.toByteArray();
	}

	public static byte[] readBytes(InputStream in, long skip, long size) throws IOException {
		ByteArrayOutputStream out = null;
		try {
			if (skip > 0) {
				long skipSize = 0;
				while (skip > 0 && (skipSize = in.skip(skip)) > 0) {
					skip -= skipSize;
				}
			}
			out = new ByteArrayOutputStream();
			for (int i = 0; i < size; i++) {
				out.write(in.read());
			}
		} finally {
			closeQuietly(out);
		}
		return out.toByteArray();
	}

	public static String readStr(InputStream in) throws IOException {
		return readStr(in, "UTF-8");
	}

	public static String readStr(InputStream in, String charset) throws IOException {
		if (TextUtils.isEmpty(charset))
			charset = "UTF-8";

		if (!(in instanceof BufferedInputStream)) {
			in = new BufferedInputStream(in);
		}
		Reader reader = new InputStreamReader(in, charset);
		StringBuilder sb = new StringBuilder();
		char[] buf = new char[1024];
		int len;
		while ((len = reader.read(buf)) >= 0) {
			sb.append(buf, 0, len);
		}
		return sb.toString().trim();
	}

	public static void writeStr(OutputStream out, String str) throws IOException {
		writeStr(out, str, "UTF-8");
	}

	public static void writeStr(OutputStream out, String str, String charset) throws IOException {
		if (TextUtils.isEmpty(charset))
			charset = "UTF-8";

		Writer writer = new OutputStreamWriter(out, charset);
		writer.write(str);
		writer.flush();
	}

	public static void copy(InputStream in, OutputStream out) throws IOException {
		if (!(in instanceof BufferedInputStream)) {
			in = new BufferedInputStream(in);
		}
		if (!(out instanceof BufferedOutputStream)) {
			out = new BufferedOutputStream(out);
		}
		int len = 0;
		byte[] buffer = new byte[1024];
		while ((len = in.read(buffer)) != -1) {
			out.write(buffer, 0, len);
		}
		out.flush();
	}

	public static boolean deleteFileOrDir(File path) {
		if (path == null || !path.exists()) {
			return true;
		}
		if (path.isFile()) {
			return path.delete();
		}
		File[] files = path.listFiles();
		if (files != null) {
			for (File file : files) {
				deleteFileOrDir(file);
			}
		}
		return path.delete();
	}

	/**
	 * 写文件到SdCard上
	 * 
	 * @param filePath
	 *            文件的绝对路径
	 * @param content
	 *            要写入的内容
	 * @return
	 */
	public static boolean writeStr2File(String filePath, String content) {
		return writeStr2File(filePath, content, false);
	}

	/**
	 * 写文件到SdCard上
	 * 
	 * @param filePath
	 *            文件的绝对路径
	 * @param content
	 *            要写入的内容
	 * @param isAppend
	 *            是否在源文件内容基础上继续添加
	 * @return
	 */
	public static boolean writeStr2File(String filePath, String content, boolean isAppend) {
		boolean isSuccess = false;
		if (!TextUtils.isEmpty(content) && !"null".equalsIgnoreCase(content)) {
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(filePath, isAppend);
				StringBuffer sb = new StringBuffer();
				sb.append(isAppend ? content + "\n" : content);
				fos.write(sb.toString().getBytes("utf-8"));
				fos.flush();
				isSuccess = true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				closeQuietly(fos);
			}
		}
		return isSuccess;
	}

	/**
	 * 从文件中读取字符串
	 * 
	 * @param filePath
	 *            文件的绝对路径
	 * @return
	 * @throws IOException
	 */
	public static String readStrFormFile(String filePath) {
		String result = "";
		if (FileUtils.isFileExists(filePath)) {
			FileInputStream fin = null;
			try {
				fin = new FileInputStream(filePath);
				int length = fin.available();
				byte[] buffer = new byte[length];
				fin.read(buffer);
				result = new String(buffer, "UTF-8");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e2) {
				e2.printStackTrace();
			} finally {
				closeQuietly(fin);
			}
		}
		return result;
	}
}
