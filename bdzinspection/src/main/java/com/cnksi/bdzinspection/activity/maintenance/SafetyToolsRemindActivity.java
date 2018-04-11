package com.cnksi.bdzinspection.activity.maintenance;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ExpandableListView;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.activity.BaseActivity;
import com.cnksi.bdzinspection.adapter.base.BaseExpandListAdapter;
import com.cnksi.bdzinspection.adapter.DataWrap;
import com.cnksi.bdzinspection.daoservice.SafeToolsInfoService;
import com.cnksi.bdzinspection.databinding.XsActivitySafetyToolsRemindBinding;
import com.cnksi.bdzinspection.databinding.XsItemSafeToolsinfoRemindBinding;
import com.cnksi.bdzinspection.databinding.XsItemSafetyToolBdzBinding;
import com.cnksi.bdzinspection.model.Bdz;
import com.cnksi.bdzinspection.model.SafeToolsInfor;
import com.cnksi.bdzinspection.utils.Config;
import com.cnksi.bdzinspection.view.keyboard.QWERKeyBoardUtils;
import com.cnksi.xscore.xsutils.CToast;
import com.cnksi.xscore.xsutils.DateUtils;
import com.cnksi.xscore.xsutils.StringUtils;
import com.lidroid.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安全工器具提醒
 * Created by wastrel on 2017/6/28.
 */

public class SafetyToolsRemindActivity extends BaseActivity {


    private XsActivitySafetyToolsRemindBinding binding;
    private SafetyToolAdapter adapter;

    private List<DataWrap<Bdz, DbModel>> dataWraps = new ArrayList<>();

    private String dept_id;
    private QWERKeyBoardUtils qwerKeyBoardUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(currentActivity, R.layout.xs_activity_safety_tools_remind);
        initUI();
    }

    public void initUI() {
        getIntentValue();
        dept_id = getIntent().getStringExtra(Config.CURRENT_DEPARTMENT_ID);
        binding.lvTools.setAdapter(adapter = new SafetyToolAdapter());
        binding.ibtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.lvTools.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                DbModel model = adapter.getChild(groupPosition, childPosition);
                Intent intent = new Intent(currentActivity, SafeToolsInformationActivity.class);
                intent.putExtra(SafeToolsInfor.ID, model.getString(SafeToolsInfor.ID));
                intent.putExtra(Bdz.BDZID, model.getString("bdz_id"));
                String title = model.getString("name");
                intent.putExtra("title", title);
                intent.putExtra(SafeToolsInfor.DEPTID, dept_id);
                startActivity(intent);
                return false;
            }
        });
        binding.lvTools.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Bdz bdz = adapter.getGroup(groupPosition);
                Intent intent = new Intent(currentActivity, SafetyToolsControlActivity.class);
                intent.putExtra(Bdz.BDZID, bdz.bdzid);
                intent.putExtra(SafeToolsInfor.DEPTID, dept_id);
                startActivity(intent);
                return true;
            }
        });
        qwerKeyBoardUtils = new QWERKeyBoardUtils(currentActivity);
        qwerKeyBoardUtils.init(binding.keyboardContainer,
                new QWERKeyBoardUtils.keyWordChangeListener() {
                    @Override
                    public void onChange(View view, String oldKey, String newKey) {
                        adapter.search(newKey);
                    }
                });
    }


    private void initData() {
        if (dept_id == null) {
            CToast.showShort(currentActivity, "获取班组信息错误！");
            return;
        }
        dataWraps.clear();
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<DbModel> allTools = SafeToolsInfoService.getInstance().findAllTools(dept_id);
                if (allTools.size() > 0) {
                    //根据bdz_id归类。
                    Map<String, List<DbModel>> tempMap = new HashMap<>(32);
                    List<DbModel> tempList;
                    for (DbModel tool : allTools) {
                        String bdzId = tool.getString("bdz_id");
                        //没有BdzId表明其存放在班组本部
                        if (bdzId == null || bdzId.isEmpty()) bdzId = "-1";
                        if ((tempList = tempMap.get(bdzId)) == null) {
                            tempList = new ArrayList<>();
                            tempMap.put(bdzId, tempList);
                        }
                        tempList.add(tool);
                    }
                    for (Map.Entry<String, List<DbModel>> entry : tempMap.entrySet()) {
                        String key = entry.getKey();
                        Bdz bdz = new Bdz(key, key.equals("-1") ? "班组本部" : entry.getValue().get(0).getString("bdz_name"));
                        DataWrap<Bdz, DbModel> dataWrap = new DataWrap<>(bdz);
                        dataWrap.setChildList(entry.getValue());
                        dataWraps.add(dataWrap);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.searchKey = "";
                        qwerKeyBoardUtils.setKeyWord("");
                        adapter.setList(dataWraps);
                    }
                });
            }
        });
    }


    private class SafetyToolAdapter extends BaseExpandListAdapter<Bdz, DbModel> {

        private String searchKey = "";

        public SafetyToolAdapter() {
            super(currentActivity, null, R.layout.xs_item_safety_tool_bdz, R.layout.xs_item_safe_toolsinfo_remind);
        }

        @Override
        public void bindGroupView(ViewDataBinding binding, Bdz item, boolean isExpand, int groupPosition) {
            XsItemSafetyToolBdzBinding bdzBinding = (XsItemSafetyToolBdzBinding) binding;
            bdzBinding.tvBdzName.setText(StringUtils.formatPartTextColor(item.name + "(%s)", Color.RED, String.valueOf(getChildrenCount(groupPosition))));
        }

        @Override
        public void bindChildView(ViewDataBinding binding, DbModel item, int groupPosition, int childPosition, boolean isLastChild) {
            XsItemSafeToolsinfoRemindBinding remindBinding = (XsItemSafeToolsinfoRemindBinding) binding;
            remindBinding.tvNumber.setText(String.valueOf(childPosition + 1));
            remindBinding.tvToolName.setText(item.getString(SafeToolsInfor.NAME));
            String num = item.getString(SafeToolsInfor.NUM);
            remindBinding.tvSerial.setText("编号：" + ("-1".equals(num) ? "" : num));
            String date = DateUtils.formatDateTime(item.getString(SafeToolsInfor.NEXTCHECKTIME), "yyyy/MM/dd");
            remindBinding.tvDate.setText("下次试验时间：" + (TextUtils.isEmpty(date) ? "未知" : date));
            if (isLastChild) remindBinding.llRoot.setDrawUnderLine(false);
        }

        private void search(String keyWord) {
            if (searchKey.equals(keyWord)) return;
            List<DataWrap<Bdz, DbModel>> result;
            if (TextUtils.isEmpty(keyWord)) {
                result = dataWraps;
            } else {
                List<DataWrap<Bdz, DbModel>> searchSet = keyWord.startsWith(searchKey) ? dataList : dataWraps;
                result = new ArrayList<>();
                for (DataWrap<Bdz, DbModel> dataWrap : searchSet) {
                    List<DbModel> temp = new ArrayList<>();
                    for (DbModel model : dataWrap.getChildList()) {
                        if (model.getString(SafeToolsInfor.PINYIN).contains(keyWord) || StringUtils.nullTo(model.getString(SafeToolsInfor.NUM),"").contains(keyWord)) {
                            temp.add(model);
                        }
                    }
                    if (temp.size() > 0) {
                        result.add(new DataWrap<>(dataWrap.getObj(), temp));
                    }
                }
            }
            searchKey = keyWord;
            setList(result);
        }

        @Override
        public void setList(List<DataWrap<Bdz, DbModel>> dataList) {
            super.setList(dataList);
            for (int i = 0; i < adapter.getGroupCount(); i++) {
                binding.lvTools.expandGroup(i);
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }
}
