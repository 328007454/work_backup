package com.cnksi.api.scanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * RFID使用类<br>
 * 该类主要提供【发现设备】【链接设备】【读取数据】等对RFID扫描器的抽象方法，如果需要接入新的扫描器
 * 请实现#RfidManager接口，同一时刻仅支持同一设备。
 * @author Today(张军)
 * @version v1.0
 * @date 2018/07/02 15:42
 */
public final class SM {
    private static SM rfid;
    private FindDevice findDevice = null;
    private FindDevice.DeviceFilter deviceFilter;
    private ScannerCallBack rfidCallBack = new ScannerCallBack() {
        public void onConnecteChange(BluetoothDevice device, int state) {
            handler.post(() -> {
                synchronized (rfidCallBackSet) {
                    for (SMCallBack callBack : rfidCallBackSet) {
                        callBack.onConnecteChange(device, state);
                    }
                }
            });
        }

        public void onCommand(@Command int command, int state) {
            //为了安全起见
//            if (command == Command.RFID_ON) {
//                scanSet.clear();
//            }

            handler.post(() -> {
                synchronized (rfidCallBackSet) {
                    for (SMCallBack callBack : rfidCallBackSet) {
                        callBack.onCommand(command, state);
                    }
                }
            });
        }

        public void onReadData(String data, Object tag, @Model int model) {
            //scanSet可以根据后续情况进行调整或者删除
            //scanSet当前仅使用与RFID
            handler.post(() -> {
                synchronized (rfidCallBackSet) {
                    for (SMCallBack callBack : rfidCallBackSet) {
                        callBack.onReadData(data, tag, model);
                    }
                }
            });
        }
    };

    private FindDevice.Callback findCallback = new FindDevice.Callback() {

        @Override
        public void onScan(BluetoothDevice device, int state) {
            //过滤的设备
            if (device == null || deviceFilter == null || deviceFilter.filter(device)) {
                handler.post(() -> {
                    synchronized (rfidCallBackSet) {
                        for (SMCallBack callBack : rfidCallBackSet) {
                            callBack.onScan(device, state);
                        }
                    }
                });
            }
        }

        @Override
        public boolean onBind(BluetoothDevice device) {
            Log.e("SM", "onBond(" + device.getName() + ":" + device.getBondState() + ")");
            if (rfidManager != null) {
                return rfidManager.bond(device);
            }
            return false;
        }
    };
    private final Set<SMCallBack> rfidCallBackSet = new HashSet<>();
    private Handler handler;

//    private Set<String> scanSet = new HashSet<>();

    private SM() {
        HandlerThread handlerThread = new HandlerThread("rfid_thread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    public synchronized static SM instance() {
        if (rfid == null) {
            rfid = new SM();
        }
        return rfid;
    }

    private Scanner rfidManager;

    /**
     * @param context 初始化时不要放Activity，建议放Application，第一次使用后会会一直持有该对象。
     */
    public synchronized void initFindDevice(Context context) {
        if (findDevice == null) {
            findDevice = new FindDevice(context, findCallback);
            setDeviceFilter(device -> {
                String deviceName = device.getName();
                if (deviceName != null && (deviceName.equals(FindDevice.DeviceFilter.FILTER_XC2600) ||
                        deviceName.contains(FindDevice.DeviceFilter.FILTER_ZEBRA_8500))) {
                    return true;
                }
                return false;
            });
        }
    }

    private Context context;

    public void init(Context c) {
        if (context == null) {
            this.context = c;
        }
    }

    public void setDeviceFilter(FindDevice.DeviceFilter filter) {
        deviceFilter = filter;
    }

    /**
     * @return true执行成功, false当前正在发现设备
     */
    public boolean startScan() {
        if (findDevice == null) {
            initFindDevice(context);
        }
        return findDevice.start();
    }


    /**
     * @return true执行成功, false当前没有扫描设备或者没有初始化{@link #findDevice}
     */
    public boolean stopScan() {
        if (findDevice != null) {
            return findDevice.stop();
        }

        return false;
    }

    public Set<BluetoothDevice> getBondedDevices() {
        if (findDevice == null) {
            initFindDevice(context);
        }

        if (deviceFilter != null) {
            Set<BluetoothDevice> reSet = new HashSet<>();
            for (Iterator<BluetoothDevice> it = findDevice.getBondedDevices().iterator(); it.hasNext(); ) {
                BluetoothDevice d = it.next();
                if (deviceFilter.filter(d)) {
                    reSet.add(d);
                }
            }
            return reSet;
        } else {
            return findDevice.getBondedDevices();
        }
    }

    /**
     * 连接时根据设备名称再初始化相应扫描仪，如果没有蓝牙名称，则会初始化失败；<br/>
     * 如果需要增加、删除相关扫描仪，可以在此进行相关修改，也可以直接访问{@link #startConnecte(BluetoothDevice, Scanner)}
     */
    public boolean startConnecte(BluetoothDevice device) {
        if (device.getName().equals(FindDevice.DeviceFilter.FILTER_XC2600)) {
            if (rfidManager instanceof XC2600) {
                return startConnecte(device, rfidManager);
            } else {
                rfidManager = new XC2600(context);
                rfidManager.register(rfidCallBack);
                return startConnecte(device, rfidManager);
            }
        } else if (device.getName().contains(FindDevice.DeviceFilter.FILTER_ZEBRA_8500)) {
            if (rfidManager instanceof Zebra8500) {
                return startConnecte(device, rfidManager);
            } else {
                rfidManager = new Zebra8500(context);
                rfidManager.register(rfidCallBack);
                return startConnecte(device, rfidManager);
            }
        } else {
            //连接失败
            return false;
        }
    }

    public boolean startConnecte(BluetoothDevice device, Scanner scanner) {
        if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
            findDevice.createBond(device);
        }

        if (scanner != null) {
            rfidManager = scanner;
            return rfidManager.startConnecte(device);
        }

        return false;
    }


    public boolean stopConnected() {
        if (rfidManager != null) {
            return rfidManager.stopConnected();
        }

        return false;
    }

    public boolean startReadTag(@Model int model) {
        if (rfidManager != null) {
            if (model == Model.RFID) {
                return rfidManager.startReadCommand();
            } else if (model == Model.QR) {
                return rfidManager.startQRCommand();
            }
        }

        return false;
    }

    public boolean stopReadTag(@Model int model) {
        if (rfidManager != null) {
            if (model == Model.RFID) {
                return rfidManager.stopReadCommand();
            } else if (model == Model.QR) {
                return rfidManager.stopQRCommand();
            }
        }
        return false;
    }

    public boolean command(@Command int command) {
        if (rfidManager == null) {
            return rfidManager.command(command, null);
        }
        return false;
    }


    public void register(SMCallBack callback) {
        if (callback != null) {
            synchronized (rfidCallBackSet) {
                rfidCallBackSet.add(callback);
            }
        }
    }


    public void unregister(SMCallBack callback) {
        if (callback != null) {
            synchronized (rfidCallBackSet) {
                rfidCallBackSet.remove(callback);
            }
        }
    }

    public boolean isScan() {
        return findDevice != null && findDevice.isScan();
    }

    public boolean isConnected() {
        if (rfidManager != null) {
            //已连接、正在连接、正在关闭，统一认为是已连接
            return rfidManager.connecteState() == BluetoothAdapter.STATE_CONNECTED ||
                    rfidManager.connecteState() == BluetoothAdapter.STATE_CONNECTING ||
                    rfidManager.connecteState() == BluetoothAdapter.STATE_DISCONNECTING;
        }
        return false;
    }

    public boolean isReadRfid() {
        if (rfidManager != null) {
            return rfidManager.rfidState() == State.ON;
        }
        return false;
    }

    public boolean isReadQR() {
        if (rfidManager != null) {
            return rfidManager.qrState() == State.ON;
        }
        return false;
    }
}
