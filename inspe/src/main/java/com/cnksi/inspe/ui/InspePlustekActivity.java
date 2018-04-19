package com.cnksi.inspe.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.inspe.R;
import com.cnksi.inspe.base.AppBaseActivity;
import com.cnksi.inspe.databinding.ActivityInspePlustekBinding;
import com.cnksi.inspe.db.TaskService;
import com.cnksi.inspe.db.entity.InspecteTaskEntity;
import com.cnksi.inspe.db.entity.TaskExtendEntity;
import com.cnksi.inspe.entity.InspectePlustekEntity;
import com.cnksi.inspe.type.PlustekType;
import com.cnksi.inspe.type.RoleType;
import com.cnksi.inspe.type.TaskProgressType;
import com.cnksi.inspe.type.TaskType;
import com.cnksi.inspe.utils.DateFormat;

import org.xutils.common.util.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * 精益化评价-菜单页面(检查大项)
 * <br/>
 * <ul>
 * <b>精益化评价分类</b>，检查项为固定的3类
 * <li>查阅资料:根据标准查找设备，比较特殊</li>
 * <li>PMS检查:根据设备查找标准，与【全面巡视】类似</li>
 * <li>现场检查:与【全面巡视】类似</li>
 * </ul>
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/21 09:04
 */
public class InspePlustekActivity extends AppBaseActivity implements View.OnClickListener {

    private ActivityInspePlustekBinding dataBinding;
    private List<InspectePlustekEntity> list = new ArrayList<>();
    private InspecteTaskEntity task;
    private Button bottomBtn;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_inspe_plustek;
    }

    @Override
    public void initUI() {
        setTitle(TaskType.jyhjc.getDesc(), R.drawable.inspe_left_black_24dp);
        dataBinding = (ActivityInspePlustekBinding) rootDataBinding;

        list.add(new InspectePlustekEntity(PlustekType.cyzl, R.mipmap.inspe_plustek_1, "查阅资料"));
        list.add(new InspectePlustekEntity(PlustekType.pmsjc, R.mipmap.inspe_plustek_2, "PMS检查"));
        list.add(new InspectePlustekEntity(PlustekType.xcjc, R.mipmap.inspe_plustek_3, "现场检查"));

        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        BaseQuickAdapter adapter = new InspecteTaskAdapter(R.layout.inspeplustek_item, list);
        adapter.openLoadAnimation();

        adapter.setOnItemClickListener((adapter1, view, position) -> {
            Intent intent = new Intent(InspePlustekActivity.this, InspeDeviceActivity.class);
            intent.putExtra("task", task);
            intent.putExtra("plustek_type", list.get(position).type);
            startActivity(intent);
        });

        dataBinding.recyclerView.setAdapter(adapter);

        bottomBtn = (Button) getLayoutInflater().inflate(R.layout.inspe_recycle_buttom_btn, (ViewGroup) dataBinding.recyclerView.getParent(), false);
        bottomBtn.setOnClickListener(this);
        adapter.addFooterView(bottomBtn);
    }

    @Override
    public void initData() {
        task = (InspecteTaskEntity) getIntent().getSerializableExtra("task");
        if (task == null || !TaskType.jyhjc.name().equals(task.getType())) {
            showToast("参数错误");
            finish();
            return;
        }

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.bottomBtn) {
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

    public class InspecteTaskAdapter extends BaseQuickAdapter<InspectePlustekEntity, BaseViewHolder> {
        public InspecteTaskAdapter(int layoutResId, List data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, InspectePlustekEntity item) {
            helper.setImageResource(R.id.icoImg, item.resId);
            helper.setText(R.id.titleTxt, item.title);

        }
    }

    /**
     * 更新任务状态
     */
    private void updateTask() {

        TaskService taskService = new TaskService();
        TaskExtendEntity extendEntity = taskService.getTaskExtend(task.getId(), getUserService().getUserExpert(RoleType.expert).getId());
        extendEntity.setProgress(TaskProgressType.done.name());
        if (taskService.updateTaskExtend(extendEntity)) {
            showToast("保存完成");
            finish();
        } else {
            showToast("保存失败");
        }
    }
}
