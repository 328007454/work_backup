package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnksi.core.utils.StringUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.HoleRecord;
import com.cnksi.sjjc.inter.ItemClickListenerPicture;

import org.xutils.x;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by han on 2016/6/13.
 */
public class XianCunHoleAdapter extends BaseAdapter<HoleRecord> {

    private ItemClickListenerPicture<HoleRecord> itemClickListener;

    public void setItemClickListener(ItemClickListenerPicture<HoleRecord> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public XianCunHoleAdapter(Context context, Collection<HoleRecord> data, int layoutId) {
        super(context, data, layoutId);
    }


    @Override
    public void convert(ViewHolder holder, final HoleRecord item, final int position) {
        ArrayList<String> listPicXC=null;
        ArrayList<String> listPicClear=null;
        if(!TextUtils.isEmpty(item.hole_images)){
            listPicXC = StringUtils.string2List(item.hole_images);
        }
        if(!TextUtils.isEmpty(item.clear_images)){
            listPicClear = StringUtils.string2List(item.clear_images);
        }
        //发现孔洞照片数量
        TextView tvDiscoverNum = holder.getView(R.id.tv_discoverhole_num);
        //发现孔洞照片
        ImageView imgDiscover = holder.getView(R.id.img_discoverhole_pic);
        //清除孔洞照片
        final ImageView imgClear = holder.getView(R.id.img_clearhole_pic);
        //清除拍照
        ImageButton ibPhoto = holder.getView(R.id.iv_take_pic);
        //清除所有照片
        ImageButton ibClearPhoto = holder.getView(R.id.iv_delet_pic);
        ibClearPhoto.setVisibility(View.VISIBLE);
        //清除照片的数量
        final TextView tvClearNum = holder.getView(R.id.tv_clearhole_num);
        holder.setText(R.id.tv_hole, item.location + "_" + item.hole_detail);
        if (listPicXC != null) {
            x.image().bind(((ImageView)holder.getView(R.id.img_discoverhole_pic)),Config.RESULT_PICTURES_FOLDER+listPicXC.get(0), CustomApplication.getLargeImageOptions());
            if (listPicXC.size() == 0 || listPicXC.size() == 1) {
                tvDiscoverNum.setVisibility(View.GONE);
            } else {
                tvDiscoverNum.setVisibility(View.VISIBLE);
                holder.setText(R.id.tv_discoverhole_num, listPicXC.size() + "");
            }
        }
        if (listPicClear != null) {
            x.image().bind(((ImageView)holder.getView(R.id.img_clearhole_pic)),Config.RESULT_PICTURES_FOLDER+listPicClear.get(0),CustomApplication.getLargeImageOptions());
            if (listPicClear.size() == 0 || listPicClear.size() == 1) {
                tvClearNum.setVisibility(View.GONE);
            } else {
                tvClearNum.setVisibility(View.VISIBLE);
                tvClearNum.setText(listPicClear.size() + "");
            }
        }else {
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_clear_default);
            imgClear.setImageBitmap(bm);
            ibClearPhoto.setVisibility(View.GONE);
            tvClearNum.setVisibility(View.INVISIBLE);
        }


        ibPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.itemClick(view, item, position, imgClear, tvClearNum);
            }
        });
        ibClearPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClickListener!=null){
                    itemClickListener.itemClick(view,item,position);
                }
            }
        });
        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClickListener!=null){
                    itemClickListener.itemClick(view,item,position);
                }
            }
        });

        imgDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClickListener!=null){
                    itemClickListener.itemClick(view,item,position);
                }
            }
        });
    }
}
