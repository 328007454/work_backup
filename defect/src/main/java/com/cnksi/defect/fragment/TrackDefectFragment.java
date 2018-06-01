package com.cnksi.defect.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;

import com.cnksi.common.CommonApplication;
import com.cnksi.common.Config;
import com.cnksi.common.activity.DrawCircleImageActivity;
import com.cnksi.common.activity.ImageDetailsActivity;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.utils.FunctionUtil;
import com.cnksi.common.utils.PlaySound;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.defect.DeviceDefectAdapter;
import com.cnksi.defect.R;
import com.cnksi.defect.databinding.FragmentTrackDefectBinding;
import com.cnksi.defect.defect_enum.DefectEnum;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Date;

import static com.cnksi.common.Config.CANCEL_RESULT_LOAD_IMAGE;
import static com.cnksi.core.utils.Cst.ACTION_IMAGE;

/**
 * @author Mr.K  on 2018/5/30.
 * @decrption 跟踪缺陷
 */

public class TrackDefectFragment extends BaseDefectFragment {

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


    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_track_defect;
    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        getActivity().runOnUiThread(() -> {
            if (defectRecords != null) {
                if (deviceDefectAdapter == null) {
                    deviceDefectAdapter = new DeviceDefectAdapter(R.layout.layout_defect_item, defectRecords);
                    binding.includeDefect.lvContainer.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.includeDefect.lvContainer.setAdapter(deviceDefectAdapter);
                    deviceDefectAdapter.setCurrentModel(Config.TRACK_DEFECT_MODEL);
                    deviceDefectAdapter.setItemClickListener(this);
                } else {
                    deviceDefectAdapter.setList(defectRecords);
                }
            }
        });
    }

    @Override
    protected void initUI() {
        super.initUI();
        binding = (FragmentTrackDefectBinding) fragmentDataBinding;
        binding.includeAdd.tvSelectDevicePart.setVisibility(View.GONE);
        initOnClick();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isFirstLoad) {
            lazyLoad();
        }
    }

    private void initOnClick() {
        binding.includeAdd.rgDefect.setOnCheckedChangeListener(onCheckedChangeListener);

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
        PlaySound.getIntance(getActivity()).play(R.raw.track);
        selectDefect.has_track = "Y";
        try {
            //复制一条跟踪的缺陷保持除内容不同外其余相同,将原有的缺陷跟踪字段置为"Y"
            CommonApplication.getInstance().getDbManager().saveOrUpdate(selectDefect);
            DefectRecord newDefect = selectDefect;
            newDefect.defectid = DefectRecord.getDefectId();
            newDefect.defectlevel = defectLevel;
            newDefect.pics = StringUtils.arrayListToString(mDefectImageList);
            newDefect.description = binding.includeAdd.etInputDefectContent.getText().toString();
            newDefect.has_track = "N";
            newDefect.discovered_date = DateUtils.getCurrentShortTime();
            CommonApplication.getInstance().getDbManager().saveOrUpdate(newDefect);
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
    protected void setClickDefectData(DefectRecord record) {
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
    }

    RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.rb_general_defect) {
                defectLevel = DefectEnum.general.value;
            } else if (checkedId == R.id.rb_serious_defect) {
                defectLevel = DefectEnum.serious.value;
            } else {
                defectLevel = DefectEnum.critical.value;
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
                    ArrayList<String> cancelList = data.getStringArrayListExtra(Config.CANCEL_IMAGEURL_LIST);
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


}
