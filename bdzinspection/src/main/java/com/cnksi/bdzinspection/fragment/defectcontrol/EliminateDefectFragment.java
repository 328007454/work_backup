package com.cnksi.bdzinspection.fragment.defectcontrol;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.TextView;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.defectcontrol.DefectReasonDialogAdapter;
import com.cnksi.bdzinspection.adapter.defectcontrol.HistoryDefectAdapter;
import com.cnksi.bdzinspection.adapter.defectcontrol.HistoryDefectAdapter.OnAdapterViewClickListener;
import com.cnksi.bdzinspection.daoservice.LookupService;
import com.cnksi.bdzinspection.databinding.XsDialogDefectReasonBinding;
import com.cnksi.bdzinspection.databinding.XsFragmentEliminateDefectBinding;
import com.cnksi.bdzinspection.fragment.BaseFragment;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.bdzinspection.utils.FunctionUtil;
import com.cnksi.common.utils.PlaySound;
import com.cnksi.common.view.CustomRadioButton;
import com.cnksi.common.Config;
import com.cnksi.common.daoservice.DefectRecordService;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.model.Lookup;
import com.cnksi.common.utils.BitmapUtil;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.view.CustomerDialog;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.cnksi.common.Config.CANCEL_RESULT_LOAD_IMAGE;
import static com.cnksi.common.Config.LOAD_DATA;
import static com.cnksi.core.utils.Cst.ACTION_IMAGE;

/**
 * 消除缺陷
 *
 * @author terry
 */
public class EliminateDefectFragment extends BaseFragment implements OnAdapterViewClickListener {

    public static final int TAKE_WORK_TICKET = 0x100;
    public static final int TAKE_ELIMINATE_RECORD = 0x101;
    public static final int INIT_DEFECT_REASON = TAKE_ELIMINATE_RECORD + 1;

    public static final String ELIMINATE_MODEL = "eliminate_model";
    public static final String TICKETS_MODEL = "tickets_model";
    public static final String ELIMINATE_REOCRD_MODEL = "eliminate_record_model";

    private List<DefectRecord> dataList;

    private HistoryDefectAdapter mHistoryDefectAdapter = null;

    /**
     * 当前缺陷图片的内容
     */
    private String currentImageName = "";
    /**
     * 消缺 缺陷照片的集合
     */
    private ArrayList<String> mDefectImageList = new ArrayList<String>();
    /**
     * 消缺工作票照片的集合
     */
    private ArrayList<String> mWorkTicketImageList = null;
    /**
     * 消缺记录照片的集合
     */
    private ArrayList<String> mEliminateRecordImageList = null;
    /**
     * 是否是从电池组跳转过来的
     */
    private boolean isFromBattery = false;
    /**
     * 当前点击的图片类型 消缺/工作票/消缺记录
     */
    private String mCurrentClickPhotoModel = "";

    private DefectReasonDialogAdapter mDefectReasonAdapter = null;
    /**
     * 选择缺陷原因的dialog
     */
    private Dialog mDefectReasonDialog = null;
    /**
     * 选择的缺陷原因
     */
    private Lookup mCurrentSelectedDefectReason = null;
    private ArrayList<Lookup> mSelectedDefectReasonList = new ArrayList<Lookup>();

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

    /**
     * 当前要消除的缺陷
     */
    private DefectRecord mCurrentEliminateDefect = null;
    /**
     * 缺陷原因类型
     */
    private List<Lookup> mDefectReasonTypeList = null;

    private XsFragmentEliminateDefectBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = XsFragmentEliminateDefectBinding.inflate(getActivity().getLayoutInflater());
        AutoUtils.autoSize(binding.getRoot());
        initialUI();
        lazyLoad();
        initOnClick();
        return binding.getRoot();
    }


    private void initialUI() {

        getBundleValue();

        if (currentInspectionType.contains("switchover") || currentInspectionType.contains("maintenance")) {
            binding.include.txtCurrentRecord.setVisibility(View.GONE);
        }
        isFromBattery = bundle.getBoolean(Config.IS_FROM_BATTERY, false);
        binding.tvEliminateDate.setText(DateUtils.getCurrentShortTime());

        binding.include.lvContainer.setParentScrollView(binding.scvRootContainer);
        binding.include.lvContainer.setMaxHeight(Integer.MAX_VALUE);

        isPrepared = true;
    }

    private void initOnClick() {
        binding.ibtnTakeWorkTicket.setOnClickListener(view -> {
            if (mOnFunctionButtonClickListener != null) {
                mOnFunctionButtonClickListener.takePicture(currentImageName = FunctionUtil.getCurrentImageName(currentActivity), Config.RESULT_PICTURES_FOLDER, TAKE_WORK_TICKET);
            }
        });
        binding.ibtnTakeEliminateRecord.setOnClickListener(view -> {
            if (mOnFunctionButtonClickListener != null) {
                mOnFunctionButtonClickListener.takePicture(currentImageName = FunctionUtil.getCurrentImageName(currentActivity), Config.RESULT_PICTURES_FOLDER, TAKE_ELIMINATE_RECORD);
            }
        });

        binding.ibtnTakePicture.setOnClickListener(view -> {
            if (mOnFunctionButtonClickListener != null) {
                mOnFunctionButtonClickListener.takePicture(currentImageName = FunctionUtil.getCurrentImageName(currentActivity), Config.RESULT_PICTURES_FOLDER, ACTION_IMAGE);
            }
        });

        binding.ibtnSelectEliminateDate.setOnClickListener(view -> {
            CustomerDialog.showDatePickerDialog(currentActivity, (result, position) -> binding.tvEliminateDate.setText(result));
        });

        binding.ivEliminateDefectPhoto.setOnClickListener(view -> {
            mCurrentClickPhotoModel = ELIMINATE_MODEL;
            showImageDetails(mDefectImageList);
        });

        binding.ivWorkTicket.setOnClickListener(view -> {
            mCurrentClickPhotoModel = TICKETS_MODEL;
            showImageDetails(mWorkTicketImageList);
        });
        binding.ivEliminateRecord.setOnClickListener(view -> {
            mCurrentClickPhotoModel = ELIMINATE_REOCRD_MODEL;
            showImageDetails(mEliminateRecordImageList);
        });

        binding.btnConfirm.setOnClickListener(view -> {
            eliminateDefect();
        });

        binding.reasonContainer.setOnClickListener(view -> {
            selectDefectReason();
        });
        binding.tvSelectDefectReason.setOnClickListener(view -> selectDefectReason());

    }

    private void selectDefectReason() {
        for (int i = 0, count = binding.rgDefectReasonContainer.getChildCount(); i < count; i++) {
            View viewItem = binding.rgDefectReasonContainer.getChildAt(i);
            if (viewItem instanceof CompoundButton) {
                if (((CompoundButton) viewItem).isChecked()) {
                    List<Lookup> mDefectReasonList = LookupService.getInstance().getDefectReasonListByParentId(viewItem.getTag().toString());
                    if (mDefectReasonList != null) {
                        showDefectReasonDilalog(((CompoundButton) viewItem).getText().toString(), mDefectReasonList);
                    }
                    break;
                }
            }
        }
    }


    /**
     * 显示图片的详细信息
     */
    private void showImageDetails(ArrayList<String> imageList) {
        if (imageList != null && !imageList.isEmpty()) {
            ArrayList<String> mImageUrlList = new ArrayList<String>();
            for (String string : imageList) {
                mImageUrlList.add(Config.RESULT_PICTURES_FOLDER + string);
            }
            showImageDetails(this, mImageUrlList, true);
        }
    }

    /**
     * 消除缺陷
     */
    private void eliminateDefect() {
        if (mCurrentEliminateDefect != null && !TextUtils.isEmpty(currentSelectDefectRecordId)) {
            PlaySound.getIntance(currentActivity).play(R.raw.clear);
            binding.tvSelectDefectReason.setText("");
            binding.etChargePerson.setText("");
            ExecutorManager.executeTask(new Runnable() {

                @Override
                public void run() {
                    try {
                        mCurrentEliminateDefect.has_remove = "Y";
                        mCurrentEliminateDefect.removeDate = DateUtils.getCurrentLongTime();
                        DefectRecordService.getInstance().update(mCurrentEliminateDefect, DefectRecord.HAS_REMOVE, DefectRecord.REMOVE_DATE);
                        DefectRecord mTempDefectRecord = null;
                        for (DefectRecord mDefectRecord : dataList) {
                            if (mDefectRecord.defectid.equalsIgnoreCase(currentSelectDefectRecordId)) {
                                mTempDefectRecord = mDefectRecord;
                                break;
                            }
                        }
                        dataList.remove(mTempDefectRecord);
                        mHandler.sendEmptyMessage(LOAD_DATA);
                        // 消除了缺陷 要对其他两个界面的缺陷列表进行更新
                        if (mOnFunctionButtonClickListener != null) {
                            mOnFunctionButtonClickListener.onDefectChanged(getString(R.string.xs_eliminate_defect_success_str));
                        }
                        // 消缺后对当前消缺对象赋空
                        mCurrentEliminateDefect = null;
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            });

        } else {
            ToastUtils.showMessage( "请选择一个需要消除的缺陷!");
        }
    }

    @Override
    protected void onRefresh(Message msg) {
        if (currentActivity.isFinishing()) {
            return;
        }
        switch (msg.what) {
            case LOAD_DATA:

                if (mHistoryDefectAdapter == null) {
                    mHistoryDefectAdapter = new HistoryDefectAdapter(currentActivity, dataList);
                    mHistoryDefectAdapter.setCurrentFunctionModel(Config.ELIMINATE_DEFECT_MODEL);
                    mHistoryDefectAdapter.setCurrentClickedPosition(currentSelectDefectRecordId);
                    mHistoryDefectAdapter.setOnAdapterViewClickListener(this);
                    binding.include.lvContainer.setAdapter(mHistoryDefectAdapter);
                } else {
                    mHistoryDefectAdapter.setList(dataList);
                }

                // 回到当前时间
                binding.tvEliminateDate.setText(DateUtils.getCurrentShortTime());
                // 设置拍摄消缺照片
                setTakePictureDisplay(mDefectImageList, binding.tvDefectCount, binding.ivEliminateDefectPhoto, false, true);
                // 设置消除记录照片
                setTakePictureDisplay(mEliminateRecordImageList, binding.tvEliminateRecordCount, binding.ivEliminateRecord, false, true);
                // 设置工作票照片
                setTakePictureDisplay(mWorkTicketImageList, binding.tvWorkTicketCount, binding.ivWorkTicket, false, true);

                break;
            case INIT_DEFECT_REASON:
                if (mDefectReasonTypeList != null && !mDefectReasonTypeList.isEmpty()) {
                    for (int i = 0, count = mDefectReasonTypeList.size(); i < count; i++) {
                        binding.rgDefectReasonContainer.addView(getDefectReasonItemLayout(mDefectReasonTypeList.get(i), i));
                    }
                }
                binding.rgDefectReasonContainer.setOnCheckedChangeListener((group, checkedId) -> {
                    RadioButton mRadioButton = (RadioButton) group.findViewById(checkedId);
                    if (mRadioButton.isChecked()) {
                        binding.tvSelectDefectReason.setHint(getResources().getString(R.string.xs_please_select_defect_reason_format_str, mRadioButton.getText().toString()));
                    }
                });

                break;
            default:
                break;
        }
    }

    /**
     * 默认选中点击消除的那个缺陷
     *
     * @param mDefectRecord
     */
    public void setCurrentSelectedPosition(DefectRecord mDefectRecord) {
        if (mDefectRecord != null) {
            mCurrentEliminateDefect = mDefectRecord;
            currentSelectDefectRecordId = mDefectRecord.defectid;
            if (mHistoryDefectAdapter != null) {
                mHistoryDefectAdapter.setCurrentClickedPosition(currentSelectDefectRecordId);
            }
        }
    }

    @Override
    protected void lazyLoad() {
        if (isPrepared && isVisible && isFirstLoad) {
            // 填充各控件的数据
            searchData();
            searchDefectReason();
            isFirstLoad = false;
        }
        if (!isFirstLoad) {
            searchData();
        }
    }

    /**
     * 查询数据
     *
     * @param
     * @param
     */
    public void searchData() {
        ExecutorManager.executeTask(new Runnable() {

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
                    dataList = DefectRecordService.getInstance().queryDefectByBatteryId(PreferencesUtils.get(Config.CURRENT_BATTERY_ID, "1"), currentDeviceId, currentBdzId);
                }
                mHandler.sendEmptyMessage(LOAD_DATA);
            }
        });
    }

    /**
     * 查询缺陷原因
     */
    private void searchDefectReason() {
        ExecutorManager.executeTask(new Runnable() {

            @Override
            public void run() {
                mDefectReasonTypeList = LookupService.getInstance().getDefectReasonType();
                mHandler.sendEmptyMessage(INIT_DEFECT_REASON);
            }
        });
    }

    /**
     * 得到缺陷原因的布局
     *
     * @param mDefectReason
     * @param position
     * @return
     */
    private View getDefectReasonItemLayout(Lookup mDefectReason, int position) {
        CustomRadioButton mDefectReasonLayout = (CustomRadioButton) mInflater.inflate(R.layout.xs_defect_reason_layout, binding.scvRootContainer, false);
        Drawable drawableLeft = ContextCompat.getDrawable(getActivity(), R.drawable.xs_radio_button_blue_selector);
        mDefectReasonLayout.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, null, null, null);
        int textSize = AutoUtils.getPercentHeightSize((int) currentActivity.getResources().getDimension(R.dimen.xs_global_text_size_px));
        mDefectReasonLayout.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        mDefectReasonLayout.setText(mDefectReason.v);
        mDefectReasonLayout.setTag(mDefectReason.k);
        mDefectReasonLayout.setChecked((position == 0));
        mDefectReasonLayout.setClickable(true);
        mDefectReasonLayout.setId(R.id.xs_radio_button_id + position);
        mDefectReasonLayout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.tvSelectDefectReason.setText("");
                }
            }
        });
        return mDefectReasonLayout;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case ACTION_IMAGE:
                    if (mOnFunctionButtonClickListener != null) {
                        mDefectImageList.add(currentImageName);
                        String pictureContent = DateUtils.getFormatterTime(new Date(), "yyyy-MM-dd HH:mm");
                        mOnFunctionButtonClickListener.drawCircle(Config.RESULT_PICTURES_FOLDER + currentImageName, pictureContent);
                    }

                    break;
                case TAKE_WORK_TICKET: // 工作票
                    if (mWorkTicketImageList == null) {
                        mWorkTicketImageList = new ArrayList<String>();
                    }
                    BitmapUtil.compressImage(Config.RESULT_PICTURES_FOLDER + currentImageName, 3);
                    // 设置工作票照片
                    setTakePictureDisplay(mWorkTicketImageList, binding.tvWorkTicketCount, binding.ivWorkTicket, true);

                    break;

                case TAKE_ELIMINATE_RECORD: // 消缺记录
                    if (mEliminateRecordImageList == null) {
                        mEliminateRecordImageList = new ArrayList<String>();
                    }
                    BitmapUtil.compressImage(Config.RESULT_PICTURES_FOLDER + currentImageName, 3);
                    // 设置消除记录照片
                    setTakePictureDisplay(mEliminateRecordImageList, binding.tvEliminateRecordCount, binding.ivEliminateRecord, true);

                    break;
                case CANCEL_RESULT_LOAD_IMAGE:

                    removePicture(data);

                    break;
                case LOAD_DATA:
                    // 设置拍摄消缺照片
                    setTakePictureDisplay(mDefectImageList, binding.tvDefectCount, binding.ivEliminateDefectPhoto, false);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 设置显示照片及数量
     *
     * @param imageList
     * @param mTextCount
     * @param mPicture
     */
    private void setTakePictureDisplay(ArrayList<String> imageList, TextView mTextCount, ImageView mPicture, boolean isAddPicture) {
        setTakePictureDisplay(imageList, mTextCount, mPicture, isAddPicture, false);
    }

    /**
     * 设置显示照片及数量
     *
     * @param imageList
     * @param mTextCount
     * @param mPicture
     * @param isClearAllPic 是否清空图片
     */
    private void setTakePictureDisplay(ArrayList<String> imageList, TextView mTextCount, ImageView mPicture, boolean isAddPicture, boolean isClearAllPic) {
        if (isAddPicture) {
            imageList.add(currentImageName);
        }
        if (isClearAllPic && imageList != null) {
            imageList.clear();
        }
        if (imageList != null && imageList.size() > 1) {
            mTextCount.setVisibility(View.VISIBLE);
            mTextCount.setText(String.valueOf(imageList.size()));
        } else {
            mTextCount.setVisibility(View.GONE);
        }
        int newWidth = getResources().getDimensionPixelSize(R.dimen.xs_new_defect_photo_height);
        mPicture.setImageBitmap(BitmapUtil.getImageThumbnail((imageList == null || imageList.isEmpty()) ? "" : Config.RESULT_PICTURES_FOLDER + imageList.get(0), newWidth, newWidth));
    }

    /**
     * 移除照片
     *
     * @param data
     * @param
     */
    private void removePicture(Intent data) {
        ArrayList<String> cancelList = data.getStringArrayListExtra(Config.CANCEL_IMAGEURL_LIST);
        for (String imageUrl : cancelList) {
            if (ELIMINATE_MODEL.equalsIgnoreCase(mCurrentClickPhotoModel)) {
                mDefectImageList.remove(imageUrl.replace(Config.RESULT_PICTURES_FOLDER, ""));
            } else if (ELIMINATE_REOCRD_MODEL.equalsIgnoreCase(mCurrentClickPhotoModel)) {
                mEliminateRecordImageList.remove(imageUrl.replace(Config.RESULT_PICTURES_FOLDER, ""));
            } else if (TICKETS_MODEL.equalsIgnoreCase(mCurrentClickPhotoModel)) {
                mWorkTicketImageList.remove(imageUrl.replace(Config.RESULT_PICTURES_FOLDER, ""));
            }
        }
        // 更新照片的状态
        if (ELIMINATE_MODEL.equalsIgnoreCase(mCurrentClickPhotoModel)) {
            // 设置拍摄消缺照片
            setTakePictureDisplay(mDefectImageList, binding.tvDefectCount, binding.ivEliminateDefectPhoto, false);
        } else if (TICKETS_MODEL.equalsIgnoreCase(mCurrentClickPhotoModel)) {
            // 设置工作票照片
            setTakePictureDisplay(mWorkTicketImageList, binding.tvWorkTicketCount, binding.ivWorkTicket, false);
        } else if (ELIMINATE_REOCRD_MODEL.equalsIgnoreCase(mCurrentClickPhotoModel)) {
            // 设置消除记录照片
            setTakePictureDisplay(mEliminateRecordImageList, binding.tvEliminateRecordCount, binding.ivEliminateRecord, false);
        }
    }

    @Override
    public void OnAdapterViewClick(View view, DefectRecord mDefect) {
        int i1 = view.getId();
        if (i1 == R.id.iv_defect_image) {
            if (mDefect != null) {
                if (!TextUtils.isEmpty(mDefect.pics)) {
                    ArrayList<String> defectImageList = new ArrayList<String>();
                    String[] defectImageArray = mDefect.pics.split(Config.COMMA_SEPARATOR);
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


    /**
     * 显示缺陷原因的dialog
     */
    private XsDialogDefectReasonBinding reasonBinding;
    private void showDefectReasonDilalog(String dialogTitle, List<Lookup> defectReasonList) {
        if (mDefectReasonDialog == null) {
            int dialogWidth = ScreenUtils.getScreenWidth(currentActivity) * 9 / 10;
            if (mDefectReasonAdapter == null) {
                mDefectReasonAdapter = new DefectReasonDialogAdapter(currentActivity, defectReasonList);
            } else {
                mDefectReasonAdapter.setList(defectReasonList);
            }
            reasonBinding = XsDialogDefectReasonBinding.inflate(getActivity().getLayoutInflater());
            mDefectReasonDialog = DialogUtils.createDialog(currentActivity,reasonBinding.getRoot(),dialogWidth, LayoutParams.WRAP_CONTENT);
            reasonBinding.lvContainer.setAdapter(mDefectReasonAdapter);
        }
        mDefectReasonAdapter.setList(defectReasonList);
        reasonBinding.ibtnCancel.setVisibility(View.INVISIBLE);
        reasonBinding.tvDialogTitle.setText(dialogTitle);
        mSelectedDefectReasonList.clear();
        mDefectReasonDialog.show();

        reasonBinding.ibtnCancel.setOnClickListener(view -> {
            if (mSelectedDefectReasonList.size() > 0) {
                mCurrentSelectedDefectReason = mSelectedDefectReasonList.remove(mSelectedDefectReasonList.size() - 1);
                List<Lookup> mDefectReasonList = LookupService.getInstance().getDefectReasonListByParentId(mCurrentSelectedDefectReason.loo_id);
                mDefectReasonAdapter.setList(mDefectReasonList);
                reasonBinding.tvDialogTitle.setText(getTitle(mSelectedDefectReasonList, true));
                if (mSelectedDefectReasonList.isEmpty()) {
                    reasonBinding.ibtnCancel.setVisibility(View.INVISIBLE);
                }
            }
        });
        reasonBinding.lvContainer.setOnItemClickListener((parent,view,position,id) -> {
            mCurrentSelectedDefectReason = (Lookup) parent.getItemAtPosition(position);
            mSelectedDefectReasonList.add(mCurrentSelectedDefectReason);
            List<Lookup> mDefectReasonList = LookupService.getInstance().getDefectReasonListByParentId(mCurrentSelectedDefectReason.id);
            if (mDefectReasonList != null && !mDefectReasonList.isEmpty()) {
                reasonBinding.tvDialogTitle.setText(getTitle(mSelectedDefectReasonList, true));
                mDefectReasonAdapter.setList(mDefectReasonList);
                reasonBinding.ibtnCancel.setVisibility(View.VISIBLE);
            } else {
                mDefectReasonDialog.dismiss();
                binding.tvSelectDefectReason.setText(getTitle(mSelectedDefectReasonList, false));
                mSelectedDefectReasonList.clear();
            }
        });
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
}
