package com.cnksi.bdzinspection.activity.maintenance;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;

import com.cnksi.bdzinspection.R;
import com.cnksi.common.base.BaseActivity;
import com.cnksi.common.utils.ViewHolder;
import com.cnksi.common.base.BaseAdapter;
import com.cnksi.bdzinspection.daoservice.SafeToolsInfoService;
import com.cnksi.bdzinspection.databinding.XsActivitySafetyBdzListBinding;
import com.cnksi.bdzinspection.model.SafeToolsInfor;
import com.cnksi.common.utils.TTSUtils;
import com.cnksi.common.Config;
import com.cnksi.common.daoservice.BdzService;
import com.cnksi.common.model.Bdz;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * @version 1.0
 * @author wastrel
 * @date 2017/7/7 8:59
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class SafetyBdzListActivity extends BaseActivity {
    XsActivitySafetyBdzListBinding binding;
    String deptId;
    Bdz currentItem;
    List<Bdz> bdzList;
    Map<String, String> count;
    boolean isStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(mActivity, R.layout.xs_activity_safety_bdz_list);
        deptId = getIntent().getStringExtra(Config.CURRENT_DEPARTMENT_ID);
        binding.btnStart.setOnClickListener(v -> {
            if (isStart) {
                return;
            }
            if (currentItem == null) {
                ToastUtils.showMessage("请先选择一个变电站！");
                return;
            }
            TTSUtils.getInstance().startSpeaking("开始" + currentItem.name + "的安全工器具检查作业");
            Intent intent = new Intent(mActivity, SafetyToolsControlActivity.class);
            intent.putExtra(Bdz.BDZID, currentItem.bdzid == null ? "-1" : currentItem.bdzid);
            intent.putExtra(Config.TITLE_NAME_KEY, currentItem.name);
            intent.putExtra(SafeToolsInfor.DEPTID, deptId);
            isStart = true;
            SafetyBdzListActivity.this.startActivity(intent);
        });

        binding.ibtnCancel.setOnClickListener(v -> finish());
        initialData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        reLoadData();
        isStart = false;
    }

    private void reLoadData() {
        ExecutorManager.executeTask(() -> {
            count = SafeToolsInfoService.getInstance().countRemindGroupByBdz(deptId);
            runOnUiThread(() -> binding.list.setAdapter(new BdzAdapter(bdzList, count)));
        });
    }

    private void initialData() {
        if (TextUtils.isEmpty(deptId)) {
            ToastUtils.showMessage( "没有获取到班组信息！");
        } else {
            bdzList = BdzService.getInstance().findAllBdzByDp(deptId);
            if (bdzList == null) {
                bdzList = new ArrayList<>();
            }
            bdzList.add(new Bdz("-1", "班组本部"));
        }
    }


    private class BdzAdapter extends BaseAdapter<Bdz> {
        Map<String, String> countMap;

        public BdzAdapter(Collection<Bdz> data, Map<String, String> count) {
            super(mActivity, data, R.layout.xs_item_safety_bdz);
            this.countMap = count;
        }

        @Override
        public void convert(ViewHolder holder, final Bdz item, int position) {
            String c = countMap.get(item.bdzid);
            holder.setText(R.id.tv_bdz_name, TextUtils.isEmpty(c) ? item.name : StringUtils.formatPartTextColor(item.name + "%s", Color.RED, "(" + c + ")"));
            holder.setImageResource(R.id.img_check, (currentItem != null && currentItem.bdzid == (item.bdzid)) ? R.drawable.xs_icon_select : R.drawable.xs_icon_unselect);
            holder.getRootView().setOnClickListener(v -> {
                currentItem = item;
                BdzAdapter.this.notifyDataSetChanged();
            });
        }
    }
}
