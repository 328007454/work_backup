package com.cnksi.bdzinspection.fragment;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.bdzinspection.adapter.DeviceAdapter;
import com.cnksi.common.daoservice.DeviceService;
import com.cnksi.common.model.Spacing;
import com.cnksi.common.model.vo.SpaceGroupItem;
import com.cnksi.common.model.vo.SpaceItem;
import com.cnksi.common.utils.Functions;
import com.cnksi.core.utils.ToastUtils;

import org.xutils.db.table.DbModel;

import java.util.List;

/**
 * @author Wastrel
 * @version 1.0
 * @author wastrel
 * @date 2018/1/31 11:11
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class DeviceHandleFunctions extends Functions {

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
        handler.postDelayed(() -> {
            adapter.setShakeAnimation(false, null);
            adapter.notifyDataSetChanged();
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
        adapter.setShakeAnimationDevice(true, spid, model.getString(DeviceService.DEVICE_ID_KEY));
        if (index[0] > -1) {
            adapter.expand(index[0]);
        }
        adapter.expand(index[1]);
        handler.postDelayed(() -> {
            adapter.setShakeAnimationDevice(false, null, null);
            adapter.notifyDataSetChanged();
        }, 2000);
    }


}