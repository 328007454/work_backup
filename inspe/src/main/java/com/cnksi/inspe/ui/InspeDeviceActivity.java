package com.cnksi.inspe.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.inspe.R;
import com.cnksi.inspe.adapter.DeviceAdapter;
import com.cnksi.inspe.base.AppBaseActivity;
import com.cnksi.inspe.databinding.ActivityInspeDeviceBinding;
import com.cnksi.inspe.db.DeviceService;
import com.cnksi.inspe.db.entity.InspecteTaskEntity;
import com.cnksi.inspe.entity.device.DeviceItem;
import com.cnksi.inspe.entity.device.SpaceItem;
import com.cnksi.inspe.type.PlustekType;
import com.cnksi.inspe.widget.PopItemWindow;
import com.cnksi.inspe.widget.keyboard.QWERKeyBoardUtils;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Mr.K
 * @decription 精益化评价设备列表
 * @Date 2018-4-9
 */
public class InspeDeviceActivity extends AppBaseActivity implements QWERKeyBoardUtils.keyWordChangeListener, BaseQuickAdapter.OnItemClickListener, DeviceAdapter.OnDeviceItemClickListerner {
    ActivityInspeDeviceBinding deviceBinding;
    /**
     * 键盘控件
     */
    private QWERKeyBoardUtils qwerKeyBoardUtils;
    private DeviceAdapter deviceAdapter;
    private List<MultiItemEntity> devicesList = new ArrayList<>();
    private List<DbModel> dbModelList = null;
    private List<DbModel> bigTypeModels = new ArrayList<>();
    private PopItemWindow popItemWindow;
    private String taskId;
    private String bigIds = "";
    private PlustekType plustekType;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_inspe_device;
    }

    @Override
    public void initUI() {
        deviceBinding = (ActivityInspeDeviceBinding) rootDataBinding;

        deviceBinding.includeInspeTitle.toolbarBackBtn.setVisibility(View.VISIBLE);
        plustekType = (PlustekType) getIntent().getSerializableExtra("plustek_type");
        deviceBinding.includeInspeTitle.toolbarTitle.setText(plustekType.getDesc());
        qwerKeyBoardUtils = new QWERKeyBoardUtils(this);
        qwerKeyBoardUtils.init(deviceBinding.keyboardContainer, this);

        deviceAdapter = new DeviceAdapter(this, devicesList);
        deviceBinding.inspeRecDevice.setAdapter(deviceAdapter);
        deviceAdapter.setOnDeviceItemClickListener(this);

        GridLayoutManager manager = new GridLayoutManager(this, 3);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return deviceAdapter.getItemViewType(position) == DeviceAdapter.DEVICE_ITEM ? 1 : manager.getSpanCount();
            }
        });
        deviceBinding.inspeRecDevice.setLayoutManager(manager);

        popItemWindow = new PopItemWindow(this);
        popItemWindow.setOnItemClickListener(this);
        initOnClick();
    }

    @Override
    public void initData() {
        InspecteTaskEntity taskEntity = (InspecteTaskEntity) getIntent().getSerializableExtra("task");
        taskId = taskEntity.id;
        String bdzId = taskEntity.bdz_id;
        String bigId = taskEntity.checked_device_bigid;
        bigIds = com.cnksi.inspe.utils.StringUtils.getDeviceStandardsType(bigId);
        ExecutorManager.executeTaskSerially(() -> {
            try {
                bigTypeModels = new DeviceService().getBigTypeModels(bigIds);
                dbModelList = new DeviceService().getAllDeviceByBigID(bdzId, bigIds);
            } catch (DbException e) {
                e.printStackTrace();
                dbModelList = new ArrayList<>();
            }
            loadAdapterData(dbModelList);
            runOnUiThread(() -> {
                if (!bigTypeModels.isEmpty()) {
                    List<String> bigTypeNames = new ArrayList<>();
                    for (DbModel model : bigTypeModels) {
                        bigTypeNames.add(TextUtils.isEmpty(model.getString("name")) ? "" : model.getString("name"));
                    }
                    bigTypeNames.add("全部");
                    popItemWindow.setListAdapter(bigTypeNames);
                }
            });
        });
    }

    /**
     * 组装adapter数据
     *
     */
    private void loadAdapterData(List<DbModel> models) {
        if (!models.isEmpty()) {
            Set<String> spaceIds = new ArraySet<>();
            for (DbModel model : models) {
                if (spaceIds.contains(model.getString("spid")))
                    continue;
                SpaceItem spaceItem = new SpaceItem(model);
                spaceItem.addAll(models);
                devicesList.add(spaceItem);
                spaceIds.add(model.getString("spid"));
            }
        }
        runOnUiThread(() -> {
            if (devicesList.size() > 0) {
                deviceAdapter.expand(0);
            }
            deviceAdapter.notifyDataSetChanged();
        });
    }

    private void initOnClick() {
        //返回
        deviceBinding.includeInspeTitle.toolbarBackBtn.setOnClickListener(view -> onBackPressed());
        //设备大类列表
        deviceBinding.txtBigDevice.setOnClickListener(view -> {
            popItemWindow.setPopWindowWidth(deviceBinding.txtBigDevice.getWidth());
            popItemWindow.showAsDropDown(deviceBinding.txtBigDevice);
        })
        ;

    }

    /**
     * 界面键盘控件响应
     *
     * @param v
     * @param oldKey 老的Key
     * @param newKey 新的key
     */
    @Override
    public void onChange(View v, String oldKey, String newKey) {
        deviceAdapter.setKeyWord(newKey);
        if (!TextUtils.isEmpty(newKey)) {
            localSearchData(newKey);
        } else {
            loadAdapterData(dbModelList);
        }
    }

    /**
     * 搜索查询本地数据
     *
     * @param newKey
     */
    private void localSearchData(String newKey) {
        devicesList.clear();
        List<DbModel> locaList = new ArrayList<>();
        for (DbModel model : dbModelList) {
//            if (model.getString("snamepy").toUpperCase().contains(newKey) || model.getString("dshortpinyin").toUpperCase().contains(newKey)) {
//                locaList.add(model);
//            }

            if (!model.isEmpty("snamepy") && model.getString("snamepy").toUpperCase().contains(newKey)) {
                locaList.add(model);
            } else if (!model.isEmpty("dshortpinyin") && model.getString("dshortpinyin").toUpperCase().contains(newKey)) {
                locaList.add(model);
            }
        }

        if (!locaList.isEmpty()) {
            loadAdapterData(locaList);
        } else {
            loadAdapterData(new ArrayList<DbModel>());
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        String name = (String) adapter.getItem(position);
        deviceBinding.txtBigDevice.setText(name);
        if (TextUtils.equals(name, "全部")) {
            devicesList.clear();
            loadAdapterData(dbModelList);
            return;
        }
        String bigId = bigTypeModels.get(position).getString("bigid");
        List<DbModel> models = new ArrayList<>();
        for (DbModel model : dbModelList) {
            if (TextUtils.equals(bigId, model.getString("bigid"))) {
                models.add(model);
            }
        }
        if (!models.isEmpty()) {
            devicesList.clear();
            loadAdapterData(models);
        }
    }

    /**
     * 点击设备跳转详情界面
     *
     * @param v
     * @param item
     * @param position
     */
    @Override
    public void OnItemClickListen(View v, Object item, int position) {
        Intent intent = new Intent(this, InspeDeviceDetailsActivity.class);
        intent.putExtra("deviceBigId", ((DeviceItem) item).dbModel.getString("bigid"));
        intent.putExtra("deviceId", ((DeviceItem) item).dbModel.getString("deviceid"));
        intent.putExtra("taskId", taskId);
        intent.putExtra("deviceName", ((DeviceItem) item).dbModel.getString("dname"));
        intent.putExtra("plustek_type", plustekType);//检查类型
        startActivity(intent);
    }
}
