package com.cnksi.inspe.ui.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.inspe.R;
import com.cnksi.inspe.base.AppBaseFragment;
import com.cnksi.inspe.databinding.FragmentInspeIssueBinding;
import com.cnksi.inspe.databinding.FragmentInspeMyissueBinding;
import com.cnksi.inspe.db.TeamService;
import com.cnksi.inspe.db.entity.InspecteTaskEntity;
import com.cnksi.inspe.db.entity.TeamRuleResultEntity;
import com.cnksi.inspe.type.ProgressType;
import com.cnksi.inspe.type.RoleType;
import com.cnksi.inspe.ui.InspeIssueDetailActivity;
import com.cnksi.inspe.utils.DateFormat;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 检查任务模块首页-我处理的问题
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/20 15:23
 */
public class MyIssueFragment extends AppBaseFragment {

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_inspe_myissue;
    }

    private FragmentInspeMyissueBinding dataDinding;
    private List<TeamRuleResultEntity> list = new ArrayList<>();
    private TeamService teamService = new TeamService();
    private BaseQuickAdapter adapter;
    private PageLister pageLister;

    private static SoftReference<List<TeamRuleResultEntity>> weakRef;

    public static List<TeamRuleResultEntity> getList() {
        if (weakRef != null) {
            return weakRef.get();
        }
        return null;
    }

    @Override
    protected void lazyLoad() {
        dataDinding = (FragmentInspeMyissueBinding) fragmentDataBinding;
        dataDinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        adapter = new InspeIssueAdapter(R.layout.inspeissue_item, list);
        adapter.openLoadAnimation();
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getContext(), InspeIssueDetailActivity.class);
                intent.putExtra("data", list.get(position));
                intent.putExtra("positon", position);
                intent.putExtra("start_type", "my_issue");
                getContext().startActivity(intent);
            }
        });
        dataDinding.recyclerView.setAdapter(adapter);

        switch (userService.getUser1().getRoleType()) {
            case director:
            case specialty:
                pageLister = new DirectorPage();
                break;
            case team_leader:
                pageLister = new TeamLeaderPage();
                break;
            case tracker:
            case guest:
                pageLister = new TrackerPage();
                break;
            default:
                pageLister = new PageLister() {
                    @Override
                    public void onSearch(String taskType, String bzdId) {

                    }
                };
                return;
        }

        weakRef = new SoftReference<List<TeamRuleResultEntity>>(list);
    }

    @Override
    public void onStart() {
        super.onStart();
        pageLister.onSearch(null, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (weakRef.get() == null) {
            weakRef = null;
        }
    }

    public class InspeIssueAdapter extends BaseQuickAdapter<TeamRuleResultEntity, BaseViewHolder> {
        public InspeIssueAdapter(int layoutResId, List data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, TeamRuleResultEntity item) {
            helper.setText(R.id.contextTxt, item.getDescription());
            helper.setText(R.id.dateTxt, DateFormat.formatYMD(DateFormat.dbdateToLong(item.getPlan_improve_time())));

            try {
                helper.setText(R.id.stateTxt, ProgressType.valueOf(item.getProgress()).getDesc());
            } catch (Exception e) {
            }
        }
    }

    class TrackerPage implements PageLister {

        @Override
        public void onSearch(String taskType, String bzdId) {
            List<TeamRuleResultEntity> listTemp = teamService.getIssueListForUserIds(userService.getUserIds(), taskType, bzdId, ProgressType.wzg.name());
            //维护人员
            list.clear();
            if (listTemp != null && listTemp.size() > 0) {
                list.addAll(listTemp);
            }

            adapter.notifyDataSetChanged();
        }
    }

    private class TeamLeaderPage implements PageLister {

        @Override
        public void onSearch(String taskType, String bzdId) {
            List<TeamRuleResultEntity> listTemp = teamService.getIssueListForGroupIds(userService.getUserGroup0(), taskType, bzdId, ProgressType.wfp.name(), ProgressType.shwtg.name(), ProgressType.wzg.name(), ProgressType.bzzwsh.name());
            list.clear();
            if (listTemp != null && listTemp.size() > 0) {
                list.addAll(listTemp);
            }

            adapter.notifyDataSetChanged();
        }
    }

    private class DirectorPage implements PageLister {

        @Override
        public void onSearch(String taskType, String bzdId) {
            List<TeamRuleResultEntity> listTemp = teamService.getIssueListForGroupIds(null, taskType, bzdId, ProgressType.zzwsh.name());
            //维护人员
            list.clear();
            if (listTemp != null && listTemp.size() > 0) {
                list.addAll(listTemp);
            }

            adapter.notifyDataSetChanged();
        }
    }

    private interface PageLister {
        void onSearch(String taskType, String bzdId);
    }
}
