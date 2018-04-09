package com.cnksi.inspe.ui;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.inspe.R;
import com.cnksi.inspe.adapter.PlustekStandardAdapter;
import com.cnksi.inspe.adapter.TeamRoleAdapter;
import com.cnksi.inspe.adapter.entity.PlustekRule0Entity;
import com.cnksi.inspe.adapter.entity.PlustekRule1Entity;
import com.cnksi.inspe.adapter.entity.TeamRole0Entity;
import com.cnksi.inspe.adapter.entity.TeamRoleEntity;
import com.cnksi.inspe.base.AppBaseActivity;
import com.cnksi.inspe.databinding.ActivityInspePlustekstandardBinding;
import com.cnksi.inspe.db.PlustekService;
import com.cnksi.inspe.db.entity.InspecteTaskEntity;
import com.cnksi.inspe.db.entity.PlusteRuleEntity;
import com.cnksi.inspe.db.entity.TeamRuleEntity;
import com.cnksi.inspe.type.RecordType;

import java.util.ArrayList;
import java.util.List;

/**
 * 精益化评价-显示设备基本信息，及相关评判标准
 *
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/21 09:31
 */
public class InspePlustekStandardActivity extends AppBaseActivity {
    private ActivityInspePlustekstandardBinding dataBinding;
    private PlustekService plustekService = new PlustekService();
    private List<MultiItemEntity> list = new ArrayList<>();
    private PlustekStandardAdapter adapter;

    @Override
    public int getLayoutResId() {
        return R.layout.activity_inspe_plustekstandard;
    }

    @Override
    public void initUI() {
        dataBinding = (ActivityInspePlustekstandardBinding) rootDataBinding;
        setTitle("变压器名称", R.drawable.inspe_left_black_24dp);

        adapter = new PlustekStandardAdapter(list);
        final GridLayoutManager manager = new GridLayoutManager(this, 1);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.getItemViewType(position) == TeamRoleAdapter.TYPE_1 ? 1 : manager.getSpanCount();
            }
        });
        dataBinding.recyclerView.setAdapter(adapter);
        dataBinding.recyclerView.setLayoutManager(manager);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                PlustekRule1Entity data = (PlustekRule1Entity) adapter.getData().get(position);

                PlusteRuleEntity entity = data.rule;

                Intent intent = new Intent(context, InspePlustekIssueActivity.class);
                intent.putExtra("data", entity);//计算可扣分数
                intent.putExtra("task", new InspecteTaskEntity());
                //计算出可以被扣得分值
                intent.putExtra("max_minus", 10f);
                startActivity(intent);

            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        searchData();
    }

    private void searchData() {
        List<PlusteRuleEntity> ruleList = plustekService.getPlusteRule();

        if (ruleList == null) {
            return;
        }

        for (int i = 0, size = ruleList.size(); i < size; i++) {
            PlusteRuleEntity entity = ruleList.get(i);
            PlustekRule0Entity rule0 = new PlustekRule0Entity(entity);

            List<PlusteRuleEntity> ruleItemList = plustekService.getPlusteRule();
            for (PlusteRuleEntity e : ruleItemList) {
                PlustekRule1Entity rule = new PlustekRule1Entity(e);
                rule0.addSubItem(rule);
            }

            list.add(rule0);
        }

        adapter.expandAll();

    }
}
