package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.AcceptStandCard;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

public class CardTypeAdapter extends SimpleBaseAdapter {

	public CardTypeAdapter(Context context, List<? extends Object> dataList) {
		super(context, dataList);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AcceptStandCard acceptType = (AcceptStandCard) getItem(position);
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.yanshou_popwindow, parent, false);
			x.view().inject(holder,convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tvPop.setText(acceptType.acceptStanderCardName);
		holder.img.setVisibility(View.GONE);
		return convertView;
	}

	static class ViewHolder {	
		@ViewInject(R.id.tv_panorama)
		private  TextView tvPop;
		@ViewInject(R.id.iv_arrow)
		private ImageView img;
	}

}
