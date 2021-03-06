package com.cnksi.bdzinspection.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;

import com.cnksi.bdzinspection.R;
import com.cnksi.common.base.BaseAdapter;
import com.cnksi.common.listener.ItemClickListener;
import com.cnksi.common.model.Spacing;
import com.cnksi.common.utils.CalcUtils;
import com.cnksi.common.utils.ViewHolder;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 定位间隔adapter
 * 
 * @author lyndon
 * 
 */
public class LocationSpacingAdapter extends BaseAdapter<Spacing> {

	private ItemClickListener<Spacing> itemClickListener;

	public void setItemClickListener(ItemClickListener<Spacing> itemClickListener) {
		this.itemClickListener = itemClickListener;
	}

	public LocationSpacingAdapter(Context context, Collection<Spacing> data, int layoutId) {
		super(context, data, layoutId);
	}

	@Override
	public void convert(ViewHolder holder, final Spacing item, final int position) {
		String result = getDigitsArray(item.name);
		((TextView) holder.getView(R.id.name)).setText(Html.fromHtml(result));
		holder.setText(R.id.distance, context.getString(R.string.xs_near_distance, CalcUtils.formatNumber(item.distance, "#.#")));

		holder.getRootView().setOnClickListener(v -> {
            if (null != itemClickListener) {
                itemClickListener.onClick(v, item, position);
            }
        });

	}

	/**
	 * 获取名称中数字
	 * 
	 * @param itemName
	 * @return
	 */
	private String getDigitsArray(String itemName) {
		Pattern pattern = Pattern.compile("[0-9]+");
		Matcher matcher = pattern.matcher(itemName);
		String result = "";
		if (matcher.find()) {
			result = matcher.replaceAll("<font color=\"#03B9A0\">$0</font>");
		}
		result = TextUtils.isEmpty(result) ? itemName : result;
		return result;
	}

}
