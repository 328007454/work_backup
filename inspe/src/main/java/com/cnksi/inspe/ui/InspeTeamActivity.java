package com.cnksi.inspe.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.inspe.R;
import com.cnksi.inspe.adapter.TeamRoleAdapter;
import com.cnksi.inspe.adapter.entity.TeamRole0Entity;
import com.cnksi.inspe.adapter.entity.TeamRoleEntity;
import com.cnksi.inspe.base.AppBaseActivity;
import com.cnksi.inspe.databinding.ActivityInspeTeamBinding;
import com.cnksi.inspe.db.TaskService;
import com.cnksi.inspe.db.TeamService;
import com.cnksi.inspe.db.entity.InspecteTaskEntity;
import com.cnksi.inspe.db.entity.TeamRuleEntity;
import com.cnksi.inspe.type.RecordType;
import com.cnksi.inspe.type.TaskProgressType;

import java.util.ArrayList;
import java.util.List;

/**
 * 班组建设检查-菜单页面(检查大项)
 * <br/>
 * <b>功能说明：</b>根据服务器检查标准显示不同的检查项
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/21 09:31
 */
public class InspeTeamActivity extends AppBaseActivity implements View.OnClickListener {

    private ActivityInspeTeamBinding dataBinding;


    private TeamService teamService = new TeamService();
    private TaskService taskService = new TaskService();

    @Override
    public int getLayoutResId() {
        return R.layout.activity_inspe_team;
    }

    private List<MultiItemEntity> list = new ArrayList<>();
    private InspecteTaskEntity task;
    private TeamRoleAdapter adapter;
    private Button bottomBtn;

    @Override
    public void initUI() {
        dataBinding = (ActivityInspeTeamBinding) rootDataBinding;
        setTitle("班组建设检查", R.drawable.inspe_left_black_24dp);


        task = (InspecteTaskEntity) getIntent().getSerializableExtra("task");
        if (task == null) {
            showToast("参数错误！");
            finish();
            return;
        }

        adapter = new TeamRoleAdapter(list);
        final GridLayoutManager manager = new GridLayoutManager(this, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.getItemViewType(position) == TeamRoleAdapter.TYPE_1 ? 1 : manager.getSpanCount();
            }
        });
        dataBinding.recyclerView.setAdapter(adapter);
        dataBinding.recyclerView.setLayoutManager(manager);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                TeamRoleEntity data = (TeamRoleEntity) adapter.getData().get(position);
                startActivity(new Intent(InspeTeamActivity.this, InspeTeamStandardActivity.class).putExtra("role_id", data.rule.getId()).putExtra("task", task));
            }
        });
        bottomBtn = (Button) getLayoutInflater().inflate(R.layout.inspe_recycle_buttom_btn, (ViewGroup) dataBinding.recyclerView.getParent(), false);
        bottomBtn.setOnClickListener(this);
        adapter.addFooterView(bottomBtn);

    }

    @Override
    public void initData() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        list.clear();
        adapter.notifyDataSetChanged();
        searchData();

    }

    //查询当前首个未检查的项位置
    private int doPosition = 0;
    //判断任务是否检查完毕，true检查完毕，false未检查完毕
    private boolean isOk = true;

    private void searchData() {
        List<TeamRuleEntity> ruleList = teamService.getRoleList();
        if (ruleList == null) {
            return;
        }
        //设置不可用状态
        isOk = true;
        doPosition = -1;

        for (int i = 0; i < ruleList.size(); i++) {
            TeamRuleEntity entity = ruleList.get(i);
            TeamRole0Entity rule0 = new TeamRole0Entity(entity);

            List<TeamRuleEntity> ruleItemList = teamService.getRoleList(entity.getId(), task.getId());
            for (TeamRuleEntity e : ruleItemList) {
                //如果状态不为不可用用状态则不处理(第一次有效，展开用户正在操作的项)
                if (doPosition == -1 && (e.getRecord_type() == null || e.getRecord_type().equals(RecordType.def.name()) || e.getRecord_type().equals(RecordType.ing.name()))) {
                    doPosition = i;
                }

                //检查是否已经检查完成
                if (isOk && (e.getRecord_type() == null || e.getRecord_type().equals(RecordType.def.name()) || e.getRecord_type().equals(RecordType.ing.name()))) {
                    isOk = false;
                }

                TeamRoleEntity rule = new TeamRoleEntity(e);
                rule0.addSubItem(rule);
            }

            list.add(rule0);
        }

        //设置默认状态
        if (doPosition == -1) {
            doPosition = 0;
        }

        //防止无数据处理异常
        if (ruleList.size() == 0) {
            isOk = false;
        }
        adapter.expand(doPosition);
        bottomBtn.setEnabled(isOk);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bottomBtn) {
            //修改任务状态；
            new AlertDialog.Builder(context)
                    .setTitle("任务完成确认").setMessage("您确定该任务你已经完成?\n")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    updateTask();
                                }
                            })
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                    .show();

        }
    }

    /**
     * 更新任务状态
     */
    private void updateTask() {
        task.setProgress(TaskProgressType.done.name());
        if (taskService.updateTask(task)) {
            showToast("保存完成");
            finish();
        } else {
            showToast("保存失败");
        }
    }
}
