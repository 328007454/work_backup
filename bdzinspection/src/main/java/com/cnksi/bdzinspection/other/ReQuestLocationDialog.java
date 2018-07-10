package com.cnksi.bdzinspection.other;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;

import com.baidu.location.BDLocation;
import com.cnksi.bdloc.LocationListener;
import com.cnksi.bdloc.LocationUtil;
import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.databinding.LayoutRequestLocationDialogBinding;
import com.cnksi.common.Config;
import com.cnksi.common.base.BaseDialogFragment;
import com.cnksi.common.listener.ItemClickListener;
import com.cnksi.common.utils.FunctionUtil;
import com.cnksi.core.utils.GPSUtils;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.core.view.CustomerDialog;

import static com.cnksi.core.utils.Cst.ACTION_IMAGE;

/**
 * Created by Mr.K on 2018/7/9.
 */

public class ReQuestLocationDialog extends BaseDialogFragment {
    private LayoutRequestLocationDialogBinding locationDialogBinding;
    private static ReQuestLocationDialog reQuestLocationDialog;
    private ItemClickListener itemClickListener;
    private Button photoView;
    private String fileName;

    public synchronized static ReQuestLocationDialog getInstance() {
        if (reQuestLocationDialog == null) {
            reQuestLocationDialog = new ReQuestLocationDialog();

        }
        return reQuestLocationDialog;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int setLayout() {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return R.layout.layout_request_location_dialog;
    }

    @Override
    public void initData() {
        super.initData();
    }

    @Override
    public void initUI() {
        locationDialogBinding = (LayoutRequestLocationDialogBinding) dataBinding;
        SpannableStringBuilder builder = new SpannableStringBuilder(locationDialogBinding.tips.getText().toString());
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#FD6067"));
        builder.setSpan(colorSpan, 9, 16, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        builder.setSpan(colorSpan, 17, locationDialogBinding.tips.getText().toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        locationDialogBinding.tips.setText(builder);
        locationDialogBinding.btPhotoLocation.setOnClickListener(v -> {
            dismiss();
            photoView = (Button) v;
            fileName = FunctionUtil.getCurrentImageName(getActivity());
            FunctionUtil.takePicture(getActivity(), fileName, Config.RESULT_PICTURES_FOLDER, ACTION_IMAGE);
        });

        locationDialogBinding.btRequestLocation.setOnClickListener(v -> {
            requestLocation(v);

        });

        locationDialogBinding.ivCancelLocation.setOnClickListener(v -> {
            dismiss();
        });
    }


    private void requestLocation(final View view) {
        if (!GPSUtils.isOPen(getActivity())) {
            ToastUtils.showMessage("请打开GPS再进行定位！");
        } else {
            CustomerDialog.showProgress(getActivity(), R.string.xs_locating_str);
            LocationUtil.getInstance().getLocalHelper(new LocationListener() {
                @Override
                public void locationSuccess(BDLocation location) {
                    String longitude = String.valueOf(location.getLongitude());
                    String latitude = String.valueOf(location.getLatitude());
                    CustomerDialog.dismissProgress();
                    if (itemClickListener != null) {
                        itemClickListener.onClick(view, location, 0);
                    }
                    ReQuestLocationDialog.this.dismiss();
                }

                @Override
                public void locationFailure(int code, String message) {
                    if (code == LocationListener.ERROR_TIMEOUT) {
                        CustomerDialog.dismissProgress();
                        ToastUtils.showMessage("请求定位超时");
                    }
                }
            }).setTimeout(10).start();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (itemClickListener != null) {
            itemClickListener.onClick(photoView, fileName, 0);
        }
    }
}
