package com.cnksi.defect.adapter;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.common.Config;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.defect.R;
import com.cnksi.defect.defect_enum.DefectEnum;

import org.xutils.db.table.DbModel;

import java.util.List;

/**
 * @author Mr.K  on 2018/6/4.
 * @decrption 默认缺陷内容
 */

public class DefectDefaultAdapter extends BaseQuickAdapter<DbModel, BaseViewHolder> {
    public DefectDefaultAdapter(int layoutResId, @Nullable List<DbModel> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DbModel item) {
        helper.setText(R.id.txt_name, (convert2DefectLevel(item)));
    }

    public void setList(List<DbModel> defaultDefectStr) {
        super.mData = defaultDefectStr;
        notifyDataSetChanged();
    }

    /**
     * 转换缺陷等级为 一般 严重 危机
     *
     * @param item
     * @return
     */
    public CharSequence convert2DefectLevel(DbModel item) {
        CharSequence result = "";
        if (TextUtils.equals(item.getString("level"), DefectEnum.general_text.value)) {
            result = StringUtils.changePartTextColor("[" + Config.GENERAL_LEVEL + "]" + item.getString("description"), Color.parseColor("#F1B55B"), 0, 6);
        } else if (TextUtils.equals(item.getString("level"), DefectEnum.serious_text.value)) {
            result = StringUtils.changePartTextColor("[" + Config.SERIOUS_LEVEL + "]" + item.getString("description"), Color.parseColor("#F18815"), 0, 6);

        } else if (TextUtils.equals(item.getString("level"), DefectEnum.critical_text.value)) {
            result = StringUtils.changePartTextColor("[" + Config.CRISIS_LEVEL + "]" + item.getString("description"), Color.RED, 0, 6);
        }

        return result;
    }
}
