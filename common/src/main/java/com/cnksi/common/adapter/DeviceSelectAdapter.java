package com.cnksi.common.adapter;

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
import com.cnksi.common.R;
import com.cnksi.common.activity.DeviceSelectActivity;
import com.cnksi.common.listener.ItemClickListener;
import com.cnksi.common.model.SpacingGroup;
import com.cnksi.common.model.vo.DeviceItem;
import com.cnksi.common.model.vo.SpaceGroupItem;
import com.cnksi.common.model.vo.SpaceItem;
import com.cnksi.common.view.UnderLineLinearLayout;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.db.table.DbModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.cnksi.common.model.vo.SpaceGroupItem.DEVICE_ITEM;
import static com.cnksi.common.model.vo.SpaceGroupItem.SPACE_GROUP_ITEM;
import static com.cnksi.common.model.vo.SpaceGroupItem.SPACE_ITEM;

/**
 * @author Wastrel
 */
public class DeviceSelectAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public Context context;
    private String keyWord;
    private boolean isOnlyExpandOne = false;
    private MultiItemEntity lastExpandIndex = null;


    private ItemClickListener<MultiItemEntity> selectListener;


    private Map<String, HashSet<DbModel>> selectSpacingDeviceMap = new HashMap<>();

    private boolean isMultiSelect;
    private boolean isAllowSelectSpace;
    private boolean isSecondDevice;

    public DeviceSelectAdapter(Activity context, List<MultiItemEntity> data,boolean isSecondDevice, DeviceSelectActivity.SelectConfig selectConfig) {
        super(data);
        this.context = context;
        this.isMultiSelect = selectConfig.isMultSelect;
        this.isAllowSelectSpace = selectConfig.isAllowSelectSpace;
        this.isSecondDevice=isSecondDevice;
        addItemType(SPACE_GROUP_ITEM, R.layout.select_xiaoshi_item);
        addItemType(SPACE_ITEM, R.layout.select_space_item);
        addItemType(DEVICE_ITEM, R.layout.select_device_item);
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
            default:
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
        HashSet<DbModel> selectDevice = selectSpacingDeviceMap.get(spaceItem.getSpid());
        TextView tvGroupItemSelectTv = helper.getView(R.id.tv_group_item_select);
        //
        final Boolean isOnlySelect;
        if (isMultiSelect) {
            if (null != selectDevice && childCount == selectDevice.size()) {
                tvGroupItemSelectTv.setText("取消全选");
            } else {
                tvGroupItemSelectTv.setText("全选");
            }
            tvGroupItemSelectTv.setVisibility(View.VISIBLE);
            isOnlySelect = false;
        } else if (isAllowSelectSpace) {
            isOnlySelect = true;
            tvGroupItemSelectTv.setText("选择");
            tvGroupItemSelectTv.setVisibility(View.VISIBLE);
        } else {
            isOnlySelect = false;
            tvGroupItemSelectTv.setVisibility(View.GONE);
        }
        tvGroupItemSelectTv.setOnClickListener(view -> {
             if (isOnlySelect) {
                selectListener.onClick(view, spaceItem, getParentPosition(spaceItem));
            } else {
                HashSet<DbModel> dbModels = selectSpacingDeviceMap.get(spaceItem.getSpid());
                List<DeviceItem> childDevices = spaceItem.getSubItems();
                if (dbModels == null) {
                    dbModels = new HashSet<>(childDevices);
                    selectSpacingDeviceMap.put(spaceItem.getSpid(), dbModels);
                } else if (dbModels.size() < childDevices.size()) {
                    dbModels.addAll(childDevices);
                } else {
                    dbModels.clear();
                }
            }
            notifyDataSetChanged();
        });
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
    }


    private void convertDevice(BaseViewHolder helper, final DeviceItem item) {
        final int position = helper.getAdapterPosition();
        TextView txtDevice = helper.getView(R.id.tv_device_name);
        String deviceName =  isSecondDevice ? item.getString("deviceName") : item.getString("shortName");
        String spaceId = item.getString("spid");
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
        // 默认绿色
        txtDevice.setTextColor(context.getResources().getColor(R.color.xs_green_color));
        helper.getView(R.id.rl_device_container).setBackgroundResource(R.drawable.xs_device_green_border_background_selector);
        // 不显示缺陷数量
        helper.getView(R.id.tv_device_defect_count).setVisibility(View.GONE);
        HashSet<DbModel> selectDevice = selectSpacingDeviceMap.get(spaceId);
        // 设备选中
        if (selectDevice != null && selectDevice.contains(item)) {
            helper.getView(R.id.rl_device_container).setSelected(true);
        } else {
            helper.getView(R.id.rl_device_container).setSelected(false);
        }
        txtDevice.setTextColor(context.getResources().getColor(R.color.xs_green_color));
        helper.getView(R.id.rl_device_container).setBackgroundResource(R.drawable.xs_device_green_border_background_selector);
        helper.getView(R.id.rl_device_container).setOnClickListener(v -> {
            if (!isMultiSelect) {
                selectListener.onClick(v, item, position);
            } else {
                HashSet<DbModel> temp = selectSpacingDeviceMap.get(spaceId);
                if (temp == null) {
                    temp = new HashSet<>();
                    temp.add(item);
                    selectSpacingDeviceMap.put(spaceId, temp);
                } else {
                    if (temp.contains(item)) {
                        temp.remove(item);

                    } else {
                        temp.add(item);
                    }
                }
                notifyDataSetChanged();
            }
        });

        //deviceName字段可能为null，会导致在mate7上会导致ANR，但是不会报空指针异常
        txtDevice.setText(Html.fromHtml(TextUtils.isEmpty(deviceName) ? "" : deviceName));
    }


    public void setDeviceClickListener(ItemClickListener<MultiItemEntity> selectListener) {
        this.selectListener = selectListener;
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

    public Map<String, HashSet<DbModel>> getSelectSpacingDeviceMap() {
        return selectSpacingDeviceMap;
    }

}
