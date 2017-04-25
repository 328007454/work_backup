package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnksi.core.utils.CoreConfig;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.DefectRecord;

import org.xutils.x;

import java.util.Collection;

/**
 * Created by han on 2017/4/25.
 */

public class DefectContentAdapter extends BaseAdapter<DefectRecord> {
    public DefectContentAdapter(Context context, Collection data) {
        super(context, data, R.layout.adapter_defect_item);
    }


    @Override
    public void convert(ViewHolder holder, DefectRecord item, int position) {
        ImageView defectImage = holder.getView(R.id.iv_defect_image);
        TextView defectContentTv = holder.getView(R.id.tv_defect_content);
        defectContentTv.setText(convert2DefectLevel(item));
        holder.setText(R.id.tv_defect_device, "设备：" + item.devcie);
        String[] defectPicArray = StringUtils.cleanString(item.pics).split(CoreConfig.COMMA_SEPARATOR);
        if (defectPicArray != null && defectPicArray.length > 0
                && !TextUtils.isEmpty(StringUtils.cleanString(defectPicArray[0]))) {
            x.image().bind(defectImage, Config.RESULT_PICTURES_FOLDER + StringUtils.cleanString(defectPicArray[0]), CustomApplication.getLargeImageOptions());
        } else {
            defectImage.setScaleType(ImageView.ScaleType.CENTER);
            defectImage.setImageResource(R.mipmap.icon_nodefect);
        }

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
