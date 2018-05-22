package com.cnksi.sjjc.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.TreeNode;
import com.cnksi.sjjc.adapter.ViewHolder;
import com.cnksi.common.model.CopyItem;
import com.cnksi.common.model.CopyResult;
import com.cnksi.sjjc.inter.CopyItemLongClickListener;
import com.cnksi.sjjc.inter.ItemClickListener;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;
import java.util.Map;

/**
 * @author lyndon
 */
public class CopyViewUtil {

    private Map<String, CopyResult> copyResultMap;
    private KeyBordListener keyBordListener;

    private ItemClickListener<CopyItem> itemClickListener;
    private CopyItemLongClickListener<CopyResult> itemLongClickListener;

    public interface KeyBordListener {
        void onViewFocus(EditText v, CopyItem item, CopyResult result);

        void hideKeyBord();
    }

    public void setItemLongClickListener(CopyItemLongClickListener<CopyResult> itemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener;
    }

    public void setKeyBordListener(KeyBordListener keyBordListener) {
        this.keyBordListener = keyBordListener;
    }

    public void setItemClickListener(ItemClickListener<CopyItem> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setCopyResultMap(Map<String, CopyResult> copyResultMap) {
        this.copyResultMap = copyResultMap;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void createCopyView(final Activity context, List<TreeNode> data, LinearLayout parentLayout) {
        parentLayout.removeAllViews();
        int index = 0;
        for (TreeNode tree : data) {
            if (tree.isParent()) {
                String remark = "";
                // 添加根节点数据
                ViewHolder parentHolder = new ViewHolder(context, null, R.layout.group_item2, false);
                CopyItem parentItem = (CopyItem) tree.bindObject;
                parentLayout.addView(parentHolder.getRootView());
                index++;
                for (TreeNode child : tree.childTreeNodes) {
                    // 添加抄录节点数据
                    final CopyItem childItem = (CopyItem) child.bindObject;
                    ViewHolder childHolder = new ViewHolder(context, null, R.layout.copy_value_child_item2, false);
                    AutoUtils.autoSize(childHolder.getRootView());
                    RelativeLayout layoutRoot = (RelativeLayout) childHolder.getRootView();
                    // 构造抄录描述
                    String descript = "";
                    if ("Y".equals(childItem.val)) {
                        descript = "抄录" + childItem.description + (TextUtils.isEmpty(childItem.unit) ? "" : "(" + childItem.unit + ")");
                    } else if ("Y".equals(childItem.val_a)) {
                        descript = "抄录A相" + childItem.description + (TextUtils.isEmpty(childItem.unit) ? "" : "(" + childItem.unit + ")");
                    } else if ("Y".equals(childItem.val_b)) {
                        descript = "抄录B相" + childItem.description + (TextUtils.isEmpty(childItem.unit) ? "" : "(" + childItem.unit + ")");
                    } else if ("Y".equals(childItem.val_c)) {
                        descript = "抄录C相" + childItem.description + (TextUtils.isEmpty(childItem.unit) ? "" : "(" + childItem.unit + ")");
                    } else if ("Y".equals(childItem.val_o)) {
                        descript = "抄录O相" + childItem.description + (TextUtils.isEmpty(childItem.unit) ? "" : "(" + childItem.unit + ")");
                    }

                    if (!TextUtils.isEmpty(childItem.min) || !TextUtils.isEmpty(childItem.max)) {
                        descript += "(" + (TextUtils.isEmpty(childItem.min) ? "" : childItem.min) + "-" + (TextUtils.isEmpty(childItem.max) ? "" : childItem.max) + ")";
                    }
                    childHolder.setText(R.id.tv_copy_content, descript);

                    EditText copyValue = childHolder.getView(R.id.et_copy_values);
//					copyValue.setRInputType(InputType.TYPE_CLASS_PHONE);
                    copyValue.addTextChangedListener(new CopyTextWatcher(childItem));
//					copyValue.setOnTouchListener(new OnTouchListener() {
//						@Override
//						public boolean onTouch(View v, MotionEvent event) {
//							CopyResult copyResult = copyResultMap.get(childItem.id);
//							keyBordListener.onViewFocus((EditText) v, childItem, copyResult);
//							return false;
//						}
//					});
                    // 显示历史抄录
                    if (copyResultMap.keySet().contains(childItem.id)) {
                        CopyResult copyResult = copyResultMap.get(childItem.id);
                        if ("Y".equals(childItem.val) && !("-1".equals(copyResult.val))) {
                            copyValue.setText(copyResult.val);
                        }
                        if ("Y".equals(childItem.val_a) && !("-1".equals(copyResult.val_a))) {
                            copyValue.setText(copyResult.val_a);
                        }
                        if ("Y".equals(childItem.val_b) && !("-1".equals(copyResult.val_b))) {
                            copyValue.setText(copyResult.val_b);
                        }
                        if ("Y".equals(childItem.val_c) && !("-1".equals(copyResult.val_c))) {
                            copyValue.setText(copyResult.val_c);
                        }
                        if ("Y".equals(childItem.val_o) && !("-1".equals(copyResult.val_o))) {
                            copyValue.setText(copyResult.val_o);
                        }
                        if (!TextUtils.isEmpty(copyResult.remark)) {//抄录结果的remar字段是否有看不清的提示。
                            // builder.append(copyResult.remark + "	");
                            remark = copyResult.remark;
                        }
                    }

                    final int position = index;
                    childHolder.getView(R.id.ibtn_history_data).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemClickListener.itemClick(v, childItem, position);
                        }
                    });
                    //抄录项如果看不清长按弹出对话框
                    layoutRoot.setOnLongClickListener(new OnLongClickListener() {

                        @Override
                        public boolean onLongClick(View v) {
                            CopyResult copyResult = copyResultMap.get(childItem.id);
                            itemLongClickListener.onItemLongClick(v, copyResult, position, childItem);
                            return true;
                        }
                    });
                    parentLayout.addView(childHolder.getRootView());
                    index++;
                }
                //查看整个抄录项remark字段是否具有看不清的提示，有则在整个item上提示该显示。
                if (!TextUtils.isEmpty(remark)) {
                    if (remark.endsWith(",")) {
                        remark = "(" + remark.substring(0, remark.length() - 1) + ")";
                    } else {
                        remark = "(" + remark.substring(0, remark.length()) + ")";
                    }
                }
                parentHolder.setText(R.id.tv_group_item, parentItem.description + remark);
            }
        }
    }

    public class CopyTextWatcher implements TextWatcher {

        private CopyItem copyItem;

        public CopyTextWatcher(CopyItem copyItem) {
            this.copyItem = copyItem;
        }

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
                String value = StringUtils.getTransformTep(s.toString());
                if ("Y".equals(copyItem.val)) {
                    copyResult.val = value;
                }
                if ("Y".equals(copyItem.val_a)) {
                    copyResult.val_a = value;
                }
                if ("Y".equals(copyItem.val_b)) {
                    copyResult.val_b = value;
                }
                if ("Y".equals(copyItem.val_c)) {
                    copyResult.val_c = value;
                }
                if ("Y".equals(copyItem.val_o)) {
                    copyResult.val_o = value;
                }
            }
        }
    }
}
