package com.cnksi.login.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.common.Config;
import com.cnksi.common.activity.ImageDetailsActivity;
import com.cnksi.common.model.DefectRecord;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.defect.utils.DefectUtils;
import com.cnksi.login.R;

import java.util.ArrayList;
import java.util.List;

import static com.cnksi.defect.utils.DefectUtils.calculateRemindTime;

/**
 * Created by Mr.K on 2018/7/5.
 */

public class HomeWeekDefectAdapter extends BaseQuickAdapter<DefectRecord, BaseViewHolder> {
    private Activity activity;

    public HomeWeekDefectAdapter(Activity activity, int layoutResId, @Nullable List<DefectRecord> data) {
        super(layoutResId, data);
        this.activity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, DefectRecord item) {
        TextView tvDefectContent = helper.getView(R.id.tv_defect_content);
        TextView tvDefectDevice = helper.getView(R.id.tv_defect_device);
        ImageView ivDefectImage = helper.getView(R.id.iv_defect_image);
        TextView tvDefectSpace = helper.getView(R.id.tv_defect_space);
        TextView tvRecordPerson = helper.getView(R.id.tv_record_person);
        TextView tvDefectDiscoverTime = helper.getView(R.id.tv_defect_discover_time);
        TextView tvDefectRemindTime = helper.getView(R.id.tv_defect_remind_time);
        TextView tvDefect = helper.getView(R.id.tv_defect);
        tvDefect.setText(TextUtils.isEmpty(item.description) ? "" : item.description);
         Object[] result = DefectUtils.convert2DefectDescBackground(item);
        tvDefectContent.setText((CharSequence) result[0]);
        tvDefectContent.setBackgroundColor(Color.parseColor((String) result[1]));
        tvDefectDevice.setText("设备：" + (TextUtils.isEmpty(item.devcie) ? "" : item.devcie));
        ivDefectImage.setImageBitmap(null);


        final ArrayList<String> listPicDis = StringUtils.stringToList(item.pics);
        if (listPicDis.size() > 0 && !TextUtils.isEmpty(listPicDis.get(0))) {
            Bitmap bitmap = BitmapUtils.getImageThumbnailByWidth(Config.RESULT_PICTURES_FOLDER + StringUtils.cleanString(listPicDis.get(0)), 280);
            if (bitmap != null) {
                ivDefectImage.setImageBitmap(bitmap);
            }
            ivDefectImage.setOnClickListener(v -> {

                        ImageDetailsActivity.with(activity).setPosition(0).setImageUrlList(StringUtils.addStrToListItem(listPicDis, Config.RESULT_PICTURES_FOLDER))
                                .setDeleteFile(false).setShowDelete(false).start();
                    }
            );
        } else {
            ivDefectImage.setScaleType(ImageView.ScaleType.CENTER);
            ivDefectImage.setImageResource(R.mipmap.icon_nodefect);
            ivDefectImage.setOnClickListener(null);
        }

        tvDefectSpace.setText("间隔：" + (TextUtils.isEmpty(item.spname) ? "" : item.spname));
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
