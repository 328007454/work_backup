package com.cnksi.bdzinspection.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.SpaceSplitAdapter;
import com.cnksi.bdzinspection.adapter.base.GridSpacingItemDecoration;
import com.cnksi.bdzinspection.application.CustomApplication;
import com.cnksi.bdzinspection.daoservice.CopyItemService;
import com.cnksi.bdzinspection.daoservice.DeviceService;
import com.cnksi.bdzinspection.databinding.XsActivitySpaceSplitBinding;
import com.cnksi.bdzinspection.model.CopyItem;
import com.cnksi.bdzinspection.model.Device;
import com.cnksi.bdzinspection.model.Spacing;
import com.cnksi.bdzinspection.utils.DialogUtils;
import com.cnksi.bdzinspection.utils.FunctionUtil;
import com.cnksi.bdzinspection.utils.OnViewClickListener;
import com.cnksi.xscore.xsutils.CToast;
import com.cnksi.xscore.xsutils.DateUtils;
import com.cnksi.xscore.xsutils.StringUtils;
import com.cnksi.xscore.xsview.CustomerDialog;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.SqlInfo;
import com.lidroid.xutils.db.table.DbModel;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
    protected String initUI() {
        binding = (XsActivitySpaceSplitBinding) getDataBinding();
        spacing = (Spacing) getIntent().getSerializableExtra("space");
        mode = getIntent().getStringExtra("mode");
        binding.etSpaceName.setText(spacing.name);
        binding.etSpaceName.setSelection(spacing.name.length());
        binding.btnComplete.setOnClickListener(v -> {
            final HashSet<Device> selectDevice = adapter.getSelectDevices();
            if (selectDevice.size() == devices.size()) {
                CToast.showShort(currentActivity, "至少需要留一个设备给原间隔！");
                return;
            }
            if (selectDevice.size() == 0) {
                CToast.showShort(currentActivity, "没有选择设备！");
                return;
            }
            String newName = binding.etSpaceName.getText().toString();
            if (TextUtils.isEmpty(newName)) {
                CToast.showShort(currentActivity, "新间隔名字不能为空！");
                return;
            }
            if (newName.equals(spacing.name)) {
                CToast.showShort(currentActivity, "新间隔名称与原间隔名称一致，建议修改便于区分！");
            }
            CharSequence tips = StringUtils.formatPartTextColor("您是否要将选中的%s个设备拆分到新间隔 %s ?", Color.RED, selectDevice.size() + "", newName);
            DialogUtils.showSureTipsDialog(currentActivity, null, tips, new OnViewClickListener() {
                @Override
                public void onClick(View v) {
                    CustomerDialog.showProgress(currentActivity, "保存数据中...");
                    mFixedThreadPoolExecutor.execute(() -> {
                        final boolean rs = saveData(new ArrayList<>(selectDevice));
                        runOnUiThread(() -> {
                            CustomerDialog.dismissProgress();
                            if (rs) {
                                CToast.showShort(currentActivity, "操作成功");
                                Intent intent = new Intent();
                                intent.putExtra("space", spacing);
                                intent.putExtra("count", selectDevice.size());
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                CToast.showLong(currentActivity, "更新数据库失败！请检查数据是否完整！");
                            }
                        });
                    });

                }
            });

        });
        return "选择拆分设备";
    }


    @Override
    protected void initData() {
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    devices = DeviceService.getInstance().findDeviceBySpacing(spacing, mode);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                copyItems = CopyItemService.getInstance().findAllBySpace(spacing.spid);
                runOnUiThread(() -> initrcv());
            }
        });
    }

    private void initrcv() {
        if (devices == null && devices.size() == 0) {
            CToast.showShort(currentActivity, "数据异常，该间隔下没有查询到设备！");
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
        String newSpid = FunctionUtil.getPrimarykey();
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
        DbUtils dbUtils = CustomApplication.getDbUtils();
        try {
            dbUtils.beginTransaction();
            //复制一个新间隔
            DbModel model = CustomApplication.getDbUtils().findDbModelFirst(new SqlInfo("select * from spacing where spid=?", spacing.pid));
            StringBuilder insertSql = new StringBuilder("INSERT INTO spacing( ");
            StringBuilder values = new StringBuilder();
            for (Map.Entry<String, String> entry : model.getDataMap().entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                insertSql.append(k).append(",");
                if (k.equals("spid")) v = spacing.spid;
                if (k.equals("pid")) v = spacing.pid;
                if (k.equals("name")) v = spacing.name;
                if (k.equals("device_type")) v = spacing.deviceType;
                if (k.equals("type")) v = "spacing_self";
                if (k.equals("update_time") || k.equals("create_time"))
                    v = DateUtils.getCurrentLongTime();
                if (v == null) {
                    values.append("null").append(",");
                } else
                    values.append("'").append(v).append("',");
            }
            insertSql.deleteCharAt(insertSql.length() - 1).append(") VALUES(").append(values.deleteCharAt(values.length() - 1).toString()).append(");");
            //保存一个新间隔
            dbUtils.execNonQuery(insertSql.toString());

            //更新设备表的SPID
            String sql = "update device set spid='" + newSpid + "' where deviceid in(" + deviceIds.toString() + ")";
            dbUtils.execNonQuery(sql);
            //更新抄录表的SPID
            if (copyItemIds.length() > 0) {
                copyItemIds.deleteCharAt(copyItemIds.length() - 1);
                sql = "update copy_item set spid='" + newSpid + "' where id in(" + copyItemIds.toString() + ")";
                dbUtils.execNonQuery(sql);
            }
            dbUtils.setTransactionSuccessful();
            return true;
        } catch (DbException e) {
            e.printStackTrace();
        } finally {
            dbUtils.endTransaction();
        }
        return false;
    }

    @Override
    protected void releaseResAndSaveData() {

    }
}
