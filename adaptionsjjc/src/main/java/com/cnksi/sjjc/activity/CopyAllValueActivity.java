package com.cnksi.sjjc.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cnksi.core.common.ScreenManager;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.CopyValueElvAdapter;
import com.cnksi.sjjc.adapter.FragmentPagerAdapter;
import com.cnksi.sjjc.bean.DefectRecord;
import com.cnksi.sjjc.bean.Device;
import com.cnksi.sjjc.bean.DevicePart;
import com.cnksi.sjjc.bean.DeviceStandards;
import com.cnksi.sjjc.bean.Lookup;
import com.cnksi.sjjc.bean.Spacing;
import com.cnksi.sjjc.fragment.CopyValueFragment;
import com.cnksi.sjjc.processor.CopyDataInterface;
import com.cnksi.sjjc.processor.ProcessorFactory;
import com.cnksi.sjjc.service.BaseService;
import com.cnksi.sjjc.service.DeviceService;
import com.cnksi.sjjc.util.DefectLevelUtils;
import com.cnksi.sjjc.util.DialogUtils;
import com.cnksi.sjjc.util.TTSUtils;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 集中抄录数据 (集中抄录设备) 从设备列表界面跳转过来
 *
 * @author terry
 */
public class CopyAllValueActivity extends BaseActivity implements OnPageChangeListener, CopyValueFragment.OnItemClickedListener,
        com.cnksi.sjjc.adapter.CopyValueElvAdapter.OnAdapterViewClickListener {

    private static final int LOAD_NEXT_COPY_DATA = 0X111;
    private static final int LOAD_PREVIOUS_COPY_DATA = LOAD_NEXT_COPY_DATA + 1;
    private static final int CLICK_FINISH_CODE = LOAD_PREVIOUS_COPY_DATA + 1;
    private static final int MODIFY_DATA_ALL = CLICK_FINISH_CODE + 1;
    private static final int CLICK_ITEM_CODE = MODIFY_DATA_ALL + 1;
    private static final int LONG_CLICK_ITEM_CODE = CLICK_ITEM_CODE + 1;
    public final static int LOAD_COPY_MAP = LONG_CLICK_ITEM_CODE + 1;
    public final static int CLICK_COMPLETE_CODE = LOAD_COPY_MAP + 1;

    @ViewInject(R.id.ll_root_container)
    private LinearLayout mLLRootContainer;
    @ViewInject(R.id.tv_title)
    private TextView mTvTitle;
    @ViewInject(R.id.viewPager)
    private ViewPager mViewPager;
    @ViewInject(R.id.rl_copy_all_value_container)
    private RelativeLayout mRlCopyAllValueContainer;
    @ViewInject(R.id.ll_keyboard_help_layout)
    private LinearLayout mLLKeyBoardHelpLayout;

    @ViewInject(R.id.btn_pre)
    private Button mBtnPre; // 上一个按钮

    @ViewInject(R.id.btn_next)
    private Button mBtnNext; // 下一个按钮

    @ViewInject(R.id.lv_pinned_container)
    private ExpandableListView mPlvContainer;

    /**
     * 放大缩小
     */
    @ViewInject(R.id.ibtn_spread)
    private ImageButton mIBtnSpread;
    /**
     * 是否是放大
     */
    private boolean isSpread = false;

    private FragmentPagerAdapter fragmentPagerAdapter = null;
    private ArrayList<Fragment> mFragmentList = null;

    private CopyValueElvAdapter mCopyValueAdapter = null;
    private LinkedList<DevicePart> groupList = new LinkedList<DevicePart>();
    ;
    private HashMap<DevicePart, ArrayList<DbModel>> groupHashMap = new HashMap<DevicePart, ArrayList<DbModel>>();

    private Dialog mReferenceValueDialog = null;
    private ViewSourceHolder mSourceHolder = null;
    /**
     * 当前选中的位置
     */
    private int currentSelectedPosition = 0;

    /**
     * Fregment中设备的索引
     */
    private int devciePosition = 0;

    /**
     * 电压等级
     */
    private List<Lookup> pmsDeviceTypeList = null;
    /**
     * 是否是事故后，验收，新投
     */
    private boolean isAcceptAccidentNewProduct = false;
    private String deviceIdList = null;

    private DbModel mCurrentDevice = null;
    /**
     * 当前键盘的状态
     */
//	private int currentKeyBoardState = KeyBoardUtil.KEYBORAD_HIDE;

    /**
     * 历史数据图片
     */
    private Dialog mHistoryDialog = null;
    private ViewHolder holder = null;
//    /**
//     * 是否显示了提示记录缺陷的Dialog
//     */
//    private boolean isShowTipsDialog = false;
    /**
     * 当前获取焦点的对象
     */
    private DbModel mCurrentDbModel = null;
    /**
     * 语音内容
     */
    private String speakContent = "";
    /**
     * 是否点击完成
     */
//    private boolean isClickFinish = false;
//    /**
//     * 是否下一个
//     */
//    private boolean isNext = false;

    // 是否记录得有缺陷（之前最初为false，由于后来不需要记录缺陷，直接改为true，保存才能成功）
    private boolean isRecordDefect = true;

    // 当前修改的设备
    private DbModel device;
    /***
     * 过程处理器
     */
    CopyDataInterface processor;

    public HashMap<String, Boolean> copyMap;
    protected Dialog tipsDialog = null;
    protected ViewCompleteHolder tipsholder = null;

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public void onPanelClosed(int featureId, Menu menu) {
        super.onPanelClosed(featureId, menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setChildView(R.layout.activity_copy_all_value);
        getIntentValue();
        processor = ProcessorFactory.getProcessor(currentInspectionType, currentReportId);
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DeviceService.getInstance().recoverCopyData(currentBdzId);
            }
        });
        initUI();

    }

    private void initUI() {
        deviceIdList = getIntent().getStringExtra(Config.ALL_DEVICE_ID_LIST);
        mTvTitle.setText(currentInspectionName + "记录");
        tvRight.setText(processor.getFinishString());
        tvRight.setVisibility(View.VISIBLE);
        tvRight.setBackgroundResource(R.drawable.red_button_background_selector);
//        if ((mTts = SpeechSynthesizer.getSynthesizer()) == null) {
//            // 初始化语音
//            initSpeech(mCurrentActivity);
//        }

        initFragmentList();
    }

    private void initFragmentList() {
        if (mFragmentList == null) {
            mFragmentList = new ArrayList<Fragment>();
        }
        pmsDeviceTypeList = processor.findPmsDeviceType();
        mFragmentList = new ArrayList<Fragment>();
        List<String> titleArray = new ArrayList<String>();
        int count = pmsDeviceTypeList.size();
        for (int i = 0; i < count; i++) {
            CopyValueFragment mFragment = new CopyValueFragment();
            mFragment.setOnItemClickedListener(this);
            mFragment.setProcessor(processor);
            Bundle args = new Bundle();
            args.putString(Config.CURRENT_FUNCTION_MODEL, pmsDeviceTypeList.get(i).k);
            args.putString(Config.ALL_DEVICE_ID_LIST, deviceIdList);
            mFragment.setArguments(args);
            mFragmentList.add(mFragment);
            titleArray.add(pmsDeviceTypeList.get(i).v);
        }
        fragmentPagerAdapter = new FragmentPagerAdapter(mFragmentManager, mFragmentList, titleArray);
        mViewPager.setAdapter(fragmentPagerAdapter);
        mViewPager.setOffscreenPageLimit(count);
        mPlvContainer.setOnGroupClickListener(new OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        queryCopyMap();
    }


    public void queryCopyMap() {
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                copyMap = processor.getCopyMap(
                        currentBdzId, pmsDeviceTypeList.get(currentSelectedPosition).k);
                if (copyMap == null)
                    copyMap = new HashMap<String, Boolean>();
                mHandler.sendEmptyMessage(LOAD_COPY_MAP);
            }
        });
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                if (mCurrentDevice == null) {
                    break;
                }
                if (mCopyValueAdapter == null) {
                    mCopyValueAdapter = new CopyValueElvAdapter(_this, mCurrentDevice.getString(Device.DEVICEID));
                    mPlvContainer.setAdapter(mCopyValueAdapter);
                    mCopyValueAdapter.setOnAdapterViewClickListener(this);
                }
                mCopyValueAdapter.setIsUpdate(true);
                mCopyValueAdapter.setGroupList(groupList);
                mCopyValueAdapter.setGroupMap(groupHashMap);
                mCopyValueAdapter.setCurrentDeviceId(mCurrentDevice.getString(Device.DEVICEID));
                if (groupList != null && !groupList.isEmpty()) {
                    for (int i = 0, count = groupList.size(); i < count; i++) {
                        mPlvContainer.expandGroup(i);
                    }
                }
                mBtnNext.setEnabled(true);
                mBtnPre.setEnabled(true);
                if (msg.obj != null && msg.obj instanceof Boolean && Boolean.parseBoolean(msg.obj.toString())) {
                    setDeviceListDisplay();
                } else {
//				if (mKeyBoardUtil != null) {
//					mKeyBoardUtil.hideKeyboard();
//				}
                }
                break;
            case LOAD_PREVIOUS_COPY_DATA:
                ((CopyValueFragment) mFragmentList.get(currentSelectedPosition)).loadFirstDeviceCopyData(devciePosition - 1,
                        true);
                break;
            case LOAD_NEXT_COPY_DATA:
                ((CopyValueFragment) mFragmentList.get(currentSelectedPosition)).loadFirstDeviceCopyData(devciePosition + 1,
                        true);
                break;
            case INIT_SPEECH:
                // 读取内容
                TTSUtils.getInstance().startSpeaking(speakContent);
                break;
            case CLICK_FINISH_CODE:
                this.finish();
                break;
            case LONG_CLICK_ITEM_CODE:
                // TODO: 编辑抄录标准
//			if (mCurrentDbModel != null) {
//				mVibrator.vibrate(100);
//				Intent intent = new Intent(_this, ChangeCopyValueActivity.class);
//				intent.putExtra(Config.COPY_DESCRIPTION, mCurrentDbModel.getString(DeviceStandards.DESCRIPTION));
//				intent.putExtra(Config.CURRENT_STANDARD_ID, mCurrentDbModel.getString(DeviceStandards.STAID));
//				startActivityForResult(intent, MODIFY_DATA_ALL);
//			}
                break;
            case LOAD_COPY_MAP:
                ((CopyValueFragment) mFragmentList.get(currentSelectedPosition))
                        .setCopyedMap(copyMap);
                break;
            case CLICK_COMPLETE_CODE:
                String tip = String.format(getText(R.string.dialog_tips_finish_str) + "", processor.getCopyResult(currentBdzId));
                showTipsDialog(rootContainer, tip);
                break;
            default:
                break;
        }
    }

    @Event({R.id.btn_back, R.id.ibtn_spread, R.id.btn_next, R.id.btn_pre, R.id.tv_right})
    private void OnViewClick(View view) {
        switch (view.getId()) {
            case R.id.tv_right:
                saveOrUpdateCopyValue(CLICK_COMPLETE_CODE);
                break;
            case R.id.ibtn_spread:
                setDeviceListDisplay();
                break;
            case R.id.btn_next: // 点击下一个
                mBtnNext.setEnabled(false);
                saveOrUpdateCopyValue(LOAD_NEXT_COPY_DATA);
                break;
            case R.id.btn_pre: // 点击上一个
                mBtnPre.setEnabled(false);
                saveOrUpdateCopyValue(LOAD_PREVIOUS_COPY_DATA);
                break;
            default:
                break;
        }
    }

    /**
     * 设置集中抄录数据设备列表的显示状态 展开 合拢
     */
    private void setDeviceListDisplay() {
        LayoutParams params = (LayoutParams) mRlCopyAllValueContainer.getLayoutParams();
        if (isSpread = !isSpread) {
            mIBtnSpread.setImageResource(R.mipmap.ic_narrow);
            mPlvContainer.setVisibility(View.GONE);
            params.height = LayoutParams.MATCH_PARENT;
        } else {
            ((CopyValueFragment) mFragmentList.get(currentSelectedPosition)).setCurrentSelection();
            mIBtnSpread.setImageResource(R.mipmap.ic_amplify);
            mPlvContainer.setVisibility(View.VISIBLE);
            params.height = getResources().getDimensionPixelSize(R.dimen.copy_all_value_container_height);
        }
        mRlCopyAllValueContainer.setLayoutParams(params);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentSelectedPosition = position;
        if (mFragmentList != null) {
            Fragment mFragment = mFragmentList.get(position);
            if (mFragment instanceof CopyValueFragment) {
                ((CopyValueFragment) mFragment).loadFirstDeviceCopyData(0, false);
            }
        }
    }

    @Override
    public void OnAdapterViewClick(View view, DbModel mStandardModel) {
        // TODO: 点击抄录数据的Tips
        switch (view.getId()) {
            case R.id.tv_copy_content:

                if (!TextUtils.isEmpty(mStandardModel.getString(DeviceStandards.REFERENCE))) {
                    showReferenceValueDialog(mStandardModel.getString(DeviceStandards.REFERENCE));
                }

                break;
            case R.id.ibtn_history_data:
                processor.showHistoryDialog(_this, mStandardModel, mCurrentDevice, currentBdzId);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean OnShowKeyboard(EditText et, MotionEvent event, DbModel mDbModel) {
        return false;
    }

    /**
     * 显示历史曲线图
     *
     * @param title
     */
    private void showHistoryDataDialog(String title) {
        int dialogWidth = ScreenUtils.getScreenWidth(_this) * 9 / 10;
        if (mHistoryDialog == null) {
            mHistoryDialog = DialogUtils.createDialog(_this, mLLRootContainer, R.layout.history_data_dialog,
                    holder == null ? holder = new ViewHolder() : holder, dialogWidth,
                    LayoutParams.WRAP_CONTENT);
        }
        holder.mTvTitle.setText(title);
        holder.mBtnCancel.setText(R.string.back_str);
        holder.mIvHistoryImage.setImageResource(getImageResourceId(title));
        mHistoryDialog.show();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                holder.mHSVHistoryData.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
            }
        });
    }

    class ViewHolder {

        @ViewInject(R.id.tv_dialog_title)
        private TextView mTvTitle;

        @ViewInject(R.id.btn_cancel)
        private Button mBtnCancel;

        @ViewInject(R.id.hsv_history_data)
        private HorizontalScrollView mHSVHistoryData;

        @ViewInject(R.id.iv_image)
        private ImageView mIvHistoryImage;

        @Event({R.id.btn_cancel})
        private void OnViewClick(View view) {
            switch (view.getId()) {
                case R.id.btn_cancel:
                    mHistoryDialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    }

    private int getImageResourceId(String title) {
        int imageResourceId = R.mipmap.test_historydata;
        if (title.contains("变压器油位")) {
            imageResourceId = R.mipmap.test_bianyaqiyouwei;
        } else if (title.contains("泄漏电流")) {
            imageResourceId = R.mipmap.test_xieloudianliu;
        } else if (title.contains("氮气瓶压力")) {
            imageResourceId = R.mipmap.test_danqipingyali;
        } else if (title.contains("氮气管道压力")) {
            imageResourceId = R.mipmap.test_danqiguandaoyali;
        } else if (title.contains("控母电压")) {
            imageResourceId = R.mipmap.test_kongmudianya;
        } else if (title.contains("合母电压")) {
            imageResourceId = R.mipmap.test_hemudianya;
        } else if (title.contains("交流输入电压")) {
            imageResourceId = R.mipmap.test_jiaoliushurudianya;
        }
        return imageResourceId;
    }

    /**
     * 数据参考范围dialog
     *
     * @param source
     */
    private void showReferenceValueDialog(String source) {
        if (mReferenceValueDialog == null) {
            int dialogWidth = ScreenUtils.getScreenWidth(_this) * 9 / 10;
            mReferenceValueDialog = DialogUtils.createDialog(_this, mLLRootContainer,
                    R.layout.dialog_standard_source,
                    mSourceHolder == null ? mSourceHolder = new ViewSourceHolder() : mSourceHolder, dialogWidth,
                    LayoutParams.WRAP_CONTENT, true);
        }
        mSourceHolder.mTvDialogTile.setText(R.string.reference_value_str);
        mSourceHolder.mTvSourceName.setText(source);
        mReferenceValueDialog.show();
    }

    class ViewSourceHolder {
        @ViewInject(R.id.tv_dialog_title)
        private TextView mTvDialogTile;

        @ViewInject(R.id.tv_source_name)
        private TextView mTvSourceName;
    }

    @Override
    public void OnItemClicked(DbModel mDevice, int position) {

        devciePosition = position;
        isSpread = true;
        device = mDevice;

        loadDataFinish(mDevice, true);
    }

    /**
     * 等设备列表加载完成了之后 再查询第一个设备的抄录数据
     */
    @Override
    public void loadDataFinish(final DbModel mDevice, final boolean isOnItemClicked) {
        if (isOnItemClicked) {
            saveOrUpdateCopyValue(CLICK_ITEM_CODE);
        }
        groupList.clear();
        groupHashMap.clear();
        if (mDevice != null) {
            mCurrentDevice = mDevice;
            mFixedThreadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        // 1、从库中查询设备部件
                        List<DevicePart> mDevicePartList = processor.findDevicePart(mCurrentDevice);
                        // 从部件中查询设备抄录标准(只读取库中)
                        if (mDevicePartList != null) {
                            for (int i = 0, count = mDevicePartList.size(); i < count; i++) {
                                DevicePart mDevicePart = mDevicePartList.get(i);
                                List<DbModel> mStandardList = processor.findCopyStandardListByDevicePartId(mCurrentDevice.getString(Device.DEVICEID), mDevicePart.duid);
                                if (mStandardList != null && !mStandardList.isEmpty()) {
                                    groupList.add(mDevicePart);
                                    groupHashMap.put(mDevicePart, new ArrayList<DbModel>(mStandardList));
                                }
                            }
                            mHandler.sendMessage(mHandler.obtainMessage(LOAD_DATA, isOnItemClicked));
                            // mHandler.sendEmptyMessage(LOAD_DATA,isOnItemClicked);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            mHandler.sendEmptyMessage(LOAD_DATA);
        }
    }

    /**
     * 保存\更新 抄录数据
     */
    private synchronized void saveOrUpdateCopyValue(final int messageCode) {
        //判断是否抄录完毕
        boolean isFinishCopy = true;
        if (groupList != null && !groupList.isEmpty()) {
            for (DevicePart mDevicePart : groupList) {
                List<DbModel> dataList = groupHashMap.get(mDevicePart);
                if (dataList != null && !dataList.isEmpty()) {
                    List<DefectRecord> saveList = new ArrayList<>();
                    for (DbModel m : dataList) {

                        String value = m.getString(DefectRecord.VAL) == null ? ""
                                : m.getString(DefectRecord.VAL);
                        if (TextUtils.isEmpty(value)) {
                            isFinishCopy = false;
                        }
                        if (!TextUtils.isEmpty(m.getString(DefectRecord.DEFECTID))) {
                            DefectRecord record = new DefectRecord(
                                    TextUtils.isEmpty(value) ? "" : currentReportId,
                                    TextUtils.isEmpty(value) ? "" : currentBdzId, currentBdzName, m);
                            m.add(DefectRecord.DEFECTID, record.defectid);
                            saveList.add(record);
                        } else {
                            if (!TextUtils.isEmpty(value)) {
                                DefectRecord record = new DefectRecord(currentReportId, currentBdzId,
                                        currentBdzName, m);
                                m.add(DefectRecord.DEFECTID, record.defectid);
                                saveList.add(record);
                            }
                        }
                    }
                    try {
                        BaseService.getInstance(DefectRecord.class).saveOrUpdate(saveList);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (mCurrentDevice != null) {
                copyMap.put(mCurrentDevice.getString(Device.DEVICEID), isFinishCopy);
            }
        }
        mHandler.sendEmptyMessage(messageCode);

        if (messageCode == LOAD_NEXT_COPY_DATA || messageCode == LOAD_PREVIOUS_COPY_DATA || messageCode == CLICK_ITEM_CODE) {
            mHandler.sendEmptyMessage(LOAD_COPY_MAP);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }


    @Override
    public void OnViewFocusChange(View view, boolean hasFocus, DbModel mDbModel, int groupPosition, int childPosition) {

        judgementInputValue(mDbModel);
    }

    @Override
    public void updateSmartKeyBoard(EditText mEditText, DbModel mDbModel) {

    }

    /**
     * 判断输入的值是否在正常范围内
     *
     * @param mStandardModel
     */
    private boolean judgementInputValue(DbModel mStandardModel) {
        //
        // if (mStandardModel.getBoolean(DeviceStandards.HAS_RECORD_DEFECT)
        // || TextUtils.isEmpty(mStandardModel.getString(DefectRecord.VAL))
        // || TextUtils.isEmpty(mStandardModel.getString(DeviceStandards.WTC))
        // || TextUtils.isEmpty(mStandardModel.getString(DeviceStandards.WTLL)))
        // {
        // return true;
        // }
        // try {
        // Float inputValue =
        // Float.parseFloat(mStandardModel.getString(DefectRecord.VAL));
        // Float theBig =
        // Float.parseFloat(mStandardModel.getString(DeviceStandards.WTC));
        // Float theLittle =
        // Float.parseFloat(mStandardModel.getString(DeviceStandards.WTLL));
        // if (inputValue > theBig) {
        // showDefectTipsDialog(DefectLevelUtils.getDefectLevel(theBig,
        // theLittle, inputValue, true),
        // mStandardModel, true);
        // return false;
        // } else if (inputValue < theLittle) {
        // showDefectTipsDialog(DefectLevelUtils.getDefectLevel(theBig,
        // theLittle, inputValue, false),
        // mStandardModel, false);
        // return false;
        // }
        //
        // if (mKeyBoardUtil != null) {
        // mKeyBoardUtil.hideKeyboard();
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // return true;
        // }
        return true;
    }

    /**
     * 完成巡检提示框
     *
     * @param mRootContainer
     */
    protected void showTipsDialog(ViewGroup mRootContainer, CharSequence copytips) {
        int dialogWidth = ScreenUtils.getScreenWidth(_this) * 9 / 10;
        int dialogHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
        if (tipsDialog == null) {
            tipsDialog = DialogUtils.createDialog(_this, mRootContainer, R.layout.dialog_copy_tips,
                    tipsholder == null ? tipsholder = new ViewCompleteHolder() : tipsholder, dialogWidth, dialogHeight, false);
        }
        tipsholder.tvCopy.setText(copytips);
        if (processor.isHasCheckResult()) {
            tipsholder.rg.setVisibility(View.VISIBLE);
            tipsholder.rbYes.setChecked(true);
        }
        tipsholder.tvTips.setText("是否完成检查？");
        tipsDialog.show();
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
                case R.id.btn_cancel:
                    tipsDialog.dismiss();
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        saveOrUpdateCopyValue(CLICK_FINISH_CODE);
    }

    /**
     * 保存一条缺陷
     *
     * @param defectLevel
     * @param defectContent
     */
    private void saveDefect(DbModel mStandardModel, String defectLevel, String defectContent) {
        defectLevel = DefectLevelUtils.convertDefectLevel2Code(defectLevel);
        DefectRecord record = new DefectRecord(currentReportId, // 报告id
                currentBdzId, // 变电站id
                currentBdzName, // 变电站名称
                mStandardModel.getString(Spacing.SPID), // 间隔ID
                mStandardModel.getString(Spacing.SNAME), // 间隔名称
                mStandardModel.getString(Device.DEVICEID), // 设备id
                mStandardModel.getString(Device.DNAME), // 设备名称
                mStandardModel.getString(DeviceStandards.DUID), // 设备部件id
                mStandardModel.getString(DevicePart.NAME), // 设备部件名称
                defectLevel, // 缺陷级别
                defectContent, // 缺陷描述
                mStandardModel.getString(DeviceStandards.STAID), // 巡视标准id
                ""// pics图片
        );
        try {
            BaseService.getInstance(DefectRecord.class).saveOrUpdate(record);
            isRecordDefect = true;
        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void OnAdapterViewLongClick(View view, DbModel mDbModel) {
        mCurrentDbModel = mDbModel;
        saveOrUpdateCopyValue(LONG_CLICK_ITEM_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case MODIFY_DATA_ALL:
                    mViewPager.setCurrentItem(currentSelectedPosition);
                    OnItemClicked(device, devciePosition);
                    break;

                default:
                    break;
            }
        }
    }
}
