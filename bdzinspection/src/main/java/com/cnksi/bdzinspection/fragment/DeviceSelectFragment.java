package com.cnksi.bdzinspection.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.DeviceAdapter;
import com.cnksi.bdzinspection.adapter.DeviceSelectAdapter;
import com.cnksi.bdzinspection.adapter.ViewHolder;
import com.cnksi.bdzinspection.daoservice.DeviceService;
import com.cnksi.bdzinspection.daoservice.SpacingService;
import com.cnksi.bdzinspection.daoservice.StandardSpecialService;
import com.cnksi.bdzinspection.model.SpacingGroup;
import com.cnksi.bdzinspection.model.StandardSpecial;
import com.cnksi.bdzinspection.model.tree.SpaceGroupItem;
import com.cnksi.bdzinspection.model.tree.SpaceItem;
import com.cnksi.bdzinspection.view.keyboard.QWERKeyBoardUtils;

import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 设备选择设备列表及键盘
 *
 * @author lyndon
 */
public class DeviceSelectFragment extends BaseFragment implements QWERKeyBoardUtils.keyWordChangeListener {

    private ViewHolder rootHolder;


    private DeviceClickListener deviceClickListener;// 设备选择事件
    private SpaceClickListener mSpaceClickListener;// 设备选择事件

    private RecyclerView recyclerView;

    private DeviceSelectAdapter adapter;

    private List<MultiItemEntity> data;

    private Map<String, List<DbModel>> selectSpacingDeviceMap;// 选中设备集合

    private LinkedHashMap<String, SpaceGroupItem> spaceGroupMap;
    private QWERKeyBoardUtils qwerKeyBoardUtils;
    private Set<String> bigTypes;


    public void setDeviceClickListener(DeviceClickListener deviceClickListener) {
        this.deviceClickListener = deviceClickListener;
    }
    public void setSpaceClickListener(SpaceClickListener spaceClickListener) {
        this.mSpaceClickListener = spaceClickListener;
    }

    public void setSelectSpacingDeviceMap(Map<String, List<DbModel>> selectSpacingDeviceMap) {
        this.selectSpacingDeviceMap = selectSpacingDeviceMap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootHolder = new ViewHolder(currentActivity, container, R.layout.xs_fragment_expadable_list_1, false);
        initUI();
        initSpacingGroup();
        lazyLoad();
        return rootHolder.getRootView();
    }

    private void initUI() {
        getBundleValue();
        qwerKeyBoardUtils = new QWERKeyBoardUtils(currentActivity);
        qwerKeyBoardUtils.init(rootHolder.getView(R.id.ll_root_container), this);

        data = new ArrayList<>();
        adapter = new DeviceSelectAdapter(currentActivity, data);
        SparseArray<Integer> layoutMap = new SparseArray<>();
        layoutMap.put(1, R.layout.xs_group_item);
        layoutMap.put(2, R.layout.xs_expandable_gridview_child_item);

        adapter.setShowOnly(false);

        // 设置设备点击事件，传递到activity处理，详细见activity中的onDeviceClick方法
        adapter.setDeviceClickListener((v, dbModel, position) -> deviceClickListener.onDeviceClick(v, dbModel, position));
        adapter.setGroupItemClickListenner((v, spaceItem, position) -> mSpaceClickListener.onSpaceClick(v, spaceItem, position));
        adapter.setSelectSpacingDeviceMap(selectSpacingDeviceMap);
        adapter.setCurrentInspectionType(currentInspectionType);
        recyclerView = rootHolder.getView(R.id.elv_container);
        final GridLayoutManager manager = new GridLayoutManager(currentActivity, "second".equals(currentFunctionModel) ? 2 : 3);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.getItemViewType(position) == DeviceAdapter.DEVICE_ITEM ? 1 : manager.getSpanCount();
            }
        });
        adapter.bindToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(manager);
        isPrepared = true;

    }

    private void initSpacingGroup() {
        if ("second".equals(currentFunctionModel) && spaceGroupMap == null) {
            List<SpacingGroup> spacingGroups = SpacingService.getInstance().findSpacingGroup(currentBdzId);
            spaceGroupMap = new LinkedHashMap<>();
            for (SpacingGroup spacingGroup : spacingGroups) {
                spaceGroupMap.put(spacingGroup.id, new SpaceGroupItem(spacingGroup));
            }
            spaceGroupMap.put(null, new SpaceGroupItem(new SpacingGroup("未拆分小室")));
        }
    }

    @Override
    protected void getBundleValue() {
        super.getBundleValue();
        bigTypes = new HashSet<>();
        List<StandardSpecial> list = StandardSpecialService.getInstance().getStandardSpecial(currentInspectionType);
        for (StandardSpecial stand : list) {
            bigTypes.add(stand.bigid);
        }
    }

    private void searChData(String keyWord) {
        adapter.setKeyWord(keyWord);
        data.clear();
        adapter.notifyDataSetChanged();
        // 根据特殊巡检类型查询设备大类
        // 根据设备大类查询设备
        // 根据设备查询间隔
        List<DbModel> deviceList;
        if (currentInspectionType.contains("special_manual"))
            deviceList = DeviceService.getInstance().findSpaceDeviceByKeyWord(currentBdzId, keyWord, currentFunctionModel, bigTypes.toArray(new String[]{}));
        else
            deviceList = DeviceService.getInstance().findAllDevice(currentBdzId, keyWord, currentFunctionModel);
        LinkedHashMap<String, List<DbModel>> spacingDeviceMap = new LinkedHashMap<>();
        if (null != deviceList && !deviceList.isEmpty()) {
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

            if ("second".equals(currentFunctionModel)) {
                Functions.buildTreeData(data, spaceGroupMap);
            }
        }
        if (TextUtils.isEmpty(keyWord))
            adapter.expand(0);
        else if (!data.isEmpty()) {
            adapter.expandAll();
        } else adapter.notifyDataSetChanged();
    }

    @Override
    protected void lazyLoad() {
        if (isPrepared && isVisible && isFirstLoad) {
            // 根据特殊巡检类型查询设备大类
            // 根据设备大类查询设备
            // 根据设备查询间隔
            searChData("");
        }
    }

    public void notifyDataChanged() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onChange(View v, String oldKey, String newKey) {
        searChData(newKey);
    }


    public interface DeviceClickListener {
        void onDeviceClick(View view, DbModel device, int position);
    }

    public interface SpaceClickListener {
        void onSpaceClick(View view, SpaceItem device, int position);
    }

    public List<MultiItemEntity> getAllData() {
        return data;
    }

    public String getFunctionModel() {
        return currentFunctionModel;
    }
}
