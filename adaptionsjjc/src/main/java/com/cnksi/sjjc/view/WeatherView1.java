package com.cnksi.sjjc.view;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.cnksi.common.utils.CalcUtils;
import com.cnksi.common.utils.ViewHolder;
import com.cnksi.core.utils.DisplayUtils;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.BaseAdapter;
import com.cnksi.sjjc.adapter.BaseRecyclerAdapter;
import com.cnksi.sjjc.adapter.holder.RecyclerHolder;
import com.cnksi.sjjc.databinding.ArrowBinding;
import com.cnksi.sjjc.databinding.WeatherTitleBinding;
import com.zhy.autolayout.utils.AutoLayoutHelper;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author luoxy
 * @version 1.0
 * @date 16/4/20
 */
public class WeatherView1 extends LinearLayout {


    private TextView mLabel;

    private RecyclerView mRecyclerView;

    private ImageView mIcon;
    /**
     * label显示文字
     */
    private String mLabelStr;
    /**
     * label 显示文字的颜色
     */
    private int mLabelColor;
    /**
     * Label显示文字的大小
     */
    private int mLabelSize;

    /**
     * label距离左边的padding
     */
    private int mLabelPaddingLeft;

    private int weatherResource;
    private int iconHeight;

    private String[] weatherSelector;

    private String currentWeather;

    private Dialog weatherDialog;

    private WeatherAdapter adapter1;

    private WeatherAdapter2 adapter2;

    private int iconResource;

    private int iconWidth;

    private int iconMargin;
    private final AutoLayoutHelper mHelper = new AutoLayoutHelper(this);

    public WeatherView1(Context context) {
        this(context, null);
    }

    public WeatherView1(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeatherView1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.WeatherView);
            // label 属性设置
            mLabelStr = a.getString(R.styleable.WeatherView_label);
            mLabelStr = (mLabelStr == null) ? "" : mLabelStr;
            // 默认黑色
            mLabelColor = a.getColor(R.styleable.WeatherView_label_color, 0xFF000000);
            mLabelPaddingLeft = a.getDimensionPixelSize(R.styleable.WeatherView_label_padding_left, 0);
            // 默认字体大小30px
            mLabelSize = a.getDimensionPixelSize(R.styleable.WeatherView_label_size,
                    CalcUtils.convertFloatToInt(DisplayUtils.getInstance().getTextScale() * 30));

            weatherResource = a.getResourceId(R.styleable.WeatherView_selector, 0);
            if (0 != weatherResource) {
                weatherSelector = a.getResources().getStringArray(weatherResource);
                currentWeather = weatherSelector[0];
            }

            iconResource = a.getResourceId(R.styleable.WeatherView_icon_src, R.drawable.ic_arrow_right);
            iconHeight = a.getDimensionPixelOffset(R.styleable.WeatherView_icon_height, 48);
            iconWidth = a.getDimensionPixelSize(R.styleable.WeatherView_icon_width, 29);
            iconMargin = a.getDimensionPixelSize(R.styleable.WeatherView_icon_margin,
                    CalcUtils.convertFloatToInt(DisplayUtils.getInstance().getTextScale() * 10));

            a.recycle();
        }

        init();
    }

    ArrowBinding bingding;
    WeatherTitleBinding titleBinding;

    private void init() {
        this.setOrientation(HORIZONTAL);
        if (bingding == null) {
            bingding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.arrow_right_layout, null, false);
            AutoUtils.auto(bingding.getRoot());
            titleBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.weather_title_layout, null, false);
            AutoUtils.auto(titleBinding.getRoot());
        }
        mLabel = titleBinding.title;
        LayoutParams leftLayoutParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mLabel.setText(mLabelStr);
        mLabel.setTextColor(mLabelColor);
        mLabel.setPadding(mLabelPaddingLeft, 0, 0, 0);
        mLabel.setLayoutParams(leftLayoutParam);
        mLabelSize = AutoUtils.getPercentHeightSizeBigger(mLabelSize);
        mLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, mLabelSize);

        mRecyclerView = new RecyclerView(getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter1 = new WeatherAdapter(mRecyclerView, Arrays.asList(weatherSelector), R.layout.item_weather);
        mRecyclerView.setAdapter(adapter1);
        LayoutParams middleLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        middleLayoutParam.weight = 1;
        middleLayoutParam.gravity = Gravity.CENTER_VERTICAL;
        this.addView(mRecyclerView, middleLayoutParam);
        this.addView(bingding.getRoot());
        bingding.arrow.setOnClickListener(v -> showWeatherDialog());
    }

    class WeatherAdapter extends BaseRecyclerAdapter<String> {

        public WeatherAdapter(RecyclerView v, Collection<String> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);
        }

        @Override
        public void convert(RecyclerHolder holder, final String item, final int position, boolean isScrolling) {
            RadioButton radioButton = holder.getView(R.id.weather);
            radioButton.setText(item);
            if (currentWeather.equals(item)) {
                radioButton.setChecked(true);
            }else if (position == 0 && TextUtils.isEmpty(currentWeather)) {
                radioButton.setChecked(true);
                currentWeather = item;
            }else{
                radioButton.setChecked(false);
            }
            radioButton.setOnClickListener(v -> {
                if (currentWeather.equals(item)) {
                    currentWeather = "";
                } else {
                    currentWeather = item;
                }
                adapter1.notifyDataSetChanged();
                if (null != adapter2) {
                    adapter2.notifyDataSetChanged();
                }
                mRecyclerView.scrollToPosition(position);
            });
        }
    }

    class WeatherAdapter2 extends BaseAdapter<String> {
        public WeatherAdapter2(Context context, Collection<String> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, final String item, final int position) {
            TextView tvWeather = holder.getView(R.id.tv_weather);
            ImageView imgButton = holder.getView(R.id.img_radio_img);
            tvWeather.setText(item);
            if (currentWeather.equals(item)) {
                imgButton.setImageResource(R.drawable.ticket_radiobutton_red_selected);
            } else {
                imgButton.setImageResource(R.drawable.ticket_radiobutton_unselected);
            }

        }


    }

    /**
     * 返回获取选择的天气
     *
     * @return
     */
    public String getSelectWeather() {
        return currentWeather;
    }

    private void showWeatherDialog() {
        if (null == weatherDialog) {
            weatherDialog = new Dialog(getContext(), R.style.dialog);
            ViewHolder holder = new ViewHolder(getContext(), null, R.layout.layout_dialog, false);
            ListView listView = holder.getView(R.id.weather_list);
            adapter2 = new WeatherAdapter2(getContext(), Arrays.asList(weatherSelector),
                    R.layout.item_weather_vertical1);
            listView.setAdapter(adapter2);
            weatherDialog.setContentView(holder.getRootView(),
                    new ViewGroup.LayoutParams(
                            CalcUtils.convertFloatToInt(DisplayUtils.getInstance().getWidth() * 0.9f),
                            CalcUtils.convertFloatToInt(DisplayUtils.getInstance().getHeightScale() * 800)));
            listView.setOnItemClickListener((adapterView, view, position, l) -> {
                TextView textView = view.findViewById(R.id.tv_weather);
                String item = textView.getText().toString();
                if (currentWeather.equals(item)) {
                    currentWeather = "";
                } else {
                    currentWeather = item;
                }
                adapter1.notifyDataSetChanged();
                if (null != adapter2) {
                    adapter2.notifyDataSetChanged();
                    weatherDialog.dismiss();
                }
                mRecyclerView.scrollToPosition(position);
            });
        }
        weatherDialog.show();

    }

    public void setCurrentWeather(String currentWeather) {

        this.currentWeather = currentWeather == null ? "" : currentWeather;
        adapter1.notifyDataSetChanged();
        if (null != adapter2) {
            adapter2.notifyDataSetChanged();
        }
    }

//    @Override
//    public LayoutParams generateLayoutParams(AttributeSet attrs)
//    {
//        return new LayoutParams(getContext(), attrs);
//    }


//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
//    {
//        if (!isInEditMode())
//        {
//            mHelper.adjustChildren();
//        }
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }

}
