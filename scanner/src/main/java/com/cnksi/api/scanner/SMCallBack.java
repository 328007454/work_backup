package com.cnksi.api.scanner;

import android.bluetooth.BluetoothDevice;

/**
 * @author Today(张军)
 * @version v1.0
 * @date 2018/07/06 17:04
 */
public interface SMCallBack extends ScannerCallBack {
    void onScan(BluetoothDevice device, @State int state);
}
