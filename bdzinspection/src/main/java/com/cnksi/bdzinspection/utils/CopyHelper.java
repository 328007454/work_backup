package com.cnksi.bdzinspection.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.cnksi.bdloc.DistanceUtil;
import com.cnksi.bdloc.LatLng;
import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.ViewHolder;
import com.cnksi.bdzinspection.daoservice.CopyItemService;
import com.cnksi.bdzinspection.daoservice.CopyResultService;
import com.cnksi.bdzinspection.daoservice.CopyTypeService;
import com.cnksi.bdzinspection.daoservice.PlacedDeviceService;
import com.cnksi.bdzinspection.daoservice.PlacedService;
import com.cnksi.bdzinspection.inter.CopyItemLongClickListener;
import com.cnksi.bdzinspection.inter.ItemClickListener;
import com.cnksi.bdzinspection.model.CopyItem;
import com.cnksi.bdzinspection.model.CopyResult;
import com.cnksi.bdzinspection.model.Placed;
import com.cnksi.bdzinspection.model.PlacedDevice;
import com.cnksi.bdzinspection.model.TreeNode;
import com.cnksi.common.Config;
import com.cnksi.common.SystemConfig;
import com.cnksi.common.model.Device;
import com.cnksi.common.model.Spacing;
import com.cnksi.common.utils.CommonUtils;
import com.cnksi.core.utils.GPSUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/2/1 11:07
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class CopyHelper {
    Activity activity;
    Map<String, String> copyType;
    String currentReportId;
    String currentBdzId;
    String currentInspectionType;
    List<CopyResult> reportResultList;
    HashSet<String> copyDeviceIdList;
    public DbModel device;
    List<EditText> requestEdtits = new ArrayList<>();
    List<CopyItem> copyItems = new ArrayList<>();
    private PlacedDevice placedDevice;
    private Placed placed;
    private boolean saveCurrentData;
    private Map<String, CopyResult> copyResultMap;
    private CopyViewUtil.KeyBordListener keyBordListener;
    private ItemClickListener<CopyItem> itemClickListener;
    private CopyItemLongClickListener<CopyResult> itemLongClickListener;

    public CopyHelper(Activity activity, String reportId, String currentBdzId, String inspectionType) {
        this.activity = activity;
        this.currentReportId = reportId;
        this.currentBdzId = currentBdzId;
        this.currentInspectionType = inspectionType;
        copyType = CopyTypeService.getInstance().getAllCopyType();
        copyDeviceIdList = CopyResultService.getInstance().getCopyDeviceIdList(reportId, currentInspectionType);
    }

    public Map<String, CopyResult> getCopyResultMap() {
        return copyResultMap;
    }


    public HashSet<String> getCopyDeviceIdList() {
        return copyDeviceIdList;
    }

    public void setSaveCurrentData(boolean saveCurrentData) {
        this.saveCurrentData = saveCurrentData;
    }

    public void loadDevice(DbModel device) {
        this.device = device;
        saveCurrentData = true;
        if (SystemConfig.isDevicePlaced()) {
            placedDevice = PlacedDeviceService.getInstance().findDevicePlaced(currentReportId, device.getString("deviceid"));
        } else {
            placed = PlacedService.getInstance().findPlaced(currentReportId, device.getString("spid"));
        }
    }

    public List<TreeNode> loadItem() {
        String currentDeviceId = device.getString("deviceid");
        final List<TreeNode> newData = new ArrayList<TreeNode>();
        // 查询当前报告已抄录项目
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
        // 查询当前设备抄录项
        List<CopyItem> copyItemList = CopyItemService.getInstance().getDeviceCopyItem(currentBdzId, currentDeviceId, currentInspectionType);
        // 抄录结构map
        copyResultMap = new HashMap<>();
        // 按照抄录类型-抄录项转换结果
        Map<String, List<CopyItem>> typeCopyItemMap = new HashMap<>();
        if (null != copyItemList && !copyItemList.isEmpty()) {
            for (CopyItem item : copyItemList) {
                CopyResult result = reportCopyResultMap.get(item.id);
                CopyResult historyResult = historyMap.get(item.id);
                if (null == result) {
                    result = new CopyResult(currentReportId, item.id, item.bdzid, item.deviceid, item.device_name, item.type_key, item.description, item.unit, item.install_place);
                    // 赋值历史数据
                    if (null != historyResult) {
                        if ("Y".equalsIgnoreCase(item.val)) {
                            result.val = "-1".equalsIgnoreCase(historyResult.val) ? "" : historyResult.val;
                            result.valSpecial = historyResult.valSpecial;
                        }
                        if ("Y".equalsIgnoreCase(item.val_a)) {
                            result.val_a = "-1".equalsIgnoreCase(historyResult.val_a) ? "" : historyResult.val_a;
                        }
                        if ("Y".equalsIgnoreCase(item.val_b)) {
                            result.val_b = "-1".equalsIgnoreCase(historyResult.val_b) ? "" : historyResult.val_b;
                        }
                        if ("Y".equalsIgnoreCase(item.val_c)) {
                            result.val_c = "-1".equalsIgnoreCase(historyResult.val_c) ? "" : historyResult.val_c;
                        }
                        if ("Y".equalsIgnoreCase(item.val_o)) {
                            result.val_o = "-1".equalsIgnoreCase(historyResult.val_o) ? "" : historyResult.val_o;
                        }
                    }
                }
                // 上次抄录值
                if (null != historyResult) {
                    if ("Y".equalsIgnoreCase(item.val)) {
                        result.val_old = historyResult.val;
                    }
                    if ("Y".equalsIgnoreCase(item.val_a)) {
                        result.val_a_old = historyResult.val_a;
                    }
                    if ("Y".equalsIgnoreCase(item.val_b)) {
                        result.val_b_old = historyResult.val_b;
                    }
                    if ("Y".equalsIgnoreCase(item.val_c)) {
                        result.val_c_old = historyResult.val_c;
                    }
                    if ("Y".equalsIgnoreCase(item.val_o)) {
                        result.val_o_old = historyResult.val_o;
                    }
                }
                copyResultMap.put(item.id, result);
                if (typeCopyItemMap.keySet().contains(item.type_key)) {
                    typeCopyItemMap.get(item.type_key).add(item);
                } else {
                    List<CopyItem> itemList = new ArrayList<>();
                    itemList.add(item);
                    typeCopyItemMap.put(item.type_key, itemList);
                }
            }
        }
        if (!typeCopyItemMap.isEmpty()) {
            // 转换抄录项树形结构
            for (String key : typeCopyItemMap.keySet()) {
                List<CopyItem> itemList = typeCopyItemMap.get(key);
                TreeNode parentNode = null;
                for (CopyItem item : itemList) {
                    if (parentNode == null) {
                        CopyItem parentItem = item.clone();
                        parentItem.description = copyType.get(item.type_key);
                        parentNode = new TreeNode(null, 1, true, parentItem);
                    }
                    if ("Y".equalsIgnoreCase(item.val)) {
                        CopyItem childItem = item.clone();
                        childItem.setCopy("Y", "N", "N", "N", "N");
                        parentNode.addChildNode(new TreeNode(parentNode, 2, false, childItem));
                    }
                    if ("Y".equalsIgnoreCase(item.val_a)) {
                        CopyItem childItem = item.clone();
                        childItem.setCopy("N", "Y", "N", "N", "N");
                        parentNode.addChildNode(new TreeNode(parentNode, 2, false, childItem));
                    }
                    if ("Y".equalsIgnoreCase(item.val_b)) {
                        CopyItem childItem = item.clone();
                        childItem.setCopy("N", "N", "Y", "N", "N");
                        parentNode.addChildNode(new TreeNode(parentNode, 2, false, childItem));
                    }
                    if ("Y".equalsIgnoreCase(item.val_c)) {
                        CopyItem childItem = item.clone();
                        childItem.setCopy("N", "N", "N", "Y", "N");
                        parentNode.addChildNode(new TreeNode(parentNode, 2, false, childItem));
                    }
                    if ("Y".equalsIgnoreCase(item.val_o)) {
                        CopyItem childItem = item.clone();
                        childItem.setCopy("N", "N", "N", "N", "Y");
                        parentNode.addChildNode(new TreeNode(parentNode, 2, false, childItem));
                    }

                }
                newData.add(parentNode);
            }
        }
        return newData;
    }

    private boolean tips=false;

    public void valueNullTips(boolean tips) {
        this.tips = tips;
    }

    public boolean saveAll() {
        if (copyResultMap == null || !saveCurrentData) {
            return false;
        }
        boolean savePlaced = false;
        boolean rs = false;
        for (CopyResult result : copyResultMap.values()) {
            if (TextUtils.isEmpty(result.val)) {
                result.val = null;
            }
            if (TextUtils.isEmpty(result.val_a)) {
                result.val_a = null;
            }
            if (TextUtils.isEmpty(result.val_b)) {
                result.val_b = null;
            }
            if (TextUtils.isEmpty(result.val_c)) {
                result.val_c = null;
            }
            if (TextUtils.isEmpty(result.val_o)) {
                result.val_o = null;
            }
            if (TextUtils.isEmpty(result.valSpecial)) {
                result.valSpecial = null;
            }
            if (result.type_key.equalsIgnoreCase("youwei") && !TextUtils.isEmpty(result.valSpecial)) {
                result.val = null;
            }
            if (tips) {
                if (TextUtils.isEmpty(result.val) && TextUtils.isEmpty(result.val_a) && TextUtils.isEmpty(result.val_b) && TextUtils.isEmpty(result.val_c) && TextUtils.isEmpty(result.val_o)) {
                    return false;
                }
            }

            if (!TextUtils.isEmpty(result.val)) {
                savePlaced = true;
            } else if (!TextUtils.isEmpty(result.val_a)) {
                savePlaced = true;
            } else if (!TextUtils.isEmpty(result.val_b)) {
                savePlaced = true;
            } else if (!TextUtils.isEmpty(result.val_c)) {
                savePlaced = true;
            } else if (!TextUtils.isEmpty(result.val_o)) {
                savePlaced = true;
            } else if (!TextUtils.isEmpty(result.valSpecial)) {
                savePlaced = true;
            }

            // 初始化动作次数等值
            result.initArresterActionValue();
            if (reportResultList.contains(result) || result.isHasCopyData()) {
                CopyResultService.getInstance().saveOrUpdate(result);
                copyDeviceIdList.add(result.deviceid);
                rs = true;
            } else if (!result.isHasCopyData()) {
                copyDeviceIdList.remove(result.deviceid);
            }
        }

        //需要保存到位 且能通过抄录到位 且是一次设备
        if (savePlaced && Config.PLACED_BY_COPY) {
            //设备到位模式 且当前设备不是重点设备，或者是重点设备但不要求强制签到。
            if (SystemConfig.isDevicePlaced()) {
                if ((!Device.isImportant(device) || !SystemConfig.isMustPicImportantDevice())) {
                    if (placedDevice == null) {
                        placedDevice = PlacedDevice.create(device, currentReportId);
                    }
                    placedDevice.setPlacedWayHighest("copy");
                    PlacedDeviceService.getInstance().saveOrUpdate(placedDevice);
                }
            } else {
                if (Device.isOnceDevice(device)) {
                    if (placed == null) {
                        placed = new Placed(currentReportId, currentBdzId, device.getString(Device.SPID), device.getString(Spacing.SNAME), 1, 0, 0);
                    }
                    PlacedService.getInstance().saveOrUpdate(placed);
                }
            }
        }
        return rs;
    }

    public void judgeDistance(BDLocation currentLocation, View shadowLayout, TextView shadomTip) {
        if (null == currentLocation || null == device || !GPSUtils.isOPen(activity)) {
            return;
        }
        String latitude = device.getString("latitude");
        String longitude = device.getString("longitude");
        if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
            LatLng location = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            LatLng deviceLocation = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
            // 在范围内不显示遮挡
            if (DistanceUtil.isInCirle(location, deviceLocation, PreferencesUtils.get( Config.COPY_DISTANCE_KEY, 100f))) {
                shadomTip.setVisibility(View.GONE);
                if (SystemConfig.isDevicePlaced()) {
                    if (placedDevice == null) {
                        placedDevice = PlacedDevice.create(device, currentReportId);
                    }
                    placedDevice.latitude = String.valueOf(currentLocation.getLatitude());
                    placedDevice.longtitude = String.valueOf(currentLocation.getLongitude());
                    placedDevice.setPlacedWayHighest("gps");
                } else {
                    if (null == placed) {
                        placed = new Placed(currentReportId, currentBdzId, device.getString(Device.SPID), device.getString(Spacing.SNAME), 1, currentLocation.getLatitude(), currentLocation.getLongitude());
                    } else {
                        placed.latitude = currentLocation.getLatitude();
                        placed.longtitude = currentLocation.getLongitude();
                    }
                }
            } else {
                // 到位通过抄录判断
                if (Config.PLACED_BY_COPY) {
                    saveCurrentData = false;
                    shadowLayout.setVisibility(View.VISIBLE);
                    shadomTip.setTextSize(48);
                    shadomTip.setText("你距离该设备较远!");

                } else {
                    shadowLayout.setVisibility(View.GONE);
                }
            }
        } else {
            shadowLayout.setVisibility(View.GONE);
        }
    }

    public void setItemLongClickListener(CopyItemLongClickListener<CopyResult> itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }

    public void setKeyBordListener(CopyViewUtil.KeyBordListener keyBordListener) {
        this.keyBordListener = keyBordListener;
    }

    public void setItemClickListener(ItemClickListener<CopyItem> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @SuppressLint("ClickableViewAccessibility")
    public synchronized void createCopyView(final Activity context, List<TreeNode> data, LinearLayout parentLayout) {
        parentLayout.removeAllViews();
        int index = 0;
        requestEdtits.clear();
        copyItems.clear();
        for (TreeNode tree : data) {
            if (tree.isParent()) {
                String remark = "";
                // 添加根节点数据
                ViewHolder parentHolder = new ViewHolder(context, null, R.layout.xs_group_item2, false);
                CopyItem parentItem = (CopyItem) tree.bindObject;
                parentLayout.addView(parentHolder.getRootView());
                index++;
                for (TreeNode child : tree.childTreeNodes) {
                    // 添加抄录节点数据
                    final CopyItem childItem = (CopyItem) child.bindObject;
                    ViewHolder childHolder = new ViewHolder(context, null, R.layout.xs_copy_value_child_item2, false);
                    AutoUtils.autoSize(childHolder.getRootView());
                    RelativeLayout layoutRoot = (RelativeLayout) childHolder.getRootView();
                    if (childItem.type_key.equalsIgnoreCase("youwei")) {
                        childHolder.getView(R.id.ibtn_history_data).setVisibility(View.INVISIBLE);
                    }
                    // 构造抄录描述
                    String descript = "";
                    if ("Y".equals(childItem.val)) {
                        descript = "抄录" + childItem.description + (TextUtils.isEmpty(childItem.unit) ? "" : "(" + childItem.unit + ")");
                    } else if ("Y".equals(childItem.val_a)) {
                        descript = "抄录A相" + childItem.description + (TextUtils.isEmpty(childItem.unit) ? "" : "(" + childItem.unit + ")");
                    } else if ("Y".equals(childItem.val_b)) {
                        descript = "抄录B相" + childItem.description + (TextUtils.isEmpty(childItem.unit) ? "" : "(" + childItem.unit + ")");
                    } else if ("Y".equals(childItem.val_c)) {
                        descript = "抄录C相" + childItem.description + (TextUtils.isEmpty(childItem.unit) ? "" : "(" + childItem.unit + ")");
                    } else if ("Y".equals(childItem.val_o)) {
                        descript = "抄录O相" + childItem.description + (TextUtils.isEmpty(childItem.unit) ? "" : "(" + childItem.unit + ")");
                    }
                    final TextView txtDescript = childHolder.getView(R.id.tv_copy_content);
                    if (!TextUtils.isEmpty(childItem.min) || !TextUtils.isEmpty(childItem.max)) {
                        descript += "(" + (TextUtils.isEmpty(childItem.min) ? "" : childItem.min) + "-" + (TextUtils.isEmpty(childItem.max) ? "" : childItem.max) + ")";
                    }
                    txtDescript.setText(descript);
                    EditText copyValue = childHolder.getView(R.id.et_copy_values);
                    requestEdtits.add(copyValue);
                    copyItems.add(childItem);
                    copyValue.addTextChangedListener(new CopyTextWatcher(childItem));
                    copyValue.setOnTouchListener((v, event) -> {
                        CopyResult copyResult = copyResultMap.get(childItem.id);
                        keyBordListener.onViewFocus((EditText) v, childItem, copyResult, requestEdtits, copyItems);
                        return false;
                    });
                    copyValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            CopyResult copyResult = copyResultMap.get(childItem.id);
                            keyBordListener.onViewFocusChange((EditText) v, childItem, copyResult, hasFocus, txtDescript.getText().toString(), requestEdtits);
                        }
                    });
                    // 显示历史抄录
                    if (copyResultMap.keySet().contains(childItem.id)) {
                        CopyResult copyResult = copyResultMap.get(childItem.id);
                        if ("Y".equals(childItem.val) && !("-1".equals(copyResult.val)) && "youwei".equalsIgnoreCase(childItem.type_key)) {
                            copyValue.setText(TextUtils.isEmpty(copyResult.valSpecial) ? "" : copyResult.valSpecial);
                        } else {
                            if ("Y".equals(childItem.val) && !("-1".equals(copyResult.val))) {
                                copyValue.setText(copyResult.val);
                            }
                        }
                        if ("Y".equals(childItem.val_a) && !("-1".equals(copyResult.val_a))) {
                            copyValue.setText(copyResult.val_a);
                        }
                        if ("Y".equals(childItem.val_b) && !("-1".equals(copyResult.val_b))) {
                            copyValue.setText(copyResult.val_b);
                        }
                        if ("Y".equals(childItem.val_c) && !("-1".equals(copyResult.val_c))) {
                            copyValue.setText(copyResult.val_c);
                        }
                        if ("Y".equals(childItem.val_o) && !("-1".equals(copyResult.val_o))) {
                            copyValue.setText(copyResult.val_o);
                        }
                        if (!TextUtils.isEmpty(copyResult.remark)) {// 抄录结果的remar字段是否有看不清的提示。
                            remark = copyResult.remark;
                        }
                    }

                    final int position = index;
                    childHolder.getView(R.id.ibtn_history_data).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemClickListener.onItemClick(v, childItem, position);
                        }
                    });
                    // 抄录项如果看不清长按弹出对话框
                    layoutRoot.setOnLongClickListener(new View.OnLongClickListener() {

                        @Override
                        public boolean onLongClick(View v) {
                            CopyResult copyResult = copyResultMap.get(childItem.id);
                            itemLongClickListener.onItemLongClick(v, copyResult, position, childItem);
                            return true;
                        }
                    });
                    parentLayout.addView(childHolder.getRootView());
                    index++;
                }
                // 查看整个抄录项remark字段是否具有看不清的提示，有则在整个item上提示该显示。
                if (!TextUtils.isEmpty(remark)) {
                    if (remark.endsWith(",")) {
                        remark = "(" + remark.substring(0, remark.length() - 1) + ")";
                    } else {
                        remark = "(" + remark.substring(0, remark.length()) + ")";
                    }
                }
                parentHolder.setText(R.id.tv_group_item, parentItem.description + remark);
            }
        }
    }

    public List<CopyItem> getAllItems() {
        return copyItems;
    }

    public List<EditText> getAllEditText() {
        return requestEdtits;
    }

    public interface KeyBordListener {
        void onViewFocus(EditText v, CopyItem item, CopyResult result, List<EditText> editTexts, List<CopyItem> copyItems);

        void hideKeyBord();

        void onViewFocusChange(EditText v, CopyItem item, CopyResult result, boolean hasFocus, String descript, List<EditText> editTexts);
    }

    public class CopyTextWatcher implements TextWatcher {

        private CopyItem copyItem;

        public CopyTextWatcher(CopyItem copyItem) {
            this.copyItem = copyItem;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            CopyResult copyResult = copyResultMap.get(copyItem.id);
            if (null != copyResult) {
                if (copyResult.type_key.equalsIgnoreCase("youwei")) {
                    copyResult.valSpecial = s.toString();
                } else {
                    String value = CommonUtils.getTransformTep(s.toString());
                    if ("Y".equals(copyItem.val)) {
                        copyResult.val = value;
                    }
                    if ("Y".equals(copyItem.val_a)) {
                        copyResult.val_a = value;
                    }
                    if ("Y".equals(copyItem.val_b)) {
                        copyResult.val_b = value;
                    }
                    if ("Y".equals(copyItem.val_c)) {
                        copyResult.val_c = value;
                    }
                    if ("Y".equals(copyItem.val_o)) {
                        copyResult.val_o = value;
                    }
                }
            }
        }
    }
}
