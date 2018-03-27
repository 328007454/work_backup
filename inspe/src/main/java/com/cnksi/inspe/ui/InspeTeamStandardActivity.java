package com.cnksi.inspe.ui;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.inspe.R;
import com.cnksi.inspe.base.AppBaseActivity;
import com.cnksi.inspe.databinding.ActivityInspeTeamstandardBinding;
import com.cnksi.inspe.db.TeamService;
import com.cnksi.inspe.db.entity.TeamRuleEntity;
import com.cnksi.inspe.entity.InspecteTaskEntity;
import com.cnksi.inspe.ui.fragment.InspectionFragment;
import com.cnksi.inspe.utils.DateFormat;
import com.cnksi.inspe.utils.StringUtils;
import com.cnksi.inspe.widget.DateDialog;

import java.util.ArrayList;
import java.util.List;

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

    private InspecteTaskEntity task;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_inspe_teamstandard;
    }

    @Override
    public void initUI() {
        dataBinding = (ActivityInspeTeamstandardBinding) rootDataBinding;
    }

    private List<TeamRuleEntity> doclist;
    private List<TeamRuleEntity> itemList;

    @Override
    public void initData() {
        String id = getIntent().getStringExtra("role_id");
        task = (InspecteTaskEntity) getIntent().getSerializableExtra("task");
        if (TextUtils.isEmpty(id) || task == null) {
            Toast.makeText(this, "参数错误!", Toast.LENGTH_SHORT).show();
            finish();
        }

        //文档标准
        doclist = teamService.getRoleDoc(id);
        String[] ids = new String[doclist.size()];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = doclist.get(i).getId();
        }
        itemList = teamService.getRoleStandard(ids);
        list.addAll(itemList);

        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //init adapter
        BaseQuickAdapter adapter = new InspecteTeamStandardAdapter(R.layout.inspesteam_standard_item, list);
        adapter.openLoadAnimation();
//        View top = getLayoutInflater().inflate(R.layout.top_view, (ViewGroup) mRecyclerView.getParent(), false);
//        homeAdapter.addHeaderView(top);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(InspeTeamStandardActivity.this, InspeTeamIssueActivity.class);
                intent.putExtra("data", itemList.get(position));
                intent.putExtra("task", task);
                //计算出可以被扣得分值
                intent.putExtra("max_minus", 5.0f);
                startActivity(intent);
            }
        });

        dataBinding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {

    }

    public class InspecteTeamStandardAdapter extends BaseQuickAdapter<TeamRuleEntity, BaseViewHolder> {
        public InspecteTeamStandardAdapter(int layoutResId, List data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, TeamRuleEntity item) {


            helper.setText(R.id.positonIdTxt, Integer.toString(helper.getAdapterPosition() + 1));
            helper.setText(R.id.contentTxt, item.getName());
            helper.setText(R.id.scoreTxt, "");
            //

        }
    }
}
