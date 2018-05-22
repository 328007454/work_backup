package com.cnksi.common.utils;

import android.support.annotation.NonNull;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Wastrel
 * @version 1.0
 * @date 2018/5/17 18:51
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class ThreadPool {
    static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, Runtime.getRuntime().availableProcessors(), 30, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(10), new ThreadFactory() {

        int i = 0;

        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread("thread-" + i++);
        }
    });

    public static void execute(Runnable r) {
        threadPoolExecutor.execute(r);
    }
}
