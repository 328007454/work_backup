package com.cnksi.sjjc.activity;

import android.app.Dialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cnksi.core.utils.BitmapUtil;
import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.CoreConfig;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.DisplayUtil;
import com.cnksi.core.utils.FunctionUtils;
import com.cnksi.core.utils.NumberUtil;
import com.cnksi.core.utils.RelayoutUtil;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.HWCWAdapter;
import com.cnksi.sjjc.bean.Device;
import com.cnksi.sjjc.bean.Report;
import com.cnksi.sjjc.bean.ReportHwcw;
import com.cnksi.sjjc.bean.Task;
import com.cnksi.sjjc.databinding.HwcwBinding;
import com.cnksi.sjjc.enmu.InspectionType;
import com.cnksi.sjjc.enmu.PMSDeviceType;
import com.cnksi.sjjc.service.HwcwService;
import com.cnksi.sjjc.service.ReportService;
import com.cnksi.sjjc.service.TaskService;
import com.cnksi.sjjc.util.DialogUtils;
import com.cnksi.sjjc.util.FunctionUtil;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author han 红外测温界面
 */
public class HWCWMainActivity extends BaseActivity {
    public static final int THERM_ONE = 0x100;
    public static final int THERM_SECOND = THERM_ONE + 1;

    // 标题
    @ViewInject(R.id.tv_title)
    private TextView tvTitle;
    private HWCWAdapter hwcwAdapter;
    private Dialog mDialogNewHeatPoint;
    private NewHeatPointHolder mHeatHolder;
    /**
     * 当前选择设备
     */
    private DbModel currentDevice;
    /**
     * 当前设备类型 一次设备
     */
    private PMSDeviceType currentFuctionDevices;
    /**
     * 照片列表
     */
    private ArrayList<String> mImageList = new ArrayList<String>();
    /**
     * 当前照片地址名字
     */
    private String currentImageName;
    /**
     * 当前红外测温报告
     */
    private ReportHwcw currentHwcw;
    /**
     * 当前报告列表
     */
    private List<ReportHwcw> mReportHwcwList;
    /**
     * 是否是保护屏测温
     */

    private boolean isBhpcw = false;
    /**
     * 当前Report
     */
    private Report currentReport;
    private int currentSelect = -1;
    private final static int ACTION_IMAGE = 0x300;
    /**
     * 红外图像
     */
    private String[] hwtx = new String[2];


    HwcwBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_hwcw, null, false);
        RelayoutUtil.reLayoutViewHierarchy(binding.getRoot());
        setChildView(binding.getRoot());
        getIntentValue();
        initUI();
        initData();
    }

    private void initUI() {
        isBhpcw = currentInspectionType.equals(InspectionType.SBJC_02.name());
        currentFuctionDevices = isBhpcw ? PMSDeviceType.second : PMSDeviceType.one;
        binding.setIsBhpcw(isBhpcw);
        tvTitle.setText(currentInspectionName);

        binding.lvContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {

                if (EmptyAll()) {
                    View tv = view.findViewById(R.id.tv_editting_show);
                    if (tv == null) {
                        return;
                    }
                    if (currentSelect == -1) {
                        currentSelect = position;
                        currentHwcw = mReportHwcwList.get(position);
//                        _this.getResources().getColor(R.color.grass_color)
                        view.setBackgroundColor(ContextCompat.getColor(_this, R.color.grass_color));
                        tv.setVisibility(View.VISIBLE);
                    } else if (currentSelect != position) {
                        View v = binding.lvContainer.getChildAt(currentSelect);
                        v.findViewById(R.id.tv_editting_show).setVisibility(View.GONE);
//                        _this.getResources().getColor(R.color.tran)
                        v.setBackgroundColor(ContextCompat.getColor(_this, R.color.tran));
//                        _this.getResources().getColor(R.color.grass_color)
                        view.setBackgroundColor(ContextCompat.getColor(_this, R.color.grass_color));
                        tv.setVisibility(View.VISIBLE);
                        currentSelect = position;
                        currentHwcw = mReportHwcwList.get(position);
                    } else {
                        view.setBackgroundColor(ContextCompat.getColor(_this, R.color.tran));
                        tv.setVisibility(View.GONE);
                        currentSelect = -1;
                        currentHwcw = null;
                    }
                    hwcwAdapter.setCurrentSelect(currentSelect);
                    fillEdit(currentHwcw);
                } else {
                    DialogUtils.createTipsDialog(_this, getString(R.string.have_not_save_data), new View.OnClickListener() {
                        @Override
                        public void onClick(View v1) {
                            View tv = view.findViewById(R.id.tv_editting_show);
                            if (tv == null) {
                                return;
                            }
                            if (currentSelect == -1) {
                                currentSelect = position;
                                currentHwcw = mReportHwcwList.get(position);
//                                _this.getResources().getColor(R.color.grass_color)
                                view.setBackgroundColor(ContextCompat.getColor(_this, R.color.grass_color));
                                tv.setVisibility(View.VISIBLE);
                            } else if (currentSelect != position) {
                                View v = binding.lvContainer.getChildAt(currentSelect);
                                v.findViewById(R.id.tv_editting_show).setVisibility(View.GONE);
//                                _this.getResources().getColor(R.color.tran)
                                v.setBackgroundColor(ContextCompat.getColor(_this, R.color.tran));
                                view.setBackgroundColor(ContextCompat.getColor(_this, R.color.grass_color));
                                tv.setVisibility(View.VISIBLE);
                                currentSelect = position;
                                currentHwcw = mReportHwcwList.get(position);
                            } else {
                                view.setBackgroundColor(ContextCompat.getColor(_this, R.color.tran));
                                tv.setVisibility(View.GONE);
                                currentSelect = -1;
                                currentHwcw = null;
                            }
                            hwcwAdapter.setCurrentSelect(currentSelect);
                            fillEdit(currentHwcw);
                        }
                    }, false).show();
                }

            }


        });
        binding.etInputEnvironTemp.addTextChangedListener(watcher);
        binding.etTemp.addTextChangedListener(watcher);
        binding.etInputHeatPartTempt.addTextChangedListener(watcher);
        binding.etInputHeatPartTempt1.addTextChangedListener(watcher);
    }

    private void initData() {
        mFixedThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mReportHwcwList = HwcwService.getInstance().findAll(currentReportId, currentInspectionType);
                if (mReportHwcwList == null) {
                    mReportHwcwList = new ArrayList<ReportHwcw>();
                }
                currentReport = ReportService.getInstance().getReportById(currentReportId);
                mHandler.sendEmptyMessage(Config.LOAD_DATA);
            }
        });
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case Config.LOAD_DATA:
                binding.setReport(currentReport);
                binding.weatherView1.setCurrentWeather(currentReport.tq);

                if (mReportHwcwList != null) {
                    if (hwcwAdapter == null) {
                        hwcwAdapter = new HWCWAdapter(this, mReportHwcwList, R.layout.hwcw_item, isBhpcw);
                        binding.lvContainer.setAdapter(hwcwAdapter);
                    } else {
                        hwcwAdapter.setList(mReportHwcwList);
                    }
                }
                break;

            default:
                break;
        }
    }

    @Event({R.id.btn_back, R.id.btn_confirm, R.id.btn_confirm_save, R.id.tv_heat_devices, R.id.ibtn_take_picture, R.id.iv_show_take_picture, R.id.btn_cancel, R.id.ibtn_temp, R.id.ibtn_temp1, R.id.iv_show_temp1_picture, R.id.iv_show_temp_picture})
    private void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                this.finish();
                break;
            case R.id.btn_confirm_save:
                FinishTask();
                break;
            case R.id.btn_cancel:
                if (!EmptyAll()) {
                    if (currentSelect >= 0) {
                        binding.lvContainer.performItemClick(binding.lvContainer.getChildAt(currentSelect), currentSelect, 0);

                    } else {
                        DialogUtils.createTipsDialog(_this, getString(R.string.have_not_save_data), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fillEdit(null);
                            }
                        }, false).show();
                    }
                }

                break;
            case R.id.btn_confirm:
                saveHwcw();
                break;
            case R.id.tv_heat_devices:
                Intent intentDevices = new Intent(_this, AllDeviceListActivity.class);
                intentDevices.putExtra(AllDeviceListActivity.FUNCTION_MODEL, currentFuctionDevices);
                intentDevices.putExtra(AllDeviceListActivity.BDZID, currentBdzId);
                startActivityForResult(intentDevices, Config.ACTIVITY_CHOSE_DEVICE);
                break;
            case R.id.ibtn_take_picture:
                FunctionUtils.takePicture(_this, currentImageName = FunctionUtil.getCurrentImageName(_this), Config.RESULT_PICTURES_FOLDER, ACTION_IMAGE);
                break;
            case R.id.iv_show_take_picture:
                if (mImageList != null && !mImageList.isEmpty()) {
                    ArrayList<String> mImageUrlList = new ArrayList<String>();
                    for (String string : mImageList) {
                        mImageUrlList.add(Config.RESULT_PICTURES_FOLDER + string);
                    }
                    showImageDetails(this, mImageUrlList, true, false);
                }
                break;
            case R.id.ibtn_temp:
                startActivityForResult(new Intent(_this, HwcwPriviewActivity.class), THERM_ONE);
                break;
            case R.id.ibtn_temp1:
                startActivityForResult(new Intent(_this, HwcwPriviewActivity.class), THERM_SECOND);
                break;
            case R.id.iv_show_temp_picture:
                if (TextUtils.isEmpty(hwtx[0])) {
                    break;
                }
                ArrayList<String> pic = new ArrayList<String>();
                pic.add(Config.RESULT_PICTURES_FOLDER + hwtx[0]);
                showImageDetails(mCurrentActivity, pic, false, false);
                break;
            case R.id.iv_show_temp1_picture:
                if (TextUtils.isEmpty(hwtx[1])) {
                    break;
                }
                ArrayList<String> pic1 = new ArrayList<String>();
                pic1.add(Config.RESULT_PICTURES_FOLDER + hwtx[1]);
                showImageDetails(mCurrentActivity, pic1, false, false);
                break;
            default:
                break;
        }
    }

    private void FinishTask() {
        //如果还没有存取过记录 则把温度湿度存进去
        if (isEmpty(binding.etHumidity) || isEmpty(binding.etTemp)) {
            CToast.showShort(_this, "温度和湿度不能为空");
            return;
        }
        currentReport.tq = binding.weatherView1.getSelectWeather();
        currentReport.endtime = DateUtils.getCurrentTime(CoreConfig.dateFormat2);
        try {
            ReportService.getInstance().saveOrUpdate(currentReport);
            TaskService.getInstance().update(WhereBuilder.b(Task.TASKID, "=", currentTaskId), new KeyValue(Task.STATUS, Task.TaskStatus.done.name()));
        } catch (DbException e) {
            e.printStackTrace();
        }
        isNeedUpdateTaskState = true;
        startActivity(new Intent(_this, HongWaiCeWenReportActivity.class));
        finish();
    }

    /**
     * 温度监听 动态计算温差和相对温差
     */
    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            calTemperature();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * 温差=发热点温度-环境温度  （温差单位应为K，实际我们理解K跟℃一样就可以）
     * 相对温差=（T1-T2）/（T1-T0）×100%      T1即发热点温度，T2即环境温度，T0即环境参照体温度
     */
    private void calTemperature() {

        if (isEmpty(binding.etInputEnvironTemp) || isEmpty(binding.etInputHeatPartTempt) || isEmpty(binding.etTemp)) {
            binding.tvEnvironTempDif.setText("");
            binding.tvRelativeEnvironTemp.setText("");
        } else {
            if (isBhpcw && isEmpty(binding.etInputHeatPartTempt1)) {
                binding.tvEnvironTempDif.setText("");
                binding.tvRelativeEnvironTemp.setText("");
            } else {
                Float t0 = Float.valueOf(getText(binding.etInputEnvironTemp));
                Float t1;
                if (isBhpcw) {
                    t1 = Float.valueOf(getText(binding.etInputHeatPartTempt)) > Float.valueOf(getText(binding.etInputHeatPartTempt1)) ? Float.valueOf(getText(binding.etInputHeatPartTempt)) : Float.valueOf(getText(binding.etInputHeatPartTempt1));
                } else {
                    t1 = Float.valueOf(getText(binding.etInputHeatPartTempt));
                }
                Float t2 = Float.valueOf(getText(binding.etTemp));
                binding.tvEnvironTempDif.setText(NumberUtil.formatNumber((t1 - t2), "#.##"));
                if (t1 - t0 == 0) {
                    Toast("发热点温度不能和环境参照温度一致");
                    binding.tvRelativeEnvironTemp.setText("");
                    return;
                }
                binding.tvRelativeEnvironTemp.setText(NumberUtil.formatNumber((t1 - t2) / (t1 - t0) * 100, "#.##"));
            }

        }
    }


    private void showNewHeatPointDialog() {
        if (mDialogNewHeatPoint == null) {
            int dialogWidth = DisplayUtil.getInstance().getWidth() * 9 / 10;
            mDialogNewHeatPoint = DialogUtils.createDialog(this, null, R.layout.add_new_heat_point_dialog, mHeatHolder == null ? mHeatHolder = new NewHeatPointHolder() : mHeatHolder, dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT, false);
        }
        String displayText = "确认新增该发热点？\n" +
                "发热设备：" + currentHwcw.device_name +
                "\n发热部位：" + currentHwcw.frbw + "\n" +
                currentHwcw.qxms;
        mHeatHolder.tvProblem.setText(displayText);
        mDialogNewHeatPoint.show();

    }


    class NewHeatPointHolder {
        @ViewInject(R.id.tv_dialog_problem)
        private TextView tvProblem;

        @Event({R.id.btn_cancel, R.id.btn_confirm})
        private void onViewClick(View view) {
            switch (view.getId()) {
                case R.id.btn_cancel:
                    mDialogNewHeatPoint.dismiss();
                    break;
                case R.id.btn_confirm:
                    currentHwcw.FormatBean();
                    if (HwcwService.getInstance().saveOrUpdateOne(currentHwcw)) {
                        Toast("保存成功");
                        fillEdit(null);
                        if (currentSelect >= 0) {
                            mReportHwcwList.set(currentSelect, currentHwcw);
                            binding.lvContainer.performItemClick(binding.lvContainer.getChildAt(currentSelect), currentSelect, 0);

                        } else {
                            mReportHwcwList.add(0, currentHwcw);
                            currentHwcw = null;
                        }
                        mHandler.sendEmptyMessage(LOAD_DATA);
                    } else {
                        Toast("保存失败");
                    }
                    mDialogNewHeatPoint.dismiss();
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Config.ACTIVITY_CHOSE_DEVICE:
                    DbModel model = (DbModel) dataMap.get(Config.DEVICE_DATA);
                    if (model != null) {
                        currentDevice = model;
                        binding.tvHeatDevices.setText(currentDevice.getString(Device.NAME));
                    }
                    break;
                case ACTION_IMAGE:

                    mImageList.add(currentImageName);
                    String pictureContent = DateUtils.getFormatterTime(new Date(), CoreConfig.dateFormat8);
                    drawCircle(Config.RESULT_PICTURES_FOLDER + currentImageName, pictureContent);
                    break;

                case CANCEL_RESULT_LOAD_IMAGE:

                    ArrayList<String> cancelList = data.getStringArrayListExtra(Config.CANCEL_IMAGEURL_LIST);
                    for (String imageUrl : cancelList) {
                        mImageList.remove(imageUrl.replace(Config.RESULT_PICTURES_FOLDER, ""));
                    }
                case LOAD_DATA:
                    showThumbPic();
                    break;
                //红外测温  /保护屏背面测温
                case THERM_ONE:
                    binding.etInputHeatPartTempt.setText(data.getStringExtra(Config.RESULT_TEMPERTURE));
                    String str = data.getStringExtra(Config.RESULT_PICTURES);
                    CToast.showShort(_this, str);
                    if (!TextUtils.isEmpty(str)) {
                        String pics[] = str.split(CoreConfig.COMMA_SEPARATOR);
                        if (pics.length == 2) {
                            binding.etTestInstrument.setText("FLIR ONE");
                            //    imgTemp.setImageBitmap(BitmapUtil.getImageThumbnail(Config.RESULT_PICTURES_FOLDER + pics[0],80 , 80));
                            String oldImg = hwtx[0];

                            hwtx[0] = pics[0];
                            //移除旧的照片
                            if (!TextUtils.isEmpty(oldImg)) {
                                oldImg = oldImg.replace("thermal", "normal");
                                mImageList.remove(oldImg);
                            }
                            mImageList.add(pics[1]);
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showThumbPic();
                                }
                            }, 1000);

                        }
                    }
                    break;
                //保护屏正面测温
                case THERM_SECOND:
                    binding.etInputHeatPartTempt1.setText(data.getStringExtra(Config.RESULT_TEMPERTURE));
                    String str1 = data.getStringExtra(Config.RESULT_PICTURES);
                    CToast.showShort(_this, str1);
                    if (!TextUtils.isEmpty(str1)) {

                        String pics[] = str1.split(CoreConfig.COMMA_SEPARATOR);
                        if (pics.length == 2) {
                            String oldImg = hwtx[1];

                            hwtx[1] = pics[0];
                            binding.etTestInstrument.setText("FLIR ONE");
                            //    imgTemp1.setImageBitmap(BitmapUtil.getImageThumbnail(Config.RESULT_PICTURES_FOLDER + pics[0],80 , 80));
                            mImageList.add(pics[1]);
                            //移除旧的照片
                            if (!TextUtils.isEmpty(oldImg)) {
                                oldImg = oldImg.replace("thermal", "normal");
                                mImageList.remove(oldImg);
                            }
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    showThumbPic();
                                }
                            }, 1000);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }


    private void saveHwcw() {

        if (isEmpty(binding.etHumidity) || isEmpty(binding.etTemp)) {
            CToast.showShort(_this, "温度和湿度不能为空");
            return;
        }
        if (!checkTextViewValue()) {
            return;
        }
        currentReport.tq = binding.weatherView1.getSelectWeather();
        try {
            ReportService.getInstance().saveOrUpdate(currentReport);
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (currentHwcw == null) {
            currentHwcw = new ReportHwcw();
            currentHwcw.id = Config.CURRENT_AREA + "-" + FunctionUtils.getPrimarykey();
            currentHwcw.dlt = "0";
            currentHwcw.insert_time = DateUtils.getCurrentTime(CoreConfig.dateFormat8);
        }
        currentHwcw.last_modify_time = DateUtils.getCurrentTime(CoreConfig.dateFormat4);
        currentHwcw.type = currentInspectionType;
        currentHwcw.report_id = currentReportId;
        currentHwcw.bdz_id = currentBdzId;
        currentHwcw.bdz_name = currentBdzName;
        currentHwcw.device_id = currentDevice.getString(Device.DEVICEID);
        currentHwcw.device_name = currentDevice.getString(Device.NAME);
        currentHwcw.frbw = getText(binding.etInputHeatPart);
        if (isBhpcw) {
            currentHwcw.bhpbmwd = getText(binding.etInputHeatPartTempt);
            currentHwcw.bhpzmwd = getText(binding.etInputHeatPartTempt1);
        } else {
            currentHwcw.frdwd = getText(binding.etInputHeatPartTempt);
        }
        currentHwcw.hjcztwd = getText(binding.etInputEnvironTemp);
        currentHwcw.wc = getText(binding.tvEnvironTempDif);
        currentHwcw.xdwc = getText(binding.tvRelativeEnvironTemp);
        currentHwcw.fhdl = getText(binding.etLoadElectric);
        currentHwcw.eddl = getText(binding.etRatedElectric);
        currentHwcw.csyq = getText(binding.etTestInstrument);
        currentHwcw.fs = getText(binding.tvWindSpeed);
        currentHwcw.bz = getText(binding.tvRemarkInfor);
        currentHwcw.last_modify_time = DateUtils.getCurrentTime(CoreConfig.dateFormat4);
        String imgs = "";
        for (String str : mImageList) {
            imgs = imgs + str + CoreConfig.COMMA_SEPARATOR;
        }
        if (!TextUtils.isEmpty(imgs))
            currentHwcw.kjgtx = imgs.substring(0, imgs.length() - 1);
        calResult(currentHwcw);
        if (isBhpcw) {

            currentHwcw.hwtx = TextUtils.isEmpty(hwtx[0]) ? TextUtils.isEmpty(hwtx[1]) ? "" : hwtx[1] : TextUtils.isEmpty(hwtx[1]) ? hwtx[0] : hwtx[0] + CoreConfig.COMMA_SEPARATOR + hwtx[1];
        } else currentHwcw.hwtx = TextUtils.isEmpty(hwtx[0]) ? "" : hwtx[0];
        showNewHeatPointDialog();

    }

    private void calResult(ReportHwcw bean) {
        if (bean != null) {
            bean.qxxz = "6";
            bean.qxms = "温差：" + bean.wc + "K;  相对温差：" + bean.xdwc + "%";
        }

    }

    /**
     * 检查是否有未填数据
     *
     * @return
     */
    private boolean checkTextViewValue() {
        if (TextUtils.isEmpty(getText(binding.etTemp).trim())) {
            Toast("温度不能为空");
            return false;
        }
        if (currentDevice == null) {
            Toast("设备选择不能为空");
            return false;
        }
        if (TextUtils.isEmpty(getText(binding.etInputHeatPartTempt).trim())) {
            Toast("发热部位温度不能为空");
            return false;
        }
        if (TextUtils.isEmpty(getText(binding.etInputEnvironTemp).trim())) {
            Toast("环境参照温度不能为空");
            return false;
        }
        if (TextUtils.isEmpty(binding.weatherView1.getSelectWeather().toString().trim())) {
            Toast("天气选择不能为空");
            return false;
        }
        return true;
    }

    /**
     * 填充控件、
     *
     * @param bean
     */
    private void fillEdit(ReportHwcw bean) {
        mImageList.clear();
        boolean isNull = false;
        if (bean != null) {
            currentDevice = new DbModel();
            currentDevice.add(Device.NAME, bean.device_name);
            currentDevice.add(Device.DEVICEID, bean.device_id);
            binding.btnConfirm.setText("保存");
            binding.tvHeatDevices.setText(bean.device_name);
            binding.etInputHeatPart.setText(bean.frbw);
        } else {
            isNull = true;
            bean = new ReportHwcw();
            binding.btnConfirm.setText("新增");
            binding.tvHeatDevices.setText("");
            binding.etInputHeatPart.setText("");
        }
        if (!TextUtils.isEmpty(bean.kjgtx)) {
            String[] imgs = bean.kjgtx.split(CoreConfig.COMMA_SEPARATOR);
            for (String img : imgs) {
                mImageList.add(img);
            }

        }
        binding.setHwcw(bean);
        if (isBhpcw) {
            binding.etInputHeatPartTempt.setText(bean.bhpbmwd);
            binding.etInputHeatPartTempt1.setText(bean.bhpzmwd);
        } else {
            binding.etInputHeatPartTempt.setText(bean.frdwd);
            binding.etInputHeatPartTempt1.setText("");
        }

        binding.etInputEnvironTemp.setText(bean.hjcztwd);
        binding.tvEnvironTempDif.setText(bean.wc);
        binding.tvRelativeEnvironTemp.setText(bean.xdwc);
        binding.etLoadElectric.setText(bean.fhdl);
        binding.etRatedElectric.setText(bean.eddl);
        binding.etTestInstrument.setText(bean.csyq);
        binding.tvWindSpeed.setText(bean.fs);
        binding.tvRemarkInfor.setText(bean.bz);

        if (!TextUtils.isEmpty(bean.hwtx)) {
            String str[] = bean.hwtx.split(CoreConfig.COMMA_SEPARATOR);
            if (str.length == 1) {
                hwtx[0] = str[0];
                hwtx[1] = "";
            } else if (str.length > 1) {
                hwtx[0] = str[0];
                hwtx[1] = str[1];
            } else {
                hwtx[0] = "";
                hwtx[1] = "";
            }
        } else {
            hwtx[0] = "";
            hwtx[1] = "";
        }

        if (isNull) {
            bean = null;
        }
        showThumbPic();
    }

    /**
     * 显示可见光缩略图
     */
    private void showThumbPic() {
        if (mImageList != null && mImageList.size() > 1) {
            binding.tvPicNum.setVisibility(View.VISIBLE);
            binding.tvPicNum.setText(String.valueOf(mImageList.size()));
        } else {
            binding.tvPicNum.setVisibility(View.GONE);
        }
        int newWidth = getResources().getDimensionPixelSize(R.dimen.new_defect_photo_height);
        if (mImageList != null && !mImageList.isEmpty()) {
            binding.ivShowTakePicture.setImageBitmap(BitmapUtil.getImageThumbnail(Config.RESULT_PICTURES_FOLDER + mImageList.get(0), newWidth, newWidth));
        } else {
            binding.ivShowTakePicture.setImageBitmap(null);
        }
        if (!TextUtils.isEmpty(hwtx[0])) {
            binding.ivShowTempPicture.setVisibility(View.VISIBLE);
            binding.ivShowTempPicture.setImageBitmap(BitmapUtil.getImageThumbnail(Config.RESULT_PICTURES_FOLDER + hwtx[0], newWidth, newWidth));
        } else {
            binding.ivShowTempPicture.setImageBitmap(null);
            binding.ivShowTempPicture.setVisibility(View.INVISIBLE);
        }
        if (!TextUtils.isEmpty(hwtx[1])) {
            binding.ivShowTemp1Picture.setVisibility(View.VISIBLE);
            binding.ivShowTemp1Picture.setImageBitmap(BitmapUtil.getImageThumbnail(Config.RESULT_PICTURES_FOLDER + hwtx[1], newWidth, newWidth));
        } else {
            binding.ivShowTemp1Picture.setVisibility(View.INVISIBLE);
            binding.ivShowTemp1Picture.setImageBitmap(null);
        }
    }

    /**
     * 是否有已填数据
     *
     * @return
     */
    private boolean EmptyAll() {
        if (isEmpty(binding.tvHeatDevices) && isEmpty(binding.etInputHeatPart) && isEmpty(binding.etInputHeatPartTempt) && isEmpty(binding.etInputHeatPartTempt1)
                && isEmpty(binding.etInputEnvironTemp) && isEmpty(binding.etLoadElectric) && isEmpty(binding.etRatedElectric)
                && isEmpty(binding.etTestInstrument) && isEmpty(binding.tvWindSpeed) && isEmpty(binding.tvRemarkInfor) && mImageList.size() == 0)
            if (isBhpcw)

                if (isEmpty(binding.etInputHeatPartTempt1))
                    return true;
                else return false;

            else
                return true;

        return false;
    }
}
