package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.CopyItem;
import com.cnksi.sjjc.bean.CopyResult;
import com.cnksi.sjjc.inter.ItemClickListener;
import com.cnksi.sjjc.inter.ItemLongClickListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lyndon on 2016/9/5.
 */
public class CopyAdapter extends TreeAdapter {

    private Map<String, CopyResult> copyResultMap;

    private ItemClickListener<CopyItem> itemClickListener;
    private ItemLongClickListener<CopyResult> itemLongClickListener;
    private String remark;
    private TextView tvTitle;
    public void setItemClickListener(ItemClickListener<CopyItem> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setItemLongClickListener(ItemLongClickListener<CopyResult> itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }

    public CopyAdapter(Context context, List<TreeNode> data) {
        super(context, data);
    }

    public void setCopyResultMap(HashMap<String, CopyResult> copyResultMap) {
        this.copyResultMap = copyResultMap;
    }

    @Override
    public void convert(ViewHolder holder, TreeNode item, final int position) {
        final CopyItem copyItem = (CopyItem) item.bindObject;
        remark ="";
        if (item.level == 1) {
            holder.setText(R.id.tv_group_item, copyItem.description);
            tvTitle = holder.getView(R.id.tv_group_item);
        } else {
            if ("Y".equals(copyItem.val))
                holder.setText(R.id.tv_copy_content, "抄录" + copyItem.description + (TextUtils.isEmpty(copyItem.unit) ? "" : "(" + copyItem.unit + ")"));
            if ("Y".equals(copyItem.val_a))
                holder.setText(R.id.tv_copy_content, "抄录A相" + copyItem.description + (TextUtils.isEmpty(copyItem.unit) ? "" : "(" + copyItem.unit + ")"));
            if ("Y".equals(copyItem.val_b))
                holder.setText(R.id.tv_copy_content, "抄录B相" + copyItem.description + (TextUtils.isEmpty(copyItem.unit) ? "" : "(" + copyItem.unit + ")"));
            if ("Y".equals(copyItem.val_c))
                holder.setText(R.id.tv_copy_content, "抄录C相" + copyItem.description + (TextUtils.isEmpty(copyItem.unit) ? "" : "(" + copyItem.unit + ")"));
            if ("Y".equals(copyItem.val_o))
                holder.setText(R.id.tv_copy_content, "抄录O相" + copyItem.description + (TextUtils.isEmpty(copyItem.unit) ? "" : "(" + copyItem.unit + ")"));

            EditText copyValue = holder.getView(R.id.et_copy_values);
            // 显示历史抄录
            if (copyResultMap.keySet().contains(copyItem.id)) {
                CopyResult copyResult = copyResultMap.get(copyItem.id);
                if ("Y".equals(copyItem.val))
                    copyValue.setText("-1".equalsIgnoreCase(copyResult.val)?"":copyResult.val);
                if ("Y".equals(copyItem.val_a))
                    copyValue.setText("-1".equalsIgnoreCase(copyResult.val_a)?"":copyResult.val_a);
                if ("Y".equals(copyItem.val_b))
                    copyValue.setText("-1".equalsIgnoreCase(copyResult.val_b)?"":copyResult.val_b);
                if ("Y".equals(copyItem.val_c))
                    copyValue.setText("-1".equalsIgnoreCase(copyResult.val_c)?"":copyResult.val_c);
                if ("Y".equals(copyItem.val_o))
                    copyValue.setText("-1".equalsIgnoreCase(copyResult.val_o)?"":copyResult.val_o);
                if (!TextUtils.isEmpty(copyResult.remark)) {
                    remark = copyResult.remark;
                }
            }
            copyValue.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    CopyResult copyResult = copyResultMap.get(copyItem.id);
                    if (null != copyResult) {
                        if ("Y".equals(copyItem.val))
                            copyResult.val = TextUtils.isEmpty(s.toString())&&"-1".equalsIgnoreCase(copyResult.val)?"-1":s.toString();
                        if ("Y".equals(copyItem.val_a))
                            copyResult.val_a = TextUtils.isEmpty(s.toString())&&"-1".equalsIgnoreCase(copyResult.val_a)?"-1":s.toString();
                        if ("Y".equals(copyItem.val_b))
                            copyResult.val_b =TextUtils.isEmpty(s.toString())&&"-1".equalsIgnoreCase(copyResult.val_b)?"-1":s.toString();
                        if ("Y".equals(copyItem.val_c))
                            copyResult.val_c = TextUtils.isEmpty(s.toString())&&"-1".equalsIgnoreCase(copyResult.val_c)?"-1":s.toString();
                        if ("Y".equals(copyItem.val_o))
                            copyResult.val_o = TextUtils.isEmpty(s.toString())&&"-1".equalsIgnoreCase(copyResult.val_o)?"-1":s.toString();
                    }
                }
            });
            holder.getView(R.id.ibtn_history_data).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    itemClickListener.itemClick(v, copyItem, position);
                }
            });
            holder.getView(R.id.rl_root_container).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CopyResult copyResult = copyResultMap.get(copyItem.id);
                    itemLongClickListener.itemLongClick(v, copyResult, position,copyItem);
                }
            });
        }
        if (!TextUtils.isEmpty(remark)) {
            if (remark.endsWith(","))
                remark = "(" + remark.substring(0, remark.length() - 1) + ")";
            else
                remark = "(" + remark.substring(0, remark.length()) + ")";
        }
        tvTitle.setText(copyItem.description + (TextUtils.isEmpty(remark)?"":remark));
    }


}
