package com.cnksi.bdzinspection.reciever;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

public class BluetoothBroardCastReceiver extends BroadcastReceiver {
    private BluetoothDevice device;
    private ArrayList<BluetoothDevice> mBoundList = new ArrayList<>();
    private BluetoothDeviceListener listener;

    public BluetoothBroardCastReceiver() {
    }

    public interface BluetoothDeviceListener {
        void boundDevice(ArrayList<BluetoothDevice> deviceArrayList);
    }

    public void setBluetoothListener(BluetoothDeviceListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        String action = intent.getAction();
        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {

        } else if (intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                Log.i("Bond", "it's boned:" + device.getName() + "----" + device.getAddress());
                if (!mBoundList.contains(device)) {
                    mBoundList.add(device);
                }
                if (null != listener) {
                    listener.boundDevice(mBoundList);
                }
            } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                if (null != mBoundList && !mBoundList.isEmpty()) {
                    if (mBoundList.contains(device)) {
                        mBoundList.remove(device);
                    }
                    listener.boundDevice(mBoundList);
                }
            }
        }else if(intent.getAction().equals(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)){
            if (!mBoundList.contains(device)) {
                mBoundList.add(device);
            }
        }
    }
}
