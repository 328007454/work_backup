package com.cnksi.bdzinspection.activity;

import android.content.Intent;
import android.view.View;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.ItemClickListener;
import com.cnksi.bdzinspection.adapter.SpaceSortAdapter;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.daoservice.BaseService;
import com.cnksi.bdzinspection.daoservice.SpacingService;
import com.cnksi.bdzinspection.databinding.XsActivitySpacesortBinding;
import com.cnksi.bdzinspection.model.Spacing;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.DialogUtils;
import com.cnksi.bdzinspection.utils.OnViewClickListener;
import com.cnksi.xscore.xsutils.CToast;
import com.cnksi.xscore.xsutils.PreferencesUtils;
import com.cnksi.xscore.xsview.CustomerDialog;

import org.xutils.DbManager;
import org.xutils.db.Selector;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 间隔排序
 *
 * @author Wastrel
 * @date 创建时间：2016年4月18日 下午5:29:08 TODO
 */
public class SpaceSortActivity extends TitleActivity {

    List<Spacing> mData = new ArrayList<Spacing>();
    HashMap<String, Integer> deviceCountMap;
    SpaceSortAdapter spaceAdapter;
    boolean hasUpdate = false;
    private String functionMode = "";
    private int currentPosition = 0;
    private XsActivitySpacesortBinding binding;


    @Override
    protected int setLayout() {
        return R.layout.xs_activity_spacesort;
    }

    @Override
    protected String initUI() {
        binding = (XsActivitySpacesortBinding) getDataBinding();
        binding.swlvContainer.setDragListener((from, to) -> {
            if (from != to) {
                hasUpdate = true;
                Spacing bean = spaceAdapter.getItem(from);
                mData.remove(bean);
                mData.add(to, bean);
                spaceAdapter.notifyDataSetChanged();
            }
        });
        return getString(R.string.xs_inspection_sort, getIntent().getStringExtra(Config.TITLE_NAME));
    }

    @Override
    protected void initData() {
        functionMode = getIntent().getStringExtra(Config.CURRENT_FUNCTION_MODEL);
        String sort = "one".equals(functionMode) ? Spacing.SORT_ONE
                : "second".equals(functionMode) ? Spacing.SORT_SECOND : Spacing.SORT;
        currentBdzId = PreferencesUtils.getString(currentActivity, Config.CURRENT_BDZ_ID, "");
        // TODO: 2017/3/22
        try {
            Selector selector = XunshiApplication.getDbUtils().selector(Spacing.class).where(Spacing.DLT, "=", 0).and(Spacing.BDZID, "=", currentBdzId).
                    expr("and spid in (select distinct(spid) spid from device where device_type = '" + functionMode
                            + "' and bdzid = '" + currentBdzId + "'  and dlt=0)")
                    .orderBy(sort, false);
            mData = selector.findAll();
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        deviceCountMap = SpacingService.getInstance().groupBySpacingCount(currentBdzId, functionMode);
        spaceAdapter = new SpaceSortAdapter(currentActivity, mData, deviceCountMap);
        spaceAdapter.setClickListener((v, data, position) -> {
            if (data.isSubPlace()) {
                DialogUtils.showSureTipsDialog(currentActivity, null, "是否恢复设备到原间隔？", new OnViewClickListener() {
                    @Override
                    public void onClick(View v) {
                        CustomerDialog.showProgress(currentActivity, "恢复中...");
                        mFixedThreadPoolExecutor.execute(() -> {
                            final boolean rs = restore(data);
                            runOnUiThread(() -> {
                                CustomerDialog.dismissProgress();
                                if (rs) {
                                    CToast.showShort(currentActivity, "恢复间隔操作成功");
                                    Integer count = deviceCountMap.get(data.pid);
                                    count = count + deviceCountMap.get(data.spid);
                                    deviceCountMap.put(data.pid, count);
                                    mData.remove(data);
                                    spaceAdapter.notifyDataSetChanged();
                                } else {
                                    CToast.showShort(currentActivity, "更新数据库失败，请检查数据库！");
                                }
                            });
                        });
                        restore(data);
                    }
                });
            } else {
                currentPosition = position;
                Intent intent = new Intent(currentActivity, SpaceSplitActivity.class);
                intent.putExtra("space", data);
                intent.putExtra("mode", functionMode);
                startActivityForResult(intent, LOAD_DATA);
            }
        });
        spaceAdapter.setSortListener(new ItemClickListener<Spacing>() {
            @Override
            public void onClick(View v, Spacing data, int position) {
                Intent intent = new Intent(currentActivity, DeviceSortActivity.class);
                intent.putExtra("space", data);
                intent.putExtra(Config.CURRENT_FUNCTION_MODEL, functionMode);
                startActivity(intent);
            }
        });
        binding.swlvContainer.setAdapter(spaceAdapter);
    }

    private boolean restore(Spacing spacing) {
        String sqlDevice = "update device set spid='" + spacing.pid + "' where spid='" + spacing.spid + "'";
        String sqlCopyItem = "update copy_item set spid='" + spacing.pid + "' where spid='" + spacing.spid + "'";
       DbManager manager= XunshiApplication.getDbUtils();
        try {
            manager.beginTransaction();
            manager.execNonQuery(sqlDevice);
            manager.execNonQuery(sqlCopyItem);
            manager.execNonQuery("update spacing set dlt=1 where spid='" + spacing.spid + "'");
            manager.setTransactionSuccessful();
            return true;
        } catch (DbException e) {
            e.printStackTrace();
        } finally {
            manager.endTransaction();
        }
        return false;

    }

    @Override
    protected void releaseResAndSaveData() {
        if (hasUpdate)
            updateSpaceSort();
        setResult(RESULT_OK);
    }

    private void updateSpaceSort() {
        String updateColumnNames;
        if ("one".equals(functionMode)) {
            for (int i = 0; i < mData.size(); i++) {
                mData.get(i).sort_one = i + 1;
            }
            updateColumnNames = "sort_one";
        } else if ("second".equals(functionMode)) {
            for (int i = 0; i < mData.size(); i++) {
                mData.get(i).sort_second = i + 1;
            }
            updateColumnNames = "sort_second";
        } else {
            for (int i = 0; i < mData.size(); i++) {
                mData.get(i).sort = i + 1;
            }
            updateColumnNames = "sort";
        }
        try {
            XunshiApplication.getDbUtils().update(mData, updateColumnNames);
            PreferencesUtils.put(this, "RELOAD_DATA", true);
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == LOAD_DATA) {
            //在当前间隔位置插入拆分出来的间隔
            Spacing spacing = (Spacing) data.getSerializableExtra("space");
            int count = data.getIntExtra("count", 0);
            Spacing oldSpace = mData.get(currentPosition);
            int oldCount = deviceCountMap.get(oldSpace.spid);
            deviceCountMap.put(oldSpace.spid, oldCount - count);
            deviceCountMap.put(spacing.spid, count);
            mData.add(currentPosition + 1, spacing);
            spaceAdapter.notifyDataSetChanged();
        }
    }
}
