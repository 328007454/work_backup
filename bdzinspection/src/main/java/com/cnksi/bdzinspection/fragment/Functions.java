package com.cnksi.bdzinspection.fragment;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.bdzinspection.adapter.DeviceAdapter;
import com.cnksi.bdzinspection.model.SpacingGroup;
import com.cnksi.bdzinspection.model.tree.SpaceGroupItem;
import com.cnksi.bdzinspection.model.tree.SpaceItem;
import com.cnksi.common.enmu.SpaceType;
import com.cnksi.common.model.Spacing;
import com.cnksi.core.utils.ToastUtils;

import org.xutils.db.table.DbModel;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/1/31 11:11
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class Functions {

    public static void animationMethodSpace(String spid, List<MultiItemEntity> data, final DeviceAdapter adapter, Handler handler, RecyclerView recyclerView) {
        int[] index = findSpaceIndex(spid, data);
        if (index[1] < 0) {
            ToastUtils.showMessage("没有找到对应的间隔");
            return;
        }
        adapter.setShakeAnimation(true, spid);
        if (index[0] > -1) {
            adapter.expand(index[0]);
        }
        adapter.expand(index[1]);
        if (-1 != index[1]) {
            recyclerView.scrollToPosition(index[1]);
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.setShakeAnimation(false, null);
                adapter.notifyDataSetChanged();
            }
        }, 2000);
    }

    /**
     * 寻找list
     *
     * @param spid
     * @param data
     * @return
     */
    public static int[] findSpaceIndex(String spid, List<MultiItemEntity> data) {
        int groupIndex = -1;
        int index = -1;
        int scrollOffset = 0;
        for (int i = 0; i < data.size(); i++) {
            MultiItemEntity entity = data.get(i);
            if (entity instanceof SpaceGroupItem && !((SpaceGroupItem) entity).isExpanded()) {
                int t = ((SpaceGroupItem) entity).findChildPosition(spid);
                if (t > -1) {
                    groupIndex = i;
                    index = i + t + 1;
                    scrollOffset = ((SpaceGroupItem) entity).getSubItem(t).getSubSize();
                    break;
                }
            } else if (entity instanceof SpaceItem) {
                if (((SpaceItem) entity).getSpid().equals(spid)) {
                    index = i;
                    scrollOffset = ((SpaceItem) entity).getSubSize();
                    break;
                }
            }
        }
        return new int[]{groupIndex, index, scrollOffset};
    }


    public static void animationDeviceMethod(DbModel model, List<MultiItemEntity> data, final DeviceAdapter adapter, Handler handler) {
        //真正的间隔id
        String spid = model.getString(Spacing.SPID);
        int[] index = findSpaceIndex(spid, data);
        if (index[1] < 0) {
            ToastUtils.showMessage("没有找到对应的间隔");
            return;
        }
        adapter.setShakeAnimationDevice(true, spid, model.getString("deviceId"));
        if (index[0] > -1) {
            adapter.expand(index[0]);
        }
        adapter.expand(index[1]);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.setShakeAnimationDevice(false, null, null);
                adapter.notifyDataSetChanged();
            }
        }, 2000);
    }


    /**
     * 组装二次设备的三级列表
     *
     * @param data
     * @param spaceGroupMap
     */
    public static void buildTreeData(List<MultiItemEntity> data, Map<String, SpaceGroupItem> spaceGroupMap) {
        HashSet<SpaceGroupItem> rs = new HashSet<>();
        SpaceGroupItem noGroupSpace = new SpaceGroupItem(new SpacingGroup("未分组间隔"));
        SpaceGroupItem noGroupPG = new SpaceGroupItem(new SpacingGroup("未分小室屏柜"));
        //清除之前的绑定信息
        for (SpaceGroupItem spaceGroupItem : spaceGroupMap.values()) {
            spaceGroupItem.setExpanded(false);
            spaceGroupItem.clear();
        }
        for (MultiItemEntity entity : data) {
            SpaceItem model = (SpaceItem) entity;
            String groupId = model.spacing.getString("group_id");
            if (TextUtils.isEmpty(groupId)) {
                if (SpaceType.isCabinet(model.spacing.getString("spaceType"))) {
                    noGroupPG.addSubItem(model);
                } else {
                    noGroupSpace.addSubItem(model);
                }
            } else {
                SpaceGroupItem t = spaceGroupMap.get(groupId);
                if (t == null) {
                    continue;
                } else {
                    t.addSubItem(model);
                }
                rs.add(t);
            }
        }
        data.clear();
        data.addAll(rs);
        if (data.size() > 1) {
            //小室排序
            Collections.sort(data, new Comparator<MultiItemEntity>() {
                @Override
                public int compare(MultiItemEntity o1, MultiItemEntity o2) {
                    int s1 = ((SpaceGroupItem) o1).group.sort;
                    int s2 = ((SpaceGroupItem) o2).group.sort;
                    return s1 - s2;
                }
            });
        }
        if (noGroupPG.getSubSize() > 0) {
            data.add(noGroupPG);
        }
        if (noGroupSpace.getSubSize() > 0) {
            data.add(noGroupSpace);
        }
    }

}