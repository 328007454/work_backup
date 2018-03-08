package com.cnksi.sjjc.activity.gztz;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.activity.AllDeviceListActivity;
import com.cnksi.sjjc.activity.BaseActivity;
import com.cnksi.sjjc.bean.Device;
import com.cnksi.sjjc.databinding.ActivityGztzBhdzjlBinding;
import com.cnksi.sjjc.databinding.ActivityGztzBhdzqkBinding;
import com.cnksi.sjjc.databinding.GztzItemBhdzjlSbBinding;
import com.cnksi.sjjc.databinding.GztzItemBhdzjlYjlxBinding;
import com.cnksi.sjjc.databinding.GztzItemSelectGroupBinding;
import com.cnksi.sjjc.enmu.PMSDeviceType;
import com.cnksi.sjjc.view.gztz.SelectGroup;

import org.xutils.common.util.KeyValue;
import org.xutils.db.table.DbModel;

import java.util.List;

/**
 * 故障保护动作记录第三页
 *
 * @version 1.0
 * @auth wastrel
 * @date 2018/3/7 10:50
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class BHDZJLActivity extends BaseActivity {
    ActivityGztzBhdzjlBinding binding;
    TextView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentValue();
        binding = ActivityGztzBhdzjlBinding.inflate(getLayoutInflater());
        setChildView(binding.getRoot());
        getIntentValue();
        setTitleText(currentBdzName + "保护动作记录");
        initView();
    }

    private void initView() {
        binding.btnNext.setOnClickListener(v -> {
            saveData();
            Intent intent = new Intent(_this, GZTZRecordActivity.class);
            startActivity(intent);
        });
        addOtherDevice();
    }

    private void saveData() {


    }

    /**
     * 点击设备名称后的+号则增加一次设备整体布局
     */
    private void addOtherDevice() {
        GztzItemBhdzjlSbBinding sbBinding = GztzItemBhdzjlSbBinding.inflate(LayoutInflater.from(_this));
        ImageButton addBt = sbBinding.sbmc.getAddButton();
        ImageButton delBt = sbBinding.sbmc.getDeleteButton();
        //首次进来需要加载一次原件类型布局
        addOtherYJLX(sbBinding);
        binding.itemDevice.addView(sbBinding.getRoot());

        if (binding.itemDevice.getChildCount() == 1) {
            sbBinding.sbmc.setVisible(View.VISIBLE, View.GONE);
        } else {
            sbBinding.sbmc.setVisible(View.GONE, View.VISIBLE);
        }

        addBt.setTag(binding.itemDevice.getChildCount());
        delBt.setTag(sbBinding);

        sbBinding.sbmc.getTvValueView().setOnClickListener(view -> {
            Intent intentDevices = new Intent(_this, AllDeviceListActivity.class);
            intentDevices.putExtra(AllDeviceListActivity.FUNCTION_MODEL, PMSDeviceType.one);
            intentDevices.putExtra(AllDeviceListActivity.BDZID, currentBdzId);
            startActivityForResult(intentDevices, Config.ACTIVITY_CHOSE_DEVICE);
            this.view = (TextView) view;
        });
        sbBinding.sbmc.getAddButton().setOnClickListener(view -> {
            addOtherDevice();
        });

        sbBinding.sbmc.getDeleteButton().setOnClickListener(view -> {
            GztzItemBhdzjlSbBinding sbBinding1 = (GztzItemBhdzjlSbBinding) view.getTag();
            binding.itemDevice.removeView(sbBinding1.getRoot());
        });

    }

    /**
     * 设备里的保护原件类型＋号点击 则增加一次相关原件布局
     *
     * @param sbBinding
     */
    private void addOtherYJLX(GztzItemBhdzjlSbBinding sbBinding) {
        GztzItemBhdzjlYjlxBinding yjlxBinding = GztzItemBhdzjlYjlxBinding.inflate(LayoutInflater.from(_this));
        sbBinding.yjlx.addView(yjlxBinding.getRoot());
        LinearLayout parentLayout = (LinearLayout) yjlxBinding.getRoot().getParent();
        if (parentLayout.getChildCount() == 1) {
            yjlxBinding.add.setVisibility(View.VISIBLE);
            yjlxBinding.delete.setVisibility(View.GONE);
            yjlxBinding.add.setTag(sbBinding);
        } else {
            yjlxBinding.add.setVisibility(View.GONE);
            yjlxBinding.delete.setVisibility(View.VISIBLE);
        }
        yjlxBinding.add.setTag(sbBinding);
        yjlxBinding.delete.setTag(yjlxBinding);

        yjlxBinding.add.setOnClickListener(view -> {
            GztzItemBhdzjlSbBinding sbBinding1 = (GztzItemBhdzjlSbBinding) view.getTag();
            addOtherYJLX(sbBinding1);
        });
        yjlxBinding.delete.setOnClickListener(view -> {
            GztzItemBhdzjlYjlxBinding yjlxBinding1 = (GztzItemBhdzjlYjlxBinding) view.getTag();
            LinearLayout layout = (LinearLayout) yjlxBinding1.getRoot().getParent();
            layout.removeView(yjlxBinding1.getRoot());
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            switch (requestCode) {
                case Config.ACTIVITY_CHOSE_DEVICE:
                    DbModel model = (DbModel) dataMap.get(Config.DEVICE_DATA);
                    if (model != null) {
                        view.setText(model.getString(Device.NAME));
                    }
                    break;
            }
        }
    }
}


