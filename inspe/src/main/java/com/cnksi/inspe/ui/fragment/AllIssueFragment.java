package com.cnksi.inspe.ui.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.PopupWindow;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.inspe.R;
import com.cnksi.inspe.base.AppBaseFragment;
import com.cnksi.inspe.databinding.FragmentInspeIssueBinding;
import com.cnksi.inspe.db.TeamService;
import com.cnksi.inspe.db.UserService;
import com.cnksi.inspe.db.entity.TeamRuleResultEntity;
import com.cnksi.inspe.type.ProgressType;
import com.cnksi.inspe.type.RoleType;
import com.cnksi.inspe.type.TaskType;
import com.cnksi.inspe.ui.InspeIssueDetailActivity;
import com.cnksi.inspe.utils.DateFormat;
import com.cnksi.inspe.widget.PopItemWindow;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 检查任务模块首页-所有问题
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/20 14:31
 */
public class AllIssueFragment extends AppBaseFragment implements View.OnClickListener {


    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_inspe_issue;
    }

    private FragmentInspeIssueBinding dataDinding;
    private List<TeamRuleResultEntity> list = new ArrayList<>();
    private TeamService teamService = new TeamService();

    private TaskType taskType = null;
    private BaseQuickAdapter adapter;
    private PageLister pageLister;
    private UserService userService = UserService.getInstance();

    private static SoftReference<List<TeamRuleResultEntity>> weakRef;

    public static List<TeamRuleResultEntity> getList() {
        if (weakRef != null) {
            return weakRef.get();
        }
        return null;
    }

    @Override
    protected void lazyLoad() {
        Log.e(tag, "lazyLoad()");

        dataDinding = (FragmentInspeIssueBinding) fragmentDataBinding;
        dataDinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        adapter = new InspeIssueAdapter(R.layout.inspeissue_item, list);
        adapter.openLoadAnimation();
//        View top = getLayoutInflater().inflate(R.layout.top_view, (ViewGroup) mRecyclerView.getParent(), false);
//        homeAdapter.addHeaderView(top);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getContext(), InspeIssueDetailActivity.class);
                intent.putExtra("data", list.get(position));
                intent.putExtra("positon", position);
                intent.putExtra("start_type", "all_issue");
                getContext().startActivity(intent);
            }
        });
        dataDinding.recyclerView.setAdapter(adapter);

        dataDinding.searchInspeTypeTxt.setOnClickListener(this);
        dataDinding.searchConvertTxt.setOnClickListener(this);
        dataDinding.batRunBtn.setVisibility(View.GONE);

        inspeTypeList.add("全部");
        inspeTypeList.add("班组建设检查");
        inspeTypeList.add("精益化评价");
//        inspeTypeList.add("设备排查");

        convertList.add("全部");

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

                    @Override
                    public void onBat(TaskType taskType, String bbzdId) {
                    }
                };
                break;
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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    private List<String> inspeTypeList = new ArrayList<>();
    private List<String> convertList = new ArrayList<>();

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.searchInspeTypeTxt) {//查询类型
            new PopItemWindow(getContext()).setListAdapter(inspeTypeList).setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    switch (position) {
                        case 0:
                            pageLister.onSearch(null, null);
                            dataDinding.searchInspeTypeTxt.setText("全部");
                            dataDinding.searchConvertTxt.setEnabled(false);
                            dataDinding.batRunBtn.setVisibility(View.GONE);

                            break;
                        case 1:
                            pageLister.onSearch(TaskType.bzjs.name(), null);
                            dataDinding.searchInspeTypeTxt.setText("班组建设检查");
                            dataDinding.searchConvertTxt.setEnabled(false);
                            dataDinding.searchConvertTxt.setText("");
                            dataDinding.batRunBtn.setVisibility(View.VISIBLE);

                            break;
                        case 2:
                            pageLister.onSearch(TaskType.jyhjc.name(), null);
                            dataDinding.searchInspeTypeTxt.setText("精益化评价");
                            dataDinding.searchConvertTxt.setEnabled(true);
                            dataDinding.searchConvertTxt.setText("全部");
                            dataDinding.batRunBtn.setVisibility(View.GONE);

                            break;
                        case 3:
                    }
                }
            }).setPopWindowWidth(v.getWidth()).showAsDropDown(v);

        } else if (v.getId() == R.id.searchConvertTxt) {//变电站查询
            new PopItemWindow(getContext()).setListAdapter(convertList).setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    if (position == 0) {//全部
                        pageLister.onSearch(TaskType.jyhjc.name(), null);
                        dataDinding.batRunBtn.setVisibility(View.GONE);
                    } else {//筛选
                        pageLister.onSearch(TaskType.jyhjc.name(), convertList.get(position));
                        dataDinding.batRunBtn.setVisibility(View.VISIBLE);
                    }
                }
            }).setPopWindowWidth(v.getWidth()).showAsDropDown(v);
        } else if (v.getId() == R.id.batRunBtn) {//批处理
            pageLister.onBat(taskType, null);
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
                e.printStackTrace();
            }

        }
    }

    class TrackerPage implements PageLister {

        @Override
        public void onSearch(String taskType, String bzdId) {
            //初始数据
            List<TeamRuleResultEntity> listTemp = teamService.getIssueListForGroupIds(userService.getUserGroup0(), taskType, bzdId);
            list.clear();
            if (listTemp != null && listTemp.size() > 0) {
                list.addAll(listTemp);
            }

            adapter.notifyDataSetChanged();
        }

        @Override
        public void onBat(TaskType askType, String bbzdId) {
            //无
        }
    }

    private class TeamLeaderPage implements PageLister {

        @Override
        public void onSearch(String taskType, String bzdId) {
            //初始数据
            List<TeamRuleResultEntity> listTemp = teamService.getIssueListForGroupIds(userService.getUserGroup0(), taskType, bzdId);
            list.clear();
            if (listTemp != null && listTemp.size() > 0) {
                list.addAll(listTemp);
            }

            adapter.notifyDataSetChanged();
        }

        @Override
        public void onBat(TaskType askType, String bbzdId) {
            //弹出对话框，选择分配、审核
            //根据用户选择查询数据
        }
    }

    private class DirectorPage implements PageLister {

        @Override
        public void onSearch(String taskType, String bzdId) {
            //初始数据
            List<TeamRuleResultEntity> listTemp = teamService.getIssueListForGroupIds(null, taskType, bzdId);
            list.clear();
            if (listTemp != null && listTemp.size() > 0) {
                list.addAll(listTemp);
            }

            adapter.notifyDataSetChanged();
        }

        @Override
        public void onBat(TaskType askType, String bbzdId) {
            //弹出对话框选择分享、审核
        }
    }

    private interface PageLister {
        /**
         * 根据角色查询数据
         *
         * @param taskType
         * @param bzdId
         */
        void onSearch(String taskType, String bzdId);

        /**
         * 批量处理
         */
        void onBat(TaskType taskType, String bbzdId);

    }
}
