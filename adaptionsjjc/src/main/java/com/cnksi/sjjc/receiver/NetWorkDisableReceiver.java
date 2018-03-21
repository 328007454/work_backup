package com.cnksi.sjjc.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.sjjc.Config;

public class NetWorkDisableReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        if (PreferencesUtils.get(Config.MASK_WIFI, true)) {
            com.cnksi.core.utils.NetWorkUtils.disableNetWork(context);
        }
    }
}
