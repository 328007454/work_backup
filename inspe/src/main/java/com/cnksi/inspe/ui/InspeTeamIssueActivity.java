package com.cnksi.inspe.ui;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cnksi.inspe.R;
import com.cnksi.inspe.adapter.GalleryAdapter;
import com.cnksi.inspe.base.AppBaseActivity;
import com.cnksi.inspe.databinding.ActivityInspeTeamissueBinding;
import com.cnksi.inspe.db.TeamService;
import com.cnksi.inspe.db.entity.TeamRuleEntity;
import com.cnksi.inspe.db.entity.TeamRuleResultEntity;
import com.cnksi.inspe.entity.InspeScoreEntity;
import com.cnksi.inspe.entity.InspecteTaskEntity;
import com.cnksi.inspe.utils.ArrayInspeUtils;
import com.cnksi.inspe.utils.Config;
import com.cnksi.inspe.utils.FunctionUtil;
import com.cnksi.inspe.utils.FunctionUtils;
import com.cnksi.inspe.widget.DateDialog;
import com.cnksi.inspe.widget.PopItemWindow;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 班组建设检查-根据标准检查相关事项，并填写相关检查结果或扣分等
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/21 09:30
 */
public class InspeTeamIssueActivity extends AppBaseActivity implements View.OnClickListener {

    private ActivityInspeTeamissueBinding dataBinding;

    private static final int TAKEPIC_REQUEST = 100;
    //检查项
    private TeamRuleEntity teamRule;
    //任务
    private InspecteTaskEntity task;
    //最大扣分值
    private float maxMinus;
    //扣分详情
    private List<InspeScoreEntity> list;
    //检查记录
    private TeamRuleResultEntity teamRuleResult = new TeamRuleResultEntity();

    private TeamService teamService = new TeamService();
    private InspeScoreEntity scoreBean;
    //当前扣分项(单位扣分)(将扣分的float转为int后处理，避免float计算误差导致不正确的数引入)
    private int scoreEntity;
    //当前扣分
    private int minusScore;

    private GalleryAdapter galleryAdapte;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_inspe_teamissue;
    }

    @Override
    public void initUI() {
        dataBinding = (ActivityInspeTeamissueBinding) rootDataBinding;
        dataBinding.addBtn.setOnClickListener(this);
        dataBinding.minuxBtn.setOnClickListener(this);
        dataBinding.dateBtn.setOnClickListener(this);
        dataBinding.dateEdit.setOnClickListener(this);
        dataBinding.cameraBtn.setOnClickListener(this);
        dataBinding.issueInfoTxt.setOnClickListener(this);
        dataBinding.okBtn.setOnClickListener(this);
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
        teamRule = (TeamRuleEntity) getIntent().getSerializableExtra("data");
        maxMinus = 10 * getIntent().getFloatExtra("max_minus", 0);
        task = (InspecteTaskEntity) getIntent().getSerializableExtra("task");

        if (teamRule == null || task == null) {
            showToast("参数错误！");
            finish();
        }

        list = JSON.parseObject(teamRule.getStand_content(), new TypeReference<List<InspeScoreEntity>>() {
        });


        if (list != null) {
            list.add(0, new InspeScoreEntity("无"));
        }

        dataBinding.addBtn.setEnabled(false);
        dataBinding.minuxBtn.setEnabled(false);
        dataBinding.contextTxt.setText(teamRule.getName());

    }

    /**
     * 扣分显示<p/>
     * 根据最大扣分值来作为判断
     *
     * @param value
     */
    private void setScoreTxt(int value) {
        boolean isAdd = true;
        int nextValue = minusScore + value;
        if (nextValue > maxMinus) {
            isAdd = false;
            dataBinding.addBtn.setEnabled(false);
        } else if (nextValue == maxMinus || scoreEntity == 0) {
            dataBinding.addBtn.setEnabled(false);
        } else {
            dataBinding.addBtn.setEnabled(true);
        }

        if (nextValue < scoreEntity) {
            isAdd = false;
            dataBinding.minuxBtn.setEnabled(false);
        } else if (nextValue == scoreEntity) {
            dataBinding.minuxBtn.setEnabled(false);
        } else {
            dataBinding.minuxBtn.setEnabled(true);
        }

        if (isAdd) {
            minusScore = nextValue;
        }

        dataBinding.minusScoreEdit.setText(String.format("%.1" +
                "f", (minusScore / 10f)));

    }

    //**整改日期*/
    private long dateTime = -1;

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.dateBtn || i == R.id.dateEdit) {
            DateDialog dateDialog = new DateDialog(this);
            dateDialog.setOnDialogListener(new DateDialog.OnDialogListener() {
                @Override
                public void onDateChanged(long date, int year, int month, int day) {
                    dataBinding.dateEdit.setText(year + "年" + month + "月" + day + "日");
                    dateTime = date;
                    Log.e(tag, "选择日期:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date));
                }
            });
            dateDialog.show();


        } else if (i == R.id.issueInfoTxt) {
            new PopItemWindow(this).setInspeScoreAdapter(list).setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    scoreBean = list.get(position);

                    if (position == 0) {
                        dataBinding.issueInfoTxt.setText(null);
                    } else {
                        dataBinding.issueInfoTxt.setText(scoreBean.content);
                    }

                    minusScore = 0;
                    scoreEntity = (int) (scoreBean.score * 10);
                    setScoreTxt(scoreEntity);
                }

            }).showAsDropDown(v);

        } else if (i == R.id.okBtn) {
            if (!TextUtils.isEmpty(dataBinding.issueInfoTxt.getText().toString().trim())) {
                if (TextUtils.isEmpty(dataBinding.issueEdit.getText().toString().trim())) {
                    showToast("请输入问题描述");
                    return;
                } else if (TextUtils.isEmpty(dataBinding.dateEdit.getText().toString().trim())) {
                    showToast("请选择整改日期");
                    return;
                }
            }

            //任务ID
            teamRuleResult.setBdz_id(task.id);
            //标准ID
            teamRuleResult.setRule_id(teamRule.getId());
            teamRuleResult.setRule_name(teamRule.getName());
            //班组ID
            teamRuleResult.setDept_id("");
            teamRuleResult.setDept_id("");
            //检查人ID
            teamRuleResult.setCheck_person_id("");
            teamRuleResult.setCheck_person_id("");


            //问题类型(记录类型：问题（answer）、普通记录（normal）)
            if (TextUtils.isEmpty(dataBinding.issueInfoTxt.getText().toString().trim())) {
                teamRuleResult.setRecord_type("normal");
            } else {
                teamRuleResult.setRecord_type("answer");
                //扣分情况
                teamRuleResult.setDeduct_score(minusScore / 10f);//放大数据后对数据进行缩小
                //问题描述
                teamRuleResult.setDescription(dataBinding.issueEdit.getText().toString().trim());
                //扣分原因json
                teamRuleResult.setReason(JSON.toJSONString(scoreBean));
                //状态（问题进度：未分配、未整改、未审核、未审核通过、已闭环）
                teamRuleResult.setProgress("未分配");
                //整改期限
                teamRuleResult.setPlan_improve_time(dateTime);
            }

            //图片(疑问？是否需要上传)
            teamRuleResult.setImg(ArrayInspeUtils.toListString(picList));
            //检查时间
            long time = System.currentTimeMillis();
            teamRuleResult.setCreate_time(time);
            teamRuleResult.setInsert_time(time);
            teamRuleResult.setLast_modify_time(time);


            if (teamService.saveRuleResult(teamRuleResult)) {
                showToast("操作成功");
                finish();
            } else {
                showToast("保存数据库失败！");
            }


        } else if (i == R.id.cameraBtn) {
            if (picList.size() < 3) {
                picTempPath = FunctionUtil.getCurrentImageName(this);
                FunctionUtils.takePicture(this, picTempPath, Config.RESULT_PICTURES_FOLDER, TAKEPIC_REQUEST);

                //文件绝对路径
                picTempPath = Config.RESULT_PICTURES_FOLDER + picTempPath;
            } else {
                showToast("目前仅支持上传3张图片");
            }

        } else if (i == R.id.addBtn) {
            setScoreTxt(scoreEntity);
        } else if (i == R.id.minuxBtn) {
            setScoreTxt(-scoreEntity);
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
