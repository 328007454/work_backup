package com.cnksi.inspe.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.inspe.R;
import com.cnksi.inspe.entity.device.DeviceItem;
import com.cnksi.inspe.entity.device.SpaceItem;

import org.xutils.db.table.DbModel;

import java.util.List;

/**
 * Created by Mr.K on 2018/4/9.
 */

public class DeviceAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public final static int SPACE_ITEM = 1;
    public final static int DEVICE_ITEM = 2;
    private String keyWord;
    private OnDeviceItemClickListerner onItemClickListerner;
    private List<String> listCheck;

    public void setKeyWord(String newKey) {
        this.keyWord = newKey;
    }

    public void setListCheck(List<String> listCheck) {
        this.listCheck = listCheck;
    }

    public interface OnDeviceItemClickListerner {
        void OnItemClickListen(View v, Object item, int position);
    }

    public void setOnDeviceItemClickListener(OnDeviceItemClickListerner onItemClickListener) {
        this.onItemClickListerner = onItemClickListener;
    }

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     * @param data A new list is created out of this one to avoid mutable list
     */
    public DeviceAdapter(Activity context, List<MultiItemEntity> data) {
        super(data);
        addItemType(SPACE_ITEM, R.layout.inspe_device_group_item);
        addItemType(DEVICE_ITEM, R.layout.inspe_device_item);
    }

    private int expandPosition;

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case SPACE_ITEM:
                SpaceItem spaceItem = (SpaceItem) item;
                DbModel spModel = spaceItem.spacing;
                String spaceName = spModel.getString("sname");
                helper.setText(R.id.tv_group_item, spaceName);
                ImageView imgOpen = helper.getView(R.id.img_open);
                // 间隔展开
                imgOpen.setImageResource(spaceItem.isExpanded() ? R.drawable.inspe_device_shrink : R.drawable.inspe_device_open);
                helper.itemView.setOnClickListener(view -> {
                    if (spaceItem.isExpanded()) {
                        collapse(helper.getAdapterPosition(), true);
                    } else {
                        expand(helper.getAdapterPosition(), true);
                        expandPosition = helper.getAdapterPosition();
                    }
                });
                formatKeyWord(helper, spaceName, spModel.getString("snamepy"));

                break;
            case DEVICE_ITEM:
                DeviceItem deviceItem = (DeviceItem) item;
                DbModel dvModle = deviceItem.dbModel;
                String deviceName = dvModle.getString("dnameshort");
                helper.setText(R.id.tv_device_name, deviceName);
                helper.itemView.setOnClickListener(view -> {
                    if (null != onItemClickListerner) {
                        onItemClickListerner.OnItemClickListen(view, deviceItem, helper.getAdapterPosition());
                    }
                });
                formatKeyWord(helper, deviceName, dvModle.getString("dshortpinyin"));
                if (!listCheck.isEmpty()&&listCheck.contains(dvModle.getString("deviceid"))){
                    helper.setTextColor(R.id.tv_device_name,mContext.getResources().getColor(R.color.color_333333));
                    helper.getView(R.id.rl_device_container).setBackground(ContextCompat.getDrawable(mContext, R.drawable.inspe_black_line_shape));
                }else {
                    helper.setTextColor(R.id.tv_device_name,mContext.getResources().getColor(R.color.color_03b9a0));
                    helper.getView(R.id.rl_device_container).setBackground(ContextCompat.getDrawable(mContext, R.drawable.inspe_device_green_border_background_selector ));
                }
                break;
        }
    }

    private MultiItemEntity lastExpandIndex = null;

    @Override
    public int expand(int position, boolean animate, boolean notify) {
        MultiItemEntity entity = getData().get(position);
        int index = super.expand(position, animate, notify);
        if (entity != lastExpandIndex) {
            if (lastExpandIndex == null) {
//                lastExpandIndex = getData().get(0);
            }
            if (lastExpandIndex != null && ((SpaceItem) lastExpandIndex).isExpanded()) {
                int p = getData().indexOf(lastExpandIndex);
                if (p >= 0) {
                    collapse(p);
                }
            }
        }
        lastExpandIndex = entity;
        return index;
    }

    @Override
    public int collapse(int position, boolean animate) {
        if (getData().get(position) == lastExpandIndex) lastExpandIndex = null;
        return super.collapse(position, animate);
    }

    /**
     * 搜索的内容标红色
     * @param helper
     * @param name
     */
    private void formatKeyWord(BaseViewHolder helper, String name, String shortName) {
        if (!TextUtils.isEmpty(keyWord)) {
            SpannableStringBuilder builder = new SpannableStringBuilder(name);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.RED);
            if (shortName.toUpperCase().contains(keyWord)) {
                int index = shortName.toUpperCase().indexOf(keyWord);
                if (index != -1 && name.length() >= (index + keyWord.length())) {
                    builder.setSpan(colorSpan, index, index + keyWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    if (helper.getItemViewType() == SPACE_ITEM)
                        helper.setText(R.id.tv_group_item, builder);
                    else if (helper.getItemViewType() == DEVICE_ITEM)
                        helper.setText(R.id.tv_device_name, builder);
                }
            }
        }
    }

    public int  getExpandPosition(){
        return expandPosition;
    }

}
