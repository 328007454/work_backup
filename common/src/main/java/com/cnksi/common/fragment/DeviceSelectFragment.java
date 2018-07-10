package com.cnksi.common.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.common.Config;
import com.cnksi.common.R;
import com.cnksi.common.activity.DeviceSelectActivity;
import com.cnksi.common.adapter.DeviceSelectAdapter;
import com.cnksi.common.daoservice.DeviceService;
import com.cnksi.common.daoservice.SpacingGroupService;
import com.cnksi.common.databinding.CommonRecyclerViewBinding;
import com.cnksi.common.enmu.PMSDeviceType;
import com.cnksi.common.model.SpacingGroup;
import com.cnksi.common.model.vo.SpaceGroupItem;
import com.cnksi.common.model.vo.SpaceItem;
import com.cnksi.common.utils.Functions;
import com.cnksi.common.utils.QWERKeyBoardUtils;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.fragment.BaseCoreFragment;

import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.cnksi.common.model.vo.SpaceGroupItem.DEVICE_ITEM;

/**
 * @author Wastrel
 */
public class DeviceSelectFragment extends BaseCoreFragment {

    CommonRecyclerViewBinding binding;
    QWERKeyBoardUtils qwerKeyBoardUtils;
    DeviceSelectActivity hostActivity;
    String currentType;
    List<DbModel> allDevice;
    boolean initSuccess = false;
    DeviceSelectAdapter adapter;
    String searchKey = "";
    LinkedHashMap<String, SpaceGroupItem> spaceGroupMap;
    DeviceSelectActivity.SelectConfig config;

    @Override
    public int getFragmentLayout() {
        return R.layout.common_recycler_view;
    }

    @Override
    protected void initUI() {
        currentType = getArguments().getString(Config.CURRENT_FUNCTION_MODEL);
        hostActivity = (DeviceSelectActivity) mActivity;
        config = hostActivity.getSelectConfig();
        binding = (CommonRecyclerViewBinding) fragmentDataBinding;
        qwerKeyBoardUtils = new QWERKeyBoardUtils(mActivity);
        qwerKeyBoardUtils.initOtherSerchView(binding.llRootContainer, (v, oldKey, newKey) -> {
            searchKey = newKey;
            search();
        });
        adapter = new DeviceSelectAdapter(mActivity, null, PMSDeviceType.second.equals(currentType), config);
        adapter.setShowOnly(false);
        // 设置设备点击事件，传递到activity处理，详细见activity中的onDeviceClick方法
        final GridLayoutManager manager = new GridLayoutManager(mActivity, PMSDeviceType.second.equals(currentType) ? 2 : 3);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.getItemViewType(position) == DEVICE_ITEM ? 1 : manager.getSpanCount();
            }
        });
        if (!config.isMultSelect) {
            adapter.setDeviceClickListener((v, data, position) -> {
                if (data instanceof SpaceItem) {
                    hostActivity.singleSelect(((SpaceItem) data).spacing, true);
                } else if (data instanceof DbModel) {
                    hostActivity.singleSelect((DbModel) data, false);
                }
            });
        }
        adapter.bindToRecyclerView(binding.elvContainer);
        binding.elvContainer.setLayoutManager(manager);
        isPrepared = true;
        super.initUI();
    }


    @Override
    protected void lazyLoad() {
        ExecutorManager.executeTask(() -> {
            allDevice = DeviceService.getInstance().findSpaceDeviceByType(config.bdzId, currentType, config.deviceBigType, config.filterSql);
            if (allDevice == null) {
                allDevice = new ArrayList<>();
            }
            LinkedHashMap<String, SpaceGroupItem> spaceGroupMapTemp;
            if (PMSDeviceType.second.equals(currentType)) {
                spaceGroupMapTemp = new LinkedHashMap<>();
                List<SpacingGroup> spacingGroups = SpacingGroupService.getInstance().findSpacingGroup(config.bdzId);
                for (SpacingGroup spacingGroup : spacingGroups) {
                    spaceGroupMapTemp.put(spacingGroup.id, new SpaceGroupItem(spacingGroup));
                }
                spaceGroupMapTemp.put(null, new SpaceGroupItem(new SpacingGroup("未拆分小室")));
            } else {
                spaceGroupMapTemp = null;
            }
            ExecutorManager.runOnUIThread(() -> {
                if (!initSuccess) {
                    initSuccess = true;
                    spaceGroupMap = spaceGroupMapTemp;
                    search();
                }
            });
        });
    }

    private void search() {
        if (initSuccess) {
            List<DbModel> deviceList = new ArrayList<>();
            if (TextUtils.isEmpty(searchKey)) {
                deviceList.addAll(allDevice);
            } else {
                for (DbModel model : allDevice) {
                    String s = model.getString("search_key");
                    if (s.contains(searchKey)) {
                        deviceList.add(model);
                    }
                }
            }
            List<MultiItemEntity> data = new ArrayList<>();
            LinkedHashMap<String, List<DbModel>> spacingDeviceMap = new LinkedHashMap<>();
            if (!deviceList.isEmpty()) {
                for (DbModel dbModel : deviceList) {
                    String spacingId = dbModel.getString("spid");
                    if (!spacingDeviceMap.keySet().contains(spacingId)) {
                        List<DbModel> treeNodeList = new ArrayList<>();
                        treeNodeList.add(dbModel);
                        spacingDeviceMap.put(spacingId, treeNodeList);
                    } else {
                        spacingDeviceMap.get(spacingId).add(dbModel);
                    }
                }
                //
                for (Map.Entry<String, List<DbModel>> entry : spacingDeviceMap.entrySet()) {
                    SpaceItem parentNode = new SpaceItem(entry.getValue().get(0));
                    parentNode.addAll(entry.getValue());
                    data.add(parentNode);
                }

                if ("second".equals(currentType)) {
                    Functions.buildSpaceTreeData(data, spaceGroupMap);
                }
            }
            adapter.setNewData(data);
            adapter.setKeyWord(searchKey);
            if (!data.isEmpty()) {
                if (TextUtils.isEmpty(searchKey)) {
                    adapter.expand(0);
                } else {
                    adapter.expandAll();
                }
            }
        }
    }

    public Map<String, HashSet<DbModel>> getSelect() {
        if (adapter != null) {
            return adapter.getSelectSpacingDeviceMap();
        } else {
            return null;
        }
    }

}