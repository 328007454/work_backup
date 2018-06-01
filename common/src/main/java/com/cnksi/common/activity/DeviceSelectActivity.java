package com.cnksi.common.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.cnksi.common.Config;
import com.cnksi.common.base.BaseTitleActivity;
import com.cnksi.common.databinding.CommonActivityDeviceSelectBinding;
import com.cnksi.common.enmu.PMSDeviceType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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

    CommonActivityDeviceSelectBinding binding;

    @Override
    protected View getChildContentView() {
        binding = CommonActivityDeviceSelectBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }


    public static class Builder {
        Activity activity;
        String bdzId;
        String inspectionType;
        HashSet<PMSDeviceType> pmsDeviceType = new HashSet<>();
        HashSet<String> deviceBigType = new HashSet<>();
        boolean isMultSelect;
        String filterSql;
        String title;

        private Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder setBdzId(String bdzId) {
            this.bdzId = bdzId;
            return this;
        }

        public Builder setInspectionType(String inspectionType) {
            this.inspectionType = inspectionType;
            return this;
        }

        public Builder setPmsDeviceType(PMSDeviceType... pmsDeviceType) {
            this.pmsDeviceType.addAll(Arrays.asList(pmsDeviceType));
            return this;
        }

        public Builder setDeviceBigType(String... deviceBigType) {
            this.deviceBigType.addAll(Arrays.asList(deviceBigType));
            return this;
        }

        public Builder setMultSelect(boolean multSelect) {
            isMultSelect = multSelect;
            return this;
        }

        public Builder setFilterSql(String filterSql) {
            this.filterSql = filterSql;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public void start() {
            Objects.requireNonNull(activity);
            Objects.requireNonNull(bdzId, "变电站ID不能为空！");
            Intent intent = new Intent(activity, DeviceSelectActivity.class);
            intent.putExtra(Config.CURRENT_BDZ_ID, bdzId);
            if (!TextUtils.isEmpty(filterSql)) {
                intent.putExtra(FILTER_SQL_KEY, filterSql);
            }
            if (!TextUtils.isEmpty(inspectionType)) {
                intent.putExtra(Config.CURRENT_INSPECTION_TYPE, inspectionType);
            }
            if (!deviceBigType.isEmpty()){
                intent.putExtra(DEVICE_BIG_TYPE_KEY,new ArrayList<>(deviceBigType));
            }
            if (!pmsDeviceType.isEmpty()){
                intent.putExtra(PMS_DEVICE_TYPE_KEY,new ArrayList<>(pmsDeviceType));
            }
        }
    }
}
