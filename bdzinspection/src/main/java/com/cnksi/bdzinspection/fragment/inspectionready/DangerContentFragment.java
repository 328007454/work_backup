package com.cnksi.bdzinspection.fragment.inspectionready;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.adapter.DangerPointAdapter;
import com.cnksi.bdzinspection.application.XunshiApplication;
import com.cnksi.bdzinspection.databinding.XsFragmentListBinding;
import com.cnksi.bdzinspection.fragment.BaseFragment;
import com.cnksi.bdzinspection.model.Dangpoint;
import com.zhy.core.utils.AutoUtils;

import org.xutils.db.DbModelSelector;
import org.xutils.db.Selector;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * 包括 危险点 Fragment 工器具Fragment
 *
 * @author terry
 */
public class DangerContentFragment extends BaseFragment {


    private DangerPointAdapter mDangerPointAdapter = null;

    private List<Dangpoint> mDangerPointList = new ArrayList<Dangpoint>();
    private XsFragmentListBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = XsFragmentListBinding.inflate(getActivity().getLayoutInflater());
        AutoUtils.autoSize(binding.getRoot());
        initUI();
        lazyLoad();
        return binding.getRoot();
    }

    private void initUI() {
        getBundleValue();
        isPrepared = true;
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                if (mDangerPointAdapter == null) {
                    mDangerPointAdapter = new DangerPointAdapter(currentActivity, mDangerPointList);
                    binding.lvContainer.setAdapter(mDangerPointAdapter);
                } else {
                    mDangerPointAdapter.setList(mDangerPointList);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void lazyLoad() {
        if (isPrepared && isVisible && isFirstLoad) {
            mFixedThreadPoolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    searchData();
                }
            });
            isFirstLoad = false;
        }
    }

    /**
     * 查询数据
     */
    private void searchData() {
        try {
            DbModelSelector avoidSelector =XunshiApplication.getDbUtils().selector(Dangpoint.class).groupBy(Dangpoint.AVOID_TYPE).orderBy(Dangpoint.SORT, false);
            List<DbModel> dangPoints = avoidSelector.findAll();
            for (DbModel dbModel : dangPoints) {
                Selector selector = XunshiApplication.getDbUtils().selector(Dangpoint.class).where(Dangpoint.AVOID_TYPE, "=", dbModel.getString("avoid_type"));
                //添加行头
                List<Dangpoint> points = selector.findAll();
                mDangerPointList.add(new Dangpoint(dbModel.getString("avoid_type")));
                mDangerPointList.addAll(points);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        mHandler.sendEmptyMessage(LOAD_DATA);
    }


}
