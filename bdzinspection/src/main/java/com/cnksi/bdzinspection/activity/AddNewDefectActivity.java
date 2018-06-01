package com.cnksi.bdzinspection.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.DefectDefineAdapter;
import com.cnksi.bdzinspection.daoservice.DefectDefineService;
import com.cnksi.bdzinspection.databinding.XsActivityAddNewDefectBinding;
import com.cnksi.bdzinspection.model.Defect;
import com.cnksi.common.utils.FunctionUtil;
import com.cnksi.common.Config;
import com.cnksi.common.activity.DrawCircleImageActivity;
import com.cnksi.common.base.BaseActivity;
import com.cnksi.common.daoservice.DefectRecordService;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.model.DevicePart;
import com.cnksi.common.utils.BitmapUtil;
import com.cnksi.common.utils.KeyBoardUtils;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.utils.ToastUtils;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cnksi.common.Config.CANCEL_RESULT_LOAD_IMAGE;
import static com.cnksi.common.Config.LOAD_DATA;
import static com.cnksi.core.utils.Cst.ACTION_IMAGE;

/**
 * 增加新缺陷的界面
 *
 * @author Oliver
 */
public class AddNewDefectActivity extends BaseActivity {
    private DefectDefineAdapter mDefectDefineAdapter = null;
    private List<DbModel> dataList = null;

    /**
     * 缺陷照片的集合
     */
    private ArrayList<String> mDefectImageList = new ArrayList<String>();
    /**
     * 当前缺陷图片的名称
     */
    private String currentImageName = "";
    /**
     * 当前的
     */
    private DbModel mCurrentDbModel = null;
    /**
     * 是否记录了缺陷
     */
    private boolean isRecordDefect = false;

    private String defectContentPre;

    XsActivityAddNewDefectBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(mActivity, R.layout.xs_activity_add_new_defect);
        initialUI();
        initOnClick();
    }


    public void initialUI() {
        getIntentValue();
        if (null != getIntent()) {
            defectContentPre = getIntent().getStringExtra(Config.DEFECT_COUNT_KEY);
        }
            binding.includeTitle.tvTitle.setText(R.string.xs_add_new_defect_str);
        binding.etInputDefectContent.addTextChangedListener(new TextWatcher() {

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

    /**
     * 查询相似的内容
     *
     * @param content
     */
    private void searchData(final String content) {
        ExecutorManager.executeTask(() -> {
            try {
                dataList = DefectDefineService.getInstance().findDefectDefineByDeviceIdAndContent(currentDeviceId,
                        content);
            } catch (DbException e) {
                e.printStackTrace();
            }
            if (dataList == null) {
                dataList = new ArrayList<DbModel>();
            }
            mHandler.sendEmptyMessage(LOAD_DATA);
        });
    }

    private void initOnClick() {
        binding.btnConfirm.setOnClickListener(view -> AddNewDefectActivity.this.saveDefect());
        binding.ibtnTakePicture.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(binding.etInputDefectContent.getText().toString().trim())) {
                FunctionUtil.takePicture(AddNewDefectActivity.this,
                        currentImageName = FunctionUtil.getCurrentImageName(AddNewDefectActivity.this), Config.RESULT_PICTURES_FOLDER);
            } else {
                ToastUtils.showMessage("请先填写缺陷内容！");
            }
        });
        binding.includeTitle.ibtnCancel.setOnClickListener(view -> {
            if (isRecordDefect) {
                AddNewDefectActivity.this.setResult(RESULT_OK);
            }
            AddNewDefectActivity.this.finish();
        });
        binding.ivNewDefectPhoto.setOnClickListener(view -> {
            // 显示大图
            if (mDefectImageList != null && !mDefectImageList.isEmpty()) {
                AddNewDefectActivity.this.showImageDetails(AddNewDefectActivity.this, StringUtils.addStrToListItem(mDefectImageList, Config.RESULT_PICTURES_FOLDER),
                        true);
            }
        });

        binding.lvContainer.setOnItemClickListener((parent, view, position, l) -> {
            mCurrentDbModel = (DbModel) parent.getItemAtPosition(position);
            if (Config.CRISIS_LEVEL.equalsIgnoreCase(mCurrentDbModel.getString(Defect.LEVEL))) {
                binding.rbCrisisDefect.setChecked(true);
            } else if (Config.SERIOUS_LEVEL.equalsIgnoreCase(mCurrentDbModel.getString(Defect.LEVEL))) {
                binding.rbSeriousDefect.setChecked(true);
            } else {
                binding.rbGeneralDefect.setChecked(true);
            }
            binding.etInputDefectContent.setText(mCurrentDbModel.getString(Defect.DESCRIPTION));
            KeyBoardUtils.closeKeybord(mActivity);
        });
    }

    /**
     * 保存缺陷
     */
    private void saveDefect() {
        String defectContent = binding.etInputDefectContent.getText().toString().trim();
        String departmentName = PreferencesUtils.get(Config.CURRENT_DEPARTMENT_NAME, "");
        if (TextUtils.isEmpty(defectContent)) {
            ToastUtils.showMessage( R.string.xs_please_input_or_select_defect_reason_str);
            return;
        }
        String currentDefectLevel = Config.GENERAL_LEVEL_CODE;
        if (binding.rbSeriousDefect.isChecked()) {
            currentDefectLevel = Config.SERIOUS_LEVEL_CODE;
        } else if (binding.rbCrisisDefect.isChecked()) {
            currentDefectLevel = Config.CRISIS_LEVEL_CODE;
        }
        DefectRecord record = new DefectRecord(currentReportId, // 报告id
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
                mCurrentDbModel == null ? "" : mCurrentDbModel.getString(Defect.STAID), // 巡视标准id
                StringUtils.arrayListToString(mDefectImageList)// pics图片
        );
        record.remark = TextUtils.isEmpty(defectContentPre) ? "" : defectContentPre;
        record.discoverer_unit = departmentName;
        if (binding.rbInflunceNo.isChecked()) {
            record.hasInfluenceDbz = "否";
        } else if (binding.rbInflunceYes.isChecked()) {
            record.hasInfluenceDbz = "是";
        } else {
            record.hasInfluenceDbz = "不清楚";
        }
        try {
            DefectRecordService.getInstance().saveOrUpdate(record);
            isRecordDefect = true;
        } catch (DbException e) {
            e.printStackTrace();
        }

        binding.etInputDefectContent.setText("");
        binding.rbGeneralDefect.setChecked(true);
        binding.ivNewDefectPhoto.setImageBitmap(null);
        // 清空图片数量
        binding.tvDefectCount.setVisibility(View.GONE);
        if (mDefectImageList != null) {
            mDefectImageList.clear();
        }
        currentImageName = "";

        setResult(RESULT_OK);
        this.finish();
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:

                if (mDefectDefineAdapter == null) {
                    mDefectDefineAdapter = new DefectDefineAdapter(mActivity, dataList);
                    binding.lvContainer.setAdapter(mDefectDefineAdapter);
                } else {
                    mDefectDefineAdapter.setList(dataList);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case ACTION_IMAGE:

                    // 拍摄的缺陷照片 进行圆圈标记操作
                    mDefectImageList.add(currentImageName);
                    DrawCircleImageActivity.with(mActivity).setTxtContent(currentDeviceName + "\n" + binding.etInputDefectContent.getText().toString() + "\n"
                            + DateUtils.getFormatterTime(new Date(), DateUtils.yyyy_MM_dd_HH_mm))
                            .setPath(Config.RESULT_PICTURES_FOLDER + currentImageName).start();
                    break;
                case CANCEL_RESULT_LOAD_IMAGE:

                    ArrayList<String> cancelList = data.getStringArrayListExtra(Config.CANCEL_IMAGE_URL_LIST_KEY);
                    if (cancelList != null) {
                        for (String imageUrl : cancelList) {
                            mDefectImageList.remove(imageUrl.replace(Config.RESULT_PICTURES_FOLDER, ""));
                        }
                    }
                case LOAD_DATA:
                    // 显示缺陷图片右上角的数字
                    if (mDefectImageList != null && mDefectImageList.size() > 1) {
                        binding.tvDefectCount.setVisibility(View.VISIBLE);
                        binding.tvDefectCount.setText(String.valueOf(mDefectImageList.size()));
                    } else {
                        binding.tvDefectCount.setVisibility(View.GONE);
                    }
                    int newWidth = getResources().getDimensionPixelSize(R.dimen.xs_new_defect_photo_height);
                    binding.ivNewDefectPhoto
                            .setImageBitmap(
                                    BitmapUtil.getImageThumbnail(
                                            (mDefectImageList == null || mDefectImageList.isEmpty()) ? ""
                                                    : Config.RESULT_PICTURES_FOLDER + mDefectImageList.get(0),
                                            newWidth, newWidth));

                    break;
                default:
                    break;
            }
        }
    }

}
