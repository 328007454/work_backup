package com.cnksi.bdzinspection.fragment;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.baidu.location.BDLocation;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.cnksi.bdloc.DistanceUtil;
import com.cnksi.bdloc.LatLng;
import com.cnksi.bdzinspection.adapter.DeviceAdapter;
import com.cnksi.bdzinspection.daoservice.PlacedDeviceService;
import com.cnksi.bdzinspection.daoservice.PlacedService;
import com.cnksi.bdzinspection.model.Placed;
import com.cnksi.bdzinspection.model.PlacedDevice;
import com.cnksi.common.model.vo.SpaceItem;
import com.cnksi.common.Config;
import com.cnksi.common.SystemConfig;
import com.cnksi.common.daoservice.DeviceService;
import com.cnksi.common.daoservice.SpacingService;
import com.cnksi.common.model.Device;
import com.cnksi.common.model.Spacing;
import com.cnksi.core.common.ExecutorManager;
import com.cnksi.core.utils.PreferencesUtils;

import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.cnksi.common.model.vo.SpaceGroupItem.SPACE_ITEM;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/1/19 19:02
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class ArriveCheckHelper {
    public List<Spacing> needCheckSpacingList = new ArrayList<>();
    public List<Device> needCheckDeviceList = new ArrayList<>();
    public HashMap<String, Placed> arrivedPlaces = new HashMap<>();
    public HashMap<String, PlacedDevice> arrivedDevices = new HashMap<>();

    float DISTANCE;
    static  boolean isPrepare = false;
    Placed finalPlaced = null;

    private long lastCheckTime;
    private List<LatLng> cacheLatLng = new ArrayList<>();
    private Activity currentActivity;
    private String currentReportId;
    private String bdzId;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private DeviceAdapter deviceAdapter;
    private String currentFunctionModel;

    public ArriveCheckHelper(Activity activity, DeviceAdapter adapter, String reportId, final String currentInspectionType, final String bdzId, final String mode) {
        currentActivity = activity;
        this.currentReportId = reportId;
        deviceAdapter = adapter;
        currentFunctionModel = mode;
        this.bdzId = bdzId;
        DISTANCE = PreferencesUtils.get( Config.COPY_DISTANCE_KEY, 100f);
        if (SystemConfig.isDevicePlaced()) {
            ExecutorManager.executeTask(() -> {
                List<PlacedDevice> result = PlacedDeviceService.getInstance().findPlacedDevice(currentReportId);
                if (result != null) {
                    for (PlacedDevice placed : result) {
                        arrivedDevices.put(placed.deviceid, placed);
                    }
                }
                List<Device> devices = DeviceService.getInstance().findDeviceByType(bdzId, mode);
                Iterator<Device> iterator = devices.iterator();
                while (iterator.hasNext()) {
                    Device device = iterator.next();
                    //重点设备并且必须拍照到位的模式移除GPS判断
                    if (arrivedDevices.containsKey(device.deviceid) || (device.isImportant() && SystemConfig.isMustPicImportantDevice())) {
                        iterator.remove();
                    }
                }
                needCheckDeviceList = devices;
                isPrepare = true;
                mHandler.post(() -> deviceAdapter.setArriveDeviceList(ArriveCheckHelper.this.getArrivedDevices()));
            });
        } else {
            ExecutorManager.executeTask(() -> {
                if (Config.PLACED_BY_COPY) {
                    // 抄录为准判断间隔到位，找出不含抄录间隔
                    needCheckSpacingList = SpacingService.getInstance().findSpacing(bdzId, "one", false, currentInspectionType);
                } else {
                    needCheckSpacingList = SpacingService.getInstance().findSpacing(bdzId, "one");
                }
                if (needCheckSpacingList == null) {
                    needCheckSpacingList = new ArrayList<>();
                }

                List<Placed> result = PlacedService.getInstance().findPlacedSpace(currentReportId);
                if (result != null) {
                    for (Placed placed : result) {
                        arrivedPlaces.put(placed.spId, placed);
                    }
                }
                isPrepare = true;
                mHandler.post(() -> deviceAdapter.setArriveSpaceIdList(ArriveCheckHelper.this.getArrivedSpids()));
            });
        }
    }

    public void refreshArrived() {
        if (!isPrepare) {
            return;
        }
        ExecutorManager.executeTask(() -> {
            arrivedPlaces.clear();
            List<Placed> result = PlacedService.getInstance().findPlacedSpace(currentReportId);
            if (result != null) {
                for (Placed placed : result) {
                    arrivedPlaces.put(placed.spId, placed);
                }
            }
            arrivedDevices.clear();
            List<PlacedDevice> resultDevice = PlacedDeviceService.getInstance().findPlacedDevice(currentReportId);
            if (result != null) {
                for (PlacedDevice placed : resultDevice) {
                    arrivedDevices.put(placed.deviceid, placed);
                }
            }
            mHandler.post(() -> {
                deviceAdapter.setArriveDeviceList(ArriveCheckHelper.this.getArrivedDevices());
                deviceAdapter.setArriveSpaceIdList(ArriveCheckHelper.this.getArrivedSpids());
                deviceAdapter.notifyDataSetChanged();
            });
            ArriveCheckHelper.this.handleSpaceArrivedData();
        });
    }

    public synchronized void checkArrived(final BDLocation location) {
        if (!Config.PmsDeviceType.one.name().equals(currentFunctionModel)) {
            return;
        }
        cacheLatLng.add(new LatLng(location));
        if (!isPrepare) {
            return;
        }
        if (System.currentTimeMillis() - lastCheckTime < 5000 && cacheLatLng.size() < 5) {
            return;
        }
        lastCheckTime = System.currentTimeMillis();
        final LatLng highest = DistanceUtil.getHighest(cacheLatLng);
        cacheLatLng.clear();
        if (SystemConfig.isDevicePlaced()) {
            checkArrivedDevice(highest);
        } else {
            checkArrivedSpace(highest);
        }
    }

    private void checkArrivedDevice(final LatLng location) {
        ExecutorManager.executeTask(() -> {
            final List<PlacedDevice> saveList = new ArrayList<>();
            for (Device device : needCheckDeviceList) {
                LatLng deviceLocation = LatLng.valueOf(device.latitude, device.longitude);
                if (deviceLocation == null) {
                    continue;
                }
                double distance = DistanceUtil.getDistance(location, deviceLocation);
                //距离范围太远
                if (distance > DISTANCE) {
                    continue;
                }
                PlacedDevice placed = arrivedDevices.get(device.deviceid);
                if (placed == null) {
                    placed = PlacedDevice.create(device, currentReportId);
                    arrivedDevices.put(placed.deviceid, placed);
                    placed.setPlacedWayHighest("gps");
                    placed.latitude = String.valueOf(deviceLocation.lat);
                    placed.longtitude = String.valueOf(deviceLocation.lng);
                    saveList.add(placed);
                }
            }
            ArriveCheckHelper.this.saveDeviceArrived(saveList);
        });
    }

    private void checkArrivedSpace(final LatLng location) {
        ExecutorManager.executeTask(() -> {
            List<Placed> saveList = new ArrayList<>();
            for (Spacing spacing : needCheckSpacingList) {
                LatLng spaceLocation = LatLng.valueOf(spacing.latitude, spacing.longitude);
                if (spaceLocation == null) {
                    continue;
                }
                double distance = DistanceUtil.getDistance(location, spaceLocation);
                //距离范围太远
                if (distance > DISTANCE) {
                    continue;
                }
                Placed placed = arrivedPlaces.get(spacing.spid);
                if (placed == null) {
                    placed = new Placed(currentReportId, bdzId, spacing.spid, spacing.name, 1, location.lat, location.lng);
                    arrivedPlaces.put(placed.spId, placed);
                    saveList.add(placed);
                }
            }
            if (saveList.size() > 0) {
                try {
                    PlacedService.getInstance().saveOrUpdate(saveList);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                Iterator<Spacing> iterator = needCheckSpacingList.iterator();
                while (iterator.hasNext()) {
                    if (arrivedPlaces.containsKey(iterator.next().spid)) {
                        iterator.remove();
                    }
                }
                mHandler.post(() -> deviceAdapter.setArriveSpaceIdList(ArriveCheckHelper.this.getArrivedSpids()));
            }
        });
    }

    synchronized public Set<String> getArrivedSpids() {
        return arrivedPlaces.keySet();
    }

    synchronized public HashMap<String, PlacedDevice> getArrivedDevices() {
        return arrivedDevices;
    }

    public void saveLocation(DbModel model, BDLocation location, boolean isSpace) {
        final String lat = String.valueOf(location.getLatitude());
        final String lng = String.valueOf(location.getLongitude());
        // 对间隔定了位，则将间隔下没有位置信息的设备的位置信息设置为和间隔相同的位置信息 并且直接标记为到位状态
        if (isSpace) {
            model.add("slot", lng);
            model.add("slat", lat);
            final String spid = model.getString(Spacing.SPID);
            finalPlaced = null;
            //设备到位模式下 直接标记到位不适用。
            if (!arrivedPlaces.containsKey(spid) && !SystemConfig.isDevicePlaced()) {
                Placed placed = new Placed(currentReportId, bdzId, spid, model.getString("spacingName"), 1, location.getLatitude(), location.getLongitude());
                arrivedPlaces.put(spid, placed);
                finalPlaced = placed;
            }
            deviceAdapter.setArriveSpaceIdList(getArrivedSpids());
            deviceAdapter.notifyDataSetChanged();
            ExecutorManager.executeTask(() -> {
                if (null != finalPlaced) {
                    PlacedService.getInstance().saveOrUpdate(finalPlaced);
                }
                SpacingService.getInstance().updateSpacingLocationInfo(spid, lat, lng);
                DeviceService.getInstance().updateDeviceLocationInfo(spid, lat, lng);
            });
        } else {
            model.add(Device.LATITUDE, String.valueOf(location.getLatitude()));
            model.add(Device.LONGITUDE, String.valueOf(location.getLongitude()));
            DeviceService.getInstance().updateDeviceLocationInfo(new Device(model.getString("deviceId"), lat, lng));
            for (Device device : needCheckDeviceList) {
                if (model == null) {
                    continue;
                }
                if (device.deviceid.equals(model.getString("deviceId"))) {
                    device.latitude = String.valueOf(location.getLatitude());
                    device.longitude = String.valueOf(location.getLongitude());
                    if (!arrivedDevices.containsKey(device.deviceid)) {
                        PlacedDevice placedDevice = PlacedDevice.create(device, currentReportId);
                        arrivedDevices.put(device.deviceid, placedDevice);
                        saveDeviceArrived(Arrays.asList(placedDevice));
                    }
                    break;
                }
            }
        }
    }

    private void saveDeviceArrived(List<PlacedDevice> saveList) {
        if (saveList == null || saveList.size() == 0) {
            return;
        }
        try {
            PlacedDeviceService.getInstance().saveOrUpdate(saveList);
        } catch (DbException e) {
            e.printStackTrace();
        }
        Iterator<Device> iterator = needCheckDeviceList.iterator();
        while (iterator.hasNext()) {
            if (arrivedDevices.containsKey(iterator.next().deviceid)) {
                iterator.remove();
            }
        }
        mHandler.post(() -> {
            deviceAdapter.setArriveDeviceList(getArrivedDevices());
            deviceAdapter.notifyDataSetChanged();
        });
    }

    /**
     * 在设备到位模式下 间隔下所有设备到位则将间隔标记为到位
     */
    public void handleSpaceArrivedData() {
        if (SystemConfig.isDevicePlaced() && "one".equals(currentFunctionModel)) {
            List<Placed> saveList = new ArrayList<>();
            for (MultiItemEntity node : deviceAdapter.getData()) {
                if (node.getItemType() == SPACE_ITEM && deviceAdapter.handleDevicePlaced((SpaceItem) node)) {
                    DbModel model = ((SpaceItem) node).spacing;
                    String spid = model.getString("spid");
                    if (arrivedPlaces.containsKey(spid)) {
                        continue;
                    } else {
                        Placed placed = new Placed(currentReportId, bdzId, spid, model.getString("sname"), 1, -1, -1);
                        saveList.add(placed);
                    }
                }
            }
            try {
                PlacedService.getInstance().saveOrUpdate(saveList);
            } catch (DbException e) {
                e.printStackTrace();
            }
            for (Placed placed : saveList) {
                arrivedPlaces.put(placed.spId, placed);
            }
        }
    }
}
