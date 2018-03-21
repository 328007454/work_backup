package com.cnksi.sjjc.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cnksi.core.common.ScreenManager;
import com.cnksi.sjjc.sync.DataSync;

public class USBBroardCastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_POWER_CONNECTED.equals(action)) {
            Intent newIntent = new Intent(context, DataSync.class);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 注意，必须添加这个标记，否则启动会失败
            context.startActivity(newIntent);
        } else if (Intent.ACTION_POWER_DISCONNECTED.equals(action)) {
            // 退出数据同步界面
            if (ScreenManager.getScreenManager().hasActivity(DataSync.class)) {
                ScreenManager.getScreenManager().popActivity(DataSync.class);
            }
        }
    }
}
