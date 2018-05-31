package com.cnksi.common.utils;

import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.common.enmu.SpaceType;
import com.cnksi.common.model.SpacingGroup;
import com.cnksi.common.model.vo.SpaceGroupItem;
import com.cnksi.common.model.vo.SpaceItem;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * @author Wastrel
 * @version 1.0
 * @date 2018/5/30 16:16
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class Functions {

    /**
     * 组装二次设备的三级列表
     *
     * @param data
     * @param spaceGroupMap
     */
    public static void buildSpaceTreeData(List<MultiItemEntity> data, Map<String, SpaceGroupItem> spaceGroupMap) {
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
            Collections.sort(data, (o1, o2) -> {
                int s1 = ((SpaceGroupItem) o1).group.sort;
                int s2 = ((SpaceGroupItem) o2).group.sort;
                return s1 - s2;
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
