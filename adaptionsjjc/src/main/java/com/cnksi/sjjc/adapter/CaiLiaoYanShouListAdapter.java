package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.AcceptCardItem;
import com.cnksi.sjjc.bean.AcceptReportItem;
import com.cnksi.sjjc.inter.ItemCommonClickListener;

import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by han on 2016/5/9.
 * 验收大项对应的小项的listivew的adapter
 *
 */
public class CaiLiaoYanShouListAdapter extends SimpleBaseAdapter {

    private ItemCommonClickListener<AcceptCardItem> itemClickListener;

    public String reportId;
    private Context mContext;

    public void setItemClickListener(ItemCommonClickListener<AcceptCardItem> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public CaiLiaoYanShouListAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

    public CaiLiaoYanShouListAdapter(Context context, List<? extends Object> dataList) {
        super(context, dataList);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int totalCount = getCount();
        AcceptReportItem mAReportItem=null;
        final AcceptCardItem acceptCardItem = (AcceptCardItem) getItem(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.expandlistview_listview_yanshou_item, parent, false);
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        try {
             mAReportItem = CustomApplication.getYanShouDbManager().selector(AcceptReportItem.class).where(AcceptReportItem.STANDER_ITEM_ID, "=", acceptCardItem.cardId).and(AcceptReportItem.REPORT_ITEM_ID, "=", acceptCardItem.itemId).and(AcceptReportItem.REPORT_ID, "=", reportId).findFirst();

            if (mAReportItem != null) {
                if (0 == mAReportItem.isNormal) {
                    holder.btOtherFuction.setBackgroundResource(R.drawable.grass_green_button_background_selector);
                    holder.btOtherFuction.setText("合格");
                } else if (-1 == mAReportItem.isNormal) {
                    holder.btOtherFuction.setBackgroundResource(R.drawable.red_button_background_selector);
                    holder.btOtherFuction.setText("不合格");
                }
                if (!TextUtils.isEmpty(mAReportItem.itemAudio) || !TextUtils.isEmpty(mAReportItem.itemImages) || !TextUtils.isEmpty(mAReportItem.itemVideo)) {
                    if (1 == mAReportItem.isParent) {
                        Bitmap bm = BitmapFactory.decodeFile(Config.SIGN_PICTURE_FOLDER + mAReportItem.itemImages);
                        holder.ivQianMing.setVisibility(View.VISIBLE);
                        holder.ivQianMing.setImageBitmap(bm);
                    } else {
                        holder.btYanshou.setImageResource(R.mipmap.icon_addshow);
                    }

                }


            }

        } catch (DbException e) {
            e.printStackTrace();
        }

        holder.tvNum.setText(position + 1 + "");
        holder.tvContent.setText(acceptCardItem.itemContent);
        holder.ivQianMing.setTag(acceptCardItem);
        //设置小项最后一个为签字项
        if (acceptCardItem.itemContent==null) {
            holder.tvNum.setVisibility(View.GONE);
            holder.tvContent.setText("验收人签字");
            Resources resource =  mContext.getResources();
            ColorStateList csl = resource.getColorStateList(R.color.light_blue_color);
            if (csl != null) {
                holder.tvContent.setTextColor(csl);
            }

            holder.btYanshou.setBackgroundResource(R.mipmap.icon_sign);
            holder.btOtherFuction.setVisibility(View.GONE);
        }
        holder.setAcceptType(acceptCardItem, position, totalCount);
        return convertView;
    }


    class ViewHolder {
        AcceptCardItem acceptCardItem;
        int position;
        int totalCount;

        public void setAcceptType(AcceptCardItem acceptCardItem, int position, int totalCount) {
            this.acceptCardItem = acceptCardItem;
            this.position = position;
            this.totalCount = totalCount;
        }

        @ViewInject(R.id.tv_number)
        private TextView tvNum;
        @ViewInject(R.id.tv_yanshou_item)
        private TextView tvContent;
        @ViewInject(R.id.btn_yanshou)
        private ImageView btYanshou;
        @ViewInject(R.id.btn_extra_function)
        private Button btOtherFuction;
        @ViewInject(R.id.iv_show_qianming)
        private ImageView ivQianMing;


        @Event({R.id.btn_yanshou, R.id.btn_extra_function, R.id.iv_show_qianming})
        private void onViewClick(View view) {
            itemClickListener.itemClick(view, acceptCardItem, position, totalCount, ivQianMing, btOtherFuction, btYanshou);
        }
    }
}

