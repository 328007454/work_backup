package com.cnksi.bdzinspection.activity;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.DeviceSortAdapter;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.databinding.XsActivitySpacesortBinding;
import com.cnksi.common.Config;
import com.cnksi.common.model.Device;
import com.cnksi.common.model.Spacing;
import com.cnksi.core.utils.PreferencesUtils;

import org.xutils.db.Selector;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * 间隔排序
 *
 * @author Wastrel
 * @date 创建时间：2016年4月18日 下午5:29:08 TODO
 */
public class DeviceSortActivity extends TitleActivity {

    List<Device> mData = new ArrayList<>();
    boolean hasUpdate = false;
    DeviceSortAdapter spaceAdapter;

    private String functionMode = "";
    private Spacing spacing;
    private XsActivitySpacesortBinding binding;

    @Override
    protected int setLayout() {
        return R.layout.xs_activity_spacesort;
    }

    @Override
    protected String initialUI() {
        binding = (XsActivitySpacesortBinding) getDataBinding();
        spacing = (Spacing) getIntent().getSerializableExtra("space");
        binding.swlvContainer.setDragListener((from, to) -> {
            if (from != to) {
                hasUpdate = true;
                Device bean = spaceAdapter.getItem(from);
                mData.remove(bean);
                mData.add(to, bean);
                spaceAdapter.notifyDataSetChanged();
            }
        });
        return getString(R.string.xs_inspection_sort, spacing.name);
    }

    @Override
    protected void initialData() {
        functionMode = getIntent().getStringExtra(Config.CURRENT_FUNCTION_MODEL);
        currentBdzId = PreferencesUtils.get(Config.CURRENT_BDZ_ID, "");
        // TODO: 2017/3/22
        try {
            Selector selector = XunshiApplication.getDbUtils().selector(Device.class).where(Device.DLT, "=", "0").and(Device.SPID, "=", spacing.spid).and(Device.DEVICE_TYPE, "=", functionMode).orderBy(Device.SORT);
            mData = selector.findAll();
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        spaceAdapter = new DeviceSortAdapter(currentActivity, mData);
        binding.swlvContainer.setAdapter(spaceAdapter);
    }


    @Override
    protected void releaseResAndSaveData() {
        if (hasUpdate) {
            updateDeviceSort();
        }
        setResult(RESULT_OK);
    }

    private void updateDeviceSort() {
        for (int i = 0; i < mData.size(); i++) {
            mData.get(i).sort = i + 1;
        }
        try {
            XunshiApplication.getDbUtils().update(mData, "sort");
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
