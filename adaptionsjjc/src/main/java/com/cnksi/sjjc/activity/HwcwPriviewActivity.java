package com.cnksi.sjjc.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnksi.core.utils.BitmapUtil;
import com.cnksi.core.utils.NumberUtil;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.util.FunctionUtil;
import com.flir.flironesdk.Device;
import com.flir.flironesdk.Frame;
import com.flir.flironesdk.FrameProcessor;
import com.flir.flironesdk.RenderedImage;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.Locale;


/**
 * Created by luoxy on 16/5/6.
 */
public class HwcwPriviewActivity extends BaseActivity implements Handler.Callback {

    @ViewInject(R.id.thermal_image)
    private ImageView thermalImageView;

    @ViewInject(R.id.tip)
    private TextView tvTip;

    @ViewInject(R.id.power)
    private TextView tvPower;

    private Handler handler;

    private String maxTemp = "";
    private String avgTemp = "";

    private FrameProcessor frameProcessor;

    private RenderedImage currentRenderImage;

    private FrameProcessor.Delegate frameDelegate = new FrameProcessor.Delegate() {
        @Override
        public void onFrameProcessed(RenderedImage renderedImage) {
            currentRenderImage = renderedImage;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    thermalImageView.setImageBitmap(currentRenderImage.getBitmap());
                }
            });
        }
    };

    private Device.Delegate deviceDelegate = new Device.Delegate() {
        @Override
        public void onTuningStateChanged(Device.TuningState tuningState) {

        }

        @Override
        public void onAutomaticTuningChanged(boolean b) {

        }

        @Override
        public void onDeviceConnected(Device device) {
            handler.removeCallbacks(showConnect);
            Log.e("TAG", "find the flirOneDevice");
//            device.setAutomaticTuning(true);
//            device.performTuning();
            device.setPowerUpdateDelegate(powerUpdateDelegate);
            device.startFrameStream(streamDelegate);
        }

        @Override
        public void onDeviceDisconnected(Device device) {

        }
    };

    private Device.PowerUpdateDelegate powerUpdateDelegate = new Device.PowerUpdateDelegate() {
        @Override
        public void onBatteryChargingStateReceived(Device.BatteryChargingState batteryChargingState) {

        }

        @Override
        public void onBatteryPercentageReceived(final byte b) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    tvPower.setText(String.format("当前电量%1$s", (int) b + "%"));
                }
            });
        }
    };

    private Device.StreamDelegate streamDelegate = new Device.StreamDelegate() {
        @Override
        public void onFrameReceived(Frame frame) {
            frameProcessor.processFrame(frame);
        }
    };


    private Runnable showConnect = new Runnable() {
        int count = 0;

        @Override
        public void run() {
            count++;
            if (count == 1)
                tvTip.setText("设备连接中.");
            else if (count == 2)
                tvTip.setText("设备连接中..");
            else
                tvTip.setText("设备连接中...");
            if (count == 3)
                count = 0;
            handler.postDelayed(this, 500);
        }
    };

    private int roatCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_priview);
        x.view().inject(this);
        handler = new Handler();
        frameProcessor = new FrameProcessor(this, frameDelegate, EnumSet.of(RenderedImage.ImageType.BlendedMSXRGBA8888Image));
        thermalImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                calculateTemperature();
                return true;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(showConnect);
        Device.startDiscovery(this, deviceDelegate);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Device.stopDiscovery();
    }

    @Override
    public boolean handleMessage(Message message) {
        return false;
    }

    public void calculateTemperature() {
        if (null != currentRenderImage) {
            FrameProcessor calculateProcessor = new FrameProcessor(this, new FrameProcessor.Delegate() {
                @Override
                public void onFrameProcessed(RenderedImage renderedImage) {
                    int averageTemp = 0;
                    int maxT = 0;
                    short[] shortPixels = new short[renderedImage.pixelData().length / 2];
                    ByteBuffer.wrap(renderedImage.pixelData()).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shortPixels);
                    for (int i = 0; i < shortPixels.length; i++) {
                        averageTemp += (((int) shortPixels[i]) - averageTemp) / ((double) i + 1);
                        maxT = Math.max((int) shortPixels[i], maxT);
                    }
                    avgTemp = NumberUtil.formatNumber(Double.valueOf((averageTemp / 100) - 273.15), "#.##");
                    maxTemp = NumberUtil.formatNumber(Double.valueOf((maxT / 100) - 273.15), "#.##");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            tvTip.setText(String.format("平均温度%1$s℃,最高温度%2$s℃", avgTemp, maxTemp));
                        }
                    });
                }
            }, EnumSet.of(currentRenderImage.imageType(), RenderedImage.ImageType.ThermalRadiometricKelvinImage));
            calculateProcessor.setImagePalette(currentRenderImage.palette());
            calculateProcessor.processFrame(currentRenderImage.getFrame());
        }
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_picture:
                thermalImageView.buildDrawingCache();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                String datePatten = sdf.format(new Date());
                Bitmap bp = thermalImageView.getDrawingCache();
                final String fileName = FunctionUtil.getCurrentImageName(_this,"thermal" + datePatten + ".jpg");
                final String fileName1 = FunctionUtil.getCurrentImageName(_this,"normal" + datePatten + ".jpg");

                BitmapUtil.saveBitmap(bp, Config.RESULT_PICTURES_FOLDER + fileName);
                takePicture(fileName, fileName1);

                break;
            case R.id.rock:
                this.thermalImageView.setRotation(90 * (roatCount + 1));
                roatCount++;
                if (roatCount == 4)
                    roatCount = 0;
                break;
        }
    }

    private void takePicture(final String fileName, final String fileName1) {
        if (null != currentRenderImage) {
            calculateTemperature();
            try {
                currentRenderImage.getFrame().save(new File(Config.RESULT_PICTURES_FOLDER + fileName1), RenderedImage.Palette.Iron, RenderedImage.ImageType.VisualYCbCr888Image);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent data = new Intent();
                        data.putExtra(Config.RESULT_TEMPERTURE, maxTemp);
                        data.putExtra(Config.RESULT_PICTURES, fileName + "," + fileName1);
                        setResult(RESULT_OK, data);
                        finish();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}


