package com.cnksi.defect.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.common.CommonApplication;
import com.cnksi.common.Config;
import com.cnksi.common.activity.DeviceSelectActivity;
import com.cnksi.common.activity.DrawCircleImageActivity;
import com.cnksi.common.activity.ImageDetailsActivity;
import com.cnksi.common.base.BaseTitleActivity;
import com.cnksi.common.daoservice.BdzService;
import com.cnksi.common.daoservice.DefectRecordService;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.model.Bdz;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.common.model.Device;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.common.utils.FunctionUtil;
import com.cnksi.common.utils.KeyBoardUtils;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.defect.R;
import com.cnksi.defect.adapter.DefectDefaultAdapter;
import com.cnksi.defect.adapter.DefectOriginAdapter;
import com.cnksi.defect.bean.DefectType;
import com.cnksi.defect.bean.DefectTypeChild;
import com.cnksi.defect.databinding.ActivityAddDefectBinding;
import com.cnksi.defect.databinding.LayoutDefectDealBinding;
import com.cnksi.defect.databinding.LayoutDefectOriginBinding;
import com.cnksi.defect.defect_enum.DefectEnum;
import com.cnksi.defect.view.PopWindowCustom;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.cnksi.common.Config.CANCEL_RESULT_LOAD_IMAGE;
import static com.cnksi.core.utils.Cst.ACTION_IMAGE;

/**
 * 新增缺陷界面
 *
 * @author Mr.K  on 2018/5/29.
 */

public class AddDefectActivity extends BaseTitleActivity {
    public static final String BDZ = "bzd";
    public static final String DEVICE = "device";
    public static final String SPACE = "space";

    private ActivityAddDefectBinding binding;
    private List<Bdz> bdzList = new ArrayList<>();
    /**
     * 缺陷等级
     */
    private String defectLevel;
    private Bdz bdz;
    private String clickMode;
    private String influnceBdz;
    private String spaceId;
    private String deviceId;
    private String devicePartName;
    private String devicePartId;
    private String defectContent;
    private String standardId;
    private List<DbModel> defaultDefectModels;
    /**
     * 设备小类id，主要用来查询设备默认的缺陷
     */
    private String deviceDtId;
    /**
     * 当前缺陷图片的名称
     */
    protected String currentImageName = "";
    /**
     * 缺陷的顶级目录
     */
    protected String picParentFolder = Config.RESULT_PICTURES_FOLDER;

    /**
     * 缺陷照片的集合
     */
    private ArrayList<String> mDefectImageList = new ArrayList<>();

    @Override
    public void initUI() {
        setTitleText("新增缺陷");
    }

    @Override
    protected View getChildContentView() {
        binding = ActivityAddDefectBinding.inflate(getLayoutInflater());
        binding.containerRgEleInternet.setOnCheckedChangeListener(onCheckedChangeListener);
        getPassData();
        return binding.getRoot();
    }

    @Override
    public void initData() {
        bdzList = BdzService.getInstance().findAllBdzByDp(PreferencesUtils.get(Config.CURRENT_DEPARTMENT_ID, ""));
        getAllDefectStandardId();
    }

    private void getAllDefectStandardId() {
        ExecutorManager.executeTaskSerially(() -> {
            defaultDefectModels = DefectRecordService.getInstance().queryDefectStidByDtid(deviceDtId, standardId);
        });
    }

    private boolean hasAllChoice;
    private boolean noDevicePart;
    private boolean hasDeviceChoice;
    private boolean hasReportId;
    private String remark;

    private void getPassData() {
        getIntentValue();
        Intent intent = getIntent();
        hasAllChoice = intent.getBooleanExtra(Config.HAS_ALL_CHOICE, false);
        noDevicePart = intent.getBooleanExtra(Config.NO_DEVICE_PART, true);
        hasDeviceChoice = intent.getBooleanExtra(Config.DEVICE_CHOICE, false);
        hasReportId = intent.getBooleanExtra(Config.HAS_REPORT_ID, true);
        deviceDtId = intent.getStringExtra(Device.DTID);
        if (!hasAllChoice && hasDeviceChoice) {
            getValue(intent);
            binding.txtBdzName.setCompoundDrawables(null, null, null, null);
            binding.txtBdzName.setText(currentBdzName);
        } else if (!hasAllChoice) {
            getValue(intent);
            binding.txtBdzName.setText(currentBdzName);
            binding.txtSpaceName.setText(spaceName);
            binding.etInputDevice.setText(deviceName);
            binding.txtBdzName.setCompoundDrawables(null, null, null, null);
            binding.txtSpaceName.setCompoundDrawables(null, null, null, null);
            binding.ivSelectDevice.setVisibility(View.GONE);
            binding.etInputDefectContent.setText(TextUtils.isEmpty(defectContent) ? "" : defectContent);
        }
        if (!noDevicePart) {
            binding.txtDevicePartName.setVisibility(View.VISIBLE);
            binding.txtDevicePartName.setText(devicePartName);
        }

        initOnClick();
    }

    public void getValue(Intent intent) {
        deviceName = intent.getStringExtra(Config.CURRENT_DEVICE_NAME);
        deviceId = intent.getStringExtra(Config.CURRENT_DEVICE_ID);
        spaceId = intent.getStringExtra(Config.CURRENT_SPACING_ID);
        spaceName = intent.getStringExtra(Config.CURRENT_SPACING_NAME);
        devicePartName = intent.getStringExtra(Config.CURRENT_DEVICE_PART_NAME);
        devicePartId = intent.getStringExtra(Config.CURRENT_DEVICE_PART_ID);
        defectContent = intent.getStringExtra(Config.DEFECT_CONTENT);
        if (!TextUtils.isEmpty(defectContent)) {
            remark = defectContent;
        }
        if (noDevicePart) {
            standardId = intent.getStringExtra(Config.CURRENT_STANDARD_ID);
        }
    }

    private void initOnClick() {
        binding.etInputDefectContent.requestFocus();
        binding.etInputDefectContent.setOnClickListener(v -> {
            if (!noDevicePart) {
                KeyBoardUtils.closeKeybord(this);
                createDefectDialog();
            } else {
                binding.etInputDefectContent.addTextChangedListener(textWatcher);
            }
        });

        binding.btnCancel.setOnClickListener(v -> {
            onBackPressed();
        });
        binding.btnSure.setOnClickListener(v -> {
            setResult(RESULT_OK);
            saveData();
        });
        if (hasAllChoice) {
            binding.txtBdzName.setOnClickListener(v -> {
                clickMode = BDZ;
                showBdzWindow();
                binding.etInputDevice.setText("");
                binding.txtSpaceName.setText("");
            });
        }
        if (hasAllChoice || hasDeviceChoice) {
            binding.txtSpaceName.setOnClickListener(v -> {
                String bdzName = binding.txtBdzName.getText().toString();
                if (TextUtils.isEmpty(bdzName)) {
                    ToastUtils.showMessage("请先选择变电站");
                    return;
                }
                binding.etInputDevice.setText("");
                clickMode = SPACE;
                jumpDeviceSelectActivity();
            });
        }

        if (hasAllChoice || hasDeviceChoice) {
            binding.ivSelectDevice.setOnClickListener(v -> {
                String bdzName = binding.txtBdzName.getText().toString();
                if (TextUtils.isEmpty(bdzName)) {
                    ToastUtils.showMessage("请先选择变电站");
                    return;
                }
                clickMode = DEVICE;
                jumpDeviceSelectActivity();
            });
        }

        binding.ivDefectPic.setOnClickListener(v -> {
            if (mDefectImageList.isEmpty()) {
                ToastUtils.showMessage("无缺陷照片");
                return;
            }
            new ImageDetailsActivity.Builder(this).setImageUrlList(StringUtils.addStrToListItem(mDefectImageList, picParentFolder)).setSelect(true).setShowDelete(true).setDeleteFile(true).start();

        });
        binding.ibTakePicture.setOnClickListener(v -> {
            if (null == currentBdzName) {
                ToastUtils.showMessage("请先选择变电站");
                return;
            }
            if (TextUtils.isEmpty(spaceName)) {
                ToastUtils.showMessage("请选择间隔");
                return;
            }
            if (TextUtils.isEmpty(binding.etInputDevice.getText().toString())) {
                ToastUtils.showMessage("请选择或者输入设备名称");
                return;
            }
            if (TextUtils.isEmpty(binding.etInputDefectContent.getText().toString())) {
                ToastUtils.showMessage("请先输入缺陷");
            } else {
                currentImageName = FunctionUtil.getCurrentImageName(this);
                FunctionUtil.takePicture(this, currentImageName, picParentFolder, ACTION_IMAGE);
            }
        });
    }

    private List<MultiItemEntity> originModels = new ArrayList<>();
    private Dialog defectDialog;
    private LayoutDefectOriginBinding originBinding;
    private HashMap<String, DefectType> map = new HashMap<>();

    private void createDefectDialog() {
        originModels.clear();
        if (defaultDefectModels!=null) {
            for (DbModel model : defaultDefectModels) {
                if (!map.keySet().isEmpty() && map.containsKey(model.getString("level"))) {
                    DefectType defectType = map.get(model.getString("level"));
                    defectType.addSubItem(new DefectTypeChild(model));
                } else {
                    DefectType defectType = new DefectType(model);
                    defectType.addSubItem(new DefectTypeChild(model));
                    originModels.add(defectType);
                    map.put(model.getString("level"), defectType);
                }
            }
        }
        if (defectDialog == null) {
            originBinding = LayoutDefectOriginBinding.inflate(getLayoutInflater());
            defectDialog = DialogUtils.createDialog(this, originBinding.getRoot(), ScreenUtils.getScreenWidth(this) * 9 / 10, LinearLayout.LayoutParams.WRAP_CONTENT);
            DefectOriginAdapter originAdapter = new DefectOriginAdapter(originModels);
            originBinding.rcDefectOrigin.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            originBinding.rcDefectOrigin.setAdapter(originAdapter);
            originAdapter.setItemOnClick((adapter, view, position) -> {
                String tag = (String) view.getTag();
                DefectTypeChild child = (DefectTypeChild) originModels.get(position);
                DbModel model = child.model;
                if (!TextUtils.isEmpty(tag) && TextUtils.equals(tag, "txt")) {
                    binding.etInputDefectContent.setText(model.getString("description"));
                    setCheckType(model);
                    defectDialog.dismiss();
                } else {
                    creatDefectOriginDialog(model);
                }

            });
            originAdapter.expandAll();
            originBinding.btnSure.setOnClickListener(v -> {
                defectDialog.dismiss();
                binding.etInputDefectContent.setText("");
                KeyBoardUtils.openKeybord(binding.etInputDefectContent, this);
            });
        }
        defectDialog.show();
    }

    private Dialog dialogTips;

    private void creatDefectOriginDialog(DbModel model) {
        if (dialogTips == null) {
            LayoutDefectDealBinding dealBinding = LayoutDefectDealBinding.inflate(getLayoutInflater());
            dialogTips = DialogUtils.createDialog(this, dealBinding.getRoot(), ScreenUtils.getScreenWidth(this) * 9 / 10, LinearLayout.LayoutParams.WRAP_CONTENT);
            String contentOrigin = model.getString("origin");
            String dealContent = model.getString("dealMethod");
            dealBinding.tvDialogTitle.setText(R.string.xs_defect_source_str);
            dealBinding.tvSourceName.setText(contentOrigin);
            dealBinding.tvDealMethod.setText(dealContent);
        }
        dialogTips.show();


    }


    private void saveData() {
        String defectContent = binding.etInputDefectContent.getText().toString();
        String pics = StringUtils.arrayListToString(mDefectImageList);
        deviceName = binding.etInputDevice.getText().toString();
        if (TextUtils.isEmpty(defectContent)) {
            ToastUtils.showMessage("请输入缺陷内容");
            return;

        }
        int checkedId = binding.containerRbGeneralDefect.getmCheckedId();
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
        if (TextUtils.isEmpty(spaceName)) {
            ToastUtils.showMessage("请选择间隔");
            return;

        }
        if (TextUtils.isEmpty(deviceName)) {
            ToastUtils.showMessage("请选择或者输入设备名称");
            return;
        }
        DefectRecord defectRecord = new DefectRecord(hasAllChoice && !hasReportId ? null : currentReportId, currentBdzId, spaceId, spaceName, deviceId, deviceName, defectLevel, pics, defectContent, influnceBdz, userName, currentDepartmentName);
        if (!noDevicePart) {
            defectRecord.duid = devicePartId;
            defectRecord.duname = devicePartName;
            defectRecord.standid = standardId;
        }
        if (currentInspectionType.contains(InspectionType.maintenance.name()) || currentInspectionType.contains(InspectionType.switchover.name())) {
            defectRecord.standSwitchId = standardId;
        }
        defectRecord.remark = remark;
        try {
            CommonApplication.getInstance().getDbManager().saveOrUpdate(defectRecord);
            ToastUtils.showMessage("保存成功");
            onBackPressed();
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    private void jumpDeviceSelectActivity() {
        if (TextUtils.isEmpty(currentBdzName)) {
            ToastUtils.showMessage("请先选择变电站");
            return;
        }
        Intent intent = new Intent(this, DeviceSelectActivity.class);
        intent.putExtra(Config.CURRENT_BDZ_ID, currentBdzId);
        intent.putExtra(Config.TITLE_NAME_KEY, "请选择间隔");
        intent.putExtra(DeviceSelectActivity.IS_ALLOW_SELECT_SPACE_KEY, true);
        startActivityForResult(intent, Config.START_ACTIVITY_FORRESULT);
    }

    private void showBdzWindow() {
        new PopWindowCustom.PopWindowBuilder<Bdz>(this).setPopWindowBuilder(bdz -> bdz.name)
                .setWidth(binding.txtBdzName.getWidth())
                .setList(bdzList)
                .setOutSideCancelable(true).
                setItemClickListener((adapter, view1, position) -> {
                    ToastUtils.showMessage(bdzList.get(position).name);
                    bdz = bdzList.get(position);
                    currentBdzName = bdz.name;
                    currentBdzId = bdz.bdzid;
                    binding.txtBdzName.setText(currentBdzName);
                }).setDropDownOfView(binding.txtBdzName).setBackgroundAlpha(0.6f).showAsDropDown(0, 10);

    }

    private String spaceName;
    private String deviceName;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Config.START_ACTIVITY_FORRESULT) {
            if (data == null) {
                return;
            }
            DbModel model = (DbModel) data.getSerializableExtra(DeviceSelectActivity.RESULT_SELECT_KEY);
            if (TextUtils.equals(clickMode, SPACE)) {
                spaceName = model.getString("spacingName");
                spaceId = model.getString("spid");
                binding.txtSpaceName.setText(TextUtils.isEmpty(spaceName) ? "" : spaceName);
            } else if (TextUtils.equals(clickMode, DEVICE)) {
                spaceName = model.getString("spacingName");
                deviceName = model.getString("deviceName");
                binding.txtSpaceName.setText(TextUtils.isEmpty(spaceName) ? "" : spaceName);
                binding.etInputDevice.setText(TextUtils.isEmpty(deviceName) ? "" : deviceName);
                spaceId = model.getString("spid");
                deviceId = model.getString("deviceId");
                deviceDtId = model.getString("dtid");
                getAllDefectStandardId();
            }
        } else if (requestCode == ACTION_IMAGE) {
            mDefectImageList.add(currentImageName);
            String pictureContent = deviceName + "\n" + binding.etInputDefectContent.getText().toString() + "\n" + DateUtils.getFormatterTime(new Date(), "yyyy-MM-dd HH:mm");
            DrawCircleImageActivity.with(mActivity).setTxtContent(pictureContent).setPath(picParentFolder + currentImageName).setRequestCode(0x01).start();
        } else if (CANCEL_RESULT_LOAD_IMAGE == requestCode) {
            ArrayList<String> cancelList = data.getStringArrayListExtra(Config.CANCEL_IMAGE_URL_LIST_KEY);
            for (String imageUrl : cancelList) {
                mDefectImageList.remove(imageUrl.replace(picParentFolder, ""));
            }
            reFreshPicUI();
        } else if (requestCode == 0x01) {
            reFreshPicUI();
        }
    }

    public void reFreshPicUI() {
        Bitmap bitmap;
        if (!mDefectImageList.isEmpty()) {
            bitmap = BitmapUtils.getImageThumbnailByWidth(picParentFolder + mDefectImageList.get(0), 210);
            binding.ivDefectPic.setImageBitmap(bitmap);
            binding.tvDefectCount.setVisibility(mDefectImageList.size() <= 1 ? View.GONE : View.VISIBLE);
            binding.tvDefectCount.setText(String.valueOf(mDefectImageList.size()));
        } else {
            binding.ivDefectPic.setImageBitmap(null);
            binding.tvDefectCount.setText("");
            binding.tvDefectCount.setVisibility(View.GONE);
        }
        currentImageName="";
    }

    RadioGroup.OnCheckedChangeListener onCheckedChangeListener = (group, checkedId) -> {
        if (checkedId == R.id.rb_influnce_yes) {
            influnceBdz = "Y";
        } else if (checkedId == R.id.rb_influnce_no) {
            influnceBdz = "N";
        } else if (checkedId == R.id.rb_influnce_nothing) {
            influnceBdz = "N";
        }
    };
    private List<DbModel> defaultDefectStr = new ArrayList<>();
    private DefectDefaultAdapter defectDefaultAdapter;
    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(final Editable s) {
            if (defaultDefectModels != null && !defaultDefectModels.isEmpty()) {
                defaultDefectStr.clear();
                for (DbModel model : defaultDefectModels) {
                    if (model.getString("description").contains(s.toString())) {
                        defaultDefectStr.add(model);
                    }
                }
            }
            if (defectDefaultAdapter == null) {
                defectDefaultAdapter = new DefectDefaultAdapter(R.layout.textview_item, defaultDefectStr);
                binding.rcDefect.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                binding.rcDefect.setAdapter(defectDefaultAdapter);
                defectDefaultAdapter.setOnItemClickListener((adapter, view, position) -> {
                    DbModel model = (DbModel) adapter.getItem(position);
                    binding.etInputDefectContent.setText(model.getString("description"));
                    setCheckType(model);
                });
            } else {
                defectDefaultAdapter.setList(defaultDefectStr);
            }
        }
    };

    private void setCheckType(DbModel model) {
        if (TextUtils.isEmpty(model.getString("level"))) {
            return;
        }
        if (TextUtils.equals(model.getString("level"), DefectEnum.general_text.value)) {
            binding.rbGeneralDefect.setChecked(true);
        } else if (TextUtils.equals(model.getString("level"), DefectEnum.serious_text.value)) {
            binding.rbSeriousDefect.setChecked(true);
        } else if (TextUtils.equals(model.getString("level"), DefectEnum.critical_text.value)) {
            binding.rbCrisisDefect.setChecked(true);
        }
    }
}

