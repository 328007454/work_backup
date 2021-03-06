package com.cnksi.bdzinspection.fragment;

import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.CopyDeviceAdapter;
import com.cnksi.common.daoservice.CopyItemService;
import com.cnksi.bdzinspection.daoservice.SpecialMenuService;
import com.cnksi.bdzinspection.databinding.XsFragmentGridlistBinding;
import com.cnksi.bdzinspection.model.SpecialMenu;
import com.cnksi.core.common.ExecutorManager;

import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.cnksi.common.Config.LOAD_DATA;

/**
 * 抄录数据
 *
 * @author Oliver
 */
public class CopyValueFragment2 extends BaseFragment {
    private CopyDeviceAdapter adapter;

    private List<DbModel> data;
    private HashSet<String> copyDeviceIds = new HashSet<>();
    private List<DbModel> originModel = new ArrayList<>();
    /**
     * 特殊巡视抄录抄录需要根据特殊条件展示设备
     */
    private SpecialMenu specialMenu;

    /**
     * 是否选择了未抄录设备
     */
    private boolean checkDevice;

    public void setCheckDevice(boolean checkDevice) {
        this.checkDevice = checkDevice;
        if (checkDevice) {
            data.clear();
            for (DbModel model : originModel) {
                if (!copyDeviceIds.contains(model.getString("deviceid"))) {
                    data.add(model);
                }
            }
        } else {
            data.clear();
            data.addAll(originModel);
        }
        adapter.notifyDataSetChanged();
        itemClickerListener.onItemClick(currentFunctionModel,data.isEmpty()?null: data.get(0), 0);
    }

    public interface FragmentItemClickerListener<T> {
        void onItemClick(String function, T t, int position);
    }

    private FragmentItemClickerListener<DbModel> itemClickerListener;

    public void setItemClickerListener(FragmentItemClickerListener<DbModel> itemClickerListener) {
        this.itemClickerListener = itemClickerListener;
    }

    private XsFragmentGridlistBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = XsFragmentGridlistBinding.inflate(inflater);
        initialUI();
        isPrepared = true;
        lazyLoad();
        return binding.getRoot();
    }



    private void initialUI() {
        getBundleValue();
        binding.gvContainer.setNumColumns(2);
        data = new ArrayList<>();
        adapter = new CopyDeviceAdapter(currentActivity, data, R.layout.select_device_item);
        adapter.setCopyDeviceModel(copyDeviceIds);
        adapter.setItemClickListener((v, data, position) -> itemClickerListener.onItemClick(currentFunctionModel, data, position));
        binding.gvContainer.setAdapter(adapter);
    }

    public void setCopyDevice(HashSet<String> copydDevice) {
        copyDeviceIds = copydDevice;
        if (adapter != null) {
            adapter.setCopyDeviceModel(copydDevice);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared) {
            return;
        }
        data.clear();
        ExecutorManager.executeTask(() -> {
            CopyValueFragment2.this.searchCurrentDeviceType();
            List<DbModel> deviceList = CopyItemService.getInstance().getCopyDeviceList(currentBdzId,
                    currentFunctionModel, currentInspectionType, specialMenu.deviceWay, currentReportId);
            if (null != deviceList && !deviceList.isEmpty()) {
                data.addAll(deviceList);
                originModel.addAll(deviceList);
            }
            mHandler.sendEmptyMessage(LOAD_DATA);
        });
    }

    /**
     * 特殊巡视查询设备需要展示的方式
     */
    private void searchCurrentDeviceType() {
        specialMenu = SpecialMenuService.getInstance().findCurrentDeviceType(currentInspectionType);
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                adapter.notifyDataSetChanged();
                itemClickerListener.onItemClick(currentFunctionModel, !data.isEmpty() ? data.get(0) : null, 0);
                break;
            default:
                break;
        }
    }

    public CopyDeviceAdapter getAdapter() {
        return adapter;
    }

    public void setPosition(int position) {
        binding.gvContainer.smoothScrollToPosition(position);
    }

}
