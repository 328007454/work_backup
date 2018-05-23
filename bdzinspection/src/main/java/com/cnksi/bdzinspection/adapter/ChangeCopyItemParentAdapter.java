package com.cnksi.bdzinspection.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.SimpleBaseAdapter;
import com.cnksi.bdzinspection.databinding.XsChangeCopyItemParentBinding;
import com.cnksi.bdzinspection.model.ChangeCopyItem;
import com.cnksi.common.model.CopyType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by han on 2017/5/18.
 */

public class ChangeCopyItemParentAdapter extends SimpleBaseAdapter {
    private ItemClickListener itemClickListener;
    //    private List<CopyType> parentTypes = new ArrayList<>();
    private Map<String, List<ChangeCopyItem>> childTypeMap = new HashMap<>();
    private XsChangeCopyItemParentBinding parentBinding;
    private ChangeCopyItemRecyclerChildAdapter changeCopyItemChildAdapter;
    private Map<CopyType, ChangeCopyItemRecyclerChildAdapter> itemChildAdapterMap = new HashMap<>();
    private CopyType type;

    public ChangeCopyItemParentAdapter(Context context, List<CopyType> dataList) {
        super(context, dataList);
//        this.parentTypes = dataList;
    }


    public void setALLCopyType(Map<String, List<ChangeCopyItem>> copyNamesMap) {
        this.childTypeMap = copyNamesMap;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        type = parentTypes.get(position);
        type = (CopyType) dataList.get(position);
        convertView = setParentItem(position, convertView, parent);
        RecyclerView recyclerView = parentBinding.lvChild;
//        if (null == itemChildAdapterMap.get(type)) {
        if (null != childTypeMap.get(type.key)) {
            changeCopyItemChildAdapter = new ChangeCopyItemRecyclerChildAdapter(recyclerView, childTypeMap.get(type.key), R.layout.xs_change_copy_item_child);
            itemChildAdapterMap.put(type, changeCopyItemChildAdapter);
            changeCopyItemChildAdapter.setItemClickListener(itemClickListener);
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
            recyclerView.setAdapter(changeCopyItemChildAdapter);
        }
//        } else {
//            recyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
//            changeCopyItemChildAdapter = itemChildAdapterMap.get(type);
//            changeCopyItemChildAdapter.setList(childTypeMap.get(type.key));
//            recyclerView.setAdapter(changeCopyItemChildAdapter);

//        }
        return convertView;
    }

    private View setParentItem(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            parentBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.xs_change_copy_item_parent, parent, false);
        } else {
            parentBinding = DataBindingUtil.getBinding(convertView);
        }

        List<ChangeCopyItem> changeCopyItems = childTypeMap.get(type.key);
        if (changeCopyItems.size() > 1) {
            parentBinding.item3to1.setBackground(mContext.getResources().getDrawable(R.drawable.xs_icon_1_1));
        } else {
            parentBinding.item3to1.setBackground(mContext.getResources().getDrawable(R.drawable.xs_icon_3_1));
        }
        String groupTitle = ((CopyType) dataList.get(position)).name;
        parentBinding.tvGroupItem.setText(TextUtils.isEmpty(groupTitle) ? "" : groupTitle);
        parentBinding.addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onClick(v, type, position);
                }
            }
        });
        parentBinding.item3to1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.onClick(v, type, position);
                }
            }
        });
        return parentBinding.getRoot();
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
