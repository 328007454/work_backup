package com.cnksi.sjjc.activity.batteryactivity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.activity.BaseSjjcActivity;
import com.cnksi.sjjc.adapter.BatteryDefectDeviceAdapter;
import com.cnksi.sjjc.bean.BatteryInstrument;
import com.cnksi.sjjc.databinding.ActivityBatteryDeviceDetectBinding;
import com.cnksi.sjjc.service.BatteryInstrumentService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kkk on 2017/12/1.
 */

public class BatteryDeviceDetectActivity extends BaseSjjcActivity {
    private ActivityBatteryDeviceDetectBinding mDetectBinding;
    private BatteryDefectDeviceAdapter mDefectDeviceAdapter;
    private List<BatteryInstrument> datas = new ArrayList<>();
    private int pageStart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changedStatusColor();
        mDetectBinding = DataBindingUtil.setContentView(mActivity, R.layout.activity_battery_device_detect);
        setSupportActionBar(mDetectBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        initView();
        initLoadWidget();
        searchData();
    }

    @Override
    public void initUI() {

    }

    @Override
    public void initData() {

    }

    public void initView() {
        mDefectDeviceAdapter = new BatteryDefectDeviceAdapter(R.layout.item_battery_defdevice, datas);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mDetectBinding.rvBatteryDevice.setLayoutManager(layoutManager);
        mDefectDeviceAdapter.bindToRecyclerView(mDetectBinding.rvBatteryDevice);
        mDefectDeviceAdapter.setOnItemClickListener((helper, v, position) -> {
            Intent intent = new Intent();
            BatteryInstrument data = (BatteryInstrument) helper.getItem(position);
            intent.putExtra(BatteryInstrument.CSYQMC, data.testName);
            intent.putExtra(BatteryInstrument.ID, data.id);
            intent.putExtra(BatteryInstrument.SELECT_NUM, (data.selectNum + 1) + "");
            setResult(RESULT_OK, intent);
            finish();
        });
    }


    private void initLoadWidget() {
        mDetectBinding.springviewLayout.setEnableRefresh(false);
        mDetectBinding.springviewLayout.setOnLoadmoreListener(refreshlayout -> {
            ToastUtils.showMessage("正在加载");
            loadMoreData(((++pageStart) * 50), 50);
            mDetectBinding.springviewLayout.finishLoadmore(2000);
        });
    }

    public void searchData() {
        loadMoreData(0, 50);
    }

    private void loadMoreData(final int pageStart, final int pageNum) {
        ExecutorManager.executeTaskSerially(() -> {
            final List<BatteryInstrument> instruments = BatteryInstrumentService.getInstance().findAllInstrument(pageStart, pageNum);
            runOnUiThread(() -> {
                if (!instruments.isEmpty()) {
                    datas.addAll(instruments);
                    mDefectDeviceAdapter.setNewData(datas);
                } else {
                    ToastUtils.showMessage("没有配置数据");
                }
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setQueryHint("输入设备编号或者名称");
        SearchView.SearchAutoComplete autoComplete = searchView.findViewById(R.id.search_src_text);
        autoComplete.setHintTextColor(getResources().getColor(android.R.color.white));
        autoComplete.setTextColor(getResources().getColor(android.R.color.white));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadData(newText);
                return true;
            }

        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    private void loadData(final String text) {
        mDetectBinding.springviewLayout.setEnableLoadmore(false);
        datas.clear();
        mDefectDeviceAdapter.notifyDataSetChanged();
        ExecutorManager.executeTaskSerially(() -> {
            if (TextUtils.isEmpty(text)) {
                loadMoreData(pageStart = 0, 50);
                mDetectBinding.springviewLayout.setEnableLoadmore(true);
                return;
            }
            final List<BatteryInstrument> instrumentList = BatteryInstrumentService.getInstance().findAllLikeName(text);
            runOnUiThread(() -> {
                if (!instrumentList.isEmpty()) {
                    datas.addAll(instrumentList);
                    mDefectDeviceAdapter.setNewData(datas);
                } else {
                    ToastUtils.showMessage("没有配置数据");
                }
            });
        });
    }

}
