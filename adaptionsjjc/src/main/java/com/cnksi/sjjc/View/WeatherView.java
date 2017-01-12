package com.cnksi.sjjc.View;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.cnksi.core.adapter.BaseRecyclerAdapter;
import com.cnksi.core.adapter.RecyclerHolder;
import com.cnksi.core.utils.DisplayUtil;
import com.cnksi.core.utils.NumberUtil;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.BaseAdapter;
import com.cnksi.sjjc.adapter.ViewHolder;
import com.cnksi.sjjc.databinding.ArrowBinding;
import com.cnksi.sjjc.databinding.WeatherTitleBinding;
import com.zhy.autolayout.utils.AutoLayoutHelper;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.Arrays;
import java.util.Collection;

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

    public WeatherView(Context context) {
        this(context, null);
    }

    public WeatherView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeatherView(Context context, AttributeSet attrs, int defStyleAttr) {
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
                    NumberUtil.convertFloatToInt(DisplayUtil.getInstance().getTextScale() * 30));

            weatherResource = a.getResourceId(R.styleable.WeatherView_selector, 0);
            if (0 != weatherResource) {
                weatherSelector = a.getResources().getStringArray(weatherResource);
                currentWeather = weatherSelector[0];
            }

            iconResource = a.getResourceId(R.styleable.WeatherView_icon_src, R.drawable.ic_arrow_right);
            iconHeight = a.getDimensionPixelOffset(R.styleable.WeatherView_icon_height, 48);
            iconWidth = a.getDimensionPixelSize(R.styleable.WeatherView_icon_width, 29);
            iconMargin = a.getDimensionPixelSize(R.styleable.WeatherView_icon_margin,
                    NumberUtil.convertFloatToInt(DisplayUtil.getInstance().getTextScale() * 10));

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
            AutoUtils.autoSize(bingding.getRoot());
            titleBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.weather_title_layout, null, false);
            AutoUtils.autoSize(titleBinding.getRoot());
        }

//        mLabel = new TextView(getContext());
        mLabel = titleBinding.title;
        LayoutParams leftLayoutParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mLabel.setText(mLabelStr);
//        mLabel.setGravity(Gravity.CENTER);
        mLabel.setTextColor(mLabelColor);
        mLabel.setPadding(mLabelPaddingLeft, 0, 0, 0);
        mLabel.setLayoutParams(leftLayoutParam);
        mLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, mLabelSize);
//        this.addView(mLabel, leftLayoutParam);
        this.addView(titleBinding.getRoot());

        mRecyclerView = new RecyclerView(getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter1 = new WeatherAdapter(mRecyclerView, Arrays.asList(weatherSelector), R.layout.item_weather);
        mRecyclerView.setAdapter(adapter1);
        LayoutParams middleLayoutParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        middleLayoutParam.weight = 1;

        this.addView(mRecyclerView, middleLayoutParam);

//        mIcon = new ImageView(getContext());
//        mIcon.setImageResource(iconResource);
//        mIcon.setScaleType(ImageView.ScaleType.FIT_XY);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(iconWidth,iconHeight);
//        layoutParams.setMargins(iconWidth,0,0,0);
//        mIcon.setLayoutParams(layoutParams);
//        this.addView(mIcon);
        this.addView(bingding.getRoot());


//        mIcon.setPadding(0, 10, 0, 10);
//        LayoutParams rightLayoutParam = new LayoutParams(AutoUtils.getPercentWidthSizeBigger(29), AutoUtils.getPercentHeightSizeBigger(48));
//        LayoutParams rightLayoutParam = new LayoutParams(29, 48);
//        LayoutParams rightLayoutParam = new LayoutParams(iconWidth, LayoutParams.MATCH_PARENT);
//        rightLayoutParam.gravity = Gravity.CENTER_VERTICAL;
//        rightLayoutParam.rightMargin = iconMargin;
//        rightLayoutParam.leftMargin=iconMargin;
//        this.addView(mIcon, rightLayoutParam);
        bingding.arrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showWeatherDialog();
            }
        });
//        int textSize = AutoUtils.getPercentWidthSizeBigger(mLabelSize);
//        mLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
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
                imgButton.setImageResource(R.drawable.ic_radiobutton_blue_selected);
            else
                imgButton.setImageResource(R.drawable.ic_radiobutton_unselected);

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
