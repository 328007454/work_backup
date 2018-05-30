package com.cnksi.sjjc.view.gztz;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.cnksi.common.base.BaseBindingAdapter;
import com.cnksi.common.databinding.DialogContentChildItemBinding;
import com.cnksi.common.utils.DialogUtils;
import com.cnksi.common.utils.StringUtilsExt;
import com.cnksi.common.utils.ViewHolder;
import com.cnksi.common.view.UnderLineLinearLayout;
import com.cnksi.core.utils.DisplayUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.service.gztz.GZTZSjlyService;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.common.util.KeyValue;

import java.util.List;

/**
 * @version 1.0
 * @auth wastrel
 * @date 2018/3/6 19:05
 * @copyRight 四川金信石信息技术有限公司
 * @since 1.0
 */
public class SelectGroup extends UnderLineLinearLayout {

    private TextView tvName, tvValue;
    private KeyValue keyValue;
    private String title;
    private Dialog selectDialog;
    private BaseBindingAdapter<DialogContentChildItemBinding,KeyValue> adapter;
    private String type;
    private ImageButton addButton;
    private ImageButton deleteButton;
    private onSelectItemListener listener;
    String hint;
    private DataProvider dataProvider = new DataProvider() {
        @Override
        public List<KeyValue> getData() {
            return GZTZSjlyService.getInstance().findByType(type);
        }
    };

    public SelectGroup(Context context) {
        this(context, null);
    }

    public SelectGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
        setDrawUnderLine(true);
        LayoutInflater.from(context).inflate(R.layout.gztz_item_select_group, this, true);
        tvValue = findViewById(R.id.tv_value);
        tvName = findViewById(R.id.tv_name);
        addButton = findViewById(R.id.add);
        deleteButton = findViewById(R.id.delete);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.SelectGroup);
        if (attributes != null) {
            title = attributes.getString(R.styleable.SelectGroup_title_str);
            tvName.setText(title);
            hint = attributes.getString(R.styleable.SelectGroup_select_hint_str);
            tvValue.setHint(StringUtilsExt.nullTo(hint, "请选择"));
        }
        tvValue.setOnClickListener(v -> SelectGroup.this.show());
    }

    public void setSelectOnClickListener(View.OnClickListener onClickListener) {
        tvValue.setOnClickListener(onClickListener);
    }

    public KeyValue getValue() {
        return keyValue;
    }

    public void setKeyValue(KeyValue keyValue) {
        if (!TextUtils.isEmpty(keyValue.key)) {
            this.keyValue = keyValue;
        } else {
            this.keyValue = null;
        }
        tvValue.setText(keyValue.getValueStr());
    }

    private void initDialog() {
        List<KeyValue> keyValues = dataProvider.getData();
        int dialogWidth = DisplayUtils.getInstance().getWidth() * 9 / 10;
        int dialogHeight = keyValues.size() > 5 ? DisplayUtils.getInstance().getHeight() * 3 / 5 :
                ViewGroup.LayoutParams.WRAP_CONTENT;
        ViewHolder holder = new ViewHolder(getContext(), this, R.layout.content_list_dialog, false);
        AutoUtils.autoSize(holder.getRootView());
        ListView lv = holder.getView(R.id.lv_container);
        holder.setText(R.id.tv_dialog_title, title);
        adapter = new BaseBindingAdapter<DialogContentChildItemBinding,KeyValue>(getContext(), keyValues, R.layout.dialog_content_child_item) {
            @Override
            public void convert(DialogContentChildItemBinding holder, KeyValue item, int position) {
                holder.tvChildItem.setText(item.getValueStr());
            }
        };
        //设置adapter的listView点击事件
        lv.setOnItemClickListener((parent, view, position, id) -> {
            SelectGroup.this.setKeyValue((KeyValue) adapter.getItem(position));
            if (listener != null) {
                listener.onselect(keyValue);
            }
            selectDialog.dismiss();
        });
        lv.setAdapter(adapter);
        selectDialog = DialogUtils.createDialog(getContext(), holder, dialogWidth, dialogHeight, true);
    }

    public void setType(String type) {
        this.type = type;
    }

    private void show() {
        if (selectDialog == null) {
            initDialog();
        }
        selectDialog.show();
    }

    public void setMustInput(boolean isMust) {
        String name = tvName.getText().toString();
        if (isMust) {
            if (!name.startsWith("*")) {
                tvName.setText("*" + name);
            }
            tvValue.setHint(hint);
        } else {
            if (name.startsWith("*")) {
                tvName.setText(name.substring(1));
            }
            tvValue.setHint("");
        }
    }

    public void setVisible(int addVisible, int deleteVisible) {
        addButton.setVisibility(addVisible);
        deleteButton.setVisibility(deleteVisible);
    }

    public ImageButton getAddButton() {
        return addButton;
    }

    public ImageButton getDeleteButton() {
        return deleteButton;
    }

    public TextView getTvValueView() {
        return tvValue;
    }

    public String getValueStr() {
        return tvValue.getText().toString();
    }

    public void setListener(onSelectItemListener listener) {
        this.listener = listener;
    }


    public interface onSelectItemListener {
        void onselect(KeyValue value);
    }

    public interface DataProvider {
        List<KeyValue> getData();
    }

    public void setDataProvider(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }
}
