package com.cnksi.bdzinspection.fragment;

import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.BluetoothAdapter.BlueDeviceAdapter;
import com.cnksi.bdzinspection.databinding.BluetoothBinding;
import com.cnksi.bdzinspection.model.bluetooth.BluetoothFileBean;
import com.cnksi.bdzinspection.reciever.BluetoothBroardCastReceiver;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.CLog;
import com.cnksi.core.utils.ToastUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by han on 2016/12/8.
 */

public class BluetoothFragment extends DialogFragment {
    public static final String TAG = "BluetoothFragment";
    //Message：连接失败
    public static final int MESSAGE_CONNECT_ERROR = 0x00000001;
    //Message：接收文件的百分比
    public static final int FILE_RECIVE_PERCENT = MESSAGE_CONNECT_ERROR + 1;
    //Message：读取到一个对象
    public static final int MESSAGE_READ_OBJECT = FILE_RECIVE_PERCENT + 1;
    //串口号
    public static final UUID PRIVATE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothBinding binding;
    //蓝牙适配器
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    //用于通信的Socket
    private BluetoothSocket socket;
    //通信之间的信息流
    public DataInputStream fileInStream;
    public DataOutputStream fileOutStream;
    //线程是否中断
    private boolean isInterrupted = false;
    //进度
    private long progress;
    private ArrayList<BluetoothDevice> bluetoothDevices = new ArrayList<>();
    //蓝牙广播
    private BluetoothBroardCastReceiver broardCastReceiver = new BluetoothBroardCastReceiver();
    //蓝牙设备列表
    private BlueDeviceAdapter deviceAdapter;
    //蓝牙通讯线程
    private BluetoothCommunThread commonThread;
    //蓝牙连接线程
    private BluetoothServerConnThread serverConnThread;
    private BluetoothServerSocket serverSocket;
    private Handler blueHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_CONNECT_ERROR://连接失败
                    ToastUtils.showMessageLong("通讯失败，请重新发送信息");
                    break;
                case MESSAGE_READ_OBJECT:
                    BluetoothFileBean fileBean = (BluetoothFileBean) msg.obj;
                    if (!TextUtils.isEmpty(fileBean.getFilename())) {
                        binding.txtBluttoothState.setText("正在传输文件...");
                        binding.txtFileName.setText("文件名：" + fileBean.getFilename());
                    }
                    break;
                case FILE_RECIVE_PERCENT:
                    if (getDialog() != null) {
                        getDialog().show();
                    }
                    BluetoothFileBean bluetoothFileBean = (BluetoothFileBean) msg.obj;
                    binding.reciveProgress.setProgress(Integer.valueOf(bluetoothFileBean.getUppercent()));
                    Log.d(TAG, "handleMessage: " + Integer.valueOf(bluetoothFileBean.getUppercent()));
                    if (Integer.valueOf(bluetoothFileBean.getUppercent()) == 100) {
                        binding.txtBluttoothState.setText("传输完毕");
                        try {
                            if (null != socket) {
                                socket.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    break;

            }
        }
    };


    @Override
    public void onStart() {
        super.onStart();
        serverConnThread = new BluetoothServerConnThread();
        if (null == bluetoothAdapter) {
            ToastUtils.showMessageLong( "该设备没有蓝牙硬件");
        } else {
            bluetoothAdapter.enable();
            Intent blueIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            blueIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(blueIntent);
            //开始连接线程
           ExecutorManager.executeTask(serverConnThread);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        getActivity().registerReceiver(broardCastReceiver, filter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        binding = DataBindingUtil.inflate(inflater, R.layout.xs_bluetooth_fragment, container, false);
        if (!bluetoothAdapter.getBondedDevices().isEmpty()) {
            binding.txtBluttoothState.setText("蓝牙已连接");
            bluetoothDevices.addAll(bluetoothAdapter.getBondedDevices());
        }

        deviceAdapter = new BlueDeviceAdapter(getActivity(), bluetoothDevices, R.layout.xs_device_bound);
        binding.lvDdevice.setAdapter(deviceAdapter);
        broardCastReceiver.setBluetoothListener(deviceArrayList -> {
            bluetoothDevices.clear();
            bluetoothDevices.addAll(deviceArrayList);
            deviceAdapter.setDeviceList(bluetoothDevices);
            if (!bluetoothDevices.isEmpty()) {
                binding.txtBluttoothState.setText("蓝牙已连接");
            }
        });
        return binding.getRoot();
    }

    /**
     * 蓝牙连接线程
     */
    private class BluetoothServerConnThread implements Runnable {
        public BluetoothServerConnThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("BlueserverSocket", PRIVATE_UUID);
                commonThread = new BluetoothCommunThread();
            } catch (IOException e) {
                e.printStackTrace();
            }
            serverSocket = tmp;
        }

        @Override
        public void run() {
            if (bluetoothAdapter == null) {
                return;
            }
            try {
                while (!isInterrupted) {
                    socket = serverSocket.accept();
                    if (null != socket) {
                       ExecutorManager.executeTask(commonThread);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 通讯线程
     */
    private class BluetoothCommunThread implements Runnable {
        @Override
        public void run() {
            try {
                fileOutStream = new DataOutputStream(socket.getOutputStream());
                fileInStream = new DataInputStream(socket.getInputStream());
                BluetoothFileBean fileBean = new BluetoothFileBean();
                getFileType(fileOutStream, fileInStream, fileBean);
            } catch (IOException e) {
                CLog.e(TAG, "获取信息流失败");
                try {
                    socket.close();
                } catch (IOException e1) {
                    CLog.i(TAG, "关于通讯线程socket失败");
                    e1.printStackTrace();
                }
                blueHandler.obtainMessage(MESSAGE_CONNECT_ERROR).sendToTarget();
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取得到的文件类型
     */
    private void getFileType(DataOutputStream fileOutStream, DataInputStream fileInStream, BluetoothFileBean fileBean) {
        long sumLength;
        byte fileType;
        try {
            sumLength = fileInStream.readLong();//数据总长度
            fileType = fileInStream.readByte();//文件类型
            if (fileType == 1) {//文本类型为普通消息
                byte len = fileInStream.readByte();//消息长度
                byte[] ml = new byte[len];
                int size = 0;
                int receivelen = 0;
                while (receivelen < len) {
                    size = fileInStream.read(ml, 0, ml.length);
                    receivelen += size;
                }
                String msg = new String(ml, "GBK");
                Log.v("调试", "msg:" + msg);
                fileBean.setMsg(msg);
            } else if (fileType == 2) {//文本类型为文件
                readFile(fileInStream, fileOutStream, fileBean, sumLength);
            }
        } catch (IOException e) {
            e.printStackTrace();
            blueHandler.obtainMessage(MESSAGE_CONNECT_ERROR).sendToTarget();
        }
    }

    private void readFile(DataInputStream fileInStream, DataOutputStream fileOutStream, BluetoothFileBean fileBean, long sumLength) {
        try {
            byte length = fileInStream.readByte();// 文件名长度
            byte[] file = new byte[length];
            fileInStream.read(file); //读取文件名
            String fileName = new String(file);
            fileBean.setFilename(fileName);
            if (!TextUtils.isEmpty(fileName)) {
                blueHandler.obtainMessage(MESSAGE_READ_OBJECT, fileBean).sendToTarget();
            }
            long fileDataLength = sumLength - 6 - file.length;
            String savePath = Environment.getExternalStorageDirectory().getPath() + "/" + fileBean.getFilename();
            fileBean.setFilepath(savePath);
            FileOutputStream fileOutputStream = new FileOutputStream(savePath, false);
            byte[] buffer = new byte[1024];
            int size = -1;
            int receiveLength = 0;
            int i = 0;
            while (receiveLength < fileDataLength) {
                size = fileInStream.read(buffer);
                fileOutputStream.write(buffer, 0, size);
                receiveLength += size;
                i++;
                progress = receiveLength * 100 / fileDataLength;
                Log.i(TAG, progress + "");
                BluetoothFileBean bluetoothFileBean = new BluetoothFileBean();
                bluetoothFileBean.setUppercent(String.valueOf(progress));
                if (i == 1) {
                    bluetoothFileBean.setShowflag(true);
                } else {
                    bluetoothFileBean.setShowflag(false);
                }
                blueHandler.obtainMessage(FILE_RECIVE_PERCENT, bluetoothFileBean).sendToTarget();
            }
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            blueHandler.obtainMessage(MESSAGE_CONNECT_ERROR).sendToTarget();
        }
    }


    @Override
    public void onDestroy() {
        cancel();
        getActivity().unregisterReceiver(broardCastReceiver);
        super.onDestroy();
    }

    private void cancel() {
        isInterrupted = true;
        commonThread = null;
        serverConnThread = null;
        try {
            if (null != socket) {
                socket.close();
            }
            if (null != serverSocket) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
