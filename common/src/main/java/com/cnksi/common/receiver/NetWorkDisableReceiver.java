package com.cnksi.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cnksi.common.Config;
import com.cnksi.core.utils.NetWorkUtils;
import com.cnksi.core.utils.PreferencesUtils;

/**
 * @author Wastrel
 */
public class NetWorkDisableReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        if (PreferencesUtils.get(Config.MASK_WIFI, true) && NetWorkUtils.isWifiConnected(context)) {
            NetWorkUtils.setWifiEnabled(context, false);
        }
    }
}
