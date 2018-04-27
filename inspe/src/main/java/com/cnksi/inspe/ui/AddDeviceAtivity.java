package com.cnksi.inspe.ui;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.inspe.R;
import com.cnksi.inspe.adapter.AddDeviceAdapter;
import com.cnksi.inspe.adapter.SpaceItemAdapter;
import com.cnksi.inspe.base.AppBaseActivity;
import com.cnksi.inspe.databinding.ActivityInspeAddDeviceBinding;
import com.cnksi.inspe.databinding.DialogListviewLayoutBinding;
import com.cnksi.inspe.db.DeviceService;
import com.cnksi.inspe.db.entity.DeviceEntity;
import com.cnksi.inspe.utils.ArrayInspeUtils;
import com.cnksi.inspe.widget.BigDevicePopWindow;
import com.cnksi.inspe.widget.CustomDialog;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * @author Mr.K 2018/4/17
 * @description 该类主要是增加缺失的设备台账
 */
public class AddDeviceAtivity extends AppBaseActivity implements AddDeviceAdapter.OnViewClickLitener {

    AddDeviceAdapter adapter;
    ActivityInspeAddDeviceBinding binding;
    private List<DeviceEntity> entityList = new ArrayList<>();
    private List<DbModel> bigTypeModels = new ArrayList<>();
    private List<DbModel> spaceModels = new ArrayList<>();
    private BigDevicePopWindow popItemWindow;
    private Dialog spaceDialog;
    private SpaceItemAdapter spaceItemAdapter;
    private String bdzId;
    private String taskId;
    /**
     * 大类id
     */
    private String deviceBigId;
    private String bigids;
    @Override
    public int getLayoutResId() {
        return R.layout.activity_inspe_add_device;
    }

    @Override
    public void initUI() {
        binding = (ActivityInspeAddDeviceBinding) rootDataBinding;
        binding.includeInspeTitle.toolbarBackBtn.setVisibility(View.VISIBLE);

        setTitle("添加PMS台账缺失设备");
        initOnClick();
        adapter = new AddDeviceAdapter(R.layout.inspe_add_device_item, entityList);
        binding.recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.recyclerView.setLayoutManager(layoutManager);
        adapter.setOnViewClick(this);
        popItemWindow = new BigDevicePopWindow(context);
        popItemWindow.setOnItemClickListtener((view, item, position) -> {
            deviceBigId = bigTypeModels.get(position).getString("bigid");
            binding.txtBigDevice.setText(bigTypeModels.get(position).getString("name"));
            adapter.setBigId(deviceBigId);
            popItemWindow.dismiss();
        });

    }


    @Override
    public void initData() {
        bdzId = getIntent().getStringExtra("bdzId");
        taskId = getIntent().getStringExtra("task_id");
        bigids = getIntent().getStringExtra("bigid");
        entityList.add(new DeviceEntity());
        adapter.setBigId(deviceBigId);
        ExecutorManager.executeTaskSerially(() -> {
            try {
                bigTypeModels = new DeviceService().getBigTypeAll();
                spaceModels = new DeviceService().getAllOneSpace(bdzId,bigids);
            } catch (DbException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                if (!bigTypeModels.isEmpty()) {
                    popItemWindow.setAdapterData(bigTypeModels);
                }
            });
        });
    }

    private void initOnClick() {
        binding.btnSave.setOnClickListener(view -> {
            saveData();
        });
        binding.includeInspeTitle.toolbarBackBtn.setOnClickListener(view -> {
            this.finish();
        });
        binding.llContainer.setOnClickListener(view -> {
            popItemWindow.setPopWindowWidth(binding.txtBigDevice.getWidth()).showAsDropDown(binding.txtBigDevice);
        });
        binding.device.setOnClickListener(view -> {
            if (TextUtils.isEmpty(entityList.get(entityList.size() - 1).name)) {
                ToastUtils.showMessage("请输入设备名称再添加");
                return;
            }
            entityList.get(entityList.size() - 1).setDeviceInfo(bdzId, deviceBigId);
            entityList.add(new DeviceEntity());
            adapter.notifyDataSetChanged();
        });
    }

    /**
     * 保存添加的设备
     */
    private void saveData() {
        entityList.get(entityList.size() - 1).setDeviceInfo(bdzId, deviceBigId);
        List<String> deviceIds = new ArrayList<>();
        for (DeviceEntity entity : entityList) {
            if (TextUtils.isEmpty(entity.deviceid)) {
                entity.deviceid = UUID.randomUUID().toString();
            }
            entity.type = taskId;
            if (TextUtils.isEmpty(entity.name)) {
                ToastUtils.showMessage("请填写设备名称，再点击保存，否则请点击返回键推出页面");
                return;
            }
            deviceIds.add(entity.deviceid);
        }
        new DeviceService().saveExtraDevice(entityList);
        Intent intent = new Intent(context, InspePlustekIssueActivity.class)
                .putExtra(InspePlustekIssueActivity.IntentKey.START_MODE, InspePlustekIssueActivity.StartMode.NOPMS)//
                .putExtra(InspePlustekIssueActivity.IntentKey.TASK_ID, taskId)//
                .putExtra(InspePlustekIssueActivity.IntentKey.DEVICE_ID, deviceIds.remove(0))//
                .putExtra(InspePlustekIssueActivity.IntentKey.NOPMS_DEVICE_OTHER, ArrayInspeUtils.toListString(deviceIds));//
        startActivity(intent);
        this.finish();
    }

    DialogListviewLayoutBinding spaceBinding;
    private DbModel spaceModel;

    @Override
    public void onViewClick(View v, Object item, int position) {
        DeviceEntity entity = (DeviceEntity) item;
        int i = v.getId();
        if (i == R.id.img_delete) {
            entityList.remove(position);
            adapter.notifyDataSetChanged();
        } else if (i == R.id.et_space_name) {
            if (spaceDialog == null) {
                spaceBinding = DialogListviewLayoutBinding.inflate(getLayoutInflater());
                spaceDialog = CustomDialog.createDialog(this, spaceBinding.getRoot(), ViewGroup.LayoutParams.MATCH_PARENT, 0);
                spaceItemAdapter = new SpaceItemAdapter(R.layout.inspe_device_item, spaceModels);
                spaceBinding.recy.setAdapter(spaceItemAdapter);
                spaceBinding.recy.setLayoutManager(new GridLayoutManager(context, 3));
                spaceBinding.tvDialogTitle.setText("选择间隔名称");
            }
            spaceItemAdapter.setOnItemClickListener((adapter1, view, position1) -> {
                spaceModel = spaceItemAdapter.getItem(position1);
                entity.setSpace(spaceModel, deviceBigId);
                adapter.notifyItemChanged(position);
                spaceDialog.dismiss();
            });
            spaceDialog.show();
        } else if (i == R.id.framelayout) {
            ToastUtils.showMessage("请先选择设备大类");
        }
    }


}
