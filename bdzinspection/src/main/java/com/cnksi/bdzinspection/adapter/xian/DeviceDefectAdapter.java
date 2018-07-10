package com.cnksi.bdzinspection.adapter.xian;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.bdzinspection.R;
import com.cnksi.common.Config;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.defect.utils.DefectUtils;

import java.util.List;

import static com.cnksi.defect.utils.DefectUtils.calculateRemindTime;

/**
 * Created by Mr.K on 2018/7/7.
 */

public class DeviceDefectAdapter extends BaseQuickAdapter<DefectRecord, BaseViewHolder> {


    public DeviceDefectAdapter(int layoutResId, @Nullable List<DefectRecord> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DefectRecord item) {
        TextView tvDefectContent = helper.getView(R.id.tv_defect_content);
        TextView tvDefect = helper.getView(R.id.tv_defect);
        TextView tvRecordPerson = helper.getView(R.id.tv_record_person);
        TextView tvDefectDiscoverTime = helper.getView(R.id.tv_defect_discover_time);
        TextView tvDefectRemindTime = helper.getView(R.id.tv_defect_remind_time);
        TextView txtDealStatus = helper.getView(R.id.txt_deal_status);
        tvDefect.setText(TextUtils.isEmpty(item.description) ? "" : item.description);
        Object[] result = DefectUtils.convert2DefectDescBackground(item);
        tvDefectContent.setText((CharSequence) result[0]);
        tvDefectContent.setBackgroundColor(Color.parseColor((String) result[1]));
        tvRecordPerson.setText("记录人员：" + (TextUtils.isEmpty(item.discoverer) ? "" : item.discoverer));
        tvDefectDiscoverTime.setText("时间：" + (TextUtils.isEmpty(item.discovered_date) ? "" : (DateUtils.getFormatterTime(item.insertTime, DateUtils.yyyy_MM_dd))));
        tvDefectRemindTime.setText(calculateRemindTime(item));
        if (Config.PROBLEM_LEVEL_CODE.equalsIgnoreCase(item.defectlevel) || Config.HIDDEN_LEVEL_CODE.equalsIgnoreCase(item.defectlevel)) {
            tvDefectRemindTime.setVisibility(View.GONE);
        } else {
            tvDefectRemindTime.setVisibility(View.VISIBLE);
        }
    }
}
