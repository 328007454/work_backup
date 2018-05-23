package com.cnksi.bdzinspection.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.BigTypePreventAdapter;
import com.cnksi.bdzinspection.adapter.DevicePartRecylerAdapter;
import com.cnksi.bdzinspection.adapter.ListContentDialogAdapter;
import com.cnksi.bdzinspection.adapter.StandardAdapter;
import com.cnksi.bdzinspection.adapter.ViewHolder;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.daoservice.DevicePartService;
import com.cnksi.bdzinspection.daoservice.DeviceService;
import com.cnksi.bdzinspection.daoservice.DeviceTypeImageService;
import com.cnksi.bdzinspection.daoservice.PlacedDeviceService;
import com.cnksi.bdzinspection.daoservice.SpecialMenuService;
import com.cnksi.bdzinspection.daoservice.StandardService;
import com.cnksi.bdzinspection.databinding.XsActivityNewDevicedetailsBinding;
import com.cnksi.bdzinspection.databinding.XsDialogStandardSourceBinding;
import com.cnksi.bdzinspection.model.DeviceStandardsOper;
import com.cnksi.bdzinspection.model.DeviceTypeImage;
import com.cnksi.bdzinspection.model.PlacedDevice;
import com.cnksi.bdzinspection.model.SpecialMenu;
import com.cnksi.bdzinspection.utils.DialogUtils;
import com.cnksi.bdzinspection.utils.DisplayUtil;
import com.cnksi.bdzinspection.utils.FunctionUtil;
import com.cnksi.bdzinspection.utils.NextDeviceUtils;
import com.cnksi.bdzinspection.utils.OnViewClickListener;
import com.cnksi.bdzinspection.utils.PlaySound;
import com.cnksi.bdzinspection.utils.TTSUtils;
import com.cnksi.common.Config;
import com.cnksi.common.SystemConfig;
import com.cnksi.common.daoservice.DefectRecordService;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.model.Device;
import com.cnksi.common.model.DevicePart;
import com.cnksi.common.model.Standards;
import com.cnksi.common.utils.BitmapUtil;
import com.cnksi.common.utils.StringUtilsExt;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.FileUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.view.CustomerDialog;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.cnksi.common.Config.CANCEL_RESULT_LOAD_IMAGE;
import static com.cnksi.common.Config.LOAD_DATA;
import static com.cnksi.core.utils.Cst.ACTION_IMAGE;
import static com.cnksi.core.utils.Cst.CROP_PICTURE;

/**
 * @author nothing  on 2017/11/01
 */
public class NewDeviceDetailsActivity extends BaseActivity implements DevicePartRecylerAdapter.DevicePartClickListener {
    /**
     * 重点关注标准
     */
    public static final int IMPORTANT_STANDARD = 0X100;
    /**
     * 设备现存缺陷
     */
    public static final int DEVICE_EXIST_DEFECT = IMPORTANT_STANDARD + 1;
    /**
     * 设备信息
     */
    public static final int DEVICE_INFORMATION = DEVICE_EXIST_DEFECT + 1;
    /**
     * 设备部件信息
     */
    public static final int DEVICE_PART_INFORMATION = DEVICE_INFORMATION + 1;
    /**
     * 危险点
     */
    public static final int DANGER_POINT = DEVICE_PART_INFORMATION + 1;
    /**
     * 特殊巡检标准
     */
    public static final int PARTICULAR_STANDARD = DANGER_POINT + 1;
    /**
     * 更新设备缺陷
     */
    public static final int UPDATE_DEVICE_DEFECT_REQUEST_CODE = PARTICULAR_STANDARD + 1;
    /**
     * 选择设备图片
     */
    public static final int SELECT_DEVICE_IMAGE = UPDATE_DEVICE_DEFECT_REQUEST_CODE + 1;
    /**
     * 设备签到拍照
     */
    public static final int PHOTO_SIGN_IMAGE = SELECT_DEVICE_IMAGE + 1;
    /**
     * 更改设备图片
     */
    public static final String CHANGE_DEVICE_PIC_TYPE = "change_device_pic_type";
    XsActivityNewDevicedetailsBinding devicedetailsBinding;
    /**
     * 巡检标准来源dialog
     */
    XsDialogStandardSourceBinding sourceBinding;
    /**
     * 显示更换图片的菜单
     */
    ViewHolder changeHolder;
    GestureHandler gestureHandler = new GestureHandler();
    /**
     * copyItem表中包含设备该类型和该变电站下的所有抄录的集合
     */
    private List<String> copyDeviceIdList = new ArrayList<>();
    /**
     * 当前的设备
     */
    private Device mCurrentDevice = null;
    /**
     * 设备部件的集合
     */
    private List<DbModel> mDevicePartList = null;
    /**
     * 现存缺陷List
     */
    private List<DefectRecord> mExistDefectList = null;
    /**
     * 巡视标准
     */
    private List<DbModel> mStandardList = null;
    /**
     * 库里已经被标注的巡视标准
     */
    private HashMap<String, DeviceStandardsOper> staidMarkMap;
    /**
     * 当前设备所属的设备类型
     */
    private DbModel dbModel, bigTypeModel;
    /**
     * 危险点的dialog
     */
    private Dialog mDangerPointDialog = null;
    private ViewHolder holder = null;
    /**
     * 更换图片的dialog
     */
    private Dialog mChangePictureDialog = null;
    private ListContentDialogAdapter mListContentDialogAdapter = null;
    private DevicePartRecylerAdapter devicePartRecylerAdapter;
    /**
     * 巡视标准
     */
    private StandardAdapter mInspectionStandardAdapter = null;
    /**
     * 当前查看的巡检标准
     */
    private DbModel mCurrentStandard = null;
    /**
     * 巡检标准来源dialog
     */
    private Dialog mStandardSourceDialog = null;
    /**
     * 当前更换照片的名称
     */
    private String currentImageName;
    private SpecialMenu specialMenu;
    private PlacedDevice placedDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changedStatusColor();
        devicedetailsBinding = DataBindingUtil.setContentView(currentActivity, R.layout.xs_activity_new_devicedetails);
        getIntentValue();
        initialUI();
        initialData();
        initOnClick();
    }


    private void initialUI() {
        devicedetailsBinding.devicePartRecy.setVisibility(View.GONE);
        devicedetailsBinding.deviceStandardLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCurrentStandard = mStandardList.get(position);
                if (isParticularInspection((null == specialMenu) ? "" : specialMenu.standardsOrigin) || (InspectionType.special_xideng.equals(currentInspectionType))) {
                    Intent intent = new Intent(currentActivity, AddNewDefectActivity.class);
                    setIntentValue(intent);
                    startActivityForResult(intent, UPDATE_DEVICE_DEFECT_REQUEST_CODE);
                } else {
                    Intent intent = new Intent(currentActivity, DefectControlActivity.class);
                    setIntentValue(intent);
                    intent.putExtra(Config.IS_SHOW_DEFECT_REASON, true);
                    intent.putExtra(Config.CURRENT_STANDARD_ID, String.valueOf(mCurrentStandard.getString(Standards.STAID))); // 传递巡视标准ID
                    startActivityForResult(intent, UPDATE_DEVICE_DEFECT_REQUEST_CODE);
                }
            }
        });
        devicedetailsBinding.deviceStandardLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showStandardSource(mStandardList.get(position));
                return true;
            }
        });
        devicedetailsBinding.btPhotoSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FunctionUtil.takePicture(currentActivity, currentImageName = FunctionUtil.getCurrentImageName(currentActivity), Config.RESULT_PICTURES_FOLDER, PHOTO_SIGN_IMAGE);
            }
        });
        devicedetailsBinding.imgArrived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showImageDetails(currentActivity, Config.RESULT_PICTURES_FOLDER + placedDevice.pic, true);
                showImageDetails(currentActivity, Config.RESULT_PICTURES_FOLDER, imgList, true);
            }
        });
    }

    /**
     * 初始化设备信息<br>
     * 1.查询设备信息<br>
     * 2.查询设备的设备部件并查询设备部件关联记录的最高缺陷等级和缺陷数量<br>
     * 3.查询历史缺陷数量 一般 严重 危机
     */

    private void initialData() {
        ExecutorManager.executeTask(() -> {
            try {
                specialMenu = SpecialMenuService.getInstance().findCurrentDeviceType(currentInspectionType);
                // 1、查询设备
                mCurrentDevice = XunshiApplication.getDbUtils().findById(Device.class, currentDeviceId);
                staidMarkMap = StandardService.getInstance().findStandardMark(mCurrentDevice.bdzid, mCurrentDevice.deviceid);
                // 查询设备所属类型
                dbModel = DevicePartService.getInstance().getDeviceType(mCurrentDevice.dtid);
                mHandler.sendEmptyMessage(DEVICE_INFORMATION);
                if (!isParticularInspection((null == specialMenu) ? "" : specialMenu.standardsOrigin) && !(InspectionType.special_xideng.equals(currentInspectionType))) {
                    // 1、从库中查询设备部件
                    mDevicePartList = DevicePartService.getInstance().getDevicePart(mCurrentDevice.dtid, currentInspectionType, currentBdzId, mCurrentDevice.deviceid);
                    mHandler.sendEmptyMessage(DEVICE_PART_INFORMATION);
                    // 2、根据部件查询巡检标准，默认查询第一个
                    if (!mDevicePartList.isEmpty()) {
                        initStandardList(mDevicePartList.get(0).getString(DevicePart.DUID));
                    }
                } else {
                    mStandardList = StandardService.getInstance().findStandardsListFromSpecial(mCurrentDevice.bigid, currentInspectionType);
                    if (!mStandardList.isEmpty()) {
                        mHandler.sendEmptyMessage(LOAD_DATA);
                    }
                }

                // 历史缺陷
                mExistDefectList = DefectRecordService.getInstance().queryDefectByDeviceid(currentDeviceId, currentBdzId);
                if (!mExistDefectList.isEmpty()) {
                    TTSUtils.getInstance().startSpeaking(getResources().getString(R.string.xs_current_device_has_defect_format_str, mExistDefectList.size()+""));
                }
                mHandler.sendEmptyMessage(DEVICE_EXIST_DEFECT);
                //控制点及危险措施
                SqlInfo sqlInfo = new SqlInfo("select * from device_bigtype where bigid= '" + mCurrentDevice.bigid + "'");
                bigTypeModel = XunshiApplication.getDbUtils().findDbModelFirst(sqlInfo);
            } catch (DbException e) {
                e.printStackTrace();
            }
        });
        ExecutorManager.executeTask(() -> {
            placedDevice = PlacedDeviceService.getInstance().findDevicePlaced(currentReportId, currentDeviceId);
            if (placedDevice != null && placedDevice.isHasPhoto()) {
                runOnUiThread(() -> {
                    devicedetailsBinding.setHasSignPhoto(true);
                    imgList = StringUtils.stringToList(placedDevice.pic, ",");
                    refreshDevicePic();
                });
            }
        });
    }

    /**
     * 查询设备部件对应的全面巡视标准
     */
    private void initStandardList(final String devicePartId) {
        ExecutorManager.executeTask(() -> {
            String inspectionTypeName = currentInspectionType;
            // 1、从库中查询部件巡视标准
            mStandardList = StandardService.getInstance().findStandardListFromDB(devicePartId, inspectionTypeName);
            if (mStandardList == null) {
                mStandardList = new ArrayList<>();
            } else {
                //移除库已经被标记为删除的标准
                Iterator<DbModel> iterator = mStandardList.iterator();
                while (iterator.hasNext()) {
                    DbModel model = iterator.next();
                    DeviceStandardsOper oper = staidMarkMap.get(model.getString("staid"));
                    if (oper != null) {
                        if (oper.isDeleted()) {
                            iterator.remove();
                        } else if (oper.isMarkStandard()) {
                            model.add("isMark", "Y");
                        }
                    }
                }
            }
            // 2、查询台账部件巡视标准
            List<DbModel> modelStandardList = StandardService.getInstance().findStandardListByDevicePartId(devicePartId, currentDeviceId, inspectionTypeName);
            // 添加
            if (null != modelStandardList) {
                mStandardList.addAll(modelStandardList);
            }
            mHandler.sendEmptyMessage(LOAD_DATA);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            copyDeviceIdList = DeviceService.getInstance().findAllDeviceInCopyItem(currentInspectionType, currentBdzId);
            mHandler.sendEmptyMessage(DEVICE_INFORMATION);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case DEVICE_INFORMATION:
                if (null == mCurrentDevice || null == dbModel) {
                    return;
                }
                initDeviceContent();
                if (copyDeviceIdList.contains(mCurrentDevice.deviceid) && (!isParticularInspection()) && (!isRoutineNotCopy())) {
                    devicedetailsBinding.ibtnCopy.setVisibility(View.VISIBLE);
                    devicedetailsBinding.ibtnCopy.setImageResource(R.drawable.xs_copy_button_background);
                } else {
                    devicedetailsBinding.ibtnCopy.setVisibility(View.GONE);
                }
                break;
            case DEVICE_EXIST_DEFECT:
                String defectCount = getString(R.string.xs_defect_count_message, StringUtils.cleanString(mExistDefectList.size() + "个"));
                devicedetailsBinding.tvDefectCount.setText(StringUtils.changePartTextColor(currentActivity, defectCount, R.color.xs_light_red_color, defectCount.length() - StringUtils.cleanString(mExistDefectList.size() + "个").length(), defectCount.length()));
                if (mExistDefectList.isEmpty()) {
                    devicedetailsBinding.tvDefectCount.setText("当前设备无现存缺陷");
                }
                break;
            case DEVICE_PART_INFORMATION:
                devicedetailsBinding.devicePartRecy.setVisibility(View.VISIBLE);
                if (null == devicePartRecylerAdapter) {
                    devicePartRecylerAdapter = new DevicePartRecylerAdapter(devicedetailsBinding.devicePartRecy, mDevicePartList, R.layout.xs_new_device_image_layout);
                    devicePartRecylerAdapter.setNew(true);
                    devicedetailsBinding.devicePartRecy.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                    devicedetailsBinding.devicePartRecy.setAdapter(devicePartRecylerAdapter);
                    devicePartRecylerAdapter.setDevicePartListener(this);
                    if (!mDevicePartList.isEmpty() && !mDevicePartList.isEmpty()) {
                        currentDevicePartId = mDevicePartList.get(0).getString(DevicePart.DUID);
                        currentDevicePartName = mDevicePartList.get(0).getString(DevicePart.NAME);
                    }
                } else {
                    devicePartRecylerAdapter.setList(mDevicePartList);
                }
                break;
            case LOAD_DATA:
                //把标记的标准放在最后
                if (mStandardList != null) {
                    Collections.sort(mStandardList, (o1, o2) -> {
                        String s1 = o1.getString("isMark");
                        String s2 = o2.getString("isMark");
                        if (TextUtils.equals(s1, s2)) {
                            return 0;
                        }
                        return "Y".equals(s1) ? 1 : -1;
                    });
                }
                if (mInspectionStandardAdapter == null) {
                    mInspectionStandardAdapter = new StandardAdapter(currentActivity, mStandardList);
                    devicedetailsBinding.deviceStandardLv.setAdapter(mInspectionStandardAdapter);
                } else {
                    mInspectionStandardAdapter.setList(mStandardList);
                }
                devicedetailsBinding.scroollerContainer.smoothScrollTo(0, 0);
                break;
            default:
                break;
        }
    }

    /**
     * 初始化设备台帐信息
     */
    private void initDeviceContent() {
        devicedetailsBinding.tvTitle.setText((mCurrentDevice.isImportant() ? "★" : "") + mCurrentDevice.name);
        devicedetailsBinding.tvManufacturers.setText("生产厂家：" + (TextUtils.isEmpty(mCurrentDevice.manufacturer) ? "" : mCurrentDevice.manufacturer));
        devicedetailsBinding.tvProductModel.setText("产品型号：" + (TextUtils.isEmpty(mCurrentDevice.model) ? "" : mCurrentDevice.model));
        String date = StringUtils.cleanString(mCurrentDevice.commissioning_date);
        if (date.length() > 10) {
            date = date.substring(0, 10);
        }
        devicedetailsBinding.tvProductDate.setText("投产日期：" + date);
        setDeviceImage();
        devicedetailsBinding.ivDeviceImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showChangePictureDialog(v);
                return true;
            }
        });
    }

    private void setDeviceImage() {
        int maxWidth = AutoUtils.getPercentWidthSizeBigger(480);
        int maxHeight = AutoUtils.getPercentHeightSizeBigger(330);
        String picPath = TextUtils.isEmpty(mCurrentDevice.change_pic) ? (TextUtils.isEmpty(mCurrentDevice.pic) ? "" : mCurrentDevice.pic) : mCurrentDevice.change_pic;
        devicedetailsBinding.ivDeviceImage.setImageBitmap(BitmapUtil.getImageThumbnail(mCurrentDevice.getPic(picPath), maxWidth, maxHeight));
    }

    /**
     * 设置需要传递的值
     */
    private void setIntentValue(Intent intent) {
        intent.putExtra(Config.CURRENT_DEVICE_ID, currentDeviceId);
        intent.putExtra(Config.CURRENT_DEVICE_NAME, currentDeviceName);
        intent.putExtra(Config.CURRENT_DEVICE_PART_ID, currentDevicePartId);
        intent.putExtra(Config.CURRENT_DEVICE_PART_NAME, currentDevicePartName);
        intent.putExtra(Config.CURRENT_SPACING_ID, currentSpacingId);
        intent.putExtra(Config.CURRENT_SPACING_NAME, currentSpacingName);
        intent.putExtra(Config.IS_PARTICULAR_INSPECTION, isParticularInspection);
        intent.putExtra(Config.IS_DEVICE_PART, InspectionType.special_xideng.equals(currentInspectionType) ? true : (isParticularInspection((null == specialMenu) ? "" : specialMenu.standardsOrigin)));
    }

    private void initOnClick() {
        devicedetailsBinding.ibtnCopy.setOnClickListener(view -> {
            PlaySound.getIntance(currentActivity).play(R.raw.input);
            Intent intent = new Intent(currentActivity, CopyValueActivity2.class);
            intent.putExtra(Config.CURRENT_DEVICE_NAME, mCurrentDevice.name);
            intent.putExtra(Config.CURRENT_DEVICE_ID, mCurrentDevice.deviceid);
            intent.putExtra(Config.CURRENT_SPACING_ID, currentSpacingId);
            intent.putExtra(Config.CURRENT_SPACING_NAME, currentSpacingName);
            startActivityForResult(intent, UPDATE_DEVICE_DEFECT_REQUEST_CODE);
        });

        devicedetailsBinding.ibtnDanger.setOnClickListener(view -> {
            if (null != bigTypeModel && !TextUtils.isEmpty(bigTypeModel.getString("identification_prevent_measures"))) {
                showDangerPointDialog();
            } else {
                ToastUtils.showMessage( "该设备暂无危险点及控制措施");
            }
        });

        devicedetailsBinding.btAccidentDeal.setOnClickListener(view -> {
            Intent intentAccident = new Intent(currentActivity, AccidentExceptionActivity.class);
            intentAccident.putExtra(Config.CURRENT_DEVICE_ID, currentDeviceId);
            startActivity(intentAccident);
        });

        devicedetailsBinding.tvDefectCount.setOnClickListener(view -> {
            Intent intent;
            if (null == mExistDefectList || mExistDefectList.isEmpty()) {
                return;
            }
            intent = new Intent(currentActivity, DefectControlActivity.class);
            setIntentValue(intent);
            intent.putExtra(Config.ADD_NEW_DEFECT_RECORD, true);
            intent.putExtra(Config.IS_TRACK_DEFECT, true);
            intent.putExtra(Config.IS_SHOW_DEVICE_WIDGET, false);
            intent.putExtra(Config.IS_NEED_SEARCH_DEFECT_REASON, true);
            startActivityForResult(intent, UPDATE_DEVICE_DEFECT_REQUEST_CODE);
        });

        devicedetailsBinding.tvAddNewDefect.setOnClickListener(view -> {
            Intent intent;
            intent = new Intent(currentActivity, AddNewDefectActivity.class);
            setIntentValue(intent);
            startActivityForResult(intent, UPDATE_DEVICE_DEFECT_REQUEST_CODE);
        });
        devicedetailsBinding.ibtnCancel.setOnClickListener(view -> onBackPressed());
        devicedetailsBinding.ibtnSetting.setOnClickListener(view -> {
            Intent intent1 = new Intent(currentActivity, SettingCopyTypeActivity.class);
            intent1.putExtra(Config.CURRENT_DEVICE_NAME, mCurrentDevice.name);
            intent1.putExtra(Config.CURRENT_DEVICE_ID, mCurrentDevice.deviceid);
            intent1.putExtra(Config.CURRENT_SPACING_ID, currentSpacingId);
            intent1.putExtra(Config.CURRENT_SPACING_NAME, currentSpacingName);
            startActivity(intent1);
        });
    }

    private void showStandardSource(final DbModel standard) {
        if (mStandardSourceDialog == null) {
            sourceBinding = XsDialogStandardSourceBinding.inflate(getLayoutInflater(), devicedetailsBinding.llContainer, false);
            int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 9 / 10;
            int dialogHeight = ScreenUtils.getScreenHeight(currentActivity) * 5 / 10;
            mStandardSourceDialog = DialogUtils.createDialog(currentActivity, sourceBinding.getRoot(), dialogWidth, dialogHeight);
        }
        String source = StringUtilsExt.nullTo(standard.getString(Standards.ORIGIN), getString(R.string.xs_no_source_str));
        final String staid = standard.getString("staid");
        final DeviceStandardsOper oper = staidMarkMap.get(staid);
        boolean isLibStandard = "1".equals(standard.getString("islib"));
        sourceBinding.tvSourceName.setMinHeight(AutoUtils.getPercentHeightSizeBigger(120));
        sourceBinding.tvDialogTitle.setText(R.string.xs_standard_source_str);
        sourceBinding.tvSourceName.setText(source);
        if (isLibStandard) {
            sourceBinding.btnMark.setVisibility(View.VISIBLE);
            sourceBinding.btnMark.setText(oper != null ? R.string.xs_resume : R.string.xs_mark_standard);
            sourceBinding.btnMark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tips;
                    final DeviceStandardsOper mark;
                    if (oper == null) {
                        tips = "是否标注当前设备不适用于此条巡视标准？标注后此条巡视标准将显示为灰色。";
                        mark = DeviceStandardsOper.createMark(mCurrentDevice, staid, PreferencesUtils.get(Config.CURRENT_LOGIN_USER, ""));
                    } else {
                        mark = oper;
                        tips = "是否撤销不适用标注?";
                        mark.dlt = "1";
                    }
                    DialogUtils.showSureTipsDialog(currentActivity, devicedetailsBinding.llContainer, tips, new OnViewClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                XunshiApplication.getDbUtils().saveOrUpdate(mark);
                                ToastUtils.showMessage( "操作成功");
                                if ("1".equals(mark.dlt)) {
                                    standard.add("isMark", "N");
                                    staidMarkMap.remove(mark.staid);
                                } else {
                                    staidMarkMap.put(mark.staid, mark);
                                    standard.add("isMark", "Y");
                                }
                                //刷新界面
                                if (devicedetailsBinding.deviceStandardLv.getAdapter() != null) {
                                    ((BaseAdapter) devicedetailsBinding.deviceStandardLv.getAdapter()).notifyDataSetChanged();
                                }
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                            mStandardSourceDialog.dismiss();
                        }
                    });
                }
            });
        } else {
            sourceBinding.btnMark.setVisibility(View.GONE);
        }
        mStandardSourceDialog.show();
    }

    private void showChangePictureDialog(View view) {
        mVibrator.vibrate(100);
        if (mListContentDialogAdapter == null) {
            mListContentDialogAdapter = new ListContentDialogAdapter(currentActivity, Arrays.asList(getResources().getStringArray(R.array.XS_ChangStandardArray)));
        } else {
            mListContentDialogAdapter.setList(Arrays.asList(getResources().getStringArray(R.array.XS_ChangStandardArray)));
        }
        if (mChangePictureDialog == null) {
            int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 9 / 10;
            changeHolder = new ViewHolder(currentActivity, null, R.layout.xs_content_list_dialog, false);
            mChangePictureDialog = DialogUtils.createDialog1(currentActivity, devicedetailsBinding.llContainer, changeHolder, dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        }
        ((ListView) changeHolder.getView(R.id.lv_container)).setAdapter(mListContentDialogAdapter);
        ((TextView) changeHolder.getView(R.id.tv_dialog_title)).setText(R.string.xs_picture_function_str);
        mChangePictureDialog.show();
        ((ListView) changeHolder.getView(R.id.lv_container)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    // 恢复默认照片
                    case 2:
                        // 1、清空数据库中更换图片
                        DeviceService.getInstance().updateDeviceChangePic(mCurrentDevice, "");
                        // 2、设置设备图片
                        setDeviceImage();
                        break;
                    // 更换图片
                    case 1:
                        FunctionUtil.takePicture(currentActivity, (currentImageName = FunctionUtil.getCurrentImageName(currentActivity)), Config.CUSTOMER_PICTURES_FOLDER);
                        break;
                    // 查看大图片
                    case 0:
                        String picPath;
                        picPath = TextUtils.isEmpty(mCurrentDevice.change_pic) ? (TextUtils.isEmpty(mCurrentDevice.pic) ? "" : mCurrentDevice.pic) : mCurrentDevice.change_pic;
                        picPath = mCurrentDevice.getPic(picPath);
                        if (FileUtils.isFileExists(picPath)) {
                            showImageDetails(currentActivity, picPath);
                        }
                        break;
                    case 3:
                        // 类型库中查询设备类型图
                        try {
                            List<DeviceTypeImage> typeImageList = DeviceTypeImageService.getInstance().queryImage(mCurrentDevice.dtid);
                            ArrayList<String> imageList = new ArrayList<String>();
                            if (null != typeImageList && !typeImageList.isEmpty()) {
                                for (DeviceTypeImage typeImage : typeImageList) {
                                    imageList.add(Config.BDZ_INSPECTION_FOLDER + typeImage.image);
                                }
                                // 跳转选择图片
                                Intent intent = new Intent(NewDeviceDetailsActivity.this, ImageDetailsActivity.class);
                                intent.putStringArrayListExtra(Config.IMAGEURL_LIST, imageList);
                                intent.putExtra(Config.TITLE_NAME, "点击选择设备图片");
                                intent.putExtra(Config.IS_SHOW_PHOTO_FLAG, false);
                                intent.putExtra("select", true);
                                startActivityForResult(intent, SELECT_DEVICE_IMAGE);
                            } else {
                                ToastUtils.showMessageLong( "暂无图库可供选择");
                            }

                        } catch (DbException e) {
                            Log.e("", "查询设备类型“" + mCurrentDevice.dtid + "”出错了");
                            e.printStackTrace();
                        }
                        break;
                    default:
                }
                mChangePictureDialog.dismiss();
            }
        });
    }

    /**
     * 显示缺陷内容的dialog
     */
    private void showDangerPointDialog() {
        if (mDangerPointDialog == null) {
            int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 9 / 10;
            int dialogHeight = ScreenUtils.getScreenHeight(currentActivity) * 2 / 3;
            List<String> data = new ArrayList<>();
            data.add(bigTypeModel.getString("identification_prevent_measures"));
            holder = new ViewHolder(currentActivity, null, R.layout.xs_content_list_dialog, false);
            BigTypePreventAdapter adapter = new BigTypePreventAdapter(currentActivity, data, R.layout.xs_danger_point_item2);
            mDangerPointDialog = DialogUtils.createDialog1(currentActivity, devicedetailsBinding.llContainer, holder, dialogWidth, (data != null && data.size() > 4) ? dialogHeight : LinearLayout.LayoutParams.WRAP_CONTENT, true);
            ((TextView) holder.getView(R.id.tv_dialog_title)).setText("危险点及控制措施");
            ((ListView) holder.getView(R.id.lv_container)).setAdapter(adapter);
        }
        mDangerPointDialog.show();
    }

    @Override
    public void devicePartClick1(View v) {
        String devicePartId = v.getTag(R.id.xs_thrid_key).toString();
        String devicePartName = v.getTag(R.id.xs_second_key).toString();
        currentDevicePartId = devicePartId;
        currentDevicePartName = devicePartName;
        initStandardList(devicePartId);
        devicePartRecylerAdapter.notifyDataSetChanged();
    }

    List<String> imgList = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case ACTION_IMAGE:
                    // 更换照片 切割照片
                    if (FileUtils.isFileExists(Config.CUSTOMER_PICTURES_FOLDER + currentImageName)) {
                        cropImageUri(Uri.fromFile(new File(Config.CUSTOMER_PICTURES_FOLDER + currentImageName)), ScreenUtils.getScreenHeight(currentActivity), ScreenUtils.getScreenHeight(currentActivity), CROP_PICTURE, currentImageName);
                    }
                    break;
                case CROP_PICTURE:
                    // 2、保存到数据库
                    DeviceService.getInstance().updateDeviceChangePic(mCurrentDevice, currentImageName);
                    // 3、更换设备图片
                    setDeviceImage();
                    break;
                case SELECT_DEVICE_IMAGE:
                    if (!data.getStringArrayListExtra(Config.CANCEL_IMAGEURL_LIST).isEmpty()) {
                        currentImageName = data.getStringArrayListExtra(Config.CANCEL_IMAGEURL_LIST).get(0).replace(Config.BDZ_INSPECTION_FOLDER, "");
                        // 2、保存到数据库
                        DeviceService.getInstance().updateDeviceChangePic(mCurrentDevice, currentImageName);
                        // 3、更换设备图片
                        setDeviceImage();
                    }
                    break;
                case UPDATE_DEVICE_DEFECT_REQUEST_CODE:
                    // 历史缺陷
                    searchCurrentDeviceExistDefect();
                    break;
                case PHOTO_SIGN_IMAGE:
                    CustomerDialog.showProgress(this, "处理水印中...");
                    ExecutorManager.executeTask(() -> {
                        Bitmap bitmap = BitmapUtil.createScaledBitmapByHeight(Config.RESULT_PICTURES_FOLDER + currentImageName, ScreenUtils.getScreenHeight(currentActivity));
                        String tips = PreferencesUtils.get(Config.CURRENT_LOGIN_USER, "");
                        tips = tips + "\n" + DateUtils.getCurrentLongTime();
                        bitmap = BitmapUtil.addText2Bitmap(bitmap, tips, 60);
                        BitmapUtil.saveBitmap(bitmap, Config.RESULT_PICTURES_FOLDER + currentImageName);
                        if (placedDevice == null) {
                            placedDevice = PlacedDevice.create(mCurrentDevice, currentReportId);
                        } else {
                            placedDevice.update_time = DateUtils.getCurrentLongTime();
                        }
                        placedDevice.setPlacedWayHighest("photo");
                        imgList.add(currentImageName);
                        placedDevice.pic = StringUtils.arrayListToString(imgList);
                        try {
                            XunshiApplication.getDbUtils().saveOrUpdate(placedDevice);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(() -> {
                            devicedetailsBinding.setHasSignPhoto(true);
                            CustomerDialog.dismissProgress();
                            refreshDevicePic();
                        });
                    });
                    break;
                case CANCEL_RESULT_LOAD_IMAGE:
                    List<String> list = data.getStringArrayListExtra(Config.CANCEL_IMAGEURL_LIST);
                    if (null != list && !list.isEmpty()) {
                        for (String file : list) {
                            FileUtils.deleteFile(file);
                            imgList.remove(file.replace(Config.RESULT_PICTURES_FOLDER, ""));
                        }
                        refreshDevicePic();
                        try {
                            placedDevice.pic = StringUtils.arrayListToString(imgList);
                            XunshiApplication.getDbUtils().saveOrUpdate(placedDevice);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 判断设备拍照后数量的显示
     */
    private void refreshDevicePic() {
        if (imgList != null && imgList.size() > 1) {
            devicedetailsBinding.tvPicNum.setText(String.valueOf(imgList.size()));
            devicedetailsBinding.tvPicNum.setVisibility(View.VISIBLE);
        } else if (imgList.isEmpty()) {
            devicedetailsBinding.setHasSignPhoto(false);
            devicedetailsBinding.tvPicNum.setVisibility(View.GONE);
        } else {
            devicedetailsBinding.tvPicNum.setVisibility(View.GONE);
        }
    }

    /**
     * 查询当前设备的历史缺陷
     */
    public void searchCurrentDeviceExistDefect() {
        ExecutorManager.executeTask(new Runnable() {
            @SuppressLint("StringFormatMatches")
            @Override
            public void run() {
                mExistDefectList = DefectRecordService.getInstance().queryDefectByDeviceid(currentDeviceId, currentBdzId);
                if (mExistDefectList != null && !mExistDefectList.isEmpty()) {
                    TTSUtils.getInstance().startSpeaking(getResources().getString(R.string.xs_current_device_has_defect_format_str, mExistDefectList.size()));
                }
                mHandler.sendEmptyMessage(DEVICE_EXIST_DEFECT);
                if (!isParticularInspection((null == specialMenu) ? "" : specialMenu.standardsOrigin) && !(InspectionType.special_xideng.equals(currentInspectionType))) {
                    // 更新设备部件的缺陷数量显示
                    // 1、从库中查询设备部件
                    mDevicePartList = DevicePartService.getInstance().getDevicePart(mCurrentDevice.dtid, currentInspectionType, currentBdzId, mCurrentDevice.deviceid);
                    mHandler.sendEmptyMessage(DEVICE_PART_INFORMATION);
                }

            }
        });
    }

    /**************************
     * TouchListener 滑动台帐切换下一个设备信息
     ****************************************/

    @Override
    public void onBackPressed() {
        if (mCurrentDevice.isImportant() && SystemConfig.isMustPicImportantDevice() && (placedDevice == null || !placedDevice.isHasPhoto())) {
            DialogUtils.showSureTipsDialog(currentActivity, null, "当前设备是关键设备，按照规定应拍照确认！是否返回？", new OnViewClickListener() {
                @Override
                public void onClick(View v) {
                    NewDeviceDetailsActivity.super.onBackPressed();
                }
            });
        } else {
            super.onBackPressed();
        }
    }

    //25%-%75 右滑  %75-%25 左滑
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return gestureHandler.doEventFling(ev) || super.dispatchTouchEvent(ev);
//        return gestureHandler.doEventFling(ev) || super.dispatchTouchEvent(ev);
    }


    class GestureHandler {
        int sWidth = DisplayUtil.getInstance().getWidth();
        int sHeight = DisplayUtil.getInstance().getHeight();
        PointF down;
        float minY, maxY;
        long downTime;
        double margin = sWidth * 0.035;
        double height = sHeight * 0.2;
        double width = sWidth * 0.5;
        boolean work = false;

        public boolean doEventFling(MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    downTime = System.currentTimeMillis();
                    down = new PointF(event.getX(), event.getY());
                    minY = maxY = down.y;
                    if (down.x < margin || (sWidth - down.x) < margin) {
                        work = true;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (work) {
                        if (event.getY() > down.y) {
                            maxY = event.getY();
                        } else {
                            minY = event.getY();
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (work) {
                        handle(new PointF(event.getX(), event.getY()));
                        work = false;
                        return true;
                    }
                    work = false;
                default:
            }
            return work;
        }

        public boolean handle(PointF up) {
            long upTime = System.currentTimeMillis();
            float tWidth = Math.abs(down.x - up.x);
            if (maxY - minY < height && tWidth > width && (upTime - downTime) / tWidth < 2.5) {
                if (down.x < margin) {
                    left();
                    return true;
                }
                if ((sWidth - down.x) < margin) {
                    right();
                    return true;
                }
            }
            return false;
        }


        public boolean left() {
            DbModel model = NextDeviceUtils.getInstance().getLeft(mCurrentDevice.device_type, mCurrentDevice.deviceid);
            if (model == null) {
                ToastUtils.showMessage( "没有找到上一个设备！");
                return false;
            }
            jumpDevice(model, R.anim.xs_left_to_right);
            return true;
        }


        public boolean right() {
            DbModel model = NextDeviceUtils.getInstance().getRight(mCurrentDevice.device_type, mCurrentDevice.deviceid);
            if (model == null) {
                ToastUtils.showMessage( "没有找到下一个设备！");
                return false;
            }
            jumpDevice(model, R.anim.xs_right_to_left);
            return true;
        }

        private void jumpDevice(DbModel model, int anim1) {
            currentDeviceId = model.getString("deviceId");
            currentDeviceName = model.getString("name");
            devicedetailsBinding.getRoot().startAnimation(android.view.animation.AnimationUtils.loadAnimation(currentActivity, anim1));
            if (devicePartRecylerAdapter != null) {
                devicePartRecylerAdapter.setClickPosition(0);
            }
            initialUI();
            initialData();
        }
    }
}