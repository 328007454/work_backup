package com.cnksi.bdzinspection.adapter.inspectionready;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.cnksi.bdzinspection.R;
import com.cnksi.common.utils.ViewHolder;
import com.cnksi.bdzinspection.adapter.base.BaseLinearLayoutAdapter;
import com.cnksi.bdzinspection.model.ReportTool;
import com.cnksi.bdzinspection.model.Tool;
import com.cnksi.common.listener.ItemClickListener;
import com.cnksi.common.utils.StringUtilsExt;

import java.util.HashMap;
import java.util.List;

/**
 * 工器具
 *
 * @author terry
 */
public class ToolsAdapter extends BaseLinearLayoutAdapter<Tool> {

    private HashMap<String, ReportTool> toolMap;

    public ToolsAdapter(Context context, List<Tool> dataList, LinearLayout layout, ItemClickListener toolsClick) {
        super(context, dataList, layout, R.layout.xs_tools_item);
        this.toolsClick = toolsClick;
    }

    @Override
    public void convert(ViewHolder holder, final Tool item, final int position) {
        holder.setText(R.id.et_input_values, getNum(item.toolid));
        holder.setText(R.id.tv_tools_name, item.name + "(" + item.unit + ")");
        holder.setVisibility(R.id.tv_tools_tips, TextUtils.isEmpty(item.tips) ? View.GONE : View.VISIBLE);
        holder.setText(R.id.tv_tools_tips, item.tips);
        holder.getView(R.id.tv_tools_name).setOnClickListener(v -> {
            if (toolsClick != null) {
                toolsClick.onClick(v, item, position);
            }
        });
    }

    private ItemClickListener toolsClick;

    private String getNum(String s) {
        ReportTool tool = toolMap.get(s);
        if (tool == null) {
            return "";
        }
        return StringUtilsExt.nullTo(tool.num, "");
    }

    public void setToolMap(HashMap<String, ReportTool> toolMap) {
        this.toolMap = toolMap;
    }
}
