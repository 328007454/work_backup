package com.cnksi.bdzinspection.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.activity.DeviceSelectActivity;
import com.cnksi.bdzinspection.model.SpacingGroup;
import com.cnksi.bdzinspection.model.tree.DeviceItem;
import com.cnksi.bdzinspection.model.tree.SpaceGroupItem;
import com.cnksi.bdzinspection.model.tree.SpaceItem;
import com.cnksi.bdzinspection.view.UnderLineLinearLayout;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.db.table.DbModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/1/25 12:14
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class DeviceSelectAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    public final static int SPACE_GROUP_ITEM = 0;
    public final static int SPACE_ITEM = 1;
    public final static int DEVICE_ITEM = 2;
    public Context context;
    private String keyWord;
    private boolean isOnlyExpandOne = false;
    private MultiItemEntity lastExpandIndex = null;


    private ItemClickListener<DbModel> deviceClickListener;
    private ItemClickListener<SpaceItem> groupItemClickListenner;

    private Map<String, List<DbModel>> selectSpacingDeviceMap = new HashMap<>();


    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public DeviceSelectAdapter(Activity context, List<MultiItemEntity> data) {
        super(data);
        this.context = context;
        addItemType(SPACE_GROUP_ITEM, R.layout.xs_group_xiaoshi_item);
        addItemType(SPACE_ITEM, R.layout.xs_group_item);
        addItemType(DEVICE_ITEM, R.layout.xs_device_item);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case SPACE_GROUP_ITEM:
                final SpaceGroupItem spaceGroupItem = (SpaceGroupItem) item;
                convertSpaceGroup(helper, spaceGroupItem);
                break;
            case SPACE_ITEM:
                final SpaceItem spaceItem = (SpaceItem) item;
                convertSpace(helper, spaceItem);
                break;
            case DEVICE_ITEM:
                final DeviceItem deviceItem = (DeviceItem) item;
                convertDevice(helper, deviceItem);
                break;
        }
    }

    private void convertSpaceGroup(final BaseViewHolder helper, final SpaceGroupItem groupItem) {
        SpacingGroup group = groupItem.group;
        TextView txtSpace = helper.getView(R.id.tv_group_item);
        txtSpace.setText(group.name);
        ImageView imgOpen = helper.getView(R.id.img_open);
        imgOpen.setImageResource(groupItem.isExpanded() ? R.drawable.xs_ic_shrink : R.drawable.xs_ic_open);
        helper.itemView.setOnClickListener(v -> {
            int pos = helper.getAdapterPosition();
            if (groupItem.isExpanded()) {
                collapse(pos);
            } else {
                expand(pos);
            }
        });
    }


    private void convertSpace(final BaseViewHolder helper, final SpaceItem spaceItem) {
        final DbModel space = spaceItem.spacing;
        TextView txtSpace = helper.getView(R.id.tv_group_item);
        ImageView imgOpen = helper.getView(R.id.img_open);

        //如果他有上级 则调整一下缩进和字体大小
        if (spaceItem.getParent() != null) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) txtSpace.getLayoutParams();
            layoutParams.leftMargin = AutoUtils.getPercentHeightSize(30);
            txtSpace.setLayoutParams(layoutParams);
            txtSpace.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(42));
            ((UnderLineLinearLayout) helper.itemView).setMarginLeft(AutoUtils.getPercentHeightSize(30));
        }
        //处理间隔名称
        String spacingName = space.getString("spacingName");
        if (!TextUtils.isEmpty(keyWord)) {
            String spacePY = space.getString("spacePY");
            int index = spacePY.indexOf(keyWord);
            if (-1 != index && index + keyWord.length() <= spacingName.length()) {
                String pyName = spacingName.substring(index, index + keyWord.length());
                spacingName = spacingName.replaceAll(pyName, "<font color=\"#FF0000\">" + pyName + "</font>");
            }
        }
        int childCount = spaceItem.getSubItems().size();
        spacingName += "(" + childCount + ")";
        txtSpace.setText(Html.fromHtml(spacingName));
        List<DbModel> selectDevice = selectSpacingDeviceMap.get(spaceItem.getSpid());
        helper.getView(R.id.tv_group_item_select).setVisibility(View.VISIBLE);
        ((TextView) helper.getView(R.id.tv_group_item_select)).setText("全选");
        if (null != selectDevice && childCount == selectDevice.size()) {
            ((TextView) helper.getView(R.id.tv_group_item_select)).setText("取消全选");
        }
        // 间隔展开
        imgOpen.setImageResource(spaceItem.isExpanded() ? R.drawable.xs_ic_shrink : R.drawable.xs_ic_open);
        // 间隔点击
        helper.itemView.setOnClickListener(v -> {
            int pos = helper.getAdapterPosition();
            if (spaceItem.isExpanded()) {
                collapse(pos);
            } else {
                expand(pos);
            }
        });
        helper.getView(R.id.tv_group_item_select).setOnClickListener(view -> {
            if (null != groupItemClickListenner) {
                groupItemClickListenner.onClick(view, spaceItem, getParentPosition(spaceItem));
            }
        });
        if (!TextUtils.isEmpty(inspectionType)&&(inspectionType.contains("maintenance")||inspectionType.contains("switchover"))){
            helper.getView(R.id.tv_group_item_select).setVisibility(View.GONE);
        }

    }


    private void convertDevice(BaseViewHolder helper, final DeviceItem item) {
        final int position = helper.getAdapterPosition();
        TextView txtDevice = helper.getView(R.id.tv_device_name);
        String deviceName = item.getString("deviceName");
        String spaceId = item.getString("spid");
        String deviceId = item.getString("deviceId");
        String devicePY = item.getString("devicePY");
        RelativeLayout relativeLayout = helper.getView(R.id.rl_device_container);
        relativeLayout.setTag("dContainer");
        relativeLayout.setMinimumHeight(AutoUtils.getPercentHeightSizeBigger(105));
        relativeLayout.setMinimumWidth(AutoUtils.getPercentWidthSizeBigger(270));
        // 设备出现搜索关键字
        if (!TextUtils.isEmpty(keyWord)) {
            int index = devicePY.indexOf(keyWord);
            if (-1 != index && index + keyWord.length() < deviceName.length()) {
                String pyName = deviceName.substring(index, index + keyWord.length());
                deviceName = deviceName.replaceAll(pyName, "<font color=\"#FF0000\">" + pyName + "</font>");
            }
        }
        //恢复Item默认状态
        txtDevice.setTextColor(context.getResources().getColor(R.color.xs_green_color));// 默认绿色
        helper.getView(R.id.rl_device_container).setBackgroundResource(R.drawable.xs_device_green_border_background_selector);
        helper.getView(R.id.tv_device_defect_count).setVisibility(View.GONE);// 不显示缺陷数量
        List<DbModel> selectDevice = selectSpacingDeviceMap.get(spaceId);
        DbModel exitDevice = DeviceSelectActivity.getSelectDevice(selectDevice, deviceId);
        // 设备选中
        if (null != exitDevice) {
            helper.getView(R.id.rl_device_container).setSelected(true);
        } else {
            helper.getView(R.id.rl_device_container).setSelected(false);
        }
        txtDevice.setTextColor(context.getResources().getColor(R.color.xs_green_color));
        helper.getView(R.id.rl_device_container).setBackgroundResource(R.drawable.xs_device_green_border_background_selector);
        helper.getView(R.id.rl_device_container).setOnClickListener(v -> deviceClickListener.onClick(v, item, position));

        //deviceName字段可能为null，会导致在mate7上会导致ANR，但是不会报空指针异常
        txtDevice.setText(Html.fromHtml(TextUtils.isEmpty(deviceName) ? "" : deviceName));
    }


    public void setDeviceClickListener(ItemClickListener<DbModel> deviceClickListener) {
        this.deviceClickListener = deviceClickListener;
    }


    public void setGroupItemClickListenner(ItemClickListener<SpaceItem> groupItemClickListenner) {
        this.groupItemClickListenner = groupItemClickListenner;
    }


    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    @Override
    public int expand(int position) {
        MultiItemEntity t = getData().get(position);
        int r = super.expand(position);
        if (isOnlyExpandOne) {
            if (t != lastExpandIndex) {
                if (lastExpandIndex != null && ((SpaceItem) lastExpandIndex).isExpanded()) {
                    int p = getData().indexOf(lastExpandIndex);
                    if (p >= 0) {
                        collapse(p);
                    }
                }
            }
        } else {
            lastExpandIndex = null;
        }
        lastExpandIndex = t;
        return r;
    }

    @Override
    public int collapse(int position) {
        if (getData().get(position) == lastExpandIndex) {
            lastExpandIndex = null;
        }
        return super.collapse(position);
    }


    public void setShowOnly(boolean showOnly) {
        this.isOnlyExpandOne = showOnly;
    }

    public void setSelectSpacingDeviceMap(Map<String, List<DbModel>> selectSpacingDeviceMap) {
        this.selectSpacingDeviceMap = selectSpacingDeviceMap;
    }


    private String inspectionType;
    public void setCurrentInspectionType(String inspectionType) {
        this.inspectionType = inspectionType;

    }
}
