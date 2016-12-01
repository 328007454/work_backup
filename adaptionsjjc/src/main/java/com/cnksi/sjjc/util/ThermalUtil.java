package com.cnksi.sjjc.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.flir.flironesdk.Device;
import com.flir.flironesdk.Frame;
import com.flir.flironesdk.FrameProcessor;
import com.flir.flironesdk.RenderedImage;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.EnumSet;

/**
 * 红外热成像工具类<br/>
 *
 * @author luoxy
 */
public class ThermalUtil {
    /**
     * 热成像事件
     */
    public interface ThermalListener {
        /**
         * 温度发生改变
         *
         * @param avgTemperature 平均温度
         * @param maxTemperature 最高温度
         */
        void temperatureChange(double avgTemperature, double maxTemperature);

        /**
         * 设备电量变更
         *
         * @param percentage 设备当前电量百分比
         */
        void powerChange(int percentage);

        /**
         * 当前热成像bitmap
         *
         * @param bitMap
         */
        void onFrameRendered(Bitmap bitMap);

        /**
         * 捕获当前图像成功 最多两张<br/>
         * 0-热成像图
         * 1-正常图
         *
         * @param path 图像存储路径
         */
        void captureSuccess(String... path);

        /**
         * 找到设备
         */
        void findDevicesSuccess(Device flirOneDevice);
    }

    private ThermalListener thermalListener;

    //设备接口代理
    private Device.Delegate deviceDelegate;
    //电源代理
    private Device.PowerUpdateDelegate powerDelegate;

    private Device.StreamDelegate streamDelegate;

    private FrameProcessor.Delegate processorDelegate, calculateDelegate;

    private FrameProcessor frameProcessor;

    private String captureFolder;

    private boolean autoCalculate;

    private Device flirOneDevice;

    private RenderedImage currentRenderImage;

    private Context context;

    private static ThermalUtil instance;


    public static ThermalUtil getInstance() {
        if (null == instance)
            instance = new ThermalUtil();
        return instance;
    }

    /**
     *
     */
    private ThermalUtil() {

        deviceDelegate = new Device.Delegate() {
            @Override
            public void onTuningStateChanged(Device.TuningState tuningState) {

            }

            @Override
            public void onAutomaticTuningChanged(boolean b) {

            }

            @Override
            public void onDeviceConnected(Device device) {
                Log.e("TAG", "find the flirOneDevice");
                flirOneDevice = device;
                startFrameStream();
                if (thermalListener != null)
                    thermalListener.findDevicesSuccess(flirOneDevice);
            }

            @Override
            public void onDeviceDisconnected(Device device) {
                flirOneDevice = null;
            }
        };
        powerDelegate = new Device.PowerUpdateDelegate() {
            @Override
            public void onBatteryChargingStateReceived(Device.BatteryChargingState batteryChargingState) {

            }

            @Override
            public void onBatteryPercentageReceived(byte b) {
                if (null != thermalListener)
                    thermalListener.powerChange((int) b);
            }
        };
        streamDelegate = new Device.StreamDelegate() {
            @Override
            public void onFrameReceived(Frame frame) {
                frameProcessor.processFrame(frame);
            }
        };

        processorDelegate = new FrameProcessor.Delegate() {
            @Override
            public void onFrameProcessed(RenderedImage renderedImage) {
                currentRenderImage = renderedImage;
                if (null != thermalListener)
                    thermalListener.onFrameRendered(currentRenderImage.getBitmap());
                if (autoCalculate) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            calculateTemperature();
                        }
                    }).start();
                }

            }
        };
        calculateDelegate = new FrameProcessor.Delegate() {
            @Override
            public void onFrameProcessed(final RenderedImage renderedImage) {
                if (renderedImage.imageType() == RenderedImage.ImageType.ThermalRadiometricKelvinImage) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            double averageTemp = 0;
                            double maxTemp = 0;
                            short[] shortPixels = new short[renderedImage.pixelData().length / 2];
                            ByteBuffer.wrap(renderedImage.pixelData()).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shortPixels);
                            for (int i = 0; i < shortPixels.length; i++) {
                                averageTemp += (((int) shortPixels[i]) - averageTemp) / ((double) i + 1);
                                maxTemp = Math.max((int) shortPixels[i], maxTemp);
                            }
                            double averageC = (averageTemp / 100) - 273.15;
                            double maxC = (maxTemp / 100) - 273.15;
                            if (null != thermalListener)
                                thermalListener.temperatureChange(averageC, maxC);
                        }
                    }).start();
                }
            }
        };
    }


    /**
     * 初始化成像处理器,启动调用
     *
     * @return
     */
    public ThermalUtil initProcessor(Context context) {
        this.context = context;
        frameProcessor = new FrameProcessor(context, processorDelegate, EnumSet.of(RenderedImage.ImageType.BlendedMSXRGBA8888Image));
        return this;
    }

    /**
     * 设置热成像事件
     *
     * @param thermalListener
     * @return
     */
    public ThermalUtil setThermalListener(ThermalListener thermalListener) {
        this.thermalListener = thermalListener;
        return this;
    }

    /**
     * 设置是否自动计算温度
     *
     * @param autoCalculate
     * @return
     */
    public ThermalUtil setAutoCalculate(boolean autoCalculate) {
        this.autoCalculate = autoCalculate;
        return this;
    }

    /**
     * 设置截图存储文件夹
     *
     * @param captureFolder
     * @return
     */
    public ThermalUtil setCaptureFolder(String captureFolder) {
        this.captureFolder = captureFolder;
        return this;
    }

    /**
     * 扫描设备,此方法写在onResume方法中
     */
    public void startDevice() {
        try {
            Device.startDiscovery(context, deviceDelegate);
        }catch (IllegalStateException e){
            e.printStackTrace();
        }
    }

    /**
     * 断开设备连接,写在onPause,onStop等方法中
     */
    public void stopDevice() {
        stopFrameStream();
        Device.stopDiscovery();
    }

    /**
     * 销毁当前设备
     */
    public void destroy() {
        this.thermalListener = null;
    }

    /**
     * 开启捕捉当前frame
     */
    public void startFrameStream() {
        if (null != flirOneDevice) {
//            flirOneDevice.setAutomaticTuning(true);
//            flirOneDevice.performTuning();
            flirOneDevice.setPowerUpdateDelegate(powerDelegate);
            flirOneDevice.startFrameStream(streamDelegate);
        }
    }

    /**
     * 停止捕捉当前frame
     */
    public void stopFrameStream() {
        if (null != flirOneDevice)
            flirOneDevice.stopFrameStream();
    }


    /**
     * 对当前frame拍照<br/>
     * note:调用此方法钱请设置{@link #setThermalListener(ThermalListener)}
     */
    public void takePicture(String fileName,String fileName1) {
        if (null != currentRenderImage) {
            String path;
            if (null != captureFolder)
                path = captureFolder;
            else
                path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
//            String datePatten = sdf.format(new Date());
    //        final String fileName = "thermal-" + datePatten + ".jpg";
           // final String fileName1 = "normal-" + datePatten + ".jpg";
            File captureFile = new File(path);
            if (!captureFile.exists())
                captureFile.mkdirs();
//           final String imagePath = path + "/" + fileName;
            final String imagePath1 = path + "/" + fileName1;
            try {
 //               currentRenderImage.getFrame().save(new File(imagePath), RenderedImage.Palette.Iron, RenderedImage.ImageType.BlendedMSXRGBA8888Image);
//                MediaScannerConnection.scanFile(context,
//                        new String[]{path + "/" + fileName}, null,
//                        new MediaScannerConnection.OnScanCompletedListener() {
//                            @Override
//                            public void onScanCompleted(String path, Uri uri) {
//                                Log.i("ExternalStorage", "Scanned " + path + ":");
//                                Log.i("ExternalStorage", "-> uri=" + uri);
//                            }
//
//                        });
                calculateTemperature();
                currentRenderImage.getFrame().save(new File(imagePath1), RenderedImage.Palette.Arctic, RenderedImage.ImageType.VisualYCbCr888Image);
//                MediaScannerConnection.scanFile(context,
//                        new String[]{path + "/" + fileName}, null,
//                        new MediaScannerConnection.OnScanCompletedListener() {
//                            @Override
//                            public void onScanCompleted(String path, Uri uri) {
//                                Log.i("ExternalStorage", "Scanned " + path + ":");
//                                Log.i("ExternalStorage", "-> uri=" + uri);
//                            }
//
//                        });
                if (null != thermalListener)
                    thermalListener.captureSuccess(fileName, fileName1);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 计算当前frame温度<br/>
     * note:调用此方法钱请设置{@link #setThermalListener(ThermalListener)}
     */
    public void calculateTemperature() {
        if (null != currentRenderImage) {
            FrameProcessor calculateProcessor = new FrameProcessor(context, calculateDelegate, EnumSet.of(currentRenderImage.imageType(), RenderedImage.ImageType.ThermalRadiometricKelvinImage));
            calculateProcessor.setImagePalette(currentRenderImage.palette());
            calculateProcessor.processFrame(currentRenderImage.getFrame());
        }
    }

//    @Override
//    public boolean handleMessage(Message msg) {
//        if (null != thermalListener) {
//            switch (msg.what) {
//                case POWER_CHANGE:
//                    thermalListener.powerChange((Integer) msg.obj);
//                    break;
//                case FRAME_RENDER:
//                    thermalListener.onFrameRendered(currentRenderImage.getBitmap());
//                    break;
//                case TEMPERATURE_CHANGE:
//                    String[] array = ((String) msg.obj).split(",");
//                    thermalListener.temperatureChange(Double.valueOf(array[0]), Double.valueOf(array[1]));
//                    break;
//                case SAVE_FRAME:
//
//                    thermalListener.captureSuccess((String) msg.obj);
//                    break;
//            }
//        }
//        return false;
//    }

}
