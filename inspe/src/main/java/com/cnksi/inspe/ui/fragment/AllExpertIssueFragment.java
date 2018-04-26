package com.cnksi.inspe.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.inspe.R;
import com.cnksi.inspe.base.AppBaseFragment;
import com.cnksi.inspe.databinding.FragmentInspeIssueBinding;
import com.cnksi.inspe.db.DeviceService;
import com.cnksi.inspe.db.PlustekService;
import com.cnksi.inspe.db.TeamService;
import com.cnksi.inspe.db.entity.DeviceEntity;
import com.cnksi.inspe.db.entity.DeviceTypeEntity;
import com.cnksi.inspe.db.entity.TeamRuleResultEntity;
import com.cnksi.inspe.db.entity.UserEntity;
import com.cnksi.inspe.type.RoleType;
import com.cnksi.inspe.ui.InspePlustekIssueActivity;
import com.cnksi.inspe.widget.PopItemWindow;

import java.util.ArrayList;
import java.util.List;

/**
 * 检查任务模块首页-所有问题(专家)
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/20 14:31
 */
public class AllExpertIssueFragment extends AppBaseFragment implements View.OnClickListener {

    private PlustekService plustekService = new PlustekService();
    private TeamService teamService = new TeamService();
    private DeviceService deviceService = new DeviceService();

    @Override
    public int getFragmentLayout() {
        return R.layout.fragment_inspe_issue;
    }

    private FragmentInspeIssueBinding dataDinding;
    private List<TeamRuleResultEntity> list = new ArrayList<>();
    private List<DeviceTypeEntity> bigTypeList = new ArrayList<>();
    private List<String> bigTypeArray = new ArrayList<>();

    private BaseQuickAdapter adapter;

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected void initUI() {
        super.initUI();
        Log.e(tag, "initUI()");
        dataDinding = (FragmentInspeIssueBinding) fragmentDataBinding;
        dataDinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new InspeIssueAdapter(R.layout.inspexpert_issue_item, list);
        adapter.openLoadAnimation();
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                TeamRuleResultEntity entity = list.get(position);
                String[] names = plustekService.getLeve1_2Name(entity.getRule_id());
                Intent intent = new Intent(getContext(), InspePlustekIssueActivity.class)
                        .putExtra(InspePlustekIssueActivity.IntentKey.START_MODE, InspePlustekIssueActivity.StartMode.MODIFY)//
                        .putExtra(InspePlustekIssueActivity.IntentKey.TASK_ID, entity.getTask_id())//
                        .putExtra(InspePlustekIssueActivity.IntentKey.DEVICE_ID, entity.getDevice_id())//
//                        .putExtra(InspePlustekIssueActivity.IntentKey.PLUSTEK_TYPE, PlustekType.valueOf(entity.resultEntity.check_type))//
                        .putExtra(InspePlustekIssueActivity.IntentKey.RULE_RESULT_ID, entity.getId())
                        .putExtra(InspePlustekIssueActivity.IntentKey.CONTENT, entity.getDevice_name() + " " + names[0] + "-" + names[1]);
                startActivity(intent);
            }
        });
        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                new AlertDialog.Builder(getContext())
                        .setTitle("删除确认").setMessage("您是想要删除该问题记录?\n")
                        .setPositiveButton("删除",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        TeamRuleResultEntity entity = list.get(position);
                                        if (plustekService.delete(entity)) {
                                            orinList.remove(entity);
                                            list.remove(position);
                                            adapter.notifyDataSetChanged();
                                            showToast("删除成功");
                                            int error = plustekService.getIssueTotal(entity.task_id, entity.device_id);
                                            if (error == 0) {
                                                DeviceEntity deviceEntity = deviceService.getDeviceById(entity.device_id);
                                                if (deviceEntity != null && entity.task_id.equals(deviceEntity.type)) {
                                                    deviceEntity.setType(null);
                                                    deviceService.update(deviceEntity);
                                                    new AlertDialog.Builder(getActivity())
                                                            .setTitle("删除新增设备提示").setMessage("新增设备【" + deviceEntity.name + "】已被删除，如需记录问题请重新添加设备\n")
                                                            .setPositiveButton("确定",
                                                                    new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                        }
                                                                    })
                                                            .show();
                                                }
                                            }
                                        } else {
                                            showToast("删除失败！");
                                        }

                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                        .show();
                return false;
            }
        });

        dataDinding.recyclerView.setAdapter(adapter);

        dataDinding.searchTeam.setOnClickListener(this);
        dataDinding.searchSubstation.setVisibility(View.GONE);
        dataDinding.batRunBtn.setVisibility(View.GONE);
        dataDinding.searchInspeDescTxt.setText("设备类型：");

    }

    private UserEntity expertEntity = userService.getUserExpert(RoleType.expert);

    private List<TeamRuleResultEntity> orinList;
    private String lastBigId;

    @Override
    public void onStart() {
        super.onStart();
        if (expertEntity == null) {
            showToast("非法权限");
            getActivity().finish();
            return;
        }

        orinList = plustekService.getUserResult(expertEntity.getId());
        bigTypeList = plustekService.getUserResultBigType(expertEntity.getId());
        bigTypeArray.clear();
        bigTypeArray.add("全部");
        if (bigTypeList != null) {
            boolean isFind = true;
            for (DeviceTypeEntity entity : bigTypeList) {
                bigTypeArray.add(entity.getName());
                if (lastBigId != null && isFind && !Integer.toString(entity.getBigid()).equals(lastBigId)) {
                    isFind = false;
                }
            }
            if (isFind) {
                lastBigId = null;
                dataDinding.searchInspeTypeTxt.setText(null);
            }
        }

        search(lastBigId);
    }

    private void search(String bigId) {
        list.clear();
        adapter.notifyDataSetChanged();
        List<TeamRuleResultEntity> listTemp;

        if (bigId == null) {
            listTemp = orinList;
        } else {
            listTemp = new ArrayList<>();
            for (TeamRuleResultEntity entity : orinList) {
                if (bigId.equals(entity.device_bigtype)) {
                    listTemp.add(entity);
                }
            }
        }
        list.addAll(listTemp);
        adapter.notifyDataSetChanged();
    }

    private boolean isFilter(TeamRuleResultEntity entity) {
        if (lastBigId == null) {
            return true;
        }
        return lastBigId.equals(entity.getDevice_bigtype());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.searchTeam) {//查询设备大类
            new PopItemWindow(getContext()).setListAdapter(bigTypeArray).setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    if (0 != position && position < bigTypeArray.size()) {
                        lastBigId = Integer.toString(bigTypeList.get(position - 1).getBigid());
                        dataDinding.searchInspeTypeTxt.setText(bigTypeArray.get(position));
                    } else {
                        lastBigId = null;
                        dataDinding.searchInspeTypeTxt.setText(null);
                    }
                    search(lastBigId);
                }
            }).setPopWindowWidth(v.getWidth()).showAsDropDown(v);
        }
    }


    public class InspeIssueAdapter extends BaseQuickAdapter<TeamRuleResultEntity, BaseViewHolder> {
        public InspeIssueAdapter(int layoutResId, List data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, TeamRuleResultEntity item) {
            helper.setText(R
                    .id.deviceNameTxt, item.getDevice_name());
            helper.setText(R.id.descTxt, item.getDescription());
            helper.setText(R.id.scoreTxt, String.format("-%.2f分", item.getDeduct_score()));
        }
    }

}
