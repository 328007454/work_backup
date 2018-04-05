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
package com.cnksi.xscore.xsutils.crash;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CrashLogUtil {
	private static final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",
			Locale.getDefault());

	/**
	 * 将日志写入文件。
	 *
	 * @param tag
	 * @param deviceInfor
	 * @param message
	 * @param tr
	 */
	public static synchronized void writeLog(File logFile, String tag, String deviceInfor, String message,
			Throwable tr) {
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
			BufferedWriter bufdWriter = null;
			PrintWriter printWriter = null;
			try {
				fileWriter = new FileWriter(logFile, true);
				bufdWriter = new BufferedWriter(fileWriter);
				printWriter = new PrintWriter(fileWriter);
				bufdWriter.append(time).append('\n').append(deviceInfor).append("E").append('/').append(tag).append(" ")
						.append(message).append('\n');
				bufdWriter.flush();
				tr.printStackTrace(printWriter);

				printWriter.flush();
				fileWriter.flush();
			} catch (IOException e) {
				closeQuietly(fileWriter);
				closeQuietly(bufdWriter);
				closeQuietly(printWriter);
			}
		}
	}

	public static void closeQuietly(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException ioe) {
				// ignore
			}
		}
	}

}
