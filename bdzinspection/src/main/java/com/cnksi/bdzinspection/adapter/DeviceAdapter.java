package com.cnksi.bdzinspection.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.model.PlacedDevice;
import com.cnksi.bdzinspection.model.ReportSnwsd;
import com.cnksi.common.Config;
import com.cnksi.common.SystemConfig;
import com.cnksi.common.daoservice.DeviceService;
import com.cnksi.common.enmu.InspectionType;
import com.cnksi.common.enmu.PMSDeviceType;
import com.cnksi.common.listener.ItemClickListener;
import com.cnksi.common.listener.ItemLongClickListener;
import com.cnksi.common.model.Device;
import com.cnksi.common.model.SpacingGroup;
import com.cnksi.common.model.vo.DefectInfo;
import com.cnksi.common.model.vo.DeviceItem;
import com.cnksi.common.model.vo.SpaceGroupItem;
import com.cnksi.common.model.vo.SpaceItem;
import com.cnksi.common.utils.StringUtilsExt;
import com.cnksi.common.view.UnderLineLinearLayout;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.db.table.DbModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.cnksi.common.model.vo.SpaceGroupItem.DEVICE_ITEM;
import static com.cnksi.common.model.vo.SpaceGroupItem.SPACE_GROUP_ITEM;
import static com.cnksi.common.model.vo.SpaceGroupItem.SPACE_ITEM;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/1/25 12:14
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class DeviceAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public Context context;
    private HashSet<String> copyDeviceIdList = new HashSet<>();
    // 抄录完成的设备
    private Map<String, List<String>> copyedMap;

    private String keyWord;
    // 变电站存在缺陷
    private Map<String, DefectInfo> defectmap;
    // 变电站已到间隔Id
    private Set<String> arriveSpaceIdList;
    // 变电站已到设备Id
    private HashMap<String, PlacedDevice> arriveDeviceList = new HashMap<>();
    /**
     * 已经抄录的小室室内温湿度的id集合
     */
    HashSet<String> groupSnwsds = new HashSet<String>();
    // 摇一摇动画
    private boolean shakeAnimation;

    // 摇一摇动画
    private boolean shakeDeviceAnimation;

    // 当前巡视类型
    private String currentInspection;

    private String shakeSpaceId;
    private String shakeDeviceId;

    private boolean isOneDevice;
    private boolean isSecondDevice;
    private boolean isOnlyExpandOne = true;
    private MultiItemEntity lastExpandIndex = null;
    private Map<String, Integer> spaceDefctCount = new HashMap<>();


    private ItemClickListener<DbModel> groupItemClickListener;
    private ItemLongClickListener<DbModel> groupItemLongClickListener;
    private ItemLongClickListener<DbModel> deviceItemLongClickListener;
    private ItemClickListener<DbModel> deviceClickListener;
    private ItemClickListener<DbModel> copyClickListener;
    private ItemClickListener<SpacingGroup> groupItemListener;
    private String inspectionType;
    private boolean hasLocation = false;
    private HashSet<String> hasQrCodeSpids = new HashSet<>();


    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public DeviceAdapter(Activity context, List<MultiItemEntity> data) {
        super(data);
        this.context = context;
        addItemType(SPACE_GROUP_ITEM, R.layout.select_xiaoshi_item);
        addItemType(SPACE_ITEM, R.layout.select_space_item);
        addItemType(DEVICE_ITEM, R.layout.select_device_item);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case SPACE_GROUP_ITEM:
                if (item instanceof SpaceGroupItem) {
                    final SpaceGroupItem spaceGroupItem = (SpaceGroupItem) item;
                    convertSpaceGroup(helper, spaceGroupItem);
                }
                break;
            case SPACE_ITEM:
                if (item instanceof SpaceItem) {
                    final SpaceItem spaceItem = (SpaceItem) item;
                    convertSpace(helper, spaceItem);
                }
                break;
            case DEVICE_ITEM:
                final DeviceItem deviceItem = (DeviceItem) item;
                convertDevice(helper, deviceItem);
                break;
            default:
        }
    }

    private void convertSpaceGroup(final BaseViewHolder helper, final SpaceGroupItem groupItem) {
        final int position = helper.getAdapterPosition();
        final SpacingGroup group = groupItem.group;
        TextView txtSpace = helper.getView(R.id.tv_group_item);
        ImageView imgCopy = helper.getView(R.id.ibt_copy_pen);
        txtSpace.setText(group.name + "(" + groupItem.getSubSize() + ")");
        ImageView imgOpen = helper.getView(R.id.img_open);
        imgOpen.setImageResource(groupItem.isExpanded() ? R.drawable.xs_ic_shrink : R.drawable.xs_ic_open);

        if (groupSnwsds.contains(group.id)) {
            imgCopy.setImageResource(R.drawable.ic_white_finish);
        } else {
            imgCopy.setImageResource(R.drawable.ic_white_unfinish);
        }
        if (!TextUtils.isEmpty(SystemConfig.getCopyInspection()) && SystemConfig.getCopyInspection().contains(currentInspection) && group.copy) {
            imgCopy.setVisibility(View.VISIBLE);
        } else {
            imgCopy.setVisibility(View.GONE);
        }
        helper.itemView.setOnClickListener(v -> {
            int pos = helper.getAdapterPosition();
            if (groupItem.isExpanded()) {
                DeviceAdapter.this.collapse(pos);
            } else {
                DeviceAdapter.this.expand(pos);
            }
        });
        imgCopy.setOnClickListener(view -> {
            if (null != groupItemListener) {
                groupItemListener.onClick(view, group, position);
            }
        });
    }

    private void convertSpace(final BaseViewHolder helper, final SpaceItem spaceItem) {
        final int position = helper.getAdapterPosition();
        final DbModel space = spaceItem.spacing;
        TextView txtSpace = helper.getView(R.id.tv_group_item);
        ImageView imgLocation = helper.getView(R.id.iv_haslocationed);
        final ImageView imgSpaceCopy = helper.getView(R.id.ibt_copy_pen);
        String spaceId = space.getString(Device.SPID);
        //如果他有上级 则调整一下缩进和字体大小
        if (spaceItem.getParent() != null) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) txtSpace.getLayoutParams();
            layoutParams.leftMargin = AutoUtils.getPercentHeightSize(30);
            txtSpace.setLayoutParams(layoutParams);
            txtSpace.setTextSize(TypedValue.COMPLEX_UNIT_PX, AutoUtils.getPercentWidthSize(42));
            ((UnderLineLinearLayout) helper.itemView).setMarginLeft(AutoUtils.getPercentHeightSize(30));
        }
        imgLocation.setImageResource(R.mipmap.ic_no_location);
        imgLocation.setTag(null);
        if (!StringUtilsExt.isEmptys(space.getString("slat"), space.getString("slot"))
                && isOneDevice) {
            imgLocation.setImageResource(R.mipmap.ic_has_location);
            imgLocation.setTag(true);
            hasLocation = true;
        }

        TextView txtSpaceCount = helper.getView(R.id.txt_defect_count);
        txtSpaceCount.setVisibility(View.GONE);
        if (!spaceDefctCount.isEmpty()) {
            if (spaceDefctCount.keySet().contains(spaceId)) {
                txtSpaceCount.setText(String.valueOf(spaceDefctCount.get(spaceId)));
                txtSpaceCount.setVisibility(View.VISIBLE);
            }
        }


        // 间隔到位
        boolean spaceArrived;
        if (SystemConfig.isDevicePlaced() && hasLocation) {
            spaceArrived = handleDevicePlaced(spaceItem);
            imgLocation.setImageResource(spaceArrived ? R.mipmap.ic_already_location : R.mipmap.ic_has_location);
        } else if (hasLocation) {
            handleDevicePlaced(spaceItem);
            spaceArrived = isOneDevice && arriveSpaceIdList != null && arriveSpaceIdList.contains(spaceId);
            imgLocation.setImageResource(spaceArrived ? R.mipmap.ic_already_location : R.mipmap.ic_has_location);
        } else {
            handleDevicePlaced(spaceItem);
        }
        if (hasQrCodeSpids != null && hasQrCodeSpids.contains(spaceId)) {
            imgLocation.setImageResource(R.mipmap.ic_rfid_location);
            imgLocation.setTag(null);
        }
        hasLocation = false;

        //处理间隔抄录
        if ("Y".equals(space.getString("hasCopy"))) {
            imgSpaceCopy.setVisibility(View.VISIBLE);
            if ("Y".equals(space.getString("copyFinish"))) {
                imgSpaceCopy.setImageResource(R.drawable.ic_white_finish);
            } else {
                imgSpaceCopy.setImageResource(R.drawable.ic_white_unfinish);
            }
        } else {
            imgSpaceCopy.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(inspectionType) && inspectionType.contains(InspectionType.special.name()) && !SystemConfig.isSpecialInspectionNeedCopy()) {
            imgSpaceCopy.setVisibility(View.GONE);
        }

        //处理间隔名称
        String spacingName = space.getString(DeviceService.SPACING_NAME_KEY);

        if ("Y".equals(space.getString("hasImportant"))) {
            spacingName = "★" + spacingName;
        }
        //二次设备间隔不参与变色 因为设备本身已经是全称
        if (!TextUtils.isEmpty(keyWord)) {
            String spacePY = space.getString(DeviceService.SPACING_PY_KEY);
            int index = spacePY.indexOf(keyWord);
            if (-1 != index && index + keyWord.length() <= spacingName.length()) {
                String pyName = spacingName.substring(index, index + keyWord.length());
                spacingName = spacingName.replaceAll(pyName, "<font color=\"#FF0000\">" + pyName + "</font>");
            }
        }
        int childCount = spaceItem.getSubItems().size();
        spacingName += "(" + childCount + ")";
        txtSpace.setText(Html.fromHtml(spacingName));
        // 间隔点击
        helper.itemView.setOnClickListener(v -> {
            int pos = helper.getAdapterPosition();
            if (spaceItem.isExpanded()) {
                DeviceAdapter.this.collapse(pos);
            } else {
                DeviceAdapter.this.expand(pos);
            }
        });
        // 间隔长按
        helper.itemView.setOnLongClickListener(v -> {
            if (groupItemLongClickListener != null) {
                groupItemLongClickListener.onLongClick(v, space, position);
            }
            return false;
        });
        //抄录笔事件
        imgSpaceCopy.setOnClickListener(view -> {
            if (groupItemClickListener != null) {
                groupItemClickListener.onClick(view, space, position);
            }
        });

        imgLocation.setOnClickListener(v -> {
            if (groupItemClickListener != null) {
                groupItemClickListener.onClick(v, space, position);
            }
        });
        txtSpaceCount.setOnClickListener(v -> {
            if (groupItemClickListener != null) {
                groupItemClickListener.onClick(v, space, position);
            }
        });
    }


    private void convertDevice(BaseViewHolder helper, final DeviceItem item) {
        final int position = helper.getAdapterPosition();
        TextView txtDevice = helper.getView(R.id.tv_device_name);
        String deviceName = isSecondDevice ? item.getString(DeviceService.DEVICE_NAME_KEY) : item.getString(DeviceService.DEVICE_SHORT_NAME_KEY);
        String devicePY = item.getString(DeviceService.DEVICE_PY_KEY);
        String spaceId = item.getString(Device.SPID);
        String deviceId = item.getString(DeviceService.DEVICE_ID_KEY);
        RelativeLayout relativeLayout = helper.getView(R.id.rl_device_container);
        relativeLayout.setMinimumHeight(AutoUtils.getPercentHeightSizeBigger(105));
        relativeLayout.setMinimumWidth(AutoUtils.getPercentWidthSizeBigger(270));
        // 设备出现搜索关键字
        if (!TextUtils.isEmpty(keyWord)) {
            int index = devicePY.indexOf(keyWord);
            if (-1 != index && index + keyWord.length() <= deviceName.length()) {
                String pyName = deviceName.substring(index, index + keyWord.length());
                deviceName = deviceName.replaceAll(pyName, "<font color=\"#FF0000\">" + pyName + "</font>");
            }
        }
        //恢复Item默认状态
        txtDevice.setTextColor(context.getResources().getColor(R.color.color_2cc2ea));// 默认绿色
        helper.getView(R.id.rl_device_container).setBackgroundResource(R.drawable.xs_device_green_border_background_selector);
        helper.getView(R.id.tv_device_defect_count).setVisibility(View.GONE);// 不显示缺陷数量
        // 摇一摇动画切换
        if (shakeAnimation) {
            if (shakeSpaceId.equals(spaceId)) {//间隔所有的设备动画
                txtDevice.setTextColor(context.getResources().getColor(android.R.color.white));
                helper.getView(R.id.rl_device_container).setBackgroundResource(R.drawable.xs_device_green_background);
                startAnimation(helper.getView(R.id.rl_device_container), 0f, 1f);
            }
        } else if (shakeDeviceAnimation) {//具体某一个设备的动画
            if (shakeSpaceId.equalsIgnoreCase(spaceId) && shakeDeviceId.equalsIgnoreCase(deviceId)) {
                txtDevice.setTextColor(context.getResources().getColor(android.R.color.white));
                helper.getView(R.id.rl_device_container).setBackgroundResource(R.drawable.xs_device_green_background);
                startAnimation(helper.getView(R.id.rl_device_container), 0f, 1f);
            }
        } else {
            txtDevice.setTextColor(context.getResources().getColor(R.color.color_2cc2ea));
            helper.getView(R.id.rl_device_container).setBackgroundResource(R.drawable.xs_device_green_border_background_selector);
            // 显示抄录
            showCopyAndArrived(item, helper);
            // 缺陷等级及数量显示
            showDefect(item, helper);
            //显示重点设备
            showImportantDevice(item, helper);
        }
        // 跳转设备详情
        helper.getView(R.id.rl_device_container).setOnClickListener(v -> {
            if (deviceClickListener != null) {
                deviceClickListener.onClick(v, item, position);
            }
        });
        // 跳转设备抄录
        helper.getView(R.id.bt_copy_data).setOnClickListener(v -> {
            if (copyClickListener != null) {
                copyClickListener.onClick(v, item, position);
            }
        });

        helper.getView(R.id.rl_device_container).setOnLongClickListener(v -> {
            if (deviceItemLongClickListener != null) {
                deviceItemLongClickListener.onLongClick(v, item, position);
            }
            return false;
        });
        txtDevice.setText(Html.fromHtml(TextUtils.isEmpty(deviceName) ? "" : deviceName));
    }


    private void startAnimation(View view, float start, float dest) {
        AlphaAnimation animation = new AlphaAnimation(0.5f, 1f);
        animation.setDuration(2000);
        animation.setFillAfter(true);
        view.startAnimation(animation);
    }

    /**
     * 显示缺陷数量及等级
     */
    private void showDefect(DbModel item, BaseViewHolder holder) {
        if (null != defectmap && !defectmap.isEmpty()) {
            String deviceId = item.getString(DeviceService.DEVICE_ID_KEY);
            DefectInfo defectInfo = defectmap.get(deviceId);
            if (null != defectInfo) {
                TextView txtDefect = holder.getView(R.id.tv_device_defect_count);
                txtDefect.setVisibility(View.VISIBLE);
                txtDefect.setText(defectInfo.defectCount);
                String defectLevel = defectInfo.defectLevel;
                if (Config.CRISIS_LEVEL_CODE.equals(defectLevel)) {
                    txtDefect.setBackgroundResource(R.drawable.xs_device_defect_red_background);
                    holder.getView(R.id.rl_device_container).setBackgroundResource(R.drawable.xs_device_red_border_background_selector);
                } else if (Config.SERIOUS_LEVEL_CODE.equals(defectLevel)) {
                    txtDefect.setBackgroundResource(R.drawable.xs_device_defect_orange_background);
                    holder.getView(R.id.rl_device_container).setBackgroundResource(R.drawable.xs_device_orange_border_background_selector);
                } else {
                    txtDefect.setBackgroundResource(R.drawable.xs_device_defect_yellow_background);
                    holder.getView(R.id.rl_device_container).setBackgroundResource(R.drawable.xs_device_yellow_border_background_selector);
                }
            }
        }
    }

    /**
     * 显示抄录笔及抄录情况
     */
    private void showCopyAndArrived(DbModel item, BaseViewHolder holder) {
        ImageView ibCopy = holder.getView(R.id.ibt_copy_pen);
        View btCopy = holder.getView(R.id.bt_copy_data);
        TextView txtDeviceName = holder.getView(R.id.tv_device_name);
        RelativeLayout relativeLayout = holder.getView(R.id.rl_device_container);
        if ("Y".equals(item.getString("arrived"))) {
            txtDeviceName.setTextColor(context.getResources().getColor(R.color.xs_global_text_color));
            relativeLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.xs_device_black_border_background_selector));
        }
        if ("Y".equals(item.getString("hasCopy"))) {
            ibCopy.setVisibility(View.VISIBLE);
            btCopy.setVisibility(View.VISIBLE);
            ibCopy.setImageResource("Y".equals(item.getString("isCopy")) ? R.drawable.xs_ic_black_finish : R.drawable.ic_c2c2ea_unfinish);
        } else {
            ibCopy.setVisibility(View.INVISIBLE);
            btCopy.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(inspectionType) && inspectionType.contains(InspectionType.special.name()) && !SystemConfig.isSpecialInspectionNeedCopy()) {
            ibCopy.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 设置重点设备
     */
    private void showImportantDevice(DbModel item, BaseViewHolder holder) {
        ImageView v = holder.getView(R.id.iv_important_device);
        if ("Y".equals(item.getString(Device.IS_IMPORTANT))) {
            v.setVisibility(View.VISIBLE);
            v.setSelected("Y".equals(item.getString("arrived")));
        } else {
            v.setVisibility(View.GONE);
        }
    }

    /**
     * 处理间隔到位和间隔下的设备到位信息
     *
     * @param node
     * @return
     */
    public boolean handleDevicePlaced(SpaceItem node) {
        boolean result = true;
        List<DeviceItem> models = node.getSubItems();
        boolean spaceHasCopy = false;
        boolean spaceCopyFinish = true;
        boolean spaceHasImportant = false;
        for (DbModel model : models) {
            boolean arrived;
            String deviceId = model.getString(DeviceService.DEVICE_ID_KEY);
            PlacedDevice placed = arriveDeviceList.get(deviceId);
            if (placed == null) {
                arrived = false;
            } else {
                arrived = true;
                model.add("placedWay", placed.placed_way);
                model.add("placedId", placed.id);
            }
            //重点设备必须拍照才能算到位
            if (Device.isImportant(model)) {
                if (SystemConfig.isMustPicImportantDevice()) {
                    arrived = arrived && (placed != null && "photo".equals(placed.placed_way));
                }
                spaceHasImportant = true;
            }
            //抄录设备必须抄录之后才变黑
            if (copyDeviceIdList.contains(deviceId)) {
                List<String> copyedDeviceList = copyedMap.get(model.getString(Device.SPID));
                if (null != copyedDeviceList && copyedDeviceList.contains(deviceId)) {
                    arrived = arrived && true;
                    model.add("isCopy", "Y");
                } else {
                    model.add("isCopy", "N");
                    spaceCopyFinish = false;
                    arrived = false;
                }
                model.add("hasCopy", "Y");
                spaceHasCopy = true;
            } else {
                model.add("hasCopy", "N");
            }
            if (SystemConfig.isDevicePlaced()) {
                model.add("arrived", arrived ? "Y" : "N");
            } else {
                model.add("arrived", model.getString("isCopy"));
            }
            result = result && arrived;
        }
        DbModel spacing = node.spacing;
        spacing.add("hasCopy", spaceHasCopy ? "Y" : "N");
        spacing.add("copyFinish", spaceCopyFinish ? "Y" : "N");
        spacing.add("hasImportant", spaceHasImportant ? "Y" : "N");
        return result;
    }

    public void setCurrentFunctionMode(String currentFunctionMode) {
        isOneDevice = PMSDeviceType.one.equals(currentFunctionMode);
        isSecondDevice = PMSDeviceType.second.equals(currentFunctionMode);
    }

    public void setGroupItemClickListener(ItemClickListener<DbModel> groupItemClickListener) {
        this.groupItemClickListener = groupItemClickListener;
    }

    public void setGroupItemListener(ItemClickListener<SpacingGroup> groupItemListener) {
        this.groupItemListener = groupItemListener;
    }

    public void setGroupItemLongClickListener(ItemLongClickListener<DbModel> groupItemLongClickListener) {
        this.groupItemLongClickListener = groupItemLongClickListener;
    }

    public void setDeviceItemLongClickListener(ItemLongClickListener<DbModel> deviceItemLongClickListener) {
        this.deviceItemLongClickListener = deviceItemLongClickListener;
    }

    public void setDeviceClickListener(ItemClickListener<DbModel> deviceClickListener) {
        this.deviceClickListener = deviceClickListener;
    }

    public void setCopyClickListener(ItemClickListener<DbModel> copyClickListener) {
        this.copyClickListener = copyClickListener;
    }

    public void setArriveSpaceIdList(Set<String> arriveSpaceIdList) {
        this.arriveSpaceIdList = arriveSpaceIdList;
        notifyDataSetChanged();
    }

    public void setArriveDeviceList(HashMap<String, PlacedDevice> arriveDeviceList) {
        this.arriveDeviceList = arriveDeviceList;
    }


    public void setDefectMap(HashMap<String, DefectInfo> defectMap) {
        this.defectmap = defectMap;
        spaceDefctCount.clear();
        Collection<DefectInfo> defectInfos = defectMap.values();
        if (!defectInfos.isEmpty()) {
            for (DefectInfo defectInfo : defectInfos) {
                if (!TextUtils.isEmpty(defectInfo.spid) && spaceDefctCount.containsKey(defectInfo.spid)) {
                    spaceDefctCount.put(defectInfo.spid, spaceDefctCount.get(defectInfo.spid) + Integer.valueOf(defectInfo.defectCount));
                } else if (!TextUtils.isEmpty(defectInfo.spid)) {
                    spaceDefctCount.put(defectInfo.spid, Integer.valueOf(defectInfo.defectCount));
                }
            }
        }
    }

    public void setCopyDeviceIdList(HashSet<String> copyDeviceIdList) {
        this.copyDeviceIdList = copyDeviceIdList;
    }


    public void setCopySNWSD(HashSet<ReportSnwsd> groupSnwsds) {
        this.groupSnwsds.clear();
        for (ReportSnwsd snwsd : groupSnwsds) {
            if (!TextUtils.isEmpty(snwsd.sd) && !TextUtils.isEmpty(snwsd.wd)) {
                this.groupSnwsds.add(snwsd.groupID);
            }
        }
    }

    public void setCurrentInspection(String currentInspection) {
        this.currentInspection = currentInspection;
    }

    public void setCopyDeviceMap(Map<String, List<String>> copyDeviceMap) {
        this.copyedMap = copyDeviceMap;
    }

    public void setShakeAnimation(boolean b, String spaceId) {
        shakeAnimation = b;
        shakeSpaceId = spaceId;
    }

    public void setShakeAnimationDevice(boolean b, String spaceId1, String rfId) {
        shakeDeviceAnimation = b;
        shakeSpaceId = spaceId1;
        shakeDeviceId = rfId;
    }

    public void setCurrentInspectionType(String inspectionType) {
        this.inspectionType = inspectionType;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    @Override
    public int expand(int position) {
        MultiItemEntity t = getData().get(position);
        if (t instanceof SpaceGroupItem) {
            return super.expand(position);
        }
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

    public DbModel getLastlySpace() {
        return lastExpandIndex != null ? ((SpaceItem) lastExpandIndex).spacing : null;
    }

    public void setShowOnly(boolean showOnly) {
        this.isOnlyExpandOne = showOnly;
    }

    public void setHasQrCodeSpids(HashSet<String> spaceIds) {
        if (hasQrCodeSpids == null) {
            hasQrCodeSpids = new HashSet<>();
        }
        hasQrCodeSpids.clear();
        hasQrCodeSpids = spaceIds;

    }
}
