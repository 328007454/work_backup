package com.cnksi.inspe.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.inspe.R;
import com.cnksi.inspe.adapter.GalleryAdapter;
import com.cnksi.inspe.base.AppBaseActivity;
import com.cnksi.inspe.databinding.ActivityInspePlustekBinding;
import com.cnksi.inspe.databinding.ActivityInspeRecfityBinding;
import com.cnksi.inspe.db.TeamService;
import com.cnksi.inspe.db.entity.TeamRuleResultEntity;
import com.cnksi.inspe.entity.InspectePlustekEntity;
import com.cnksi.inspe.type.ProgressType;
import com.cnksi.inspe.type.RoleType;
import com.cnksi.inspe.utils.ArrayInspeUtils;
import com.cnksi.inspe.utils.Config;
import com.cnksi.inspe.utils.DateFormat;
import com.cnksi.inspe.utils.FunctionUtil;
import com.cnksi.inspe.utils.FunctionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 整改
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/21 09:04
 */
public class InspeRecfityActivity extends AppBaseActivity implements View.OnClickListener {

    private static final int TAKEPIC_REQUEST = 100;
    private ActivityInspeRecfityBinding dataBinding;
    private List<InspectePlustekEntity> list = new ArrayList<>();
    private TeamRuleResultEntity issueEntity;
    private TeamService teamService = new TeamService();

    @Override
    public int getLayoutResId() {
        return R.layout.activity_inspe_recfity;
    }

    private GalleryAdapter galleryAdapte;

    @Override
    public void initUI() {
        setTitle("整改", R.drawable.inspe_left_black_24dp);
        dataBinding = (ActivityInspeRecfityBinding) rootDataBinding;
        issueEntity = (TeamRuleResultEntity) getIntent().getSerializableExtra("data");

        dataBinding.cameraBtn.setOnClickListener(this);
        dataBinding.rectifyBtn.setOnClickListener(this);

        galleryAdapte = new GalleryAdapter(picList);
        galleryAdapte.setOnDeleteListener(new GalleryAdapter.OnDeleteListener() {
            @Override
            public void onDelete(String entity, int possion) {
                picList.remove(possion);
                galleryAdapte.notifyDataSetChanged();

                File file = new File(entity);
                if (file.exists()) {
                    file.delete();
                }
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        dataBinding.galleryView.setLayoutManager(linearLayoutManager);
        dataBinding.galleryView.setAdapter(galleryAdapte);
    }

    @Override
    public void initData() {
        if (issueEntity == null) {
            finish();
            return;
        }

        dataBinding.recfityNameTxt.setText(getUserService().getUser1().getUsername());
        dataBinding.convertDataTxt.setText(DateFormat.formatYMD(System.currentTimeMillis()));

    }

    private long recfityDate = -1;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rectifyBtn) {

            String descripton = dataBinding.descriptionTxt.getText().toString();
            if (TextUtils.isEmpty(descripton)) {
                descripton = "没问题";
            }

            if (recfityDate == -1) {
                recfityDate = System.currentTimeMillis();
            }


            issueEntity.setImprove_description(descripton);
            issueEntity.setImprove_person_id(getUserService().getUser1().getId());//整改人
            issueEntity.setImprove_person_name(getUserService().getUser1().getUsername());//整改人
            issueEntity.setImprove_time(DateFormat.dateToDbString(recfityDate));
            issueEntity.setImprove_img(ArrayInspeUtils.toListString(picList));//整改图片


//            if (RoleType.tracker == getUserService().getUser1().getRoleType()) {
            //①班组长主任审核
            issueEntity.setProgress(ProgressType.bzzwsh.name());
//            } else {
//                //②自动为主任审核
//                issueEntity.setProgress(ProgressType.zzwsh.name());
//            }

            teamService.saveRuleResult(issueEntity);

            setResult(RESULT_OK);
            finish();

        } else if (v.getId() == R.id.cameraBtn) {
            if (picList.size() < 3) {
                picTempPath = FunctionUtil.getCurrentImageName(this);
                FunctionUtils.takePicture(this, picTempPath, Config.RESULT_PICTURES_FOLDER, TAKEPIC_REQUEST);

                //文件绝对路径
                picTempPath = Config.RESULT_PICTURES_FOLDER + picTempPath;
            } else {
                showToast("目前仅支持上传3张图片");
            }
        }
    }

    //**拍照临时图片地址*/
    private String picTempPath;
    //**拍照保存的地址*/
    private List<String> picList = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKEPIC_REQUEST) {
            if (resultCode == RESULT_OK) {
                picList.add(picTempPath);
                galleryAdapte.notifyDataSetChanged();
            }
        }
    }
}