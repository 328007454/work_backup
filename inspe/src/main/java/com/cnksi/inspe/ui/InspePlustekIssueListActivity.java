package com.cnksi.inspe.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.inspe.R;
import com.cnksi.inspe.base.AppBaseActivity;
import com.cnksi.inspe.databinding.ActivityInspePlustekIssuelistBinding;
import com.cnksi.inspe.db.PlustekService;
import com.cnksi.inspe.db.entity.TeamRuleResultEntity;
import com.cnksi.inspe.entity.IssueListEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 精益化检查-问题列表
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/04/10 15:49
 */
public class InspePlustekIssueListActivity extends AppBaseActivity {

    private ActivityInspePlustekIssuelistBinding dataBinding;
    private List<IssueListEntity> list = new ArrayList<>();
    private PlustekIssueListAdapter adapter;
    private PlustekService plustekService = new PlustekService();

    @Override
    public int getLayoutResId() {
        return R.layout.activity_inspe_plustek_issuelist;
    }

    @Override
    public void initUI() {
        setTitle("问题记录", R.drawable.inspe_left_black_24dp);
        dataBinding = (ActivityInspePlustekIssuelistBinding) rootDataBinding;
        adapter = new PlustekIssueListAdapter(R.layout.inspe_plustek_issuelist_item, list);
        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataBinding.recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //修改
                IssueListEntity entity = list.get(position);
                Intent intent = new Intent(context, InspePlustekIssueActivity.class);
                intent.putExtra(InspePlustekIssueActivity.IntentKey.START_MODE, InspePlustekIssueActivity.StartMode.COPY);//设置页面模式
                intent.putExtra("edit_data", list.get(position).resultEntity);//计算可扣分数
                if (entity.names != null && entity.names.length > 1) {
                    intent.putExtra("info_txt", (entity.resultEntity.getDevice_name() + " " + entity.names[0] + "-" + entity.names[1]));
                } else {
                    intent.putExtra("info_txt", entity.resultEntity.getDevice_name());
                }
//                intent.putExtra("task_id", taskId);//任务ID
//                intent.putExtra("device_id", deviceId);//设备ID
//                intent.putExtra("plustek_type", plustekType);
                startActivity(intent);
            }
        });

        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, final int position) {
                new AlertDialog.Builder(context)
                        .setTitle("删除确认").setMessage("您是想要删除该问题记录?\n")
                        .setPositiveButton("删除",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (plustekService.delete(list.get(position).resultEntity)) {
                                            plustekService.delete(list.remove(position));
                                            adapter.notifyDataSetChanged();
                                            showToast("删除成功");
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
                // 显示
                return false;
            }
        });
    }

    private String taskId;
    private String deviceId;

    @Override
    public void initData() {
        taskId = getIntent().getStringExtra("task_id");
        deviceId = getIntent().getStringExtra("device_id");
        if (TextUtils.isEmpty(taskId) || TextUtils.isEmpty(deviceId)) {
            showToast("参数错误！");
            finish();
            return;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        list.clear();
        adapter.notifyDataSetChanged();
        List<TeamRuleResultEntity> listTemp = plustekService.getIssues(taskId, deviceId);
        if (listTemp != null && listTemp.size() > 0) {
            TeamRuleResultEntity resultEntity;
            for (int i = 0, size = listTemp.size(); i < size; i++) {
                resultEntity = listTemp.get(i);
                list.add(new IssueListEntity(resultEntity, plustekService.getLeve1_2Name(resultEntity.getRule_id())));
            }
            adapter.notifyDataSetChanged();
        }
    }

    public class PlustekIssueListAdapter extends BaseQuickAdapter<IssueListEntity, BaseViewHolder> {
        public PlustekIssueListAdapter(int layoutResId, List<IssueListEntity> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, IssueListEntity item) {
            helper.setText(R.id.issueInfoTxt, item.resultEntity.getDescription());//1
            helper.setText(R.id.issueScoreTxt, String.format("-%.2f", item.resultEntity.getDeduct_score()));//扣分
            if (item.names != null && item.names.length > 1) {
                helper.setText(R.id.issueReasonTxt, item.names[0] + " " + item.names[1]);//3
            } else {
                helper.setText(R.id.issueReasonTxt, "");
            }
            helper.setText(R.id.issueNatureTxt, item.resultEntity.getProblem_nature());//问题性质
        }
    }
}
