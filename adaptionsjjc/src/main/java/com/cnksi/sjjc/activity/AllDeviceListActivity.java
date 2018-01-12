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
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;

import com.cnksi.core.utils.CToast;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.DeviceExpandabelListAdapter;
import com.cnksi.sjjc.bean.Spacing;
import com.cnksi.sjjc.databinding.ActivityDevicesExpadableListBinding;
import com.cnksi.sjjc.enmu.PMSDeviceType;
import com.cnksi.sjjc.service.DeviceService;
import com.cnksi.sjjc.service.SpacingService;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * @author nothing 2017/12/11
 */
public class AllDeviceListActivity extends BaseActivity implements DeviceExpandabelListAdapter.OnAdapterViewClickListener {

    public final static String FUNCTION_MODEL = "fuction";
    public final static String BDZID = "bdzid";

    private LinkedList<Spacing> groupList = new LinkedList<Spacing>();
    private HashMap<Spacing, ArrayList<DbModel>> groupHashMap = new HashMap<Spacing, ArrayList<DbModel>>();
    private DeviceExpandabelListAdapter mDeviceExpandableAdapater = null;
    private PMSDeviceType currentFunctionModel;
    private String currentBdzId = "";
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
//        setChildView(R.layout.activity_devices_expadable_list);
        initUI();
        initData();
    }

    private void initUI() {
        currentFunctionModel = (PMSDeviceType) getIntent().getSerializableExtra(FUNCTION_MODEL);
        currentBdzId = getIntent().getStringExtra(BDZID);
        mExpadableListBinding.elvContainer.setOnGroupExpandListener(new OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (isFinishAnimation) {
                    for (int i = 0, count = mDeviceExpandableAdapater.getGroupCount(); i < count; i++) {
                        if (i != groupPosition && mExpadableListBinding.elvContainer.isGroupExpanded(i)) {
                            mExpadableListBinding.elvContainer.collapseGroup(i);
                        }
                    }
                }
            }
        });
    }

    List<DbModel> mDeviceList = null;
    List<Spacing> mSpacingList = null;

    private void initData() {
        if (TextUtils.isEmpty(currentBdzId) || currentFunctionModel == null) {
            CToast.showShort(_this, "没有获取到正确的变电站和设备类别");
            return;
        }
        mFixedThreadPoolExecutor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    mSpacingList = SpacingService.getInstance().findSpacingByModel(currentBdzId, currentFunctionModel.name());
                    if (mSpacingList != null) {
                        groupList.clear();
                        groupHashMap.clear();
                        Long time0 = System.currentTimeMillis();
                        Log.d("Tag", time0 + "");
                        SqlInfo sqlInfo = new SqlInfo(
                                "select * from device d where d.bdzid='" + currentBdzId + "' and d.dlt='0' and d.device_type='" + currentFunctionModel.name() + "'  group by d.deviceid order by sort");
                        mDeviceList = DeviceService.getInstance().findDbModelAll(sqlInfo);
                        for (Spacing mSpacing : mSpacingList) {
                            ArrayList<DbModel> dbModels = new ArrayList<DbModel>();
                            for (DbModel dbModel : mDeviceList) {
                                if (dbModel.getString("spid").equalsIgnoreCase(mSpacing.spid)) {
                                    dbModel.add("spaceName", mSpacing.name);
                                    dbModels.add(dbModel);
                                }
                            }
                            groupList.add(mSpacing);
                            groupHashMap.put(mSpacing, dbModels);
                        }
                        Log.d("Tag", (System.currentTimeMillis() - time0) + "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(LOAD_DATA);
            }
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

    @Event(value = R.id.btn_back)
    private void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void OnAdapterViewClick(View view, DbModel mDevice) {
        // TODO Auto-generated method stub
        dataMap.put(Config.DEVICE_DATA, mDevice);
        setResult(RESULT_OK);
        _this.finish();
    }

    @Override
    public void OnItemViewClick(AdapterView<?> parent, View view, DbModel mDevice, Spacing mCurrentSpacing) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, DbModel mDevice, Spacing mCurrentSpacing) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean OnGroupItemLongClick(Spacing mSpacing) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void OnGroupItemClick(Spacing mSpacing, View v, int groupPosition) {
        // TODO Auto-generated method stub
        //	currentClickGroupPosition = groupPosition;
        //	int childCount = mDeviceExpandableAdapater.getChildrenCountByGroup(groupPosition);
        //	if (childCount == 1) {
        //		OnItemViewClick(mElvContainer, v, mDeviceExpandableAdapater.getChild(groupPosition, 0), mDeviceExpandableAdapater.getGroup(groupPosition));
        //	} else {
        mExpadableListBinding.elvContainer.expandGroup(groupPosition, true);
//		}
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
                isSearch = true;
                packagingData(newText);
                return true;
            }

        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                isSearch = false;
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }


    private void packagingData(String text) {
        groupList.clear();
        groupHashMap.clear();
        for (Spacing mSpacing : mSpacingList) {
            ArrayList<DbModel> dbModels = new ArrayList<DbModel>();
            for (DbModel dbModel : mDeviceList) {
                if (!isSearch && dbModel.getString("spid").equalsIgnoreCase(mSpacing.spid)) {
                    dbModel.add("spaceName", mSpacing.name);
                    dbModels.add(dbModel);
                } else if (isSearch && (mSpacing.name.contains(text) || mSpacing.pinyin.contains(text.toUpperCase(Locale.ENGLISH))) && dbModel.getString("spid").equalsIgnoreCase(mSpacing.spid)) {
                    dbModel.add("spaceName", mSpacing.name);
                    dbModels.add(dbModel);
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
