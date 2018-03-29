package com.cnksi.inspe.ui.fragment;

import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.inspe.BuildConfig;
import com.cnksi.inspe.R;
import com.cnksi.inspe.base.AppBaseFragment;
import com.cnksi.inspe.databinding.FragmentInspeInspectionBinding;
import com.cnksi.inspe.db.TeamService;
import com.cnksi.inspe.db.entity.InspecteTaskEntity;
import com.cnksi.inspe.type.TaskType;
import com.cnksi.inspe.ui.InspePlustekActivity;
import com.cnksi.inspe.ui.InspeTeamActivity;
import com.cnksi.inspe.utils.DateFormat;
import com.cnksi.inspe.utils.StringUtils;

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

    @Override
    protected void lazyLoad() {
        dataBinding = (FragmentInspeInspectionBinding) fragmentDataBinding;


        //模拟数据
        List<InspecteTaskEntity> listTemp = teamService.getTaskList();
        if (listTemp != null && listTemp.size() > 0) {
            list.addAll(listTemp);
        } else if (BuildConfig.DEBUG) {
//            list.add(new InspecteTaskEntity("0", "0", "曹一班", "班组建设检查", "220Kv三班", "", "责任人A, 责任人B", System.currentTimeMillis()));
//            list.add(new InspecteTaskEntity("0", "1", "220V黑", "班组建设检查", "220Kv三班", "", "责任人A, 责任人B", System.currentTimeMillis()));
        }

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
                InspecteTaskEntity task = list.get(position);
                switch (valueOf(task.getType())) {
                    case jxh:
                        intent.setClass(getContext(), InspePlustekActivity.class).putExtra("task", task);
                        break;
                    case bzjs:
                        intent.setClass(getContext(), InspeTeamActivity.class).putExtra("task", task);
                        break;
                    case sbpc:
                    default:
                        showToast("检查类型错误");
                        return;

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


            //精益化检查(显示变电站)
            //班组建设(显示班组)
            //设备排查(显示变电站)
            String teamName = null;
            if ("jxh".equals(item.getType())) {
                teamName = item.getBdz_name();
            } else if ("bzjs".equals(item.getType())) {
                teamName = item.getDept_name();
            } else {
                teamName = item.getBdz_name();
            }

            String firstChar = StringUtils.getFirstChar(teamName);
            helper.setText(R.id.firstNameTxt, firstChar);
            helper.setBackgroundRes(R.id.firstNameTxt, firstBgResIds[firstChar.charAt(0) % firstBgResIds.length]);

            helper.setText(R.id.teamTxt, teamName);

            helper.setText(R.id.typeTxt, item.getCheck_type());
            helper.setText(R.id.dateTxt, "时间：" + DateFormat.formatYMD(DateFormat.dbdateToLong(item.getPlan_check_time())));


            helper.setText(R.id.persionsTxt, item.getCheckuser_name().replace(",", " "));
        }
    }

}
