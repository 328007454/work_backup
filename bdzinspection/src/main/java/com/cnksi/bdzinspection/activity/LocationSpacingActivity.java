package com.cnksi.bdzinspection.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.cnksi.bdloc.DistanceUtil;
import com.cnksi.bdloc.LatLng;
import com.cnksi.bdloc.LocationListener;
import com.cnksi.bdloc.LocationUtil;
import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.LocationSpacingAdapter;
import com.cnksi.bdzinspection.daoservice.SpacingService;
import com.cnksi.bdzinspection.databinding.XsActivityLocationSpacingBinding;
import com.cnksi.bdzinspection.inter.ItemClickListener;
import com.cnksi.bdzinspection.view.CircleBar;
import com.cnksi.bdzinspection.view.CircleBar.OnProgressChangeListener;
import com.cnksi.common.Config;
import com.cnksi.common.model.Spacing;
import com.cnksi.core.utils.CLog;
import com.cnksi.core.utils.PreferencesUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 摇一摇间隔排序,按照间隔由近及远排序<br/>
 * 点击间隔返回间隔列表并展开相应间隔
 *
 * @author lyndon
 */
public class LocationSpacingActivity extends BaseActivity implements OnProgressChangeListener {
    public final static int OPEN_RESULT_ANIMOTION_TIME = 2000;

    private List<Spacing> spacingData;

    private int spaceCount;

    private LocationSpacingAdapter adapter;

    private List<Spacing> data;


    private String currentFunctionModel;
    
    private XsActivityLocationSpacingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(currentActivity,R.layout.xs_activity_location_spacing);
        
        initialUI();
        initialData();
        if (spaceCount >= 1) {
            initLocation();
            // shakeListener = new ShakeListener(this);
            // shakeListener.setOnShakeListener(onShakeListener);
            startAnimotion();
        } else {
            // rlAnim.setVisibility(View.GONE);
            Toast.makeText(currentActivity, "当前巡检类型没有一次设备间隔，仅一次间隔支持摇一摇。", Toast.LENGTH_SHORT).show();
        }
        initOnClick();
    }

    private void startAnimotion() {
        // rlAnim.setVisibility(View.VISIBLE);
        // circleBar.setMaxstepnumber(spaceCount);
        // circleBar.setProgressChangeListener(this);
        // circleBar.update(spaceCount, 3000);
        // binding.listSpacing.setVisibility(View.GONE);
    }

    private void initialUI() {
        binding.includeTitle.tvTitle.setText(R.string.xs_near_spacing);
        data = new ArrayList<Spacing>();
        adapter = new LocationSpacingAdapter(this, data, R.layout.xs_item_location_spacing);
        adapter.setItemClickListener(new ItemClickListener<Spacing>() {
            @Override
            public void onItemClick(View v, Spacing item, int position) {
                Intent data = new Intent();
                data.putExtra("spacingId", item.spid);
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        });
        binding.listSpacing.setAdapter(adapter);
    }

    private List<LatLng> latLngs = new ArrayList<>();

    private void initialData() {
        getIntentValue();
        LatLng latLng = getIntent().getParcelableExtra(Config.CURRENT_LATLNG);
        if (latLng != null) {
            latLngs.add(latLng);
        }
        // 摇一摇只算一次设备
        // currentFunctionModel=getIntent().getStringExtra(Config.CURRENT_FUNCTION_MODEL);
        currentFunctionModel = "one";
        currentBdzId = PreferencesUtils.get(Config.CURRENT_BDZ_ID, "");
        if (isParticularInspection()) {
            spacingData = SpacingService.getInstance().findSpacingByDeviceTypeAndInspection(currentBdzId, currentFunctionModel, "sort", currentInspectionType, currentReportId);
        } else {
            spacingData = SpacingService.getInstance().findSpacingByDevicesType(currentBdzId, currentFunctionModel,
                    "sort");
        }
        spaceCount = spacingData.size();
        mHandler.postDelayed(() -> openResult(), 300);
    }

    LocationUtil.LocationHelper locationHelper;

    private void initLocation() {
        // 初始化搜索模块，注册事件监听
        locationHelper = LocationUtil.getInstance().getLocalHelper(new LocationListener() {
            @Override
            public void locationSuccess(BDLocation location) {
                latLngs.add(new LatLng(location));
            }
        }).setPeriod(2);
        locationHelper.start();
    }


    public void avgLatLng() {
        calculateDistance(DistanceUtil.getHighest(latLngs));
        binding.listSpacing.setVisibility(View.VISIBLE);
        if (locationHelper != null) {
            locationHelper.stop();
        }
    }

    public void openResult() {

        final TranslateAnimation animationTop = new TranslateAnimation(0, 0, 0, -binding.top.getHeight());
        animationTop.setDuration(OPEN_RESULT_ANIMOTION_TIME);// 设置动画持续时间
        animationTop.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.top.setVisibility(View.GONE);
                avgLatLng();
            }
        });
        animationTop.setFillAfter(true);
        binding.top.startAnimation(animationTop);

        final TranslateAnimation animationButton = new TranslateAnimation(0, 0, 0, binding.buttom.getHeight());
        animationButton.setDuration(OPEN_RESULT_ANIMOTION_TIME);// 设置动画持续时间
        animationButton.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.buttom.setVisibility(View.GONE);
                CLog.e();
                // isOver = true;
            }
        });
        animationButton.setFillAfter(true);
        binding.buttom.startAnimation(animationButton);

    }

    private void calculateDistance(LatLng latLng) {
        if (null != spacingData && !spacingData.isEmpty() && latLng != null && latLng.lat > 0 && latLng.lng > 0) {
            for (int i = 0, count = spacingData.size(); i < count; i++) {
                Spacing spacing = spacingData.get(i);
                String sLat = spacing.latitude;
                String sLng = spacing.longitude;
                if (!TextUtils.isEmpty(sLat) && !TextUtils.isEmpty(sLng)) {
                    LatLng sLatLng = new LatLng(Double.valueOf(sLat), Double.valueOf(sLng));
                    double distance = DistanceUtil.getDistance(sLatLng, latLng);
                    spacing.distance = distance;
                    data.add(spacing);

                }
            }
        }
        Collections.sort(data, (lhs, rhs) -> {
            if (lhs.distance > rhs.distance) {
                return 1;
            } else if (lhs.distance < rhs.distance) {
                return -1;
            } else {
                return 0;
            }
        });
        adapter.notifyDataSetChanged();
        binding.listSpacing.startLayoutAnimation();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    private void initOnClick() {
        binding.includeTitle.ibtnCancel.setOnClickListener(view -> onBackPressed());
    }


    private boolean isOver = false;

    @Override
    public void Change(CircleBar circleBar, final int step) {
        binding.tvSpaceCount.setText(step + "");
        if (spaceCount == step) {
            isOver = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != locationHelper) {
            locationHelper.stop();
        }
    }
}
