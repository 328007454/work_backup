package com.cnksi.sjjc.adapter;

import android.content.Context;
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

        holder.setText(R.id.tv_defect_content, item.description);
        holder.setText(R.id.tv_defect_device, item.devcie);
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
     * @param defectLevel
     * @return
     */
    public  CharSequence convert2DefectLevel(String defectLevel) {
        if (Config.GENERAL_LEVEL_CODE.equalsIgnoreCase(defectLevel)) {
            defectLevel =StringUtils.changeTextColor(Config.GENERAL_LEVEL,context;
        } else if (Config.SERIOUS_LEVEL_CODE.equalsIgnoreCase(defectLevel)) {
            defectLevel = Config.SERIOUS_LEVEL;
        } else {
            defectLevel = Config.CRISIS_LEVEL;
        }
        return defectLevel;
    }
}
