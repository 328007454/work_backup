package com.cnksi.bdzinspection.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.databinding.XsActivityBluedeviceBinding;
import com.cnksi.common.base.BaseActivity;

/**
 *
 * @auth han
 * @date 2017/1/5 17:32
 * @since 1.0
 * @version 1.0
 * @copyRight 四川金信石信息技术有限公司
 */

public class BlueDeviceActivity extends BaseActivity {
    // 调试用
    private static final String TAG = "DeviceListActivity";
    private static final boolean D = true;
    // 返回时数据标签
    public static String EXTRA_DEVICE_ADDRESS = "设备地址";

    // 成员域
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private XsActivityBluedeviceBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         binding = DataBindingUtil.setContentView(mActivity,R.layout.xs_activity_bluedevice);
        setResult(Activity.RESULT_CANCELED);
        binding.buttonScan.setOnClickListener(view -> {
            doDiscovery();
            view.setVisibility(View.GONE);
        });

        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this,
                R.layout.xs_device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this,
                R.layout.xs_device_name);

        //已经配对的设备列表
        binding.pairedDevices.setAdapter(mPairedDevicesArrayAdapter);
        binding.pairedDevices.setOnItemClickListener(mDeviceClickListener);

        //为配对的设备列表
        binding.newDevices.setAdapter(mNewDevicesArrayAdapter);
        binding.newDevices.setOnItemClickListener(mDeviceClickListener);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    private void doDiscovery() {
        if (D) {
            Log.d(TAG, "doDiscovery()");
        }
        binding.titleNewDevices.setVisibility(View.VISIBLE);
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        mBtAdapter.startDiscovery();
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            mBtAdapter.cancelDiscovery();
            String info = ((TextView) v).getText().toString();
            if (info.length() < 17) {
                finish();
                return;
            }
            String address = info.substring(info.length() - 17);
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n"
                            + device.getAddress());
                } else {
                    mPairedDevicesArrayAdapter.add(device.getName() + "\n"
                            + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = context.getResources().getString(
                            R.string.xs_listnotgetdev);
                    mNewDevicesArrayAdapter.add(noDevices);
                }
            }
        }
    };
}
