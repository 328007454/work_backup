//package com.cnksi.sjjc.activity;
//
//
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.os.Message;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.ExpandableListView;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.cnksi.core.utils.BitmapUtil;
//import com.cnksi.core.utils.DateUtils;
//import com.cnksi.core.utils.FunctionUtils;
//import com.cnksi.core.view.CustomerDialog;
//import com.cnksi.sjjc.Config;
//import com.cnksi.sjjc.CustomApplication;
//import com.cnksi.sjjc.R;
//import com.cnksi.sjjc.adapter.JYHPJExpandableListAdapter;
//import com.cnksi.sjjc.adapter.JYHPJListItemAdapter;
//import com.cnksi.sjjc.bean.EvaluationItem;
//import com.cnksi.sjjc.bean.EvaluationItemReport;
//import com.cnksi.sjjc.bean.EvaluationReport;
//import com.cnksi.sjjc.bean.EvaluationType;
//import com.cnksi.sjjc.bean.InputValue;
//
//import org.xutils.DbManager;
//import org.xutils.common.util.KeyValue;
//import org.xutils.db.sqlite.SqlInfo;
//import org.xutils.db.table.DbModel;
//import org.xutils.ex.DbException;
//import org.xutils.view.annotation.Event;
//import org.xutils.view.annotation.ViewInject;
//import org.xutils.x;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//
//public class JYHPJActivity extends BaseActivity implements JYHPJListItemAdapter.OnListItemClick {
//    /**
//     * 得分提示
//     */
//    @ViewInject(R.id.tv_df_tips)
//    private TextView tvDftips;
//    /**
//     * 得分
//     */
//    @ViewInject(R.id.tv_df)
//    private TextView tvDf;
//    /**
//     * 评价日期
//     */
//    @ViewInject(R.id.tv_pjrq)
//    private TextView tvPjrq;
//
//    /**
//     * 签名结果
//     */
//    @ViewInject(R.id.img_qmjg)
//    private ImageView imgQmjg;
//    /**
//     * 基础信息标题
//     */
//    @ViewInject(R.id.baseInfoTitle)
//    TextView baseInfoTitle;    /**
//     * 基础信息
//     */
//    @ViewInject(R.id.baseInfoContainer)
//    LinearLayout baseInfo;
//    /**
//     * expandableListView
//     */
//    @ViewInject(R.id.elv_container)
//    ExpandableListView elvContainer;
//    JYHPJExpandableListAdapter jyhpjAdapter;
//    LinkedList<EvaluationItem> groupList=new LinkedList<EvaluationItem>();
//    HashMap<EvaluationItem,ArrayList<DbModel>> groupHashMap=new HashMap<EvaluationItem, ArrayList<DbModel>>();
//    /**
//     * 签名文件
//     */
//    private String mSignFile="";
//    private EvaluationType currentType;
//    List<InputValue> mInputValue;
//    List<EditViewHolder> editViewHolders=new ArrayList<EditViewHolder>();
//    DbManager db= CustomApplication.getPJDbManager();
//    private EvaluationReport currentEvaReport;
//    private  String typeId="bdzywglpjxz";
//    private float totalSocer=0;
//    private float disScoer =0;
//    private DbModel currentDbModel;
//    private DbModel parentDbModel;
//    private EvaluationItem rootItem;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setChildView(R.layout.activity_jyhpj);
//        initUI();
//        initData();
//        setTitleText("变电运维管理精益化评价");
//    }
//    private void initUI(){
//        setViewVisible(tvRight,View.VISIBLE);
//
//        elvContainer.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//            @Override
//            public void onGroupExpand(int groupPosition) {
//              if (baseInfo.getVisibility()!=View.GONE)
//               baseInfoTitle.performClick();
//            }
//        });
//        try {
//            currentType= db.findFirst(EvaluationType.class);
//
//            if (currentType!=null)
//            {
//                CreateReportIfExists();
//                totalSocer=currentType.totalStore;
//                if(TextUtils.isEmpty(currentEvaReport.evaluationDate))
//                    tvPjrq.setText(DateUtils.getCurrentShortTime());
//                else
//                    tvPjrq.setText(currentEvaReport.evaluationDate);
//                tvDftips.setText("得分（总分"+(int)totalSocer+")");
//                tvDf.setText(totalSocer+"分");
//                mSignFile=currentEvaReport.evaluationSign;
//                //加载签名
//                if (!TextUtils.isEmpty(mSignFile))
//                {
//                    mFixedThreadPoolExecutor.execute(new Runnable() {
//                        @Override
//                        public void run() {
//                            final Bitmap bitmap=BitmapUtil.getImageThumbnail(Config.SIGN_PICTURE_FOLDER+mSignFile,160,80);
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    imgQmjg.setImageBitmap(bitmap);
//                                }
//                            });
//                        }
//                    });
//                }
//                baseInfoTitle.setText(currentType.baseLabel);
//                if (TextUtils.isEmpty(currentEvaReport.baseInfo)) {
//                    mInputValue = JSON.parseArray(currentType.baseInfo, InputValue.class);
//                }
//                else {
//                    mInputValue = JSON.parseArray(currentEvaReport.baseInfo, InputValue.class);
//                }
//                initBaseInfo();
//            }
//        } catch (DbException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//     private  void initBaseInfo() {
//         if (mInputValue != null){
//             int size= mInputValue.size();
//             int count=(size+1)/2;
//
//                 for (int i = 0; i < count;i++ ) {
//                        View v= LayoutInflater.from(this).inflate(R.layout.layout_jyhpj_edittext,null);
//                        EditViewHolder holder=new EditViewHolder();
//                        x.view().inject(holder,v);
//                        if (i*2+1==size){
//                            FillBaseInfo(holder,mInputValue.get(i*2),null);
//                            setViewVisible(v.findViewById(R.id.line),View.INVISIBLE);
//                            setViewVisible(v.findViewById(R.id.ll_two),View.INVISIBLE);
//                        }else {
//                            FillBaseInfo(holder,mInputValue.get(i*2),mInputValue.get(i*2+1));
//                        }
//                        editViewHolders.add(holder);
//                        baseInfo.addView(v);
//                        View line=new View(mCurrentActivity);
//                     line.setLayoutParams(new LinearLayout.LayoutParams(-1,2));
//                     baseInfo.addView(line);
//                 }
//        }
//     }
//
//    @Override
//    protected void onRefresh(Message msg) {
//        switch (msg.what)
//        {
//            case LOAD_DATA:
//                if (jyhpjAdapter == null) {
//                    jyhpjAdapter = new JYHPJExpandableListAdapter(_this);
//                    jyhpjAdapter.setItemClickListener(this);
//                    tvDf.setText((totalSocer- disScoer)+"分");
//                    elvContainer.setAdapter(jyhpjAdapter);
//                }
//                jyhpjAdapter.setGroupList(groupList);
//                jyhpjAdapter.setGroupMap(groupHashMap);
//            case REFRESH_DATA:
//                tvDf.setText((totalSocer- disScoer)+"分");
//                jyhpjAdapter.notifyDataSetChanged();
//               break;
//        }
//    }
//
//    private void  initData(){
//        mFixedThreadPoolExecutor.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                   if(!db.getTable(EvaluationItemReport.class).tableIsExist())
//                    {
//                        EvaluationItemReport _t=new EvaluationItemReport();
//                       db.save(_t);
//                        db.deleteById(EvaluationItemReport.class,_t.uid);
//                    }
//                    List<EvaluationItem> mList=  db.selector(EvaluationItem.class).expr("parent_id is null").and(EvaluationItem.TYPE_ID,"=",typeId).orderBy(EvaluationItem.ITEM_ORDER).findAll();
//                    groupList.clear();
//                    groupHashMap.clear();
//                    String str;
//                    for (EvaluationItem parentItem:mList)
//                    {
//                        float itemTotalScoer=0;
//                        SqlInfo sql=new SqlInfo("SELECT * FROM evaluation_item where parent_id=?  order by   item_order");
//                        sql.addBindArg(new KeyValue("itemId",parentItem.itemId));
//                        List<DbModel> childList=db.findDbModelAll(sql);
//                        ArrayList<DbModel> childTemp=new ArrayList<DbModel>();
//                        for (DbModel childItem:childList)
//                        {
//
//                            sql=new SqlInfo("SELECT *,CASE WHEN b.discount_score ISNULL THEN 0 ELSE  b.discount_score END as discount_score FROM evaluation_item a  LEFT JOIN evaluation_item_report b ON b.evaluation_item_id = a.item_id " +
//                                    "AND b.report_id =? where a.parent_id=?  order by item_order");
//                            sql.addBindArg(new KeyValue("reportId",currentEvaReport.reportId));
//                            sql.addBindArg(new KeyValue("parent_id",childItem.getString(EvaluationItem.ITEM_ID)));
//                            List<DbModel> modelList=db.findDbModelAll(sql);
//                            float childItemScoer=0;
//                            for (DbModel model:modelList)
//                            {
//                                str=model.getString(EvaluationItemReport.DISCOUNT_SCORE);
//                                if (!TextUtils.isEmpty(str))
//                                    childItemScoer=childItemScoer+Float.valueOf(str);
//                            }
//                            childItem.add(EvaluationItemReport.DISCOUNT_SCORE,childItemScoer+"");
//                            childTemp.add(childItem);
//                            childTemp.addAll(modelList);
//                            itemTotalScoer=itemTotalScoer+childItemScoer;
//                        }
//                        parentItem.disScore=itemTotalScoer;
//                        disScoer = disScoer +itemTotalScoer;
//                        groupList.add(parentItem);
//                        groupHashMap.put(parentItem,childTemp);
//                    }
//                    mHandler.sendEmptyMessage(LOAD_DATA);
//                } catch (DbException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//    @Event(value = {R.id.img_pjrq,R.id.img_qm,R.id.baseInfoTitle,R.id.tv_right})
//   private void OnClick(View v) {
//        switch (v.getId())
//        {
//            case R.id.img_pjrq:
//                CustomerDialog.showDatePickerDialog(mCurrentActivity, false, new CustomerDialog.DialogItemClickListener() {
//                    @Override
//                    public void confirm(String result, int position) {
//                       tvPjrq.setText(result);
//                    }
//                });
//                break;
//            case R.id.img_qm:
//                Intent intent=new Intent(_this,SignNameActivity.class);
//                mSignFile=FunctionUtils.getPrimarykey()+Config.IMAGE_JPG_POSTFIX;
//                intent.putExtra(Config.SIGN_FILENAME,Config.SIGN_PICTURE_FOLDER+mSignFile);
//                startActivityForResult(intent,0x124);
//                break;
//            case R.id.baseInfoTitle:
//                if(baseInfo.getVisibility()==View.GONE)
//                {
//                    baseInfo.setVisibility(View.VISIBLE);
//                    baseInfoTitle.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.icon_down,0);
//                }else {
//                    baseInfo.setVisibility(View.GONE);
//                    baseInfoTitle.setCompoundDrawablesWithIntrinsicBounds(0,0,R.mipmap.icon_up,0);
//                }
//                break;
//            case R.id.tv_right:
//                saveData();
//                finish();
//                break;
//            default:
//                break;
//        }
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode==RESULT_OK)
//        {
//            switch (requestCode){
//                //签名返回逻辑
//                case 0x124:
//                    imgQmjg.setImageBitmap(BitmapUtil.getImageThumbnail(Config.SIGN_PICTURE_FOLDER+mSignFile,160,80));
//                    break;
//                //填数据之后返回
//                case 0x125:
//                    boolean isUpdate= data.getBooleanExtra(Config.DATA,false);
//                    float score=data.getFloatExtra(Config.DATA1,0);
//                    float oldscore=currentDbModel.getFloat(EvaluationItemReport.DISCOUNT_SCORE);
//                    if (isUpdate) {
//                        disScoer = disScoer -oldscore+score;
//                        rootItem.disScore=rootItem.disScore-oldscore+score;
//                        float oldParent=parentDbModel.getFloat(EvaluationItemReport.DISCOUNT_SCORE);
//                        oldParent=oldParent-oldscore+score;
//                        parentDbModel.add(EvaluationItemReport.DISCOUNT_SCORE,oldParent+"");
//                    }else {
//                        float oldParent=parentDbModel.getFloat(EvaluationItemReport.DISCOUNT_SCORE);
//                        oldParent=oldParent+score;
//                        parentDbModel.add(EvaluationItemReport.DISCOUNT_SCORE,oldParent+"");
//                        disScoer = disScoer +score;
//                        rootItem.disScore+=score;
//                    }
//                    currentDbModel.add(EvaluationItemReport.DISCOUNT_SCORE, score + "");
//                    mHandler.sendEmptyMessage(REFRESH_DATA);
//                    break;
//            }
//        }
//    }
//
//    private void FillBaseInfo(final EditViewHolder holder, InputValue bean, InputValue bean1) {
//        if (bean!=null)
//        {
//           holder.name.setText(bean.name);
//            if (bean.IsDate())
//            {
//                setViewVisible(holder.input,View.GONE);
//                setViewVisible(holder.calender,View.VISIBLE);
//                setViewVisible(holder.imgCalendar,View.VISIBLE);
//                holder.calender.setText(bean.value);
//                holder.imgCalendar.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        CustomerDialog.showDatePickerDialog(mCurrentActivity, false, new CustomerDialog.DialogItemClickListener() {
//                            @Override
//                            public void confirm(String result, int position) {
//                                holder.calender.setText(result);
//                                holder.input.setText(result);
//                            }
//                        });
//                    }
//                });
//            }
//                holder.input.setText(bean.value);
//        }
//        if (bean1!=null)
//        {
//            holder.name1.setText(bean1.name);
//            if (bean1.IsDate())
//            {
//                setViewVisible(holder.input1,View.GONE);
//                setViewVisible(holder.calender1,View.VISIBLE);
//                holder.calender1.setText(bean1.value);
//                setViewVisible(holder.imgCalendar1,View.VISIBLE);
//                holder.imgCalendar1.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        CustomerDialog.showDatePickerDialog(mCurrentActivity, false, new CustomerDialog.DialogItemClickListener() {
//                            @Override
//                            public void confirm(String result, int position) {
//                                holder.calender1.setText(result);
//                                holder.input1.setText(result);
//                            }
//                        });
//                    }
//                });
//            }
//            holder.input1.setText(bean1.value);
//        }
//    }
////
////
////    public void groupData()
////    {
////        if (groupList!=null&&groupHashMap!=null)
////        {
////            int count=groupList.size();
////            for (int i=0;i<count;i++)
////            {
////                EvaluationItem item=groupList.get(i);
////                ArrayList<DbModel> dbModels=groupHashMap.get(item);
////                if (dbModels!=null)
////                {
////                    int dc=dbModels.size();
////
////                    for (int k=0;k<dc;k++)
////                    {
////
////                    }
////                }
////            }
////        }
////    }
//
//
//    class EditViewHolder{
//        @ViewInject(R.id.tv_name)
//        TextView name;
//        @ViewInject(R.id.et_input)
//        EditText input;
//        @ViewInject(R.id.tv_calendar)
//        TextView calender ;
//        @ViewInject(R.id.img_calendar)
//        ImageView imgCalendar;
//        @ViewInject(R.id.tv_name1)
//        TextView name1;
//        @ViewInject(R.id.et_input1)
//        EditText input1;
//        @ViewInject(R.id.tv_calendar1)
//        TextView calender1 ;
//        @ViewInject(R.id.img_calendar1)
//        ImageView imgCalendar1;
//    }
//
//    public void CreateReportIfExists() {
//        try {
//            //currentEvaReport= db.selector(EvaluationReport.class).where(EvaluationReport.TYPE_ID,"=",typeId).findFirst();
//           // if (currentEvaReport==null)
//           // {
//                currentEvaReport=new EvaluationReport();
//                currentEvaReport.typeId=currentType.typeId;
//                currentEvaReport.typeName=currentType.typeName;
//                db.save(currentEvaReport);
//          //  }
//        } catch (DbException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void saveData() {
//        int count=mInputValue.size();
//        for (int i=0;i<count;i++)
//        {
//            if (i%2==0)
//            mInputValue.get(i).value=editViewHolders.get(i/2).input.getText().toString();
//            else
//                mInputValue.get(i).value=editViewHolders.get(i/2).input1.getText().toString();
//        }
//        if (count>0)
//        {
//            currentEvaReport.baseInfo= JSONArray.toJSONString(mInputValue);
//        }
//        currentEvaReport.evaluationSign=mSignFile;
//        currentEvaReport.evaluationDate=tvPjrq.getText().toString();
//        currentEvaReport.finalStore=totalSocer;
//        try {
//            db.saveOrUpdate(currentEvaReport);
//        } catch (DbException e) {
//            e.printStackTrace();
//        }
//    }
//    @Override
//    public void onItemClick(DbModel item,DbModel parent,EvaluationItem root) {
//        currentDbModel=item;
//        parentDbModel=parent;
//        rootItem=root;
//        Intent intent=new Intent(_this, PJMainActivity.class);
//        EvaluationItem bean=new EvaluationItem();
//        bean.itemId=item.getString(EvaluationItem.ITEM_ID);
//        bean.itemContent=item.getString(EvaluationItem.ITEM_CONTENT);
//        bean.itemCheckOption=item.getString(EvaluationItem.ITEM_CHECK_OPTION);
//        bean.itemCheckExtra=item.getString(EvaluationItem.ITEM_CHECK_EXTRA);
//        bean.itemCheckStander=item.getString(EvaluationItem.ITEM_CHECK_STANDER);
//        bean.itemCheckStyle=item.getString(EvaluationItem.ITEM_CHECK_STYLE);
//        bean.parentId=item.getString(EvaluationItem.PARENT_ID);
//        bean.parentId=item.getString(EvaluationItem.PARENT_ID);
//        intent.putExtra(Config.DATA1,currentEvaReport);
//        intent.putExtra(Config.DATA,bean);
//        startActivityForResult(intent,0x125);
//    }
//}
