package com.cnksi.inspe.adapter;

import android.graphics.Bitmap;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.inspe.R;
import com.cnksi.inspe.adapter.entity.GalleryEntity;

import java.util.List;

/**
 * @version v1.0
 * @auther Today(张军)
 * @date 2018/3/27 14:53
 */
public class GalleryAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public GalleryAdapter(List data) {
        super(R.layout.inspe_gallery_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
//        helper.setText(R.id.imgImg, item);
        Bitmap bitmap = BitmapUtils.getImageThumbnail(item, 73 * 2, 49 * 2);
        helper.setImageBitmap(R.id.imgImg, bitmap);
        helper.getView(R.id.delBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onDeleteListener != null) {
                    onDeleteListener.onDelete(item, helper.getAdapterPosition());
                }
            }
        });
    }

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener listener) {
        onDeleteListener = listener;
    }

    public interface OnDeleteListener {
        void onDelete(String entity, int possion);
    }

}