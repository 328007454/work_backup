package com.cnksi.sjjc.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnksi.core.utils.CoreConfig;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.activity.DefectControlActivity;
import com.cnksi.sjjc.bean.DefectRecord;

import org.xutils.x;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by han on 2017/4/25.
 */

public class DefectContentAdapter extends BaseAdapter<DefectRecord> {
    DefectControlActivity activity;

    public DefectContentAdapter(DefectControlActivity context, Collection data) {
        super(context, data, R.layout.adapter_defect_item);
        activity = context;
    }

    public static CharSequence calculateRemindTime(DefectRecord defectRecord) {
        if (defectRecord == null || TextUtils.isEmpty(defectRecord.discovered_date)) return "";
        int day;
        if (Config.CRISIS_LEVEL_CODE.equalsIgnoreCase(defectRecord.defectlevel) || Config.CRISIS_LEVEL.equals(defectRecord.defectlevel)) {
            day = 1;
        } else if (Config.SERIOUS_LEVEL_CODE.equalsIgnoreCase(defectRecord.defectlevel) || Config.SERIOUS_LEVEL.equals(defectRecord.defectlevel)) {
            day = 30;
        } else {
            day = 365;
        }
        String s = "到期时间：" + DateUtils.getAfterTime(defectRecord.discovered_date, day, "yyyy-MM-dd");
        if (s.compareTo(DateUtils.getAfterTime(3)) > 0) {
            return s;
        } else return StringUtils.changeTextColor(s, Color.RED);
    }

    @Override
    public void convert(ViewHolder holder, DefectRecord item, int position) {
        ImageView defectImage = holder.getView(R.id.iv_defect_image);
        TextView defectContentTv = holder.getView(R.id.tv_defect_content);
        defectContentTv.setText(convert2DefectLevel(item));
        holder.setText(R.id.tv_defect_device, "设备：" + (TextUtils.isEmpty(item.devcie) ? "" : item.devcie));
        final ArrayList<String> listPicDis = com.cnksi.core.utils.StringUtils.string2List(item.pics);
        if (listPicDis.size() > 0 && !TextUtils.isEmpty(listPicDis.get(0))) {
            x.image().bind(defectImage, Config.RESULT_PICTURES_FOLDER + StringUtils.cleanString(listPicDis.get(0)), CustomApplication.getLargeImageOptions());
            defectImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.showImageDetails(activity, StringUtils.addStrToListItem(listPicDis, Config.RESULT_PICTURES_FOLDER));
                }
            });
        } else {
            defectImage.setScaleType(ImageView.ScaleType.CENTER);
            defectImage.setImageResource(R.mipmap.icon_nodefect);
            defectImage.setOnClickListener(null);
        }

        holder.setText(R.id.tv_defect_space, "间隔：" + (TextUtils.isEmpty(item.spname) ? "" : item.spname));
        holder.setText(R.id.tv_record_person, "记录人员：" + (TextUtils.isEmpty(item.discoverer) ? "" : item.discoverer));
        holder.setText(R.id.tv_defect_discover_time, "时间：" + (TextUtils.isEmpty(item.discovered_date) ? "" : (DateUtils.formatDateTime(item.insertTime, CoreConfig.dateFormat1))));
        holder.setText(R.id.tv_defect_remind_time, calculateRemindTime(item));
    }

    /**
     * 转换缺陷等级为 一般 严重 危机
     *
     * @param item
     * @return
     */
    public CharSequence convert2DefectLevel(DefectRecord item) {
        CharSequence result;

        if (Config.GENERAL_LEVEL_CODE.equalsIgnoreCase(item.defectlevel)) {
            result = StringUtils.changePartTextColor("[" + Config.GENERAL_LEVEL + "]" + item.description, Color.parseColor("#F1B55B"), 0, 6);
        } else if (Config.SERIOUS_LEVEL_CODE.equalsIgnoreCase(item.defectlevel)) {
            result = StringUtils.changePartTextColor("[" + Config.SERIOUS_LEVEL + "]" + item.description, Color.parseColor("#F18815"), 0, 6);
        } else {
            result = StringUtils.changePartTextColor("[" + Config.CRISIS_LEVEL + "]" + item.description, Color.RED, 0, 6);
        }
        return result;
    }
}
