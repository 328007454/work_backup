package com.cnksi.bdzinspection.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.czp.OperateTaskListActivity;
import com.cnksi.bdzinspection.czp.adapter.OperateTaskListAdapter;
import com.cnksi.bdzinspection.daoservice.OperateTicketService;
import com.cnksi.bdzinspection.databinding.XsFragmentListBinding;
import com.cnksi.bdzinspection.model.OperateTick;
import com.cnksi.core.common.ExecutorManager;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.List;

import static com.cnksi.common.Config.LOAD_DATA;

/**
 * 巡检任务提醒
 *
 * @author terry
 */
public class OperateTaskListFragment extends BaseFragment {

    private OperateTaskListAdapter mAdapter;
    private List<DbModel> mDataList;

    public static OperateTaskListFragment getInstance(Bundle bundle) {
        OperateTaskListFragment fragment = new OperateTaskListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private XsFragmentListBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = XsFragmentListBinding.inflate(inflater);

        initialUI();

        lazyLoad();
        initOnClick();

        return binding.getRoot();
    }


    private void initialUI() {
        getBundleValue();
        // 设置类型

        isPrepared = true;
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                if (mAdapter == null) {
                    mAdapter = new OperateTaskListAdapter(currentActivity, mDataList);
                    binding.lvContainer.setAdapter(mAdapter);
                } else {
                    mAdapter.setList(mDataList);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void lazyLoad() {
        if (isPrepared && isVisible && isFirstLoad) {
            // 填充各控件的数据
            searchData();
            isFirstLoad = false;
        }
    }

    public void setIsFirstLoad(boolean isFirstLoad) {
        this.isFirstLoad = isFirstLoad;
    }

    /**
     * 查询数据
     */
    public void searchData() {
        ExecutorManager.executeTask(new Runnable() {

            @Override
            public void run() {
                mDataList = OperateTicketService.getInstance().getAllOperateTaskListByModel(currentFunctionModel);
                mHandler.sendEmptyMessage(LOAD_DATA);
            }
        });
    }

    private void initOnClick() {
        binding.lvContainer.setOnItemClickListener((parent,view,position,id) ->{
            DbModel item = (DbModel) parent.getItemAtPosition(position);
            ((OperateTaskListActivity) currentActivity).startOperateTask(item);
        });
        binding.lvContainer.setOnItemLongClickListener((parent,view,position,id) -> {
            DbModel item = (DbModel) parent.getItemAtPosition(position);
            try {
                OperateTicketService.getInstance().logicDeleteById( item.getString(OperateTick.ID));
            } catch (DbException e) {
                e.printStackTrace();
            }
            ((OperateTaskListActivity) currentActivity).onActivityResult(OperateTaskListActivity.START_OPERATE_CODE, Activity.RESULT_OK, null);
            return true;
        });
    }


}
