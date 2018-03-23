package com.cnksi.sjjc.activity;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.common.ScreenManager;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.CopyDeviceAdapter;
import com.cnksi.sjjc.adapter.TreeNode;
import com.cnksi.sjjc.bean.CopyItem;
import com.cnksi.sjjc.bean.CopyResult;
import com.cnksi.sjjc.databinding.ActivityCopyAll3Binding;
import com.cnksi.sjjc.databinding.ActivityCopyDialogBinding;
import com.cnksi.sjjc.databinding.DialogCopyTipsBinding;
import com.cnksi.sjjc.inter.ItemClickListener;
import com.cnksi.sjjc.processor.CopyDataInterface;
import com.cnksi.sjjc.processor.ProcessorFactory;
import com.cnksi.sjjc.service.CopyItemService;
import com.cnksi.sjjc.service.CopyResultService;
import com.cnksi.sjjc.service.CopyTypeService;
import com.cnksi.sjjc.util.CopyViewUtil;
import com.cnksi.sjjc.util.DialogUtils;
import com.cnksi.sjjc.util.KeyBoardUtils;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 集中抄录数据 (集中抄录设备) 从设备列表界面跳转过来
 *
 * @author terry
 */
public class CopyAllValueActivity3 extends BaseActivity {
    private static final int LOAD_COPY_FINISIH = 0x10;
    private static final int LOAD_COPY_MAP = LOAD_COPY_FINISIH + 1;
    String remarkInfor = "";
    private CopyDeviceAdapter deviceAdapter;

    private List<DbModel> copyDeviceList;
    private List<DbModel> copyMap;
    private List<TreeNode> data;
    //当前报告抄录结果
    private DbModel currentDevice;
    private List<CopyResult> reportResultList;
    private HashMap<String, CopyResult> copyResultMap;
    private boolean isSpread = true;
    private boolean isFinish;
    private Dialog tipsDialog;
    private String currentDeviceId;
    private CopyViewUtil copyViewUtil;
    // 抄录看不清弹出备注对话框
    private Dialog dialog;
    //点击下一步后时间
    private long mAfterTime;
    //点击下一步的累计次数
    private int clickIndex;
    private CopyDataInterface processor;
    private ActivityCopyAll3Binding binding;

    /**
     * 遮罩计时器
     */
    private CountDownTimer timer = new CountDownTimer(6000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            binding.btnNext.setClickable(false);
            binding.btnPre.setClickable(false);
            deviceAdapter.setClickAble(false);
            binding.shadomTip1.setText("" + millisUntilFinished / 1000 + "");
        }

        @Override
        public void onFinish() {
            binding.shadom1.setVisibility(View.GONE);
            deviceAdapter.setClickAble(true);
            binding.btnNext.setClickable(true);
            binding.btnPre.setClickable(true);
            clickIndex = 0;
            mAfterTime = 0;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCopyAll3Binding.inflate(getLayoutInflater());
        setChildView(binding.getRoot());
        initView();
        loadData();
        setDeviceListDisplay();
        initOnClick();
    }

    @Override
    public void initUI() {

    }

    @Override
    public void initData() {

    }


    public void initView() {
        getIntentValue();
        processor = ProcessorFactory.getProcessor(currentInspectionType, currentReportId);
        mTitleBinding.tvTitle.setText(currentInspectionName + "记录" + "");
        mTitleBinding.tvRight.setText("完成记录");
        mTitleBinding.tvRight.setVisibility(View.VISIBLE);
        mTitleBinding.tvRight.setBackgroundResource(R.drawable.red_button_background_selector);
        copyDeviceList = new ArrayList<>();
        deviceAdapter = new CopyDeviceAdapter(this, copyDeviceList, R.layout.device_item);
        deviceAdapter.setItemClickListener(new ItemClickListener<DbModel>() {
            @Override
            public void itemClick(View v, DbModel dbModel, int position) {
                if (isSpread) {
                    setDeviceListDisplay();
                }
                if (!showShadom()) {
                    if (null != dbModel) {
                        saveAll();
                        deviceAdapter.setCurrentSelectedPosition(position);
                        currentDevice = dbModel;
                        if (!deviceAdapter.isLast()) {
                            isFinish = false;
                            binding.btnNext.setText(R.string.next);
                        }
                        setCurrentDevice(position);

                    } else {
                        data.clear();
                        binding.copyContainer.removeAllViews();
                    }
                }
            }

            @Override
            public void itemLongClick(View v, DbModel dbModel, int position) {

            }
        });
        binding.gvContainer.setAdapter(deviceAdapter);

        data = new ArrayList<>();
        copyViewUtil = new CopyViewUtil();
        copyViewUtil.setItemLongClickListener((v, result, position, item) -> {
            final ActivityCopyDialogBinding notClearDialogBinding = ActivityCopyDialogBinding.inflate(getLayoutInflater());
            notClearDialogBinding.btnCancel.setOnClickListener(v1 -> dialog.dismiss());
            notClearDialogBinding.btnSure.setOnClickListener(v12 -> saveNotClearCopyInfo(result, notClearDialogBinding.etCopyValues, item));
            dialog = DialogUtils.creatDialog(_this, notClearDialogBinding.getRoot(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            notClearDialogBinding.etCopyValues.setText(TextUtils.isEmpty(result.remark) ? "看不清" : result.remark.subSequence(0, result.remark.length()));
            dialog.show();
        });
        copyViewUtil.setItemClickListener(new ItemClickListener<CopyItem>() {
            @Override
            public void itemClick(View v, CopyItem copyItem, int position) {
                KeyBoardUtils.closeKeybord(_this);
                CopyDataInterface.showHistory(_this, copyItem);
            }

            @Override
            public void itemLongClick(View v, CopyItem copyItem, int position) {

            }
        });
    }

    /**
     * 展示遮罩
     */
    public boolean showShadom() {
        long mCurrentTime = System.currentTimeMillis();
        if (0 == clickIndex)
            mAfterTime = mCurrentTime;
        long diffTime = mCurrentTime - mAfterTime;
        clickIndex++;
        if (1000 >= diffTime && 3 <= clickIndex) {
            binding.shadom1.setVisibility(View.VISIBLE);
            ToastUtils.showMessage("不要作弊哟，5秒后继续操作。");
            timer.start();
            return true;
        } else {
            if (500 <= diffTime) {
                mAfterTime = 0;
                clickIndex = 0;
            }
            return false;
        }
    }

    public void loadData() {
        ExecutorManager.executeTaskSerially(() -> {
            try {
                List<DbModel> deviceList = processor.findAllDeviceHasCopyValue("one", currentBdzId);
                copyDeviceList.clear();
                if (null != deviceList && !deviceList.isEmpty()) {
                    copyDeviceList.addAll(deviceList);
                    setCurrentDevice(0);
                }
                mHandler.sendEmptyMessage(LOAD_DATA);
            } catch (DbException e) {
                e.printStackTrace();
            }
        });
    }

    private void setCurrentDevice(final int position) {

        binding.gvContainer.smoothScrollToPosition(position);
        ExecutorManager.executeTaskSerially(() -> {
            currentDevice = copyDeviceList.get(position);
            currentDeviceId = currentDevice.getString("deviceid");
            data.clear();
            // 所有抄录类型
            Map<String, String> copyType = CopyTypeService.getInstance().getAllCopyType();
            // 查询当前报告已抄录项目
            reportResultList = CopyResultService.getInstance().getResultList(currentBdzId, currentReportId, currentDeviceId, true, processor.getCopyType());
            Map<String, CopyResult> reportCopyResultMap = new HashMap<>();
            if (null != reportResultList && !reportResultList.isEmpty()) {
                for (CopyResult result : reportResultList)
                    reportCopyResultMap.put(result.item_id, result);
            }
            // 历史抄录值
            List<CopyResult> historyResultList = CopyResultService.getInstance().getResultList(currentBdzId, currentReportId, currentDeviceId, false, processor.getCopyType());
            Map<String, CopyResult> historyMap = new HashMap<>();
            if (null != historyResultList && !historyResultList.isEmpty()) {
                for (CopyResult historyResult : historyResultList) {
                    historyMap.put(historyResult.item_id, historyResult);
                }
            }
            // 查询当前设备抄录项
//				List<CopyItem> copyItemList = CopyItemService.getInstance().getDeviceCopyItem1(currentBdzId, currentDeviceId, CopyItemService.getInstance().getCopyType());
            List<CopyItem> copyItemList = CopyItemService.getInstance().getDeviceCopyItem(currentBdzId,
                    currentDeviceId, processor.getCopyType());
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
                            if ("Y".equalsIgnoreCase(item.val))
                                result.val = "-1".equalsIgnoreCase(historyResult.val) ? "" : historyResult.val;
                            if ("Y".equalsIgnoreCase(item.val_a))
                                result.val_a = "-1".equalsIgnoreCase(historyResult.val_a) ? "" : historyResult.val_a;
                            if ("Y".equalsIgnoreCase(item.val_b))
                                result.val_b = "-1".equalsIgnoreCase(historyResult.val_b) ? "" : historyResult.val_b;
                            if ("Y".equalsIgnoreCase(item.val_c))
                                result.val_c = "-1".equalsIgnoreCase(historyResult.val_c) ? "" : historyResult.val_c;
                            if ("Y".equalsIgnoreCase(item.val_o))
                                result.val_o = "-1".equalsIgnoreCase(historyResult.val_o) ? "" : historyResult.val_o;
                        }
                    }
                    // 上次抄录值
                    if (null != historyResult) {
                        if ("Y".equalsIgnoreCase(item.val))
                            result.val_old = historyResult.val;
                        if ("Y".equalsIgnoreCase(item.val_a))
                            result.val_a_old = historyResult.val_a;
                        if ("Y".equalsIgnoreCase(item.val_b))
                            result.val_b_old = historyResult.val_b;
                        if ("Y".equalsIgnoreCase(item.val_c))
                            result.val_c_old = historyResult.val_c;
                        if ("Y".equalsIgnoreCase(item.val_o))
                            result.val_o_old = historyResult.val_o;
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
                    data.add(parentNode);
                }
            }
            mHandler.sendEmptyMessage(LOAD_COPY_FINISIH);
            // 设置当前抄录设备集合,判断当前设备是否抄录
//				copyMap = CopyResultService.getInstance().getCopyDeviceIdList1(currentReportId, CopyItemService.getInstance().getCopyType());
            copyMap = CopyResultService.getInstance().getCopyDeviceIdList(processor.getCopyType(), currentReportId);
            mHandler.sendEmptyMessage(LOAD_COPY_MAP);
        });

    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                deviceAdapter.notifyDataSetChanged();
                break;
            case LOAD_COPY_FINISIH:
                if (!data.isEmpty()) {
                    copyViewUtil.setCopyResultMap(copyResultMap);
                    copyViewUtil.createCopyView(this, data, binding.copyContainer);
                }

                break;
            case LOAD_COPY_MAP:
                deviceAdapter.setCopyDeviceModel(copyMap);
                deviceAdapter.notifyDataSetChanged();
                break;
        }
    }


    private void initOnClick() {
        mTitleBinding.tvRight.setOnClickListener(view -> {
                    saveAll();
                    String tip = String.format(getText(R.string.dialog_tips_finish_str) + "", processor.getCopyResult(currentBdzId));
                    showTipsDialog(binding.llRootContainer, tip);
                }
        );
        binding.ibtnSpread.setOnClickListener(view -> {
            setDeviceListDisplay();
        });
        binding.btnNext.setOnClickListener(view -> {
            if (!isFinish) {
                saveAll();
                if (deviceAdapter.isLast()) {
                    binding.btnNext.setText(R.string.finish_str);
                    isFinish = true;
                } else {
                    deviceAdapter.next();
                }
            } else {
//                onClick(findViewById(R.id.tv_right));
            }
        });
        binding.btnPre.setOnClickListener(view -> {
            saveAll();
            if (!deviceAdapter.isFirst())
                deviceAdapter.pre();
            if (isFinish) {
                isFinish = false;
                binding.btnNext.setText(R.string.next);
            }
        });
    }


    /**
     * 完成巡检提示框
     */
    private DialogCopyTipsBinding mTipsBinding;

    protected void showTipsDialog(ViewGroup mRootContainer, CharSequence copytips) {
        int dialogWidth = ScreenUtils.getScreenWidth(_this) * 9 / 10;
        int dialogHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
        if (tipsDialog == null) {
            mTipsBinding = DialogCopyTipsBinding.inflate(getLayoutInflater());
            tipsDialog = DialogUtils.creatDialog(_this, mTipsBinding.getRoot(), dialogWidth, dialogHeight);
        }
        mTipsBinding.tvCopy.setText(copytips);
        mTipsBinding.tvTips.setVisibility(View.GONE);
        tipsDialog.show();
        mTipsBinding.btnSure.setOnClickListener(view -> {
            KeyBoardUtils.closeKeybord(_this);
            try {
                String checkRs = "";
                checkRs = mTipsBinding.rbYes.isChecked() ? "正常" : "不正常";
                processor.finishTask(currentTaskId, checkRs);
            } catch (DbException e) {
                e.printStackTrace();
            }
            isNeedUpdateTaskState = true;
            Intent intent = new Intent(_this, CopyValueReportActivity.class);
            startActivity(intent);
            ScreenManager.getScreenManager().popActivity(CopyBaseDataActivity.class);
            _this.finish();
        });
        mTipsBinding.btnCancel.setOnClickListener(view -> {
            KeyBoardUtils.closeKeybord(_this);
            tipsDialog.dismiss();
        });
    }

    /**
     * 设置集中抄录数据设备列表的显示状态 展开 合拢
     */
    private void setDeviceListDisplay() {

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.rlCopyAllValueContainer.getLayoutParams();
        if (!isSpread) {

            binding.ibtnSpread.setImageResource(R.mipmap.ic_narrow);
            binding.copyContainer.setVisibility(View.GONE);
            params.height = LinearLayout.LayoutParams.MATCH_PARENT;
        } else {
            binding.ibtnSpread.setImageResource(R.mipmap.ic_amplify);
            binding.copyContainer.setVisibility(View.VISIBLE);
//            params.height = getResources().getDimensionPixelSize(R.dimen.copy_all_value_container_height);
            params.height = AutoUtils.getPercentHeightSizeBigger((int) getResources().getDimension(R.dimen.copy_all_value_container_height_px));
        }
        binding.rlCopyAllValueContainer.setLayoutParams(params);
        isSpread = !isSpread;
    }

    @Override
    public void finish() {
        saveAll();
        super.finish();
    }

    public void saveAll() {
        if (copyResultMap == null) {
            return;
        }
        for (CopyResult result : copyResultMap.values()) {
            if (TextUtils.isEmpty(result.val))
                result.val = null;
            if (TextUtils.isEmpty(result.val_a))
                result.val_a = null;
            if (TextUtils.isEmpty(result.val_b))
                result.val_b = null;
            if (TextUtils.isEmpty(result.val_c))
                result.val_c = null;
            if (TextUtils.isEmpty(result.val_o))
                result.val_o = null;
            // 初始化动作次数等值
            result.initArresterActionValue();
            //如果当前报告抄录有该项或者该项的抄录字段值都不为空时进行保存或者更新
            if (reportResultList.contains(result) || !TextUtils.isEmpty(result.val) || !TextUtils.isEmpty(result.val_a) || !TextUtils.isEmpty(result.val_b) || !TextUtils.isEmpty(result.val_c) || !TextUtils.isEmpty(result.val_o))
                CopyResultService.getInstance().saveOrUpdate(result);
        }

    }

    private void saveNotClearCopyInfo(CopyResult result, View v, CopyItem item) {
        EditText etInput = (EditText) v;
        if ("youwei".equalsIgnoreCase(item.type_key))
            result.valSpecial = null;
        if ("Y".equals(item.val))
            result.val = "-1";
        else if ("Y".equals(item.val_a))
            result.val_a = "-1";
        else if ("Y".equals(item.val_b))
            result.val_b = "-1";
        else if ("Y".equals(item.val_c))
            result.val_c = "-1";
        else if ("Y".equals(item.val_o))
            result.val_o = "-1";
        result.remark = TextUtils.isEmpty(etInput.getText().toString()) ? "" : (TextUtils.isEmpty(result.remark) ? etInput.getText().toString() + "," : etInput.getText().toString());
        dialog.dismiss();
        copyViewUtil.createCopyView(_this, data, binding.copyContainer);
    }

}