package com.cnksi.inspe.ui;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.inspe.R;
import com.cnksi.inspe.adapter.GalleryAdapter;
import com.cnksi.inspe.base.AppBaseActivity;
import com.cnksi.inspe.databinding.ActivityInspeTeamissueBinding;
import com.cnksi.inspe.db.TaskService;
import com.cnksi.inspe.db.TeamService;
import com.cnksi.inspe.db.entity.TeamRuleEntity;
import com.cnksi.inspe.db.entity.TeamRuleResultEntity;
import com.cnksi.inspe.db.entity.InspeScoreEntity;
import com.cnksi.inspe.db.entity.InspecteTaskEntity;
import com.cnksi.inspe.type.ProgressType;
import com.cnksi.inspe.type.RecordType;
import com.cnksi.inspe.type.TaskProgressType;
import com.cnksi.inspe.utils.ArrayInspeUtils;
import com.cnksi.inspe.utils.Config;
import com.cnksi.inspe.utils.DateFormat;
import com.cnksi.inspe.utils.FileUtils;
import com.cnksi.inspe.utils.FunctionUtil;
import com.cnksi.inspe.utils.FunctionUtils;
import com.cnksi.inspe.utils.ImageUtils;
import com.cnksi.inspe.widget.DateDialog;
import com.cnksi.inspe.widget.PopItemWindow;

import java.io.File;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
    private TeamRuleResultEntity teamRuleResult;

    private TeamService teamService = new TeamService();
    private TaskService taskService = new TaskService();
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
        setTitle("问题记录", R.drawable.inspe_left_black_24dp);
        dataBinding = (ActivityInspeTeamissueBinding) rootDataBinding;
        dataBinding.addBtn.setOnClickListener(this);
        dataBinding.minuxBtn.setOnClickListener(this);
        dataBinding.dateBtn.setOnClickListener(this);
        dataBinding.dateTxt.setOnClickListener(this);
        dataBinding.cameraBtn.setOnClickListener(this);
        dataBinding.issueInfoTxt.setOnClickListener(this);
        dataBinding.okBtn.setOnClickListener(this);
        galleryAdapte = new GalleryAdapter(picList);
        galleryAdapte.setOnDeleteListener(new GalleryAdapter.OnDeleteListener() {
            @Override
            public void onDelete(String entity, int possion) {
                picDeleteList.add(picList.remove(possion));
                galleryAdapte.notifyDataSetChanged();
            }
        });
        galleryAdapte.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ArrayList<String> showList = new ArrayList<>(picList.size());
                for (int i = 0, size = picList.size(); i < size; i++) {
                    showList.add(FileUtils.getInpseRootPath() + picList.get(i));
                }
                ImageUtils.showImageDetails(context, position, showList, false, false);
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

        //
        teamRuleResult = teamService.getRuleResult(teamRule.getId(), task.getId());
        if (teamRuleResult == null) {
            teamRuleResult = new TeamRuleResultEntity();
        }
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

        dataBinding.minusScoreEdit.setText(String.format("%.1f", (minusScore / 10f)));

    }

    //**整改日期*/
    private long dateTime = -1;

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.dateBtn || i == R.id.dateTxt) {
            DateDialog dateDialog = new DateDialog(this);
            dateDialog.setOnDialogListener(new DateDialog.OnDialogListener() {
                @Override
                public void onDateChanged(long date, int year, int month, int day) {
                    dataBinding.dateTxt.setText(year + "年" + month + "月" + day + "日");
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
                        dataBinding.issueEdit.setText(null);
                    } else {
                        dataBinding.issueInfoTxt.setText(scoreBean.content);
                        dataBinding.issueEdit.setText(scoreBean.content.replaceAll(",{0,}，{0,}[扣].*分.*", "").replaceAll("[0-9].]{0,}[a-z)]{0,}[a-z]{0,}", ""));
                    }

                    minusScore = 0;
                    scoreEntity = (int) (scoreBean.score * 10);
                    setScoreTxt(scoreEntity);

                }

            }).showAsDropDown(v);

        } else if (i == R.id.okBtn) {
            if (!TextUtils.isEmpty(dataBinding.issueInfoTxt.getText().toString().trim())) {
                if (TextUtils.isEmpty(dataBinding.issueEdit.getText().toString().trim())) {
                    dataBinding.issueEdit.requestFocus();
                    showToast("请输入问题描述");
                    return;
                } else if (TextUtils.isEmpty(dataBinding.dateTxt.getText().toString().trim())) {
                    showToast("请输入整改日期");
                    dataBinding.dateBtn.performClick();
                    return;
                }
            }
            //创建ID
            if (teamRuleResult.getId() == null) {//创建或覆盖
                teamRuleResult.setId(UUID.randomUUID().toString().replace("-", ""));
                if (!TextUtils.isEmpty(teamRuleResult.getImg()) && picList.size() > 0) { //防止图片被覆盖后，本地图片没有清除
                    picDeleteList.addAll(Arrays.asList(teamRuleResult.getImg().split(",")));
                }
            }
            //任务ID
            teamRuleResult.setTask_id(task.id);
            //标准ID
            teamRuleResult.setRule_id(teamRule.getId());
            teamRuleResult.setRule_name(teamRule.getName());
            //被检查班组ID
            teamRuleResult.setDept_id(task.getDept_id());
            teamRuleResult.setDept_name(task.getDept_name());
            //检查人ID
            teamRuleResult.setCheck_person_id(getUserService().getUser1().getId());
            teamRuleResult.setCheck_person_name(getUserService().getUser1().getUsername());
            teamRuleResult.setCheck_type(task.getType());


            //问题类型(记录类型：问题（answer）、普通记录（normal）)
            if (TextUtils.isEmpty(dataBinding.issueInfoTxt.getText().toString().trim())) {
                teamRuleResult.setRecord_type(RecordType.normal.name());
                //扣分情况
                teamRuleResult.setDeduct_score(0);//放大数据后对数据进行缩小
                //问题描述
                teamRuleResult.setDescription(null);
                //扣分原因jsonArray
                teamRuleResult.setReason(null);
                //状态（问题进度：未分配、未整改、未审核、未审核通过、已闭环）
                teamRuleResult.setProgress(null);
                //整改期限
                teamRuleResult.setPlan_improve_time(null);

            } else {
                teamRuleResult.setRecord_type(RecordType.answer.name());
                //扣分情况
                teamRuleResult.setDeduct_score(minusScore / 10f);//放大数据后对数据进行缩小
                //问题描述
                teamRuleResult.setDescription(dataBinding.issueEdit.getText().toString().trim());
                //扣分原因jsonArray
                teamRuleResult.setReason(JSON.toJSONString(new InspeScoreEntity[]{scoreBean}));
                //状态（问题进度：未分配、未整改、未审核、未审核通过、已闭环）
                teamRuleResult.setProgress(ProgressType.wfp.name());
                //整改期限
                teamRuleResult.setPlan_improve_time(DateFormat.dateToDbString(dateTime));
            }

            //图片(疑问？是否需要上传)
            if (picList.size() > 0) {
                teamRuleResult.setImg(ArrayInspeUtils.toListString(picList));
            }
            //检查时间
            String datetime = DateFormat.dateToDbString(System.currentTimeMillis());
            teamRuleResult.setCreate_time(datetime);
            teamRuleResult.setInsert_time(datetime);
            teamRuleResult.setLast_modify_time(datetime);

            //更新任务状态
            if (task.getProgress() == null || TaskProgressType.valueOf(task.getProgress()) == TaskProgressType.todo) {
                task.setProgress(TaskProgressType.doing.name());
                taskService.updateTask(task);
            }

            if (teamService.saveRuleResult(teamRuleResult)) {
                showToast("操作成功");
                isSave = true;
                finish();
            } else {
                showToast("保存数据库失败！");
            }


        } else if (i == R.id.cameraBtn) {
            if (picList.size() < 3) {
                String picName = FileUtils.createInpseImgLongName(task);//生成图片名称
                picTempPath = FileUtils.getInpseImgPath(task);
                FunctionUtils.takePicture(this, picName, FileUtils.getInpseRootPath() + picTempPath, TAKEPIC_REQUEST);
                //文件相对地址
                picTempPath = picTempPath + picName;
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
    //**删除的地址的图片统一处理*/
    private Set<String> picDeleteList = new HashSet<>();
    private boolean isSave = false;//是否保存项目

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKEPIC_REQUEST) {
            if (resultCode == RESULT_OK) {
                BitmapUtils.compressImage(FileUtils.getInpseRootPath() + picTempPath, 4);
                picList.add(picTempPath);
                galleryAdapte.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isSave) {//保存成功
            for (String pic : picList) {
                picDeleteList.remove(pic);//移除
            }
        } else {
            if (teamRuleResult != null) {//重新编辑
                for (String pic : picList) {
                    picDeleteList.add(pic);//添加保存的图片
                }
                if (!TextUtils.isEmpty(teamRuleResult.getImg())) {
                    String[] imsg = teamRuleResult.getImg().split(",");
                    for (String pic : imsg) {
                        picDeleteList.remove(pic);//将删除的图片从列表中移除
                    }
                }
            } else {//取消
                for (String pic : picList) {
                    picDeleteList.add(pic);//将图片列表添加进删除文件夹
                }
            }
        }

        //删除文件
        for (String fileName : picDeleteList) {
            File file = new File(FileUtils.getInpseRootPath(), fileName);
            if (file.exists()) {
                file.delete();
            }
        }

    }
}
