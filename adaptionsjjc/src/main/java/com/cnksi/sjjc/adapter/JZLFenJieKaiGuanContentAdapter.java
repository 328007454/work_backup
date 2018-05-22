package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.text.TextUtils;
import android.view.View;

import com.cnksi.sjjc.bean.CdbhclValue;
import com.cnksi.sjjc.bean.ReportCdbhcl;
import com.cnksi.sjjc.bean.ReportJzlbyqfjkg;
import com.cnksi.sjjc.databinding.JzlfenjiekaiguanAdapterBinding;
import com.cnksi.common.enmu.InspectionType;

import org.xutils.db.table.DbModel;

import java.util.List;

/**
 * Created by han on 2016/6/7.
 * 交直流分接开关适配器
 */
public class JZLFenJieKaiGuanContentAdapter extends com.cnksi.core.adapter.BaseAdapter {
    private String currentInspectionType;
    private DbModel model;
    private ReportCdbhcl mCdbhcl;
    private CdbhclValue value;

    public JZLFenJieKaiGuanContentAdapter(Context context, List<? extends Object> dataList, String currentInspectionType, int layoutId) {
        super(context, dataList, layoutId);
        this.currentInspectionType = currentInspectionType;
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        ViewHolder holder;
//        if (convertView == null) {
//            convertView = mInflater.inflate(R.layout.jzlfenjiekaiguan_adapter, null);
//            holder = new ViewHolder();
//            convertView.setTag(holder);
//            AutoUtils.autoSize(convertView);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//        if (currentInspectionType.equals(InspectionType.SBJC_05.name())) {
//        return convertView;
//    }

    @Override
    public void convert(ViewDataBinding dataBinding, Object item, int position) {
        if (currentInspectionType.equals(InspectionType.SBJC_05.name())) {
            model = (DbModel) getItem(position);
        } else if (currentInspectionType.equals(InspectionType.SBJC_04.name())) {
            value = (CdbhclValue) getItem(position);
        }
        JzlfenjiekaiguanAdapterBinding binding = (JzlfenjiekaiguanAdapterBinding) dataBinding;
        if (currentInspectionType.equals(InspectionType.SBJC_05.name())) {
            binding.llBcdsContainer.setVisibility(View.VISIBLE);
            binding.llDzcsContainer.setVisibility(View.VISIBLE);
            binding.cdbhReportContainer.setVisibility(View.GONE);
            binding.jzlTitle.setText(model.getString(ReportJzlbyqfjkg.DEVICE_NAME));
            if (TextUtils.isEmpty(model.getString(ReportJzlbyqfjkg.BCDS))) {
                binding.llBcdsContainer.setVisibility(View.GONE);
            }
            if (TextUtils.isEmpty(model.getString(ReportJzlbyqfjkg.DZCS))) {
                binding.llDzcsContainer.setVisibility(View.GONE);
            }
            binding.copyPartTwo.setText("分接开关动作次数");
            binding.copyPartOne.setText("分接开关调整后档位");
            binding.tvPutDzcs.setText(model.getString(ReportJzlbyqfjkg.DZCS));
            binding.tvPutBcds.setText(model.getString(ReportJzlbyqfjkg.BCDS));
        } else if (currentInspectionType.equals(InspectionType.SBJC_04.name())) {
            binding.cdbhTitle.setText(value.getName());
            binding.cdbhContent.setText(value.getValue());
            binding.jzlReportContainer.setVisibility(View.GONE);
        }

    }

    public void setListBean(List<CdbhclValue> listBean) {
        realDatas = listBean;
        notifyDataSetChanged();
    }

    public void setListModel(List<DbModel> model) {
        realDatas = model;
        notifyDataSetChanged();
    }
}
