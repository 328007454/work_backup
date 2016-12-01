package com.cnksi.sjjc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.WorkPlanAdapter;
import com.cnksi.sjjc.bean.WorkPiao;

import org.xutils.ex.DbException;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

/**
 * Created by han on 2016/5/5.
 */
public class WorkPiaoActivity extends BaseActivity {
    //标题
    @ViewInject(R.id.tv_title)
    private TextView tvTitle;
    @ViewInject(R.id.lv_container)
    private ListView lvContainer;
    private List<WorkPiao> wpList;

    private WorkPlanAdapter wpAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setChildView(R.layout.activity_work_piao);
        initUI();
        initData();
    }

    private void initUI() {
        tvTitle.setText("工作票");
    }

    public void initData() {
        try {
            wpList = db.selector(WorkPiao.class).findAll();
            mHandler.sendEmptyMessage(LOAD_DATA);
        } catch (DbException e) {
            e.printStackTrace();
        }

        lvContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(_this, WorkPlanInformationActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case LOAD_DATA:
                if(wpAdapter==null){
                    wpAdapter = new WorkPlanAdapter(_this,wpList,R.layout.work_piao_list_item);
                }
                lvContainer.setAdapter(wpAdapter);
                break;
            default:
                break;
        }
    }
}
