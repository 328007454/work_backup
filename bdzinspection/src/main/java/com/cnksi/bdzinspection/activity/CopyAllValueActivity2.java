package com.cnksi.bdzinspection.activity;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.baidu.location.BDLocation;
import com.cnksi.bdloc.LocationListener;
import com.cnksi.bdloc.LocationUtil;
import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.FragmentPagerAdapter;
import com.cnksi.bdzinspection.daoservice.DefectRecordService;
import com.cnksi.bdzinspection.daoservice.LookupService;
import com.cnksi.bdzinspection.databinding.XsActivityCopyAll2Binding;
import com.cnksi.bdzinspection.databinding.XsActivityCopyDialogBinding;
import com.cnksi.bdzinspection.databinding.XsDialogTipsBinding;
import com.cnksi.bdzinspection.fragment.CopyValueFragment2;
import com.cnksi.bdzinspection.fragment.CopyValueFragment2.FragmentItemClickerListener;
import com.cnksi.bdzinspection.inter.CopyItemLongClickListener;
import com.cnksi.bdzinspection.inter.ItemClickListener;
import com.cnksi.bdzinspection.model.CopyItem;
import com.cnksi.bdzinspection.model.CopyResult;
import com.cnksi.bdzinspection.model.DefectRecord;
import com.cnksi.bdzinspection.model.Lookup;
import com.cnksi.bdzinspection.model.TreeNode;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.Config.LookUpType;
import com.cnksi.bdzinspection.utils.CopyHelper;
import com.cnksi.bdzinspection.utils.CopyViewUtil.KeyBordListener;
import com.cnksi.bdzinspection.utils.DefectUtils;
import com.cnksi.bdzinspection.utils.DialogUtils;
import com.cnksi.bdzinspection.utils.KeyBoardUtil;
import com.cnksi.bdzinspection.utils.KeyBoardUtil.OnKeyBoardStateChangeListener;
import com.cnksi.bdzinspection.utils.ScreenUtils;
import com.cnksi.bdzinspection.utils.ShowHistroyDialogUtils;
import com.cnksi.xscore.xsutils.CToast;
import com.cnksi.xscore.xsutils.KeyBoardUtils;
import com.zhy.core.AutoLinearLayout;
import com.zhy.core.utils.AutoUtils;

import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.List;

import static com.cnksi.bdzinspection.activity.NewDeviceDetailsActivity.UPDATE_DEVICE_DEFECT_REQUEST_CODE;


/**
 * 集中抄录
 *
 * @author lyndon
 */
public class CopyAllValueActivity2 extends BaseActivity implements OnPageChangeListener, FragmentItemClickerListener<DbModel>, KeyBordListener {

    public final int LOAD_COPY_FINISH = 0x10;
    protected int currentKeyBoardState = KeyBoardUtil.KEYBORAD_HIDE;
    boolean unchecked = true;
    LocationUtil.LocationHelper locationHelper;
    CopyHelper copyHelper;
    private List<Lookup> lookups;
    private ArrayList<CopyValueFragment2> fragmentList;
    private boolean isSpread = true;
    private List<TreeNode> data;
    private CopyValueFragment2 currentFragment;
    private BDLocation currentLocation;
    private int currentPosition;
    private boolean isFinish;

    // 抄录看不清弹出备注对话框
    private Dialog dialog;
    //点击下一步后时间
    private long mAfterTime;
    //点击下一步的累计次数
    private int clickIndex;

    //是否显示抄录大小于上下限值对话框标志
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
    //传递预设缺陷内容
    private String transDefectContent = "";
    private List<DefectRecord> mExistDefectList = new ArrayList<>();
    private Dialog defectDialog;
    private XsDialogTipsBinding tipsBinding;
    private XsActivityCopyAll2Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(currentActivity, R.layout.xs_activity_copy_all2);
        setDeviceListDisplay();
        initialUI();
        initLocation();
        initialData();
        initOnClick();
    }


    private void initialUI() {
        getIntentValue();
        createDefectDialog();
        copyHelper = new CopyHelper(currentActivity, currentReportId, currentBdzId, currentInspectionType);
        binding.tvTitle.setText(R.string.xs_copy_all_value_str);
        data = new ArrayList<>();

        copyHelper.setKeyBordListener(this);
        copyHelper.setItemLongClickListener(new CopyItemLongClickListener<CopyResult>() {
            @Override
            public void onItemLongClick(View v, final CopyResult result, int position, final CopyItem item) {
                final XsActivityCopyDialogBinding notClearDialogBinding = XsActivityCopyDialogBinding.inflate(getLayoutInflater());
                notClearDialogBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                notClearDialogBinding.btnSure.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveNotClearCopyInfo(result, notClearDialogBinding.etCopyValues, item);
                    }
                });
                dialog = DialogUtils.createDialog(currentActivity, notClearDialogBinding.getRoot(), LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                notClearDialogBinding.etCopyValues.setText(TextUtils.isEmpty(result.remark) ? "看不清" : result.remark.subSequence(0, result.remark.length()));
                //隐藏自定义键盘
                hideKeyBord();
                dialog.show();
            }
        });
        copyHelper.setItemClickListener(new ItemClickListener<CopyItem>() {
            @Override
            public void onItemClick(View v, CopyItem item, int position) {
                hideKeyBord();
                // 显示历史曲线
                ShowHistroyDialogUtils.showHistory(currentActivity, item);
            }
        });
        initFragment();
        binding.llKeyboardHelpLayout.setVisibility(View.GONE);
    }

    private void initialData() {
        for (CopyValueFragment2 fragment2 : fragmentList) {
            fragment2.setCopyDevice(copyHelper.getCopyDeviceIdList());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationHelper.resume();
        setWindowOverLayPermission();
    }

    private void initFragment() {
        lookups = LookupService.getInstance().findLookupByType(LookUpType.pmsDeviceType.name(), false);
        if (null != lookups && !lookups.isEmpty()) {
            fragmentList = new ArrayList<>();
            List<String> titleArray = new ArrayList<>();
            for (Lookup look : lookups) {
                CopyValueFragment2 fragment = new CopyValueFragment2();
                Bundle bundle = new Bundle();
                bundle.putString(Config.CURRENT_FUNCTION_MODEL, look.k);
                fragment.setArguments(bundle);
                titleArray.add(look.v);
                fragmentList.add(fragment);
                fragment.setItemClickerListener(this);
            }
            FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), fragmentList, titleArray);
            binding.viewPager.setAdapter(pagerAdapter);
            binding.tabStrip.setViewPager(  binding.viewPager);
            setPagerTabStripValue(binding.tabStrip);
            binding.tabStrip.setTabPaddingLeftRight(37);
            binding.tabStrip.setShouldExpand(false);
            binding.tabStrip.setOnPageChangeListener(this);
            binding.viewPager.setOffscreenPageLimit(titleArray.size());
        }
        currentFragment = fragmentList.get(  binding.viewPager.getCurrentItem());
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPosition = position;
        currentFragment = fragmentList.get(position);
    }

    private void initOnClick() {
        binding.cbSelected.setOnClickListener(view -> {
            if (!unchecked) {
                unchecked = true;
                currentFragment.setCheckDevice(false);
            } else {//未选中代表全部设备
                unchecked = false;
                currentFragment.setCheckDevice(true);
            }
        });
        binding.btnNext.setOnClickListener(view -> {
            if (isFinish) {
                finish();
            } else {
                if (!currentFragment.getAdapter().isLast()) {
                    currentFragment.getAdapter().next();
                } else {
                    // 保存当前数据，指向下一个fragment
                    if (currentPosition < fragmentList.size() - 1) {
                        copyHelper.saveAll();
                        binding.viewPager.setCurrentItem(currentPosition + 1);
                    } else {
                        binding.btnNext.setText(R.string.xs_finish_str);
                        isFinish = true;
                    }
                }
            }
        });
        binding.btnPre.setOnClickListener(view -> {
            // 不是列表中第一个，指向前一个设备,保存当前数据
            if (!currentFragment.getAdapter().isFirst()) {
                currentFragment.getAdapter().pre();
            } else {
                // 保存当前数据，指向前一个fragment
                if (currentPosition != 0) {
                    copyHelper.saveAll();
                    binding.viewPager.setCurrentItem(currentPosition - 1);
                }
            }
            // 如果从最后一个fragment的最后一个设备指向前一个设备更改完成按钮为下一个
            if (isFinish) {
                isFinish = false;
                binding.btnNext.setText(R.string.xs_next_str);
            }
        });
        binding.ibtnSpread.setOnClickListener(view -> {
            setDeviceListDisplay();
        });
        binding.ibtnCancel.setOnClickListener(view -> {
            finish();
        });
    }


    /**
     * 设置集中抄录数据设备列表的显示状态 展开 合拢
     */
    private void setDeviceListDisplay() {
        LinearLayout.LayoutParams params = (AutoLinearLayout.LayoutParams) binding.rlCopyAllValueContainer.getLayoutParams();
        if (!isSpread) {
            binding.ibtnSpread.setImageResource(R.drawable.xs_ic_narrow);
            binding.copyContainer.setVisibility(View.GONE);
            params.height = LinearLayout.LayoutParams.MATCH_PARENT;
        } else {
            binding.ibtnSpread.setImageResource(R.drawable.xs_ic_amplify);
            binding.copyContainer.setVisibility(View.VISIBLE);
            params.height = AutoUtils.getPercentHeightSizeBigger((int) getResources().getDimension(R.dimen.xs_copy_all_value_container_height_px));
        }
        binding.rlCopyAllValueContainer.setLayoutParams(params);
        isSpread = !isSpread;
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        locationHelper = LocationUtil.getInstance().getLocalHelper(new LocationListener() {
            @Override
            public void locationSuccess(BDLocation location) {
                currentLocation = location;
                copyHelper.judgeDistance(location, binding.shadom, binding.shadomTip);
            }
        });
        locationHelper.setKeep(true).start();
    }

    @Override
    public void onItemClick(String function, DbModel dbModel, int position) {
        if (isSpread) {
            setDeviceListDisplay();
        }
        if (function.equals(lookups.get(currentPosition).k) && !showShadom()) {
            if (null != dbModel) {
                // 保存前一个数据
                copyHelper.saveAll();
                // 切换新设备,默认保存当前界面值
                currentFragment.getAdapter().setCurrentSelectedPosition(position);
                if (copyHelper != null && null != copyHelper.device)
                    currentDeviceId = copyHelper.device.getString("deviceid");
                copyHelper.loadDevice(dbModel);
                // 加载新的抄录数据
                copyHelper.judgeDistance(currentLocation, binding.shadom, binding.shadomTip);
                loadCopyItem();
                currentFragment.setPosition(position);
            } else {
                data.clear();
                binding.copyContainer.removeAllViews();
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
            CToast.showLong(currentActivity, "不要作弊哟，5秒后继续操作。");
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

    private void loadCopyItem() {// 查询抄录标准
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                searchDefect();
                final List<TreeNode> newData = copyHelper.loadItem();
                // 设置当前抄录设备集合,判断当前设备是否抄录
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        data.clear();
                        data.addAll(newData);
                        mHandler.sendEmptyMessage(LOAD_COPY_FINISH);
                    }
                });
            }
        });
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_COPY_FINISH:
                if (!data.isEmpty()) {
                    copyHelper.createCopyView(this, data, binding.copyContainer);
                    currentFragment.getAdapter().notifyDataSetChanged();
                }
                break;

            default:
                break;
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
            if (null != defectDialog)
                defectDialog.show();
        } else {
        }
    }

    public void createDefectDialog() {
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
        if (null != copyHelper && null != copyHelper.device) {
            currentDeviceName = copyHelper.device.getString("name");
            currentDeviceId = copyHelper.device.getString("deviceid");
            currentSpacingName = copyHelper.device.getString("sname");
        }
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
    public void finish() {
        if (currentKeyBoardState == KeyBoardUtil.KEYBORAD_SHOW) {
            mKeyBoardUtil.hideKeyboard();
        }
        copyHelper.saveAll();
        setResult(RESULT_OK);
        super.finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideKeyBord();
    }


    @Override
    public void onViewFocus(EditText v, final CopyItem item, final CopyResult result, final List<EditText> editTexts, final List<CopyItem> copyItems) {

        if (item.type_key.contains("youwei")) {// 如果有油温抄录项则直接弹出系统键盘可以输入分数-----yangjun
            v.setRawInputType(InputType.TYPE_CLASS_NUMBER);
            KeyBoardUtils.openKeybord(v, getApplicationContext());
            hideKeyBord();
            binding.llKeyboardHelpLayout.setVisibility(View.GONE);
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
                                    binding.llKeyboardHelpLayout.setVisibility(View.GONE);
                                    break;
                                case KeyBoardUtil.KEYBORAD_SHOW: // 键盘显示
                                    binding.llKeyboardHelpLayout.setVisibility(View.VISIBLE);
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
                            binding.llKeyboardHelpLayout.setVisibility(View.GONE);
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
        if (null != mKeyBoardUtil && currentKeyBoardState == KeyBoardUtil.KEYBORAD_SHOW) {
            mKeyBoardUtil.hideKeyboard();
        }
    }

    private void saveNotClearCopyInfo(CopyResult result, View v, CopyItem item) {
        EditText etInput = (EditText) v;
        if ("youwei".equalsIgnoreCase(item.type_key))
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
        copyHelper.createCopyView(currentActivity, data, binding.copyContainer);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPDATE_DEVICE_DEFECT_REQUEST_CODE) {
            searchDefect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationHelper.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationHelper.destory();
    }
}
