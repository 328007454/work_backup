package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.CdbhclValue;
import com.cnksi.sjjc.bean.ReportCdbhcl;
import com.cnksi.sjjc.bean.ReportJzlbyqfjkg;
import com.cnksi.sjjc.enmu.InspectionType;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.db.table.DbModel;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by han on 2016/6/7.
 * 交直流分接开关适配器
 */
public class JZLFenJieKaiGuanContentAdapter extends SimpleBaseAdapter {
    private String currentInspectionType;
    private DbModel model;
    private ReportCdbhcl mCdbhcl;
    private CdbhclValue value;

    public JZLFenJieKaiGuanContentAdapter(Context context, List<? extends Object> dataList, String currentInspectionType) {
        super(context, dataList);
        this.currentInspectionType = currentInspectionType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (currentInspectionType.equals(InspectionType.SBJC_05.name())) {
            model = (DbModel) getItem(position);
        } else if (currentInspectionType.equals(InspectionType.SBJC_04.name())) {
//            mCdbhcl = (ReportCdbhcl) getItem(position);
            value = (CdbhclValue) getItem(position);
        }

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.jzlfenjiekaiguan_adapter, null);
            holder = new ViewHolder();
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
            AutoUtils.autoSize(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (currentInspectionType.equals(InspectionType.SBJC_05.name())) {
            holder.bcdsContainer.setVisibility(View.VISIBLE);
            holder.dongZuoContainer.setVisibility(View.VISIBLE);
            holder.cdbhContaner.setVisibility(View.GONE);

            holder.txtTitle.setText(model.getString(ReportJzlbyqfjkg.DEVICE_NAME));
            if (TextUtils.isEmpty(model.getString(ReportJzlbyqfjkg.BCDS)))
                holder.bcdsContainer.setVisibility(View.GONE);
            if (TextUtils.isEmpty(model.getString(ReportJzlbyqfjkg.DZCS)))
                holder.dongZuoContainer.setVisibility(View.GONE);
            holder.partTitleTwo.setText("分接开关动作次数");
            holder.partTitleOne.setText("分接开关调整后档位");
            holder.txtDzcs.setText(model.getString(ReportJzlbyqfjkg.DZCS));
            holder.txtBcds.setText(model.getString(ReportJzlbyqfjkg.BCDS));
        } else if (currentInspectionType.equals(InspectionType.SBJC_04.name())) {
            holder.txtCdbhTitle.setText(value.getName());
            holder.txtCdbhContent.setText(value.getValue());
            holder.jzlContainer.setVisibility(View.GONE);
        }


        return convertView;
    }

    class ViewHolder {
        @ViewInject(R.id.jzl_report_container)
        private LinearLayout jzlContainer;
        @ViewInject(R.id.jzl_title)
        private TextView txtTitle;
        @ViewInject(R.id.tv_put_bcds)
        private TextView txtBcds;
        @ViewInject(R.id.tv_put_dzcs)
        private TextView txtDzcs;
        //档位容器描述
        @ViewInject(R.id.copy_part_one)
        private TextView partTitleOne;
        //档位容器描述
        @ViewInject(R.id.copy_part_two)
        private TextView partTitleTwo;


        @ViewInject(R.id.cdbh_report_container)
        private LinearLayout cdbhContaner;
        //档位容器
        @ViewInject(R.id.ll_bcds_container)
        private LinearLayout bcdsContainer;
        //动作次数容器
        @ViewInject(R.id.ll_dzcs_container)
        private LinearLayout dongZuoContainer;

        @ViewInject(R.id.cdbh_title)
        private TextView txtCdbhTitle;
        @ViewInject(R.id.cdbh_content)
        private TextView txtCdbhContent;

    }
}
