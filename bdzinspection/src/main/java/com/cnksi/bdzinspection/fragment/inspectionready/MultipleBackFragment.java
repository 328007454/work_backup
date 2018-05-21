package com.cnksi.bdzinspection.fragment.inspectionready;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.MultipleBackAdapter;
import com.cnksi.bdzinspection.daoservice.ZzhtService;
import com.cnksi.bdzinspection.databinding.XsFragmentMultipleBackBinding;
import com.cnksi.bdzinspection.fragment.BaseFragment;
import com.cnksi.bdzinspection.inter.ItemClickListener;
import com.cnksi.bdzinspection.model.zzht.ZzhtResult;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 综合后台数据
 * Created by kkk on 2018/1/31.
 */

public class MultipleBackFragment extends BaseFragment implements ItemClickListener {
    XsFragmentMultipleBackBinding mBackBinding;
    MultipleBackAdapter mAdapter;
    List<Object> mObjects = new ArrayList<>();
    private List<DbModel> mBackDataList = new ArrayList<>();
    private Map<String, List<DbModel>> zzhtInfoMap = new HashMap<>();
    List<DbModel> zzhtModels;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBackBinding = XsFragmentMultipleBackBinding.inflate(LayoutInflater.from(container.getContext()));
        initUI();
        initData();
        return mBackBinding.getRoot();
    }

    private void initUI() {
        getBundleValue();
        mAdapter = new MultipleBackAdapter(R.layout.xs_item_multiple_back, mBackDataList);
        mAdapter.setClickListener(this);
        mBackBinding.recMultipleback.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mBackBinding.recMultipleback.setAdapter(mAdapter);
    }

    private void initData() {
        zzhtModels = ZzhtService.getService().getAllZzhtData(currentBdzId, currentReportId);
        for (DbModel model : zzhtModels) {
            if (TextUtils.isEmpty(model.getString("pid"))) {
                mBackDataList.add(model);
                List<DbModel> models = new ArrayList<>();
                zzhtInfoMap.put(model.getString("id"), models);
            } else {
                zzhtInfoMap.get(model.getString("pid")).add(model);
            }
        }
        mAdapter.setInforMap(zzhtInfoMap);
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    public void onItemClick(View v, Object o, int position) {
        if (!mObjects.contains(o))
            mObjects.add(o);
    }

    public boolean getCheckAll() {
        for (DbModel model : mBackDataList) {
            if (TextUtils.isEmpty(model.getString(ZzhtResult.CONFIRM_TIME))) return false;
        }
        saveData();
        return true;
    }

    public  void saveData() {
        List<ZzhtResult> modelList = new ArrayList<>();
        for (DbModel model : zzhtModels) {
            ZzhtResult result = new ZzhtResult(model, currentReportId);
            modelList.add(result);
        }
        try {
            ZzhtService.getService().saveOrUpdate(modelList);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
