package com.cnksi.inspe.ui.fragment;

import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.inspe.R;
import com.cnksi.inspe.base.AppBaseFragment;
import com.cnksi.inspe.databinding.FragmentInspeInspectionBinding;
import com.cnksi.inspe.entity.InspecteTaskEntity;
import com.cnksi.inspe.ui.InspePlustekActivity;
import com.cnksi.inspe.ui.InspeTeamActivity;
import com.cnksi.inspe.utils.DateFormat;
import com.cnksi.inspe.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Override
    protected void lazyLoad() {
        dataBinding = (FragmentInspeInspectionBinding) fragmentDataBinding;



        //模拟数据
        list.add(new InspecteTaskEntity("0", "0", "曹溪运维班", 0, "班组建设检查", System.currentTimeMillis(), new String[]{"责任人A", "责任人B"}));
        list.add(new InspecteTaskEntity("1", "1", "220Kv三班", 1, "精益化检查", System.currentTimeMillis(), new String[]{"责任人A", "责任人B"}));
        list.add(new InspecteTaskEntity("0", "0", "220Kv曹班", 0, "班组建设检查", System.currentTimeMillis(), new String[]{"责任人A", "责任人B"}));
        list.add(new InspecteTaskEntity("1", "1", "田园班", 1, "精益化检查", System.currentTimeMillis(), new String[]{"责任人A", "责任人B"}));
        list.add(new InspecteTaskEntity("0", "0", "220kv黑班", 0, "班组建设检查", System.currentTimeMillis(), new String[]{"责任人A", "责任人B"}));
        list.add(new InspecteTaskEntity("1", "1", "清班", 1, "精益化检查", System.currentTimeMillis(), new String[]{"责任人A", "责任人B"}));
        list.add(new InspecteTaskEntity("0", "0", "曹班", 0, "班组建设检查", System.currentTimeMillis(), new String[]{"责任人A", "责任人B"}));
        list.add(new InspecteTaskEntity("1", "1", "田班", 1, "精益化检查", System.currentTimeMillis(), new String[]{"责任人A", "责任人B"}));

        dataBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //init adapter
        BaseQuickAdapter adapter = new InspecteTaskAdapter(R.layout.inspetask_item, list);
        adapter.openLoadAnimation();
//        View top = getLayoutInflater().inflate(R.layout.top_view, (ViewGroup) mRecyclerView.getParent(), false);
//        homeAdapter.addHeaderView(top);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent();
                if (list.get(position).taskType == 0) {
                    intent.setClass(getContext(), InspeTeamActivity.class);
                } else {
                    intent.setClass(getContext(), InspePlustekActivity.class);
                }
                startActivity(intent);
            }
        });

        dataBinding.recyclerView.setAdapter(adapter);
        //分割线
//        dataBinding.recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
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


            helper.setText(R.id.teamTxt, item.teamName);
            helper.setText(R.id.typeTxt, item.taskTypeName);
            helper.setText(R.id.dateTxt, DateFormat.formatYMD(item.targetDate));
            String firstChar = StringUtils.getFirstChar(item.teamName);
            helper.setText(R.id.firstNameTxt, firstChar);
            helper.setBackgroundRes(R.id.firstNameTxt, firstBgResIds[firstChar.charAt(0) % firstBgResIds.length]);

            helper.setText(R.id.persionsTxt, item.toExecutorsString());
        }
    }

}
