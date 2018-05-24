package com.cnksi.bdzinspection.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.SpaceSplitAdapter;
import com.cnksi.bdzinspection.adapter.base.GridSpacingItemDecoration;
import com.cnksi.bdzinspection.databinding.XsActivitySpaceSplitBinding;
import com.cnksi.common.daoservice.CopyItemService;
import com.cnksi.common.daoservice.DeviceService;
import com.cnksi.common.daoservice.SpacingService;
import com.cnksi.common.listener.OnViewClickListener;
import com.cnksi.common.model.BaseModel;
import com.cnksi.common.model.CopyItem;
import com.cnksi.common.model.Device;
import com.cnksi.common.model.Spacing;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.view.CustomerDialog;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/1/17 11:37
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class SpaceSplitActivity extends TitleActivity {

    private SpaceSplitAdapter adapter;
    private String mode;
    private Spacing spacing;
    private List<Device> devices;
    private List<CopyItem> copyItems;
    private XsActivitySpaceSplitBinding binding;

    @Override
    protected int setLayout() {
        return R.layout.xs_activity_space_split;
    }

    @Override
    protected String initialUI() {
        binding = (XsActivitySpaceSplitBinding) getDataBinding();
        spacing = (Spacing) getIntent().getSerializableExtra("space");
        mode = getIntent().getStringExtra("mode");
        binding.etSpaceName.setText(spacing.name);
        binding.etSpaceName.setSelection(spacing.name.length());
        binding.btnComplete.setOnClickListener(v -> {
            final HashSet<Device> selectDevice = adapter.getSelectDevices();
            if (selectDevice.size() == devices.size()) {
                ToastUtils.showMessage( "至少需要留一个设备给原间隔！");
                return;
            }
            if (selectDevice.size() == 0) {
                ToastUtils.showMessage( "没有选择设备！");
                return;
            }
            String newName = binding.etSpaceName.getText().toString();
            if (TextUtils.isEmpty(newName)) {
                ToastUtils.showMessage( "新间隔名字不能为空！");
                return;
            }
            if (newName.equals(spacing.name)) {
                ToastUtils.showMessage( "新间隔名称与原间隔名称一致，建议修改便于区分！");
            }
            CharSequence tips = StringUtils.formatPartTextColor("您是否要将选中的%s个设备拆分到新间隔 %s ?", Color.RED, selectDevice.size() + "", newName);
            DialogUtils.showSureTipsDialog(currentActivity, null, tips, new OnViewClickListener() {
                @Override
                public void onClick(View v) {
                    CustomerDialog.showProgress(currentActivity, "保存数据中...");
                    ExecutorManager.executeTask(() -> {
                        final boolean rs = saveData(new ArrayList<>(selectDevice));
                        runOnUiThread(() -> {
                            CustomerDialog.dismissProgress();
                            if (rs) {
                                ToastUtils.showMessage( "操作成功");
                                Intent intent = new Intent();
                                intent.putExtra("space", spacing);
                                intent.putExtra("count", selectDevice.size());
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                ToastUtils.showMessageLong( "更新数据库失败！请检查数据是否完整！");
                            }
                        });
                    });

                }
            });

        });
        return "选择拆分设备";
    }


    @Override
    protected void initialData() {
        ExecutorManager.executeTask(() -> {
            try {
                devices = DeviceService.getInstance().findDeviceBySpacing(spacing, mode);
            } catch (DbException e) {
                e.printStackTrace();
            }
            copyItems = CopyItemService.getInstance().findAllBySpace(spacing.spid);
            runOnUiThread(() -> initrcv());
        });
    }

    private void initrcv() {
        if (devices == null && devices.size() == 0) {
            ToastUtils.showMessage( "数据异常，该间隔下没有查询到设备！");
        }
        HashSet<String> copyDeviceId = new HashSet<>();
        for (CopyItem item : copyItems) {
            copyDeviceId.add(item.deviceid);
        }
        adapter = new SpaceSplitAdapter(binding.rcv, devices);
        adapter.setCopyDeviceIds(copyDeviceId);
        binding.rcv.setLayoutManager(new GridLayoutManager(currentActivity, 2));
        binding.rcv.addItemDecoration(new GridSpacingItemDecoration(2, 20, true));
        binding.rcv.setAdapter(adapter);
    }


    public boolean saveData(List<Device> devices) {
        spacing.pid = spacing.spid;
        String newSpid = BaseModel.getPrimarykey();
        spacing.spid = newSpid;
        spacing.name = binding.etSpaceName.getText().toString();
        spacing.deviceType = mode;
        HashSet<String> deviceIdSet = new HashSet<>();
        StringBuilder deviceIds = new StringBuilder();
        for (Device device : devices) {
            deviceIdSet.add(device.deviceid);
            deviceIds.append("'").append(device.deviceid).append("',");
        }
        deviceIds.deleteCharAt(deviceIds.length() - 1);
        StringBuilder copyItemIds = new StringBuilder();
        for (CopyItem item : copyItems) {
            if (deviceIdSet.contains(item.deviceid)) {
                copyItemIds.append("'").append(item.id).append("',");
            }
        }
        return SpacingService.getInstance().copySpacing(spacing,newSpid,copyItemIds,deviceIds);
    }

    @Override
    protected void releaseResAndSaveData() {

    }
}
