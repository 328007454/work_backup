package com.cnksi.inspe.ui;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.inspe.R;
import com.cnksi.inspe.base.AppBaseActivity;
import com.cnksi.inspe.databinding.ActivityInspePlustekBinding;
import com.cnksi.inspe.db.entity.InspecteTaskEntity;
import com.cnksi.inspe.entity.InspectePlustekEntity;
import com.cnksi.inspe.type.PlustekType;
import com.cnksi.inspe.type.TaskType;

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
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/21 09:04
 */
public class InspePlustekActivity extends AppBaseActivity {

    private ActivityInspePlustekBinding dataBinding;
    private List<InspectePlustekEntity> list = new ArrayList<>();
    private InspecteTaskEntity task;

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

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(InspePlustekActivity.this, InspePlustekDevicesActivity.class);
                intent.putExtra("task", task);
                intent.putExtra("type", list.get(position));
                startActivity(intent);
            }
        });

        dataBinding.recyclerView.setAdapter(adapter);
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
}
