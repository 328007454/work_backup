package com.cnksi.sjjc.activity;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.cnksi.common.daoservice.BdzService;
import com.cnksi.common.daoservice.DefectRecordService;
import com.cnksi.common.model.Bdz;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DisplayUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.DefectContentAdapter;
import com.cnksi.sjjc.adapter.DefectTypeAdapter;
import com.cnksi.sjjc.adapter.DialogBDZAdapter;
import com.cnksi.sjjc.adapter.ViewHolder;
import com.cnksi.sjjc.databinding.ActivityDefectControlBinding;
import com.cnksi.sjjc.inter.ItemClickListener;
import com.cnksi.sjjc.util.DialogUtils;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by han on 2017/4/25.
 */

public class DefectControlActivity extends BaseActivity {
    private ActivityDefectControlBinding defectControlBinding;
    private List<Bdz> bdzList = new ArrayList<>();
    private DefectContentAdapter defectContentAdapter;
    //变电站弹出对话框
    private Dialog mPowerStationDialog = null;
    private Bdz currentBdz;
    private int defectLevel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defectControlBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_defect_control, null, false);
        setChildView(defectControlBinding.getRoot());
        initView();
        loadData();
    }

    @Override
    public void initUI() {

    }

    @Override
    public void initData() {

    }

    private void search() {
        ExecutorManager.executeTaskSerially(() -> {
            final List<DefectRecord> defectRecords = DefectRecordService.getInstance().queryCurrentBdzExistDefectList(currentBdz == null ? "" : currentBdz.bdzid, defectLevel);
            runOnUiThread(() -> defectContentAdapter.setList(defectRecords));
        });
    }
    public void initView() {

        defectControlBinding.setEvent(this);
        mTitleBinding.tvTitle.setText("缺陷管理");
        mTitleBinding.btnBack.setImageResource(R.drawable.ic_hompage_selector);
        mTitleBinding.btnBack.setVisibility(View.GONE);
        mTitleBinding.btnBackDefect.setVisibility(View.VISIBLE);
        mTitleBinding.btnBackDefect.setOnClickListener((View.OnClickListener) view -> onBackPressed());
        defectControlBinding.defectType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                defectLevel = i * 2;
                search();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                defectLevel = 0;
                search();
            }
        });
    }

    public void loadData() {
        defectControlBinding.defectType.setAdapter(
                new DefectTypeAdapter(this,
                        Arrays.asList(getResources().getStringArray(R.array.DefectTypeArray)),
                        R.layout.adapter_defect_type));
        ExecutorManager.executeTaskSerially(() -> {
            try {
                bdzList = BdzService.getInstance().findAll();
            } catch (DbException e) {
                e.printStackTrace();
            }

            mHandler.post(() -> {
                initBDZDialog();
                if (bdzList.size() > 0) {
                    currentBdz = bdzList.get(0);
                    defectControlBinding.bdzName.setText(currentBdz.name);
                }
                search();
            });
        });
        defectContentAdapter = new DefectContentAdapter(this, null);
        defectControlBinding.lvDefect.setAdapter(defectContentAdapter);

    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bdz_contanier:
                mPowerStationDialog.show();
                break;
            default:
                break;
        }
    }

    private void initBDZDialog() {
        int dialogWidth = DisplayUtils.getInstance().getWidth() * 9 / 10;
        int dialogHeight = bdzList.size() > 8 ? DisplayUtils.getInstance().getHeight() * 3 / 5 : LinearLayout.LayoutParams.WRAP_CONTENT;
        final ViewHolder holder = new ViewHolder(this, null, R.layout.content_list_dialog, false);
        AutoUtils.autoSize(holder.getRootView());
        ListView listView = holder.getView(R.id.lv_container);
        holder.setText(R.id.tv_dialog_title, getString(R.string.please_select_power_station_str));
        DialogBDZAdapter adapter = new DialogBDZAdapter(this, bdzList, R.layout.dialog_content_child_item);
        adapter.setItemClickListener(new ItemClickListener<Bdz>() {
            @Override
            public void itemClick(View v, Bdz bdz, int position) {
                if (!bdz.name.contains("未激活")) {
                    currentBdz = bdz;
                    search();
                    defectControlBinding.bdzName.setText(bdz.name);
                    mPowerStationDialog.dismiss();
                } else {
                    ToastUtils.showMessage("该变电站未激活");
                }
            }

            @Override
            public void itemLongClick(View v, Bdz bdz, int position) {

            }
        });
        listView.setAdapter(adapter);
        mPowerStationDialog = DialogUtils.createDialog(this, holder, dialogWidth, dialogHeight, true);
    }

}
