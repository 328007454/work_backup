package com.cnksi.inspe.ui;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.inspe.R;
import com.cnksi.inspe.adapter.entity.TeamRoleEntity;
import com.cnksi.inspe.base.AppBaseActivity;
import com.cnksi.inspe.databinding.ActivityInspeTeamstandardBinding;
import com.cnksi.inspe.db.TaskService;
import com.cnksi.inspe.db.TeamService;
import com.cnksi.inspe.db.entity.InspeScoreEntity;
import com.cnksi.inspe.db.entity.TeamRuleEntity;
import com.cnksi.inspe.db.entity.InspecteTaskEntity;
import com.cnksi.inspe.db.entity.TeamRuleResultEntity;
import com.cnksi.inspe.type.ProgressType;
import com.cnksi.inspe.type.RecordType;
import com.cnksi.inspe.type.TaskProgressType;
import com.cnksi.inspe.type.TaskType;
import com.cnksi.inspe.utils.ArrayInspeUtils;
import com.cnksi.inspe.utils.DateFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 班组建设检查-显示检查标准
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/21 09:31
 */
public class InspeTeamStandardActivity extends AppBaseActivity implements View.OnClickListener {

    private ActivityInspeTeamstandardBinding dataBinding;

    private List<TeamRuleEntity> list = new ArrayList<>();

    private TeamService teamService = new TeamService();
    private TaskService taskService = new TaskService();

    private InspecteTaskEntity task;
    private Button bottomBtn;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_inspe_teamstandard;
    }

    @Override
    public void initUI() {
        dataBinding = (ActivityInspeTeamstandardBinding) rootDataBinding;
    }

    //    private List<TeamRuleEntity> doclist;
    private List<TeamRuleEntity> itemList;
    private String role2Id;
    private BaseQuickAdapter adapter;

    @Override
    public void initData() {
        setTitle("班组建设检查", R.drawable.inspe_left_black_24dp, R.mipmap.inspe_file_normal);

        role2Id = getIntent().getStringExtra("role_id");
        task = (InspecteTaskEntity) getIntent().getSerializableExtra("task");
        if (TextUtils.isEmpty(role2Id) || task == null) {
            Toast.makeText(this, "参数错误!", Toast.LENGTH_SHORT).show();
            finish();
        }


        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //init adapter
        adapter = new InspecteTeamStandardAdapter(R.layout.inspesteam_standard_item, list);
        adapter.openLoadAnimation();
//        View top = getLayoutInflater().inflate(R.layout.top_view, (ViewGroup) mRecyclerView.getParent(), false);
//        homeAdapter.addHeaderView(top);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                TeamRuleEntity entity = itemList.get(position);
                entity = teamService.getRoleScore(entity, task.getId());

                Intent intent = new Intent(InspeTeamStandardActivity.this, InspeTeamIssueActivity.class);
                intent.putExtra("data", entity);//计算可扣分数
                intent.putExtra("task", task);
                //计算出可以被扣得分值
                intent.putExtra("max_minus", (entity.getScoreCount() * 10 - entity.getDeduct_score() * 10) / 10f);
                startActivity(intent);
            }
        });
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                TeamRuleEntity entity = list.get(position);
                if (null == entity.getRecord_type()) {//未做得情况执行确认
                    saveOK(entity);
                } else {//做了的情况执行修改
                    entity = teamService.getRoleScore(entity, task.getId());

                    Intent intent = new Intent(InspeTeamStandardActivity.this, InspeTeamIssueActivity.class);
                    intent.putExtra("data", entity);//计算可扣分数
                    intent.putExtra("task", task);
                    //计算出可以被扣得分值
                    intent.putExtra("max_minus", (entity.getScoreCount() * 10 - entity.getDeduct_score() * 10) / 10f);
                    startActivity(intent);
                }
            }
        });

        dataBinding.recyclerView.setAdapter(adapter);

        bottomBtn = (Button) getLayoutInflater().inflate(R.layout.inspe_recycle_buttom_btn, (ViewGroup) dataBinding.recyclerView.getParent(), false);
        bottomBtn.setOnClickListener(this);
        adapter.addFooterView(bottomBtn);
    }

    private void saveOK(TeamRuleEntity teamRule) {
        teamRule.setRecord_type(RecordType.normal.name());

        TeamRuleResultEntity teamRuleResult = teamService.getRuleResult(teamRule.getId(), task.getId());
        //创建ID
        if (teamRuleResult == null) {//创建或覆盖
            teamRuleResult = new TeamRuleResultEntity();
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
        teamRuleResult.setCheck_person_id(getUserService().getUser1().getId());
        teamRuleResult.setCheck_person_name(getUserService().getUser1().getUsername());
        teamRuleResult.setCheck_type(task.getType());


        //问题类型(记录类型：问题（answer）、普通记录（normal）)
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


        //图片(疑问？是否需要上传)
        teamRuleResult.setImg(null);
        //检查时间
        String datetime = DateFormat.dateToDbString(System.currentTimeMillis());
        teamRuleResult.setCreate_time(datetime);
        teamRuleResult.setInsert_time(datetime);
        teamRuleResult.setLast_modify_time(datetime);


        if (!teamService.saveRuleResult(teamRuleResult)) {
            showToast("操作失败");
            teamRule.setRecord_type(null);
        }

        adapter.notifyDataSetChanged();

        //更新任务状态
        if (task.getProgress() == null || TaskProgressType.valueOf(task.getProgress()) == TaskProgressType.todo) {
            task.setProgress(TaskProgressType.doing.name());
            taskService.updateTask(task);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        list.clear();
        adapter.notifyDataSetChanged();
        //文档标准
//        doclist = teamService.getRoleDoc(role2Id);

        itemList = teamService.getRoleStandard(role2Id, task.getId());
        list.addAll(itemList);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bottomBtn) {
            //设置全部完成
            for (TeamRuleEntity entity : list) {
                if (entity.getRecord_type() == null) {
                    saveOK(entity);
                }
            }
            finish();
        }
    }

    @Override
    protected void onMenu(View view) {
        super.onMenu(view);
        startActivity(new Intent(context, InspeDocActivity.class).putExtra("role_id", role2Id).putExtra("task", task));
    }

    public class InspecteTeamStandardAdapter extends BaseQuickAdapter<TeamRuleEntity, BaseViewHolder> {
        public InspecteTeamStandardAdapter(int layoutResId, List data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, TeamRuleEntity item) {

            helper.addOnClickListener(R.id.scoreImg);//点击事件

            RecordType recordType = RecordType.valueOf(item.getRecord_type() != null ? item.getRecord_type() : RecordType.def.name());
            if (recordType != null) {
                switch (recordType) {
                    case answer://有问题
                        helper.setBackgroundRes(R.id.positonIdTxt, R.drawable.inspe_numcircle_red_shape);
                        helper.setImageResource(R.id.scoreImg, 0x00000000);
                        helper.setText(R.id.scoreTxt, String.format("-%.1f", item.getDeduct_score()));
                        break;
                    case normal://无问题
                        helper.setBackgroundRes(R.id.positonIdTxt, R.drawable.inspe_numcircle_green_shape);
                        helper.setImageResource(R.id.scoreImg, R.mipmap.inspe_standard_checked);
                        helper.setText(R.id.scoreTxt, "");
                        break;
                    case ing://进行中
                    case def://默认(未开始)
                    default:
                        helper.setBackgroundRes(R.id.positonIdTxt, R.drawable.inspe_numcircle_dark_shape);
                        helper.setImageResource(R.id.scoreImg, R.mipmap.inspe_standard_normal);
                        helper.setText(R.id.scoreTxt, "");
                        break;
                }
            } else {//未开始
                helper.setBackgroundRes(R.id.positonIdTxt, R.drawable.inspe_numcircle_dark_shape);
                helper.setImageResource(R.id.scoreImg, R.mipmap.inspe_standard_normal);
                helper.setText(R.id.scoreTxt, "");
            }

            helper.setText(R.id.positonIdTxt, Integer.toString(helper.getAdapterPosition() + 1));
            helper.setText(R.id.contentTxt, item.getName());
            //

        }
    }
}
