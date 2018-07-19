package com.cnksi.api.scanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.cnksi.api.scanner.utils.ClsUtils;
import com.zebra.scannercontrol.DCSSDKDefs;

import invengo.javaapi.core.BaseReader;
import invengo.javaapi.core.IMessage;
import invengo.javaapi.core.IMessageNotification;
import invengo.javaapi.core.Util;
import invengo.javaapi.handle.IMessageNotificationReceivedHandle;
import invengo.javaapi.protocol.IRP1.GBInventoryTag;
import invengo.javaapi.protocol.IRP1.PowerOff;
import invengo.javaapi.protocol.IRP1.PowerOff_800;
import invengo.javaapi.protocol.IRP1.RXD_ReaderTriggerStatus;
import invengo.javaapi.protocol.IRP1.RXD_TagData;
import invengo.javaapi.protocol.IRP1.ReadTag;
import invengo.javaapi.protocol.IRP1.Reader;
import invengo.javaapi.protocol.receivedInfo.BarcodeReceivedInfo;

/**
 * 2600实现类
 * @author Today(张军)
 * @version v1.0
 * @date 2018/07/02 15:41
 */
public class XC2600 implements Scanner {
    //所有状态在回调前改编
    int connecteState = BluetoothAdapter.STATE_DISCONNECTED;
    int rfidState = State.OFF;
    int qrState = State.OFF;

    private ScannerCallBack rfidCallBack;
    private BluetoothDevice bluetoothDevice;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BroadcastReceiver receiver;
    private Reader reader;

    private Context context;
    private Handler handler;

    public XC2600(Context c) {
        this.context = c;
        HandlerThread handlerThread = new HandlerThread("XC2600_SendThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                boolean result = false;
                try {
                    switch (msg.what) {
                        case Command.RFID_ON:
//                            readerDevice.getRFIDReader().Actions.Inventory.perform();
                            ReadTag readTag = new ReadTag(ReadTag.ReadMemoryBank.EPC_6C);
                            result = exeCmd(readTag);
                            if (result) {
                                rfidState = State.ON;
                            }
                            break;
                        case Command.RFID_OFF:
//                            readerDevice.getRFIDReader().Actions.Inventory.stop();
                            result = exeCmd(new PowerOff_800());
                            rfidState = State.OFF;
                            result = true;
                            break;
                        case Command.QR_ON:
                            break;
                        case Command.QR_OFF:

                            break;
                        case Command.DEVICE_CONFIG:
                            //需要分RFID和QR
                            break;
                        default:
                            break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (result) {
                    rfidCallBack.onCommand(msg.what, State.SUCCESS);
                } else {
                    rfidCallBack.onCommand(msg.what, State.ERROR);
                }
            }
        };
    }

    @Override
    public boolean bond(BluetoothDevice device) {
        if (device.getName() != null && device.getName().equals(FindDevice.DeviceFilter.FILTER_XC2600)) {
            if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                rfidCallBack.onConnecteChange(device, State.ERROR);
            } else if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                try {
                    ClsUtils.cancelPairingUserInput(device.getClass(), device);
                    ClsUtils.setPin(device.getClass(), device, "1234"); // 手机和蓝牙采集器配对
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }


    @Override
    public boolean startConnecte(BluetoothDevice device) {
        if (reader != null) {
            stopConnected();
        }
        connecteState = BluetoothAdapter.STATE_CONNECTING;
        bluetoothDevice = device;


        new Thread(() -> {
            reader = new Reader("Reader1", "Bluetooth", device.getAddress());
            reader.setChannelType(BaseReader.ReaderChannelType.RFID_CHANNEL_TYPE);//选择通道
            if (reader.connect()) {
                reader.onMessageNotificationReceived.add((baseReader, msg) -> {
                    if (reader == null) {
                        return;
                    } else if (msg instanceof RXD_TagData) {
                        RXD_TagData data = (RXD_TagData) msg;
                        String epc = Util.convertByteArrayToHexString(data.getReceivedMessage().getEPC());
                        rfidCallBack.onReadData(epc, null, Model.RFID);
                    } else if (msg instanceof RXD_ReaderTriggerStatus) {
                        if (rfidState == State.ON) {
                            stopReadCommand();
                        } else {
                            startReadCommand();
                        }
                    } else if ((msg instanceof GBInventoryTag)) {
                        String epc = Util.convertByteArrayToHexString(((GBInventoryTag) msg).getReceivedMessage().getTagData());
                        rfidCallBack.onReadData(epc, null, Model.QR);
                    }
                });
                connecteState = BluetoothAdapter.STATE_CONNECTED;
                rfidCallBack.onConnecteChange(device, State.SUCCESS);
            } else {
                connecteState = BluetoothAdapter.STATE_DISCONNECTED;
                rfidCallBack.onConnecteChange(device, State.ERROR);
                reader = null;
            }
        }).start();
        return true;
    }

    @Override
    public boolean stopConnected() {
        if (reader != null) {
            connecteState = BluetoothAdapter.STATE_DISCONNECTING;
            handler.removeCallbacksAndMessages(null);
            command(Command.RFID_OFF, null);
            handler.post(() -> {
                reader.disConnect();
                connecteState = BluetoothAdapter.STATE_DISCONNECTED;
                rfidState = State.OFF;
                qrState = State.OFF;
                rfidCallBack.onConnecteChange(bluetoothDevice, State.ERROR);
                reader = null;
                bluetoothDevice = null;
            });
            return true;
        }

        return false;
    }

    @Override
    public boolean startReadCommand() {
        return command(Command.RFID_ON, null);
    }

    @Override
    public boolean stopReadCommand() {
        return command(Command.RFID_OFF, null);
    }

    @Override
    public boolean startQRCommand() {
        return false;//command(Command.QR_ON, null);
    }

    @Override
    public boolean stopQRCommand() {
        return false;// command(Command.QR_OFF, null);
    }

    @Override
    public boolean command(int command, Object param) {
        if (reader == null || handler.hasMessages(command)) {
            return false;
        }
        handler.sendEmptyMessage(command);
        return true;
    }

    private boolean exeCmd(IMessage cmd) {
        //可以使用队列，解决发送耗时可能导致的卡顿
        if (reader != null) {
            try {
                reader.send(cmd);
                return true;//由于部分命令不知为何返回false，故在此同一true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public void register(ScannerCallBack callback) {
        rfidCallBack = callback;
    }

    @Override
    public void unregister(ScannerCallBack callback) {

    }


    @Override
    public int connecteState() {
        return connecteState;
    }

    @Override
    public int rfidState() {
        return rfidState;
    }

    @Override
    public int qrState() {
        return qrState;
    }
}
