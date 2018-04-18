package com.cnksi.inspe.ui;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
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
import com.cnksi.inspe.entity.device.SpaceItem;
import com.cnksi.inspe.widget.CustomDialog;
import com.cnksi.inspe.widget.PopItemWindow;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

public class AddDeviceAtivity extends AppBaseActivity implements AddDeviceAdapter.OnViewClickLitener, BaseQuickAdapter.OnItemClickListener {

    AddDeviceAdapter adapter;
    ActivityInspeAddDeviceBinding binding;
    private List<DeviceEntity> entityList = new ArrayList<>();
    private List<DbModel> bigTypeModels = new ArrayList<>();
    private List<DbModel> spaceModels = new ArrayList<>();
    private PopItemWindow popItemWindow;
    private Dialog spaceDialog;
    private SpaceItemAdapter spaceItemAdapter;
    private String bdzId;

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

        popItemWindow = new PopItemWindow(this);
        popItemWindow.setOnItemClickListener(this);
        popItemWindow.setLayoutManager(new GridLayoutManager(context, 3));
    }


    @Override
    public void initData() {
        bdzId = getIntent().getStringExtra("bdzId");
        entityList.add(new DeviceEntity());
        adapter.setBigId(deviceBigId);
        ExecutorManager.executeTaskSerially(() -> {
            try {
                bigTypeModels = new DeviceService().getBigTypeAll();
                spaceModels = new DeviceService().getAllOneSpace();
            } catch (DbException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                if (!bigTypeModels.isEmpty()) {
                    List<String> bigTypeNames = new ArrayList<>();
                    for (DbModel model : bigTypeModels) {
                        bigTypeNames.add(TextUtils.isEmpty(model.getString("name")) ? "" : model.getString("name"));
                    }
                    popItemWindow.setListAdapter(bigTypeNames);
                }
            });
        });
    }

    private void initOnClick() {
        binding.btnSave.setOnClickListener(view -> {
            saveData();
            Intent intent = new Intent(this, InspePlustekIssueActivity.class);
            startActivity(intent);
        });
        binding.includeInspeTitle.toolbarBackBtn.setOnClickListener(view -> {
            this.finish();
        });
        binding.llContainer.setOnClickListener(view -> {
            popItemWindow.setPopWindowWidth(binding.txtBigDevice.getWidth());
            popItemWindow.showAsDropDown(binding.txtBigDevice);
        });
        binding.device.setOnClickListener(view -> {
            entityList.get(entityList.size() - 1).setDeviceInfo(bdzId, deviceBigId);
            entityList.add(new DeviceEntity());
            adapter.notifyDataSetChanged();
        });
    }

    /**
     * 保存添加的设备
     */
    private void saveData() {
        ArrayList<String> deviceIds = new ArrayList<>();
        for (DeviceEntity entity: entityList){
            deviceIds.add(entity.deviceid);
        }
        Intent intent = new Intent ();
        intent.putStringArrayListExtra("device_id_arrays",deviceIds);

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

    private String deviceBigId;

    /**
     * 设备大类选择Item点击事件
     *
     * @param adapter1
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(BaseQuickAdapter adapter1, View view, int position) {
        deviceBigId = bigTypeModels.get(position).getString("bigid");
        binding.txtBigDevice.setText(bigTypeModels.get(position).getString("name"));
        adapter.setBigId(deviceBigId);
    }
}
