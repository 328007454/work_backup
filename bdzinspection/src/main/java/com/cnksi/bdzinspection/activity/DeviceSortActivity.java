package com.cnksi.bdzinspection.activity;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.DeviceSortAdapter;
import com.cnksi.bdzinspection.application.CustomApplication;
import com.cnksi.bdzinspection.daoservice.BaseService;
import com.cnksi.bdzinspection.databinding.XsActivitySpacesortBinding;
import com.cnksi.bdzinspection.model.Device;
import com.cnksi.bdzinspection.model.Spacing;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.xscore.xsutils.PreferencesUtils;
import com.cnksi.xscore.xsview.swipemenulist.SwipeMenuDragSortListView.DragListener;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

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
    protected String initUI() {
        binding = (XsActivitySpacesortBinding) getDataBinding();
        spacing = (Spacing) getIntent().getSerializableExtra("space");
        binding.swlvContainer.setDragListener(new DragListener() {
            @Override
            public void drag(int from, int to) {
                if (from != to) {
                    hasUpdate = true;
                    Device bean = spaceAdapter.getItem(from);
                    mData.remove(bean);
                    mData.add(to, bean);
                    spaceAdapter.notifyDataSetChanged();
                }
            }
        });
        return getString(R.string.xs_inspection_sort, spacing.name);
    }

    @Override
    protected void initData() {
        functionMode = getIntent().getStringExtra(Config.CURRENT_FUNCTION_MODEL);
        currentBdzId = PreferencesUtils.getString(currentActivity, Config.CURRENT_BDZ_ID, "");
        // TODO: 2017/3/22
        Selector selector = BaseService.from(Device.class).and(Device.SPID, "=", spacing.spid).and(Device.DEVICE_TYPE, "=", functionMode).orderBy(Device.SORT);
        try {
            mData = CustomApplication.getDbUtils().findAll(selector);
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        spaceAdapter = new DeviceSortAdapter(currentActivity, mData);
        binding.swlvContainer.setAdapter(spaceAdapter);
    }


    @Override
    protected void releaseResAndSaveData() {
        if (hasUpdate)
            updateDeviceSort();
        setResult(RESULT_OK);
    }

    private void updateDeviceSort() {
        for (int i = 0; i < mData.size(); i++) {
            mData.get(i).sort = i + 1;
        }
        try {
            CustomApplication.getDbUtils().updateAll(mData, "sort");
        } catch (DbException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
