package com.cnksi.bdzinspection.activity;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.ChangeCopyItemParentAdapter;
import com.cnksi.bdzinspection.adapter.CopyTypeAdapter;
import com.cnksi.bdzinspection.adapter.ItemClickListener;
import com.cnksi.bdzinspection.daoservice.LogsService;
import com.cnksi.bdzinspection.databinding.XsActivitySettingCopyBinding;
import com.cnksi.bdzinspection.databinding.XsAddCopyItemBinding;
import com.cnksi.bdzinspection.databinding.XsDialogInput1Binding;
import com.cnksi.bdzinspection.model.ChangeCopyItem;
import com.cnksi.bdzinspection.model.Logs;
import com.cnksi.common.Config;
import com.cnksi.common.SystemConfig;
import com.cnksi.common.base.BaseActivity;
import com.cnksi.common.daoservice.CopyItemService;
import com.cnksi.common.daoservice.CopyResultService;
import com.cnksi.common.daoservice.CopyTypeService;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.listener.OnViewClickListener;
import com.cnksi.common.model.CopyItem;
import com.cnksi.common.model.CopyResult;
import com.cnksi.common.model.CopyType;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DisplayUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.cnksi.common.Config.LOAD_DATA;

/**
 * @author han
 *         <p>
 *         该类主要是对抄录项进行修改
 */

public class SettingCopyTypeActivity extends BaseActivity implements ItemClickListener {
    private XsActivitySettingCopyBinding settingCopyBinding;
    private ChangeCopyItemParentAdapter itemParentAdapter;
    private List<CopyResult> reportResultList;
    private ArrayList<CopyItem> copyItemList;
    private List<CopyType> copyTypes;
    private List<String> typeCounts = new ArrayList<>();
    private ArrayList<CopyItem> originItems = new ArrayList<>();
    private String currentPersonId;
    private String currentUserName;

    //-------------------
    private List<CopyType> parentTypes = new ArrayList<>();
    //查询所有的抄录类型的集合
    private Map<String, CopyType> copyTypeHashMap = new HashMap<>();


    //全面巡视抄录类型及其抄录项
    private List<CopyType> parentFullTypes = new ArrayList<>();
    private Map<String, List<ChangeCopyItem>> childFullHashMap = new HashMap<>();

    //例行巡视抄录类型及其抄录项
    private List<CopyType> parentRoutineTypes = new ArrayList<>();
    private Map<String, List<ChangeCopyItem>> childRoutineMap = new HashMap<>();

    //特殊巡视抄录类型及其抄录项
    private List<CopyType> parentSpecialTypes = new ArrayList<>();
    private Map<String, List<ChangeCopyItem>> childSpecialMap = new HashMap<>();


    private String currentSelectType = InspectionType.full.name();


    private Map<String, List<ChangeCopyItem>> childTypeMap = new HashMap<>();
    private Map<String, CopyItem> originCopyItemMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingCopyBinding = DataBindingUtil.setContentView(this, R.layout.xs_activity_setting_copy);
        DisplayUtils.getInstance().init(getApplicationContext());
        initialUI();
        initialData();
    }

    private void initialUI() {
        getIntentValue();
        settingCopyBinding.tab.addTab(settingCopyBinding.tab.newTab().setText(InspectionType.full.value));
        settingCopyBinding.tab.addTab(settingCopyBinding.tab.newTab().setText(InspectionType.routine.value));
        boolean special = SystemConfig.isSpecialInspectionNeedCopy() && currentInspectionType.contains(InspectionType.special.name());
        settingCopyBinding.setSpecial(special);
        if (special) {
            settingCopyBinding.tab.addTab(settingCopyBinding.tab.newTab().setText(currentInspectionTypeName));
        }
        currentUserName = PreferencesUtils.get( Config.CURRENT_LOGIN_USER, "");
        currentPersonId = PreferencesUtils.get( Config.CURRENT_DEPARTMENT_ID, "");
        settingCopyBinding.tvTitle.setText(TextUtils.isEmpty(currentDeviceName) ? "" : currentDeviceName);
        if (itemParentAdapter == null) {
            itemParentAdapter = new ChangeCopyItemParentAdapter(mActivity, parentTypes);
        }
        itemParentAdapter.setItemClickListener(this);
        settingCopyBinding.tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().toString().equalsIgnoreCase(InspectionType.full.value.toString())) {
                    currentSelectType = InspectionType.full.name();
                    mHandler.sendEmptyMessage(LOAD_DATA);
                } else if (tab.getText().toString().equalsIgnoreCase(InspectionType.routine.value.toString())) {
                    currentSelectType = InspectionType.routine.name();
                    mHandler.sendEmptyMessage(LOAD_DATA);
                } else if (tab.getText().toString().contains(currentInspectionTypeName)) {
                    currentSelectType = currentInspectionType;
                    mHandler.sendEmptyMessage(LOAD_DATA);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initialData() {
        ExecutorManager.executeTask(() -> {
            copyTypes = CopyTypeService.getInstance().findAllCopyType();
            for (CopyType type : copyTypes) {
                if (!copyTypeHashMap.containsKey(type.key)) {
                    copyTypeHashMap.put(type.key, type);
                }
            }
            typeCounts = Arrays.asList(SettingCopyTypeActivity.this.getResources().getStringArray(R.array.XS_CopyItemCount));
            // 查询当前设备抄录项
            copyItemList = (ArrayList<CopyItem>) CopyItemService.getInstance().getDeviceALLCopyItem(currentBdzId, currentDeviceId);
//                originItems = (ArrayList<CopyItem>) copyItemList.clone();
            originItems = (ArrayList<CopyItem>) CopyItemService.getInstance().getDeviceALLCopyItem(currentBdzId, currentDeviceId);
            List<String> keys = new ArrayList<>();
            List<String> fullKeys = new ArrayList<>();
            List<String> routineKeys = new ArrayList<>();
            List<String> specialKeys = new ArrayList<>();
            for (CopyItem item : copyItemList) {
                if (item.kind.contains(InspectionType.full.name())) {
                    if (copyTypeHashMap.containsKey(item.type_key) && !fullKeys.contains(item.type_key)) {
                        parentFullTypes.add(copyTypeHashMap.get(item.type_key));
                        fullKeys.add(item.type_key);
                    }
                }
                if (item.kind.contains(InspectionType.routine.name())) {
                    if (copyTypeHashMap.containsKey(item.type_key) && !routineKeys.contains(item.type_key)) {
                        parentRoutineTypes.add(copyTypeHashMap.get(item.type_key));
                        routineKeys.add(item.type_key);
                    }
                }
                if (item.kind.contains(currentInspectionType)) {
                    if (copyTypeHashMap.containsKey(item.type_key) && !specialKeys.contains(item.type_key)) {
                        parentSpecialTypes.add(copyTypeHashMap.get(item.type_key));
                        specialKeys.add(item.type_key);
                    }
                }
            }

            for (CopyItem copyItem : originItems) {
                if (null == originCopyItemMap.get(copyItem.id)) {
                    originCopyItemMap.put(copyItem.id, copyItem);
                }
            }

            reportResultList = CopyResultService.getInstance().getResultList(currentBdzId, currentReportId, currentDeviceId, true);
            Map<String, CopyResult> reportCopyResultMap = new HashMap<>();
            if (null != reportResultList && !reportResultList.isEmpty()) {
                for (CopyResult result : reportResultList) {
                    reportCopyResultMap.put(result.item_id, result);
                }
            }
            // 历史抄录值
            List<CopyResult> historyResultList = CopyResultService.getInstance().getResultList(currentBdzId, currentReportId, currentDeviceId, false);
            Map<String, CopyResult> historyMap = new HashMap<>();
            if (null != historyResultList && !historyResultList.isEmpty()) {
                for (CopyResult historyResult : historyResultList) {
                    historyMap.put(historyResult.item_id, historyResult);
                }
            }
            CopyResult originResult = null;
            for (CopyItem item : copyItemList) {
                if (null != reportCopyResultMap.get(item.id)) {
                    originResult = reportCopyResultMap.get(item.id);
                } else if (null != historyMap.get(item.id)) {
                    originResult = historyMap.get(item.id);
                } else {
                    originResult = new CopyResult();
                }
                SettingCopyTypeActivity.this.initCopyItemData(item, originResult);
            }
            mHandler.sendEmptyMessage(LOAD_DATA);
        });
    }

    private void initCopyItemData(CopyItem item, CopyResult originResult) {
        List<ChangeCopyItem> changeItems = null;
        if (item.kind.contains(InspectionType.full.name()) && null == childFullHashMap.get(item.type_key)) {
            changeItems = new ArrayList<>();
            childFullHashMap.put(item.type_key, changeItems);
        }
        if (item.kind.contains(InspectionType.routine.name()) && null == childRoutineMap.get(item.type_key)) {
            changeItems = new ArrayList<>();
            childRoutineMap.put(item.type_key, changeItems);
        }

        if (item.kind.contains(currentInspectionType) && null == childSpecialMap.get(item.type_key)) {
            changeItems = new ArrayList<>();
            childSpecialMap.put(item.type_key, changeItems);
        }

        if ("Y".equalsIgnoreCase(item.val)) {
            ChangeCopyItem changeCopyItem = new ChangeCopyItem(originResult, item, "Y", "N", "N", "N", "N");
            if (item.kind.contains(InspectionType.full.name())) {
                childFullHashMap.get(item.type_key).add(changeCopyItem);
            }
            if (item.kind.contains(InspectionType.routine.name())) {
                childRoutineMap.get(item.type_key).add(changeCopyItem);
            }
            if (item.kind.contains(currentInspectionType)) {
                childSpecialMap.get(item.type_key).add(changeCopyItem);
            }
        }
        if ("Y".equalsIgnoreCase(item.val_a)) {
            ChangeCopyItem changeCopyItem = new ChangeCopyItem(originResult, item, "N", "Y", "N", "N", "N");
            if (item.kind.contains(InspectionType.full.name())) {
                childFullHashMap.get(item.type_key).add(changeCopyItem);
            }
            if (item.kind.contains(InspectionType.routine.name())) {
                childRoutineMap.get(item.type_key).add(changeCopyItem);
            }
            if (item.kind.contains(currentInspectionType)) {
                childSpecialMap.get(item.type_key).add(changeCopyItem);
            }
        }
        if ("Y".equalsIgnoreCase(item.val_b)) {
            ChangeCopyItem changeCopyItem = new ChangeCopyItem(originResult, item, "N", "N", "Y", "N", "N");
            if (item.kind.contains(InspectionType.full.name())) {
                childFullHashMap.get(item.type_key).add(changeCopyItem);
            }
            if (item.kind.contains(InspectionType.routine.name())) {
                childRoutineMap.get(item.type_key).add(changeCopyItem);
            }
            if (item.kind.contains(currentInspectionType)) {
                childSpecialMap.get(item.type_key).add(changeCopyItem);
            }
        }
        if ("Y".equalsIgnoreCase(item.val_c)) {
            ChangeCopyItem changeCopyItem = new ChangeCopyItem(originResult, item, "N", "N", "N", "Y", "N");
            if (item.kind.contains(InspectionType.full.name())) {
                childFullHashMap.get(item.type_key).add(changeCopyItem);
            }
            if (item.kind.contains(InspectionType.routine.name())) {
                childRoutineMap.get(item.type_key).add(changeCopyItem);
            }
            if (item.kind.contains(currentInspectionType)) {
                childSpecialMap.get(item.type_key).add(changeCopyItem);
            }
        }
        if ("Y".equalsIgnoreCase(item.val_o)) {
            ChangeCopyItem changeCopyItem = new ChangeCopyItem(originResult, item, "N", "N", "N", "N", "Y");
            if (item.kind.contains(InspectionType.full.name())) {
                childFullHashMap.get(item.type_key).add(changeCopyItem);
            }
            if (item.kind.contains(InspectionType.routine.name())) {
                childRoutineMap.get(item.type_key).add(changeCopyItem);
            }
            if (item.kind.contains(currentInspectionType)) {
                childSpecialMap.get(item.type_key).add(changeCopyItem);
            }
        }
    }


    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                if (currentSelectType.equalsIgnoreCase(InspectionType.full.name())) {
                    itemParentAdapter.setList(parentFullTypes);
                    itemParentAdapter.setALLCopyType(childFullHashMap);
                } else if (currentSelectType.equalsIgnoreCase(InspectionType.routine.name())) {
                    itemParentAdapter.setALLCopyType(childRoutineMap);
                    itemParentAdapter.setList(parentRoutineTypes);
                } else if (currentSelectType.equalsIgnoreCase(currentInspectionType)) {
                    itemParentAdapter.setALLCopyType(childSpecialMap);
                    itemParentAdapter.setList(parentSpecialTypes);
                }
                settingCopyBinding.lvCopy.setAdapter(itemParentAdapter);
//                creatCopyNameDialog();
                break;
            default:
                break;
        }
    }


    public void onClickEvent(View view) {
        int i = view.getId();
        if (i == R.id.ibtn_cancel) {
            this.finish();

        } else if (i == R.id.save_data) {
            saveData();
            this.finish();

        } else if (i == R.id.btn_add_copy) {
            creatAddItemDialog();

        } else {
        }
    }

    private Dialog copyNameDialog;
    private XsDialogInput1Binding input1Binding;

    private void creatCopyNameDialog() {
        int dialogWidth = ScreenUtils.getScreenWidth(mActivity) * 7 / 9;
        input1Binding = XsDialogInput1Binding.inflate(getLayoutInflater());
        input1Binding.tvDialogTitle.setText("确定抄录项名称");
        copyNameDialog = DialogUtils.createDialog(mActivity, input1Binding.getRoot(), dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        input1Binding.btnCancel.setOnClickListener(v -> copyNameDialog.dismiss());
        input1Binding.btnSure.setOnClickListener(v -> {
            String updateName = TextUtils.isEmpty(input1Binding.edit.getText().toString().trim()) ? clickCopyType.name : input1Binding.edit.getText().toString().trim();
            CopyItem item1 = new CopyItem(currentBdzId, currentDeviceName, currentDeviceId, updateName, clickCopyType.key, true, currentInspectionType);
            SettingCopyTypeActivity.this.add1Item(clickCopyType, item1);
            itemParentAdapter.notifyDataSetChanged();
            copyNameDialog.dismiss();
        });
    }


    private Dialog dialog;
    private CopyTypeAdapter typeAdapter;
    private CopyTypeAdapter typeCountAdapter;
    private XsAddCopyItemBinding itemBinding;
    private int typeSelectPosition;
    private int countSelectPosition;
    private String selectKind = "";

    /**
     * 弹出新增抄录项对话框
     */
    private void creatAddItemDialog() {
        int dialogWidth = ScreenUtils.getScreenWidth(mActivity) * 7 / 9;
        itemBinding = XsAddCopyItemBinding.inflate(LayoutInflater.from(getApplicationContext()));
        boolean specialNeedCopy = currentInspectionType.contains(InspectionType.special.name()) && SystemConfig.isSpecialInspectionNeedCopy();
        if (specialNeedCopy) {
            itemBinding.radioSpecial.setVisibility(View.VISIBLE);
            itemBinding.radioSpecial.setText(currentInspectionTypeName);
        }
        itemBinding.tvDevieName.append(TextUtils.isEmpty(currentDeviceName) ? "" : currentDeviceName);
        itemBinding.tvSpaceName.append(TextUtils.isEmpty(currentSpacingName) ? "" : currentSpacingName);
        if (null == typeAdapter) {
            typeAdapter = new CopyTypeAdapter(mActivity, copyTypes, R.layout.xs_copy_type, "0");
        }
        itemBinding.typeSpinner.setAdapter(typeAdapter);
        if (null == typeCountAdapter) {
            typeCountAdapter = new CopyTypeAdapter(mActivity, typeCounts, R.layout.xs_copy_type, "1");
        }
        itemBinding.countSipnner.setAdapter(typeCountAdapter);
        dialog = DialogUtils.createDialog(mActivity, itemBinding.getRoot(), dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();
        itemBinding.btnSave.setOnClickListener(v -> {
            if (specialNeedCopy) {
                if (!itemBinding.radioRoutine.isChecked() && !itemBinding.radioFull.isChecked() && !itemBinding.radioSpecial.isChecked()) {
                    ToastUtils.showMessage( "请选择抄录适用的巡检类型");
                    return;
                }
            } else {
                if (!itemBinding.radioRoutine.isChecked() && !itemBinding.radioFull.isChecked()) {
                    ToastUtils.showMessage( "请选择抄录适用的巡检类型");
                    return;
                }
            }
            CopyType copyType = ((CopyType) itemBinding.typeSpinner.getSelectedItem());
            String updateCopyName = TextUtils.isEmpty(itemBinding.etUpdateName.getText().toString()) ? copyType.name : itemBinding.etUpdateName.getText().toString();
            if (itemBinding.radioFull.isChecked()) {
                selectKind = "full,";
            }
            if (itemBinding.radioRoutine.isChecked()) {
                selectKind = selectKind + "routine,";
            }

            if (specialNeedCopy && itemBinding.radioSpecial.isChecked()) {
                selectKind = selectKind + currentInspectionType;
            }

            if ("单相".equalsIgnoreCase(itemBinding.countSipnner.getSelectedItem().toString())) {
                CopyItem item = new CopyItem(currentBdzId, currentDeviceName, currentDeviceId, updateCopyName, copyType.key, true, selectKind);
                add1Item(copyType, item);
            } else {
                CopyItem item = new CopyItem(currentBdzId, currentDeviceName, currentDeviceId, updateCopyName, copyType.key, false, selectKind);
                add3Item(copyType, item);
            }
            mHandler.sendEmptyMessage(LOAD_DATA);
//                itemParentAdapter.notifyDataSetChanged();
            dialog.dismiss();
        });
        itemBinding.btnCancel.setOnClickListener(v -> dialog.dismiss());
        itemBinding.typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = view.findViewById(R.id.tv_type_name);
                textView.setTextColor(parent.getContext().getResources().getColor(R.color.xs_global_base_color));
                typeSelectPosition = position;
                typeAdapter.setPosition(typeSelectPosition);
                itemBinding.etUpdateName.setText(textView.getText().toString().trim());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        itemBinding.countSipnner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = view.findViewById(R.id.tv_type_name);
                textView.setTextColor(parent.getContext().getResources().getColor(R.color.xs_global_base_color));
                countSelectPosition = position;
                typeCountAdapter.setPosition(countSelectPosition);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /**
     * 三项抄录转为一项抄录或者是新增一项
     *
     * @param copyType 抄录大类
     * @param item     抄录小类
     */
    private void add1Item(CopyType copyType, CopyItem item) {
        ChangeCopyItem copyItem = new ChangeCopyItem(new CopyResult(), item, "Y", "N", "N", "N", "N", -1);
        if (childFullHashMap.containsKey(copyType.key) && !childFullHashMap.get(copyType.key).contains(copyItem) && (TextUtils.isEmpty(selectKind) || selectKind.contains("full"))) {
            childFullHashMap.get(copyType.key).add(copyItem);
            if (!parentFullTypes.contains(copyType)) {
                parentFullTypes.add(copyType);
            }
        } else if (selectKind.contains("full")) {
            checkItemExit(copyType, copyItem);
        }
        if (childRoutineMap.containsKey(copyType.key) && !childRoutineMap.get(copyType.key).contains(copyItem) && (TextUtils.isEmpty(selectKind) || selectKind.contains("routine"))) {
            childRoutineMap.get(copyType.key).add(copyItem);
            if (!parentRoutineTypes.contains(copyType)) {
                parentRoutineTypes.add(copyType);
            }
        } else if (selectKind.contains("routine")) {
            checkItemExit(copyType, copyItem);
        }
        if (childSpecialMap.containsKey(copyType.key) && !childSpecialMap.get(copyType.key).contains(copyItem) && (TextUtils.isEmpty(selectKind) || (selectKind.contains(currentInspectionType)))) {
            childSpecialMap.get(copyType.key).add(copyItem);
            if (!parentSpecialTypes.contains(copyType)) {
                parentSpecialTypes.add(copyType);
            }
        } else if (selectKind.contains(currentInspectionType)) {
            checkItemExit(copyType, copyItem);
        }
        selectKind = "";
    }

    /**
     * 一项抄录转为三项抄录或者是新增三项
     *
     * @param copyType 抄录大类
     * @param copyItem 抄录小类
     */
    public void checkItemExit(CopyType copyType, ChangeCopyItem copyItem) {
        if (selectKind.contains(InspectionType.routine.name())) {
            if (null == childRoutineMap.get(copyType.key) || !childRoutineMap.get(copyType.key).contains(copyItem)) {
                List<ChangeCopyItem> newCopyItems = new ArrayList<>();
                newCopyItems.add(copyItem);
                childRoutineMap.put(copyType.key, newCopyItems);
                if (!parentRoutineTypes.contains(copyType)) {
                    parentRoutineTypes.add(copyType);
                }
            }
        }
        if (selectKind.contains(InspectionType.full.name())) {
            if (null == childFullHashMap.get(copyType.key) || !childFullHashMap.get(copyType.key).contains(copyItem)) {
                List<ChangeCopyItem> newCopyItems = new ArrayList<>();
                newCopyItems.add(copyItem);
                childFullHashMap.put(copyType.key, newCopyItems);
                if (!parentFullTypes.contains(copyType)) {
                    parentFullTypes.add(copyType);
                }
            }
        }
        if (selectKind.contains(currentInspectionType)) {
            if (null == childSpecialMap.get(copyType.key) || !childSpecialMap.get(copyType.key).contains(copyItem)) {
                List<ChangeCopyItem> newCopyItems = new ArrayList<>();
                newCopyItems.add(copyItem);
                childSpecialMap.put(copyType.key, newCopyItems);
                if (!parentSpecialTypes.contains(copyType)) {
                    parentSpecialTypes.add(copyType);
                }
            }
        }
    }


    private void add3Item(CopyType copyType, CopyItem item) {
        ChangeCopyItem copyItem = new ChangeCopyItem(new CopyResult(), item, "N", "Y", "N", "N", "N", -1);
        ChangeCopyItem copyItem1 = new ChangeCopyItem(new CopyResult(), item, "N", "N", "Y", "N", "N", -1);
        ChangeCopyItem copyItem2 = new ChangeCopyItem(new CopyResult(), item, "N", "N", "N", "Y", "N", -1);
        if (childFullHashMap.containsKey(copyType.key) && !childFullHashMap.get(copyType.key).contains(copyItem) && (TextUtils.isEmpty(selectKind) || selectKind.contains("full"))) {
            childFullHashMap.get(copyType.key).add(copyItem);
            childFullHashMap.get(copyType.key).add(copyItem1);
            childFullHashMap.get(copyType.key).add(copyItem2);
            if (!parentFullTypes.contains(copyType)) {
                parentFullTypes.add(copyType);
            }
        } else if (selectKind.contains("full")) {
            check3ItemExit(copyType, copyItem, copyItem1, copyItem2);
        }
        if (childRoutineMap.containsKey(copyType.key) && !childRoutineMap.get(copyType.key).contains(copyItem) && (TextUtils.isEmpty(selectKind) || selectKind.contains("routine"))) {
            childRoutineMap.get(copyType.key).add(copyItem);
            childRoutineMap.get(copyType.key).add(copyItem1);
            childRoutineMap.get(copyType.key).add(copyItem2);
            if (!parentRoutineTypes.contains(copyType)) {
                parentRoutineTypes.add(copyType);
            }
        } else if (selectKind.contains("routine")) {
            check3ItemExit(copyType, copyItem, copyItem1, copyItem2);
        }

        if (childSpecialMap.containsKey(copyType.key) && !childSpecialMap.get(copyType.key).contains(copyItem) && (TextUtils.isEmpty(selectKind) || selectKind.contains(currentInspectionType))) {
            childSpecialMap.get(copyType.key).add(copyItem);
            childSpecialMap.get(copyType.key).add(copyItem1);
            childSpecialMap.get(copyType.key).add(copyItem2);
            if (!parentSpecialTypes.contains(copyType)) {
                parentSpecialTypes.add(copyType);
            }
        } else if (selectKind.contains(currentInspectionType)) {
            check3ItemExit(copyType, copyItem, copyItem1, copyItem2);
        }

        selectKind = "";

    }

    public void check3ItemExit(CopyType copyType, ChangeCopyItem copyItem, ChangeCopyItem copyItem1, ChangeCopyItem copyItem2) {
        if (selectKind.contains(InspectionType.routine.name())) {
            if (null == childRoutineMap.get(copyType.key) || !childRoutineMap.get(copyType.key).contains(copyItem)) {
                List<ChangeCopyItem> newCopyItems = new ArrayList<>();
                newCopyItems.add(copyItem);
                newCopyItems.add(copyItem1);
                newCopyItems.add(copyItem2);
                childRoutineMap.put(copyType.key, newCopyItems);
                if (!parentRoutineTypes.contains(copyType)) {
                    parentRoutineTypes.add(copyType);
                }
            }
        }
        if (selectKind.contains(InspectionType.full.name())) {
            if (null == childFullHashMap.get(copyType.key) || !childFullHashMap.get(copyType.key).contains(copyItem)) {
                List<ChangeCopyItem> newCopyItems = new ArrayList<>();
                newCopyItems.add(copyItem);
                newCopyItems.add(copyItem1);
                newCopyItems.add(copyItem2);
                childFullHashMap.put(copyType.key, newCopyItems);
                if (!parentFullTypes.contains(copyType)) {
                    parentFullTypes.add(copyType);
                }
            }
        }
        if (selectKind.contains(currentInspectionType)) {
            if (null == childSpecialMap.get(copyType.key) || !childSpecialMap.get(copyType.key).contains(copyItem)) {
                List<ChangeCopyItem> newCopyItems = new ArrayList<>();
                newCopyItems.add(copyItem);
                newCopyItems.add(copyItem1);
                newCopyItems.add(copyItem2);
                childSpecialMap.put(copyType.key, newCopyItems);
                if (!parentSpecialTypes.contains(copyType)) {
                    parentSpecialTypes.add(copyType);
                }
            }
        }
    }

    private ArrayList<Logs> increaseLogs = new ArrayList<>();

    private void saveData() {
        Logs logs = null;
        for (CopyItem copyItem : updateCopyItems) {
            if (copyItem.dlt.equals(Config.DELETED)) {
                logs = new Logs();
                logs.setCurrentMessage(currentDeviceId, currentDeviceName, currentPersonId, currentUserName, "delete", "copy_item");
                logs.content = "删除抄录项";
            }
            increaseLogs.add(logs);
        }
        if (!parentRoutineTypes.isEmpty()) {
            saveCopyItem(parentRoutineTypes, childRoutineMap);
        }
        if (!parentFullTypes.isEmpty()) {
            saveCopyItem(parentFullTypes, childFullHashMap);
        }
        if (!parentSpecialTypes.isEmpty()) {
            saveCopyItem(parentSpecialTypes, childSpecialMap);
        }
        if (!increaseLogs.isEmpty()) {
            LogsService.getInstance().saveOrUpdateQuiet(increaseLogs);
            CopyItemService.getInstance().saveUpdate(new ArrayList<>(updateCopyItems), currentReportId);
        }

    }

    /**
     * 分全面巡视和例行巡视进行保存
     *
     * @param childRoutineMap    需要保存的抄录项集合
     * @param parentRoutineTypes 对应抄录类型的大项集合
     */
    private void saveCopyItem(List<CopyType> parentRoutineTypes, Map<String, List<ChangeCopyItem>> childRoutineMap) {
        Logs logs = null;
        parentTypes = parentRoutineTypes;
        childTypeMap = childRoutineMap;
        for (CopyType copyType : parentTypes) {
            List<ChangeCopyItem> changeCopyItems = childTypeMap.get(copyType.key);
            for (ChangeCopyItem changeCopyItem : changeCopyItems) {
                if (changeCopyItem.getVersion() == -1 && changeCopyItem.getItem().version == -1) {
                    changeCopyItem.getItem().version += 1;
                    logs = new Logs();
                    logs.setCurrentMessage(currentDeviceId, currentDeviceName, currentPersonId, currentUserName, "add", "copy_item");
                    logs.content = "增加抄录项";
                    increaseLogs.add(logs);
                    updateCopyItems.add(changeCopyItem.getItem());
                } else {
                    if (updateCopyItems.contains(changeCopyItem.getItem())) {
                        continue;
                    }
                    CopyItem item = originCopyItemMap.get(changeCopyItem.getItem().id);
                    Logs logs1 = new Logs();
                    logs1.setCurrentMessage(currentDeviceId, currentDeviceName, currentPersonId, currentUserName, "update", "copy_item");
                    if (!changeCopyItem.getItem().val.equalsIgnoreCase(item.val)) {
                        logs1.content = ("Y".equalsIgnoreCase(changeCopyItem.getItem().val) ? "增加单项抄录" : "去除单项抄录") + "\n";
                    } else if (!changeCopyItem.getItem().val_a.equalsIgnoreCase(item.val_a)) {
                        logs1.content = ("Y".equalsIgnoreCase(changeCopyItem.getItem().val_a) ? "增加A项抄录" : "去除A项抄录") + "\n";
                    } else if (!changeCopyItem.getItem().val_b.equalsIgnoreCase(item.val_b)) {
                        logs1.content = ("Y".equalsIgnoreCase(changeCopyItem.getItem().val_b) ? "增加B项抄录" : "去除B项抄录") + "\n";
                    } else if (!changeCopyItem.getItem().val_c.equalsIgnoreCase(item.val_c)) {
                        logs1.content = ("Y".equalsIgnoreCase(changeCopyItem.getItem().val_c) ? "增加C项抄录" : "去除C项抄录") + "\n";
                    } else if (!changeCopyItem.getItem().val_o.equalsIgnoreCase(item.val_o)) {
                        logs1.content = ("Y".equalsIgnoreCase(changeCopyItem.getItem().val_o) ? "增加O项抄录" : "去除O项抄录") + "\n";
                    }
                    if (!TextUtils.isEmpty(logs1.content)) {
                        changeCopyItem.getItem().version += 1;
                        changeCopyItem.getItem().isUpLoad = "N";
                        increaseLogs.add(logs1);
                        updateCopyItems.add(changeCopyItem.getItem());
                    }
                }
            }
        }
    }

    //需要更新的item，主要是征对原本数据库有的但是删除后的需要记录的item集合
    private Set<CopyItem> updateCopyItems = new HashSet<>();
    private CopyType clickCopyType;
    String type = "";

    @Override
    public void onClick(View v, final Object data, final int position) {
        int i = v.getId();
        if (i == R.id.add_item) {//                clickCopyType = parentTypes.get(position);
//                input1Binding.edit.setText(clickCopyType.name);
//                copyNameDialog.show();

        } else if (i == R.id.item_3to1) {
            CopyType copyType = null;
            ArrayList<ChangeCopyItem> copyItems = null;
            List<ChangeCopyItem> fullItems;
            List<ChangeCopyItem> routineItems;
            List<ChangeCopyItem> specaialItems;
            if (currentSelectType.equalsIgnoreCase(InspectionType.full.name())) {
                copyType = parentFullTypes.get(position);
                copyItems = (ArrayList<ChangeCopyItem>) ((ArrayList<ChangeCopyItem>) childFullHashMap.get(copyType.key)).clone();
            } else if (currentSelectType.equalsIgnoreCase(InspectionType.routine.name())) {
                copyType = parentRoutineTypes.get(position);
                copyItems = (ArrayList<ChangeCopyItem>) ((ArrayList<ChangeCopyItem>) childRoutineMap.get(copyType.key)).clone();
            } else if (currentSelectType.equalsIgnoreCase(currentInspectionType)) {
                copyType = parentSpecialTypes.get(position);
                copyItems = (ArrayList<ChangeCopyItem>) ((ArrayList<ChangeCopyItem>) childSpecialMap.get(copyType.key)).clone();
            }
            fullItems = childFullHashMap.get(copyType.key);
            routineItems = childRoutineMap.get(copyType.key);
            specaialItems = childSpecialMap.get(copyType.key);
            if (copyItems.size() != 1 && copyItems.size() != 3) {
                ToastUtils.showMessage( "当前只能3项转1项或者1项转3项");
                return;
            }
            for (ChangeCopyItem changeCopyItem : copyItems) {
                type = changeCopyItem.getItem().kind;
                changeCopyItem.getItem().isUpLoad = "N";
                changeCopyItem.getItem().dlt = Config.DELETED;
                if (originCopyItemMap.containsKey(changeCopyItem.getItem().id) && !updateCopyItems.contains(changeCopyItem.getItem())) {
                    updateCopyItems.add(changeCopyItem.getItem());
                }
                if (null != fullItems) {
                    fullItems.clear();
                }
                if (null != routineItems) {
                    routineItems.clear();
                }
            }
            if (copyItems.size() == 1) {
                CopyItem item = new CopyItem(currentBdzId, currentDeviceName, currentDeviceId, copyType.name, copyType.key, false, type);
                add3Item(copyType, item);
            } else {
                CopyItem item = new CopyItem(currentBdzId, currentDeviceName, currentDeviceId, copyType.name, copyType.key, true, type);
                add1Item(copyType, item);
            }
            itemParentAdapter.notifyDataSetChanged();

        } else if (i == R.id.item_delete) {
            DialogUtils.showSureTipsDialog(mActivity, null, "提示", "确定删除该项？", "是", "否", new OnViewClickListener() {
                @Override
                public void onClick(View v) {
                    super.onClick(v);
                    ChangeCopyItem changeCopyItem = (ChangeCopyItem) data;
                    String typeKey = changeCopyItem.getItem().type_key;
                    List<ChangeCopyItem> fullItems = childFullHashMap.get(changeCopyItem.getItem().type_key);
                    List<ChangeCopyItem> routineItems = childRoutineMap.get(changeCopyItem.getItem().type_key);
                    List<ChangeCopyItem> specialItems = childSpecialMap.get(changeCopyItem.getItem().type_key);
                    if (changeCopyItem.setCopyItemVal(changeCopyItem)) {
                        updateCopyItems.add(changeCopyItem.getItem());
                    }
                    if (changeCopyItem.getItem().version != -1) {
                        changeCopyItem.getItem().version += 1;
                    }
                    changeCopyItem.getItem().isUpLoad = "N";
                    if (null != childFullHashMap.get(changeCopyItem.getItem().type_key) && fullItems.contains(changeCopyItem)) {
                        fullItems.remove(changeCopyItem);
                    }
                    if (null != childRoutineMap.get(changeCopyItem.getItem().type_key) && routineItems.contains(changeCopyItem)) {
                        routineItems.remove(changeCopyItem);
                    }
                    if (null != childSpecialMap.get(changeCopyItem.getItem().type_key) && specialItems.contains(changeCopyItem)) {
                        specialItems.remove(changeCopyItem);
                    }
                    if (null != fullItems && fullItems.size() == 0) {
                        parentFullTypes.remove(copyTypeHashMap.get(typeKey));
                    }
                    if (null != routineItems && routineItems.size() == 0) {
                        parentRoutineTypes.remove(copyTypeHashMap.get(typeKey));
                    }
                    if (null != specialItems && specialItems.size() == 0) {
                        parentSpecialTypes.remove(copyTypeHashMap.get(typeKey));
                    }


                    itemParentAdapter.notifyDataSetChanged();
                }
            });

        } else {
        }
    }

}
