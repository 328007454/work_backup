package com.cnksi.defect.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cnksi.common.Config;
import com.cnksi.common.activity.DrawCircleImageActivity;
import com.cnksi.common.activity.ImageDetailsActivity;
import com.cnksi.common.daoservice.DefectRecordService;
import com.cnksi.common.daoservice.LookupService;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.model.Lookup;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.common.utils.FunctionUtil;
import com.cnksi.common.utils.PlaySound;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.view.CustomerDialog;
import com.cnksi.defect.R;
import com.cnksi.defect.adapter.DefectReasonAdapter;
import com.cnksi.defect.adapter.DeviceDefectAdapter;
import com.cnksi.defect.databinding.FragmentEliminateDefectBinding;
import com.cnksi.defect.databinding.LayoutDefectReasonItemBinding;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cnksi.common.Config.CANCEL_RESULT_LOAD_IMAGE;
import static com.cnksi.core.utils.Cst.ACTION_IMAGE;

/**
 * 消除缺陷
 *
 * @author Mr.K  on 2018/5/30.
 */

public class EliminateDefectFragment extends BaseDefectFragment {
    public static final String ELIMINATE_MODEL = "eliminate_model";
    public static final String TICKETS_MODEL = "tickets_model";
    public static final String ELIMINATE_REOCRD_MODEL = "eliminate_record_model";
    private FragmentEliminateDefectBinding binding;
    private DeviceDefectAdapter deviceDefectAdapter;
    private String mCurrentClickPhotoModel;

    /**
     * 当前缺陷图片的名称
     */
    protected String currentImageName = "";

    /**
     * 缺陷照片的集合
     */
    private ArrayList<String> mDefectImageList = new ArrayList<>();

    /**
     * 缺陷照片的集合
     */
    private ArrayList<String> mTicketImageList = new ArrayList<>();

    /**
     * 缺陷照片的集合
     */
    private ArrayList<String> mDefectRecordImageList = new ArrayList<>();

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_eliminate_defect;
    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        List<Lookup> mDefectReasonList = null;
        if (binding.rbInflunceNo.isChecked()) {
            mDefectReasonList = LookupService.getInstance().getDefectReasonListByParentId(String.valueOf(102));
        } else {
            mDefectReasonList = LookupService.getInstance().getDefectReasonListByParentId(String.valueOf(101));
        }
        reasonList.clear();
        reasonList.addAll(mDefectReasonList);
        getActivity().runOnUiThread(() -> {
            if (defectRecords != null) {
                if (deviceDefectAdapter == null) {
                    deviceDefectAdapter = new DeviceDefectAdapter(R.layout.adapter_defect_item, defectRecords);
                    binding.includeDefect.lvContainer.setLayoutManager(new LinearLayoutManager(getContext()));
                    binding.includeDefect.lvContainer.setAdapter(deviceDefectAdapter);
                    deviceDefectAdapter.setCurrentModel(Config.ELIMINATE_DEFECT_MODEL);
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
        binding = (FragmentEliminateDefectBinding) fragmentDataBinding;
        initOnClick();
    }

    private void initOnClick() {
        binding.containerRgDefectReason.setOnCheckedChangeListener(onCheckedChangeListener);

        binding.ibTakePicture.setOnClickListener(v -> {
            if (isSelectDefect()) {
                mCurrentClickPhotoModel = ELIMINATE_MODEL;
                currentImageName = FunctionUtil.getCurrentImageName(getActivity());
                FunctionUtil.takePicture(getActivity(), currentImageName, picParentFolder, ACTION_IMAGE);
            }
        });
        binding.ivDefectPic.setOnClickListener(v -> {
            if (!mDefectImageList.isEmpty()) {
                new ImageDetailsActivity.Builder(getActivity()).setImageUrlList(StringUtils.addStrToListItem(mDefectImageList, picParentFolder)).setShowDelete(true).setDeleteFile(true).start();
            }
        });


        binding.ibSelectTime.setOnClickListener(v -> {
            CustomerDialog.showDatePickerDialog(mActivity, (result, position) -> binding.txtDate.setText(result));
        });

        binding.ibTakeTicketPicture.setOnClickListener(v -> {
            if (isSelectDefect()) {
                mCurrentClickPhotoModel = TICKETS_MODEL;
                currentImageName = FunctionUtil.getCurrentImageName(getActivity());
                FunctionUtil.takePicture(getActivity(), currentImageName, picParentFolder, ACTION_IMAGE);
            }
        });

        binding.ivDefectTicketPic.setOnClickListener(v -> {
            if (!mTicketImageList.isEmpty()) {
                new ImageDetailsActivity.Builder(getActivity()).setImageUrlList(StringUtils.addStrToListItem(mTicketImageList, picParentFolder)).setShowDelete(true).setDeleteFile(true).start();

            }
        });

        binding.ibTakeTicketElimatePicture.setOnClickListener(v -> {
            if (isSelectDefect()) {
                mCurrentClickPhotoModel = ELIMINATE_REOCRD_MODEL;
                currentImageName = FunctionUtil.getCurrentImageName(getActivity());
                FunctionUtil.takePicture(getActivity(), currentImageName, picParentFolder, ACTION_IMAGE);
            }
        });

        binding.ivDefectTicketElimatePic.setOnClickListener(v -> {
            if (!mDefectRecordImageList.isEmpty()) {
                new ImageDetailsActivity.Builder(getActivity()).setImageUrlList(StringUtils.addStrToListItem(mDefectRecordImageList, picParentFolder)).setShowDelete(true).setDeleteFile(true).start();
            }
        });

        binding.btnConfirm.setOnClickListener(v -> {
            if (isSelectDefect()) {
                binding.tvSelectDefectReason.setText("");
                binding.etChargePerson.setText("");
                saveData();
            }
        });

        binding.tvSelectDefectReason.setOnClickListener(v -> {
            showDefectReasonDialog();
        });
    }

    private void saveData() {
        PlaySound.getIntance(getActivity()).play(R.raw.clear);
        selectDefect.has_remove = "Y";
        String removeTime = binding.txtDate.getText().toString();
        selectDefect.removeDate = TextUtils.isEmpty(removeTime) ? DateUtils.getCurrentLongTime() : removeTime;
        try {
            DefectRecordService.getInstance().saveOrUpdate(selectDefect);
            defectRecords.remove(selectDefect);
            deviceDefectAdapter.notifyDataSetChanged();
            selectDefect = null;
            binding.ivDefectTicketElimatePic.setImageBitmap(null);
            binding.txtDate.setText("");
            binding.txtDate.setHint("请选择消除缺陷日期");
            binding.ivDefectTicketPic.setImageBitmap(null);
            binding.ivDefectPic.setImageBitmap(null);
        } catch (DbException e) {
            e.printStackTrace();
            ToastUtils.showMessage("保存异常");
        }

    }

    List<Lookup> reasonList = new ArrayList<>();
    private String txtReasonTitle;
    RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            reasonList.clear();
            if (checkedId == R.id.rb_influnce_yes) {
                txtReasonTitle = "缺陷技术原因";
                binding.tvSelectDefectReason.setHint("请选择技术原因");
                List<Lookup> mDefectReasonList = LookupService.getInstance().getDefectReasonListByParentId(String.valueOf(101));
                reasonList.addAll(mDefectReasonList);
            } else if (checkedId == R.id.rb_influnce_no) {
                txtReasonTitle = "缺陷责任原因";
                binding.tvSelectDefectReason.setHint("请选择责任原因");
                List<Lookup> mDefectReasonList = LookupService.getInstance().getDefectReasonListByParentId(String.valueOf(102));
                reasonList.addAll(mDefectReasonList);
            }

        }
    };

    private DefectReasonAdapter reasonAdapter;
    private LayoutDefectReasonItemBinding reasonItemBinding;
    private Dialog mDefectReasonDialog;
    private ArrayList<Lookup> mSelectedDefectReasonList = new ArrayList<>();
    /**
     * 选择的缺陷原因
     */
    private Lookup mCurrentSelectedDefectReason = null;

    private void showDefectReasonDialog() {
        if (reasonAdapter == null) {
            reasonAdapter = new DefectReasonAdapter(getContext(), reasonList, R.layout.textview_item);
        } else {
            reasonAdapter.setList(reasonList);
        }
        reasonItemBinding = LayoutDefectReasonItemBinding.inflate(getLayoutInflater());
        mDefectReasonDialog = DialogUtils.createDialog(getActivity(), reasonItemBinding.getRoot(), ScreenUtils.getScreenWidth(getContext()) * 7 / 9, LinearLayout.LayoutParams.WRAP_CONTENT);
        reasonItemBinding.lvContainer.setAdapter(reasonAdapter);
        reasonItemBinding.ibtnCancel.setVisibility(View.INVISIBLE);
        reasonItemBinding.tvDialogTitle.setText(txtReasonTitle);
        mDefectReasonDialog.show();
        mSelectedDefectReasonList.clear();

        reasonItemBinding.ibtnCancel.setOnClickListener(v -> {
            if (mSelectedDefectReasonList.size() > 0) {
                mCurrentSelectedDefectReason = mSelectedDefectReasonList.remove(mSelectedDefectReasonList.size() - 1);
                List<Lookup> mDefectReasonList = LookupService.getInstance().getDefectReasonListByParentId(mCurrentSelectedDefectReason.loo_id);
                reasonAdapter.setList(mDefectReasonList);
                reasonItemBinding.tvDialogTitle.setText(getTitle(mSelectedDefectReasonList, true));
                if (mSelectedDefectReasonList.isEmpty()) {
                    reasonItemBinding.ibtnCancel.setVisibility(View.INVISIBLE);
                }
            }
        });

        reasonItemBinding.lvContainer.setOnItemClickListener((parent, view, position, id) -> {
            mCurrentSelectedDefectReason = (Lookup) parent.getItemAtPosition(position);
            mSelectedDefectReasonList.add(mCurrentSelectedDefectReason);
            List<Lookup> mDefectReasonList = LookupService.getInstance().getDefectReasonListByParentId(mCurrentSelectedDefectReason.id);
            if (mDefectReasonList != null && !mDefectReasonList.isEmpty()) {
                reasonItemBinding.tvDialogTitle.setText(EliminateDefectFragment.this.getTitle(mSelectedDefectReasonList, true));
                reasonAdapter.setList(mDefectReasonList);
                reasonItemBinding.ibtnCancel.setVisibility(View.VISIBLE);
            } else {
                mDefectReasonDialog.dismiss();
                binding.tvSelectDefectReason.setText(getTitle(mSelectedDefectReasonList, false));
                mSelectedDefectReasonList.clear();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case ACTION_IMAGE:
                    if (TextUtils.equals(mCurrentClickPhotoModel, ELIMINATE_MODEL)) {
                        mDefectImageList.add(currentImageName);
                        String pictureContent = selectDefect.devcie + "\n" + DateUtils.getFormatterTime(new Date(), "yyyy-MM-dd HH:mm");
                        DrawCircleImageActivity.with(mActivity).setTxtContent(pictureContent).setPath(picParentFolder + currentImageName).setRequestCode(0x0).start();
                    } else if (TextUtils.equals(mCurrentClickPhotoModel, TICKETS_MODEL)) {
                        mTicketImageList.add(currentImageName);
                        Bitmap bitmap = BitmapUtils.getImageThumbnailByWidth(picParentFolder + currentImageName, 210);
                        if (bitmap != null) {
                            binding.ivDefectTicketPic.setImageBitmap(bitmap);
                        }
                    } else {
                        Bitmap bitmap = BitmapUtils.getImageThumbnailByWidth(picParentFolder + currentImageName, 210);
                        if (bitmap != null) {
                            binding.ivDefectTicketElimatePic.setImageBitmap(bitmap);
                        }
                        mDefectRecordImageList.add(currentImageName);
                    }
                    break;
                case CANCEL_RESULT_LOAD_IMAGE:
                    ArrayList<String> cancelList = data.getStringArrayListExtra(Config.CANCEL_IMAGE_URL_LIST_KEY);
                    if (TextUtils.equals(mCurrentClickPhotoModel, ELIMINATE_MODEL)) {
                        for (String imageUrl : cancelList) {
                            mDefectImageList.remove(imageUrl.replace(picParentFolder, ""));
                        }
                        reFreshPicUI(mDefectImageList, binding.ivDefectPic, binding.tvDefectCount);
                    } else if (TextUtils.equals(mCurrentClickPhotoModel, TICKETS_MODEL)) {
                        for (String imageUrl : cancelList) {
                            mTicketImageList.remove(imageUrl.replace(picParentFolder, ""));
                        }
                        reFreshPicUI(mTicketImageList, binding.ivDefectTicketPic, binding.tvDefectTicketCount);
                    } else {
                        for (String imageUrl : cancelList) {
                            mDefectRecordImageList.remove(imageUrl.replace(picParentFolder, ""));
                        }
                        reFreshPicUI(mDefectRecordImageList, binding.ivDefectTicketElimatePic, binding.tvDefectTicketElimateCount);
                    }
                    break;
                case 0x0:
                    reFreshPicUI(mDefectImageList, binding.ivDefectPic, binding.tvDefectCount);
                    break;
                default:
                    break;
            }
        }

    }

    public void reFreshPicUI(ArrayList<String> pics, ImageView imageView, TextView txtCount) {
        Bitmap bitmap;
        if (!pics.isEmpty()) {
            bitmap = BitmapUtils.getImageThumbnailByWidth(picParentFolder + pics.get(0), 210);
            imageView.setImageBitmap(bitmap);
            txtCount.setVisibility(pics.size() <= 1 ? View.GONE : View.VISIBLE);
            txtCount.setText(String.valueOf(pics.size()));
        } else {
            imageView.setImageBitmap(null);
            txtCount.setText("");
            txtCount.setVisibility(View.GONE);
        }
    }

    private boolean isSelectDefect() {
        if (selectDefect == null) {
            ToastUtils.showMessage("请点击需要验收的缺陷");
            return false;
        }
        return true;
    }

    /**
     * 得到title
     *
     * @param mSelectedDefectReasonList
     * @return
     */
    private String getTitle(List<Lookup> mSelectedDefectReasonList, boolean isTitle) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, count = mSelectedDefectReasonList.size(); i < count; i++) {
            if (isTitle) {
                sb.append(" -> ");
            }
            sb.append(mSelectedDefectReasonList.get(i).v);
            if (!isTitle && i < count - 1) {
                sb.append(" -> ");
            }
        }
        if (isTitle) {
            sb.insert(0, getString(R.string.xs_defect_reason_str));
        }
        return sb.toString();
    }

    public void setTrackDefectNewId(String defectNewId) {
        defectId = defectNewId;
        if (!isFirstLoad) {
            lazyLoad();
        }
    }

    @Override
    protected void setClickDefectData(View v, DefectRecord record) {
        if (v.getId() == R.id.iv_defect_image) {
            final ArrayList<String> listPicDis = StringUtils.stringToList(record.pics);
            ImageDetailsActivity.with(getActivity()).setPosition(0).setImageUrlList(StringUtils.addStrToListItem(listPicDis, Config.RESULT_PICTURES_FOLDER))
                    .setDeleteFile(false).setShowDelete(false).start();
        }
    }
}
