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
import android.view.View;

import com.cnksi.core.utils.CToast;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.activity.BaseActivity;
import com.cnksi.sjjc.adapter.BaseRecyclerDataBindingAdapter;
import com.cnksi.sjjc.adapter.BatteryDefetcDeviceAdapter;
import com.cnksi.sjjc.bean.BatteryInstrument;
import com.cnksi.sjjc.databinding.ActivityBatteryDeviceDetectBinding;
import com.cnksi.sjjc.service.BatteryInstrumentService;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kkk on 2017/12/1.
 */

public class BatteryDeviceDetectActivity extends BaseActivity {
    private ActivityBatteryDeviceDetectBinding mDetectBinding;
    private BatteryDefetcDeviceAdapter mDefetcDeviceAdapter;
    private List<BatteryInstrument> datas = new ArrayList<>();
    private int pageStart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changedStatusColor();
        mDetectBinding = DataBindingUtil.setContentView(_this, R.layout.activity_battery_device_detect);
        setSupportActionBar(mDetectBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        initUI();
        initLoadWidget();
        initData();
    }

    private void initUI() {
        mDefetcDeviceAdapter = new BatteryDefetcDeviceAdapter(mDetectBinding.rvBatteryDevice, datas, R.layout.item_battery_defdevice);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mDetectBinding.rvBatteryDevice.setLayoutManager(layoutManager);
        mDetectBinding.rvBatteryDevice.setAdapter(mDefetcDeviceAdapter);
        mDefetcDeviceAdapter.setOnItemClickListener(new BaseRecyclerDataBindingAdapter.OnItemClickListener() {
            @Override
            public void onAdapterItemClick(View view, Object data, int position) {
                Intent intent = new Intent();
                intent.putExtra(BatteryInstrument.CSYQMC, ((BatteryInstrument) data).testName);
                intent.putExtra(BatteryInstrument.ID, ((BatteryInstrument) data).id);
                intent.putExtra(BatteryInstrument.SELECT_NUM, (((BatteryInstrument) data).selectNum + 1) + "");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }


    private void initLoadWidget() {
        mDetectBinding.springviewLayout.setEnableRefresh(false);
        mDetectBinding.springviewLayout.setOnLoadmoreListener(new OnLoadmoreListener() {

            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                CToast.showShort(_this, "正在加载");
                loadMoreData(((++pageStart) * 50), 50);
                mDetectBinding.springviewLayout.finishLoadmore(2000);
            }
        });
    }

    private void initData() {
        loadMoreData(0, 50);
    }

    private void loadMoreData(final int pageStart, final int pageNum) {
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final List<BatteryInstrument> instruments = BatteryInstrumentService.getInstance().findAllInstrument(pageStart, pageNum);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!instruments.isEmpty()) {
                            datas.addAll(instruments);
                            mDefetcDeviceAdapter.setList(datas);
                        } else {
                            CToast.showShort(_this, "没有配置数据");
                        }
                    }
                });
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
                loadData(newText);
                return true;
            }

        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return true;
    }

    private void loadData(final String text) {
        mDetectBinding.springviewLayout.setEnableLoadmore(false);
        datas.clear();
        mDefetcDeviceAdapter.notifyDataSetChanged();
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(text)) {
                    loadMoreData(pageStart = 0, 50);
                    mDetectBinding.springviewLayout.setEnableLoadmore(true);
                    return;
                }
                final List<BatteryInstrument> instrumentList = BatteryInstrumentService.getInstance().findAllLikeName(text);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!instrumentList.isEmpty()) {
                            datas.addAll(instrumentList);
                            mDefetcDeviceAdapter.setList(datas);
                        } else {
                            CToast.showShort(_this, "没有配置数据");
                        }
                    }
                });
            }
        });
    }

}