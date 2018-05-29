package com.cnksi.bdzinspection.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.BluetoothAdapter.RFIDReadDataAdapter;
import com.cnksi.bdzinspection.databinding.XsActivityBtdemoBinding;
import com.cnksi.common.base.BaseActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import android_serialport_api.M100_RFID_API;

/**
 * Created by han on 2017/1/5.
 */

public class BTDemoActivity extends BaseActivity {

    private final static int REQUEST_CONNECT_DEVICE = 1; // 宏定义查询设备句柄
    final byte MSG_M100CMDRESP = 0X08;
    final byte MSG_M100DATARESP_INV = 0X0B;
    private final static String MY_UUID = "00001101-0000-1000-8000-00805F9B34FB"; // SPP服务UUID号
    private XsActivityBtdemoBinding binding;
    private BluetoothAdapter _bluetooth = BluetoothAdapter.getDefaultAdapter(); // 获取本地蓝牙适配器，即蓝牙设备
    ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

    public M100_RFID_API rfid = new M100_RFID_API();
    BluetoothDevice _device = null; // 蓝牙设备
    BluetoothSocket _socket = null; // 蓝牙通信socket
    boolean _discoveryFinished = false;
    boolean bRun = true;
    boolean bThread = false;
    boolean bCycles = false;
    boolean goon = false;
    int taglenth = 0;
    boolean isLen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(mActivity, R.layout.xs_activity_btdemo);
        HashMap<String, String> map1 = new HashMap<String, String>();
        map1.put("TagData", "                      "
                + getResources().getString(R.string.xs_Tagdata));
        map1.put("CountNum", getResources().getString(R.string.xs_Tagcount));
        list.add(map1);
        RFIDReadDataAdapter listAdapter = new RFIDReadDataAdapter(mActivity, list, R.layout.xs_rfid_user,
                new String[]{"TagData", "CountNum"}, new int[]{
                R.id.TagData, R.id.CountNum});
        binding.TagList.setAdapter(listAdapter);
        if (_bluetooth == null) {
            Toast.makeText(this, R.string.xs_info_nobtdevice, Toast.LENGTH_LONG)
                    .show();
            finish();
            return;
        }
        new Thread() {
            @Override
            public void run() {
                if (_bluetooth.isEnabled() == false) {
                    _bluetooth.enable();
                }
            }
        }.start();
    }

    // 连接按键响应函数
    public void onConnectButtonClicked(View v) throws IOException {
        if (_bluetooth.isEnabled() == false) { // 如果蓝牙服务不可用则提示
            Toast toast = Toast.makeText(this, R.string.xs_info_nobt,
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

        // 如未连接设备则打开DeviceListActivity进行设备搜索
        if (_socket == null) {
            Intent serverIntent = new Intent(this, BlueDeviceActivity.class); // 跳转程序设置
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE); // 设置返回宏定义
        } else {
            rfid.M100_CloseBluetoothSocket();// DisConnected
            _socket.close();
            _socket = null;
            binding.Button03.setText(R.string.xs_BT_Conn);
//            btsta.setText(R.string.BT_sta_Conn);
            // btn.setImageResource(R.drawable.icon_r);
        }
        return;
    }

    public void onInfCycButtonClicked(View v) throws IOException { // 盘点

        if (_socket == null) {
            Toast toast = Toast.makeText(this, R.string.xs_info_btconn,
                    Toast.LENGTH_LONG);// .show();
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }
        if ("".equals(binding.Timeinterval.getText().toString())) {
            System.out.println("Can NOT Get Timeinterval");
            return;
        }
        if (Integer.parseInt(binding.Timeinterval.getText().toString()) <= 0) {
            Toast toast = Toast.makeText(this, R.string.xs_errortimeinter,
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }
        rfid.M100SetMessageHandler(mHandler);
        rfid.M100StartInvTag(Integer.parseInt(binding.Timeinterval.getText().toString()));

    }

    //停止读盘
    public void onstopInfCycButtonClicked(View v) throws IOException {
        if (_socket == null) {
            Toast toast = Toast.makeText(this, R.string.xs_info_btconn,
                    Toast.LENGTH_LONG);// .show();
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }
        rfid.M100SetMessageHandler(mHandler);
        rfid.M100StopInvTag();
    }

    @Override
    protected void onRefresh(Message msg) {
        super.onRefresh(msg);
        switch (msg.what) {
            case MSG_M100DATARESP_INV:
                Bundle x6b4 = msg.getData();
                String x6EPCstr4 = x6b4.getString("InvData");
                x6EPCstr4 = x6EPCstr4.replaceAll(" ","");
                byte[] x6invdatabyte = x6b4.getByteArray("InvDatabyte");
                String x6invtt = "";
                for (int i = 0; i < x6invdatabyte.length; i++) {
                    x6invtt += String.format("%1$02x", x6invdatabyte[i]);
                }
                System.out.println("ByteArray INV:" + x6invtt);
                System.out.println("handle INV: " + x6EPCstr4);

                ModifyListView(x6EPCstr4);
                break;
            case MSG_M100CMDRESP:
                Bundle x6ver = msg.getData();
                String x6HWver = x6ver.getString("HWver");
                String x6SWver = x6ver.getString("SWver");

                System.out.println("x6HWver: " + x6HWver);
                System.out.println("x6SWver: " + x6SWver);
                ShowDevVersion(x6HWver, x6SWver);

                break;
            default:
                break;
        }

    }

    public void ShowDevVersion(String HWver, String SWver) {
        Toast toast = Toast.makeText(this, "HardWareVer = " + HWver + "\n\n\n"
                + "SoftWareVer = " + SWver, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    RFIDReadDataAdapter listAdapter1;
    Intent intent2 = new Intent();
    public void ModifyListView(String tagdata) {
        HashMap<String, String> maptemp = new HashMap<String, String>();
        HashMap<String, String> mapnew = new HashMap<String, String>();
        boolean inTheList = false;

        Iterator<HashMap<String, String>> it = list.iterator(); // 迭代器
        // 遍历ArrayList
        while (it.hasNext()) {
            maptemp = it.next();
            if (tagdata.equals(maptemp.get("TagData"))) // 存在于ArrayList中
            {
                maptemp.put("TagData", tagdata);
                maptemp.put("CountNum",
                        "" + (Integer.parseInt(maptemp.get("CountNum")) + 1));
                list.set(list.indexOf(maptemp), maptemp);
                inTheList = true;
            }

        }
        if (inTheList == false) {
            mapnew.put("TagData", tagdata);
            mapnew.put("CountNum", "1");
            list.add(mapnew);
            int n = list.size() - 1;
//            tagall.setText("" + n);
        }

        if (listAdapter1 != null) {
            listAdapter1.notifyDataSetChanged();
        } else {
            listAdapter1 = new RFIDReadDataAdapter(this, list, R.layout.xs_rfid_user,
                    new String[]{"TagData", "CountNum"}, new int[]{
                    R.id.TagData, R.id.CountNum});
            binding.TagList.setAdapter(listAdapter1);
            binding.TagList.setOnItemClickListener((adapterView, view, i, l) -> {
                TextView textView = view.findViewById(R.id.TagData);
                String data = textView.getText().toString();
                Log.i("TTTTT",data);
                intent2.putExtra("spacingId",data);
                setResult(Activity.RESULT_OK,intent2);
                BTDemoActivity.this.finish();
            });
        }

    }

    // 接收活动结果，响应startActivityForResult()
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE: // 连接结果，由DeviceListActivity设置返回
                // 响应返回结果
                if (resultCode == Activity.RESULT_OK) { // 连接成功，由DeviceListActivity设置返回
                    // MAC地址，由DeviceListActivity设置返回
                    String address = data.getExtras().getString(
                            BlueDeviceActivity.EXTRA_DEVICE_ADDRESS);
                    // 得到蓝牙设备句柄
                    _device = _bluetooth.getRemoteDevice(address);
                    // 用服务号得到socket
                    try {
                        _socket = _device.createRfcommSocketToServiceRecord(UUID
                                .fromString(MY_UUID));
                    } catch (IOException e) {
                        Toast toast = Toast.makeText(
                                this,
                                getResources().getString(R.string.xs_act_btconn)
                                        + getResources().getString(
                                        R.string.xs_act_btconnfail),
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                    // 连接socket
                    try {
                        _socket.connect();
                        Toast toast = Toast.makeText(
                                this,
                                getResources().getString(R.string.xs_act_btconn)
                                        + _device.getName()
                                        + getResources().getString(
                                        R.string.xs_act_btconngood),
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        binding.Button03.setText(R.string.xs_BT_dConn);
//                        btsta.setText(R.string.BT_sta_Conn);

                    } catch (IOException e) {
                        try {
                            _socket.close();
                            _socket = null;
                            Toast toast = Toast.makeText(
                                    this,
                                    getResources().getString(R.string.xs_act_btconn)
                                            + getResources().getString(
                                            R.string.xs_act_btconnfail),
                                    Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                        } catch (IOException ee) {
                            Toast toast = Toast.makeText(
                                    this,
                                    getResources().getString(R.string.xs_act_btconn)
                                            + getResources().getString(
                                            R.string.xs_act_btconnfail),
                                    Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                        return;
                    }
                    // 打开接收线程
                    try {
                        rfid.M100_SetBluetoothSocket(_socket, 1);// 设置蓝牙Socket 1
                        rfid.M100SetMessageHandler(mHandler);
                    } catch (IOException e) {
                        Toast.makeText(this,
                                getResources().getString(R.string.xs_info_revfail),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                break;
            default:
                break;
        }
    }
}
