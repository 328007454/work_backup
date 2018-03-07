package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cnksi.core.utils.RelayoutUtil;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.view.MyListView;
import com.cnksi.sjjc.bean.EvaluationItem;

import org.xutils.db.table.DbModel;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;


public class JYHPJExpandableListAdapter extends BaseMapListExpandableAdapter<EvaluationItem, DbModel> {


    private SparseArray<JYHPJListItemAdapter> mAdapterMap = new SparseArray<JYHPJListItemAdapter>();


    public JYHPJExpandableListAdapter(Context context) {
        super(context);
    }
    public JYHPJListItemAdapter.OnListItemClick listener;
    public void setItemClickListener(JYHPJListItemAdapter.OnListItemClick listener)
    {
        this.listener=listener;
    }
    @Override
    public void setGroupMap(HashMap<EvaluationItem, ArrayList<DbModel>> groupMap) {
        mAdapterMap.clear();
        super.setGroupMap(groupMap);
    }



    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }



    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
//        if (groupPosition>0)
//        groupPosition=groupPosition-1;
//        else
//        {
//            TextView tv=new TextView(mContext);
//            tv.setText("1111121312312312312312svdmsdnfasukbdkawsbdkjashhdaishdisla");
//            return tv;
//        }
        ViewChildHolder holder = null;
        if (convertView == null) {
            holder = new ViewChildHolder();
            convertView = mInflater.inflate(R.layout.layout_list, parent, false);
            RelayoutUtil.reLayoutViewHierarchy(convertView);
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewChildHolder) convertView.getTag();
        }
        JYHPJListItemAdapter adapter= mAdapterMap.get(groupPosition);
        if (adapter==null)
        {
            adapter = new JYHPJListItemAdapter(mContext);
            adapter.setItemClickListener(listener,groupList.get(groupPosition));
            mAdapterMap.put(groupPosition, adapter);
        }

        ArrayList<DbModel> mList = getChildList(groupPosition);
        adapter.setList(mList);
        holder.setCurrentEvaluationAndParent(mList, getGroup(groupPosition));
        holder.mFollowListView.setAdapter(adapter);
        return convertView;
    }
    class ViewChildHolder {
        private ArrayList<DbModel> mEvaluationList;

        private EvaluationItem mCurrentEvaluation;

        public void setCurrentEvaluationAndParent(ArrayList<DbModel> mDeviceList, EvaluationItem mCurrentSpacing) {
            this.mEvaluationList = mDeviceList;
            this.mCurrentEvaluation = mCurrentSpacing;
        }

        @ViewInject(R.id.lv_container)
        private MyListView mFollowListView;


    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        EvaluationItem group = getGroup(groupPosition);

        ViewGroupHolder holder = null;
        if (convertView == null) {
            holder = new ViewGroupHolder();
            convertView = mInflater.inflate(R.layout.layout_jyhpj_textview3, parent, false);
            x.view().inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewGroupHolder) convertView.getTag();
        }
        holder.evaluationItem = group;
        holder.groupPosition = groupPosition;
        holder.tvLeft
                .setText(group.itemContent);

        if (group.disScore>0) {
            holder.tvRight.setText("累计-"+group.disScore+"分");
            convertView.setBackgroundResource(R.color.dis_score_item1);
        }else {
            convertView.setBackgroundResource(R.color.green_color);
            holder.tvRight.setText("累计-0分");
        }

        if (isExpanded) {
            holder.tvRight.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_down, 0);
        } else {
            holder.tvRight.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.mipmap.icon_up, 0);
        }
        return convertView;
    }

    class ViewGroupHolder {

        public EvaluationItem evaluationItem;

        public int groupPosition;

        @ViewInject(R.id.textleft)
        TextView tvLeft;
        @ViewInject(R.id.textright)
        TextView tvRight;

    }
}
