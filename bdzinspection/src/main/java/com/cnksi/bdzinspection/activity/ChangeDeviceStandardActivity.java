package com.cnksi.bdzinspection.activity;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.ListContentDialogAdapter;
import com.cnksi.bdzinspection.adapter.StandardDefectDefineAdapter;
import com.cnksi.bdzinspection.adapter.StandardDefectDefineAdapter.OnAdapterViewClickListener;
import com.cnksi.bdzinspection.daoservice.DefectDefineService;
import com.cnksi.bdzinspection.daoservice.DeviceStandardsService;
import com.cnksi.bdzinspection.databinding.XsActivityChangeStandardBinding;
import com.cnksi.bdzinspection.databinding.XsContentListDialogBinding;
import com.cnksi.bdzinspection.databinding.XsDialogAddDefectDefineBinding;
import com.cnksi.bdzinspection.databinding.XsDialogTipsBinding;
import com.cnksi.bdzinspection.model.Defect;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.bdzinspection.utils.FunctionUtil;
import com.cnksi.common.Config;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.model.DeviceStandards;
import com.cnksi.common.utils.BitmapUtil;
import com.cnksi.common.utils.CommonUtils;
import com.cnksi.common.utils.KeyBoardUtils;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.FileUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.cnksi.common.Config.LOAD_DATA;
import static com.cnksi.common.Config.LOAD_MORE_DATA;
import static com.cnksi.core.utils.Cst.ACTION_IMAGE;
import static com.cnksi.core.utils.Cst.CROP_PICTURE;

/**
 * 编辑巡检标准（定期试验/全面/日常）
 *
 * @author Oliver
 */
public class ChangeDeviceStandardActivity extends BaseActivity implements OnAdapterViewClickListener {
    private StandardDefectDefineAdapter mDefectDefineAdapter;

    /**
     * 更换图片的dialog
     */
    private Dialog mChangePictureDialog = null;
    private ListContentDialogAdapter mListContentDialogAdapter = null;
    private Dialog mAddDefectDefineDialog = null;
    private String currentStandardId = "";
    private HashMap<String, ArrayList<Defect>> groupMap = null;
    private DeviceStandards mCurrentDeviceStandard;
    private String currentImageName;
    // 是否是删除巡检标准
    private boolean isDeleteDeviceStandard = false;
    // 是否是新增巡检标准
    private boolean isAddDeviceStandard = false;
    private boolean isAddDefectDefine = false;
    private Defect mCurrentDefectDefine = null;
    private String mCurrentDefectLevel = "";
    private XsActivityChangeStandardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.xs_activity_change_standard);

        initialUI();
        initialData();
        initOnClick();
    }

    private void initialUI() {
        getIntentValue();
        currentStandardId = getIntent().getStringExtra(Config.CURRENT_STANDARD_ID);
        isAddDeviceStandard = getIntent().getBooleanExtra(Config.IS_ADD_DEVICE_STANDARD, false);
        binding.rlTakeStandardImage.setVisibility(isAddDeviceStandard ? View.VISIBLE : View.GONE);
        binding.ivStandardImage.setVisibility(isAddDeviceStandard ? View.GONE : View.VISIBLE);
        binding.btnDelete.setVisibility(isAddDeviceStandard ? View.GONE : View.VISIBLE);
        if (isAddDeviceStandard) {
            binding.includeTitle.tvTitle.setText(R.string.xs_add_inspection_standard_str);
            mCurrentDeviceStandard = new DeviceStandards();
        } else {
            binding.includeTitle.tvTitle.setText(R.string.xs_edit_inspection_standard_str);
        }
    }

    private void initialData() {
        ExecutorManager.executeTask(() -> {
            if (!isAddDeviceStandard) {
                mCurrentDeviceStandard = DeviceStandardsService.getInstance().findDeviceStandardById(currentStandardId);
                mHandler.sendEmptyMessage(LOAD_DATA);
                groupMap = DefectDefineService.getInstance().findDefectDefineByStandardId(currentStandardId);
                mHandler.sendEmptyMessage(LOAD_MORE_DATA);
            }
        });

        binding.elvContainer.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            // TODO: 弹出编辑缺陷定义的dialog
            isAddDefectDefine = false;
            mCurrentDefectDefine = mDefectDefineAdapter.getChild(groupPosition, childPosition);
            mCurrentDefectLevel = mDefectDefineAdapter.getGroup(groupPosition);
            showAddDefectDefineDialog();

            return false;
        });
    }

    @Override
    protected void onRefresh(Message msg) {
        super.onRefresh(msg);
        switch (msg.what) {
            case LOAD_DATA:

                initDeviceStandard();

                break;
            case LOAD_MORE_DATA:
                if (mDefectDefineAdapter == null) {
                    mDefectDefineAdapter = new StandardDefectDefineAdapter(mActivity);
                    mDefectDefineAdapter.setOnAdapterViewClickListener(this);
                    binding.elvContainer.setAdapter(mDefectDefineAdapter);
                }
                mDefectDefineAdapter.setGroupList(groupMap.keySet());
                mDefectDefineAdapter.setGroupMap(groupMap);
                for (int i = 0, count = mDefectDefineAdapter.getGroupCount(); i < count; i++) {
                    binding.elvContainer.expandGroup(i);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 初始化巡检标准
     */
    private void initDeviceStandard() {
        if (mCurrentDeviceStandard != null) {
            int maxHeight = getResources().getDimensionPixelSize(R.dimen.xs_standard_image_minheight);
            int maxWidth = ScreenUtils.getScreenWidth(mActivity) / 2
                    - getResources().getDimensionPixelSize(R.dimen.xs_global_padding_left_right) * 2;
            String picPath = CommonUtils.getDefaultPicPath(mCurrentDeviceStandard.change_pic,
                    mCurrentDeviceStandard.pics);
            if (FileUtils.isFileExists(picPath)) {
                binding.ivStandardImage.setImageBitmap(BitmapUtil.getImageThumbnail(picPath, maxWidth, maxHeight));
            } else {
                binding.ivStandardImage.setImageResource(R.drawable.xs_ic_long_press);
            }
            binding.etContent.setText(mCurrentDeviceStandard.description);
        }
    }

    private void initOnClick() {
        binding.includeTitle.ibtnCancel.setOnClickListener(view -> ChangeDeviceStandardActivity.this.onBackPressed());
        binding.tvAddNewDefectDefine.setOnClickListener(view -> {
            isAddDefectDefine = true;
            mCurrentDefectDefine = null;
            if (isAddDeviceStandard) {
                if (ChangeDeviceStandardActivity.this.saveDeviceStandards(isAddDeviceStandard)) {
                    if (!TextUtils.isEmpty(currentStandardId)) {
                        ChangeDeviceStandardActivity.this.showAddDefectDefineDialog();
                    }
                }
            } else {
                ChangeDeviceStandardActivity.this.showAddDefectDefineDialog();
            }
            isAddDefectDefine = true;
            mCurrentDefectDefine = null;
            if (isAddDeviceStandard) {
                if (ChangeDeviceStandardActivity.this.saveDeviceStandards(isAddDeviceStandard)) {
                    if (!TextUtils.isEmpty(currentStandardId)) {
                        ChangeDeviceStandardActivity.this.showAddDefectDefineDialog();
                    }
                }
            } else {
                ChangeDeviceStandardActivity.this.showAddDefectDefineDialog();
            }
        });
        binding.btnSave.setOnClickListener(view -> {
            if (ChangeDeviceStandardActivity.this.saveDeviceStandards(false)) {
                // TODO:更新和添加巡检标准
                if (!TextUtils.isEmpty(currentStandardId)) {
                    Intent intent = new Intent();
                    ChangeDeviceStandardActivity.this.setResult(RESULT_OK, intent);
                    ChangeDeviceStandardActivity.this.finish();
                }
            }
        });

        binding.btnDelete.setOnClickListener(view -> {
            isDeleteDeviceStandard = true;
            ChangeDeviceStandardActivity.this.showSureTipsDialog();
        });

        binding.ivStandardImage.setOnClickListener(view -> {
            if (isAddDeviceStandard) {
                ChangeDeviceStandardActivity.this.showChangePictureDialog();
            }
        });

        binding.rlTakeStandardImage.setOnClickListener(view -> FunctionUtil.takePicture(mActivity,
                (currentImageName = FunctionUtil.getCurrentImageName(mActivity)),
                Config.CUSTOMER_PICTURES_FOLDER));
        binding.btnTakeStandardImage.setOnClickListener(view -> FunctionUtil.takePicture(mActivity,
                (currentImageName = FunctionUtil.getCurrentImageName(mActivity)),
                Config.CUSTOMER_PICTURES_FOLDER));

        binding.ivStandardImage.setOnLongClickListener(view -> {
            if (mCurrentDeviceStandard != null) {
                isDeleteDeviceStandard = false;
                ChangeDeviceStandardActivity.this.showChangePictureDialog();
            }
            return true;
        });
    }

    /**
     * 保存巡检标准
     */
    private boolean saveDeviceStandards(boolean isAdd) {
        String description = binding.etContent.getText().toString().trim();
        if (TextUtils.isEmpty(description)) {
            ToastUtils.showMessage( R.string.xs_please_input_inspection_standards_str);
            return false;
        }
        mCurrentDeviceStandard.description = description;
        // 巡检标准均应出现在全面巡检里面
        mCurrentDeviceStandard.kind = InspectionType.full.name();
        mCurrentDeviceStandard.duid = TextUtils.isEmpty(currentDevicePartId) ? mCurrentDeviceStandard.duid
                : currentDevicePartId;
        if (!TextUtils.isEmpty(currentStandardId)) {
            mCurrentDeviceStandard.staid = currentStandardId;
        }
        currentStandardId = DeviceStandardsService.getInstance().saveDeviceStandards(mCurrentDeviceStandard, isAdd);
        return !TextUtils.isEmpty(currentStandardId);
    }


    @Override
    public void OnAdapterViewClick(View view, String defectLevel, Defect define) {
        // TODO:删除 弹出确认提示框
        isDeleteDeviceStandard = false;
        mCurrentDefectDefine = define;
        mCurrentDefectLevel = defectLevel;
        showSureTipsDialog();
    }

    /**
     * 显示更换图片的菜单
     */
    XsContentListDialogBinding listDialogBinding;

    private void showChangePictureDialog() {
        if (!isAddDeviceStandard) {
            mVibrator.vibrate(100);
        }

        if (mListContentDialogAdapter == null) {
            List<String> functionArray = Arrays.asList(getResources()
                    .getStringArray(isAddDeviceStandard ? R.array.XS_AddStandardArray : R.array.XS_ChangStandardArray));
            mListContentDialogAdapter = new ListContentDialogAdapter(mActivity, functionArray);
        }
        if (mChangePictureDialog == null) {
            int dialogWidth = ScreenUtils.getScreenWidth(mActivity) * 9 / 10;
            listDialogBinding = XsContentListDialogBinding.inflate(getLayoutInflater());
            mChangePictureDialog = DialogUtils.createDialog(mActivity, listDialogBinding.getRoot(), dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        listDialogBinding.lvContainer.setAdapter(mListContentDialogAdapter);
        listDialogBinding.tvDialogTitle.setText(R.string.xs_picture_function_str);
        mChangePictureDialog.show();
        listDialogBinding.lvContainer.setOnItemClickListener((adapterView, view, position, l) -> {
            switch (position) {
                case 2: // 恢复默认照片
                    int maxHeight = ChangeDeviceStandardActivity.this.getResources().getDimensionPixelSize(R.dimen.xs_standard_image_minheight);
                    int maxWidth = ScreenUtils.getScreenWidth(mActivity) / 2
                            - ChangeDeviceStandardActivity.this.getResources().getDimensionPixelSize(R.dimen.xs_global_padding_left_right) * 2;
                    String picPath = CommonUtils.getDefaultPicPath("", mCurrentDeviceStandard.pics);
                    if (FileUtils.isFileExists(picPath)) {
                        binding.ivStandardImage.setImageBitmap(BitmapUtil.getImageThumbnail(picPath, maxWidth, maxHeight));
                    } else {
                        binding.ivStandardImage.setImageResource(R.drawable.xs_ic_long_press);
                    }
                    DeviceStandardsService.getInstance().updateStandardPic(mCurrentDeviceStandard, "");
                    break;
                case 1: // 更换图片

                    FunctionUtil.takePicture(mActivity,
                            (currentImageName = FunctionUtil.getCurrentImageName(mActivity)),
                            Config.CUSTOMER_PICTURES_FOLDER);

                    break;
                case 0:// 查看大图片
                    if (isAddDeviceStandard) {
                        ChangeDeviceStandardActivity.this.showImageDetails(mActivity,
                                Config.CUSTOMER_PICTURES_FOLDER + binding.ivStandardImage.getTag().toString());
                    } else {
                        picPath = CommonUtils.getDefaultPicPath(mCurrentDeviceStandard.change_pic,
                                mCurrentDeviceStandard.pics);
                        if (FileUtils.isFileExists(picPath)) {
                            ChangeDeviceStandardActivity.this.showImageDetails(mActivity, picPath);
                        }
                    }
                    break;
                default:
                    break;
            }
            mChangePictureDialog.dismiss();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case ACTION_IMAGE:
                    // 更换照片 切割照片
                    if (FileUtils.isFileExists(Config.CUSTOMER_PICTURES_FOLDER + currentImageName)) {
                        cropImageUri(Uri.fromFile(new File(Config.CUSTOMER_PICTURES_FOLDER + currentImageName)),
                                ScreenUtils.getScreenHeight(mActivity), ScreenUtils.getScreenHeight(mActivity),
                                CROP_PICTURE, currentImageName);
                    }
                    break;
                case CROP_PICTURE:
                    if (isAddDeviceStandard) {
                        binding.ivStandardImage.setVisibility(View.VISIBLE);
                        binding.rlTakeStandardImage.setVisibility(View.GONE);
                    }
                    // 更换设备部件图片
                    int maxHeight = getResources().getDimensionPixelSize(R.dimen.xs_standard_image_minheight);
                    int maxWidth = ScreenUtils.getScreenWidth(mActivity) / 2
                            - getResources().getDimensionPixelSize(R.dimen.xs_global_padding_left_right) * 2;
                    if (FileUtils.isFileExists(Config.CUSTOMER_PICTURES_FOLDER + currentImageName)) {
                        binding.ivStandardImage.setImageBitmap(BitmapUtil.getImageThumbnail(
                                Config.CUSTOMER_PICTURES_FOLDER + currentImageName, maxWidth, maxHeight));
                        binding.ivStandardImage.setTag(currentImageName);
                        if (isAddDeviceStandard && mCurrentDeviceStandard != null) {
                            mCurrentDeviceStandard.pics = currentImageName;
                        }
                        if (!isAddDeviceStandard) {
                            // 保存到数据
                            DeviceStandardsService.getInstance().updateStandardPic(mCurrentDeviceStandard, currentImageName);
                        }
                    } else {
                        ToastUtils.showMessage( R.string.xs_change_photo_failure_try_again_str);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 是否删除巡检标准
     */
    private XsDialogTipsBinding tipsBinding;

    private void showSureTipsDialog() {
        if (tipsDialog == null) {
            int dialogWidth = ScreenUtils.getScreenWidth(mActivity) * 9 / 10;
            int dialogHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
            tipsBinding = XsDialogTipsBinding.inflate(getLayoutInflater());
            tipsDialog = DialogUtils.createDialog(mActivity, tipsBinding.getRoot(), dialogWidth, dialogHeight);
        }
        tipsBinding.tvDialogTitle.setText(R.string.xs_dialog_tips_str);
        tipsBinding.tvDialogContent.setText(isDeleteDeviceStandard ? R.string.xs_dialog_tips_delete_standard_content
                : R.string.xs_dialog_tips_delete_defect_define_content);
        tipsBinding.btnSure.setText(R.string.xs_delete_str);
        tipsBinding.btnCancel.setText(R.string.xs_no_str);
        tipsDialog.show();
        tipsBinding.btnSure.setOnClickListener(view -> ChangeDeviceStandardActivity.this.operateDefect());
        tipsBinding.btnCancel.setOnClickListener(view -> tipsDialog.dismiss());
    }

    private void operateDefect() {
        if (isDeleteDeviceStandard) {
            boolean isSuccess = DeviceStandardsService.getInstance().deleteStandardById(currentStandardId);
            if (isSuccess) {
                Intent intent = getIntent();
                intent.putExtra(Config.CURRENT_STANDARD_ID, currentStandardId);
                setResult(RESULT_OK, intent);
            }
            mActivity.finish();
        } else {
            // TODO:删除缺陷定义
            if (DefectDefineService.getInstance().deleteDefectDefine(mCurrentDefectDefine)) {
                List<Defect> mDefectDefineList = groupMap.get(mCurrentDefectLevel);
                if (mDefectDefineList != null && mDefectDefineList.size() > 0) {
                    int index = -1;
                    for (int i = 0, count = mDefectDefineList.size(); i < count; i++) {
                        if (mCurrentDefectDefine.defectid == mDefectDefineList.get(i).defectid) {
                            index = i;
                            break;
                        }
                    }
                    if (index > -1) {
                        mDefectDefineList.remove(index);
                    }
                    if (mDefectDefineList.isEmpty()) {
                        groupMap.remove(mCurrentDefectLevel);
                    }
                    mHandler.sendEmptyMessage(LOAD_MORE_DATA);
                }
            }
        }
    }

    /**
     * 添加缺陷定义
     */
    XsDialogAddDefectDefineBinding defectDefineBinding;

    private void showAddDefectDefineDialog() {
        if (mAddDefectDefineDialog == null) {
            int dialogWidth = ScreenUtils.getScreenWidth(mActivity) * 9 / 10;
            int dialogHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
            defectDefineBinding = XsDialogAddDefectDefineBinding.inflate(getLayoutInflater());
            mAddDefectDefineDialog = DialogUtils.createDialog(mActivity, defectDefineBinding.getRoot(), dialogWidth, dialogHeight);
        }
        defectDefineBinding.tvDialogTitle.setText(R.string.xs_add_defect_define_title_str);
        // 恢复缺陷定义
        if (mCurrentDefectDefine != null) {
            defectDefineBinding.etContent.setText(mCurrentDefectDefine.description);
            if (Config.SERIOUS_LEVEL.equalsIgnoreCase(mCurrentDefectLevel)) {
                defectDefineBinding.rbSeriousDefect.setChecked(true);
            } else if (Config.CRISIS_LEVEL.equalsIgnoreCase(mCurrentDefectLevel)) {
                defectDefineBinding.rbCrisisDefect.setChecked(true);
            } else {
                defectDefineBinding.rbGeneralDefect.setChecked(true);
            }
        } else {
            defectDefineBinding.etContent.setText("");
            defectDefineBinding.rbGeneralDefect.setChecked(true);
            KeyBoardUtils.openKeybord(defectDefineBinding.etContent, mActivity);
        }
        mAddDefectDefineDialog.show();
        defectDefineBinding.btnCancel.setOnClickListener(view -> {
            KeyBoardUtils.closeKeybord(defectDefineBinding.etContent, mActivity);
            mAddDefectDefineDialog.dismiss();
        });

        defectDefineBinding.btnConfirm.setOnClickListener(view -> {
            String defectLevel = defectDefineBinding.rbGeneralDefect.getText().toString();
            if (defectDefineBinding.rbSeriousDefect.isChecked()) {
                defectLevel = defectDefineBinding.rbSeriousDefect.getText().toString();
            } else if (defectDefineBinding.rbCrisisDefect.isChecked()) {
                defectLevel = defectDefineBinding.rbCrisisDefect.getText().toString();
            }
            String description = defectDefineBinding.etContent.getText().toString().trim();
            if (TextUtils.isEmpty(description)) {
                ToastUtils.showMessage(R.string.xs_please_input_defect_content_str);
                return;
            }
            Defect mDefectDefine = null;
            if (isAddDefectDefine) {
                mDefectDefine = new Defect();
                mDefectDefine.staid = currentStandardId;
                mDefectDefine.description = description;
                mDefectDefine.level = defectLevel;
            } else {
                mDefectDefine = mCurrentDefectDefine;
                mDefectDefine.description = description;
                mDefectDefine.level = defectLevel;
            }
            if (DefectDefineService.getInstance().saveOrUpdateDefectDefine(mDefectDefine, isAddDefectDefine)) {
                ChangeDeviceStandardActivity.this.updateDefectDefineList();
            }
        });
    }

    /**
     * 更新缺陷定义列表
     */
    private void updateDefectDefineList() {
        ExecutorManager.executeTask(() -> {
            if (groupMap != null) {
                groupMap.clear();
            }
            groupMap = DefectDefineService.getInstance().findDefectDefineByStandardId(currentStandardId);
            mHandler.sendEmptyMessage(LOAD_MORE_DATA);
        });
    }

    @Override
    public void onBackPressed() {
        if (isAddDeviceStandard) {
            DeviceStandardsService.getInstance().deleteStandardById(currentStandardId);
        }
        super.onBackPressed();
    }
}
