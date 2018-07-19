package com.cnksi.api.scanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.Set;

/**
 * 发现设备
 * @author Today(张军)
 * @version v1.0
 * @date 2018/07/06 14:47
 */
public class FindDevice {
    private Callback callback;
    private Context context;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BroadcastReceiver receiver;

    public FindDevice(Context c, Callback b) {
        this.context = c;
        this.callback = b;
    }

    public Set<BluetoothDevice> getBondedDevices() {
        return bluetoothAdapter.getBondedDevices();
    }

    public boolean isScan() {
        return receiver != null;
    }

    public boolean start() {
        if (receiver != null) {
            return false;
        }

        initRecever();
        // 寻找蓝牙设备，android会将查找到的设备以广播形式发出去
        bluetoothAdapter.startDiscovery();
        callback.onScan(null, State.ON);

        return true;
    }

    private void initRecever() {
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                        callback.onScan(device, State.SUCCESS);
                    } else if (intent.getAction().equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
                        if (callback.onBind(intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE))) {
                            abortBroadcast();
                        }
                        if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                            stop();
                        }
                        //自动配对代码
//                    try {
//                        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                        com.rfidy.yuanwang.ClsUtils.cancelPairingUserInput(BluetoothDevice.class, device);
//                        com.rfidy.yuanwang.ClsUtils.setPin(bluetoothDevice.getClass(), bluetoothDevice, "1234"); // 手机和蓝牙采集器配对
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    } else if (intent.getAction().equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                        if (callback.onBind(intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE))) {
                            abortBroadcast();
                        }
                        
                        if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                            stop();
                        }
                    }
                }
            }
        };
        // 设置广播信息过滤
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        //绑定状态发生变化
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction("android.bluetooth.device.action.PAIRING_REQUEST");
        // 注册广播接收器，接收并处理搜索结果
        context.registerReceiver(receiver, intentFilter);
    }

    public boolean stop() {
        if (receiver != null) {
            bluetoothAdapter.cancelDiscovery();
            context.unregisterReceiver(receiver);
            callback.onScan(null, State.OFF);
            receiver = null;
            return true;
        }
        return false;
    }

    public boolean createBond(BluetoothDevice device) {
        if (device == null) {
            return false;
        } else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
            return true;
        } else {
            if (receiver == null) {
                initRecever();
            }

//            if (!device.createBond()) {
//                stop();
//                return false;
//            }

            return true;
        }
    }

    public interface DeviceFilter {
        /** 远望谷2600,设备名称为:xc2600 */
        String FILTER_XC2600 = "xc2600";
        /** 斑马8500,设备名称为:RFD8500... */
        String FILTER_ZEBRA_8500 = "RFD8500";

        /**
         * @param device
         * @return true不过滤设备，false过滤设备
         */
        boolean filter(BluetoothDevice device);
    }

    public interface Callback {

        /**
         * 扫描设备回调接口
         * @param device 发现的蓝牙设备，如果等于null标识该回调为状态消息
         * @param state <ul>
         * <li>当device==null时，{@link State#ON}开始扫描,{@link State#OFF}停止扫描；</li>
         * <li>当device!=null时，{@link State#ON}有效设备,{@link State#OFF}无效设备。</li>
         * <ul/>
         */
        void onScan(BluetoothDevice device, @State int state);

        /**
         * 建立绑定时回调
         * @param device
         * @return true中断广播，false继续广播
         */
        boolean onBind(BluetoothDevice device);
    }
}
