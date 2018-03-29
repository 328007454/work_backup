package com.cnksi.inspe.ui.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.inspe.R;
import com.cnksi.inspe.base.AppBaseFragment;
import com.cnksi.inspe.databinding.FragmentInspeIssueBinding;
import com.cnksi.inspe.db.TeamService;
import com.cnksi.inspe.db.entity.TeamRuleResultEntity;
import com.cnksi.inspe.ui.InspeIssueDetailActivity;
import com.cnksi.inspe.utils.DateFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * 检查任务模块首页-所有问题
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/20 14:31
 */
public class AllIssueFragment extends AppBaseFragment {


    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_inspe_issue;
    }

    private FragmentInspeIssueBinding dataDinding;
    private List<TeamRuleResultEntity> list = new ArrayList<>();
    private TeamService teamService = new TeamService();

    @Override
    protected void lazyLoad() {
        Log.e(tag, "lazyLoad()");

        dataDinding = (FragmentInspeIssueBinding) fragmentDataBinding;
        dataDinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //模拟数据
        List<TeamRuleResultEntity> listTemp = teamService.getIssueList();
        if (listTemp != null && listTemp.size() > 0) {
            list.addAll(listTemp);
        }

        BaseQuickAdapter adapter = new InspeIssueAdapter(R.layout.inspeissue_item, list);
        adapter.openLoadAnimation();
//        View top = getLayoutInflater().inflate(R.layout.top_view, (ViewGroup) mRecyclerView.getParent(), false);
//        homeAdapter.addHeaderView(top);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getContext(), InspeIssueDetailActivity.class);
                getContext().startActivity(intent);
            }
        });
        dataDinding.recyclerView.setAdapter(adapter);
    }

    public class InspeIssueAdapter extends BaseQuickAdapter<TeamRuleResultEntity, BaseViewHolder> {
        public InspeIssueAdapter(int layoutResId, List data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, TeamRuleResultEntity item) {
            helper.setText(R.id.contextTxt, item.getDescription());
            helper.setText(R.id.dateTxt, DateFormat.formatYMD(DateFormat.dbdateToLong(item.getPlan_improve_time())));
            helper.setText(R.id.stateTxt, item.getProgress());
        }
    }
}
