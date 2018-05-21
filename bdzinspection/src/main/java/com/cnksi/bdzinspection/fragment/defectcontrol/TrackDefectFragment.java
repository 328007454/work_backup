package com.cnksi.bdzinspection.fragment.defectcontrol;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.defectcontrol.DefectContentAdapter;
import com.cnksi.bdzinspection.adapter.defectcontrol.HistoryDefectAdapter;
import com.cnksi.bdzinspection.adapter.defectcontrol.HistoryDefectAdapter.OnAdapterViewClickListener;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.daoservice.DefectDefineService;
import com.cnksi.bdzinspection.daoservice.DefectRecordService;
import com.cnksi.bdzinspection.daoservice.DeviceService;
import com.cnksi.bdzinspection.databinding.XsDialogDefectSourceBinding;
import com.cnksi.bdzinspection.databinding.XsFragmentRecordDefectContentDialogBinding;
import com.cnksi.bdzinspection.databinding.XsFragmentTrackDefectBinding;
import com.cnksi.bdzinspection.fragment.BaseFragment;
import com.cnksi.bdzinspection.model.CopyItem;
import com.cnksi.bdzinspection.model.CopyResult;
import com.cnksi.bdzinspection.model.DefectDefine;
import com.cnksi.bdzinspection.model.DefectRecord;
import com.cnksi.bdzinspection.model.TreeNode;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.CopyHelper;
import com.cnksi.bdzinspection.utils.CopyViewUtil;
import com.cnksi.bdzinspection.utils.DialogUtils;
import com.cnksi.bdzinspection.utils.FunctionUtil;
import com.cnksi.bdzinspection.utils.PlaySound;
import com.cnksi.bdzinspection.utils.ShowHistroyDialogUtils;
import com.cnksi.xscore.xsutils.BitmapUtil;
import com.cnksi.xscore.xsutils.CToast;
import com.cnksi.xscore.xsutils.CoreConfig;
import com.cnksi.xscore.xsutils.DateUtils;
import com.cnksi.xscore.xsutils.KeyBoardUtils;
import com.cnksi.xscore.xsutils.PreferencesUtils;
import com.cnksi.xscore.xsutils.ScreenUtils;
import com.cnksi.xscore.xsutils.StringUtils;
import com.zhy.core.utils.AutoUtils;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 跟踪缺陷
 *
 * @author terry
 */
@SuppressLint("ClickableViewAccessibility")
public class TrackDefectFragment extends BaseFragment implements OnAdapterViewClickListener, com.cnksi.bdzinspection.adapter.defectcontrol.DefectContentAdapter.OnAdapterViewClickListener, CopyViewUtil.KeyBordListener {
    public final int LOAD_COPY_FINSIH = 0x10;
    public static final int TRACK_DEFECT = 0X100;


    /**
     * 当前缺陷图片的内容
     */
    private String currentImageName = "";
    /**
     * 当前的缺陷内容
     */
    private String currentPictureContent = "";
    /**
     * 缺陷照片的集合
     */
    private ArrayList<String> mDefectImageList = new ArrayList<String>();

    private List<DefectRecord> dataList;

    private List<DefectRecord> trackDefectList;

    private HistoryDefectAdapter mHistoryDefectAdapter = null;
    /**
     * 跟踪历史缺陷
     */
    private HistoryDefectAdapter mTrackDefectAdapter = null;
    /**
     * 当前点击的跟踪缺陷
     */
    private DefectRecord mCurrentTrackDefect = null;

    /**
     * 当前点击的缺陷级别，保存跟踪缺陷时的级别
     */
    private String mCurrentClickDefectLevel = "";
    /**
     * 是否是从电池组跳转过来的
     */
    private boolean isFromBattery = false;
    /**
     * 跟踪缺陷的id
     */
    private String mTrackDefectRecordId = "";

    private Dialog mDefectContentDialog = null;
    private DefectContentAdapter mDialogContentAdapter = null;
    private Dialog mDefectContentSourceDialog = null;
    /**
     * 跟踪缺陷的standardId
     */
    private String mTrackDefectStandardId = "";

    private OnFunctionButtonClickListener mOnFunctionButtonClickListener;
    private CopyHelper copyViewUtil;

    @Override
    public void onViewFocus(EditText v, CopyItem item, CopyResult result, List<EditText> editTexts, List<CopyItem> copyItems) {

    }

    @Override
    public void hideKeyBord() {

    }

    @Override
    public void onViewFocusChange(EditText v, CopyItem item, CopyResult result, boolean hasFocus, String descript, List<EditText> editTexts) {

    }

    public interface OnFunctionButtonClickListener {
        void onFunctionClick(View view, DefectRecord mDefect);

        void onDefectChanged(String speakContent);

        void takePicture(String pictureName, String folder, int requestCode);

        void drawCircle(String pictureName, String pictureContent);
    }

    public void setOnFunctionButtonClickListener(OnFunctionButtonClickListener mOnFunctionButtonClickListener) {
        this.mOnFunctionButtonClickListener = mOnFunctionButtonClickListener;
    }

    XsFragmentTrackDefectBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = XsFragmentTrackDefectBinding.inflate(inflater);
        AutoUtils.autoSize(binding.getRoot());
        initUI();
        lazyLoad();
        initOnClick();
        return binding.getRoot();
    }


    private void initUI() {

        getBundleValue();
        if (currentInspectionType.contains("switchover") || currentInspectionType.contains("maintenance")) {
            binding.include.txtCurrentRecord.setVisibility(View.GONE);
        }
        isFromBattery = bundle.getBoolean(Config.IS_FROM_BATTERY, false);
        mTrackDefectRecordId = bundle.getString(Config.TRACK_DEFECT_RECORD_ID);
        binding.includeAdd.tvSelectDevicePart.setVisibility(View.GONE);
        binding.includeAdd.tvSelectDevicePart.setVisibility(View.GONE);
        binding.tvDefectDeal.setText(R.string.xs_track_defect_str);
        binding.include.lvContainer.setParentScrollView(binding.scvRootContainer);
        binding.include.lvContainer.setMaxHeight(Integer.MAX_VALUE);
        binding.lvTrackContainer.setParentScrollView(binding.scvRootContainer);
        binding.lvTrackContainer.setMaxHeight(Integer.MAX_VALUE);

        setOnTouchListener();

        isPrepared = true;
        copyViewUtil = new CopyHelper(currentActivity, currentReportId, currentBdzId, currentInspectionType);
        copyViewUtil.setKeyBordListener(this);
        copyViewUtil.setItemClickListener((v, item, position) -> {
            // 显示历史曲线
            hideKeyBord();
            ShowHistroyDialogUtils.showHistory(currentActivity, item);
        });
        copyViewUtil.setItemLongClickListener((v, result, position, item) -> {

        });
    }

    private void initOnClick() {
        binding.includeAdd.rbGeneralDefect.setOnClickListener(view -> mCurrentClickDefectLevel = Config.GENERAL_LEVEL_CODE);
        binding.includeAdd.rbCrisisDefect.setOnClickListener(view -> mCurrentClickDefectLevel = Config.CRISIS_LEVEL_CODE);
        binding.includeAdd.rbSeriousDefect.setOnClickListener(view -> mCurrentClickDefectLevel = Config.SERIOUS_LEVEL_CODE);
        binding.btnConfirm.setOnClickListener(view -> save());
        binding.includeAdd.ibtnTakePicture.setOnClickListener(view -> {
            currentPictureContent = binding.includeAdd.etInputDefectContent.getText().toString().trim();
            if (!TextUtils.isEmpty(currentPictureContent)) {
                if (mOnFunctionButtonClickListener != null) {
                    mOnFunctionButtonClickListener.takePicture(currentImageName = FunctionUtil.getCurrentImageName(currentActivity), Config.RESULT_PICTURES_FOLDER, ACTION_IMAGE);
                }
            } else {
                CToast.showShort(currentActivity, "请先填写缺陷内容！");
            }
        });
        binding.includeAdd.ivNewDefectPhoto.setOnClickListener(view -> {
            if (mDefectImageList != null && !mDefectImageList.isEmpty()) {
                ArrayList<String> mImageUrlList = new ArrayList<String>();
                for (String string : mDefectImageList) {
                    mImageUrlList.add(Config.RESULT_PICTURES_FOLDER + string);
                }
                showImageDetails(this, mImageUrlList, true);
            }
        });

        binding.includeAdd.etInputDefectContent.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(mTrackDefectStandardId) && TextUtils.isEmpty(binding.includeAdd.etInputDefectContent.getText().toString().trim())) {
                showDefectContentDialog(mTrackDefectStandardId);
            }
        });


    }

    private void save() {
        if (data != null && !data.isEmpty()) {
            copyViewUtil.setSaveCurrentData(true);
            copyViewUtil.valueNullTips(true);
            if (!copyViewUtil.saveAll()) {
                CToast.showShort("请填写设备数据");
                return;
            }
        }
        PlaySound.getIntance(currentActivity).play(R.raw.track);
        if (!TextUtils.isEmpty(binding.includeAdd.etInputDefectContent.getText().toString().trim())) {
            if (mCurrentTrackDefect == null) {
                for (DefectRecord mDefectRecord : dataList) {
                    if (currentSelectDefectRecordId.equalsIgnoreCase(mDefectRecord.defectid)) {
                        mCurrentTrackDefect = mDefectRecord;
                    }
                }
            }
            if (mCurrentTrackDefect != null) {
                try {
                    // 1、复制一条原有缺陷并标记为已跟踪缺陷。并且把报告Id换为当前报告的Id

                    String defectId = DefectRecord.getDefectId();
                    XunshiApplication.getDbUtils().update(DefectRecord.class,
                            WhereBuilder.b(DefectRecord.DEFECTID, "=", mCurrentTrackDefect.defectid), new KeyValue(DefectRecord.DEFECTID, defectId), new KeyValue(DefectRecord.REPORTID, currentReportId), new KeyValue(DefectRecord.HAS_TRACK, "Y"));
                    // 2、更新原有缺陷信息。
                    mCurrentTrackDefect.has_track = "N"; // 新缺陷没有被跟踪
                    mCurrentTrackDefect.has_remove = "N";
                    mCurrentTrackDefect.discovered_date = DateUtils.getCurrentLongTime();
                    mCurrentTrackDefect.description = binding.includeAdd.etInputDefectContent.getText().toString(); // 保存新缺陷描述
                    mCurrentTrackDefect.defectlevel = mCurrentClickDefectLevel; // 保存跟踪缺陷的级别
                    if (mDefectImageList != null && !mDefectImageList.isEmpty()) {
                        mCurrentTrackDefect.pics = StringUtils.ArrayListToString(mDefectImageList);// 跟踪缺陷图片
                        // 恢复状态
                        mDefectImageList.clear();
                    }
                    XunshiApplication.getDbUtils().save(mCurrentTrackDefect);
                    // 刷新历史缺陷
                    trackDefectList.clear();
                    trackDefectList = DefectRecordService.getInstance().queryDefectHistoryByDefectCode(mCurrentTrackDefect.defectcode);
                    mHandler.sendEmptyMessage(TRACK_DEFECT);
                    // 跟踪了缺陷 要对其他两个界面的缺陷列表进行更新
                    if (mOnFunctionButtonClickListener != null) {
                        mOnFunctionButtonClickListener.onDefectChanged(getString(R.string.xs_track_defect_success_str));
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                // 清空跟踪的数据
                setTrackInforNull();
                // 刷新数据
                mHistoryDefectAdapter.setList(dataList);
            }
        } else {
            CToast.showShort(currentActivity, "请选择要跟踪的缺陷!");
        }


    }

    /**
     * 阻止输入法弹出
     */
    private void setOnTouchListener() {
        // EditText有焦点阻止输入法弹出
        if (!TextUtils.isEmpty(mTrackDefectStandardId)) {
            binding.includeAdd.etInputDefectContent.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (TextUtils.isEmpty(binding.includeAdd.etInputDefectContent.getText().toString().trim())) {
                        int inType = binding.includeAdd.etInputDefectContent.getInputType(); // backup the input type
                        binding.includeAdd.etInputDefectContent.setInputType(InputType.TYPE_NULL); // disable soft input
                        binding.includeAdd.etInputDefectContent.onTouchEvent(event); // call native handler
                        binding.includeAdd.etInputDefectContent.setInputType(inType); // restore input type
                        return true;
                    }
                    return false;
                }
            });
        }

        binding.includeAdd.etInputDefectContent.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !TextUtils.isEmpty(mTrackDefectStandardId)) {
                    showDefectContentDialog(mTrackDefectStandardId);
                }
            }
        });
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:

                if (mHistoryDefectAdapter == null) {
                    mHistoryDefectAdapter = new HistoryDefectAdapter(currentActivity, dataList);
                    mHistoryDefectAdapter.setOnAdapterViewClickListener(this);
                    mHistoryDefectAdapter.setCurrentClickedPosition(currentSelectDefectRecordId);
                    mHistoryDefectAdapter.setCurrentFunctionModel(Config.TRACK_DEFECT_MODEL);
                    binding.include.lvContainer.setAdapter(mHistoryDefectAdapter);
                } else {
                    mHistoryDefectAdapter.setList(dataList);
                }
                // 将从设备信息界面传过来的缺陷id赋值给CurrentSelectDefectRecordId
                if (!TextUtils.isEmpty(mTrackDefectRecordId) && dataList != null) {
                    for (int i = 0, count = dataList.size(); i < count; i++) {
                        if (mTrackDefectRecordId.equalsIgnoreCase(dataList.get(i).defectid)) {
                            setCurrentSelectedPosition(dataList.get(i));
                        }
                    }
                }

                setTrackInforNull();

                break;
            case TRACK_DEFECT:

                if (mTrackDefectAdapter == null) {
                    mTrackDefectAdapter = new HistoryDefectAdapter(currentActivity, trackDefectList);
                    mTrackDefectAdapter.setTrackHistory(true);
                    mTrackDefectAdapter.setOnAdapterViewClickListener(this);
                    binding.lvTrackContainer.setAdapter(mTrackDefectAdapter);
                } else {
                    mTrackDefectAdapter.setList(trackDefectList);
                }
                if (trackDefectList != null && !trackDefectList.isEmpty()) {
                    binding.tvTrackHistoryCount.setVisibility(View.VISIBLE);
                } else {
                    binding.tvTrackHistoryCount.setVisibility(View.GONE);
                }

                break;
            case LOAD_COPY_FINSIH:
                if (!data.isEmpty()) {
                    copyViewUtil.createCopyView(currentActivity, data, binding.contanierSf6);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 缺陷消除后 跟踪缺陷信息设置为空
     */
    private void setTrackInforNull() {
        binding.includeAdd.etInputDefectContent.setText("");
        binding.includeAdd.ivNewDefectPhoto.setImageBitmap(null);
        binding.includeAdd.tvDefectCount.setText("");
        binding.includeAdd.tvDefectCount.setVisibility(View.GONE);
        // 跟踪缺陷后为当前跟踪的缺陷赋空
        mCurrentTrackDefect = null;
    }

    /**
     * 点击跟踪跳转到跟踪缺陷界面 默认选中点击跟踪的那个缺陷
     *
     * @param mDefectRecord
     */
    public void setCurrentSelectedPosition(DefectRecord mDefectRecord) {
        if (mDefectRecord != null) {
            mTrackDefectStandardId = mDefectRecord.standid;
            currentSelectDefectRecordId = mDefectRecord.defectid;
            if (mHistoryDefectAdapter != null) {
                mHistoryDefectAdapter.setCurrentClickedPosition(currentSelectDefectRecordId);
            }
            fillDefectRecordInfor(mDefectRecord);
        }
    }

    /**
     * 显示缺陷内容的dialog
     */
    XsFragmentRecordDefectContentDialogBinding contentDialogBinding;

    private void showDefectContentDialog(String standardId) {
        HashMap<String, ArrayList<DefectDefine>> dataMap = DefectDefineService.getInstance().findDefectDefineByStandardId(currentStandardId);
        contentDialogBinding = XsFragmentRecordDefectContentDialogBinding.inflate(getActivity().getLayoutInflater());
        if (dataMap != null && !dataMap.isEmpty()) {
            if (mDefectContentDialog == null) {
                int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 9 / 10;
                int dialogHeight = ScreenUtils.getScreenHeight(currentActivity) * 2 / 3;
                mDefectContentDialog = DialogUtils.createDialog(currentActivity, contentDialogBinding.getRoot(), dialogWidth, dialogHeight);
            }
            if (mDialogContentAdapter == null) {
                mDialogContentAdapter = new DefectContentAdapter(currentActivity);
            }
            mDialogContentAdapter.setGroupList(dataMap.keySet());
            mDialogContentAdapter.setGroupMap(dataMap);
            contentDialogBinding.elvContainer.setAdapter(mDialogContentAdapter);
            for (int i = 0, count = mDialogContentAdapter.getGroupCount(); i < count; i++) {
                contentDialogBinding.elvContainer.expandGroup(i);
            }
            mDefectContentDialog.show();
            KeyBoardUtils.closeKeybord(binding.includeAdd.etInputDefectContent, currentActivity);
        } else {
            KeyBoardUtils.openKeybord(binding.includeAdd.etInputDefectContent, currentActivity);
        }

        contentDialogBinding.btnCustomDefect.setOnClickListener(view -> {
            mDefectContentDialog.dismiss();
            binding.includeAdd.etInputDefectContent.setText("");
            binding.includeAdd.rbGeneralDefect.setChecked(true);
            KeyBoardUtils.openKeybord(binding.includeAdd.etInputDefectContent, currentActivity);
        });

        contentDialogBinding.elvContainer.setOnChildClickListener((expandableListView, view, groupPosition, childPosition, l) -> {
            String group = "";
            String clickDefectContent = "";
            group = mDialogContentAdapter.getGroup(groupPosition);
            clickDefectContent = mDialogContentAdapter.getChild(groupPosition, childPosition).description;
            mDefectContentDialog.dismiss();
            if (Config.CRISIS_LEVEL.equalsIgnoreCase(group)) {
                binding.includeAdd.rbCrisisDefect.setChecked(true);
                mCurrentClickDefectLevel = Config.CRISIS_LEVEL_CODE;
            } else if (Config.SERIOUS_LEVEL.equalsIgnoreCase(group)) {
                binding.includeAdd.rbSeriousDefect.setChecked(true);
                mCurrentClickDefectLevel = Config.SERIOUS_LEVEL_CODE;
            } else {
                binding.includeAdd.rbGeneralDefect.setChecked(true);
                mCurrentClickDefectLevel = Config.GENERAL_LEVEL_CODE;
            }
            binding.includeAdd.etInputDefectContent.setText(clickDefectContent);
            Selection.setSelection(binding.includeAdd.etInputDefectContent.getEditableText(), binding.includeAdd.etInputDefectContent.getEditableText().length());
            return false;
        });
    }

    /**
     * 缺陷定义来源dialog
     */
    XsDialogDefectSourceBinding sourceBinding;

    private void showDefectSource(String source) {
        if (mDefectContentSourceDialog == null) {
            int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 9 / 10;
            int dialogHeight = ScreenUtils.getScreenHeight(currentActivity) / 3;// LinearLayout.LayoutParams.WRAP_CONTENT
            sourceBinding = XsDialogDefectSourceBinding.inflate(getActivity().getLayoutInflater());
            mDefectContentSourceDialog = DialogUtils.createDialog(currentActivity, sourceBinding.getRoot(), dialogWidth, dialogHeight);
        }
        sourceBinding.tvDialogTitle.setText(R.string.xs_defect_source_str);
        sourceBinding.tvSourceName.setText(source);
        mDefectContentSourceDialog.show();
    }

    @Override
    protected void lazyLoad() {
        if (isPrepared && isVisible && isFirstLoad) {
            // 填充各控件的数据
            searchData();
            isFirstLoad = false;
        }
        if (!isFirstLoad) {
            searchData();
        }
    }

    /**
     * 查询数据
     */
    List<TreeNode> data;

    public void searchData() {
        mFixedThreadPoolExecutor.execute(() -> {
            if (!isFromBattery) {
                if (currentInspectionType.contains("switchover") || currentInspectionType.contains("maintenance")) {
                    dataList = DefectRecordService.getInstance().getReportDefectRecords(currentReportId, currentBdzId);
                } else {
                    // 查询历史缺陷
                    dataList = DefectRecordService.getInstance().queryDefectByDeviceid(currentDeviceId, currentBdzId);
                }
            } else {
                // 查询电池的缺陷
                dataList = DefectRecordService.getInstance().queryDefectByBatteryId(PreferencesUtils.getString(currentActivity, Config.CURRENT_BATTERY_ID, "1"), currentDeviceId, currentBdzId);
            }
            mHandler.sendEmptyMessage(LOAD_DATA);

            trackDefectList = DefectRecordService.getInstance().queryDefectHistoryByDefectCode(currentDeviceId);
            mHandler.sendEmptyMessage(TRACK_DEFECT);
            DbModel deviceModel = DeviceService.getInstance().findCopySF6DefectDevice(currentDeviceId, currentBdzId);
            if (null != deviceModel) {
                copyViewUtil.device = deviceModel;
                data = copyViewUtil.loadItem();
                mHandler.sendEmptyMessage(LOAD_COPY_FINSIH);
            }
        });

    }

    @Override
    public void OnAdapterViewClick(View view, DefectRecord mDefect) {
        mCurrentTrackDefect = mDefect;
        int i1 = view.getId();
        if (i1 == R.id.iv_defect_image) {
            if (mDefect != null) {
                if (!TextUtils.isEmpty(mDefect.pics)) {
                    ArrayList<String> defectImageList = new ArrayList<String>();
                    String[] defectImageArray = mDefect.pics.split(CoreConfig.COMMA_SEPARATOR);
                    for (int i = 0, count = defectImageArray.length; i < count; i++) {
                        defectImageList.add(Config.RESULT_PICTURES_FOLDER + defectImageArray[i]);
                    }
                    showImageDetails(this, defectImageList);

                }
            }
            mCurrentTrackDefect = null;

        } else if (i1 == R.id.tv_track_defect) {
            fillDefectRecordInfor(mDefect);


        } else if (i1 == R.id.tv_report_defect) {
            if (mOnFunctionButtonClickListener != null) {
                mOnFunctionButtonClickListener.onFunctionClick(view, mDefect);
            }

        }

    }


    /**
     * 填充缺陷信息
     *
     * @param mDefect
     */
    private void fillDefectRecordInfor(DefectRecord mDefect) {
        PlaySound.getIntance(currentActivity).play(R.raw.control_click);
        mTrackDefectStandardId = mDefect.standid;
        // 查询当前选中缺陷的历史记录
        trackDefectList = DefectRecordService.getInstance().queryDefectHistoryByDefectCode(mDefect.defectcode);
        mHandler.sendEmptyMessage(TRACK_DEFECT);
        binding.includeAdd.etInputDefectContent.setText(mDefect.description);
        if (Config.CRISIS_LEVEL_CODE.equalsIgnoreCase(mDefect.defectlevel)) {
            binding.includeAdd.rbCrisisDefect.setChecked(true);
        } else if (Config.SERIOUS_LEVEL_CODE.equalsIgnoreCase(mDefect.defectlevel)) {
            binding.includeAdd.rbSeriousDefect.setChecked(true);
        } else {
            binding.includeAdd.rbGeneralDefect.setChecked(true);
        }
        mCurrentClickDefectLevel = mDefect.defectlevel;
        if (mHistoryDefectAdapter != null) {
            mHistoryDefectAdapter.setCurrentClickedPosition(mDefect.defectid);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case ACTION_IMAGE:

                    if (mOnFunctionButtonClickListener != null) {
                        mDefectImageList.add(currentImageName);
                        String pictureContent = binding.includeAdd.etInputDefectContent.getText().toString() + "\n" + DateUtils.getFormatterTime(new Date(), "yyyy-MM-dd HH:mm");
                        mOnFunctionButtonClickListener.drawCircle(Config.RESULT_PICTURES_FOLDER + currentImageName, pictureContent);
                    }

                    break;
                case CANCEL_RESULT_LOAD_IMAGE:

                    ArrayList<String> cancelList = data.getStringArrayListExtra(Config.CANCEL_IMAGEURL_LIST);
                    for (String imageUrl : cancelList) {
                        mDefectImageList.remove(imageUrl.replace(Config.RESULT_PICTURES_FOLDER, ""));
                    }
                case LOAD_DATA:
                    if (mDefectImageList != null && mDefectImageList.size() > 1) {
                        binding.includeAdd.tvDefectCount.setVisibility(View.VISIBLE);
                        binding.includeAdd.tvDefectCount.setText(String.valueOf(mDefectImageList.size()));
                    } else {
                        binding.includeAdd.tvDefectCount.setVisibility(View.GONE);
                    }
                    int newWidth = getResources().getDimensionPixelSize(R.dimen.xs_new_defect_photo_height);
                    binding.includeAdd.ivNewDefectPhoto.setImageBitmap(BitmapUtil.getImageThumbnail(Config.RESULT_PICTURES_FOLDER + currentImageName, newWidth, newWidth));

                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void OnAdapterViewClick(View view, DefectDefine define) {
        int i = view.getId();
        if (i == R.id.img_child_item_bt) {
            String content = define.reference;
            if (TextUtils.isEmpty(content)) {
                CToast.showLong(currentActivity, R.string.xs_no_source_str);
            } else {
                showDefectSource(content);
            }

        } else {
        }
    }
}
