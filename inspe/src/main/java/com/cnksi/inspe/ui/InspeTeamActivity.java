package com.cnksi.inspe.ui;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.inspe.R;
import com.cnksi.inspe.adapter.TeamRoleAdapter;
import com.cnksi.inspe.adapter.entity.TeamRole0Entity;
import com.cnksi.inspe.adapter.entity.TeamRoleEntity;
import com.cnksi.inspe.base.AppBaseActivity;
import com.cnksi.inspe.databinding.ActivityInspeTeamBinding;
import com.cnksi.inspe.db.TeamService;
import com.cnksi.inspe.db.entity.TeamRuleEntity;
import com.cnksi.inspe.entity.InspecteTaskEntity;

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
public class InspeTeamActivity extends AppBaseActivity {

    private ActivityInspeTeamBinding dataBinding;


    private TeamService teamService = new TeamService();

    @Override
    public int getLayoutResId() {
        return R.layout.activity_inspe_team;
    }

    private List<MultiItemEntity> list = new ArrayList<>();
    private InspecteTaskEntity task;

    @Override
    public void initUI() {

        dataBinding = (ActivityInspeTeamBinding) rootDataBinding;
        task = (InspecteTaskEntity) getIntent().getSerializableExtra("task");
        if (task == null) {
            showToast("参数错误！");
            finish();
        }
//        dataBinding.recyclerView.setAdapter(null);
//        databinding
//        databinding.setAdapter(null);
        //测试
//        list.add(new TeamRuleEntity());
//        list.add(new TeamRuleEntity());
        initAdapterData();


        TeamRoleAdapter adapter = new TeamRoleAdapter(list);
        final GridLayoutManager manager = new GridLayoutManager(this, 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.getItemViewType(position) == TeamRoleAdapter.TYPE_1 ? 1 : manager.getSpanCount();
            }
        });

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //启动Item点击事件
                TeamRoleEntity data = (TeamRoleEntity) adapter.getData().get(position);
//                Toast.makeText(InspeTeamActivity.this, data.rule.getName(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(InspeTeamActivity.this, InspeTeamStandardActivity.class).putExtra("role_id", data.rule.getId()).putExtra("task", task));
            }
        });

        dataBinding.recyclerView.setAdapter(adapter);

        dataBinding.recyclerView.setAdapter(adapter);
        dataBinding.recyclerView.setLayoutManager(manager);
//        adapter.expandAll();
        adapter.expand(0);


    }

    @Override
    public void initData() {

    }

    private void initAdapterData() {
        List<TeamRuleEntity> ruleList = teamService.getRoleList();
        for (TeamRuleEntity entity : ruleList) {
            TeamRole0Entity rule0 = new TeamRole0Entity(entity);

            List<TeamRuleEntity> ruleItemList = teamService.getRoleList(entity.getId());
            for (TeamRuleEntity e : ruleItemList) {
                TeamRoleEntity rule = new TeamRoleEntity(e);
                rule0.addSubItem(rule);
            }

            list.add(rule0);
        }


    }

}
