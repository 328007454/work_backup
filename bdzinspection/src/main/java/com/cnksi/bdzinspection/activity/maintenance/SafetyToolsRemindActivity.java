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
import com.cnksi.bdzinspection.daoservice.SafeToolsInfoService;
import com.cnksi.bdzinspection.databinding.XsActivitySafetyToolsRemindBinding;
import com.cnksi.bdzinspection.databinding.XsItemSafeToolsinfoRemindBinding;
import com.cnksi.bdzinspection.databinding.XsItemSafetyToolBdzBinding;
import com.cnksi.bdzinspection.model.SafeToolsInfor;
import com.cnksi.common.Config;
import com.cnksi.common.model.Bdz;
import com.cnksi.common.model.vo.DataWrap;
import com.cnksi.common.utils.QWERKeyBoardUtils;
import com.cnksi.common.utils.StringUtilsExt;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.utils.ToastUtils;

import org.xutils.db.table.DbModel;

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
        initialUI();
    }

    public void initialUI() {
        getIntentValue();
        dept_id = getIntent().getStringExtra(Config.CURRENT_DEPARTMENT_ID);
        binding.lvTools.setAdapter(adapter = new SafetyToolAdapter());
        binding.ibtnCancel.setOnClickListener(v -> SafetyToolsRemindActivity.this.finish());
        binding.lvTools.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            DbModel model = adapter.getChild(groupPosition, childPosition);
            Intent intent = new Intent(currentActivity, SafeToolsInformationActivity.class);
            intent.putExtra(SafeToolsInfor.ID, model.getString(SafeToolsInfor.ID));
            intent.putExtra(Bdz.BDZID, model.getString("bdz_id"));
            String title = model.getString("name");
            intent.putExtra("title", title);
            intent.putExtra(SafeToolsInfor.DEPTID, dept_id);
            SafetyToolsRemindActivity.this.startActivity(intent);
            return false;
        });
        binding.lvTools.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            Bdz bdz = adapter.getGroup(groupPosition);
            Intent intent = new Intent(currentActivity, SafetyToolsControlActivity.class);
            intent.putExtra(Bdz.BDZID, bdz.bdzid);
            intent.putExtra(SafeToolsInfor.DEPTID, dept_id);
            SafetyToolsRemindActivity.this.startActivity(intent);
            return true;
        });
        qwerKeyBoardUtils = new QWERKeyBoardUtils(currentActivity);
        qwerKeyBoardUtils.init(binding.keyboardContainer,
                (view, oldKey, newKey) -> adapter.search(newKey));
    }


    private void initialData() {
        if (dept_id == null) {
            ToastUtils.showMessage( "获取班组信息错误！");
            return;
        }
        dataWraps.clear();
        ExecutorManager.executeTask(() -> {
            List<DbModel> allTools = SafeToolsInfoService.getInstance().findAllTools(dept_id);
            if (allTools.size() > 0) {
                //根据bdz_id归类。
                Map<String, List<DbModel>> tempMap = new HashMap<>(32);
                List<DbModel> tempList;
                for (DbModel tool : allTools) {
                    String bdzId = tool.getString("bdz_id");
                    //没有BdzId表明其存放在班组本部
                    if (bdzId == null || bdzId.isEmpty()) {
                        bdzId = "-1";
                    }
                    if ((tempList = tempMap.get(bdzId)) == null) {
                        tempList = new ArrayList<>();
                        tempMap.put(bdzId, tempList);
                    }
                    tempList.add(tool);
                }
                for (Map.Entry<String, List<DbModel>> entry : tempMap.entrySet()) {
                    String key = entry.getKey();
                    Bdz bdz = new Bdz(key, "-1".equals(key) ? "班组本部" : entry.getValue().get(0).getString("bdz_name"));
                    DataWrap<Bdz, DbModel> dataWrap = new DataWrap<>(bdz);
                    dataWrap.setChildList(entry.getValue());
                    dataWraps.add(dataWrap);
                }
            }
            SafetyToolsRemindActivity.this.runOnUiThread(() -> {
                adapter.searchKey = "";
                qwerKeyBoardUtils.setKeyWord("");
                adapter.setList(dataWraps);
            });
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
            String date = DateUtils.getFormatterTime(item.getString(SafeToolsInfor.NEXTCHECKTIME), "yyyy/MM/dd");
            remindBinding.tvDate.setText("下次试验时间：" + (TextUtils.isEmpty(date) ? "未知" : date));
            if (isLastChild) {
                remindBinding.llRoot.setDrawUnderLine(false);
            }
        }

        private void search(String keyWord) {
            if (searchKey.equals(keyWord)) {
                return;
            }
            List<DataWrap<Bdz, DbModel>> result;
            if (TextUtils.isEmpty(keyWord)) {
                result = dataWraps;
            } else {
                List<DataWrap<Bdz, DbModel>> searchSet = keyWord.startsWith(searchKey) ? dataList : dataWraps;
                result = new ArrayList<>();
                for (DataWrap<Bdz, DbModel> dataWrap : searchSet) {
                    List<DbModel> temp = new ArrayList<>();
                    for (DbModel model : dataWrap.getChildList()) {
                        if (model.getString(SafeToolsInfor.PINYIN).contains(keyWord) || StringUtilsExt.nullTo(model.getString(SafeToolsInfor.NUM),"").contains(keyWord)) {
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
        initialData();
    }
}
