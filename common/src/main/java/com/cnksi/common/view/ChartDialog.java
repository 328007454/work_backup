package com.cnksi.common.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cnksi.common.R;
import com.cnksi.common.databinding.CommonDialogChartBinding;
import com.cnksi.common.utils.CalcUtils;
import com.cnksi.core.utils.DisplayUtils;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 图标弹出框
 *
 * @author luoxy
 * @date 2016/6/17
 * @copyRight
 */
public class ChartDialog {

	private Dialog dialog;

	private static ChartDialog instance;

	public static ChartDialog getInstance() {
		if (instance == null) {
            instance = new ChartDialog();
        }
		return instance;
	}

	private void dismissDialog() {
		if (null != dialog && dialog.isShowing()) {
			Context context = dialog.getContext();
			if (context instanceof Activity) {
				Activity activity = (Activity) context;
				if (!activity.isFinishing()) {
                    dialog.dismiss();
                }
			}
		}
	}

	public Dialog getDialog() {
		return dialog;
	}

	public void showLineChartDialog(Context context, String title, String xLabel, String yLabel, List<String> xValues,
			List<LineSet> yValues, List<Integer> yColors) {
		dismissDialog();
		dialog = new Dialog(context, R.style.dialog);
		CommonDialogChartBinding holder = CommonDialogChartBinding.inflate(LayoutInflater.from(context));
		holder.title.setText(title);
		holder.xLabel.setText(xLabel);
		holder.yLabel.setText(yLabel);
		holder.btnBack.setVisibility( View.GONE);
		holder.btnBack.setOnClickListener(v -> dialog.dismiss());

		LineChart chart = holder.lineChart;
		chart.setDescription("");
		chart.setNoDataTextDescription("无图形数据");
		XAxis xAxis = chart.getXAxis();
		xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
		xAxis.setTextColor(context.getResources().getColor(R.color.xs_mpchart_asia_color));
		xAxis.setTextSize(10f);
		xAxis.setDrawGridLines(false);

		YAxis yAxis = chart.getAxisLeft();
		yAxis.removeAllLimitLines();
		yAxis.enableGridDashedLine(10f, 0f, 0f);
		yAxis.setDrawZeroLine(false);
		yAxis.setDrawLimitLinesBehindData(true);
		yAxis.setTextColor(context.getResources().getColor(R.color.xs_mpchart_asia_color));
		yAxis.setTextSize(10f);

		chart.getAxisRight().setEnabled(false);
		chart.animateXY(3000, 0);

		List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
		// 设置数据
		int index = 0;
		for (LineSet key : yValues) {
			List<Entry> entryList = new ArrayList<Entry>();
			int i = 0;
			for (Float value : key.getSet()) {
				entryList.add(new Entry(value.floatValue(), i));
				i++;
			}
			LineDataSet dataSet = new LineDataSet(entryList, key.getKey());

			dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
			dataSet.setColor(context.getResources().getColor(yColors.get(index)));
			dataSet.setCircleColor(context.getResources().getColor(yColors.get(index)));
			dataSet.setLineWidth(2f);
			dataSet.setValueTextColor(context.getResources().getColor(yColors.get(index)));
			dataSet.setCircleRadius(3f);
			dataSet.setFillAlpha(65);
			dataSet.setFillColor(context.getResources().getColor(yColors.get(index)));
			dataSet.setHighLightColor(context.getResources().getColor(yColors.get(index)));
			dataSet.setDrawCircleHole(false);

			dataSets.add(dataSet);
			index++;

		}
		LineData data = new LineData(xValues, dataSets);
		data.setValueFormatter(new MyValueFormatter());
		chart.setData(data);

		dialog.setContentView(holder.getRoot(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
				CalcUtils.convertFloatToInt(DisplayUtils.getInstance().getScale() * 800)));
		dialog.show();
	}

	public void showLineChartDialog(Context context, String title, String xLabel, String yLabel, List<String> xValues,
			String lineName, List<Float> yValues, int color) {
		List<LineSet> yValue = new ArrayList<>();
		yValue.add(new LineSet(lineName, yValues));
		showLineChartDialog(context, title, xLabel, yLabel, xValues, yValue, Arrays.asList(color));

	}

	public static class LineSet {
		String key;
		List<Float> set;

		public LineSet(String key, List<Float> set) {
			this.key = key;
			this.set = set;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public List<Float> getSet() {
			return set;
		}

		public void setSet(List<Float> set) {
			this.set = set;
		}
	}
	/**
	 * Created by wastrel on 2016/7/7.
	 */
	public static class MyValueFormatter implements ValueFormatter {

		private DecimalFormat mFormat;

		public MyValueFormatter() {

			this.mFormat = new DecimalFormat("###,###,###,###.####");
		}

		@Override
		public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
			return this.mFormat.format(v);
		}
	}
}
