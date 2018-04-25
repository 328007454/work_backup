package com.cnksi.inspe.ui;

import android.content.Intent;
import android.support.v4.util.ArraySet;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.core.common.ExecutorManager;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private String bdzId;
    private boolean isFirstLoad = true;
    private InspecteTaskEntity taskEntity;
    Map<String ,Integer> checkMap = new HashMap<>();
    List<String> checkDevices = new ArrayList<>();
    @Override
    public int getLayoutResId() {
        return R.layout.activity_inspe_device;
    }

    @Override
    public void initUI() {
        deviceBinding = (ActivityInspeDeviceBinding) rootDataBinding;
        deviceBinding.includeInspeTitle.toolbarBackBtn.setVisibility(View.VISIBLE);
        plustekType = (PlustekType) getIntent().getSerializableExtra("plustek_type");

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

        taskEntity = (InspecteTaskEntity) getIntent().getSerializableExtra("task");
        taskId = taskEntity.id;
        bdzId = taskEntity.bdz_id;
        String bigId = taskEntity.persion_device_bigid;
        deviceBinding.includeInspeTitle.toolbarTitle.setText(TextUtils.isEmpty(taskEntity.bdz_name) ? (plustekType.getDesc()) : taskEntity.bdz_name + plustekType.getDesc());
        bigIds = com.cnksi.inspe.utils.StringUtils.getDeviceStandardsType(bigId);

//        if (TextUtils.equals(plustekType.name(),PlustekType.pmsjc.name())){
//        deviceBinding.btnAddDevice.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public void initData() {

    }

    private void initDevice() {
        ExecutorManager.executeTaskSerially(() -> {
            try {
                bigTypeModels = new DeviceService().getBigTypeModels(bigIds);
                dbModelList = new DeviceService().getAllDeviceByBigID(bdzId, bigIds);
                List<DbModel> otherDevice = new DeviceService().getAddDevice(bdzId, taskId);
                checkMap = new DeviceService().getCheckSpace(taskId, plustekType.name());
                checkDevices = new DeviceService().getCheckDevices(taskId, plustekType.name());
                if (otherDevice != null && !otherDevice.isEmpty()) {
                    for (DbModel model : otherDevice) {
                        if (TextUtils.isEmpty(model.getString("spid"))) {
                            model.add("spid", "000000");
                            model.add("sname", "添加设备");
                            model.add("dnameshort", model.getString("name_short"));
                        }
                        if (TextUtils.isEmpty(model.getString("dnameshort"))) {
                            model.add("dnameshort", model.getString("name_short"));
                        }
                    }
                    dbModelList.addAll(otherDevice);
                }
            } catch (DbException e) {
                e.printStackTrace();
                dbModelList = new ArrayList<>();
            }

            runOnUiThread(() -> {
                deviceAdapter.setListCheck(checkMap);
                deviceAdapter.setCheckDevice(checkDevices);
                filterDevice(checkModule);


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

    @Override
    protected void onStart() {
        super.onStart();
        if (!isFirstLoad) {
            expandPosition = deviceAdapter.getExpandPosition();
        } else {
            isFirstLoad = !isFirstLoad;
        }
        devicesList.clear();
        deviceAdapter.notifyDataSetChanged();
        initDevice();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 组装adapter数据
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
            if (expandPosition == -1) {
                expandPosition = 0;
            }
            deviceAdapter.notifyDataSetChanged();
            if (!devicesList.isEmpty()) {
                try {
                    deviceBinding.inspeRecDevice.scrollToPosition(expandPosition);
                    deviceAdapter.expand(expandPosition);
                } catch (Exception e) {
                }
            }
        });
    }

    private void initOnClick() {
        //返回
        deviceBinding.includeInspeTitle.toolbarBackBtn.setOnClickListener(view -> onBackPressed());
        //设备大类列表
        deviceBinding.txtBigDevice.setOnClickListener(view -> {
            popItemWindow.setPopWindowWidth(deviceBinding.txtBigDevice.getWidth());
            popItemWindow.showAsDropDown(deviceBinding.txtBigDevice);
        });

        deviceBinding.btnAddDevice.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddDeviceAtivity.class);
            intent.putExtra("bdzId", bdzId);
            intent.putExtra("task_id", taskId);
            intent.putExtra("bigid", bigIds);
            startActivity(intent);
        });

        deviceBinding.btnFinish.setOnClickListener(view -> {
            finish();
        });

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
        devicesList.clear();
        deviceAdapter.notifyDataSetChanged();
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

    private DbModel checkModule;

    private void filterDevice(DbModel bigEntity) {
        devicesList.clear();
        deviceAdapter.notifyDataSetChanged();
        String name = bigEntity == null ? "全部" : bigEntity.getString("name");
        deviceBinding.txtBigDevice.setText(name);
        if (bigEntity==null||TextUtils.equals(name, "全部")) {
            loadAdapterData(dbModelList);
            return;
        }
        String bigId = bigEntity.getString("bigid");
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

    int expandPosition = -1;

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (position == bigTypeModels.size()) {
            checkModule = null;
        } else {
            checkModule = bigTypeModels.get(position);
        }
        expandPosition = -1;
        deviceAdapter.setExpandablePosition(expandPosition);
        filterDevice(checkModule);

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
        intent.putExtra("spid",((DeviceItem) item).dbModel.getString("spid"));
        startActivity(intent);
    }
}
