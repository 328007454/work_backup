package com.cnksi.sjjc.activity;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.cnksi.common.Config;
import com.cnksi.common.daoservice.DeviceService;
import com.cnksi.common.daoservice.SpacingService;
import com.cnksi.common.enmu.PMSDeviceType;
import com.cnksi.common.model.Device;
import com.cnksi.common.model.Spacing;
import com.cnksi.common.utils.StringUtils;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.DeviceExpandabelListAdapter;
import com.cnksi.sjjc.databinding.ActivityDevicesExpadableListBinding;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

;

/**
 * @author nothing 2017/12/11
 */
public class AllDeviceListActivity extends BaseActivity implements DeviceExpandabelListAdapter.OnAdapterViewClickListener {

    public static final String FUNCTION_MODEL = "fuction";
    public static final String BDZID = "bdzid";
    public static final String BIGID = "bigid";
    public static final String SPCAEID = "spcaeid";
    private LinkedList<Spacing> groupList = new LinkedList<>();
    private HashMap<Spacing, ArrayList<DbModel>> groupHashMap = new HashMap<>();
    private DeviceExpandabelListAdapter mDeviceExpandableAdapater = null;
    private PMSDeviceType currentFunctionModel;
    private int currentClickGroupPosition = 0;

    private boolean isFinishAnimation = true;
    private ActivityDevicesExpadableListBinding mExpadableListBinding;

    private boolean isSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isDefaultTitle = false;
        super.onCreate(savedInstanceState);
        mExpadableListBinding = DataBindingUtil.setContentView(_this, R.layout.activity_devices_expadable_list);
        setSupportActionBar(mExpadableListBinding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mExpadableListBinding.toolbar.setTitle(StringUtils.BlankToDefault(getIntent().getStringExtra(Config.TITLE_NAME), "选择设备"));
        initView();
        loadData();
    }

    @Override
    public void initUI() {

    }

    @Override
    public void initData() {

    }


    public void initView() {
        currentFunctionModel = (PMSDeviceType) getIntent().getSerializableExtra(FUNCTION_MODEL);
        currentBdzId = getIntent().getStringExtra(BDZID);
        mExpadableListBinding.elvContainer.setOnGroupExpandListener(groupPosition -> {
            if (isFinishAnimation) {
                for (int i = 0, count = mDeviceExpandableAdapater.getGroupCount(); i < count; i++) {
                    if (i != groupPosition && mExpadableListBinding.elvContainer.isGroupExpanded(i)) {
                        mExpadableListBinding.elvContainer.collapseGroup(i);
                    }
                }
            }
        });
    }

    List<DbModel> mDeviceList = null;
    List<Spacing> mSpacingList = null;

    public void loadData() {
        if (TextUtils.isEmpty(currentBdzId) || currentFunctionModel == null) {
            ToastUtils.showMessage("没有获取到正确的变电站和设备类别");
            return;
        }
        String spid = getIntent().getStringExtra(SPCAEID);
        String bigId = getIntent().getStringExtra(BIGID);
        ExecutorManager.executeTaskSerially(() -> {
            try {
                mSpacingList = SpacingService.getInstance().findSpacingByModel(currentBdzId, currentFunctionModel.name());
                if (mSpacingList != null) {
                    groupList.clear();
                    groupHashMap.clear();
                    String expr = "";
                    if (!TextUtils.isEmpty(spid)) {
                        expr += " and d.spid='" + spid + "' ";
                    }
                    if (!TextUtils.isEmpty(bigId)) {
                        expr += " and d.bigid in(" + bigId + ") ";
                    }
                    SqlInfo sqlInfo = new SqlInfo(
                            "select * from device d where d.bdzid='" + currentBdzId + "'" + expr +
                                    " and d.dlt='0' and d.device_type='" + currentFunctionModel.name() +
                                    "'  group by d.deviceid order by sort");
                    mDeviceList = DeviceService.getInstance().findDbModelAll(sqlInfo);
                    for (Spacing mSpacing : mSpacingList) {
                        ArrayList<DbModel> dbModels = new ArrayList<>();
                        for (DbModel dbModel : mDeviceList) {
                            if (dbModel.getString("spid").equalsIgnoreCase(mSpacing.spid)) {
                                dbModel.add("spaceName", mSpacing.name);
                                dbModels.add(dbModel);
                            }
                        }
                        if (!dbModels.isEmpty()) {
                            groupList.add(mSpacing);
                            groupHashMap.put(mSpacing, dbModels);
                        }
                    }
                }
            } catch (Exception e) {
                Log.d("Tag", e.getMessage());
            }
            mHandler.sendEmptyMessage(LOAD_DATA);
        });
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                if (mDeviceExpandableAdapater == null) {
                    mDeviceExpandableAdapater = new DeviceExpandabelListAdapter(_this);
                    mDeviceExpandableAdapater.setOnAdapterViewClickListener(this);
                    mExpadableListBinding.elvContainer.setAdapter(mDeviceExpandableAdapater);
                }
                mDeviceExpandableAdapater.setGroupList(groupList);
                mDeviceExpandableAdapater.setGroupMap(groupHashMap);
                if (groupList != null && !groupList.isEmpty() && mDeviceExpandableAdapater.getChildrenCountByGroup(currentClickGroupPosition) > 1) {
                    mExpadableListBinding.elvContainer.expandGroup(currentClickGroupPosition);
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void OnAdapterViewClick(View view, DbModel mDevice) {
        dataMap.put(Config.DEVICE_DATA, mDevice);
        setResult(RESULT_OK);
        _this.finish();
    }

    @Override
    public void OnItemViewClick(AdapterView<?> parent, View view, DbModel mDevice, Spacing mCurrentSpacing) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, DbModel mDevice, Spacing mCurrentSpacing) {
        return false;
    }

    @Override
    public boolean OnGroupItemLongClick(Spacing mSpacing) {
        return false;
    }

    @Override
    public void OnGroupItemClick(Spacing mSpacing, View v, int groupPosition) {
        mExpadableListBinding.elvContainer.expandGroup(groupPosition, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_view_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setQueryHint("输入设备名称");
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
                isSearch = true;
                packagingData(newText);
                return true;
            }

        });
        searchView.setOnCloseListener(() -> {
            isSearch = false;
            return false;
        });
        return true;
    }

    private void packagingData(String text) {
        groupList.clear();
        groupHashMap.clear();
        for (Spacing mSpacing : mSpacingList) {
            ArrayList<DbModel> dbModels = new ArrayList<DbModel>();
            for (DbModel dbModel : mDeviceList) {
                if (isSearch && dbModel.getString("spid").equalsIgnoreCase(mSpacing.spid)) {
                    if ((dbModel.getString("name").contains(text) || dbModel.getString(Device.NAME_PINYIN).contains(text.toUpperCase(Locale.ENGLISH)))
                            || (!TextUtils.isEmpty(mSpacing.name) && mSpacing.name.contains(text)) || (!TextUtils.isEmpty(mSpacing.name_pinyin) && mSpacing.name_pinyin.contains(text.toUpperCase(Locale.ENGLISH)))
                            ) {
                        dbModel.add("spaceName", mSpacing.name);
                        dbModels.add(dbModel);
                    }
                }
            }
            if (!dbModels.isEmpty()) {
                groupList.add(mSpacing);
                groupHashMap.put(mSpacing, dbModels);
            }
        }
        mHandler.sendEmptyMessage(LOAD_DATA);
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
