package com.cnksi.bdzinspection.fragment.defectcontrol;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.activity.DeviceSelectActivity;
import com.cnksi.bdzinspection.adapter.DefectDefineAdapter;
import com.cnksi.bdzinspection.adapter.defectcontrol.BatteryDefectContentAdapter;
import com.cnksi.bdzinspection.adapter.defectcontrol.DefectContentAdapter;
import com.cnksi.bdzinspection.adapter.defectcontrol.HistoryDefectAdapter;
import com.cnksi.bdzinspection.adapter.defectcontrol.HistoryDefectAdapter.OnAdapterViewClickListener;
import com.cnksi.bdzinspection.adapter.infrared.DevicePartAdapter;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.daoservice.DefectDefineService;
import com.cnksi.bdzinspection.daoservice.DefectRecordService;
import com.cnksi.bdzinspection.daoservice.DevicePartService;
import com.cnksi.bdzinspection.databinding.XsContentListDialogBinding;
import com.cnksi.bdzinspection.databinding.XsDialogDefectSourceBinding;
import com.cnksi.bdzinspection.databinding.XsDialogTipsBinding;
import com.cnksi.bdzinspection.databinding.XsFragmentRecordDefectBinding;
import com.cnksi.bdzinspection.databinding.XsFragmentRecordDefectContentDialogBinding;
import com.cnksi.bdzinspection.fragment.BaseFragment;
import com.cnksi.bdzinspection.model.DefectDefine;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.model.DevicePart;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.Config.InspectionType;
import com.cnksi.bdzinspection.utils.DialogUtils;
import com.cnksi.bdzinspection.utils.FunctionUtil;
import com.cnksi.bdzinspection.utils.PlaySound;
import com.cnksi.xscore.xsutils.BitmapUtil;
import com.cnksi.xscore.xsutils.CToast;
import com.cnksi.xscore.xsutils.CoreConfig;
import com.cnksi.xscore.xsutils.DateUtils;
import com.cnksi.xscore.xsutils.KeyBoardUtils;
import com.cnksi.xscore.xsutils.PreferencesUtils;
import com.cnksi.xscore.xsutils.ScreenUtils;
import com.cnksi.xscore.xsutils.StringUtils;
import com.zhy.core.utils.AutoUtils;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.cnksi.common.Config.CANCEL_RESULT_LOAD_IMAGE;
import static com.cnksi.common.Config.LOAD_DATA;
import static com.cnksi.common.Config.LOAD_MORE_DATA;
import static com.cnksi.core.utils.Cst.ACTION_IMAGE;

/**
 * 记录缺陷
 *
 * @author terry
 */
@SuppressLint("ClickableViewAccessibility")
public class RecordDefectFragment extends BaseFragment implements OnAdapterViewClickListener, com.cnksi.bdzinspection.adapter.defectcontrol.DefectContentAdapter.OnAdapterViewClickListener {

    public static final int INIT_DEFECT_CONTENT_CODE = 0x111;
    public static final int SELECT_DEVICE = INIT_DEFECT_CONTENT_CODE + 1;

    private Dialog mDefectContentDialog = null;
    private DefectContentAdapter mDialogContentAdapter = null;
    private Dialog mDefectContentSourceDialog = null;

    private Dialog mDevicePartDialog = null;

    private List<DefectRecord> dataList;
    /**
     * 搜索相似内容集合
     */
    private List<DbModel> dbModelList;

    private HistoryDefectAdapter mHistoryDefectAdapter = null;
    private DefectDefineAdapter mDefectDefineAdapter = null;
    private boolean isAddNewDefect = false;
    private boolean isNeedSearchDefect = false;

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
    /**
     * 是否是从电池组跳转过来的
     */
    private boolean isFromBattery = false;

    private OnFunctionButtonClickListener mOnFunctionButtonClickListener;

    public interface OnFunctionButtonClickListener {
        void onFunctionClick(View view, DefectRecord mDefect);

        void onDefectChanged(String speakContent);

        void takePicture(String pictureName, String folder, int requestCode);

        void drawCircle(String pictureName, String pictureContent);
    }

    public void setOnFunctionButtonClickListener(OnFunctionButtonClickListener mOnFunctionButtonClickListener) {
        this.mOnFunctionButtonClickListener = mOnFunctionButtonClickListener;
    }

    // 缺陷内容
    private HashMap<String, ArrayList<DefectDefine>> mDefectContentMap = null;
    private DbModel mCurrentDbModel;
    private XsFragmentRecordDefectBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = XsFragmentRecordDefectBinding.inflate(inflater);
        AutoUtils.autoSize(binding.getRoot());
        initialUI();
        lazyLoad();
        initOnClick();


        return binding.getRoot();
    }


    private void addEditextListener() {
        binding.includeDefect.etInputDefectContent.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchData(s.toString());
            }
        });
    }

    private void initialUI() {
        getBundleValue();
        isParticularInspection = bundle.getBoolean(Config.IS_DEVICE_PART);
        boolean isShowDeviceWidget = bundle.getBoolean(Config.IS_SHOW_DEVICE_WIDGET);
        isNeedSearchDefect = bundle.getBoolean(Config.IS_NEED_SEARCH_DEFECT_REASON);
        isAddNewDefect = bundle.getBoolean(Config.ADD_NEW_DEFECT_RECORD);
        if (!isShowDeviceWidget)
            binding.includeDefect.tvSelectDevicePart.setVisibility(View.GONE);
        else
            binding.includeDefect.tvSelectDevicePart.setCompoundDrawables(null, null, null, null);
        isFromBattery = bundle.getBoolean(Config.IS_FROM_BATTERY, false);
        initBitmapUtils(currentActivity);
        binding.lvContainerDefect.setParentScrollView(binding.scvRootContainer);
        binding.lvContainerDefect.setMaxHeight(Integer.MAX_VALUE);
        binding.include.lvContainer.setParentScrollView(binding.scvRootContainer);
        binding.include.lvContainer.setMaxHeight(Integer.MAX_VALUE);

        // 特殊巡检不显示设备部件
        if (isParticularInspection || (InspectionType.battery.name().equalsIgnoreCase(currentInspectionType) && !isFromBattery)) {
            binding.includeDefect.tvSelectDevicePart.setVisibility(View.GONE);
        }
        if (currentInspectionType.contains("switchover") || currentInspectionType.contains("maintenance")) {
            binding.includeDefect.tvSelectDevicePart.setVisibility(View.VISIBLE);
            Drawable drawable = getResources().getDrawable(R.drawable.xs_ic_arrow_right);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            binding.includeDefect.tvSelectDevicePart.setCompoundDrawables(null, null, drawable, null);
            binding.include.txtCurrentRecord.setVisibility(View.GONE);
        }
        binding.includeDefect.etInputDefectContent.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && isShowDefectReason) {
                    if (isFromBattery) {
                        showBatteryDefectContentDialog();
                    } else {
                        searchDefectContent();
                    }
                } else {
                    if (isNeedSearchDefect)
                        addEditextListener();
                }
            }
        });
        if (isShowDefectReason && !isParticularInspection) {
            binding.includeDefect.tvSelectDevicePart.setText(currentDevicePartName);
            // EditText有焦点阻止输入法弹出
            binding.includeDefect.etInputDefectContent.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (TextUtils.isEmpty(binding.includeDefect.etInputDefectContent.getText().toString().trim())) {
                        int inType = binding.includeDefect.etInputDefectContent.getInputType(); // backup the input type
                        binding.includeDefect.etInputDefectContent.setInputType(InputType.TYPE_NULL); // disable soft input
                        binding.includeDefect.etInputDefectContent.onTouchEvent(event); // call native handler
                        binding.includeDefect.etInputDefectContent.setInputType(inType); // restore input type
                        return true;
                    }
                    return false;
                }
            });
        }

        if (isFromBattery) {
            binding.includeDefect.tvSelectDevicePart.setEnabled(false);
        }
        isPrepared = true;

        initDeleteDialog();
    }

    Dialog deleteDialog;
    DefectRecord deleteRecord;

    private void initDeleteDialog() {
        int dialogWidth = com.cnksi.bdzinspection.utils.ScreenUtils.getScreenWidth(currentActivity) * 9 / 10;
        XsDialogTipsBinding tipsBinding = XsDialogTipsBinding.inflate(currentActivity.getLayoutInflater());
        deleteDialog = DialogUtils.createDialog(currentActivity, tipsBinding.getRoot(), dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        tipsBinding.tvDialogContent.setText("确认要删除本次缺陷？");
        tipsBinding.btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRecord.dlt = "1";
                try {
                    XunshiApplication.getDbUtils().saveOrUpdate(deleteRecord);
                    dataList.remove(deleteRecord);
                    mHistoryDefectAdapter.notifyDataSetChanged();
                    deleteRecord = null;
                    deleteDialog.dismiss();
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });
        tipsBinding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();
            }
        });
    }


    private void initOnClick() {
        binding.lvContainerDefect.setOnItemClickListener((parent, view, position, l) -> {
            mCurrentDbModel = (DbModel) parent.getItemAtPosition(position);
            if (Config.CRISIS_LEVEL.equalsIgnoreCase(mCurrentDbModel.getString(DefectDefine.LEVEL))) {
                binding.includeDefect.rbCrisisDefect.setChecked(true);
            } else if (Config.SERIOUS_LEVEL.equalsIgnoreCase(mCurrentDbModel.getString(DefectDefine.LEVEL))) {
                binding.includeDefect.rbSeriousDefect.setChecked(true);
            } else {
                binding.includeDefect.rbGeneralDefect.setChecked(true);
            }
            binding.includeDefect.etInputDefectContent.setText(mCurrentDbModel.getString(DefectDefine.DESCRIPTION));
            KeyBoardUtils.closeKeybord(currentActivity);
        });

        binding.includeDefect.tvSelectDevicePart.setOnClickListener(view -> {
            if (!isShowDefectReason) {
                showDevicePartDialog();
            }
            if (currentInspectionType.contains("switchover") || currentInspectionType.contains("maintenance")) {
                Intent intent = new Intent(currentActivity, DeviceSelectActivity.class);
                intent.putExtra(DeviceSelectActivity.SELECT_TYPE, DeviceSelectActivity.SELECT_TYPE_RADIO);
                intent.putExtra(Config.CURRENT_INSPECTION_TYPE, currentInspectionType);
                getActivity().startActivityForResult(intent, SELECT_DEVICE);
            }
        });

        binding.includeDefect.etInputDefectContent.setOnClickListener(view -> {
            if (TextUtils.isEmpty(binding.includeDefect.etInputDefectContent.getText().toString().trim())) {
                if (isShowDefectReason) {
                    if (isFromBattery) {
                        showBatteryDefectContentDialog();
                    } else {
                        searchDefectContent();
                    }
                }
            }
        });

        binding.includeDefect.ibtnTakePicture.setOnClickListener(view -> {
            currentPictureContent = binding.includeDefect.etInputDefectContent.getText().toString().trim();
            if (!TextUtils.isEmpty(currentPictureContent)) {
                if (mOnFunctionButtonClickListener != null) {
                    mOnFunctionButtonClickListener.takePicture(currentImageName = FunctionUtil.getCurrentImageName(currentActivity), Config.RESULT_PICTURES_FOLDER, ACTION_IMAGE);
                }
            } else {
                CToast.showShort(currentActivity, R.string.xs_please_input_or_select_defect_reason_str);
            }

        });
        binding.includeDefect.ivNewDefectPhoto.setOnClickListener(view -> {
            if (mDefectImageList != null && !mDefectImageList.isEmpty()) {
                ArrayList<String> mImageUrlList = new ArrayList<String>();
                for (String string : mDefectImageList) {
                    mImageUrlList.add(Config.RESULT_PICTURES_FOLDER + string);
                }
                showImageDetails(this, mImageUrlList, true);
            }
        });
        binding.btnConfirm.setOnClickListener(view -> {
            PlaySound.getIntance(currentActivity).play(R.raw.record);
            // 征对特殊巡检和熄灯巡检
            if (binding.includeDefect.tvSelectDevicePart.getVisibility() == View.GONE) {
                saveDefect();
            } else if (!TextUtils.isEmpty(binding.includeDefect.tvSelectDevicePart.getText().toString().trim())) {
                saveDefect();
            } else {
                CToast.showShort(currentActivity, "请选择缺陷设备");
            }

            KeyBoardUtils.closeKeybord(binding.includeDefect.etInputDefectContent, currentActivity);
        });

    }


    private DefectRecord mofifyDefectRe;

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                if (mHistoryDefectAdapter == null) {
                    mHistoryDefectAdapter = new HistoryDefectAdapter(currentActivity, dataList);
                    mHistoryDefectAdapter.setOnAdapterViewClickListener(this);
                    mHistoryDefectAdapter.setCurrentFunctionModel(Config.RECORD_DEFECT_MODEL);
                    binding.include.lvContainer.setAdapter(mHistoryDefectAdapter);
                    binding.include.lvContainer.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> view, View view1, int position, long l) {
                            deleteRecord = dataList.get(position);
                            if (!TextUtils.equals(currentReportId, deleteRecord.reportid)) {
                                CToast.showShort(currentActivity, "只能删除本次记录的缺陷");
                                return true;
                            } else {
                                deleteDialog.show();
                            }
                            return false;
                        }
                    });
                    binding.include.lvContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view1, int position, long id) {
                            mofifyDefectRe = (DefectRecord) parent.getItemAtPosition(position);
                            if (!TextUtils.equals(currentReportId, mofifyDefectRe.reportid)) {
                                CToast.showShort(currentActivity, "只能修改本次记录的缺陷");
                            } else {
                                modifyDefectUI();
                            }

                        }
                    });
                } else {
                    mHistoryDefectAdapter.setList(dataList);
                }
                mHistoryDefectAdapter.setInspectionType(isParticularInspection);
                break;
            case INIT_DEFECT_CONTENT_CODE:

                showDefectContentDialog();

                break;
            case LOAD_MORE_DATA:
                if (mDefectDefineAdapter == null) {
                    mDefectDefineAdapter = new DefectDefineAdapter(currentActivity, dbModelList);
                    binding.lvContainerDefect.setAdapter(mDefectDefineAdapter);
                } else {
                    mDefectDefineAdapter.setList(dbModelList);
                }
                break;
            default:
                break;
        }
    }


    /**
     * 修改本次缺陷
     */
    private void modifyDefectUI() {
        clearUI();
        String level = mofifyDefectRe.defectlevel;
        String pics = mofifyDefectRe.pics;
        if (TextUtils.equals("2", level)) {
            binding.includeDefect.rbGeneralDefect.setChecked(true);
        } else if (TextUtils.equals("4", level)) {
            binding.includeDefect.rbSeriousDefect.setChecked(true);
        } else {
            binding.includeDefect.rbCrisisDefect.setChecked(true);
        }
        binding.includeDefect.etInputDefectContent.setText(mofifyDefectRe.description);
        if (!TextUtils.isEmpty(pics)) {
            String[] picArray = pics.split(",");
            for (String pic : picArray) {
                mDefectImageList.add(pic);
            }
            if (mDefectImageList != null && mDefectImageList.size() > 1) {
                binding.includeDefect.tvDefectCount.setVisibility(View.VISIBLE);
                binding.includeDefect.tvDefectCount.setText(String.valueOf(mDefectImageList.size()));
            } else {
                binding.includeDefect.tvDefectCount.setVisibility(View.GONE);
            }
            int newWidth = getResources().getDimensionPixelSize(R.dimen.xs_new_defect_photo_height);
            binding.includeDefect.ivNewDefectPhoto.setImageBitmap(BitmapUtil.getImageThumbnail((mDefectImageList == null || mDefectImageList.isEmpty()) ? "" : Config.RESULT_PICTURES_FOLDER + mDefectImageList.get(0), newWidth, newWidth));
        }
        binding.includeDefect.tvSelectDevicePart.setText(mofifyDefectRe.devcie);
        mHistoryDefectAdapter.notifyDataSetChanged();

    }


    @Override
    protected void lazyLoad() {
        if (null != dbModelList) {
            dbModelList.clear();
            if (mDefectDefineAdapter != null)
                mDefectDefineAdapter.notifyDataSetChanged();
        }
        if (isPrepared && isVisible && isFirstLoad) {
            // 填充各控件的数据
            searchData();
            isFirstLoad = false;
        }
    }

    /**
     * 查询数据
     *
     * @param
     * @param
     */
    public void searchData() {

        mFixedThreadPoolExecutor.execute(new Runnable() {

            @Override
            public void run() {
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
            }
        });
    }

    /**
     * 查询相似的内容
     *
     * @param content
     */
    private void searchData(final String content) {
        mFixedThreadPoolExecutor.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    dbModelList = DefectDefineService.getInstance().findDefectDefineByDeviceIdAndContent(currentDeviceId, content);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                if (dbModelList == null) {
                    dbModelList = new ArrayList<DbModel>();
                }
                mHandler.sendEmptyMessage(LOAD_MORE_DATA);
            }
        });
    }

    /**
     * 查询设备部件对应的常见缺陷内容
     */
    private void searchDefectContent() {
        mFixedThreadPoolExecutor.execute(new Runnable() {

            @Override
            public void run() {
                mDefectContentMap = DefectDefineService.getInstance().findDefectDefineByStandardId(currentStandardId);
                mHandler.sendEmptyMessage(INIT_DEFECT_CONTENT_CODE);
            }
        });
    }

    /**
     * 显示缺陷内容的dialog
     */
    private XsFragmentRecordDefectContentDialogBinding recordDefectContentDialogBinding;

    private void showDefectContentDialog() {
        if (mDefectContentMap != null && !mDefectContentMap.isEmpty()) {
            if (mDefectContentDialog == null) {
                int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 9 / 10;
                int dialogHeight = ScreenUtils.getScreenHeight(currentActivity) * 2 / 3;
                recordDefectContentDialogBinding = XsFragmentRecordDefectContentDialogBinding.inflate(getActivity().getLayoutInflater());
                mDefectContentDialog = DialogUtils.createDialog(currentActivity, recordDefectContentDialogBinding.getRoot(), dialogWidth, dialogHeight);
            }
            if (mDialogContentAdapter == null) {
                mDialogContentAdapter = new DefectContentAdapter(currentActivity);
                mDialogContentAdapter.setOnAdapterViewClickListener(RecordDefectFragment.this);
            }
            mDialogContentAdapter.setGroupList(mDefectContentMap.keySet());
            mDialogContentAdapter.setGroupMap(mDefectContentMap);
            recordDefectContentDialogBinding.elvContainer.setAdapter(mDialogContentAdapter);
            for (int i = 0, count = mDialogContentAdapter.getGroupCount(); i < count; i++) {
                recordDefectContentDialogBinding.elvContainer.expandGroup(i);
            }
            mDefectContentDialog.show();
            KeyBoardUtils.closeKeybord(binding.includeDefect.etInputDefectContent, currentActivity);
        } else {
            KeyBoardUtils.openKeybord(binding.includeDefect.etInputDefectContent, currentActivity);
        }

        recordDefectContentDialogBinding.btnCustomDefect.setOnClickListener(view -> {
            mDefectContentDialog.dismiss();
            binding.includeDefect.etInputDefectContent.setText("");
            KeyBoardUtils.openKeybord(binding.includeDefect.etInputDefectContent, currentActivity);
            binding.includeDefect.rbGeneralDefect.setChecked(true);
        });

        recordDefectContentDialogBinding.elvContainer.setOnChildClickListener((expandableListView, view, groupPosition, childPosition, l) -> {
            group = mDialogContentAdapter.getGroup(groupPosition);
            clickDefectContent = (mDialogContentAdapter.getChild(groupPosition, childPosition)).description;
            mDefectContentDialog.dismiss();
            if (Config.CRISIS_LEVEL.equalsIgnoreCase(group)) {
                binding.includeDefect.rbCrisisDefect.setChecked(true);
            } else if (Config.SERIOUS_LEVEL.equalsIgnoreCase(group)) {
                binding.includeDefect.rbSeriousDefect.setChecked(true);
            } else {
                binding.includeDefect.rbGeneralDefect.setChecked(true);
            }
            binding.includeDefect.etInputDefectContent.setText(clickDefectContent);
            return false;
        });
    }

    /**
     * 显示缺陷内容的dialog
     */
    String group = "";
    String clickDefectContent = "";
    XsFragmentRecordDefectContentDialogBinding dialogBinding;

    private void showBatteryDefectContentDialog() {
        int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 9 / 10;
        BatteryDefectContentAdapter mDialogContentBatteryAdapter = new BatteryDefectContentAdapter(currentActivity);
        HashMap<String, ArrayList<String>> dataMap = DefectDefineService.getInstance().getBatteryDefectContent();
        if (!dataMap.isEmpty()) {
            mDialogContentBatteryAdapter.setGroupList(dataMap.keySet());
            mDialogContentBatteryAdapter.setGroupMap(dataMap);
            if (mDefectContentDialog == null) {
                dialogBinding = XsFragmentRecordDefectContentDialogBinding.inflate(getActivity().getLayoutInflater());
                mDefectContentDialog = DialogUtils.createDialog(currentActivity, dialogBinding.getRoot(), dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
            dialogBinding.elvContainer.setAdapter(mDialogContentBatteryAdapter);
            for (int i = 0, count = mDialogContentBatteryAdapter.getGroupCount(); i < count; i++) {
                dialogBinding.elvContainer.expandGroup(i);
            }
            mDefectContentDialog.show();
        }
        dialogBinding.btnCustomDefect.setOnClickListener(view -> {
            mDefectContentDialog.dismiss();
            binding.includeDefect.etInputDefectContent.setText("");
            KeyBoardUtils.openKeybord(binding.includeDefect.etInputDefectContent, currentActivity);
            binding.includeDefect.rbGeneralDefect.setChecked(true);
        });

        dialogBinding.elvContainer.setOnChildClickListener((expandableListView, view, groupPosition, childPosition, l) -> {

            if (isFromBattery) {
                group = mDialogContentBatteryAdapter.getGroup(groupPosition);
                clickDefectContent = mDialogContentBatteryAdapter.getChild(groupPosition, childPosition);
            } else {
                group = mDialogContentAdapter.getGroup(groupPosition);
                clickDefectContent = (mDialogContentAdapter.getChild(groupPosition, childPosition)).description;
            }
            mDefectContentDialog.dismiss();
            if (Config.CRISIS_LEVEL.equalsIgnoreCase(group)) {
                binding.includeDefect.rbCrisisDefect.setChecked(true);
            } else if (Config.SERIOUS_LEVEL.equalsIgnoreCase(group)) {
                binding.includeDefect.rbSeriousDefect.setChecked(true);
            } else {
                binding.includeDefect.rbGeneralDefect.setChecked(true);
            }
            binding.includeDefect.etInputDefectContent.setText(clickDefectContent);
            return false;
        });
    }


    /**
     * 缺陷定义来源dialog
     */
    private XsDialogDefectSourceBinding sourceBinding;

    private void showDefectSource(String defectOrigin, String dealMethod) {
        if (mDefectContentSourceDialog == null) {
            int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 9 / 10;
            int dialogHeight = ScreenUtils.getScreenHeight(currentActivity) * 3 / 5;
            sourceBinding = XsDialogDefectSourceBinding.inflate(getActivity().getLayoutInflater());
            mDefectContentSourceDialog = DialogUtils.createDialog(currentActivity, sourceBinding.getRoot(), dialogWidth, dialogHeight);
            AutoUtils.autoSize(LayoutInflater.from(currentActivity).inflate(R.layout.xs_dialog_defect_source, null, false));
        }
        sourceBinding.tvDialogTitle.setText(R.string.xs_defect_source_str);
        sourceBinding.tvSourceName.setText(defectOrigin);
        sourceBinding.tvDealMethod.setText(dealMethod);
        mDefectContentSourceDialog.show();
    }

    /**
     * 显示缺陷内容的dialog
     */

    private XsContentListDialogBinding contentListDialogBinding;

    private void showDevicePartDialog() {

        int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 9 / 10;
        int dialogHeight = ScreenUtils.getScreenHeight(currentActivity) * 2 / 3;
        // 查询设备部件列表
        List<DevicePart> mDevicePartList = DevicePartService.getInstance().getDevicePartList(currentDeviceId);
        DevicePartAdapter mDeviceAdapter = new DevicePartAdapter(currentActivity, mDevicePartList);
        if (mDevicePartDialog == null) {
            contentListDialogBinding = XsContentListDialogBinding.inflate(getActivity().getLayoutInflater());
            mDevicePartDialog = DialogUtils.createDialog(currentActivity, contentListDialogBinding.getRoot(), dialogWidth, dialogHeight);
        }
        contentListDialogBinding.lvContainer.setAdapter(mDeviceAdapter);
        contentListDialogBinding.tvDialogTitle.setText(currentDeviceName);
        if (mDevicePartList != null && !mDevicePartList.isEmpty()) {
            mDevicePartDialog.show();
        }
        contentListDialogBinding.lvContainer.setOnItemClickListener((parent, view, position, id) -> {
            DevicePart part = (DevicePart) parent.getItemAtPosition(position);
            binding.includeDefect.tvSelectDevicePart.setText(currentDevicePartName = part.name);
            binding.includeDefect.etInputDefectContent.setText("");
            currentDevicePartId = part.duid;
            mDevicePartDialog.dismiss();
        });
    }


    /**
     * 保存缺陷
     */
    private void saveDefect() {
        String defectContent = binding.includeDefect.etInputDefectContent.getText().toString().trim();
        String departmentName = PreferencesUtils.getString(currentActivity, Config.CURRENT_DEPARTMENT_NAME, "");
        if (TextUtils.isEmpty(defectContent)) {
            CToast.showShort(currentActivity, "请选择或输入缺陷内容");
            return;
        }

        String currentDefectLevel = Config.GENERAL_LEVEL_CODE;
        if (binding.includeDefect.rbSeriousDefect.isChecked()) {
            currentDefectLevel = Config.SERIOUS_LEVEL_CODE;
        } else if (binding.includeDefect.rbCrisisDefect.isChecked()) {
            currentDefectLevel = Config.CRISIS_LEVEL_CODE;
        }

        DefectRecord record = null;
        if (null != mofifyDefectRe) {
            record = mofifyDefectRe;
            record.defectlevel = currentDefectLevel;
            record.description = defectContent;
            record.pics = StringUtils.ArrayListToString(mDefectImageList);
        }
        if (isFromBattery) {
            // 电池记录的缺陷 间隔id和名称 存的是电池组的id和名称 设备Id和名称 以及 设备部件id和名称 存的都是电池的编号 如 001
            String batteryId = PreferencesUtils.getString(currentActivity, Config.CURRENT_BATTERY_ID, "");
            String batteryName = PreferencesUtils.getString(currentActivity, Config.CURRENT_BATTERY_NAME, "");
            record = new DefectRecord(currentReportId, // 报告id
                    currentBdzId, // 变电站id
                    currentBdzName, // 变电站名称
                    batteryId, // 间隔ID
                    batteryName, // 间隔名称
                    currentDeviceId, // 设备id
                    currentDeviceId, // 设备名称
                    currentDeviceId, // 设备部件id
                    currentDeviceId, // 设备部件名称
                    currentDefectLevel, // 缺陷级别
                    defectContent, // 缺陷描述
                    "1", // 巡视标准id
                    StringUtils.ArrayListToString(mDefectImageList)// pics图片
            );
        } else if (mofifyDefectRe == null) {
//            if (currentInspectionType.contains("special")) {
            if (isParticularInspection) {
                currentDevicePartId = "";
                currentDevicePartName = "";
            }
            if (isAddNewDefect)
                record = new DefectRecord(currentReportId, // 报告id
                        currentBdzId, // 变电站id
                        currentBdzName, // 变电站名称
                        currentSpacingId, // 间隔ID
                        currentSpacingName, // 间隔名称
                        currentDeviceId, // 设备id
                        currentDeviceName, // 设备名称
                        mCurrentDbModel == null ? "" : mCurrentDbModel.getString(DevicePart.DUID), // 设备部件id
                        mCurrentDbModel == null ? "" : mCurrentDbModel.getString(DevicePart.NAME), // 设备部件名称
                        currentDefectLevel, // 缺陷级别
                        defectContent, // 缺陷描述
                        mCurrentDbModel == null ? "" : mCurrentDbModel.getString(DefectDefine.STAID), // 巡视标准id
                        StringUtils.ArrayListToString(mDefectImageList)// pics图片
                );
            else
                record = new DefectRecord(currentReportId, // 报告id
                        currentBdzId, // 变电站id
                        currentBdzName, // 变电站名称
                        currentSpacingId, // 间隔ID
                        currentSpacingName, // 间隔名称
                        currentDeviceId, // 设备id
                        currentDeviceName, // 设备名称
                        currentDevicePartId, // 设备部件id
                        currentDevicePartName, // 设备部件名称
                        currentDefectLevel, // 缺陷级别
                        defectContent, // 缺陷描述
                        currentStandardId, // 巡视标准id
                        StringUtils.ArrayListToString(mDefectImageList)// pics图片
                );
        }
        if (binding.rbInflunceNo.isChecked()) {
            record.hasInfluenceDbz = "否";
        } else if (binding.rbInflunceYes.isChecked()) {
            record.hasInfluenceDbz = "是";
        } else if (binding.rbInflunceNothing.isChecked()) {
            record.hasInfluenceDbz = "不清楚";
        }
        record.discoverer_unit = departmentName;
        try {
            XunshiApplication.getDbUtils().saveOrUpdate(record);
            mofifyDefectRe = null;
        } catch (DbException e) {
            e.printStackTrace();
        }

        binding.includeDefect.etInputDefectContent.setText("");
        binding.includeDefect.tvSelectDefectReason.setText("");
        if (!isFromBattery && !isShowDefectReason) {
            binding.includeDefect.tvSelectDevicePart.setText("");
        }
        binding.includeDefect.rbGeneralDefect.setChecked(true);
        if (!dataList.contains(record)) {
            dataList.add(record);
        }
        binding.includeDefect.ivNewDefectPhoto.setImageBitmap(null);
        // 清空图片数量
        binding.includeDefect.tvDefectCount.setVisibility(View.GONE);
        if (mDefectImageList != null) {
            mDefectImageList.clear();
        }
        currentImageName = "";
        if (mOnFunctionButtonClickListener != null) {
            mOnFunctionButtonClickListener.onDefectChanged(getString(R.string.xs_save_defect_success_str));
        }
        mHandler.sendEmptyMessage(LOAD_DATA);
    }

    public void clearUI() {
        binding.includeDefect.etInputDefectContent.setText("");
        binding.includeDefect.tvSelectDefectReason.setText("");
        binding.includeDefect.rbGeneralDefect.setChecked(true);
        binding.includeDefect.ivNewDefectPhoto.setImageBitmap(null);
        if (mDefectImageList != null) {
            mDefectImageList.clear();
        }
        binding.includeDefect.tvDefectCount.setVisibility(View.GONE);
    }

    @Override
    public void OnAdapterViewClick(View view, DefectRecord mDefect) {
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

        } else {
            if (mOnFunctionButtonClickListener != null) {
                mOnFunctionButtonClickListener.onFunctionClick(view, mDefect);
            }

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
                        String pictureContent = currentDeviceName + "：" + (TextUtils.isEmpty(currentDevicePartName) ? "" : currentDevicePartName) + "\n" + binding.includeDefect.etInputDefectContent.getText().toString() + "\n" + DateUtils.getFormatterTime(new Date(), CoreConfig.dateFormat8);
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
                        binding.includeDefect.tvDefectCount.setVisibility(View.VISIBLE);
                        binding.includeDefect.tvDefectCount.setText(String.valueOf(mDefectImageList.size()));
                    } else {
                        binding.includeDefect.tvDefectCount.setVisibility(View.GONE);
                    }
                    int newWidth = getResources().getDimensionPixelSize(R.dimen.xs_new_defect_photo_height);
                    binding.includeDefect.ivNewDefectPhoto.setImageBitmap(BitmapUtil.getImageThumbnail((mDefectImageList == null || mDefectImageList.isEmpty()) ? "" : Config.RESULT_PICTURES_FOLDER + mDefectImageList.get(0), newWidth, newWidth));

                    break;
                case SELECT_DEVICE:
                    String spaceName = data.getStringExtra(DeviceSelectActivity.RESULT_SELECT_SPACE);
                    String spaceId = data.getStringExtra(DeviceSelectActivity.RESULT_SELECT_SPACE_ID);
                    String deviceID = data.getStringExtra(DeviceSelectActivity.RESULT_SELECT_DEVICE);
                    String deviceName = data.getStringExtra(DeviceSelectActivity.RESULT_SELECT_DEVICE_NAME);
                    binding.includeDefect.tvSelectDevicePart.setText(deviceName);
                    currentDeviceId = deviceID;
                    currentDeviceName = deviceName;
                    currentSpacingId = spaceId;
                    currentSpacingName = spaceName;
                    if (mofifyDefectRe != null) {
                        mofifyDefectRe.devcie = deviceName;
                        mofifyDefectRe.deviceid = currentDeviceId;
                        mofifyDefectRe.spid = currentSpacingId;
                        mofifyDefectRe.spname = currentSpacingName;
                    }
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
            String defectOrigin = define.origin;
            String dealMethod = define.dealMethod;
            if (TextUtils.isEmpty(defectOrigin) && TextUtils.isEmpty(dealMethod)) {
                CToast.showLong(currentActivity, R.string.xs_no_defect_source_and_dealmethod_str);
            } else {
                showDefectSource(defectOrigin, dealMethod);
            }

        } else {
        }
    }


}
