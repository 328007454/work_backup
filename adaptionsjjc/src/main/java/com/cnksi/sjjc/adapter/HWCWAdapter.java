package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.cnksi.core.adapter.BaseAdapter;
import com.cnksi.core.adapter.ViewHolder;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.ReportHwcw;

import java.util.List;


/**
 * @author han 红外测温界面的适配器
 */
public class HWCWAdapter extends BaseAdapter<ReportHwcw>{

	private  boolean isBhpcw;
	public HWCWAdapter(Context context, List<ReportHwcw> data, int layoutId, boolean isBhpcw) {
		super(context, data, layoutId);
		this.isBhpcw=isBhpcw;
	}

	public int getCurrentSelect() {
		return currentSelect;
	}

	public void setCurrentSelect(int currentSelect) {
		this.currentSelect = currentSelect;
	}

	/**
	 * 当前选中的条目
	 */
	private int currentSelect=-1;
	@Override
	public void convert(ViewHolder holder, ReportHwcw item, int position) {

		TextView tv=holder.getView(R.id.tv_defect_degree_item);
			tv.setVisibility(View.GONE);
		holder.setText(R.id.tv_heat_device_item,item.device_name);
		if (isBhpcw){
			String display="";

			holder.setText(R.id.tv_temp_item,compareTemp(item.bhpbmwd,item.bhpzmwd)+ Config.TEMPPERATURE_POSTFIX);
		}else
		{
			holder.setText(R.id.tv_temp_item,item.frdwd+ Config.TEMPPERATURE_POSTFIX);
		}
		if (position==currentSelect)
		{
			holder.setVisable(R.id.tv_editting_show,View.VISIBLE);
		}else{
			holder.setVisable(R.id.tv_editting_show,View.GONE);
		}
	}
	String compareTemp(String s1,String s2)
	{
		if (TextUtils.isEmpty(s1))
			s1=Integer.MIN_VALUE+"";
		if (TextUtils.isEmpty(s2))
			s2=Integer.MIN_VALUE+"";
		float f1=	Float.valueOf(s1);
		float f2=Float.valueOf(s2);
		if (f1>f2)
		{
			return  f1+"";
		}else{
			return f2+"";
		}
	}

}
