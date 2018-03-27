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
import com.cnksi.inspe.entity.InspecteIssueEntity;
import com.cnksi.inspe.ui.InspeIssueDetailActivity;
import com.cnksi.inspe.utils.DateFormat;

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

    FragmentInspeMyissueBinding dataDinding;
    List<InspecteIssueEntity> list = new ArrayList<>();

    @Override
    protected void lazyLoad() {
        dataDinding = (FragmentInspeMyissueBinding) fragmentDataBinding;
        dataDinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //模拟数据
        list.add(new InspecteIssueEntity("1", "", "未按照发布单位有效技术b标准、规章制度清册的。", 0, "未整改", System.currentTimeMillis()));
        list.add(new InspecteIssueEntity("1", "", "未按照发布单位有效技术b标准、规章制度清册的。", 0, "未整改", System.currentTimeMillis()));
        list.add(new InspecteIssueEntity("1", "", "未按照发布单位有效技术b标准、规章制度清册的。", 0, "未整改", System.currentTimeMillis()));
        list.add(new InspecteIssueEntity("1", "", "未按照发布单位有效技术b标准、规章制度清册的。", 0, "未整改", System.currentTimeMillis()));


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


    public class InspeIssueAdapter extends BaseQuickAdapter<InspecteIssueEntity, BaseViewHolder> {
        public InspeIssueAdapter(int layoutResId, List data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, InspecteIssueEntity item) {


            helper.setText(R.id.contextTxt, item.context);
            helper.setText(R.id.dateTxt, DateFormat.formatYMD(item.date));
            helper.setText(R.id.stateTxt, item.stateName);
        }
    }
}
