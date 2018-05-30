package com.cnksi.bdzinspection.activity;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.ViewHolder;
import com.cnksi.common.base.BaseAdapter;
import com.cnksi.bdzinspection.fragment.DeviceSelectFragment;
import com.cnksi.bdzinspection.fragment.DeviceSelectFragment.DeviceClickListener;
import com.cnksi.common.Config;
import com.cnksi.common.base.BaseActivity;
import com.cnksi.common.base.FragmentPagerAdapter;
import com.cnksi.common.daoservice.LookupService;
import com.cnksi.common.databinding.CommonActivityDeviceSelectBinding;
import com.cnksi.common.enmu.LookUpType;
import com.cnksi.common.model.Device;
import com.cnksi.common.model.Lookup;
import com.cnksi.common.model.vo.SpaceItem;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.ToastUtils;

import org.xutils.db.table.DbModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备选择界面
 *
 * @author lyndon
 */
public class DeviceSelectActivity extends BaseActivity implements OnPageChangeListener, DeviceClickListener, DeviceSelectFragment.SpaceClickListener {
    public final static String SELECT_TYPE = "choose_type";
    public final static String SELECT_TYPE_RADIO = "radio";
    public final static String SELECT_TYPE_MULT = "mult";
    public final static String RESULT_SELECT_DEVICE = "result_device";
    public final static String RESULT_SELECT_SPACE = "result_select_space";
    public final static String RESULT_SELECT_SPACE_ID = "result_select_space_id";
    public final static String RESULT_SELECT_DEVICE_NAME = "result_device_name";

    private List<Lookup> lookups;

    private List<Fragment> fragmentList;

    private int currentPosition;

    private DeviceSelectFragment currentFragment;

    private Map<String, List<DbModel>> selectSpacingDeviceMap = new HashMap<>();

    private String selectType;

    private CommonActivityDeviceSelectBinding binding;

    public static DbModel getSelectDevice(List<DbModel> selectDeviceList, String deviceId) {
        if (selectDeviceList != null && !selectDeviceList.isEmpty()) {
            for (DbModel dbModel : selectDeviceList) {
                if (deviceId.equals(dbModel.getString("deviceId"))) {
                    return dbModel;
                }
            }
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(mActivity, R.layout.common_activity_device_select);

        initialUI();
        initOnClick();
    }


    private void initialUI() {
        getIntentValue();
        currentInspectionType = getIntent().getStringExtra(Config.CURRENT_INSPECTION_TYPE);
       // binding.includeTitle.tvTitle.setText(R.string.xs_device_select);
        selectType = getIntent().getStringExtra(SELECT_TYPE);
        if (SELECT_TYPE_MULT.equals(selectType)) {
            binding.btnStartInspection.setVisibility(View.VISIBLE);
        }
        lookups = LookupService.getInstance().findLookupByType(LookUpType.pmsDeviceType.name(), true);
        if (null != lookups && !lookups.isEmpty()) {
            fragmentList = new ArrayList<>();
            List<String> titleArray = new ArrayList<>();
            for (Lookup look : lookups) {
                DeviceSelectFragment selectFragment = new DeviceSelectFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Config.CURRENT_FUNCTION_MODEL, look.k);
                bundle.putString(Config.CURRENT_INSPECTION_TYPE, currentInspectionType);
                bundle.putBoolean(Config.SEARCH_DEVICE_KEY, look.k.equals(Config.SEARCH_DEVICE_KEY));
                selectFragment.setArguments(bundle);
                selectFragment.setSelectSpacingDeviceMap(selectSpacingDeviceMap);
                titleArray.add(look.v);
                selectFragment.setDeviceClickListener(this);
                selectFragment.setSpaceClickListener(this);
                fragmentList.add(selectFragment);
            }
            FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(), fragmentList, titleArray);
            binding.viewPager.setAdapter(pagerAdapter);
            binding.tabStrip.setViewPager(binding.viewPager);
            setPagerTabStripValue(binding.tabStrip);
            binding.tabStrip.setTabPaddingLeftRight(37);
            binding.tabStrip.setShouldExpand(false);
            binding.tabStrip.setOnPageChangeListener(this);
        }
        currentFragment = (DeviceSelectFragment) fragmentList.get(binding.viewPager.getCurrentItem());
    }

    private void initOnClick() {

     //   binding.includeTitle.ibtnCancel.setOnClickListener(view -> DeviceSelectActivity.this.onBackPressed());

        binding.btnStartInspection.setOnClickListener(view -> {
            List<DbModel> selectDeviceList = new ArrayList<>();
            for (String key : selectSpacingDeviceMap.keySet()) {
                List<DbModel> spacingDeviceList = selectSpacingDeviceMap.get(key);
                if (!spacingDeviceList.isEmpty()) {
                    selectDeviceList.addAll(spacingDeviceList);
                }
            }
            if (selectDeviceList.isEmpty()) {
                ToastUtils.showMessage("请选择设备");
                return;
            }
            DeviceSelectActivity.this.showConfig(selectDeviceList);
        });

    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {
        currentPosition = position;
        currentFragment = (DeviceSelectFragment) fragmentList.get(position);
    }

    private void showConfig(final List<DbModel> selectDeviceList) {
        final Dialog dialog = new Dialog(mActivity, R.style.dialog);
        ViewHolder viewHolder = new ViewHolder(mActivity, null, R.layout.common_inspection_tips, false);
        ListView listView = viewHolder.getView(R.id.lv_container);
        listView.setVisibility(View.VISIBLE);
        viewHolder.getView(R.id.tv_dialog_content).setVisibility(View.GONE);
        TextView title = viewHolder.getView(R.id.tv_dialog_title);
        title.setPadding(getResources().getDimensionPixelSize(R.dimen.xs_exclusive_media_margin_bottom), 0, 0, 0);
        title.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        viewHolder.setText(R.id.tv_dialog_title, "你选择的设备如下：");
        listView.setAdapter(new BaseAdapter<DbModel>(mActivity, selectDeviceList, R.layout.xs_item_location_spacing) {
            @Override
            public void convert(ViewHolder holder, DbModel item, int position) {
                holder.setText(R.id.name, item.getString("deviceName"));
            }
        });
        OnClickListener dialogClick = v -> {
            dialog.dismiss();
            if (v.getId() == R.id.btn_sure) {
                // 返回传递设备
                Intent data = new Intent();
                StringBuffer sb = new StringBuffer();
                for (DbModel dbmodel : selectDeviceList) {
                    sb.append(dbmodel.getString("deviceId")).append(",");
                }
                sb.deleteCharAt(sb.length() - 1);
                data.putExtra(RESULT_SELECT_DEVICE, sb.toString());
                DeviceSelectActivity.this.setResult(RESULT_OK, data);
                DeviceSelectActivity.this.finish();
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
        mWindow.setWindowAnimations(R.style.XS_DialogAnim);
        dialog.show();
    }

    @Override
    public void onDeviceClick(View view, DbModel device, int position) {
        if (SELECT_TYPE_MULT.equals(selectType)) {
            selectDeviceData(device);
            currentFragment.notifyDataChanged();
        } else {
            Intent data = new Intent();
            data.putExtra(RESULT_SELECT_SPACE_ID, device.getString(Device.SPID));
            data.putExtra(RESULT_SELECT_SPACE, device.getString("spacingName"));
            data.putExtra(RESULT_SELECT_DEVICE, device.getString("deviceId"));
            data.putExtra(RESULT_SELECT_DEVICE_NAME, device.getString("deviceName"));
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private void selectGroupData(SpaceItem spaceItem) {
        List<MultiItemEntity> data = currentFragment.getAllData();
        int childCount = spaceItem.getSubItems().size();
        List<DbModel> spacingDeviceList = selectSpacingDeviceMap.get(spaceItem.getSpid());
        if (spacingDeviceList != null && spacingDeviceList.size() == childCount) {
            spacingDeviceList.clear();
        } else {
            List<DbModel> deviceModels = spaceItem.getAllSubDevices();
            if (spacingDeviceList == null) {
                spacingDeviceList = new ArrayList<>();
            }
            spacingDeviceList.clear();
            spacingDeviceList.addAll(deviceModels);
            selectSpacingDeviceMap.put(spaceItem.getSpid(), spacingDeviceList);
        }
    }

    private void selectDeviceData(DbModel device) {
        String spacingId = device.getString("spid");
        String deviceId = device.getString("deviceId");
        List<DbModel> spacingDeviceList = selectSpacingDeviceMap.get(spacingId);
        if (spacingDeviceList == null) {
            spacingDeviceList = new ArrayList<>();
            selectSpacingDeviceMap.put(spacingId, spacingDeviceList);
        }
        DbModel exitDevice = getSelectDevice(spacingDeviceList, deviceId);
        if (null != exitDevice) {
            spacingDeviceList.remove(exitDevice);
        } else {
            spacingDeviceList.add(device);
        }
    }

    @Override
    public void onSpaceClick(View view, SpaceItem spaceItem, int position) {
        selectGroupData(spaceItem);
        currentFragment.notifyDataChanged();
    }
}
