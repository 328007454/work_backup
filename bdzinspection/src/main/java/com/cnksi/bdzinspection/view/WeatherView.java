package com.cnksi.bdzinspection.view;

import java.util.Arrays;
import java.util.Collection;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.cnksi.bdzinspection.R;
import com.cnksi.bdzinspection.adapter.base.BaseAdapter;
import com.cnksi.bdzinspection.adapter.base.BaseRecyclerAdapter;
import com.cnksi.bdzinspection.adapter.RecyclerHolder;
import com.cnksi.bdzinspection.adapter.ViewHolder;
import com.cnksi.bdzinspection.utils.DisplayUtil;
import com.cnksi.bdzinspection.utils.NumberUtil;
import com.cnksi.bdzinspection.utils.TQEnum;
import com.zhy.core.utils.AutoLayoutHelper;

import com.zhy.core.utils.AutoUtils;

/**
 * @version 1.0
 * @auth luoxy
 * @date 16/4/20
 */
public class WeatherView extends LinearLayout {


    private TextView mLabel;

    private RecyclerView mRecyclerView;

    private ImageView mIcon;

    private String mLabelStr;// label显示的文字

    private int mLabelColor;// label显示文字的颜色

    private int mLabelSize;// Label显示文字大小

    private int mLabelPaddingLeft;// label距离左边padding

    private int weatherResource;

    private String[] weatherSelector;

    private String currentWeather;

    private Dialog weatherDialog;

    private WeatherAdapter adapter1;

    private WeatherAdapter2 adapter2;

    private int iconResource;

    private int iconWidth;

    private int iconMargin;
    private final AutoLayoutHelper mHelper = new AutoLayoutHelper(this);

    public WeatherView(Context context) {
        this(context, null);
    }

    public WeatherView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeatherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.XS_WeatherView);
            // label 属性设置
            mLabelStr = a.getString(R.styleable.XS_WeatherView_label);
            mLabelStr = (mLabelStr == null) ? "" : mLabelStr;
            // 默认黑色
            mLabelColor = a.getColor(R.styleable.XS_WeatherView_label_color, 0xFF000000);
            mLabelPaddingLeft = a.getDimensionPixelSize(R.styleable.XS_WeatherView_label_padding_left, 0);
            // 默认字体大小30px
            mLabelSize = a.getDimensionPixelSize(R.styleable.XS_WeatherView_label_size,
                    NumberUtil.convertFloatToInt(DisplayUtil.getInstance().getTextScale() * 30));
            weatherResource = a.getResourceId(R.styleable.XS_WeatherView_selector, 0);
            if (0 != weatherResource) {
                weatherSelector = TQEnum.getAllName();
                currentWeather = weatherSelector[0];
            }

            iconResource = a.getResourceId(R.styleable.XS_WeatherView_icon_src, R.drawable.xs_ic_arrow_right);
            iconWidth = a.getDimensionPixelSize(R.styleable.XS_WeatherView_icon_width, -2);
            iconMargin = a.getDimensionPixelSize(R.styleable.XS_WeatherView_icon_margin,
                    NumberUtil.convertFloatToInt(DisplayUtil.getInstance().getTextScale() * 10));

            a.recycle();
        }

        init();
    }

    private void init() {
        this.setOrientation(HORIZONTAL);
        mLabel = new TextView(getContext());
        LayoutParams leftLayoutParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        mLabel.setText(mLabelStr);
        mLabel.setGravity(Gravity.CENTER);
        mLabel.setTextColor(mLabelColor);
        mLabel.setPadding(mLabelPaddingLeft, 0, 0, 0);
        int textSize = AutoUtils.getPercentHeightSizeBigger(mLabelSize);
        mLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        this.addView(mLabel, leftLayoutParam);

        mRecyclerView = new RecyclerView(getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter1 = new WeatherAdapter(mRecyclerView, Arrays.asList(weatherSelector), R.layout.xs_item_weather);
        mRecyclerView.setAdapter(adapter1);
        LayoutParams middleLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        middleLayoutParam.weight = 1;
        this.addView(mRecyclerView, middleLayoutParam);

        mIcon = new ImageView(getContext());
        mIcon.setImageResource(iconResource);
        mIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        mIcon.setPadding(0, 10, 0, 10);
        LayoutParams rightLayoutParam = new LayoutParams(AutoUtils.getPercentWidthSizeBigger(29), AutoUtils.getPercentHeightSizeBigger(48));
        rightLayoutParam.gravity = Gravity.CENTER_VERTICAL;
        rightLayoutParam.rightMargin = iconMargin;
        rightLayoutParam.leftMargin=iconMargin;
        this.addView(mIcon, rightLayoutParam);
        mIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showWeatherDialog();
            }
        });
    }

    class WeatherAdapter extends BaseRecyclerAdapter<String> {

        // public WeatherAdapter(Context context, Collection<String> datas, int itemLayoutId) {
        public WeatherAdapter(RecyclerView v, Collection<String> datas, int itemLayoutId) {
            super(v, datas, itemLayoutId);
            // super(context, datas, itemLayoutId);
        }

        @Override
        public void convert(RecyclerHolder holder, final String item, final int position, boolean isScrolling) {
            RadioButton radioButton = holder.getView(R.id.weather);
            radioButton.setChecked(false);
            radioButton.setText(item);
            if (currentWeather.equals(item))
                radioButton.setChecked(true);
            radioButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentWeather.equals(item)) {
                        currentWeather = "";
                    } else
                        currentWeather = item;
                    adapter1.notifyDataSetChanged();
                    if (null != adapter2) {
                        adapter2.notifyDataSetChanged();
                    }
                    mRecyclerView.scrollToPosition(position);
                }
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
            if (currentWeather.equals(item))
                imgButton.setImageResource(R.drawable.xs_ic_radiobutton_selected);
            else
                imgButton.setImageResource(R.drawable.xs_ic_radiobutton_unselected);

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
            ViewHolder holder = new ViewHolder(getContext(), null, R.layout.xs_layout_dialog, false);
            ListView listView = holder.getView(R.id.weather_list);
            adapter2 = new WeatherAdapter2(getContext(), Arrays.asList(weatherSelector),
                    R.layout.xs_item_weather_vertical1);
            listView.setAdapter(adapter2);
            weatherDialog.setContentView(holder.getRootView(),
                    new ViewGroup.LayoutParams(
                            NumberUtil.convertFloatToInt(DisplayUtil.getInstance().getWidth() * 0.9f),
                            NumberUtil.convertFloatToInt(DisplayUtil.getInstance().getHeightScale() * 800)));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    TextView textView = (TextView) view.findViewById(R.id.tv_weather);
                    String item = textView.getText().toString();
                    if (currentWeather.equals(item)) {
                        currentWeather = "";
                    } else
                        currentWeather = item;
                    adapter1.notifyDataSetChanged();
                    if (null != adapter2) {
                        adapter2.notifyDataSetChanged();
                        weatherDialog.dismiss();
                    }
                    mRecyclerView.scrollToPosition(position);
                }
            });
        }
        weatherDialog.show();

    }

    public void setCurrentWeather(String currentWeather) {

        this.currentWeather = currentWeather == null ? "" : currentWeather;
        adapter1.notifyDataSetChanged();
        if (null != adapter2)
            adapter2.notifyDataSetChanged();
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs)
    {
        return new LayoutParams(getContext(), attrs);
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        if (!isInEditMode())
        {
            mHelper.adjustChildren();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
