package com.cnksi.login.activity;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.api.scanner.SM;
import com.cnksi.api.scanner.SMCallBack;
import com.cnksi.api.scanner.State;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.login.R;
import com.cnksi.login.activity.xian.XHomeActivity;
import com.iflytek.thirdparty.S;

import java.util.ArrayList;
import java.util.List;

/**
 * 扫描仪连接/断开和设置页面
 * @author Today(张军)
 * @version v1.0
 * @date 2018/07/12 11:18
 */
public class ScannerSetting extends AppCompatActivity {
    private View lastScannerGroup;
    private TextView nameBtn, addressBtn, settingBtn;
    private RecyclerView recycler;
    private Button discoverBtn;
    private ProgressDialog progressDialog;
    private SMCallBack smCallBack = new SMCallBack() {

        @Override
        public void onConnecteChange(BluetoothDevice device, int state) {
            runOnUiThread(() -> {
                if (state == State.SUCCESS) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        setLastScanner(device);
                        Toast.makeText(ScannerSetting.this, device.getName() + "连接成功", Toast.LENGTH_SHORT).show();
                        PreferencesUtils.put("lastScannerAddress", device.getAddress());
                    }
                } else {
                    lastScannerGroup.setVisibility(View.GONE);
                    {
                        Toast.makeText(ScannerSetting.this, (device != null ? device.getName() : "") + "连接失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public void onCommand(int command, int state) {

        }

        @Override
        public void onReadData(String tagId, Object tag, int model) {

        }

        @Override
        public void onScan(BluetoothDevice device, int state) {
            if (device != null) {
                runOnUiThread(() -> {
                    devices.add(device);
                    adapter.notifyDataSetChanged();
                });
            }
        }
    };
    private String lastScannerAddress;
    private ScannerAdapter adapter;
    private List<BluetoothDevice> devices = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_scanner_setting);
        lastScannerGroup = findViewById(R.id.lastScannerGroup);
        nameBtn = findViewById(R.id.nameBtn);
        addressBtn = findViewById(R.id.addressBtn);
        settingBtn = findViewById(R.id.settingBtn);
        recycler = findViewById(R.id.recycler);
        discoverBtn = findViewById(R.id.discoverBtn);
        settingBtn.setOnClickListener(v -> {
            SM.instance().stopConnected();
            PreferencesUtils.put("lastScannerAddress", "");
        });
        recycler.setLayoutManager(new LinearLayoutManager(this));
        discoverBtn.setOnClickListener(v -> {
            devices.clear();
            devices.addAll(SM.instance().getBondedDevices());
//            adapter = new ScannerAdapter(R.layout.textview_item, devices);
//            recycler.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            SM.instance().startScan();
        });

        SM.instance().register(smCallBack);
        lastScannerAddress = PreferencesUtils.get("lastScannerAddress", "");
        if (!TextUtils.isEmpty(lastScannerAddress) && SM.instance().isConnected()) {
            setLastScanner(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(lastScannerAddress));
        } else {
            lastScannerGroup.setVisibility(View.GONE);
        }

        adapter = new ScannerAdapter(R.layout.textview_item, devices);
        recycler.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            progressDialog = new ProgressDialog(ScannerSetting.this);
            progressDialog.setMessage("正在连接" + devices.get(position).getName() + "...");
            progressDialog.setCancelable(false);
            if (SM.instance().startConnecte(devices.get(position))) {
                progressDialog.show();
            }
        });
        discoverBtn.performClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SM.instance().unregister(smCallBack);
    }

    private void setLastScanner(BluetoothDevice device) {
        lastScannerGroup.setVisibility(View.VISIBLE);
        nameBtn.setText(device.getName());
        addressBtn.setText(device.getAddress());
    }

    public class ScannerAdapter extends BaseQuickAdapter<BluetoothDevice, BaseViewHolder> {
        public ScannerAdapter(int layoutResId, @Nullable List<BluetoothDevice> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, BluetoothDevice item) {
            String name = item.getName() + ":" + item.getAddress();
            helper.setText(R.id.txt_name, name);
        }
    }
}
