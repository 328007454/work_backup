package com.cnksi.sjjc.service.androidservice;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by han on 2016/11/29.
 */

public abstract  class BaseService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BaseService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        start(intent,startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startConmmand(intent,flags,startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        handleIntent(intent);
    }

    public abstract  void handleIntent(Intent intent);
    public abstract  void start(Intent intent,int startId);
    public abstract  void startConmmand(Intent intent,int flags,int startId);
}
