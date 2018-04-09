package com.cnksi.inspe.ui;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.cnksi.inspe.R;
import com.cnksi.inspe.adapter.GalleryAdapter;
import com.cnksi.inspe.base.AppBaseActivity;
import com.cnksi.inspe.databinding.ActivityInspePlustekissueBinding;
import com.cnksi.inspe.db.DictionaryService;
import com.cnksi.inspe.db.TaskService;
import com.cnksi.inspe.db.TeamService;
import com.cnksi.inspe.db.entity.DictionaryEntity;
import com.cnksi.inspe.db.entity.InspeScoreEntity;
import com.cnksi.inspe.db.entity.InspecteTaskEntity;
import com.cnksi.inspe.db.entity.PlusteRuleEntity;
import com.cnksi.inspe.db.entity.TeamRuleResultEntity;
import com.cnksi.inspe.db.entity.UserEntity;
import com.cnksi.inspe.type.ProgressType;
import com.cnksi.inspe.type.RecordType;
import com.cnksi.inspe.type.RoleType;
import com.cnksi.inspe.type.TaskProgressType;
import com.cnksi.inspe.utils.ArrayInspeUtils;
import com.cnksi.inspe.utils.Config;
import com.cnksi.inspe.utils.DateFormat;
import com.cnksi.inspe.utils.FunctionUtil;
import com.cnksi.inspe.utils.FunctionUtils;
import com.cnksi.inspe.widget.PopItemWindow;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 精益化评价-根据标准检查相关事项，并填写相关检查结果或扣分等
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/21 09:25
 */
public class InspePlustekIssueActivity extends AppBaseActivity implements View.OnClickListener {
    private DictionaryService dictionaryService = new DictionaryService();
    private UserEntity expertEntity = getUserService().getUserExpert(RoleType.expert);
    private TeamService teamService = new TeamService();

    private static final int TAKEPIC_REQUEST = 100;
    private ActivityInspePlustekissueBinding dataBinding;
    private List<DictionaryEntity> natureList;
    private List<DictionaryEntity> reasonList;
    private List<String> natureArray;
    private List<String> reasonArray;
    //检查项
    private PlusteRuleEntity teamRule;
    //任务
    private InspecteTaskEntity task;
    //最大扣分值
    private float maxMinus;
    //当前扣分项(单位扣分)(将扣分的float转为int后处理，避免float计算误差导致不正确的数引入)
    private int scoreEntity;
    //当前扣分
    private int minusScore;

    private GalleryAdapter galleryAdapte;
    //**拍照临时图片地址*/
    private String picTempPath;
    //**拍照保存的地址*/
    private List<String> picList = new ArrayList<>();

    //检查记录
    private TeamRuleResultEntity teamRuleResult;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_inspe_plustekissue;
    }

    @Override
    public void initUI() {
        dataBinding = (ActivityInspePlustekissueBinding) rootDataBinding;
        setTitle("问题记录", R.drawable.inspe_left_black_24dp);

        dataBinding.addBtn.setOnClickListener(this);
        dataBinding.minuxBtn.setOnClickListener(this);
        dataBinding.dateBtn.setOnClickListener(this);
        dataBinding.dateTxt.setOnClickListener(this);
        dataBinding.cameraBtn.setOnClickListener(this);
        dataBinding.issueInfoTxt.setOnClickListener(this);
        dataBinding.okBtn.setOnClickListener(this);
        //
        dataBinding.issueNatureTxt.setOnClickListener(this);
        dataBinding.blameTeamTxt.setOnClickListener(this);
        dataBinding.blameBranchTxt.setOnClickListener(this);
        dataBinding.issueReasonTxt.setOnClickListener(this);

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
        Object teamRuleObject = getIntent().getSerializableExtra("data");
        maxMinus = 10 * getIntent().getFloatExtra("max_minus", 0);
        Object taskObject = getIntent().getSerializableExtra("task");

        if (teamRuleObject != null && teamRuleObject instanceof PlusteRuleEntity) {
            teamRule = (PlusteRuleEntity) teamRuleObject;
        }
        if (taskObject != null && taskObject instanceof InspecteTaskEntity) {
            task = (InspecteTaskEntity) taskObject;
        }

        if (teamRule == null || task == null) {
            showToast("参数错误!");
            finish();
            return;
        }


        if (expertEntity == null) {
            showToast("非法权限!");
            finish();
            return;
        }

        natureList = dictionaryService.getDictonaryIssueNature();
        reasonList = dictionaryService.getDictonaryIssueReason();
        if (natureList == null || natureList.size() == 0 || reasonList == null || reasonList.size() == 0) {
            showToast("没有查询到字典，请先同步数据!");
            finish();
            return;
        }
        natureArray = new ArrayList<>(natureList.size());
        reasonArray = new ArrayList<>(reasonList.size());
        for (int i = 0, size = natureList.size(); i < size; i++) {
            natureArray.add(natureList.get(i).getV());
        }
        for (int i = 0, size = reasonList.size(); i < size; i++) {
            reasonArray.add(reasonList.get(i).getV());
        }

        teamRuleResult = teamService.getRuleResult(teamRule.getId(), task.getId());
        if (teamRuleResult == null) {
            teamRuleResult = new TeamRuleResultEntity();
        }

    }

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

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.cameraBtn) {//拍照
            if (picList.size() < 3) {
                picTempPath = FunctionUtil.getCurrentImageName(this);
                FunctionUtils.takePicture(this, picTempPath, Config.RESULT_PICTURES_FOLDER, TAKEPIC_REQUEST);

                //文件绝对路径
                picTempPath = Config.RESULT_PICTURES_FOLDER + picTempPath;
            } else {
                showToast("目前仅支持上传3张图片");
            }
        } else if (i == R.id.addBtn) {//+扣分
            setScoreTxt(scoreEntity);
        } else if (i == R.id.minuxBtn) {//-扣分
            setScoreTxt(-scoreEntity);
        } else if (i == R.id.issueNatureTxt) {//问题性质(独立),一般、严重、危急
            new PopItemWindow(this).setListAdapter(natureArray).setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    dataBinding.issueNatureTxt.setText(natureList.get(position).getV());
                }
            }).showAsDropDown(view);
        } else if (i == R.id.issueReasonTxt) {//文件产生原因(独立)
            new PopItemWindow(this).setListAdapter(reasonArray).setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    dataBinding.issueNatureTxt.setText(reasonList.get(position).getV());
                }
            }).showAsDropDown(view);
        } else if (i == R.id.issueInfoTxt) {//错误问题
//            new PopItemWindow(this).setInspeScoreAdapter(list).setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
//                @Override
//                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                    scoreBean = list.get(position);
//
//                    if (position == 0) {
//                        dataBinding.issueInfoTxt.setText(null);
//                        dataBinding.issueEdit.setText(null);
//                    } else {
//                        dataBinding.issueInfoTxt.setText(scoreBean.content);
//                        dataBinding.issueEdit.setText(scoreBean.content.replaceAll(",{0,}，{0,}[扣].*分.*", "").replaceAll("[0-9].]{0,}[a-z)]{0,}[a-z]{0,}", ""));
//                    }
//
//                    minusScore = 0;
//                    scoreEntity = (int) (scoreBean.score * 10);
//                    setScoreTxt(scoreEntity);
//
//                }
//
//            }).showAsDropDown(v);

        } else if (i == R.id.blameTeamTxt) {//问题班组

        } else if (i == R.id.blameBranchTxt) {//责任单位

        } else if (i == R.id.okBtn) {//确定
            if (TextUtils.isEmpty(dataBinding.issueInfoTxt.getText().toString().trim())) {
                //没有错误直接关闭,并删除记录
                teamService.delete(teamRuleResult);
                finish();
            } else {
                if (TextUtils.isEmpty(dataBinding.issueEdit.getText().toString().trim())) {
                    dataBinding.issueEdit.requestFocus();
                    showToast("请输入问题描述");
                    return;
//                } else if (TextUtils.isEmpty(dataBinding.dateTxt.getText().toString().trim())) {
//                    showToast("请输入整改日期");
//                    dataBinding.dateBtn.performClick();
//                    return;
                } else if (TextUtils.isEmpty(dataBinding.issueReasonTxt.getText())) {
                    showToast("请选择产生原因");
                    dataBinding.issueReasonTxt.performClick();
                    return;
//                } else if (picList.size() == 0) {
//                    showToast("请对问题拍照");
//                    dataBinding.cameraBtn.performClick();
//                    return;
                } else if (TextUtils.isEmpty(dataBinding.issueNatureTxt.getText())) {
                    showToast("请选择问题性质");
                    dataBinding.issueNatureTxt.performClick();
                    return;
                } else if (TextUtils.isEmpty(dataBinding.blameTeamTxt.getText())) {
                    showToast("请选择责任班组");
                    dataBinding.issueReasonTxt.performClick();
                    return;
                } else if (TextUtils.isEmpty(dataBinding.blameBranchTxt.getText())) {
                    showToast("请选择责任单位");
                    dataBinding.issueReasonTxt.performClick();
                    return;
                } else if (TextUtils.isEmpty(dataBinding.issueReasonTxt.getText())) {
                    showToast("请选择产生原因");
                    dataBinding.issueReasonTxt.performClick();
                    return;
                } else if (TextUtils.isEmpty(dataBinding.issueEdit.getText().toString().trim())) {
                    showToast("请输入处理措施");
                    dataBinding.issueEdit.performClick();
                    return;
                }
            }
            //创建ID
            if (teamRuleResult.getId() == null) {//创建或覆盖
                teamRuleResult.setId(UUID.randomUUID().toString());
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
            teamRuleResult.setCheck_person_id(expertEntity.getId());
            teamRuleResult.setCheck_person_name(expertEntity.getUsername());
            teamRuleResult.setCheck_type(task.getType());

            //精益化评价独有
            //问题性质
            teamRuleResult.setProblem_nature(dataBinding.issueNatureTxt.getText().toString());
            //问题原因
            teamRuleResult.setProduce_reason(dataBinding.issueReasonTxt.getText().toString());
            //责任班组
            teamRuleResult.setCharge_group(dataBinding.blameTeamTxt.getText().toString());
            //责任单位
            teamRuleResult.setCharge_unit(dataBinding.blameBranchTxt.getText().toString());
            //建议处理方式
            teamRuleResult.setSuggest_deal_way(dataBinding.issueEdit.getText().toString().trim());

            //问题类型(记录类型：问题（answer）、普通记录（normal）)
            teamRuleResult.setRecord_type(RecordType.answer.name());
            //扣分情况
            teamRuleResult.setDeduct_score(minusScore / 10f);//放大数据后对数据进行缩小
            //问题描述
            teamRuleResult.setDescription(dataBinding.issueEdit.getText().toString().trim());
            //扣分原因jsonArray
//                teamRuleResult.setReason(JSON.toJSONString(new InspeScoreEntity[]{scoreBean}));
            //状态（问题进度：未分配、未整改、未审核、未审核通过、已闭环）
            teamRuleResult.setProgress(ProgressType.wfp.name());
            //整改期限
            //teamRuleResult.setPlan_improve_time(DateFormat.dateToDbString(dateTime));


            //图片(疑问？是否需要上传)
            teamRuleResult.setImg(ArrayInspeUtils.toListString(picList));
            //检查时间
            String datetime = DateFormat.dateToDbString(System.currentTimeMillis());
            teamRuleResult.setCreate_time(datetime);
            teamRuleResult.setInsert_time(datetime);
            teamRuleResult.setLast_modify_time(datetime);

            //更新任务状态
            if (task.getProgress() == null || TaskProgressType.valueOf(task.getProgress()) == TaskProgressType.todo) {
                task.setProgress(TaskProgressType.doing.name());
                new TaskService().updateTask(task);
            }

            if (teamService.saveRuleResult(teamRuleResult)) {
                showToast("操作成功");
                finish();
            } else {
                showToast("保存数据库失败！");
            }
        }
    }
}
