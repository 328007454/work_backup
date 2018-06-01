package com.cnksi.defect.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioGroup;

import com.cnksi.common.Config;
import com.cnksi.common.activity.DeviceSelectActivity;
import com.cnksi.common.activity.DrawCircleImageActivity;
import com.cnksi.common.base.BaseTitleActivity;
import com.cnksi.common.daoservice.BdzService;
import com.cnksi.common.model.Bdz;
import com.cnksi.common.utils.FunctionUtil;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.defect.R;
import com.cnksi.defect.databinding.ActivityAddDefectBinding;
import com.cnksi.defect.view.PopWindowCustom;

import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.Date;
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
        return binding.getRoot();
    }

    @Override
    public void initData() {
        bdzList = BdzService.getInstance().findAllBdzByDp(PreferencesUtils.get(Config.CURRENT_DEPARTMENT_ID, ""));
    }


    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_cancel) {

        } else if (id == R.id.btn_sure) {

        } else if (id == R.id.txt_bdz_name) {
            clickMode = BDZ;
            showBdzWindow();
            binding.etInputDevice.setText("");
            binding.txtSpaceName.setText("");
        } else if (id == R.id.txt_space_name) {
            binding.etInputDevice.setText("");
            clickMode = SPACE;
            jumpDeviceSelectActivity();
        } else if (id == R.id.iv_select_device) {
            clickMode = DEVICE;
            jumpDeviceSelectActivity();

        } else if (id == R.id.iv_defect_pic) {

        } else if (id == R.id.ib_take_picture) {
            if (TextUtils.isEmpty(binding.etInputDefectContent.getText().toString())) {
                ToastUtils.showMessage("请先输入缺陷");
            } else {
                currentImageName = FunctionUtil.getCurrentImageName(this);
                FunctionUtil.takePicture(this, currentImageName, picParentFolder, ACTION_IMAGE);
            }
        }
    }

    private void jumpDeviceSelectActivity() {
        if (null == bdz) {
            ToastUtils.showMessage("请先选择变电站");
            return;
        }
        Intent intent = new Intent(this, DeviceSelectActivity.class);
        intent.putExtra(Config.CURRENT_BDZ_ID, bdz.bdzid);
        intent.putExtra(Config.TITLE_NAME, "请选择间隔");
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
                    binding.txtBdzName.setText(bdz.name);
                }).setDropDownOfView(binding.txtBdzName).setBackgroundAlpha(0.6f).showAsDropDown(0, 10);

    }
    private String spaceName;
    private String deviceName;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Config.START_ACTIVITY_FORRESULT) {
            DbModel model = (DbModel) data.getSerializableExtra(DeviceSelectActivity.RESULT_SELECT_KEY);
            if (TextUtils.equals(clickMode, SPACE)) {
                spaceName = model.getString("spacingName");
                binding.txtSpaceName.setText(TextUtils.isEmpty(spaceName)?"":spaceName);
            } else if (TextUtils.equals(clickMode, DEVICE)) {
                spaceName = model.getString("spacingName");
                deviceName = model.getString("deviceName");
                binding.txtSpaceName.setText(TextUtils.isEmpty(spaceName)?"":spaceName);
                binding.etInputDevice.setText(TextUtils.isEmpty(deviceName)?"":deviceName);
            } else if (requestCode == ACTION_IMAGE) {
                mDefectImageList.add(currentImageName);
                String pictureContent = deviceName + "\n" + binding.etInputDefectContent.getText().toString() + "\n" + DateUtils.getFormatterTime(new Date(), "yyyy-MM-dd HH:mm");
                DrawCircleImageActivity.with(mActivity).setTxtContent(pictureContent).setPath(picParentFolder + currentImageName).setRequestCode(0x0).start();
            } else if (CANCEL_RESULT_LOAD_IMAGE == resultCode) {
                ArrayList<String> cancelList = data.getStringArrayListExtra(Config.CANCEL_IMAGEURL_LIST);
                for (String imageUrl : cancelList) {
                    mDefectImageList.remove(imageUrl.replace(picParentFolder, ""));
                }
                reFreshPicUI();
            } else if (requestCode == 0x0) {
                reFreshPicUI();
            }
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
    }

    private String txtReasonTitle;
    RadioGroup.OnCheckedChangeListener onCheckedChangeListener = (group, checkedId) -> {
        if (checkedId == R.id.rb_influnce_yes) {

        } else if (checkedId == R.id.rb_influnce_no) {

        }else if (checkedId == R.id.rb_influnce_nothing){

        }else if (checkedId == R.id.rb_general_defect){

        }else if (checkedId == R.id.rb_serious_defect){

        }else if(checkedId == R.id.rb_crisis_defect){

        }else if (checkedId == R.id.rb_problem_defect){

        }else  if (checkedId == R.id.rb_hidden_defect){

        }

    };
}

