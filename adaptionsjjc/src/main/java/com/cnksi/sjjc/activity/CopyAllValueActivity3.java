package com.cnksi.sjjc.activity;

import android.app.Dialog;
import android.content.Intent;
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

import com.cnksi.core.common.ScreenManager;
import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.KeyBoardUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.CopyDeviceAdapter;
import com.cnksi.sjjc.adapter.TreeNode;
import com.cnksi.sjjc.bean.CopyItem;
import com.cnksi.sjjc.bean.CopyResult;
import com.cnksi.sjjc.inter.CopyItemLongClickListener;
import com.cnksi.sjjc.inter.ItemClickListener;
import com.cnksi.sjjc.processor.CopyDataInterface;
import com.cnksi.sjjc.processor.ProcessorFactory;
import com.cnksi.sjjc.service.CopyItemService;
import com.cnksi.sjjc.service.CopyResultService;
import com.cnksi.sjjc.service.CopyTypeService;
import com.cnksi.sjjc.util.CopyViewUtil;
import com.cnksi.sjjc.util.DialogUtils;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
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
    private CopyDeviceAdapter deviceAdapter;

    @ViewInject(R.id.tv_title)
    private TextView mTvTitle;

    @ViewInject(R.id.gv_container)
    private GridView deviceListView;


    @ViewInject(R.id.copy_container)
    private LinearLayout copyContainer;

    @ViewInject(R.id.rl_copy_all_value_container)
    private RelativeLayout deviceLayout;

    @ViewInject(R.id.ll_root_container)
    private LinearLayout rootLayout1;

    @ViewInject(R.id.btn_next)
    private Button btnNext;

    @ViewInject(R.id.btn_pre)
    private Button btnPre;

    @ViewInject(R.id.ibtn_spread)
    private ImageButton btnSpread;

    @ViewInject(R.id.shadom_1)
    private LinearLayout layoutShadom;

    @ViewInject(R.id.shadom_tip_1)
    private TextView tvTip;

    private List<DbModel> copyDeviceList;

    private List<DbModel> copyMap;

    private List<TreeNode> data;

    private DbModel currentDevice;
    //当前报告抄录结果

    private List<CopyResult> reportResultList;

    private HashMap<String, CopyResult> copyResultMap;
    private boolean isSpread = true;
    private boolean isFinish;

    private Dialog tipsDialog;

    private ViewCompleteHolder tipsholder;

    private String currentDeviceId;

    private CopyViewUtil copyViewUtil;
    // 抄录看不清弹出备注对话框
    private Dialog dialog;
    String remarkInfor = "";
    //点击下一步后时间
    private long mAfterTime;
    //点击下一步的累计次数
    private int clickIndex;
    private CopyDataInterface processor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setChildView(R.layout.activity_copy_all3);
        x.view().inject(this);
        setDeviceListDisplay();
        initUI();
        initData();
    }

    private void initUI() {
        getIntentValue();
        processor = ProcessorFactory.getProcessor(currentInspectionType, currentReportId);
        mTvTitle.setText(currentInspectionName + "记录" + "");
        tvRight.setText("完成记录");
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setBackgroundResource(R.drawable.red_button_background_selector);
        copyDeviceList = new ArrayList<>();
        deviceAdapter = new CopyDeviceAdapter(this, copyDeviceList, R.layout.device_item);
        deviceAdapter.setItemClickListener(new ItemClickListener<DbModel>() {

            @Override
            public void itemClick(View v, DbModel dbModel, int position) {
                if (isSpread) {
                    setDeviceListDisplay();
                }
                if (null != dbModel) {
                    deviceAdapter.setCurrentSelectedPosition(position);
                    currentDevice = dbModel;
                    if (!deviceAdapter.isLast()) {
                        isFinish = false;
                        btnNext.setText(R.string.next);
                    }
                    setCurrentDevice(position);

                } else {
                    data.clear();
                    copyContainer.removeAllViews();
                }
            }

            @Override
            public void itemLongClick(View v, DbModel dbModel, int position) {

            }
        });
        deviceListView.setAdapter(deviceAdapter);

        data = new ArrayList<>();
        copyViewUtil = new CopyViewUtil();
//		copyViewUtil.setKeyBordListener(this);
        copyViewUtil.setItemLongClickListener(new CopyItemLongClickListener<CopyResult>() {
            @Override
            public void onItemLongClick(View v, CopyResult result, int position, CopyItem item) {
                remarkInfor = "";
                ViewHolder holder = new ViewHolder(result, v, item);
                dialog = DialogUtils.createDialog(_this, R.layout.activity_copy_dialog, holder);
                holder.etInput.setText(TextUtils.isEmpty(result.remark) ? "看不清" : result.remark.subSequence(0, result.remark.length()));
                // holder.etInput.setText("看不清");
                holder.remark = holder.etInput.getText().toString();
                dialog.show();
            }
        });
        copyViewUtil.setItemClickListener(new ItemClickListener<CopyItem>() {
            @Override
            public void itemClick(View v, CopyItem copyItem, int position) {
                KeyBoardUtils.closeKeybord(_this);
                processor.showHistory(_this, copyItem);
            }

            @Override
            public void itemLongClick(View v, CopyItem copyItem, int position) {

            }
        });
    }

    private void initData() {
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
//					List<DbModel> deviceList = CopyItemService.getInstance().findAllDeviceHasCopyValue(currentInspectionType, currentBdzId);
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
            }
        });
    }

    private void setCurrentDevice(final int position) {
        deviceListView.smoothScrollToPosition(position);
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                currentDevice = copyDeviceList.get(position);
                currentDeviceId = currentDevice.getString("deviceid");
                data.clear();
                // 所有抄录类型
                Map<String, String> copyType = CopyTypeService.getInstance().getAllCopyType();
                // 查询当前报告已抄录项目
                reportResultList = CopyResultService.getInstance().getResultList(currentBdzId,currentReportId, currentDeviceId, true, processor.getCopyType());
                Map<String, CopyResult> reportCopyResultMap = new HashMap<>();
                if (null != reportResultList && !reportResultList.isEmpty()) {
                    for (CopyResult result : reportResultList)
                        reportCopyResultMap.put(result.item_id, result);
                }
                // 历史抄录值
                List<CopyResult> historyResultList = CopyResultService.getInstance().getResultList(currentBdzId,currentReportId, currentDeviceId, false, processor.getCopyType());
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
                if (!data.isEmpty()) {
                    copyViewUtil.setCopyResultMap(copyResultMap);
                    copyViewUtil.createCopyView(this, data, copyContainer);
                }
                break;
            case LOAD_COPY_MAP:
                deviceAdapter.setCopyDeviceModel(copyMap);
                deviceAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Event({R.id.ibtn_cancel, R.id.ibtn_spread, R.id.btn_next, R.id.btn_pre, R.id.tv_right})
    private void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_right:
                saveAll();
//			String tip = String.format(getText(R.string.dialog_tips_finish_str) + "", CopyResultService.getInstance().getCopyResult(currentBdzId, currentReportId, CopyItemService.getInstance().getCopyType()));
                String tip = String.format(getText(R.string.dialog_tips_finish_str) + "", processor.getCopyResult(currentBdzId));
                showTipsDialog(rootLayout1, tip);
                break;
            case R.id.ibtn_cancel:
                onBackPressed();
                break;
            case R.id.ibtn_spread:
                setDeviceListDisplay();
                break;
            case R.id.btn_pre:
                saveAll();
                if (!deviceAdapter.isFirst())
                    deviceAdapter.pre();
                if (isFinish) {
                    isFinish = false;
                    btnNext.setText(R.string.next);
                }
                break;
            case R.id.btn_next:
                long mCurrentTime = System.currentTimeMillis();
                if (0 == clickIndex)
                    mAfterTime = mCurrentTime;
                long diffTime = mCurrentTime - mAfterTime;
                clickIndex++;
                if (1000 >= diffTime && 3 <= clickIndex) {
                    layoutShadom.setVisibility(View.VISIBLE);
                    layoutRelat.setVisibility(View.VISIBLE);
                    btnNext.setClickable(false);
                    btnPre.setClickable(false);
                    CToast.showLong(_this, "不要作弊哟，5秒后继续操作。");
                    new CountDownTimer(6000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            tvTip.setText("" + millisUntilFinished / 1000 + "");
                        }

                        @Override
                        public void onFinish() {
                            layoutShadom.setVisibility(View.GONE);
                            layoutRelat.setVisibility(View.GONE);
                            btnNext.setClickable(true);
                            btnPre.setClickable(true);
                            clickIndex = 0;
                            mAfterTime = 0;
                        }
                    }.start();
                } else {
                    if (500 <= diffTime) {
                        mAfterTime = 0;
                        clickIndex = 0;
                    }
                    if (!isFinish) {
                        saveAll();
                        if (deviceAdapter.isLast()) {
                            btnNext.setText(R.string.finish_str);
                            isFinish = true;
                        } else {
                            deviceAdapter.next();
                        }
                    } else {
                        onClick(findViewById(R.id.tv_right));
                    }
                }
                break;
        }
    }

    /**
     * 完成巡检提示框
     */
    protected void showTipsDialog(ViewGroup mRootContainer, CharSequence copytips) {
        int dialogWidth = ScreenUtils.getScreenWidth(_this) * 9 / 10;
        int dialogHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
        if (tipsDialog == null) {
            tipsDialog = DialogUtils.createDialog(_this, mRootContainer, R.layout.dialog_copy_tips, tipsholder == null ? tipsholder = new ViewCompleteHolder() : tipsholder, dialogWidth, dialogHeight, false);
        }
        tipsholder.tvCopy.setText(copytips);
        tipsDialog.show();
        tipsholder.tvTips.setVisibility(View.GONE);
    }

    /**
     * 设置集中抄录数据设备列表的显示状态 展开 合拢
     */
    private void setDeviceListDisplay() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) deviceLayout.getLayoutParams();
        if (!isSpread) {
            btnSpread.setImageResource(R.mipmap.ic_narrow);
            copyContainer.setVisibility(View.GONE);
            params.height = LinearLayout.LayoutParams.MATCH_PARENT;
        } else {
            btnSpread.setImageResource(R.mipmap.ic_amplify);
            copyContainer.setVisibility(View.VISIBLE);
//            params.height = getResources().getDimensionPixelSize(R.dimen.copy_all_value_container_height);
            params.height = AutoUtils.getPercentHeightSizeBigger((int) getResources().getDimension(R.dimen.copy_all_value_container_height_px));
        }
        deviceLayout.setLayoutParams(params);
        isSpread = !isSpread;
    }

    class ViewCompleteHolder {
        @ViewInject(R.id.tv_dialog_title)
        private TextView mTvDialogTile;
        @ViewInject(R.id.tv_copy)
        private TextView tvCopy;
        @ViewInject(R.id.rg)
        RadioGroup rg;
        @ViewInject(R.id.rb_yes)
        private RadioButton rbYes;
        @ViewInject(R.id.rb_no)
        private RadioButton rbNo;
        @ViewInject(R.id.tv_tips)
        private TextView tvTips;
        @ViewInject(R.id.btn_sure)
        private Button mBtnSure;
        @ViewInject(R.id.btn_cancel)
        private Button mBtnCancel;

        @Event({R.id.btn_sure, R.id.btn_cancel})
        private void OnViewClick(View view) {
            switch (view.getId()) {
                case R.id.btn_sure:
                    KeyBoardUtils.closeKeybord(_this);
                    try {
                        String checkRs = "";
                        checkRs = rbYes.isChecked() ? "正常" : "不正常";
                        processor.finishTask(currentTaskId, checkRs);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    isNeedUpdateTaskState = true;
                    Intent intent = new Intent(_this, CopyValueReportActivity.class);
                    startActivity(intent);
                    ScreenManager.getScreenManager().popActivity(CopyBaseDataActivity.class);
                    _this.finish();
                    break;
                case R.id.btn_cancel:
                    KeyBoardUtils.closeKeybord(_this);
                    tipsDialog.dismiss();
                    break;

                default:
                    break;
            }
        }
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


    class ViewHolder {
        private CopyResult result;
        private String remark;
        private CopyItem item;

        public ViewHolder(CopyResult result, View v, CopyItem item) {
            this.result = result;
            this.item = item;
        }

        @ViewInject(R.id.et_copy_values)
        EditText etInput;

        @Event({R.id.btn_sure, R.id.btn_cancel})
        private void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_cancel:
                    dialog.dismiss();
                    break;
                case R.id.btn_sure:
                    if (item.type_key.equalsIgnoreCase("youwei"))
                        result.valSpecial = "-1";
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
                    result.remark = TextUtils.isEmpty(etInput.getText().toString()) ? "" : (TextUtils.isEmpty(result.remark) ? etInput.getText().toString() + "," : result.remark + etInput.getText().toString() + ",");
                    dialog.dismiss();
                    copyViewUtil.createCopyView(_this, data, copyContainer);
                    break;
            }
        }
    }
}