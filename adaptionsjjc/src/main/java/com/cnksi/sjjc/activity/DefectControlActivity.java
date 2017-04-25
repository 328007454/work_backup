package com.cnksi.sjjc.activity;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.cnksi.core.adapter.ViewHolder;
import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.DisplayUtil;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.BdzAdapter;
import com.cnksi.sjjc.adapter.DefectContentAdapter;
import com.cnksi.sjjc.adapter.DefectTypeAdapter;
import com.cnksi.sjjc.adapter.DialogBDZAdapter;
import com.cnksi.sjjc.bean.Bdz;
import com.cnksi.sjjc.databinding.ActivityDefectControlBinding;
import com.cnksi.sjjc.databinding.BdzPopwindowBinding;
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
    private List<String> defectTypeList;
    private List<Bdz> bdzList = new ArrayList<>();
    private DefectContentAdapter defectContentAdapter;
    //变电站listview
    private ListView mPowerStationListView;
    //变电站弹出对话框
    private Dialog mPowerStationDialog = null;
    //变电站适配器
    private BdzAdapter bdzAdapter;
    //变电站弹出popwindow
    private BdzPopwindowBinding bdzPopwindowBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defectControlBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_defect_control, null, false);
        setChildView(defectControlBinding.getRoot());
        initUI();
        initData();

    }

    private void initUI() {
        defectControlBinding.setEvent(this);
        tvTitle.setText("缺陷管理");
        btnBack.setImageResource(R.drawable.ic_hompage_selector);
        bdzPopwindowBinding = BdzPopwindowBinding.inflate(getLayoutInflater(), null, false);

        //TODO:需要对该adapter填充数据
        defectContentAdapter = new DefectContentAdapter(_this, new ArrayList(), R.layout.adapter_defect_item);

        defectControlBinding.defectType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // TODO:
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void initData() {
        defectTypeList = Arrays.asList(getResources().getStringArray(R.array.DefectTypeArray));
        defectControlBinding.defectType.setAdapter(new DefectTypeAdapter(this, defectTypeList, R.layout.adapter_defect_type));
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    bdzList = CustomApplication.getDbManager().findAll(Bdz.class);
                } catch (DbException e) {
                    e.printStackTrace();
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        initBDZDialog();
                        if (bdzAdapter == null) {
                            bdzAdapter = new BdzAdapter(_this, bdzList, R.layout.dialog_content_child_item);
                            bdzPopwindowBinding.lvBzd.setAdapter(bdzAdapter);
                        } else
                            bdzAdapter.setList(bdzList);
                        defectControlBinding.bdzName.setText(bdzList.isEmpty() ? "" : bdzList.get(0).name);
                    }
                });
            }
        });


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
        int dialogWidth = DisplayUtil.getInstance().getWidth() * 9 / 10;
        int dialogHeight = bdzList.size() > 8 ? DisplayUtil.getInstance().getHeight() * 3 / 5 : LinearLayout.LayoutParams.WRAP_CONTENT;
        final ViewHolder holder = new ViewHolder(this, null, R.layout.content_list_dialog, false);
        AutoUtils.autoSize(holder.getRootView());
        mPowerStationListView = holder.getView(R.id.lv_container);
        holder.setText(R.id.tv_dialog_title, getString(R.string.please_select_power_station_str));
        DialogBDZAdapter adapter = new DialogBDZAdapter(this, bdzList, R.layout.dialog_content_child_item);
        adapter.setItemClickListener(new ItemClickListener<Bdz>() {
            @Override
            public void itemClick(View v, Bdz bdz, int position) {
                if (!bdz.name.contains("未激活")) {
                    defectControlBinding.bdzName.setText(bdz.name);
                    mPowerStationDialog.dismiss();
                } else
                    CToast.showShort(_this, "该变电站未激活");
            }

            @Override
            public void itemLongClick(View v, Bdz bdz, int position) {

            }
        });
        mPowerStationListView.setAdapter(adapter);
        mPowerStationDialog = DialogUtils.createDialog(this, holder, dialogWidth, dialogHeight, true);
    }

}
