package com.cnksi.bdzinspection.activity;

import android.content.Intent;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.SpaceSortAdapter;
import com.cnksi.bdzinspection.databinding.XsActivitySpacesortBinding;
import com.cnksi.common.Config;
import com.cnksi.common.daoservice.SpacingService;
import com.cnksi.common.model.Spacing;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.view.CustomerDialog;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.cnksi.common.Config.LOAD_DATA;

/**
 * 间隔排序
 *
 * @author Wastrel
 * @date 创建时间：2016年4月18日 下午5:29:08 TODO
 */
public class SpaceSortActivity extends TitleActivity {

    List<Spacing> mData = new ArrayList<>();
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
    protected String initialUI() {
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
        return getString(R.string.xs_inspection_sort, getIntent().getStringExtra(Config.TITLE_NAME_KEY));
    }

    @Override
    protected void initialData() {
        functionMode = getIntent().getStringExtra(Config.CURRENT_FUNCTION_MODEL);
        String sort = "one".equals(functionMode) ? Spacing.SORT_ONE
                : "second".equals(functionMode) ? Spacing.SORT_SECOND : Spacing.SORT;
        currentBdzId = PreferencesUtils.get(Config.CURRENT_BDZ_ID, "");
        // TODO: 2017/3/22
        try {
            mData = SpacingService.getInstance().findByFunctionModel(currentBdzId, functionMode, sort);
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        deviceCountMap = SpacingService.getInstance().groupBySpacingCount(currentBdzId, functionMode);
        spaceAdapter = new SpaceSortAdapter(mActivity, mData, deviceCountMap);
        spaceAdapter.setClickListener((v, data, position) -> {
            if (data.isSubPlace()) {
                DialogUtils.showSureTipsDialog(mActivity, null, "是否恢复设备到原间隔？", v1 -> {
                    CustomerDialog.showProgress(mActivity, "恢复中...");
                    ExecutorManager.executeTask(() -> {
                        final boolean rs = restore(data);
                        runOnUiThread(() -> {
                            CustomerDialog.dismissProgress();
                            if (rs) {
                                ToastUtils.showMessage("恢复间隔操作成功");
                                Integer count = deviceCountMap.get(data.pid);
                                count = count + deviceCountMap.get(data.spid);
                                deviceCountMap.put(data.pid, count);
                                mData.remove(data);
                                spaceAdapter.notifyDataSetChanged();
                            } else {
                                ToastUtils.showMessage("更新数据库失败，请检查数据库！");
                            }
                        });
                    });
                    restore(data);
                });
            } else {
                currentPosition = position;
                Intent intent = new Intent(mActivity, SpaceSplitActivity.class);
                intent.putExtra("space", data);
                intent.putExtra("mode", functionMode);
                SpaceSortActivity.this.startActivityForResult(intent, LOAD_DATA);
            }
        });
        spaceAdapter.setSortListener((v, data, position) -> {
            Intent intent = new Intent(mActivity, DeviceSortActivity.class);
            intent.putExtra("space", data);
            intent.putExtra(Config.CURRENT_FUNCTION_MODEL, functionMode);
            startActivity(intent);
        });
        binding.swlvContainer.setAdapter(spaceAdapter);
    }

    private boolean restore(Spacing spacing) {
        return SpacingService.getInstance().restore(spacing);
    }

    @Override
    protected void releaseResAndSaveData() {
        if (hasUpdate) {
            updateSpaceSort();
        }
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
            SpacingService.getInstance().update(mData, updateColumnNames);
            PreferencesUtils.put("RELOAD_DATA", true);
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
