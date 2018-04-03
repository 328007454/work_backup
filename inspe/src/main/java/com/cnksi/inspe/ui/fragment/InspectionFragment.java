package com.cnksi.inspe.ui.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.inspe.BuildConfig;
import com.cnksi.inspe.R;
import com.cnksi.inspe.base.AppBaseFragment;
import com.cnksi.inspe.databinding.FragmentInspeInspectionBinding;
import com.cnksi.inspe.db.TeamService;
import com.cnksi.inspe.db.entity.InspecteTaskEntity;
import com.cnksi.inspe.type.RoleType;
import com.cnksi.inspe.type.TaskType;
import com.cnksi.inspe.ui.InspePlustekActivity;
import com.cnksi.inspe.ui.InspeTeamActivity;
import com.cnksi.inspe.utils.DateFormat;
import com.cnksi.inspe.utils.StringUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.cnksi.inspe.type.TaskType.*;

/**
 * 检查任务模块首页-检查任务列表
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/20 16:13
 */
public class InspectionFragment extends AppBaseFragment {


    private List<InspecteTaskEntity> list = new ArrayList<>();

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_inspe_inspection;
    }

    private FragmentInspeInspectionBinding dataBinding;
    private TeamService teamService = new TeamService();
    private BaseQuickAdapter adapter;
    private PageInterface pageInterface;

    @Override
    protected void lazyLoad() {
        dataBinding = (FragmentInspeInspectionBinding) fragmentDataBinding;


        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new InspecteTaskAdapter(R.layout.inspetask_item, list);
        adapter.openLoadAnimation();
        dataBinding.recyclerView.setAdapter(adapter);
        //头布局
//        View top = getLayoutInflater().inflate(R.layout.top_view, (ViewGroup) mRecyclerView.getParent(), false);
//        homeAdapter.addHeaderView(top);
        //分割线
        //dataBinding.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent();
                InspecteTaskEntity task = list.get(position);

                TaskType taskType = null;
                try {
                    taskType = TaskType.valueOf(task.getType());
                } catch (Exception e) {
                    showToast("任务类型不正确");
                    return;
                }

                switch (taskType) {
                    case jyhjc:
                        intent.setClass(getContext(), InspePlustekActivity.class).putExtra("task", task);
                        break;
                    case bzjs:
                        intent.setClass(getContext(), InspeTeamActivity.class).putExtra("task", task);
                        break;
                    case sbpc:
                    case sbjc:
                    default:
                        showToast("检查类型错误");
                        return;

                }

                startActivity(intent);
            }
        });

        //根据角色创建不同对象做不同事物
        try {
            switch (userService.getUser1().getRoleType()) {
                case director:
                case specialty:
                    pageInterface = new DirectorPage();
                    break;
                case team_leader:
                    pageInterface = new TeamLeaderPage();
                    break;
                case tracker:
                default:
                    pageInterface = new TrackerPage();
                    break;

            }
        } catch (Exception e) {
            showToast("角色类型错误！");
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        pageInterface.onSearchData();
    }

    public class InspecteTaskAdapter extends BaseQuickAdapter<InspecteTaskEntity, BaseViewHolder> {
        public InspecteTaskAdapter(int layoutResId, List data) {
            super(layoutResId, data);
        }

        final int[] firstBgResIds = new int[]

                {R.drawable.inspe_circle_red_shape, R.drawable.inspe_circle_green_shape, R.drawable.inspe_circle_blue_shape, R.drawable.inspe_circle_orange_shape
                };

        @Override
        protected void convert(BaseViewHolder helper, InspecteTaskEntity item) {


            //精益化检查(显示变电站)
            //班组建设(显示班组)
            //设备排查(显示变电站)

            //去首汉字显示
            String teamName = null;
            try {
                TaskType taskType = TaskType.valueOf(item.getType());

                if (TaskType.jyhjc == taskType) {
                    //精益化显示变电站
                    teamName = item.getBdz_name();
                } else if (TaskType.bzjs == taskType) {
                    //班组建设显示班组
                    teamName = item.getDept_name();
                } else {
                    teamName = item.getDept_name();
                }
            } catch (Exception e) {
                teamName = item.getDept_name();
                return;
            }


            String firstChar = StringUtils.getFirstChar(teamName);
            //首字显示
            helper.setText(R.id.firstNameTxt, firstChar);
            //首字背景
            helper.setBackgroundRes(R.id.firstNameTxt, firstBgResIds[firstChar.charAt(0) % firstBgResIds.length]);
            //班组名称/变电站名称
            helper.setText(R.id.teamTxt, teamName);
            //检查类型
            helper.setText(R.id.typeTxt, item.getCheck_type());
            //检查完成时间
            helper.setText(R.id.dateTxt, "时间：" + DateFormat.formatYMD(DateFormat.dbdateToLong(item.getPlan_check_time())));
            //检查人(可能是多人，用,分割的字符串)
            helper.setText(R.id.persionsTxt, item.getCheckuser_name().replace(",", " "));
        }
    }

    class DirectorPage implements PageInterface {

        @Override
        public void onSearchData() {
            List<InspecteTaskEntity> listTemp = teamService.getTaskList(null, null);
            list.clear();
            if (listTemp != null && listTemp.size() > 0) {
                list.addAll(listTemp);
            }
            adapter.notifyDataSetChanged();
        }
    }

    class TeamLeaderPage implements PageInterface {

        @Override
        public void onSearchData() {
            List<InspecteTaskEntity> listTemp = teamService.getTaskList(null, userService.getUser1().getDept_id());
            list.clear();
            if (listTemp != null && listTemp.size() > 0) {
                list.addAll(listTemp);
            }
            adapter.notifyDataSetChanged();
        }
    }

    class TrackerPage implements PageInterface {

        @Override
        public void onSearchData() {
            List<InspecteTaskEntity> listTemp = teamService.getTaskList(userService.getUserIds(), null);
            list.clear();
            if (listTemp != null && listTemp.size() > 0) {
                list.addAll(listTemp);
            }
            adapter.notifyDataSetChanged();
        }
    }

    public interface PageInterface {
        void onSearchData();
    }

}
