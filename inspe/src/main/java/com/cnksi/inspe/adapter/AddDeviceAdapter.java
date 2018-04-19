package com.cnksi.inspe.adapter;

import android.bluetooth.BluetoothClass;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.cnksi.core.utils.ToastUtils;
import com.cnksi.inspe.R;
import com.cnksi.inspe.db.entity.DeviceEntity;

import java.util.List;

/**
 * 添加缺失设备台账适配器
 * <p>
 * Created by Mr.K on 2018/4/17.
 */

public class AddDeviceAdapter extends BaseQuickAdapter<DeviceEntity, BaseViewHolder> {
    public interface OnViewClickLitener {
        void onViewClick(View v, Object item, int position);
    }

    private OnViewClickLitener onViewClickLitener;

    public void setOnViewClick(OnViewClickLitener onViewClick) {
        this.onViewClickLitener = onViewClick;
    }

    public AddDeviceAdapter(int layoutResId, @Nullable List data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, DeviceEntity item) {
        if (TextUtils.isEmpty(bigid)) {
            helper.getView(R.id.framelayout).setVisibility(View.VISIBLE);
        } else {
            helper.getView(R.id.framelayout).setVisibility(View.GONE);
        }
        final int position = helper.getAdapterPosition();
        if (position == 0 && getItemCount() == 1) {
            helper.getView(R.id.img_delete).setVisibility(View.INVISIBLE);
        } else {
            helper.getView(R.id.img_delete).setVisibility(View.VISIBLE);
        }
        ((TextView) helper.getView(R.id.name_num)).setText(String.valueOf(position + 1));

        EditText editText = helper.getView(R.id.et_device_name);

        if (editText.getTag() instanceof  EditTextWatcher){
            editText.removeTextChangedListener((android.text.TextWatcher) editText.getTag());
        }
        editText.setText(TextUtils.isEmpty(item.name_short) ? "" : item.name_short);

        EditTextWatcher watcher = new EditTextWatcher();
        watcher.setEntity(item);
        editText.addTextChangedListener(watcher);
        editText.setTag(watcher);


        ((TextView) helper.getView(R.id.et_space_name)).setText(TextUtils.isEmpty(item.spaceName) ? "" : item.spaceName);
        helper.getView(R.id.et_space_name).setOnClickListener(view -> {
            if (onViewClickLitener != null) {
                onViewClickLitener.onViewClick(view, item, position);
            }
        });

        helper.getView(R.id.img_delete).setOnClickListener(view -> {
            if (onViewClickLitener != null) {
                onViewClickLitener.onViewClick(view, item, position);
            }
        });

        helper.getView(R.id.framelayout).setOnClickListener(view -> {
            if (onViewClickLitener != null) {
                onViewClickLitener.onViewClick(view, item, position);
            }
        });

    }

    class EditTextWatcher implements android.text.TextWatcher {
        DeviceEntity entity;

        public void setEntity(DeviceEntity entity) {
            this.entity = entity;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String text = editable.toString();
            entity.setName_short(text);
            entity.setName(text);
        }
    }

    private String bigid;

    public void setBigId(String bigid) {
        this.bigid = bigid;
        notifyDataSetChanged();
    }
}
