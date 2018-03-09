package com.cnksi.sjjc.activity.gztz;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.activity.BaseActivity;
import com.cnksi.sjjc.adapter.BHDZJLDeviceAdapter;
import com.cnksi.sjjc.bean.Device;
import com.cnksi.sjjc.databinding.ActivityGzjlXzsbBinding;
import com.cnksi.sjjc.view.GridLayoutItemSpacing;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 保护动作记录设备选择
 *
 * @author kkk
 * @date 2018/3/9
 */

public class BHDZJLXZSBActivity extends BaseActivity {
    ActivityGzjlXzsbBinding mXzsbBinding;
    private BHDZJLDeviceAdapter mAdapter;
    private List<Device> mDevices = new ArrayList<>();
    private List<Device> originDevices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isDefaultTitle = false;
        super.onCreate(savedInstanceState);
        mXzsbBinding = DataBindingUtil.setContentView(_this, R.layout.activity_gzjl_xzsb);
        setSupportActionBar(mXzsbBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        initUI();
        initData();
    }

    private void initUI() {
        getIntentValue();
        mXzsbBinding.toolbar.setTitle("请选择对应设备");
        mAdapter = new BHDZJLDeviceAdapter(mXzsbBinding.rlvContainer, mDevices, R.layout.device_item);
        mXzsbBinding.rlvContainer.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        mXzsbBinding.rlvContainer.addItemDecoration(new GridLayoutItemSpacing(2, 20, false));
        mXzsbBinding.rlvContainer.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((view, data, position) -> {
            Device item = (Device) data;
            DbModel dbModel = new DbModel();
            dbModel.add(Device.DEVICEID, item.deviceid);
            dbModel.add(Device.NAME, item.pmsName);
            dataMap.put(Config.DEVICE_DATA, dbModel);
            setResult(RESULT_OK);
            finish();
        });
    }

    private void initData() {
        String deviceBigIds = getIntent().getStringExtra("bigid");
        mFixedThreadPoolExecutor.execute(() -> {
            String sql = " and  bigid in (" + deviceBigIds + ")";
            try {
                mDevices = CustomApplication.getDbManager().selector(Device.class).where(Device.BDZID, "=", currentBdzId).expr(sql).findAll();
                originDevices.addAll(mDevices);
                runOnUiThread(() -> {
                    mAdapter.setList(mDevices);
                });
            } catch (DbException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setQueryHint("输入设备编号或者名称");
        SearchView.SearchAutoComplete autoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
        autoComplete.setHintTextColor(getResources().getColor(android.R.color.white));
        autoComplete.setTextColor(getResources().getColor(android.R.color.white));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mDevices.clear();
                mDevices.addAll(originDevices);
                if (TextUtils.isEmpty(newText)) {
                    mAdapter.notifyDataSetChanged();
                } else {
                    Iterator<Device> devices = mDevices.iterator();
                    while (devices.hasNext()) {
                        Device device = devices.next();
                        if (!device.name.contains(newText)) {
                            devices.remove();
                        }
                    }
                    mAdapter.setList(mDevices);
                }
                return true;
            }

        });
        searchView.setOnCloseListener(() -> {
            mDevices.clear();
            mDevices.addAll(originDevices);
            mAdapter.setList(mDevices);
            return false;
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            dataMap.clear();
            onBackPressed();
        }
        return true;
    }


}
