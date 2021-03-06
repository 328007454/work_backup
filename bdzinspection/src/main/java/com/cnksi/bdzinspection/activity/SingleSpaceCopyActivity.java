package com.cnksi.bdzinspection.activity;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.baidu.location.BDLocation;
import com.cnksi.bdloc.LocationListener;
import com.cnksi.bdloc.LocationUtil;
import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.CopyRcvDeviceAdapter;
import com.cnksi.bdzinspection.adapter.base.GridSpacingItemDecoration;
import com.cnksi.bdzinspection.databinding.XsActivityCopyDialogBinding;
import com.cnksi.bdzinspection.databinding.XsActivitySingleSpaceCopyBinding;
import com.cnksi.common.model.TreeNode;
import com.cnksi.bdzinspection.utils.CopyHelper;
import com.cnksi.common.utils.CopyViewUtil;
import com.cnksi.common.Config;
import com.cnksi.common.base.BaseActivity;
import com.cnksi.common.daoservice.CopyItemService;
import com.cnksi.common.daoservice.CopyResultService;
import com.cnksi.common.daoservice.DefectRecordService;
import com.cnksi.common.databinding.CommonInspectionTipsBinding;
import com.cnksi.common.listener.ItemClickListener;
import com.cnksi.common.model.CopyItem;
import com.cnksi.common.model.CopyResult;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.model.Device;
import com.cnksi.common.utils.CopyKeyBoardUtil;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.common.utils.KeyBoardUtils;
import com.cnksi.common.utils.ShowCopyHistroyDialogUtils;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.defect.activity.AddDefectActivity;
import com.cnksi.defect.utils.DefectUtils;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.cnksi.bdzinspection.activity.NewDeviceDetailsActivity.UPDATE_DEVICE_DEFECT_REQUEST_CODE;
import static com.cnksi.common.Config.LOAD_DATA;

/**
 * author by kkk
 * Time: 2018/1/8
 * Description: 本类操作具有抄录设备的间隔，展示方式类似于全面/例行的集中抄录，但是不存在一次二次设备
 */
public class SingleSpaceCopyActivity extends BaseActivity implements ItemClickListener<DbModel>, CopyViewUtil.KeyBordListener {
    public final int LOAD_COPY_FINISH = 0x10;
    protected int currentKeyBoardState = CopyKeyBoardUtil.KEYBORAD_HIDE;
    private XsActivitySingleSpaceCopyBinding mCopyBinding;
    /**
     * 设备列表是否全部展开
     */
    private boolean isSpread = true;

    // 抄录看不清弹出备注对话框
    private Dialog dialog;
    //点击下一步后时间
    private long mAfterTime;
    //点击下一步的累计次数
    private int clickIndex;

    private List<TreeNode> data = new ArrayList<>();
    private LocationUtil.LocationHelper locationHelper;
    private BDLocation currentLocation;

    private List<DefectRecord> mExistDefectList = new ArrayList<>();
    private CopyRcvDeviceAdapter adapter;
    private List<DbModel> deviceData = new ArrayList<>();
    private int scrollPosition;
    private String spid;
    private boolean isFinish;

    private Dialog defectDialog;
    private CommonInspectionTipsBinding tipsBinding;

    //传递预设缺陷内容
    private String transDefectContent = "";
    private CopyHelper copyHelper;
    private CountDownTimer timer = new CountDownTimer(6000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            mCopyBinding.shadomTip.setText("" + millisUntilFinished / 1000);
            mCopyBinding.btnNext.setEnabled(false);
            mCopyBinding.btnPre.setEnabled(false);
        }

        @Override
        public void onFinish() {
            mCopyBinding.shadomTip.setVisibility(View.GONE);
            clickIndex = 0;
            mAfterTime = 0;
            mCopyBinding.btnNext.setEnabled(true);
            mCopyBinding.btnPre.setEnabled(true);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCopyBinding = DataBindingUtil.setContentView(mActivity, R.layout.xs_activity_single_space_copy);
        setDeviceListDisplay();
        initialUI();
        initialData();
    }

    private void initialData() {
        List<DbModel> deviceList = CopyItemService.getInstance().getCopyDevicebySpidList(currentBdzId, getIntent().getStringExtra(Config.CURRENT_FUNCTION_MODEL), currentInspectionType, spid);
        if (null != deviceList && !deviceList.isEmpty()) {
            deviceData.addAll(deviceList);
            mHandler.sendEmptyMessage(LOAD_DATA);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initLocation();
    }

    private void initialUI() {
        getIntentValue();
        copyHelper = new CopyHelper(mActivity, currentReportId, currentBdzId, currentInspectionType);
        mCopyBinding.llKeyboardHelpLayout.setVisibility(View.GONE);
        createDefectDialog();
        Intent intent = getIntent();
        String spaceName = intent.getStringExtra(Config.CURRENT_SPACING_NAME);
        spid = intent.getStringExtra(Config.CURRENT_SPACING_ID);
        mCopyBinding.includeTitle.tvTitle.setText(TextUtils.isEmpty(spaceName) ? "" : spaceName);
        adapter = new CopyRcvDeviceAdapter(deviceData, R.layout.select_device_item);
        adapter.setItemClickListener(this);
        mCopyBinding.rcv.setLayoutManager(new GridLayoutManager(mActivity, 2));
        mCopyBinding.rcv.addItemDecoration(new GridSpacingItemDecoration(2, 20, 8, true));
        adapter.bindToRecyclerView(mCopyBinding.rcv);
        mCopyBinding.includeTitle.ibtnCancel.setOnClickListener(view -> {
            KeyBoardUtils.closeKeybord(mActivity);
            finish();
        });
        mCopyBinding.ibtnSpread.setOnClickListener(view -> setDeviceListDisplay());
        mCopyBinding.shadomTip.setOnClickListener(v -> {

        });
        mCopyBinding.btnNext.setOnClickListener(view -> {
//                 点击完成
            if (isFinish) {
                finish();
            } else {
                if (!adapter.isLast()) {
                    adapter.next();
                } else {
                    copyHelper.saveAll();
                    mCopyBinding.btnNext.setText(R.string.xs_finish_str);
                    isFinish = true;
                }
            }
        });
        mCopyBinding.btnPre.setOnClickListener(view -> {
            // 不是列表中第一个，指向前一个设备,保存当前数据
            if (!adapter.isFirst()) {
                adapter.pre();
            } else {
                copyHelper.saveAll();
            }
            if (isFinish) {
                isFinish = false;
                mCopyBinding.btnNext.setText(R.string.xs_next_str);
            }
        });

        copyHelper.setKeyBordListener(this);
        copyHelper.setItemLongClickListener((v, result, position, item) -> {
            final XsActivityCopyDialogBinding notClearDialogBinding = XsActivityCopyDialogBinding.inflate(getLayoutInflater());
            notClearDialogBinding.btnCancel.setOnClickListener(v12 -> dialog.dismiss());
            notClearDialogBinding.btnSure.setOnClickListener(v1 -> saveNotClearCopyInfo(result, notClearDialogBinding.etCopyValues, item));
            dialog = DialogUtils.createDialog(mActivity, notClearDialogBinding.getRoot(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            notClearDialogBinding.etCopyValues.setText(TextUtils.isEmpty(result.remark) ? "看不清" : result.remark.subSequence(0, result.remark.length()));
            //隐藏自定义键盘
            hideKeyBord();
            dialog.show();
        });
        copyHelper.setItemClickListener((v, item, position) -> {
            SingleSpaceCopyActivity.this.hideKeyBord();
            // 显示历史曲线
            ShowCopyHistroyDialogUtils.showHistory(mActivity, item);
        });
    }

    private void saveNotClearCopyInfo(CopyResult result, View v, CopyItem item) {
        EditText etInput = (EditText) v;
        if ("youwei".equalsIgnoreCase(item.type_key)) {
            result.valSpecial = null;
        }
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
        } else if ("Y".equals(item.val_b) ) {
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
        copyHelper.createCopyView(mActivity, data, mCopyBinding.copyContainer);
    }

    /**
     * 设置集中抄录数据设备列表的显示状态 展开 合拢
     */
    private void setDeviceListDisplay() {
        final LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mCopyBinding.rlCopyAllValueContainer.getLayoutParams();
        if (!isSpread) {
            mCopyBinding.typeContainer.setVisibility(View.GONE);
            mCopyBinding.ibtnSpread.setImageResource(R.mipmap.icon_full_screen);
            params.height = LinearLayout.LayoutParams.MATCH_PARENT;
        } else {
            mCopyBinding.typeContainer.setVisibility(View.VISIBLE);
            mCopyBinding.ibtnSpread.setImageResource(R.mipmap.icon_full_screen);
            params.height = AutoUtils.getPercentHeightSizeBigger(330);
        }
        mCopyBinding.rlCopyAllValueContainer.setLayoutParams(params);
        isSpread = !isSpread;
    }

    private void loadCopyItem() {
        ExecutorManager.executeTask(() -> {
            searchDefect();
            final List<TreeNode> newData = copyHelper.loadItem();
            // 设置当前抄录设备集合,判断当前设备是否抄录
            HashSet<String> copyDeviceList = CopyResultService.getInstance().getCopyDeviceIdListIds(currentReportId, currentInspectionType);
            adapter.setCopyDeviceModel(copyDeviceList);
            mHandler.post(() -> {
                data.clear();
                data.addAll(newData);
                mHandler.sendEmptyMessage(LOAD_COPY_FINISH);
            });
        });
    }

    /**
     * 查询当前设备抄录项是否记录有上下限抄录缺陷
     */
    public void searchDefect() {
        mExistDefectList = DefectRecordService.getInstance().queryDefectByDeviceid(currentDeviceId, currentBdzId, currentReportId);
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_COPY_FINISH:
                if (!data.isEmpty()) {
                    copyHelper.createCopyView(this, data, mCopyBinding.copyContainer);
                    adapter.setCurrentSelectedPosition(scrollPosition);
                }
                break;
            case LOAD_DATA:
                adapter.notifyDataSetChanged();
                this.onClick(new View(this), deviceData.get(0), 0);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationHelper.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationHelper.destory();
    }

    /**
     * 初始化定位
     */
    private LocationListenenSub mListenenSub;

    private void initLocation() {
        locationHelper = LocationUtil.getInstance().getLocalHelper(mListenenSub = new LocationListenenSub());
        locationHelper.setKeep(true).start();
    }


    @Override
    public void onViewFocus(EditText v, CopyItem item, CopyResult result, final List<EditText> editTexts, final List<CopyItem> copyItems) {

        if (item.type_key.contains("youwei")) {// 如果有油温抄录项则直接弹出系统键盘可以输入分数-----yangjun
            v.setRawInputType(InputType.TYPE_CLASS_NUMBER);
            KeyBoardUtils.openKeybord(v, getApplicationContext());
            hideKeyBord();
            mCopyBinding.llKeyboardHelpLayout.setVisibility(View.GONE);
        } else {
            if (null == mKeyBoardUtil) {
                createKeyBoardView(mCopyBinding.llKeyboardHelpLayout);
                mKeyBoardUtil.setOnValueChangeListener(new CopyKeyBoardUtil.OnKeyBoardStateChangeListener() {
                    @Override
                    public void onKeyBoardStateChange(int state) {
                        if (currentKeyBoardState != state) {
                            currentKeyBoardState = state;
                            switch (state) {
                                // 键盘隐藏
                                case CopyKeyBoardUtil.KEYBORAD_HIDE:
                                    mCopyBinding.llKeyboardHelpLayout.setVisibility(View.GONE);
                                    break;
                                // 键盘显示
                                case CopyKeyBoardUtil.KEYBORAD_SHOW:
                                    mCopyBinding.llKeyboardHelpLayout.setVisibility(View.VISIBLE);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onKeyBoardNextInput(EditText editText) {
                        int position = editTexts.indexOf(editText);
                        if (position < editTexts.size() - 1) {
                            EditText nextEditText = editTexts.get(position + 1);
                            editText.clearFocus();
                            nextEditText.requestFocus();
                            onViewFocus(nextEditText, copyItems.get(position + 1), copyHelper.getCopyResultMap().get(copyItems.get(position + 1)), editTexts, copyItems);
                        } else {
                            mCopyBinding.llKeyboardHelpLayout.setVisibility(View.GONE);
                            hideKeyBord();
                        }
                    }
                });
            }
            KeyBoardUtils.closeKeybord(v, getApplicationContext());
            mKeyBoardUtil.setCurrentEditText(v, item.min, item.max, item.description + item.unit);
            mKeyBoardUtil.showKeyboard();
            showCursor(v);
        }
    }

    @Override
    public void hideKeyBord() {
        if (null != mKeyBoardUtil && currentKeyBoardState == CopyKeyBoardUtil.KEYBORAD_SHOW) {
            mKeyBoardUtil.hideKeyboard();
        }
    }

    @Override
    public void onViewFocusChange(EditText v, CopyItem item, CopyResult result, boolean hasFocus, String descript, List<EditText> editTexts) {
        String val = v.getText().toString().trim();
        if (hasFocus || TextUtils.isEmpty(val)) {

            return;
        }
        List<String> rs = new ArrayList<>();
        if (DefectUtils.calcCopyBound(item, copyHelper.getCopyResultMap().get(item.id), val, mExistDefectList, rs)) {
            tipsBinding.tvDialogContent.setText(rs.get(1));
            transDefectContent = rs.get(0);
            if (null != defectDialog) {
                defectDialog.show();
            }
        } else {
        }
    }

    public void createDefectDialog() {
        int dialogWidth = ScreenUtils.getScreenWidth(mActivity) * 7 / 9;
        tipsBinding = CommonInspectionTipsBinding.inflate(getLayoutInflater());
        defectDialog = DialogUtils.createDialog(mActivity, tipsBinding.getRoot(), dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        tipsBinding.tvDialogTitle.setText("警告");
        tipsBinding.btnCancel.setText("否");
        tipsBinding.btnSure.setText("是");
        tipsBinding.btnCancel.setOnClickListener(v -> defectDialog.dismiss());
        tipsBinding.btnSure.setOnClickListener(v -> {
            SingleSpaceCopyActivity.this.hideKeyBord();
            defectDialog.dismiss();
            if (currentKeyBoardState == CopyKeyBoardUtil.KEYBORAD_SHOW) {
                mKeyBoardUtil.hideKeyboard();
            }
            Intent intent = new Intent(mActivity, AddDefectActivity.class);
            intent.putExtra(Config.HAS_ALL_CHOICE, false);
            intent.putExtra(Config.NO_DEVICE_PART,true);
            intent.putExtra(Config.DEFECT_CONTENT,transDefectContent);
            intent.putExtra(Device.DTID, copyHelper.device.getString("dtid"));
            SingleSpaceCopyActivity.this.setIntentValue(intent);
            SingleSpaceCopyActivity.this.startActivityForResult(intent, UPDATE_DEVICE_DEFECT_REQUEST_CODE);
        });
    }

    /**
     * 设置需要传递的值
     *
     * @param intent
     */
    private void setIntentValue(Intent intent) {
        currentDeviceName = copyHelper.device.getString("name");
        currentDeviceId = copyHelper.device.getString("deviceid");
        intent.putExtra(Config.DEFECT_COUNT_KEY, transDefectContent);
        intent.putExtra(Config.CURRENT_DEVICE_ID, currentDeviceId);
        intent.putExtra(Config.CURRENT_DEVICE_NAME, currentDeviceName);
        intent.putExtra(Config.CURRENT_DEVICE_PART_ID, currentDevicePartId);
        intent.putExtra(Config.CURRENT_DEVICE_PART_NAME, currentDevicePartName);
        intent.putExtra(Config.CURRENT_SPACING_ID, currentSpacingId);
        intent.putExtra(Config.CURRENT_SPACING_NAME, currentSpacingName);
        intent.putExtra(Config.IS_PARTICULAR_INSPECTION, isParticularInspection);
    }

    @Override
    public void onClick(View v, DbModel dbModel, int position) {

        if (isSpread) {
            setDeviceListDisplay();
        }
        if (!showShadom()) {
            if (null != dbModel) {
                // 保存前一个数据
                copyHelper.saveAll();
                // 切换新设备,默认保存当前界面值
                scrollPosition = position;
                currentDeviceId = ((DbModel) dbModel).getString("deviceid");
                copyHelper.loadDevice((DbModel) dbModel);
                // 加载新的抄录数据
                copyHelper.judgeDistance(currentLocation, mCopyBinding.shadomTip, mCopyBinding.shadomTip);
                loadCopyItem();
            } else {
                data.clear();
                mCopyBinding.copyContainer.removeAllViews();
            }
        }
    }

    public boolean showShadom() {
        long mCurrentTime = System.currentTimeMillis();
        if (0 == clickIndex) {
            mAfterTime = mCurrentTime;
        }
        long diffTime = mCurrentTime - mAfterTime;
        clickIndex++;
        if (1000 >= diffTime && 3 <= clickIndex) {
            mCopyBinding.shadomTip.setVisibility(View.VISIBLE);
            mCopyBinding.shadomTip.setTextSize(120);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_DEVICE_DEFECT_REQUEST_CODE) {
            searchDefect();
        }
    }

    @Override
    public void finish() {
        if (currentKeyBoardState == CopyKeyBoardUtil.KEYBORAD_SHOW) {
            mKeyBoardUtil.hideKeyboard();
        }
        if (copyHelper != null) {
            copyHelper.saveAll();
        }
        setResult(RESULT_OK);
        super.finish();
    }

    public class LocationListenenSub extends LocationListener {

        @Override
        public void locationSuccess(BDLocation location) {
            currentLocation = location;
            copyHelper.judgeDistance(currentLocation, mCopyBinding.shadomTip, mCopyBinding.shadomTip);
        }
    }

}

