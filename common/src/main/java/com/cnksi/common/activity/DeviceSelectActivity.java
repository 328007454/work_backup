package com.cnksi.common.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cnksi.common.Config;
import com.cnksi.common.R;
import com.cnksi.common.base.BaseAdapter;
import com.cnksi.common.base.BaseTitleActivity;
import com.cnksi.common.base.FragmentPagerAdapter;
import com.cnksi.common.daoservice.DeviceService;
import com.cnksi.common.daoservice.StandardSpecialService;
import com.cnksi.common.databinding.CommonActivityDeviceSelectBinding;
import com.cnksi.common.enmu.PMSDeviceType;
import com.cnksi.common.fragment.DeviceSelectFragment;
import com.cnksi.common.model.StandardSpecial;
import com.cnksi.common.utils.ViewHolder;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.ToastUtils;

import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Wastrel
 * @version 1.0
 * @date 2018/5/30 10:15
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class DeviceSelectActivity extends BaseTitleActivity {

    public final static String FILTER_SQL_KEY = "filter_sql_key";
    public final static String DEVICE_BIG_TYPE_KEY = "device_big_type_key";
    public final static String PMS_DEVICE_TYPE_KEY = "pms_device_type_key";
    public final static String IS_MULTI_SELECT_KEY = "is_multi_select_key";
    public final static String IS_ALLOW_SELECT_SPACE_KEY = "is_allow_select_space_key";

    public final static String RESULT_SELECT_KEY = "result_device";
    public final static String RESULT_SELECT_TYPE_KEY = "result_device_type";

    public final static String SELECT_SPACE_TYPE = "space";
    public final static String SELECT_DEVICE_TYPE = "device";
    public final static String SELECT_DEVICES_TYPE = "devices";


    SelectConfig selectConfig = new SelectConfig();

    CommonActivityDeviceSelectBinding binding;
    List<DeviceSelectFragment> fragments;

    @Override
    protected View getChildContentView() {
        binding = CommonActivityDeviceSelectBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void initUI() {
        getIntentValue();
        if (selectConfig.isMultSelect) {
            binding.btnSure.setVisibility(View.VISIBLE);
        }
        setTitleText(selectConfig.title);
        binding.btnSure.setOnClickListener(v -> submit());
        fragments = new ArrayList<>();
        List<String> titleArray = new ArrayList<>();
        for (PMSDeviceType type : selectConfig.pmsDeviceType) {
            DeviceSelectFragment selectFragment = new DeviceSelectFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Config.CURRENT_FUNCTION_MODEL, type.name());
            selectFragment.setArguments(bundle);
            titleArray.add(type.getValue());
            fragments.add(selectFragment);
        }
        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), fragments, titleArray);
        binding.viewPager.setAdapter(pagerAdapter);
        binding.tabStrip.setViewPager(binding.viewPager);
        binding.viewPager.setOffscreenPageLimit(Integer.MAX_VALUE);
        setPagerTabStripValue(binding.tabStrip);
        binding.tabStrip.setTabPaddingLeftRight(37);
        binding.tabStrip.setShouldExpand(false);
        if (selectConfig.pmsDeviceType.size()==1){
            binding.tabStrip.setVisibility(View.GONE);
        }
    }

    @Override
    protected void getIntentValue() {
        Intent intent = getIntent();
        selectConfig.bdzId = intent.getStringExtra(Config.CURRENT_BDZ_ID);
        selectConfig.inspectionType = intent.getStringExtra(Config.CURRENT_INSPECTION_TYPE);
        selectConfig.pmsDeviceType = (ArrayList<PMSDeviceType>) intent.getSerializableExtra(PMS_DEVICE_TYPE_KEY);
        selectConfig.deviceBigType = intent.getStringArrayListExtra(DEVICE_BIG_TYPE_KEY);
        selectConfig.isMultSelect = intent.getBooleanExtra(IS_MULTI_SELECT_KEY, false);
        selectConfig.isAllowSelectSpace = intent.getBooleanExtra(IS_ALLOW_SELECT_SPACE_KEY, false);
        selectConfig.title = intent.getStringExtra(Config.TITLE_NAME_KEY);
        selectConfig.filterSql = intent.getStringExtra(FILTER_SQL_KEY);
        if (TextUtils.isEmpty(selectConfig.title)) {
            if (selectConfig.isAllowSelectSpace) {
                selectConfig.title = "请选择设备或间隔";
            } else {
                selectConfig.title = "请选择设备";
            }
        }
        if (selectConfig.pmsDeviceType == null || selectConfig.pmsDeviceType.isEmpty()) {
            selectConfig.pmsDeviceType = Arrays.asList(PMSDeviceType.values());
        }


    }

    private void submit() {
        ArrayList<DbModel> models = new ArrayList<>();
        for (DeviceSelectFragment fragment : fragments) {
            Map<String, HashSet<DbModel>> listMap = fragment.getSelect();
            if (listMap == null) {
                continue;
            }
            for (HashSet<DbModel> modelHashSet : listMap.values()) {
                if (modelHashSet != null && !modelHashSet.isEmpty()) {
                    models.addAll(modelHashSet);
                }
            }
        }
        if (models.isEmpty()) {
            ToastUtils.showMessage("至少选择一个设备！");
            return;
        }

        showConfig(models);

    }

    private void showConfig(final ArrayList<DbModel> selectDeviceList) {
        final Dialog dialog = new Dialog(mActivity, R.style.dialog);
        ViewHolder viewHolder = new ViewHolder(mActivity, null, R.layout.common_inspection_tips, false);
        ListView listView = viewHolder.getView(R.id.lv_container);
        listView.setVisibility(View.VISIBLE);
        viewHolder.getView(R.id.tv_dialog_content).setVisibility(View.GONE);
        TextView title = viewHolder.getView(R.id.tv_dialog_title);
        title.setPadding(getResources().getDimensionPixelSize(R.dimen.xs_exclusive_media_margin_bottom), 0, 0, 0);
        title.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        viewHolder.setText(R.id.tv_dialog_title, "你选择的设备如下：");
        listView.setAdapter(new BaseAdapter<DbModel>(mActivity, selectDeviceList, R.layout.dialog_content_child_item) {
            @Override
            public void convert(ViewHolder holder, DbModel item, int position) {
                holder.setText(R.id.tv_child_item, item.getString(DeviceService.DEVICE_NAME_KEY));
            }
        });
        View.OnClickListener dialogClick = v -> {
            dialog.dismiss();
            if (v.getId() == R.id.btn_sure) {
                Intent intent = new Intent();
                intent.putExtra(RESULT_SELECT_KEY, selectDeviceList);
                intent.putExtra(RESULT_SELECT_TYPE_KEY, SELECT_DEVICES_TYPE);
                setResult(RESULT_OK, intent);
                finish();
            }
        };
        viewHolder.getView(R.id.btn_cancel).setOnClickListener(dialogClick);
        viewHolder.getView(R.id.btn_sure).setOnClickListener(dialogClick);
        dialog.setContentView(viewHolder.getRootView());
        Window mWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        int dialogWidth = ScreenUtils.getScreenWidth(mActivity) * 9 / 10;
        int dialogHeight = LinearLayout.LayoutParams.WRAP_CONTENT;
        lp.width = dialogWidth;
        lp.height = dialogHeight;
        mWindow.setAttributes(lp);
        // 添加动画
        mWindow.setWindowAnimations(R.style.DialogAnim);
        dialog.show();
    }

    @Override
    public void initData() {
        if ((selectConfig.deviceBigType == null || selectConfig.deviceBigType.isEmpty()) && currentInspectionType.contains("special_manual")) {
            List<StandardSpecial> list = StandardSpecialService.getInstance().getStandardSpecial(currentInspectionType);
            for (StandardSpecial stand : list) {
                selectConfig.deviceBigType.add(stand.bigid);
            }
        }
    }

    public SelectConfig getSelectConfig() {
        return selectConfig;
    }

    public void singleSelect(DbModel model, boolean isSpace) {
        Intent intent = new Intent();
        intent.putExtra(RESULT_SELECT_KEY, model);
        intent.putExtra(RESULT_SELECT_TYPE_KEY, isSpace ? SELECT_SPACE_TYPE : SELECT_DEVICE_TYPE);
        setResult(RESULT_OK, intent);
        finish();
    }


    public static class SelectConfig {
        public String bdzId;
        public String inspectionType;
        public List<PMSDeviceType> pmsDeviceType;
        public ArrayList<String> deviceBigType;
        public boolean isMultSelect;
        public boolean isAllowSelectSpace;
        public String filterSql;
        public String title;
    }


    public static Builder with(Activity activity) {
        return new Builder(activity);
    }

    public static class Builder {
        Activity activity;
        String bdzId;
        String inspectionType;
        HashSet<PMSDeviceType> pmsDeviceType = new HashSet<>();
        HashSet<String> deviceBigType = new HashSet<>();
        boolean isMultiSelect = false;
        boolean isAllowSelectSpace=false;
        String filterSql;
        String title;
        int requestCode;

        private Builder(Activity activity) {
            this.activity = activity;
        }

        /**
         * 设置变电站ID 不允许为空
         * @param bdzId
         * @return
         */
        public Builder setBdzId(String bdzId) {
            this.bdzId = bdzId;
            return this;
        }

        public Builder setInspectionType(String inspectionType) {
            this.inspectionType = inspectionType;
            return this;
        }

        /**
         * 设置设备类型
         * @param pmsDeviceType
         * @return
         */
        public Builder setPmsDeviceType(PMSDeviceType... pmsDeviceType) {
            this.pmsDeviceType.addAll(Arrays.asList(pmsDeviceType));
            return this;
        }

        /**
         * 设置设备大类
         * @param deviceBigType
         * @return
         */
        public Builder setDeviceBigType(String... deviceBigType) {
            this.deviceBigType.addAll(Arrays.asList(deviceBigType));
            return this;
        }

        /**
         * 设置是否多选，多选模式只支持设备多选，间隔选择自动失效
         * @param multiSelect
         * @return
         */
        public Builder setMultiSelect(boolean multiSelect) {
            isMultiSelect = multiSelect;
            return this;
        }

        /**
         * 单选时是否允许选择间隔
         * @param allowSelectSpace
         * @return
         */
        public Builder setAllowSelectSpace(boolean allowSelectSpace) {
            isAllowSelectSpace = allowSelectSpace;
            return this;
        }

        /**
         * 设置过滤SQL，device表别名为d,spacing表别名为s。
         * @param filterSql
         * @return
         */
        public Builder setFilterSql(String filterSql) {
            this.filterSql = filterSql;
            return this;
        }

        /**
         * 设置标题
         * @param title
         * @return
         */
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }


        public Builder setRequestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        public void start() {
            Objects.requireNonNull(activity);
            Objects.requireNonNull(bdzId, "变电站ID不能为空！");
            Intent intent = new Intent(activity, DeviceSelectActivity.class);
            intent.putExtra(Config.CURRENT_BDZ_ID, bdzId);
            intent.putExtra(IS_MULTI_SELECT_KEY, isMultiSelect);
            intent.putExtra(IS_ALLOW_SELECT_SPACE_KEY, (!isMultiSelect)&&isAllowSelectSpace);
            if (!TextUtils.isEmpty(filterSql)) {
                intent.putExtra(FILTER_SQL_KEY, filterSql);
            }
            if (!TextUtils.isEmpty(inspectionType)) {
                intent.putExtra(Config.CURRENT_INSPECTION_TYPE, inspectionType);
            }
            if (!deviceBigType.isEmpty()) {
                intent.putStringArrayListExtra(DEVICE_BIG_TYPE_KEY, new ArrayList<>(deviceBigType));
            }
            if (!pmsDeviceType.isEmpty()) {
                intent.putExtra(PMS_DEVICE_TYPE_KEY, new ArrayList<>(pmsDeviceType));
            }
            if (!TextUtils.isEmpty(title)) {
                intent.putExtra(Config.TITLE_NAME_KEY, title);
            }
            activity.startActivityForResult(intent, requestCode);
        }
    }
}
