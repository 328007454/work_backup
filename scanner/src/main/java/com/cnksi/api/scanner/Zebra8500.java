package com.cnksi.api.scanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.zebra.rfid.api3.Events;
import com.zebra.rfid.api3.HANDHELD_TRIGGER_EVENT_TYPE;
import com.zebra.rfid.api3.InvalidUsageException;
import com.zebra.rfid.api3.OperationFailureException;
import com.zebra.rfid.api3.ReaderDevice;
import com.zebra.rfid.api3.Readers;
import com.zebra.rfid.api3.RfidEventsListener;
import com.zebra.rfid.api3.RfidReadEvents;
import com.zebra.rfid.api3.RfidStatusEvents;
import com.zebra.rfid.api3.STATUS_EVENT_TYPE;
import com.zebra.rfid.api3.TagData;
import com.zebra.scannercontrol.DCSSDKDefs;
import com.zebra.scannercontrol.DCSScannerInfo;
import com.zebra.scannercontrol.FirmwareUpdateEvent;
import com.zebra.scannercontrol.IDcsSdkApiDelegate;
import com.zebra.scannercontrol.SDKHandler;

import java.util.ArrayList;
import java.util.List;

import invengo.javaapi.core.IMessage;

/**
 * 斑马8500实现类（暂不实现）
 * @author Today(张军)
 * @version v1.0
 * @date 2018/07/02 15:41
 */
public class Zebra8500 implements Scanner {
    //所有状态在回调前改编
    int connecteState = BluetoothAdapter.STATE_DISCONNECTED;
    int rfidState = State.OFF;
    int qrState = State.OFF;

    private String tag = "Zebra8500";
    private Readers readers = new Readers();
    private List<ReaderDevice> devices = readers.GetAvailableRFIDReaderList();
    private BluetoothDevice device;
    private ScannerCallBack rfidCallBack;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private RfidEventsListener rfidEventsListener = new RfidEventsListener() {
        @Override
        public void eventReadNotify(RfidReadEvents rfidReadEvents) {
            System.out.println("eventReadNotify(RfidReadEvents " + rfidReadEvents + ")");
            final TagData[] myTags = readerDevice.getRFIDReader().Actions.getReadTags(100);
            if (myTags != null && myTags.length > 0) {
                for (TagData tag : myTags) {
                    rfidCallBack.onReadData(tag.getTagID(), null, Model.RFID);
                }
            }
        }

        @Override
        public void eventStatusNotify(RfidStatusEvents rfidStatusEvents) {
            System.out.println("eventStatusNotify(RfidStatusEvents " + rfidStatusEvents.StatusEventData.getStatusEventType() + ")");
            STATUS_EVENT_TYPE eventType = rfidStatusEvents.StatusEventData.getStatusEventType();
            if (eventType == STATUS_EVENT_TYPE.HANDHELD_TRIGGER_EVENT) {//扫描按键
                Events.HandheldTriggerEventData triggerEventData = rfidStatusEvents.StatusEventData.HandheldTriggerEventData;
                if (triggerEventData.getHandheldEvent() == HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_PRESSED) {
                    //按下
                    startReadCommand();
                } else {
                    //弹起
                    stopReadCommand();
                }
            } else {
                System.out.println("其他事件");
            }
        }
    };

    private SDKHandler sdkHandler;
//    @Override
//    public void startScan(Context context) {
//        rfidCallBack.onScan(null, State.SUCCESS);
//        for (ReaderDevice d : devices) {
//            rfidCallBack.onScan(bluetoothAdapter.getRemoteDevice(d.getAddress()), State.SUCCESS);
//        }
//    }
//
//    @Override
//    public void stopScan() {
//        rfidCallBack.onScan(null, State.ERROR);
//    }

    private ReaderDevice readerDevice;
    private Context context;
    private Handler handler;

    public Zebra8500(Context c) {
        this.context = c;
        HandlerThread handlerThread = new HandlerThread("Zebra8500_SendThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                boolean result = false;
                try {
                    switch (msg.what) {
                        case Command.RFID_ON:
                            readerDevice.getRFIDReader().Actions.Inventory.perform();
                            rfidState = State.ON;
                            result = true;
                            break;
                        case Command.RFID_OFF:
                            readerDevice.getRFIDReader().Actions.Inventory.stop();
                            rfidState = State.OFF;
                            result = true;
                            break;
                        case Command.QR_ON:
                            String in_xml_on = "<inArgs><scannerID>" + scannerInfo.getScannerID() + "</scannerID></inArgs>";
                            result = exeQRCmd(DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_PULL_TRIGGER, in_xml_on, null, scannerInfo.getScannerID());
                            if (result) {
                                qrState = State.ON;
                            }
                            break;
                        case Command.QR_OFF:
                            String in_xml_off = "<inArgs><scannerID>" + scannerInfo.getScannerID() + "</scannerID></inArgs>";
                            result = exeQRCmd(DCSSDKDefs.DCSSDK_COMMAND_OPCODE.DCSSDK_DEVICE_RELEASE_TRIGGER, in_xml_off, null, scannerInfo.getScannerID());
                            if (result) {
                                qrState = State.OFF;
                            }
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

        sdkHandler = new SDKHandler(context);
        sdkHandler.dcssdkSetDelegate(new IDcsSdkApiDelegate() {
            @Override
            public void dcssdkEventScannerAppeared(DCSScannerInfo dcsScannerInfo) {
                Log.e(tag, "dcssdkEventScannerAppeared");
            }

            @Override
            public void dcssdkEventScannerDisappeared(int i) {
                Log.e(tag, "dcssdkEventScannerDisappeared");
            }

            @Override
            public void dcssdkEventCommunicationSessionEstablished(DCSScannerInfo dcsScannerInfo) {
                Log.e(tag, "dcssdkEventCommunicationSessionEstablished");
            }

            @Override
            public void dcssdkEventCommunicationSessionTerminated(int i) {
                Log.e(tag, "dcssdkEventCommunicationSessionTerminated");
            }

            @Override
            public void dcssdkEventBarcode(byte[] bytes, int i, int i1) {
                Log.e(tag, "dcssdkEventBarcode(byte[] " + new String(bytes) + ", int " + i + ", int " + i1 + ")");
                qrState = State.OFF;
                rfidCallBack.onCommand(Command.QR_OFF, State.SUCCESS);
                rfidCallBack.onReadData(new String(bytes), null, Model.QR);

            }

            @Override
            public void dcssdkEventImage(byte[] bytes, int i) {
                Log.e(tag, "dcssdkEventImage");
            }

            @Override
            public void dcssdkEventVideo(byte[] bytes, int i) {
                Log.e(tag, "dcssdkEventVideo");
            }

            @Override
            public void dcssdkEventFirmwareUpdate(FirmwareUpdateEvent firmwareUpdateEvent) {
                Log.e(tag, "dcssdkEventFirmwareUpdate");
            }

            @Override
            public void dcssdkEventAuxScannerAppeared(DCSScannerInfo dcsScannerInfo, DCSScannerInfo dcsScannerInfo1) {
                Log.e(tag, "dcssdkEventAuxScannerAppeared");
            }
        });
        sdkHandler.dcssdkEnableAvailableScannersDetection(true);
        sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_BT_NORMAL);
        sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_SNAPI);
        sdkHandler.dcssdkSetOperationalMode(DCSSDKDefs.DCSSDK_MODE.DCSSDK_OPMODE_BT_LE);
        initializeDcsSdkWithAppSettings();
    }

    @Override
    public boolean bond(BluetoothDevice device) {
        if (device.getName() != null && device.getName().contains(FindDevice.DeviceFilter.FILTER_ZEBRA_8500)) {
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                startConnecte(device);
            } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                rfidCallBack.onConnecteChange(device, State.ERROR);
            }
        }

        return false;
    }

    @Override
    public boolean startConnecte(BluetoothDevice device) {
        handler.removeCallbacksAndMessages(null);
        if (startConnecteRfid(device)) {
            if (startConnecteQR(device)) {
                return true;
            } else {
                stopConnectedRfid();
            }
        }

        return true;
    }

    @Override
    public boolean stopConnected() {
        handler.removeCallbacksAndMessages(null);
        int k = 0;
        if (stopConnectedRfid()) {
            k++;
        }
        if (stopConnectedQR()) {
            k++;
        }

        return k == 2;
    }


    private boolean startConnecteRfid(BluetoothDevice d) {
        if (d.getBondState() != BluetoothDevice.BOND_BONDED) {
            return d.createBond();
        }

        ReaderDevice rd = null;
        devices = readers.GetAvailableRFIDReaderList();
        for (ReaderDevice rdt : devices) {
            if (rdt.getAddress().equals(d.getAddress())) {
                rd = rdt;
                break;
            }
        }

        if (rd == null) {
            rfidCallBack.onConnecteChange(device, State.ERROR);
            return false;
        }
        this.device = d;
        this.readerDevice = rd;
        connecteState = BluetoothAdapter.STATE_CONNECTING;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    readerDevice.getRFIDReader().connect();
                    if (readerDevice.getRFIDReader().isConnected()) {
                        readerDevice.getRFIDReader().Events.addEventsListener(rfidEventsListener);
                        readerDevice.getRFIDReader().Events.setBatchModeEvent(true);
                        readerDevice.getRFIDReader().Events.setReaderDisconnectEvent(true);
                        readerDevice.getRFIDReader().Events.setInventoryStartEvent(true);
                        readerDevice.getRFIDReader().Events.setInventoryStopEvent(true);
                        readerDevice.getRFIDReader().Events.setTagReadEvent(true);
                        readerDevice.getRFIDReader().Events.setHandheldEvent(true);
                        readerDevice.getRFIDReader().Events.setBatteryEvent(true);
                        readerDevice.getRFIDReader().Events.setPowerEvent(true);
                        readerDevice.getRFIDReader().Events.setOperationEndSummaryEvent(true);
                        connecteState = BluetoothAdapter.STATE_CONNECTED;
                        rfidCallBack.onConnecteChange(device, State.SUCCESS);
                        stopReadCommand();
                    }
                } catch (InvalidUsageException | OperationFailureException e) {
                    e.printStackTrace();
                    stopConnectedRfid();
                }
            }
        }).start();
        return true;
    }

    private boolean stopConnectedRfid() {
        if (readerDevice != null) {
            connecteState = BluetoothAdapter.STATE_DISCONNECTING;
            stopReadCommand();
            handler.post(() -> {
                try {
                    if (readerDevice.getRFIDReader() != null) {
//                        if (readerDevice.getRFIDReader().Events != null) {
//                            readerDevice.getRFIDReader().Events.removeEventsListener(rfidEventsListener);
//                        }
                        readerDevice.getRFIDReader().disconnect();
                    }
                } catch (InvalidUsageException | OperationFailureException e) {
                    e.printStackTrace();
                }

                connecteState = BluetoothAdapter.STATE_DISCONNECTED;
                rfidCallBack.onConnecteChange(device, State.ERROR);
                readerDevice = null;
                device = null;
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
        return command(Command.QR_ON, null);
    }

    @Override
    public boolean stopQRCommand() {
        return command(Command.QR_OFF, null);
    }

    @Override
    public boolean command(int command, Object param) {
        if (handler.hasMessages(command)) {
            return false;
        } else {
            return handler.sendEmptyMessage(command);
        }
    }

    private boolean exeQRCmd(DCSSDKDefs.DCSSDK_COMMAND_OPCODE opCode, String inXML, StringBuilder outXML, int scannerID) {
        if (sdkHandler != null) {
            if (outXML == null) {
                outXML = new StringBuilder();
            }
            DCSSDKDefs.DCSSDK_RESULT result = sdkHandler.dcssdkExecuteCommandOpCodeInXMLForScanner(opCode, inXML, outXML, scannerID);
            if (result == DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_SUCCESS)
                return true;
            else if (result == DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_FAILURE)
                return false;
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

    //QR
//    AvailableScanner availableScanner;
    private ArrayList<DCSScannerInfo> scannerTreeList = new ArrayList<>();
    private DCSScannerInfo scannerInfo;

    public boolean startConnecteQR(BluetoothDevice device) {
        scannerTreeList.clear();
        sdkHandler.dcssdkGetAvailableScannersList(scannerTreeList);
        sdkHandler.dcssdkGetActiveScannersList(scannerTreeList);

        DCSScannerInfo di = null;
        for (DCSScannerInfo si : scannerTreeList) {
            if (si.getScannerHWSerialNumber().equals(device.getAddress())) {
                di = si;
                break;
            }
        }
        if (di == null) {
            return false;
        }
        scannerInfo = di;
        connecteState = BluetoothAdapter.STATE_CONNECTING;
        new Thread(() -> {
            DCSSDKDefs.DCSSDK_RESULT result = sdkHandler.dcssdkEstablishCommunicationSession(scannerInfo.getScannerID());
            if (DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_SUCCESS == result) {
                connecteState = BluetoothAdapter.STATE_CONNECTED;
                rfidCallBack.onConnecteChange(device, State.SUCCESS);
                stopQRCommand();
            } else {
                stopConnectedQR();
                rfidCallBack.onConnecteChange(device, State.ERROR);
            }
        }).start();
        return true;
    }

    public boolean stopConnectedQR() {
        if (sdkHandler != null && scannerInfo != null) {
            connecteState = BluetoothAdapter.STATE_DISCONNECTING;

            stopQRCommand();
            handler.post(() -> {
                DCSSDKDefs.DCSSDK_RESULT result = sdkHandler.dcssdkTerminateCommunicationSession(scannerInfo.getScannerID());
                if (DCSSDKDefs.DCSSDK_RESULT.DCSSDK_RESULT_SUCCESS == result) {

                }
                connecteState = BluetoothAdapter.STATE_DISCONNECTED;
                rfidCallBack.onConnecteChange(device, State.ERROR);
                scannerInfo = null;
            });

            return true;
        }

        return false;
    }

    public void initializeDcsSdkWithAppSettings() {

        boolean MOT_SETTING_SCANNER_DETECTION = true;// settings.getBoolean(Constants.PREF_SCANNER_DETECTION, );
        boolean MOT_SETTING_EVENT_IMAGE = true;//settings.getBoolean(Constants.PREF_EVENT_IMAGE, true);
        boolean MOT_SETTING_EVENT_VIDEO = false;//settings.getBoolean(Constants.PREF_EVENT_VIDEO, false);

        boolean MOT_SETTING_EVENT_ACTIVE = true;//settings.getBoolean(Constants.PREF_EVENT_ACTIVE, true);
        boolean MOT_SETTING_EVENT_AVAILABLE = true;//settings.getBoolean(Constants.PREF_EVENT_AVAILABLE, true);
        boolean MOT_SETTING_EVENT_BARCODE = true;//settings.getBoolean(Constants.PREF_EVENT_BARCODE, true);

        boolean MOT_SETTING_NOTIFICATION_AVAILABLE = false;// settings.getBoolean(Constants.PREF_NOTIFY_AVAILABLE, false);
        boolean MOT_SETTING_NOTIFICATION_ACTIVE = false;//settings.getBoolean(Constants.PREF_NOTIFY_ACTIVE, false);
        boolean MOT_SETTING_NOTIFICATION_BARCODE = false;//settings.getBoolean(Constants.PREF_NOTIFY_BARCODE, false);

        boolean MOT_SETTING_NOTIFICATION_IMAGE = false;//settings.getBoolean(Constants.PREF_NOTIFY_IMAGE, false);
        boolean MOT_SETTING_NOTIFICATION_VIDEO = false;//settings.getBoolean(Constants.PREF_NOTIFY_VIDEO, false);

        int notifications_mask = 0;
        if (MOT_SETTING_EVENT_AVAILABLE) {
            notifications_mask |= (DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SCANNER_APPEARANCE.value | DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SCANNER_DISAPPEARANCE.value);
        }
        if (MOT_SETTING_EVENT_ACTIVE) {
            notifications_mask |= (DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SESSION_ESTABLISHMENT.value | DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_SESSION_TERMINATION.value);
        }
        if (MOT_SETTING_EVENT_BARCODE) {
            notifications_mask |= (DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_BARCODE.value);
        }
        if (MOT_SETTING_EVENT_IMAGE) {
            notifications_mask |= (DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_IMAGE.value);
        }
        if (MOT_SETTING_EVENT_VIDEO) {
            notifications_mask |= (DCSSDKDefs.DCSSDK_EVENT.DCSSDK_EVENT_VIDEO.value);
        }
        sdkHandler.dcssdkSubsribeForEvents(notifications_mask);
    }

}
