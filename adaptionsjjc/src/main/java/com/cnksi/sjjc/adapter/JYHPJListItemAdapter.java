//package com.cnksi.sjjc.adapter;
//
//import android.content.Context;
//import android.support.v4.content.ContextCompat;
//import android.text.TextUtils;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.cnksi.sjjc.R;
//import com.cnksi.sjjc.bean.EvaluationItem;
//import com.cnksi.sjjc.bean.EvaluationItemReport;
//
//import org.xutils.db.table.DbModel;
//import org.xutils.view.annotation.ViewInject;
//import org.xutils.x;
//
//import java.util.HashMap;
//import java.util.List;
//
///**
// * 设备列表(DeviceListActivity) 的 设备ListAdapter
// *
// * @author Joe
// *
// */
//public class JYHPJListItemAdapter extends SimpleBaseAdapter {
//	public static final int PARENT = 0;// 2种不同的布局
//	public static final int ITEM = 1;
//
//	public OnListItemClick listener;
//
//	public JYHPJListItemAdapter(Context context) {
//		super(context);
//	}
//
//	public JYHPJListItemAdapter(Context context, List<? extends Object> dataList) {
//		super(context, dataList);
//	}
//	private EvaluationItem parentItem;
//	public void setItemClickListener(OnListItemClick listener,EvaluationItem item)
//	{
//		this.parentItem=item;
//		this.listener=listener;
//	}
//	HashMap<Integer,Integer> relationTable=new HashMap<Integer, Integer>();
//	@Override
//	public int getItemViewType(int position) {
//		 DbModel item= (DbModel) getItem(position);
//		if (TextUtils.isEmpty(item.getString(EvaluationItem.ITEM_CHECK_OPTION)))
//			return 0;
//		else
//			return  1;
//	}
//	private int currentParent;
//	@Override
//	public int getViewTypeCount() {
//		return 2;
//	}
//
//	@Override
//	public View getView(final int position, View convertView, ViewGroup parent) {
//
//		final DbModel item = (DbModel) getItem(position);
//		int type = getItemViewType(position);
//		ViewHolderItem holderItem=null;
//		ViewHolderParent holderParent=null;
//		if (convertView==null)
//		{
//			switch (type)
//			{
//				case PARENT:
//
//					holderParent=new ViewHolderParent();
//					convertView=mInflater.inflate(R.layout.layout_jyhpj_textview,null);
//					x.view().inject(holderParent,convertView);
//					holderParent.tv.setText(item.getString(EvaluationItem.ITEM_ORDER)+"、"+item.getString(EvaluationItem.ITEM_CONTENT));
//					convertView.setTag(holderParent);
//
//					break;
//				case ITEM:
//					holderItem=new ViewHolderItem();
//					convertView=mInflater.inflate(R.layout.layout_jyhpj_textview1,null);
//					x.view().inject(holderItem,convertView);
//					holderItem.tv.setText(item.getString(EvaluationItem.ITEM_CONTENT));
//					convertView.setTag(holderItem);
//					break;
//				default:
//					break;
//			}
//		}else {
//			switch (type){
//				case PARENT:
//					holderParent= (ViewHolderParent) convertView.getTag();
//					holderParent.tv.setText(item.getString(EvaluationItem.ITEM_ORDER)+"、"+item.getString(EvaluationItem.ITEM_CONTENT));
//					break;
//				case ITEM:
//					holderItem= (ViewHolderItem) convertView.getTag();
//					holderItem.tv.setText(item.getString(EvaluationItem.ITEM_CONTENT));
//					break;
//				default:
//					break;
//			}
//		}
//		if(type==ITEM)
//		{
//			relationTable.put(position,currentParent);
//			if (item.getFloat(EvaluationItemReport.DISCOUNT_SCORE)>0) {
//				convertView.setBackgroundResource(R.color.dis_score_item3);
//				holderItem.imgKouFen.setVisibility(View.VISIBLE);
//			}else{
//				convertView.setBackgroundResource(R.color.white);
//				holderItem.imgKouFen.setVisibility(View.INVISIBLE);
//			}
//			final int p=relationTable.get(position);
//			convertView.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//
//					listener.onItemClick(item,(DbModel)getItem(p),parentItem);
//				}
//			});
//			holderItem.tv.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					listener.onItemClick(item,(DbModel)getItem(p),parentItem);
//				}
//			});
//		}else {
//			currentParent=position;
//			if (item.getFloat(EvaluationItemReport.DISCOUNT_SCORE)>0) {
//				convertView.setBackgroundResource(R.color.dis_score_item2);
////				mContext.getResources().getColor(R.color.dis_score_text_item2)
//				holderParent.tv.setTextColor(ContextCompat.getColor(mContext,R.color.dis_score_text_item2));
//			}else{
//				convertView.setBackgroundResource(R.color.grass_color);
////				mContext.getResources().getColor(R.color.green_color)
//				holderParent.tv.setTextColor(ContextCompat.getColor(mContext,R.color.green_color));
//			}
//		}
//		return convertView;
//	}
//
//	class ViewHolderParent {
//		@ViewInject(R.id.text)
//		 TextView tv;//标题
//	}
//
//	class ViewHolderItem {
//		@ViewInject(R.id.text)
//		TextView tv;//内容
//		@ViewInject(R.id.have_koufen)
//		ImageView imgKouFen;
//	}
//
//	public interface OnListItemClick{
//		void onItemClick(DbModel item,DbModel parentItem,EvaluationItem root);
//	}
//
//
//}
