package com.cnksi.bdzinspection.activity;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.CopyDeviceAdapter;
import com.cnksi.bdzinspection.daoservice.CopyItemService;
import com.cnksi.bdzinspection.daoservice.CopyResultService;
import com.cnksi.bdzinspection.daoservice.CopyTypeService;
import com.cnksi.bdzinspection.daoservice.DefectRecordService;
import com.cnksi.bdzinspection.databinding.XsActivityCopyAll3Binding;
import com.cnksi.bdzinspection.databinding.XsActivityCopyDialogBinding;
import com.cnksi.bdzinspection.databinding.XsDialogCopyTipsBinding;
import com.cnksi.bdzinspection.databinding.XsDialogTipsBinding;
import com.cnksi.bdzinspection.model.CopyItem;
import com.cnksi.bdzinspection.model.CopyResult;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.bdzinspection.model.TreeNode;
import com.cnksi.common.Config;
import com.cnksi.bdzinspection.utils.CopyViewUtil;
import com.cnksi.bdzinspection.utils.CopyViewUtil.KeyBordListener;
import com.cnksi.bdzinspection.utils.DefectUtils;
import com.cnksi.bdzinspection.utils.DialogUtils;
import com.cnksi.bdzinspection.utils.KeyBoardUtil;
import com.cnksi.bdzinspection.utils.KeyBoardUtil.OnKeyBoardStateChangeListener;
import com.cnksi.bdzinspection.utils.ShowHistroyDialogUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.common.utils.KeyBoardUtils;
import com.cnksi.core.utils.ScreenUtils;

import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.cnksi.bdzinspection.activity.NewDeviceDetailsActivity.UPDATE_DEVICE_DEFECT_REQUEST_CODE;
import static com.cnksi.common.Config.LOAD_DATA;


/**
 * 集中抄录数据 (集中抄录设备) 从设备列表界面跳转过来
 *
 * @author terry
 */
public class CopyAllValueActivity3 extends BaseActivity implements KeyBordListener {
    private static final int LOAD_COPY_FINISIH = 0x10;
    private static final int LOAD_COPY_MAP = LOAD_COPY_FINISIH + 1;
    protected int currentKeyBoardState = KeyBoardUtil.KEYBORAD_HIDE;
    Dialog defectDialog;
    XsDialogTipsBinding tipsBinding;
    private CopyDeviceAdapter deviceAdapter;
    private List<DbModel> copyDeviceList;
    private HashSet<String> copyMap;
    private List<TreeNode> data;
    private DbModel currentDevice;
    private HashMap<String, CopyResult> copyResultMap;
    private boolean isSpread = true;
    private boolean isFinish;
    private Dialog tipsDialog;
    private String currentDeviceId;
    private CopyViewUtil copyViewUtil;
    // 抄录看不清弹出备注对话框
    private Dialog dialog;
    //看不清标记
    private String remarkInfor = "";
    //点击下一步后时间
    private long mAfterTime;
    //点击下一步的累计次数
    private int clickIndex;
    // 当前报告抄录结果
    private List<CopyResult> reportResultList;
    private CountDownTimer timer = new CountDownTimer(6000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            binding.shadomTip1.setText("" + millisUntilFinished / 1000);
        }

        @Override
        public void onFinish() {
            binding.shadom1.setVisibility(View.GONE);
            clickIndex = 0;
            mAfterTime = 0;
        }
    };
    //是否显示抄录大小于上下限值对话框标志
    private boolean isShowTips = true;
    //传递预设缺陷内容
    private String transDefectContent = "";
    private List<DefectRecord> mExistDefectList = new ArrayList<>();
    private EditText v;
    private List<EditText> editTextList;
    private List<CopyItem> copyItemList;
    private XsActivityCopyAll3Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(currentActivity, R.layout.xs_activity_copy_all3);
        setDeviceListDisplay();
        initialUI();
        initialData();
        initOnClick();
    }


    private void initialUI() {
        getIntentValue();
        creatDefectDialog();
        binding.tvTitle.setText(currentInspectionTypeName + "记录");
        binding.tvBatteryTestStep.setText("完成记录");
        binding.tvBatteryTestStep.setVisibility(View.VISIBLE);
        binding.tvBatteryTestStep.setBackgroundResource(R.drawable.xs_red_button_background_selector);

        copyDeviceList = new ArrayList<>();
        deviceAdapter = new CopyDeviceAdapter(this, copyDeviceList, R.layout.xs_device_item);
        deviceAdapter.setItemClickListener((v, dbModel, position) -> {
            if (isSpread) {
                setDeviceListDisplay();
            }
            if (!showShadom()) {
                if (null != dbModel) {
                    deviceAdapter.setCurrentSelectedPosition(position);
                    currentDevice = dbModel;
                    if (!deviceAdapter.isLast()) {
                        isFinish = false;
                        binding.btnNext.setText(R.string.xs_next);
                    }
                    saveAll();
                    setCurrentDevice(position);

                } else {
                    data.clear();
                    binding.copyContainer.removeAllViews();
                }
            }
        });
        binding.gvContainer.setAdapter(deviceAdapter);

        data = new ArrayList<>();
        copyViewUtil = new CopyViewUtil();
        copyViewUtil.setKeyBordListener(this);
        copyViewUtil.setItemLongClickListener((v, result, position, item) -> {
            remarkInfor = "";
            XsActivityCopyDialogBinding copyDialogBinding = XsActivityCopyDialogBinding.inflate(getLayoutInflater());
            copyDialogBinding.etCopyValues.setText(TextUtils.isEmpty(result.remark) ? "看不清" : result.remark.subSequence(0, result.remark.length()));
            dialog = DialogUtils.createDialog(currentActivity,copyDialogBinding.getRoot(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            dialog.show();
            copyDialogBinding.btnCancel.setOnClickListener(view -> dialog.dismiss());
            copyDialogBinding.btnSure.setOnClickListener(view -> {
                saveRemarkData(result, copyDialogBinding.etCopyValues, item);
            });
        });
        copyViewUtil.setItemClickListener((v, copyItem, position) -> {
            hideKeyBord();
            ShowHistroyDialogUtils.showHistory(currentActivity, copyItem);
        });
    }

    private void saveRemarkData(CopyResult result, TextView etInput, CopyItem item) {
        if (item.type_key.equalsIgnoreCase("youwei"))
            result.valSpecial = null;
        if ("Y".equals(item.val)) {
            if ((!TextUtils.isEmpty(etInput.getText().toString()))) {
                result.val = "-1";
            } else {
                result.val = "";
            }
        } else if ("Y".equals(item.val_a)) {
            if ((!TextUtils.isEmpty(etInput.getText().toString()))) {
                result.val_a = "-1";
            } else {
                result.val_a = "";
            }
        } else if ("Y".equals(item.val_b)) {
            if ((!TextUtils.isEmpty(etInput.getText().toString()))) {
                result.val_b = "-1";
            } else {
                result.val_b = "";
            }
        } else if ("Y".equals(item.val_c)) {
            if ((!TextUtils.isEmpty(etInput.getText().toString()))) {
                result.val_c = "-1";
            } else {
                result.val_c = "";
            }
        } else if ("Y".equals(item.val_o)) {
            if ((!TextUtils.isEmpty(etInput.getText().toString()))) {
                result.val_o = "-1";
            } else {
                result.val_o = "";
            }
        }
        result.remark = TextUtils.isEmpty(etInput.getText().toString()) ? "" : (TextUtils.isEmpty(result.remark) ? etInput.getText().toString() + "," : etInput.getText().toString());
        dialog.dismiss();
        copyViewUtil.createCopyView(currentActivity, data, binding.copyContainer);
    }

    private void initialData() {
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<DbModel> deviceList = null;
                try {
                    searchDefect();
                    deviceList = CopyItemService.getInstance().findAllDeviceHasCopyValue(currentInspectionType, currentBdzId);
                    copyDeviceList.clear();
                    if (null != deviceList && !deviceList.isEmpty()) {
                        copyDeviceList.addAll(deviceList);
                        setCurrentDevice(0);
                    }
                    mHandler.sendEmptyMessage(LOAD_DATA);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setCurrentDevice(final int position) {
        binding.gvContainer.smoothScrollToPosition(position);
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                currentDevice = copyDeviceList.get(position);
                currentDeviceId = currentDevice.getString("deviceid");
                final List<TreeNode> newData = new ArrayList<TreeNode>();
                // 所有抄录类型
                Map<String, String> copyType = CopyTypeService.getInstance().getAllCopyType();
                // 查询当前报告已抄录项目
                reportResultList = CopyResultService.getInstance().getResultList(currentBdzId, currentReportId, currentDeviceId, true, CopyItemService.getInstance().getCopyType());
                Map<String, CopyResult> reportCopyResultMap = new HashMap<>();
                if (null != reportResultList && !reportResultList.isEmpty()) {
                    for (CopyResult result : reportResultList)
                        reportCopyResultMap.put(result.item_id, result);
                }
                // 历史抄录值
                List<CopyResult> historyResultList = CopyResultService.getInstance().getResultList(currentBdzId, currentReportId, currentDeviceId, false, CopyItemService.getInstance().getCopyType());
                Map<String, CopyResult> historyMap = new HashMap<>();
                if (null != historyResultList && !historyResultList.isEmpty()) {
                    for (CopyResult historyResult : historyResultList) {
                        historyMap.put(historyResult.item_id, historyResult);
                    }
                }
                // 查询当前设备抄录项
                List<CopyItem> copyItemList = CopyItemService.getInstance().getDeviceCopyItem1(currentBdzId, currentDeviceId, CopyItemService.getInstance().getCopyType());
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
                        newData.add(parentNode);
                    }
                }
                mHandler.sendEmptyMessage(LOAD_COPY_FINISIH);
                // 设置当前抄录设备集合,判断当前设备是否抄录
                copyMap = CopyResultService.getInstance().getCopyDeviceIdList1(currentReportId, CopyItemService.getInstance().getCopyType());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        data.clear();
                        data.addAll(newData);
                        mHandler.sendEmptyMessage(LOAD_COPY_MAP);
                    }
                });
            }
        });

    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                deviceAdapter.notifyDataSetChanged();
                break;
            case LOAD_COPY_FINISIH:

                break;
            case LOAD_COPY_MAP:
                if (!data.isEmpty()) {
                    copyViewUtil.setCopyResultMap(copyResultMap);
                    copyViewUtil.createCopyView(this, data, binding.copyContainer);
                    deviceAdapter.setCopyDeviceModel(copyMap);
                    deviceAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void onViewFocusChange(EditText v, CopyItem item, CopyResult result, boolean hasFocus, String descript, List<EditText> editTexts) {
        String val = v.getText().toString().trim();
        if (hasFocus || TextUtils.isEmpty(val)) {
            isShowTips = false;
            return;
        }
        List<String> rs = new ArrayList<>();
        if (DefectUtils.calcCopyBound(item, copyResultMap.get(item.id), val, mExistDefectList, rs)) {
            tipsBinding.tvDialogContent.setText(rs.get(1));
            transDefectContent = rs.get(0);
            if (null != defectDialog)
                defectDialog.show();
            isShowTips = true;
        } else {
            isShowTips = false;
        }
    }

    public void creatDefectDialog() {
        int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 7 / 9;
        tipsBinding = XsDialogTipsBinding.inflate(getLayoutInflater());
        defectDialog = DialogUtils.createDialog(currentActivity, tipsBinding.getRoot(), dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        tipsBinding.tvDialogTitle.setText("警告");
        tipsBinding.btnCancel.setText("否");
        tipsBinding.btnSure.setText("是");
        tipsBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defectDialog.dismiss();
            }
        });
        tipsBinding.btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyBord();
                defectDialog.dismiss();
                Intent intent = new Intent(currentActivity, AddNewDefectActivity.class);
                setIntentValue(intent);
                startActivityForResult(intent, UPDATE_DEVICE_DEFECT_REQUEST_CODE);
            }
        });
    }

    /**
     * 查询当前设备抄录项是否记录有上下限抄录缺陷
     */
    public void searchDefect() {
        mExistDefectList = DefectRecordService.getInstance().queryDefectByDeviceid(currentDeviceId, currentBdzId, currentReportId);
    }

    /**
     * 设置需要传递的值
     *
     * @param intent
     */
    private void setIntentValue(Intent intent) {
        intent.putExtra(Config.DEFECT_COUNT_KEY, transDefectContent);
        intent.putExtra(Config.CURRENT_DEVICE_ID, currentDeviceId);
        intent.putExtra(Config.CURRENT_DEVICE_NAME, currentDeviceName);
        intent.putExtra(Config.CURRENT_DEVICE_PART_ID, currentDevicePartId);
        intent.putExtra(Config.CURRENT_DEVICE_PART_NAME, currentDevicePartName);
        intent.putExtra(Config.CURRENT_SPACING_ID, currentSpacingId);
        intent.putExtra(Config.CURRENT_SPACING_NAME, currentSpacingName);
        intent.putExtra(Config.IS_PARTICULAR_INSPECTION, isParticularInspection);
    }

    private void initOnClick() {
        binding.tvBatteryTestStep.setOnClickListener(view -> {
            saveAll();
            String tip = String.format(getText(R.string.xs_dialog_tips_finish_str) + "", CopyResultService.getInstance().getCopyResult(currentBdzId, currentReportId, CopyItemService.getInstance().getCopyType()));
            showTipsDialog(binding.llRootContainer, tip);
        });


        binding.ibtnCancel.setOnClickListener(view -> {
            onBackPressed();
        });
        binding.btnNext.setOnClickListener(view -> {
            operateNextDevice();
        });

        binding.ibtnSpread.setOnClickListener(view -> {
            saveAll();
            if (!deviceAdapter.isFirst())
                deviceAdapter.pre();
            if (isFinish) {
                isFinish = false;
                binding.btnNext.setText(R.string.xs_next);
            }
            setDeviceListDisplay();
        });
    }

    private void operateNextDevice() {
        if (!isFinish) {
            saveAll();
            if (deviceAdapter.isLast()) {
                binding.btnNext.setText(R.string.xs_finish_str);
                isFinish = true;
            } else {
                deviceAdapter.next();
            }
        }
    }


    public boolean showShadom() {
        long mCurrentTime = System.currentTimeMillis();
        if (0 == clickIndex)
            mAfterTime = mCurrentTime;
        long diffTime = mCurrentTime - mAfterTime;
        clickIndex++;
        if (1000 >= diffTime && 3 <= clickIndex) {
            binding.shadom1.setVisibility(View.VISIBLE);
            ToastUtils.showMessageLong( "不要作弊哟，5秒后继续操作。");
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

    /**
     * 完成巡检提示框
     *
     * @param mRootContainer
     */
    XsDialogCopyTipsBinding copyTipsBinding;

    protected void showTipsDialog(ViewGroup mRootContainer, CharSequence copytips) {
        int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 9 / 10;
        int dialogHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
        if (tipsDialog == null) {
            copyTipsBinding = XsDialogCopyTipsBinding.inflate(getLayoutInflater());
            tipsDialog = DialogUtils.createDialog(currentActivity, copyTipsBinding.getRoot(), dialogWidth, dialogHeight);
        }
        copyTipsBinding.tvCopy.setText(copytips);
        copyTipsBinding.tvTips.setVisibility(View.GONE);
        tipsDialog.show();
        copyTipsBinding.btnSure.setOnClickListener(view -> {
            currentActivity.finish();
        });
        copyTipsBinding.btnCancel.setOnClickListener(view -> {
            tipsDialog.dismiss();
        });
    }

    /**
     * 设置集中抄录数据设备列表的显示状态 展开 合拢
     */
    private void setDeviceListDisplay() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) binding.rlCopyAllValueContainer.getLayoutParams();
        if (!isSpread) {
            binding.ibtnSpread.setImageResource(R.drawable.xs_ic_narrow);
            binding.copyContainer.setVisibility(View.GONE);
            params.height = LinearLayout.LayoutParams.MATCH_PARENT;
        } else {
            binding.ibtnSpread.setImageResource(R.drawable.xs_ic_amplify);
            binding.copyContainer.setVisibility(View.VISIBLE);
//            params.height = getResources().getDimensionPixelSize(R.dimen.copy_all_value_container_height);
            params.height = AutoUtils.getPercentHeightSizeBigger((int) getResources().getDimension(R.dimen.xs_copy_all_value_container_height_px));
        }
        binding.rlCopyAllValueContainer.setLayoutParams(params);
        isSpread = !isSpread;
    }

    @Override
    public void finish() {
        if (currentKeyBoardState == KeyBoardUtil.KEYBORAD_SHOW) {
            mKeyBoardUtil.hideKeyboard();
        }
        saveAll();
        super.finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideKeyBord();
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
            if (TextUtils.isEmpty(result.valSpecial))
                result.valSpecial = null;
            // 初始化动作次数等值
            result.initArresterActionValue();
            if (reportResultList.contains(result) || !TextUtils.isEmpty(result.val) || !TextUtils.isEmpty(result.val_a) || !TextUtils.isEmpty(result.val_b) || !TextUtils.isEmpty(result.val_c) || !TextUtils.isEmpty(result.val_o))
                CopyResultService.getInstance().saveOrUpdate(result);
        }

    }

    @Override
    public void onViewFocus(EditText v, CopyItem item, CopyResult result, List<EditText> editTexts, List<CopyItem> copyItems) {
        this.v = v;
        this.editTextList = editTexts;
        this.copyItemList = copyItems;
        if (item.type_key.contains("youwei") || "maintenance_7144".equalsIgnoreCase(item.type_key)) {// 如果有油温抄录项则直接弹出系统键盘可以输入分数-----yangjun
            v.setRawInputType(InputType.TYPE_CLASS_NUMBER);
            KeyBoardUtils.openKeybord(v, getApplicationContext());
            hideKeyBord();
        } else {
            if (null == mKeyBoardUtil) {
                createKeyBoardView(binding.llRootContainer);
                mKeyBoardUtil.setOnValueChangeListener(new OnKeyBoardStateChangeListener() {
                    @Override
                    public void onKeyBoardStateChange(int state) {
                        if (currentKeyBoardState != state) {
                            currentKeyBoardState = state;
                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.llKeyboardHelpLayout.getLayoutParams();
                            switch (state) {
                                case KeyBoardUtil.KEYBORAD_HIDE: // 键盘隐藏
                                    params.height = 0;
                                    params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                                    binding.llKeyboardHelpLayout.setLayoutParams(params);
                                    break;
                                case KeyBoardUtil.KEYBORAD_SHOW: // 键盘显示
                                    params.height = mWindowLayoutParams.height - getResources().getDimensionPixelSize(R.dimen.xs_button_minheight);
                                    params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
                                    binding.llKeyboardHelpLayout.setLayoutParams(params);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onKeyBoardNextInput(EditText editText) {
                        int position = editTextList.indexOf(editText);
                        if (position < editTextList.size() - 1) {
                            EditText nextEditText = editTextList.get(position + 1);
                            editText.clearFocus();
                            nextEditText.requestFocus();
                            onViewFocus(nextEditText, copyItemList.get(position + 1), copyResultMap.get(copyItemList.get(position + 1)), editTextList, copyItemList);
                        } else {
                            binding.llKeyboardHelpLayout.setVisibility(View.GONE);
                            hideKeyBord();
                        }
                    }
                });
                mKeyBoardUtil.showKeyboard();
            }
            KeyBoardUtils.closeKeybord(v, getApplicationContext());
            mKeyBoardUtil.setCurrentEditText(v, item.min, item.max, item.description + item.unit);
            mKeyBoardUtil.showKeyboard();
            showCursor(v);
        }
    }

    @Override
    public void hideKeyBord() {
        if (null != mKeyBoardUtil && currentKeyBoardState == KeyBoardUtil.KEYBORAD_SHOW) {
            mKeyBoardUtil.hideKeyboard();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_DEVICE_DEFECT_REQUEST_CODE) {
            searchDefect();
        }
    }

}
