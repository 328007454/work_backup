//package com.cnksi.sjjc.adapter;
//
//import android.content.Context;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.cnksi.core.view.FollowListView;
//import com.cnksi.sjjc.R;
//import com.cnksi.sjjc.bean.AcceptCardItem;
//import com.cnksi.sjjc.inter.ItemCommonClickListener;
//import com.cnksi.sjjc.inter.WidgetClickListener;
//
//import org.xutils.view.annotation.ViewInject;
//import org.xutils.x;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
///**
// * Created by han on 2016/5/9.
// * 验收标准大项adapter
// *
// */
//public class YanShouStandardCLExPandAdapter extends BaseMapListExpandableAdapter<String, AcceptCardItem> implements ItemCommonClickListener<AcceptCardItem> {
//    //保证一个大项下面对应的listview的adapter都是唯一的
//    private HashMap<Integer,CaiLiaoYanShouListAdapter> mAdapterMap = new HashMap<Integer, CaiLiaoYanShouListAdapter>();
//    public CaiLiaoYanShouListAdapter mPanorListAdapter;
//    private Context mContext;
//    public String reportId;
//    private WidgetClickListener<AcceptCardItem> itemClickListener;
//
//    public void setItemClickListener(WidgetClickListener<AcceptCardItem> itemClickListener) {
//        this.itemClickListener = itemClickListener;
//    }
//
//    public YanShouStandardCLExPandAdapter(Context context) {
//        super(context);
//        this.mContext = context;
//    }
//
//    @Override
//    public void setGroupMap(HashMap<String, ArrayList<AcceptCardItem>> groupHashMap) {
//        mAdapterMap.clear();
//        super.setGroupMap(groupHashMap);
//    }
//
//    @Override
//    public int getChildrenCount(int groupPosition) {
//        return 1;
//    }
//
//    private ViewChildHolder holder;
//
//    @Override
//    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
//        if (convertView == null) {
//            holder = new ViewChildHolder();
//            convertView = mInflater.inflate(R.layout.expandable_yanshou_gridview_child_item, parent, false);
//            x.view().inject(holder, convertView);
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewChildHolder) convertView.getTag();
//        }
//        ArrayList<AcceptCardItem> itemTitleList = getChildList(groupPosition);
//        mPanorListAdapter = mAdapterMap.get(groupPosition);
//        if (null == mAdapterMap.get(groupPosition)) {
//            mPanorListAdapter = new CaiLiaoYanShouListAdapter(mContext);
//            mPanorListAdapter.reportId = reportId;
//            mPanorListAdapter.setItemClickListener(this);
//            mAdapterMap.put(groupPosition, mPanorListAdapter);
//
//        }
//        //大项下边的小项整个是listview
//        holder.mFollowListView.setAdapter(mPanorListAdapter);
//        mPanorListAdapter.setList(itemTitleList);
//        holder.mFollowListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                itemClickListener.onitemClick((AcceptCardItem) adapterView.getItemAtPosition(i), i, 0, groupPosition);
//            }
//        });
//        return convertView;
//    }
//
//    class ViewChildHolder {
//
//        @ViewInject(R.id.gv_container)
//        private FollowListView mFollowListView;
//    }
//
//
//    @Override
//    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
//        String title = getGroup(groupPosition);
//        ViewGroupHolder holder = null;
//        if (convertView == null) {
//            holder = new ViewGroupHolder();
//            convertView = mInflater.inflate(R.layout.expandlistview_jichu_group, parent, false);
//            x.view().inject(holder, convertView);
//            convertView.setTag(holder);
//
//        } else {
//            holder = (ViewGroupHolder) convertView.getTag();
//        }
//        holder.tv.setText(title);
//        if (isExpanded) {
//            holder.ivArrow.setBackgroundResource(R.mipmap.icon_up);
//        } else {
//            holder.ivArrow.setBackgroundResource(R.mipmap.icon_down);
//        }
//        return convertView;
//    }
//
//    class ViewGroupHolder {
//        @ViewInject(R.id.tv_panorama)
//        private TextView tv;
//        @ViewInject(R.id.iv_arrow)
//        private ImageView ivArrow;
//    }
//
//
//    @Override
//    public void onClick(View v, AcceptCardItem acceptCardItem, int position, int total, ImageView imageView, Button btYanShou, ImageView ivOther) {
//        itemClickListener.onClick(v, acceptCardItem, position, total, imageView, btYanShou, ivOther);
//    }
//
//    @Override
//    public void onClick(View v, AcceptCardItem acceptCardItem, int position) {
//
//    }
//
//}
//
