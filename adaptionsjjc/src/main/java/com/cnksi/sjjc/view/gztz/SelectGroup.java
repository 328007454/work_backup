package com.cnksi.sjjc.view.gztz;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.cnksi.core.adapter.ViewHolder;
import com.cnksi.core.utils.DisplayUtil;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.BaseAdapter;
import com.cnksi.sjjc.service.gztz.GZTZSjlyService;
import com.cnksi.sjjc.util.DialogUtils;
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
public class SelectGroup extends com.cnksi.sjjc.view.UnderLineLinearLayout {

    private TextView tvName, tvValue;
    private KeyValue keyValue;
    private String title;
    private Dialog selectDialog;
    private BaseAdapter<KeyValue> adapter;
    private String type;
    private ImageButton addButton;
    private ImageButton deleteButton;

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
        tvValue = (TextView) findViewById(R.id.tv_value);
        tvName = (TextView) findViewById(R.id.tv_name);
        addButton = (ImageButton) findViewById(R.id.add);
        deleteButton = (ImageButton) findViewById(R.id.delete);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.SelectGroup);
        if (attributes != null) {
            title = attributes.getString(R.styleable.SelectGroup_title_str);
            tvName.setText(title);
            tvValue.setHint(attributes.getString(R.styleable.SelectGroup_select_hint_str));
        }
        tvValue.setOnClickListener(v -> show());
    }

    public void setSelectOnClickListener(View.OnClickListener onClickListener) {
        tvValue.setOnClickListener(onClickListener);
    }


    public void setClickListener(View.OnClickListener onClickListener) {
        setOnClickListener(onClickListener);
    }

    public KeyValue getValue() {
        return keyValue;
    }

    public void setKeyValue(KeyValue keyValue) {
        this.keyValue = keyValue;
        tvValue.setText(keyValue.getValueStr());
    }

    private void initDialog() {
        int dialogWidth = DisplayUtil.getInstance().getWidth() * 9 / 10;
        int dialogHeight = DisplayUtil.getInstance().getHeight() * 3 / 5;
        ViewHolder holder = new ViewHolder(getContext(), this, R.layout.content_list_dialog, false);
        AutoUtils.autoSize(holder.getRootView());
        ListView lv = holder.getView(R.id.lv_container);
        holder.setText(R.id.tv_dialog_title, title);
        List<KeyValue> keyValues = GZTZSjlyService.getInstance().findByType(type);
        adapter = new BaseAdapter<KeyValue>(getContext(), keyValues, R.layout.dialog_content_child_item) {
            @Override
            public void convert(com.cnksi.sjjc.adapter.ViewHolder holder, KeyValue item, int position) {
                holder.setText(R.id.tv_child_item, item.getValueStr());
            }
        };
        //设置adapter的listView点击事件
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setKeyValue((KeyValue) adapter.getItem(position));
                selectDialog.dismiss();
            }
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
}
