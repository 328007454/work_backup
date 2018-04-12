package com.cnksi.inspe.adapter;

import android.graphics.Bitmap;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.core.utils.BitmapUtils;
import com.cnksi.inspe.R;
import com.cnksi.inspe.adapter.entity.GalleryEntity;
import com.cnksi.inspe.utils.Config;
import com.cnksi.inspe.utils.FileUtils;

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
        Bitmap bitmap = BitmapUtils.getImageThumbnail(FileUtils.getInpseRootPath() + item, 73 * 2, 49 * 2);
        helper.setImageBitmap(R.id.imgImg, bitmap);
        if (isRead) {
            helper.setVisible(R.id.delBtn, false);
        } else {
            helper.setVisible(R.id.delBtn, true);
            helper.getView(R.id.delBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onDeleteListener != null) {
                        onDeleteListener.onDelete(item, helper.getAdapterPosition());
                    }
                }
            });
        }
    }

    private boolean isRead = false;

    /**
     * 是否不可编辑
     *
     * @param isRead true不可删除，false可以删除
     */
    public void setMode(boolean isRead) {
        this.isRead = isRead;
    }

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener listener) {
        onDeleteListener = listener;
    }

    public interface OnDeleteListener {
        void onDelete(String entity, int possion);
    }

}