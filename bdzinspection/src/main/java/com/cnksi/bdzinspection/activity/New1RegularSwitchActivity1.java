package com.cnksi.bdzinspection.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.New1RegularSwitchListAdapter1;
import com.cnksi.bdzinspection.adapter.SwitchMenuAudioAdapter;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.daoservice.DefectRecordService;
import com.cnksi.bdzinspection.daoservice.ReportService;
import com.cnksi.bdzinspection.daoservice.StandardSwitchOverService;
import com.cnksi.bdzinspection.databinding.PopMenuBinding;
import com.cnksi.bdzinspection.databinding.XsActivityRegularSwitch1Binding;
import com.cnksi.bdzinspection.model.DefectRecord;
import com.cnksi.bdzinspection.model.Report;
import com.cnksi.bdzinspection.model.StandardSwitchover;
import com.cnksi.bdzinspection.model.SwitchPic;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.utils.DialogUtils;
import com.cnksi.bdzinspection.utils.FunctionUtil;
import com.cnksi.bdzinspection.utils.KeyBoardUtil;
import com.cnksi.bdzinspection.utils.KeyboardChangeListener;
import com.cnksi.bdzinspection.utils.MediaRecorderUtils;
import com.cnksi.bdzinspection.utils.OnViewClickListener;
import com.cnksi.bdzinspection.utils.RecordAudioUtils;
import com.cnksi.bdzinspection.utils.TTSUtils;
import com.cnksi.xscore.xsutils.BitmapUtil;
import com.cnksi.xscore.xsutils.CLog;
import com.cnksi.xscore.xsutils.CToast;
import com.cnksi.xscore.xsutils.CoreConfig;
import com.cnksi.xscore.xsutils.DateUtils;
import com.cnksi.xscore.xsutils.FileUtils;
import com.cnksi.xscore.xsutils.FunctionUtils;
import com.cnksi.xscore.xsutils.PreferencesUtils;
import com.cnksi.xscore.xsutils.ScreenUtils;
import com.cnksi.xscore.xsutils.StringUtils;
import com.cnksi.xscore.xsview.CustomerDialog;

import org.xutils.db.Selector;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class New1RegularSwitchActivity1 extends BaseActivity implements KeyboardChangeListener.KeyBoardListener {

    public static final int SYNC_TIME = 0x111;
    public static final int UPDATE_DEFECT_STATUS_REQUEST_CODE = SYNC_TIME + 1;
    public static final int UPDATE_DEVICE_STANDARD_REQUEST_CODE = UPDATE_DEFECT_STATUS_REQUEST_CODE + 1;
    public static final int ONBACKPRESSED_CODE = UPDATE_DEVICE_STANDARD_REQUEST_CODE + 1;
    public static final int ON_COMPELETE_PRESSED_CODE = ONBACKPRESSED_CODE + 1;
    public static final int BATTERY_TEST_REQUEST = ON_COMPELETE_PRESSED_CODE + 1;
    LinkedList<DbModel> groupList;
    HashMap<DbModel, ArrayList<DbModel>> childMapLists;
    ToolPop popMenu;
    private New1RegularSwitchListAdapter1 mRegularSwitchAdapter = null;
    private List<DbModel> dataList = null;
    private List<DbModel> dbModelList = new ArrayList<DbModel>();
    private String inspectionTips = "";
    /**
     * 当前操作图片录音的对象
     */
    private SwitchPic switchPic;
    /**
     * 当前操作的对象
     */
    private DbModel mCurrentDbModel = null;
    /**
     * 当前照片的名称
     */
    private String currentImageName = "";
    /**
     * 照片集合
     */
    private ArrayList<String> mPictureList = null;

    /**
     * 录音文件的名称
     */
    private String audioFileName = "";

    /**
     * 当前键盘的状态
     */
    private int currentKeyBoardState = KeyBoardUtil.KEYBORAD_HIDE;
    private StandardSwitchover standardSwitchOver;
    private DefectRecord defectRecord;
    private Report report;
    private List<DbModel> batteryDbmodelList;

    private boolean isFirstLoad = true;
    private XsActivityRegularSwitch1Binding mSwitch1Binding;

    private List<String> audioUrls;
    private SwitchMenuAudioAdapter mAudioAdapter;
    private RecordAudioUtils recordAudioUtils;
    private DbModel batteryCopyTotal = null;
    private int sumBatteryCode;
    private DbModel dbModel;
    private New1RegularSwitchListAdapter1.AdapterClickListener itemClickListener = new New1RegularSwitchListAdapter1.AdapterClickListener() {
        @Override
        public void toolClick(View v, DbModel data, int position) {
            ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(v, "rotation", 0, 360f);
            rotationAnimator.start();
            if (popMenu.isShowing() && popMenu.model == data)
                return;
            popMenu.dismiss();
            popMenu.setModel(data);
            popMenu.show(v);
        }

        @Override
        public void imgClick(View v, DbModel data, int position) {
            dbModel = data;
            String pics = dbModel.getString(SwitchPic.PIC);
            if (mPictureList == null) {
                mPictureList = new ArrayList<>();
            } else {
                mPictureList.clear();
            }
            if (!TextUtils.isEmpty(pics)) {
                ArrayList<String> mImageUrlList = new ArrayList<>();
                String[] mPictureArray = pics.split(CoreConfig.COMMA_SEPARATOR);
                if (mPictureArray != null && mPictureArray.length > 0) {
                    for (int i = 0, count = mPictureArray.length; i < count; i++) {
                        mImageUrlList.add(Config.RESULT_PICTURES_FOLDER + mPictureArray[i]);
                        mPictureList.add(mPictureArray[i]);
                    }
                    showImageDetails(currentActivity, mImageUrlList, true);
                }
            }
        }

        @Override
        public void radioClick(View v, DbModel data, int position) {
            dbModel = data;
            String voice = dbModel.getString(SwitchPic.VOICE);
            if (MediaRecorderUtils.getInstance().isPlaying()) {
                MediaRecorderUtils.getInstance().stopPlayAudio();
            } else {
                MediaRecorderUtils.getInstance().startPlayAudio(Config.AUDIO_FOLDER + voice, mp -> {
                    CToast.showShort(currentActivity, "播放完毕...");
                    MediaRecorderUtils.getInstance().setPlaying(false);
                });
            }
        }

        @Override
        public void defectClick(View v, DbModel data, int position) {
            CustomerDialog.showProgress(currentActivity, "加载缺陷中");
            dbModel = data;
            String deviceId = dbModel.getString(DefectRecord.DEVICEID);
            String standId = dbModel.getString(StandardSwitchover.ID);
            String defectLevel = dbModel.getString(DefectRecord.DEFECTLEVEL);
            if (!TextUtils.isEmpty(defectLevel)) {
                Intent intent = new Intent(currentActivity, DefectControlActivity.class);
                intent.putExtra(Config.CURRENT_DEVICE_ID, deviceId);
                intent.putExtra(Config.IS_NEED_SEARCH_DEFECT_REASON, true);
                intent.putExtra(Config.CURRENT_STANDARD_ID, standId);
                intent.putExtra(Config.CURRENT_REPORT_ID, currentReportId);
                intent.putExtra(Config.IS_TRACK_DEFECT, true);
                startActivityForResult(intent, UPDATE_DEFECT_STATUS_REQUEST_CODE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSwitch1Binding = DataBindingUtil.setContentView(currentActivity, R.layout.xs_activity_regular_switch1);
        initUI();
        initData();
        popMenu = new ToolPop();
        new KeyboardChangeListener(this).setKeyBoardListener(this);

        initOnClick();
    }

    private void initUI() {
        getIntentValue();
        mSwitch1Binding.includeTitle.tvTitle.setText(currentInspectionTypeName);
        boolean xudianchi = currentInspectionTypeName.contains(Config.XUDIANCHI) && (currentInspectionTypeName.contains(Config.DIANYA) || currentInspectionTypeName.contains(Config.NEIZU));
        if ("maintenance_blqdzcs".equalsIgnoreCase(currentInspectionType) || xudianchi || currentInspectionTypeName.contains("压板")) {
            mSwitch1Binding.includeTitle.ibtnExit.setVisibility(View.VISIBLE);
            mSwitch1Binding.includeTitle.ibtnExit.setImageResource(R.drawable.xs_copy_button_background);
        } else {
            int width = getResources().getDimensionPixelSize(R.dimen.xs_title_bar_minheight);
            LayoutParams params = new LayoutParams(width, width);
            params.addRule(RelativeLayout.LEFT_OF, R.id.rg_title);
            mSwitch1Binding.includeTitle.ibtnAdd.setLayoutParams(params);
        }

        TTSUtils.getInstance().startSpeaking(getString(R.string.xs_speak_content_format_str1, currentInspectionTypeName));
        mSwitch1Binding.lvContainer.setGroupIndicator(null);
        mSwitch1Binding.lvContainer.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState != SCROLL_STATE_IDLE) {
                    popMenu.dismiss();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        recordAudioUtils = new RecordAudioUtils(this, mSwitch1Binding.llRootContainer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isFirstLoad) {
            try {
                report = XunshiApplication.getDbUtils().selector(Report.class).where(Report.REPORTID, "=", currentReportId).and(Report.DLT, "=", "0").and(Report.BDZID, "=", currentBdzId).findFirst();
                report.inspection = currentInspectionType;
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
        isFirstLoad = false;
    }

    private void initData() {
        mFixedThreadPoolExecutor.execute(() -> {
            try {
                report = XunshiApplication.getDbUtils().selector(Report.class).where(Report.REPORTID, "=", currentReportId).and(Report.DLT, "=", "0").and(Report.BDZID, "=", currentBdzId).findFirst();
                String repSwitchId = report.repSwithoverId;
                dbModelList = StandardSwitchOverService.getInstance().getAllType(currentInspectionType, currentBdzId, currentReportId, repSwitchId);
                boolean xudianchi = currentInspectionTypeName.contains(Config.XUDIANCHI) && (currentInspectionTypeName.contains(Config.DIANYA) || currentInspectionTypeName.contains(Config.NEIZU));
                if (xudianchi) {
                    batteryDbmodelList = ReportService.getInstance().findBatteryGroup(currentBdzId);
                }
                if (dbModelList != null) {
                    groupList = new LinkedList<DbModel>();
                    childMapLists = new HashMap<>();
                    ArrayList<DbModel> tempList = null;
                    //生成显示用的序号
                    int n1 = 0, n2 = 0, n3 = 0;
                    for (DbModel model : dbModelList) {
                        if (model.getInt(StandardSwitchover.LEVEL) == 1) {
                            tempList = new ArrayList<DbModel>();
                            n1++;
                            n2 = 0;
                            model.add(StandardSwitchover.DISPLAYNUM, n1 + "、");
                            groupList.add(model);
                            childMapLists.put(model, tempList);
                        } else {
                            if (model.getInt(StandardSwitchover.LEVEL) == 2) {
                                n2++;
                                n3 = 0;
                                model.add(StandardSwitchover.DISPLAYNUM, n1 + "." + n2);
                            } else {
                                n3++;
                                model.add(StandardSwitchover.DISPLAYNUM, n1 + "." + n2 + "." + n3);
                            }
                            tempList.add(model);
                        }
                    }
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
            mHandler.sendEmptyMessage(LOAD_DATA);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        CustomerDialog.dismissProgress();
    }

    @Override
    protected void onRefresh(Message msg) {
        super.onRefresh(msg);
        switch (msg.what) {
            case LOAD_DATA:
                if (mRegularSwitchAdapter == null) {
                    mRegularSwitchAdapter = new New1RegularSwitchListAdapter1(currentActivity);
                    mRegularSwitchAdapter.setClickListener(itemClickListener);
                    mSwitch1Binding.lvContainer.setAdapter(mRegularSwitchAdapter);
                    if (!TextUtils.isEmpty(inspectionTips)) {
                        TTSUtils.getInstance().startSpeaking(inspectionTips);
                        mSwitch1Binding.tvTips.setVisibility(View.VISIBLE);
                        mSwitch1Binding.tvTips.setText("Tips:" + inspectionTips);
                    }
                }
                mRegularSwitchAdapter.setGroupList(groupList);
                mRegularSwitchAdapter.setGroupMap(childMapLists);
                if (mAudioAdapter == null) {
                    String audioUrl = report.audioUrl;
                    audioUrls = StringUtils.stringsToList(audioUrl, ",");
                    mAudioAdapter = new SwitchMenuAudioAdapter(R.layout.xs_item_audio, audioUrls);
                    mSwitch1Binding.recyAudio.setLayoutManager(new GridLayoutManager(currentActivity, 4));
                    mSwitch1Binding.recyAudio.setAdapter(mAudioAdapter);
                    mAudioAdapter.setItemClicListener((view, data, position) -> {
                        DialogUtils.showSureTipsDialog(currentActivity, null, "确定要删除吗？", new OnViewClickListener() {
                            @Override
                            public void onClick(View v) {
                                FileUtils.deleteFile(Config.AUDIO_FOLDER + audioUrls.get(position));
                                audioUrls.remove(position);
                                report.audioUrl = StringUtils.ArrayListToString(audioUrls);
                                saveReportAudioFile();
                            }
                        });
                    });
                }
                break;
            case LOAD_MORE_DATA:
                CustomerDialog.dismissProgress();
                mRegularSwitchAdapter.notifyDataSetChanged();
                break;
            case ONBACKPRESSED_CODE:
                if (currentKeyBoardState == KeyBoardUtil.KEYBORAD_SHOW) {
                    mKeyBoardUtil.hideKeyboard();
                }
                setResult(RESULT_OK);
                this.finish();
                break;
            case ON_COMPELETE_PRESSED_CODE:
                int showStr = 0;
                if (currentInspectionType.contains("switchover")) {
                    showStr = R.string.xs_dialog_tips_content_switchover;
                } else if (currentInspectionType.contains("maintenance")) {
                    showStr = R.string.xs_dialog_tips_content_maintance;
                } else {
                    showStr = R.string.xs_dialog_tips_content_special;
                }
                final Intent intent = new Intent(currentActivity, GenerateReportActivity.class);
                intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, currentInspectionType);
                boolean xudianchi = currentInspectionTypeName.contains(Config.XUDIANCHI) && (currentInspectionTypeName.contains(Config.DIANYA) || currentInspectionTypeName.contains(Config.NEIZU));
                if (xudianchi) {
                    sumBatteryCode = 0;
                    for (DbModel dbModel : batteryDbmodelList) {
                        sumBatteryCode = sumBatteryCode + Integer.valueOf(dbModel.getString("amount"));
                    }
                    mFixedThreadPoolExecutor.execute(() -> {
                        batteryCopyTotal = ReportService.getInstance().findAllBatteryCodeCount(currentBdzId, currentReportId);
                        runOnUiThread(() -> {
                            int showStr1 = R.string.xs_dialog_tips_content_maintance;
                            showTipsDialog(mSwitch1Binding.llRootContainer, intent, -1, "本次蓄电池抄录:" + (TextUtils.isEmpty(batteryCopyTotal.getString("count")) ? 0 : batteryCopyTotal.getString("count")) + "/" + sumBatteryCode + "。" + getText(showStr1), false);
                        });
                    });
                } else {
                    showTipsDialog(mSwitch1Binding.llRootContainer, intent, -1, showStr, false);
                }
                break;
            default:
                break;
        }
    }


    /**
     * 保存当前的属于报告的录音文件
     */
    private void saveReportAudioFile() {
        List<String> audioUrlList = StringUtils.stringsToList(report.audioUrl, ",");
        if (!TextUtils.isEmpty(audioFileName))
            audioUrlList.add(audioFileName);
        String audioUrls = StringUtils.ArrayListToString(audioUrlList);
        report.audioUrl = audioUrls;
        this.audioUrls.clear();
        this.audioUrls.addAll(audioUrlList);
        mAudioAdapter.notifyDataSetChanged();
        audioFileName = "";
        try {
            XunshiApplication.getDbUtils().saveOrUpdate(report);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onKeyboardChange(boolean isShow, int keyboardHeight) {
        if (popMenu != null && popMenu.isShowing()) {
            popMenu.dismiss();
        }
    }

    private void initOnClick() {
        mSwitch1Binding.includeTitle.ibtnCancel.setOnClickListener(view -> {
            saveOrUpdateInputValue(0x00);
            onBackPressed();
        });

        mSwitch1Binding.btnComplete.setOnClickListener(view -> {
            TTSUtils.getInstance().stopSpeak();
            // 保存抄录数据
            saveOrUpdateInputValue(ON_COMPELETE_PRESSED_CODE);
        });

        mSwitch1Binding.includeTitle.ibtnAdd.setOnClickListener(view -> {
            Intent intent = new Intent(currentActivity, ChangeDeviceStandardActivity.class);
            intent.putExtra(Config.IS_ADD_DEVICE_STANDARD, true);
            intent.putExtra(Config.CURRENT_DEVICE_PART_ID, currentDevicePartId);
            startActivityForResult(intent, UPDATE_DEVICE_STANDARD_REQUEST_CODE);
        });

        mSwitch1Binding.includeTitle.ibtnExit.setOnClickListener(view -> {
            boolean xudianchi = currentInspectionTypeName.contains(Config.XUDIANCHI) && (currentInspectionTypeName.contains(Config.DIANYA) || currentInspectionTypeName.contains(Config.NEIZU));
            if (xudianchi) {
                Intent intent1 = new Intent();
                ComponentName componentName = new ComponentName("com.cnksi.sjjc", "com.cnksi.sjjc.activity.batteryactivity.BatteryTestActivity");
                intent1.putExtra(Config.CURRENT_BDZ_ID, currentBdzId);
                intent1.putExtra(Config.CURRENT_TASK_ID, currentTaskId);
                intent1.putExtra(Config.CURRENT_REPORT_ID, currentReportId);
                intent1.putExtra(Config.CURRENT_BDZ_NAME, currentBdzName);
                intent1.putExtra(Config.CURRENT_MAINTANENCE_BATTERY, currentInspectionType);
                intent1.putExtra(Config.CURRENT_FILENAME, PreferencesUtils.getString(currentActivity, Config.PICTURE_PREFIX, ""));
                intent1.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, currentInspectionTypeName);
                intent1.setComponent(componentName);
                startActivity(intent1);
            } else if ("maintenance_blqdzcs".equalsIgnoreCase(currentInspectionType)) {
                startActivity(new Intent(currentActivity, CopyAllValueActivity3.class));
            } else if (currentInspectionTypeName.contains("压板")) {
                startActivity(new Intent(currentActivity, CopyMaintenanceDeviceActivity.class));
            }
        });

        mSwitch1Binding.tvTips.setOnClickListener(view -> {
            if (!MediaRecorderUtils.getInstance().isRecording()) {
                audioFileName = DateUtils.getCurrentTime(CoreConfig.dateFormat6) + Config.AMR_POSTFIX;
                recordAudioUtils.startRecord(Config.AUDIO_FOLDER + audioFileName, () -> saveReportAudioFile());
            } else {
                CToast.showShort("当前正在录音");
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case ACTION_IMAGE:// 拍照后返回
                    addWaterTextToBitmap();
                    break;
                case CANCEL_RESULT_LOAD_IMAGE:// 删除所记录的照片后
                    ArrayList<String> cancelList = data.getStringArrayListExtra(Config.CANCEL_IMAGEURL_LIST);
                    if (cancelList != null && !cancelList.isEmpty()) {
                        for (String imageUrl : cancelList) {
                            mPictureList.remove(imageUrl.replace(Config.RESULT_PICTURES_FOLDER, ""));
                        }
                        String pics = StringUtils.ArrayListToString(mPictureList);
                        dbModel.add(SwitchPic.PIC, pics);
                        saveSwitchPic();
                    }
                    break;
                case UPDATE_DEFECT_STATUS_REQUEST_CODE: // 缺陷记录后返回该界面刷新
                    try {
                        String maxLevel = StandardSwitchOverService.getInstance().getStandardMaxLevel(dbModel.getString(StandardSwitchover.ID), currentReportId);
                        dbModel.add(DefectRecord.DEFECTLEVEL, maxLevel);
                        mHandler.sendEmptyMessage(LOAD_MORE_DATA);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getActionMasked() == ev.ACTION_DOWN && popMenu.isShowing())
            popMenu.dismiss();
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        CLog.e(newConfig.keyboard);
    }

    /**
     * 加水印在图片上
     */
    private void addWaterTextToBitmap() {
        CustomerDialog.showProgress(currentActivity, "正在处理图片...");
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap currentBitmap = BitmapUtil.createScaledBitmapByWidth(BitmapUtil.postRotateBitmap(Config.RESULT_PICTURES_FOLDER + currentImageName), ScreenUtils.getScreenWidth(currentActivity));
                currentBitmap = BitmapUtil.addText2Bitmap(currentBitmap, DateUtils.getCurrentTime(CoreConfig.dateFormat2), getResources().getDimensionPixelOffset(R.dimen.xs_global_text_size));
                BitmapUtil.saveBitmap(currentBitmap, Config.RESULT_PICTURES_FOLDER + currentImageName, 60);
                String pics = dbModel.getString(SwitchPic.PIC);
                pics = TextUtils.isEmpty(pics) ? currentImageName : pics + "," + currentImageName;
                dbModel.add(SwitchPic.PIC, pics);
                saveSwitchPic();
            }
        });
    }

    /**
     * 保存switchPic
     */
    private void saveSwitchPic() {
        SwitchPic pic = null;
        try {
            pic = StandardSwitchOverService.getInstance().findFirstPic(currentReportId, dbModel.getString(StandardSwitchover.ID));
        } catch (DbException e1) {
            e1.printStackTrace();
        }
        if (null == pic) {
            pic = new SwitchPic();
            pic.standSwitchId = dbModel.getString(StandardSwitchover.ID);
            pic.reportid = currentReportId;
            pic.bdzid = currentBdzId;
        }
        if (!TextUtils.isEmpty(audioFileName))
            pic.voice = audioFileName;
        pic.pic = dbModel.getString(SwitchPic.PIC);
        try {
            audioFileName = "";
            XunshiApplication.getDbUtils().saveOrUpdate(pic);
            mHandler.sendEmptyMessage(LOAD_MORE_DATA);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存抄录数据
     */
    private void saveOrUpdateInputValue(final int messageCode) {
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    XunshiApplication.getDbUtils().saveOrUpdate(report);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                DefectRecord defectRecord = null;
                for (DbModel model : dbModelList) {
                    if (model.getInt(StandardSwitchover.LEVEL) == 3 && "1".equalsIgnoreCase(model.getString(StandardSwitchover.ISCOPY))) {
                        defectRecord = DefectRecordService.getInstance().findFirstCopyRecord(currentReportId, model.getString(StandardSwitchover.ID));
                        if (null == defectRecord) {
                            defectRecord = new DefectRecord(currentReportId, currentBdzId, currentBdzName);
                        }
                        defectRecord.standSwitchId = model.getString(StandardSwitchover.ID);
                        defectRecord.val = model.getString(DefectRecord.VAL);
                        defectRecord.oldval = model.getString(DefectRecord.OLDVAL);
                        try {
                            XunshiApplication.getDbUtils().saveOrUpdate(defectRecord);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                }
                mHandler.sendEmptyMessage(messageCode);
            }
        });
    }

    @Override
    protected void onDestroy() {
        recordAudioUtils.onDestory();
        super.onDestroy();
    }

    class ToolPop extends PopupWindow {
        DbModel model;
        PopMenuBinding binding;
        View anchor;
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbModel = model;
                int i = v.getId();
                if (i == R.id.id_camera) {
                    FunctionUtils.takePicture(currentActivity, currentImageName = FunctionUtil.getCurrentImageName(currentActivity), Config.RESULT_PICTURES_FOLDER);

                } else if (i == R.id.id_defect) {
                    saveOrUpdateInputValue(0x00);
                    String deviceId = dbModel.getString(DefectRecord.DEVICEID);
                    String standId = dbModel.getString(StandardSwitchover.ID);
                    Intent intent = new Intent(currentActivity, DefectControlActivity.class);
                    intent.putExtra(Config.CURRENT_DEVICE_ID, deviceId);
                    intent.putExtra(Config.CURRENT_STANDARD_ID, standId);
                    intent.putExtra(Config.IS_NEED_SEARCH_DEFECT_REASON, true);
                    startActivityForResult(intent, UPDATE_DEFECT_STATUS_REQUEST_CODE);

                } else if (i == R.id.id_record) {
                    if (!MediaRecorderUtils.getInstance().isRecording()) {
                        String voice = dbModel.getString(SwitchPic.VOICE);
                        if (!TextUtils.isEmpty(voice)) {
                            audioFileName = voice;
                            FileUtils.deleteFile(Config.AUDIO_FOLDER + audioFileName);
                        }
                        audioFileName = DateUtils.getCurrentTime(CoreConfig.dateFormat6) + Config.AMR_POSTFIX;
                        recordAudioUtils.startRecord(Config.AUDIO_FOLDER + audioFileName, () -> {
                            String recordTime = recordAudioUtils.getAllRecordTime(Config.AUDIO_FOLDER + audioFileName);
                            dbModel.add(SwitchPic.VOICELENGTH, recordTime);
                            dbModel.add(SwitchPic.VOICE, audioFileName);
                            saveSwitchPic();
                        });
                    } else {
                        CToast.showShort(currentActivity, "当前正在录音");
                    }

                }
                dismiss();
            }
        };

        public ToolPop() {
            super(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.xs_pop_regular_menu, null, false);
            setContentView(binding.getRoot());
            setOutsideTouchable(true);
            initClickListener();
        }

        public void setModel(DbModel model) {
            this.model = model;
        }

        public void initClickListener() {
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(anchor, "rotation", 0, 360f);
                    rotationAnimator.start();
                }
            });
            binding.idCamera.setOnClickListener(listener);
            binding.idDefect.setOnClickListener(listener);
            binding.idRecord.setOnClickListener(listener);
        }

        public void show(View view) {
            anchor = view;
            showAsDropDown(view, -200, -view.getHeight(), Gravity.LEFT | Gravity.TOP);
        }

    }
}