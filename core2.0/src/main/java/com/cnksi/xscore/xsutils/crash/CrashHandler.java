/*
 * @(#)CrashHandler.java		       Project: crash
 * Date:2014-5-26
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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 崩溃处理者。
 *
 */
public class CrashHandler implements UncaughtExceptionHandler {
	private static final CrashHandler sHandler = new CrashHandler();
	private static final UncaughtExceptionHandler sDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
	private static final ExecutorService THREAD_POOL = Executors.newSingleThreadExecutor();
	private Future<?> future;
	private CrashListener mListener;
	private SaveDbListener sListener;

	/**
	 * @return the sListener
	 */
	public SaveDbListener getsListener() {
		return sListener;
	}

	/**
	 * @param sListener
	 *            the sListener to set
	 */
	public void setsListener(SaveDbListener sListener) {
		this.sListener = sListener;
	}

	private File mLogFile;
	/**
	 * 发送报告的超时时间。
	 */
	protected int timeout = 5;

	public static CrashHandler getInstance() {
		return sHandler;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (future != null && !future.isDone()) {
			future.cancel(true);
		}

		CrashLogUtil.writeLog(mLogFile, "CrashHandler", AbstractCrashReportHandler.buildBody(), ex.getMessage(), ex);
		if (sListener != null) {
			ErrorLogBean bean = AbstractCrashReportHandler.buildBodyForBean();
			bean.exception = ex.getMessage();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			bean.exception_desc = sw.toString();
			sListener.saveErrorDb(bean);

			try {
				pw.close();
				sw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();

			}
		}
		future = THREAD_POOL.submit(new Runnable() {
			@Override
			public void run() {
				if (mListener != null) {
					mListener.afterSaveCrash(mLogFile);
				}
			};
		});
		if (!future.isDone()) {
			try {
				future.get(timeout, TimeUnit.SECONDS);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sDefaultHandler.uncaughtException(thread, ex);
	}

	/**
	 * 初始化日志文件及CrashListener对象
	 *
	 * @param logFile
	 *            保存日志的文件
	 * @param listener
	 *            回调接口
	 */
	public void init(File logFile, CrashListener listener) {
		mLogFile = logFile;
		mListener = listener;
	}

}
