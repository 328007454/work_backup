package com.cnksi.defect.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.cnksi.common.Config;
import com.cnksi.common.activity.DrawCircleImageActivity;
import com.cnksi.common.activity.ImageDetailsActivity;
import com.cnksi.common.daoservice.DefectRecordService;
import com.cnksi.common.daoservice.DeviceService;
import com.cnksi.common.model.CopyItem;
import com.cnksi.common.model.CopyResult;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.model.TreeNode;
import com.cnksi.common.utils.CopyViewUtil;
import com.cnksi.common.utils.FunctionUtil;
import com.cnksi.common.utils.PlaySound;
import com.cnksi.common.utils.ShowCopyHistroyDialogUtils;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.defect.R;
import com.cnksi.defect.adapter.DeviceDefectAdapter;
import com.cnksi.defect.databinding.FragmentTrackDefectBinding;
import com.cnksi.defect.defect_enum.DefectEnum;
import com.cnksi.defect.utils.CopyHelper;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cnksi.common.Config.CANCEL_RESULT_LOAD_IMAGE;
import static com.cnksi.core.utils.Cst.ACTION_IMAGE;

/**
 * @author Mr.K  on 2018/5/30.
 * @decrption 跟踪缺陷
 */

public class TrackDefectFragment extends BaseDefectFragment implements CopyViewUtil.KeyBordListener {

    private FragmentTrackDefectBinding binding;
    private DeviceDefectAdapter deviceDefectAdapter;
    private String defectLevel;
    /**
     * 当前缺陷图片的名称
     */
    protected String currentImageName = "";

    /**
     * 缺陷照片的集合
     */
    private ArrayList<String> mDefectImageList = new ArrayList<>();
    private String infulenceEleNet;
    /**
     * 查询数据
     */
    List<TreeNode> data;
    private CopyHelper copyViewUtil;

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_track_defect;
    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        ExecutorManager.executeTaskSerially(() -> {
            DbModel deviceModel = DeviceService.getInstance().findCopySF6DefectDevice(deviceId, bdzId);
            if (null != deviceModel) {
                copyViewUtil.device = deviceModel;
                data = copyViewUtil.loadItem();
            }
            getActivity().runOnUiThread(() -> {
                if (defectRecords != null) {
                    if (deviceDefectAdapter == null) {
                        deviceDefectAdapter = new DeviceDefectAdapter(R.layout.adapter_defect_item, defectRecords);
                        binding.includeDefect.lvContainer.setLayoutManager(new LinearLayoutManager(getContext()));
                        binding.includeDefect.lvContainer.setAdapter(deviceDefectAdapter);
                        deviceDefectAdapter.setCurrentModel(Config.TRACK_DEFECT_MODEL);
                        deviceDefectAdapter.setItemClickListener(this);
                    } else {
                        deviceDefectAdapter.setList(defectRecords);
                    }
                }
                if (data!=null&&!data.isEmpty()) {
                    copyViewUtil.createCopyView(getActivity(), data, binding.contanierSf6);
                }
            });
        });
    }

    @Override
    protected void initUI() {
        super.initUI();
        binding = (FragmentTrackDefectBinding) fragmentDataBinding;
        binding.includeAdd.tvSelectDevicePart.setVisibility(View.GONE);
        initOnClick();
        copyViewUtil = new CopyHelper(getActivity(), currentReportId, bdzId, "full");
        copyViewUtil.setKeyBordListener(this);
        copyViewUtil.setItemClickListener((v, item, position) -> {
            // 显示历史曲线
            TrackDefectFragment.this.hideKeyBord();
            ShowCopyHistroyDialogUtils.showHistory(getActivity(), item);
        });
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isFirstLoad) {
            lazyLoad();
        }
    }

    private void initOnClick() {
        binding.containerRgEleInternet.setOnCheckedChangeListener(onCheckedChangeListener);

        binding.includeAdd.ibtnTakePicture.setOnClickListener(v -> {
            if (selectDefect == null) {
                ToastUtils.showMessage("请点击需要跟踪的缺陷");
            }
            String defectContent = binding.includeAdd.etInputDefectContent.getText().toString();
            if (TextUtils.isEmpty(defectContent)) {
                ToastUtils.showMessage("请先输入缺陷内容");
                return;
            }
            currentImageName = FunctionUtil.getCurrentImageName(getActivity());
            FunctionUtil.takePicture(getActivity(), currentImageName, picParentFolder, ACTION_IMAGE);
        });


        binding.btnConfirm.setOnClickListener(v -> {
            if (selectDefect == null) {
                ToastUtils.showMessage("请点击需要跟踪的缺陷");
                return;
            }
            saveData();


        });

        binding.includeAdd.ivNewDefectPhoto.setOnClickListener(v -> {
            if (mDefectImageList.isEmpty()) {
                ToastUtils.showMessage("无缺陷照片");
                return;
            }
            new ImageDetailsActivity.Builder(getActivity()).setImageUrlList(StringUtils.addStrToListItem(mDefectImageList, picParentFolder)).setShowDelete(true).setDeleteFile(true).start();
        });
    }

    private void saveData() {
        if (data != null && !data.isEmpty()) {
            copyViewUtil.setSaveCurrentData(true);
            copyViewUtil.valueNullTips(true);
            if (!copyViewUtil.saveAll()) {
                ToastUtils.showMessage("请填写设备抄录数据");
                return;
            }
        }

        PlaySound.getIntance(getActivity()).play(R.raw.track);
        selectDefect.has_track = "Y";
        int checkedId = binding.includeAdd.containerRbGeneralDefect.getmCheckedId();
        if (checkedId == R.id.rb_general_defect) {
            defectLevel = DefectEnum.general.value;
        } else if (checkedId == R.id.rb_serious_defect) {
            defectLevel = DefectEnum.serious.value;
        } else if (checkedId == R.id.rb_crisis_defect) {
            defectLevel = DefectEnum.critical.value;
        } else if (checkedId == R.id.rb_problem_defect) {
            defectLevel = DefectEnum.problem.value;
        } else if (checkedId == R.id.rb_hidden_defect) {
            defectLevel = DefectEnum.hidden.value;
        }
        try {
            //复制一条跟踪的缺陷保持除内容不同外其余相同,将原有的缺陷跟踪字段置为"Y"
            DefectRecordService.getInstance().saveOrUpdate(selectDefect);
            DefectRecord newDefect = selectDefect;
            newDefect.defectid = DefectRecord.getDefectId();
            newDefect.defectlevel = defectLevel;
            newDefect.pics = StringUtils.arrayListToString(mDefectImageList);
            newDefect.description = binding.includeAdd.etInputDefectContent.getText().toString();
            newDefect.has_track = "N";
            newDefect.discovered_date = DateUtils.getCurrentShortTime();
            newDefect.hasInfluenceDbz = infulenceEleNet;
            DefectRecordService.getInstance().saveOrUpdate(newDefect);
            defectRecords.remove(selectDefect);
            defectRecords.add(newDefect);
            defectId = newDefect.defectid;
            deviceDefectAdapter.clearSelectStatus();
            deviceDefectAdapter.notifyDataSetChanged();
            mDefectImageList.clear();
            selectDefect = null;
            reFreshPicUI();
            binding.includeAdd.etInputDefectContent.setText("");
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void setClickDefectData(View v, DefectRecord record) {
        if (v.getId() == R.id.iv_defect_image) {
            final ArrayList<String> listPicDis = StringUtils.stringToList(record.pics);
            ImageDetailsActivity.with(getActivity()).setPosition(0).setImageUrlList(StringUtils.addStrToListItem(listPicDis, Config.RESULT_PICTURES_FOLDER))
                    .setDeleteFile(false).setShowDelete(false).start();
        } else {
            mDefectImageList.clear();
            binding.includeAdd.etInputDefectContent.setText(TextUtils.isEmpty(record.description) ? "" : record.description);
            if (TextUtils.equals(record.defectlevel, DefectEnum.critical.value)) {
                binding.includeAdd.rbCrisisDefect.setChecked(true);
                defectLevel = DefectEnum.critical.value;
            } else if (TextUtils.equals(record.defectlevel, DefectEnum.serious.value)) {
                binding.includeAdd.rbSeriousDefect.setChecked(true);
                defectLevel = DefectEnum.serious.value;
            } else {
                binding.includeAdd.rbGeneralDefect.setChecked(true);
                defectLevel = DefectEnum.general.value;
            }

            if (TextUtils.equals(record.hasInfluenceDbz, "Y")) {
                infulenceEleNet = "Y";
                binding.rbInflunceYes.setChecked(true);
            } else if (TextUtils.equals(record.hasInfluenceDbz, "N")) {
                binding.rbInflunceNo.setChecked(true);
            } else {
                binding.rbInflunceNothing.setChecked(true);
            }
        }
    }

    RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.rb_influnce_yes) {
                infulenceEleNet = "Y";
            } else if (checkedId == R.id.rb_influnce_no) {
                infulenceEleNet = "N";
            } else {
                infulenceEleNet = "NOT KNOW";
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case ACTION_IMAGE:
                    mDefectImageList.add(currentImageName);
                    String pictureContent = selectDefect.devcie + "\n" + binding.includeAdd.etInputDefectContent.getText().toString() + "\n" + DateUtils.getFormatterTime(new Date(), "yyyy-MM-dd HH:mm");
                    DrawCircleImageActivity.with(mActivity).setTxtContent(pictureContent).setPath(picParentFolder + currentImageName).setRequestCode(0x0).start();
                    break;
                case CANCEL_RESULT_LOAD_IMAGE:
                    ArrayList<String> cancelList = data.getStringArrayListExtra(Config.CANCEL_IMAGE_URL_LIST_KEY);
                    for (String imageUrl : cancelList) {
                        mDefectImageList.remove(imageUrl.replace(picParentFolder, ""));
                    }
                    reFreshPicUI();
                    break;
                case 0x0:
                    reFreshPicUI();
                    break;
                default:
                    break;
            }
        }
    }


    public void reFreshPicUI() {
        Bitmap bitmap;
        if (!mDefectImageList.isEmpty()) {
            bitmap = BitmapUtils.getImageThumbnailByWidth(picParentFolder + mDefectImageList.get(0), 210);
            binding.includeAdd.ivNewDefectPhoto.setImageBitmap(bitmap);
            binding.includeAdd.tvDefectCount.setVisibility(mDefectImageList.size() <= 1 ? View.GONE : View.VISIBLE);
            binding.includeAdd.tvDefectCount.setText(String.valueOf(mDefectImageList.size()));
        } else {
            binding.includeAdd.ivNewDefectPhoto.setImageBitmap(null);
            binding.includeAdd.tvDefectCount.setText("");
            binding.includeAdd.tvDefectCount.setVisibility(View.GONE);
        }
    }

    public String getNewDefectId() {
        return defectId;
    }


    @Override
    public void onViewFocus(EditText v, CopyItem item, CopyResult result, List<EditText> editTexts, List<CopyItem> copyItems) {

    }

    @Override
    public void hideKeyBord() {

    }

    @Override
    public void onViewFocusChange(EditText v, CopyItem item, CopyResult result, boolean hasFocus, String descript, List<EditText> editTexts) {

    }
}
