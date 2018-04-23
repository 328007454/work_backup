package com.cnksi.inspe.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.inspe.R;
import com.cnksi.inspe.base.AppBaseFragment;
import com.cnksi.inspe.databinding.FragmentInspeInspectionBinding;
import com.cnksi.inspe.db.TaskService;
import com.cnksi.inspe.db.entity.InspecteTaskEntity;
import com.cnksi.inspe.db.entity.UserEntity;
import com.cnksi.inspe.type.RoleType;
import com.cnksi.inspe.type.TaskType;
import com.cnksi.inspe.ui.InspeCreateActivity;
import com.cnksi.inspe.ui.InspeModifyActivity;
import com.cnksi.inspe.ui.InspePlustekActivity;
import com.cnksi.inspe.ui.InspeTeamActivity;
import com.cnksi.inspe.utils.DateFormat;
import com.cnksi.inspe.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 检查任务模块首页-检查任务列表
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/20 16:13
 */
public class InspectionFragment extends AppBaseFragment implements View.OnClickListener {


    private List<InspecteTaskEntity> list = new ArrayList<>();
    //专家用户
    private UserEntity expertUser = userService.getUserExpert(RoleType.expert);

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_inspe_inspection;
    }

    private FragmentInspeInspectionBinding dataBinding;
    private TaskService taskService = new TaskService();
    private BaseQuickAdapter adapter;
    private PageInterface pageInterface;
    private String[] taskTypes;

    @Override
    protected void lazyLoad() {

    }
    @Override
    protected void initUI() {
        super.initUI();
        Log.e(tag, "initUI()");
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
        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                InspecteTaskEntity task = list.get(position);

                TaskType taskType = null;
                try {
                    taskType = TaskType.valueOf(task.getType());
                    if (taskType == TaskType.jyhjc && !TextUtils.isEmpty(task.getPersion_device_bigid())) {//修改精益化任务
                        new AlertDialog.Builder(getContext())
                                .setTitle("任务修改确认").setMessage("任务已开始，你确定要修改评价的设备类型吗？\n")
                                .setPositiveButton("确定",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivity(new Intent(getContext(), InspeModifyActivity.class).putExtra("task_id", task.getId()));
                                            }
                                        })
                                .setNegativeButton("取消",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        })
                                .show();

                    } else {
                        startActivity(new Intent(getContext(), InspeModifyActivity.class).putExtra("task_id", task.getId()));
                    }

                } catch (Exception e) {
                    showToast("任务类型不正确");
                }
                return false;
            }
        });
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
                        if (TextUtils.isEmpty(task.getPersion_device_bigid())) {
                            intent.setClass(getContext(), InspeModifyActivity.class).putExtra("task_id", task.getId());
                        } else {
                            intent.setClass(getContext(), InspePlustekActivity.class).putExtra("task", task);
                        }
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
                    pageInterface = new TrackerPage();
                    break;
                default:
                    pageInterface = new PageInterface() {
                        @Override
                        public void onSearchData() {
                            //专家
                            if (expertUser != null) {
                                List<InspecteTaskEntity> listTemp = taskService.getTaskList(new String[]{expertUser.getId()}, null, taskTypes);
                                list.clear();
                                if (listTemp != null && listTemp.size() > 0) {
                                    list.addAll(listTemp);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        }
                    };
                    break;

            }
        } catch (
                Exception e)

        {
            showToast("角色类型错误！");
        }

        dataBinding.createTaskBtn.setVisibility(View.GONE);
        if (expertUser == null) {
//            dataBinding.createTaskBtn.setVisibility(View.GONE);
            taskTypes = new String[]{TaskType.bzjs.name()};
        } else {
//            dataBinding.createTaskBtn.setOnClickListener(this);
//            dataBinding.createTaskBtn.setVisibility(View.VISIBLE);
            taskTypes = new String[]{TaskType.bzjs.name(), TaskType.jyhjc.name()};
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        pageInterface.onSearchData();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.createTaskBtn) {
            startActivity(new Intent(getActivity(), InspeCreateActivity.class));
        }
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


            //精益化评价(显示变电站)
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
            if (TaskType.jyhjc.name().equals(item.check_type)) {
                helper.setText(R.id.dateTxt, "时间：" + DateFormat.formatYM(DateFormat.dbdateToLong(item.getPlan_check_time())));
            } else {
                helper.setText(R.id.dateTxt, "时间：" + DateFormat.formatYMD(DateFormat.dbdateToLong(item.getPlan_check_time())));
            }
            //检查人(可能是多人，用,分割的字符串)
            helper.setText(R.id.persionsTxt, item.getCheckuser_name().replace(",", " "));
        }
    }

    class DirectorPage implements PageInterface {

        @Override
        public void onSearchData() {
            List<InspecteTaskEntity> listTemp = taskService.getTaskList(null, null, taskTypes);
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
            List<InspecteTaskEntity> listTemp = taskService.getTaskList(null, userService.getUser1().getDept_id(), taskTypes);
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
            List<InspecteTaskEntity> listTemp = taskService.getTaskList(userService.getUserIds(), null, taskTypes);
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