package com.cnksi.sjjc.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cnksi.core.fragment.BaseCoreFragment;
import com.cnksi.core.json.JSONUtils;
import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.DateUtils;
import com.cnksi.core.utils.ScreenUtils;
import com.cnksi.core.utils.StringUtils;
import com.cnksi.core.view.CustomerDialog;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.View.CustomRadioButton;
import com.cnksi.sjjc.activity.DialogActivity;
import com.cnksi.sjjc.activity.ImageDetailsActivity;
import com.cnksi.sjjc.activity.SignNameActivity;
import com.cnksi.sjjc.adapter.CardTypeAdapter;
import com.cnksi.sjjc.adapter.YanShouStandardCLExPandAdapter;
import com.cnksi.sjjc.bean.AcceptCardItem;
import com.cnksi.sjjc.bean.AcceptReport;
import com.cnksi.sjjc.bean.AcceptReportItem;
import com.cnksi.sjjc.bean.AcceptStandCard;
import com.cnksi.sjjc.inter.WidgetClickListener;
import com.cnksi.sjjc.util.DialogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

/**
 * Created by han on 2016/5/9.
 * 验收标准卡Fragment
 */
public class YanShouStandardFargment extends BaseCoreFragment implements WidgetClickListener<AcceptCardItem> {
    //间距背景
    @ViewInject(R.id.view_background)
    private View viewBack;
    //验收标准卡所有验收详情根布局
    @ViewInject(R.id.ll_root_container)
    private LinearLayout llContainer;
    //动态添加布局
    @ViewInject(R.id.ll_container_add)
    private RelativeLayout relContainer;
    //材料信息展示
    @ViewInject(R.id.elv_container)
    private ExpandableListView exListView;
    //基础信息显示标题
    @ViewInject(R.id.tv_group_title)
    private TextView tvGroupTitle;
    @ViewInject(R.id.tv_panorama)
    private TextView mTvPanorama;
    //基础信息展示listview
    @ViewInject(R.id.il_base_info)
    private ListView mIlBaseInfo;
    //确认按钮
    @ViewInject(R.id.btn_confirm)
    private Button btConfirm;

    @ViewInject(R.id.iv_image_arrow)
    private ImageView ivArrow;
    //动态添加布局（关于基础信息布局）
    @ViewInject(R.id.ll_dongtai_container)
    private LinearLayout llDTContainer;
    //签名路径
    private String qmPath;
    private YanShouStandardCLExPandAdapter mYaShouCLAdapter;
    //基础信息Group
    LinkedList<String> listGroup = new LinkedList<String>();
    //验收具体大项集合
    LinkedList<String> yanShouListGroup = new LinkedList<String>();
    //验收大项下对应的小项集合
    private LinkedHashMap<String, ArrayList<AcceptCardItem>> yanShouHashMap = new LinkedHashMap<String, ArrayList<AcceptCardItem>>();
    //验收基础信息展示对应的名称
    private ArrayList<String> listPager = new ArrayList<String>();
    //验收基础信息名称对应的类型(string or  date )
    private ArrayList<String> contentList = new ArrayList<String>();
    //卡类型名字
    private String cardName;
    //验收标准卡对应具体的验收小项
    private AcceptCardItem acceptCardItem;
    //验收标准卡
    private AcceptStandCard acceptStandCard;
    //验收标准卡集合
    private ArrayList<AcceptCardItem> acceptCardItemList;
    //验收标准卡对应具体的验收小项集合
    private ArrayList<AcceptCardItem> cardItemList;
    //签字显示图片view
    private ImageView imgView;
    //装载EditText
    ArrayList<TextView> etJchuList = new ArrayList<TextView>();
    //验收类型id
    private String typeId;
    //验收设备id
    private String deviceId;
    //验收类型
    private String yanShouType;
    //新建标卡时会新建一个report
    private String reportId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_yanshou_standard, null);
        x.view().inject(view);
        reportId = UUID.randomUUID().toString();
        return view;
    }

    @Override
    protected void lazyLoad() {
        exListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int i) {
                if (i == 0) {
                    llDTContainer.setVisibility(View.GONE);
                    ivArrow.setBackgroundResource(R.mipmap.icon_down);
                }
            }
        });

    }

    @Override
    protected void initUI() {
        ivArrow.setBackgroundResource(R.mipmap.icon_up);
        typeId = getArguments().getString("4");
        deviceId = getArguments().getString(Config.DEVICE_ID);
        yanShouType = getArguments().getString(Config.TYPE_NAME);
        try {
            acceptStandCard = CustomApplication.getYanShouDbManager().selector(AcceptStandCard.class).where(AcceptStandCard.ACCEPT_TYPE_ID, "=", typeId).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initData() {

        try {
            acceptCardItemList = (ArrayList<AcceptCardItem>) CustomApplication.getYanShouDbManager().selector(AcceptCardItem.class).where(AcceptCardItem.PARENT_ITEM_ID, "=", null).and(AcceptCardItem.ACCEPT_STANDER_CARD_ID, "=", acceptStandCard.acceptStanderCardId).findAll();

            for (AcceptCardItem mAcceptCardItem : acceptCardItemList) {
                ArrayList<AcceptCardItem> mCardItemList = new ArrayList<AcceptCardItem>();
                mCardItemList = (ArrayList<AcceptCardItem>) CustomApplication.getYanShouDbManager().selector(AcceptCardItem.class).where(AcceptCardItem.ACCEPT_STANDER_CARD_ID, "=", mAcceptCardItem.cardId).and(AcceptCardItem.PARENT_ITEM_ID, "!=", null).and(AcceptCardItem.PARENT_ITEM_ID, "=", mAcceptCardItem.itemId).findAll();
                AcceptCardItem mAcItem = new AcceptCardItem();
                mAcItem.cardId = mAcceptCardItem.cardId;
                mCardItemList.add(mAcItem);
                yanShouHashMap.put(mAcceptCardItem.itemContent.toString(), mCardItemList);
                yanShouListGroup.add(mAcceptCardItem.itemContent);
            }
            String json = acceptStandCard.acceptStanderCardBaseInfo;
            cardName = acceptStandCard.acceptStanderCardName;
            HashMap<String, String> map = (HashMap<String, String>) JSONUtils.parseKeyAndValueToMap(json);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                listPager.add(entry.getKey());
                contentList.add(entry.getValue());
            }

        } catch (DbException e) {
            e.printStackTrace();
        }

        tvGroupTitle.setText(cardName);
    }

    boolean isShow = true;

    @Event({R.id.ll_container_add, R.id.re_container, R.id.btn_confirm})
    private void onClickEvent(View view) {
        switch (view.getId()) {
            case R.id.ll_container_add://点击新建验收标准卡时控制（新建标准卡布局、验收标准卡具体内容布局的显示）
                if (!showMoreXuanXk()) {
                    viewBack.setVisibility(View.VISIBLE);
                    llContainer.setVisibility(View.VISIBLE);
                    relContainer.setVisibility(View.GONE);
                    btConfirm.setVisibility(View.VISIBLE);
                    refreshData();
                }

                break;
            case R.id.re_container://点击基础信息控制（其子布局时候显示，以及右边箭头符号显示状态）
                if (isShow) {
                    llDTContainer.setVisibility(View.GONE);
                    ivArrow.setBackgroundResource(R.mipmap.icon_down);
                    isShow = false;
                } else {
                    llDTContainer.setVisibility(View.VISIBLE);
                    ivArrow.setBackgroundResource(R.mipmap.icon_up);
                    isShow = true;
                }
                break;
            case R.id.btn_confirm://保存信息并且控制（新建标准卡布局、验收标准卡具体内容布局的显示状态）
                if (!saveJchuInfor()) {
                    CToast.showShort(getActivity(), "请填写完所有信息");
                    return;
                }
                viewBack.setVisibility(View.GONE);
                llContainer.setVisibility(View.GONE);
                relContainer.setVisibility(View.VISIBLE);
                btConfirm.setVisibility(View.GONE);
                break;
            default:
                break;
        }

    }

    /**
     * 查询当前验收类型下的选项卡的数目
     */
    ArrayList<AcceptStandCard> listTypeCard = new ArrayList<AcceptStandCard>();

    private boolean showMoreXuanXk() {
        try {
            listTypeCard = (ArrayList<AcceptStandCard>) CustomApplication.getYanShouDbManager().selector(AcceptStandCard.class).where(AcceptStandCard.ACCEPT_TYPE_ID, "=", typeId).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (listTypeCard != null && listTypeCard.size() > 1) {
            showTypeCardDialog();
            return true;
        }
        return false;
    }

    private TypeCardHolder cardHolder;
    private Dialog cardDialog;
    private CardTypeAdapter mCardTypeAdpter;

    /**
     * 点击新建验收标准卡时展示一个验收类型下的选项卡的种类dialog
     */
    private void showTypeCardDialog() {
        int dialogWidth = ScreenUtils.getScreenWidth(mCurrentActivity) * 4 / 5;
        if (cardHolder == null) {
            cardHolder = new TypeCardHolder();
        }
        if (cardDialog == null) {
            cardDialog = DialogUtils.createDialog(mCurrentActivity, null, R.layout.carddialog_list, cardHolder, dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        if (mCardTypeAdpter == null) {
            mCardTypeAdpter = new CardTypeAdapter(getActivity(), listTypeCard);
            cardHolder.lvCrad.setAdapter(mCardTypeAdpter);
        } else {
            mCardTypeAdpter.setList(listTypeCard);
        }
        cardDialog.show();
        cardHolder.lvCrad.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                acceptStandCard = (AcceptStandCard) adapterView.getItemAtPosition(i);
                cardDialog.dismiss();
                listTypeCard.clear();
                acceptCardItemList.clear();
                yanShouHashMap.clear();
                yanShouListGroup.clear();
                listPager.clear();
                contentList.clear();
                viewBack.setVisibility(View.VISIBLE);
                llContainer.setVisibility(View.VISIBLE);
                relContainer.setVisibility(View.GONE);
                btConfirm.setVisibility(View.VISIBLE);
                initData();
                refreshData();
            }
        });

    }

    class TypeCardHolder {
        @ViewInject(R.id.lv_card)
        private ListView lvCrad;
    }

    /**
     * 保存基础信息内容
     */

    private boolean saveJchuInfor() {
        JSONArray jsonarray = new JSONArray();//json数组，里面包含的内容为pet的所有对象
        AcceptReport mAcceptReport = new AcceptReport();
        AcceptReport.StanderBaseInfo asInfor = mAcceptReport.new StanderBaseInfo();
        for (int i = 0; i < listPager.size(); i++) {
            try {
                JSONObject jsonObj = new JSONObject();//pet对象，json形式
                jsonObj.put("name", listPager.get(i));//向pet对象里面添加值
                jsonObj.put("type", contentList.get(i));
                if (TextUtils.isEmpty(etJchuList.get(i).getText().toString())) {
                    return true;
                }
                jsonObj.put("value", etJchuList.get(i).getText().toString());
                // 把每个数据当作一对象添加到数组里
                jsonarray.put(jsonObj);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        String result = jsonarray.toString();
        mAcceptReport.reportId = reportId;
        mAcceptReport.deviceId = deviceId;
        mAcceptReport.acceptTypeId = typeId;
        mAcceptReport.acceptTypeName = yanShouType;
        mAcceptReport.acceptStanderCardId = acceptStandCard.acceptStanderCardId;
        mAcceptReport.acceptStanderCardName = acceptStandCard.acceptStanderCardName;
        mAcceptReport.standerCardBaseInfo = result;
        try {
            CustomApplication.getYanShouDbManager().saveOrUpdate(mAcceptReport);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return true;
    }


    private void refreshData() {
        mHandler.sendEmptyMessage(Config.LOAD_DATA);
    }

    @Override
    protected void onRefresh(Message msg) {
        switch (msg.what) {
            case Config.LOAD_DATA:
                mTvPanorama.setText("变压器基础信息");
                reportId = UUID.randomUUID().toString();//每次新建标准卡时都会产生reportId
                addView();
                if (mYaShouCLAdapter == null) {
                    mYaShouCLAdapter = new YanShouStandardCLExPandAdapter(getActivity());
                    exListView.setAdapter(mYaShouCLAdapter);
                    mYaShouCLAdapter.setGroupMap(yanShouHashMap);
                    mYaShouCLAdapter.setGroupList(yanShouListGroup);
                    mYaShouCLAdapter.reportId = reportId;
                } else {
                    mYaShouCLAdapter.setGroupMap(yanShouHashMap);
                }
                mYaShouCLAdapter.setItemClickListener(this);
                break;
            default:
                break;
        }
        super.onRefresh(msg);
    }

    /**
     * 动态添加基础信息布局
     */
    private void addView() {
        llDTContainer.removeAllViews();
        llDTContainer.setVisibility(View.VISIBLE);
        viewBack.setVisibility(View.VISIBLE);
        if (listPager != null && !listPager.isEmpty()) {
            for (int i = 0; i < listPager.size(); i++) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.expandlist_jichu_listview_item, null);
                TextView tvName = (TextView) view.findViewById(R.id.tv_jichu_name);
                EditText etInput = (EditText) view.findViewById(R.id.et_input_jichu_part);
                final TextView tvCalendar = (TextView) view.findViewById(R.id.tv_calendar);
                if (listPager.get(i).contains("日期")) {
                    etJchuList.add(tvCalendar);
                    etInput.setVisibility(View.GONE);
                    tvCalendar.setVisibility(View.VISIBLE);
                    tvCalendar.setText(DateUtils.getCurrentShortTime());
                    tvCalendar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CustomerDialog.showDatePickerDialog(getActivity(), false, new CustomerDialog.DialogItemClickListener() {
                                @Override
                                public void confirm(String result, int position) {
                                    tvCalendar.setText(result.substring(0, 10));
                                }
                            });
                        }
                    });
                } else {
                    etJchuList.add(etInput);
                    etInput.setVisibility(View.VISIBLE);
                    tvCalendar.setVisibility(View.GONE);
                }
                tvName.setText(listPager.get(i));
                llDTContainer.addView(view, i);
            }
        }

    }

    /**
     * 材料验收列表之控件按钮响应事件
     */
    AcceptCardItem mAccpCardItem;
    ImageView ivTakePic;

    @Override
    public void itemClick(View v, AcceptCardItem acceptCardItem, int position, int totalCount, ImageView imageView, Button btYanShou, ImageView ivOther) {
        switch (v.getId()) {
            case R.id.btn_extra_function:
                showYanshouDialog(acceptCardItem, btYanShou, ivOther);
                break;
            case R.id.btn_yanshou:
                if (!TextUtils.isEmpty(acceptCardItem.itemContent)) {
                    ivTakePic = ivOther;
                    Intent intent = new Intent(mCurrentActivity, DialogActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Config.ACCPETCARD_ITEM, acceptCardItem);
                    bundle.putString(Config.REPORT_ID, reportId);
                    intent.putExtras(bundle);
                    getActivity().startActivityForResult(intent, Config.PAIZHAO_LUXIANG_REQUSET);
                    getActivity().overridePendingTransition(R.anim.activity_open, 0);
                } else {
                    if (!(mAccpCardItem == acceptCardItem)) {
                        qmPath = UUID.randomUUID().toString() + ".jpg";
                    }
                    mAccpCardItem = acceptCardItem;
                    imgView = imageView;
                    ivMap.put((AcceptCardItem) imageView.getTag(), qmPath);
                    Intent intent = new Intent(mCurrentActivity, SignNameActivity.class);
                    intent.putExtra(Config.SIGN_FILENAME, Config.SIGN_PICTURE_FOLDER + qmPath);
                    getActivity().startActivityForResult(intent, Config.QIAN_MING_REQUEST);
                }
                break;
            case R.id.iv_show_qianming:
                String qmName = ivMap.get(imageView.getTag());
                showImageDetails(getActivity(), 0, Config.SIGN_PICTURE_FOLDER + qmName, false);
                break;
            default:
                break;
        }
    }


    private Dialog yanShouDialog;
    //具体验收小项的名称（解析json后的集合）
    private ArrayList<String> listName = new ArrayList<String>();
    //具体验收小项的名称的类型（String，int,or date...）（解析json后的集合）
    private ArrayList<String> listType = new ArrayList<String>();
    //具体验收小项的名称单位集合（解析json后的集合）
    private ArrayList<String> listUnit = new ArrayList<String>();
    private YanShouDialogHolder yanShouHolder;
    private ArrayList<TextView> clTvList = new ArrayList<TextView>();
    private String json;
    private AcceptReportItem mAReportItem;

    /**
     *
     * 点击具体验收大项下面的验收按钮时弹出对应的布局
     *
     * */
    private void showYanshouDialog(AcceptCardItem acceptCardItem, Button btYanShou, ImageView ivOther) {
        listName.clear();
        listType.clear();
        listUnit.clear();
        clTvList.clear();
        json = acceptCardItem.extraContent;
        int dialogWidth = ScreenUtils.getScreenWidth(mCurrentActivity) * 4 / 5;
        if (yanShouHolder == null) {
            yanShouHolder = new YanShouDialogHolder();
        }
        if (yanShouDialog == null) {
            yanShouDialog = DialogUtils.createDialog(mCurrentActivity, null, R.layout.dialog_yanshou, yanShouHolder, dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        yanShouHolder.etProblem.setText("");
        yanShouHolder.rbNo.setChecked(false);
        yanShouHolder.rbYes.setChecked(false);
        try {
            mAReportItem = CustomApplication.getYanShouDbManager().selector(AcceptReportItem.class).where(AcceptReportItem.REPORT_ID, "=", reportId).and(AcceptReportItem.REPORT_ITEM_ID, "=", acceptCardItem.itemId).findFirst();
            if (mAReportItem != null) {
                if (!TextUtils.isEmpty(mAReportItem.itemExtraContent)) {
                    json = mAReportItem.itemExtraContent;
                }
                String problem = mAReportItem.itemQuestionRemark;
                yanShouHolder.etProblem.setText(problem);
                if (0 == mAReportItem.isNormal) {
                    yanShouHolder.rbYes.setChecked(true);
                    yanShouHolder.rbNo.setChecked(false);
                } else if (-1 == mAReportItem.isNormal) {
                    yanShouHolder.rbYes.setChecked(false);
                    yanShouHolder.rbNo.setChecked(true);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(acceptCardItem.extraContent)) {
            JSONArray jsonArray = null;
            yanShouHolder.llContainer.removeAllViews();
            yanShouHolder.llContainer.setVisibility(View.VISIBLE);
            try {
                jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    listName.add(jsonObject.getString("name"));
                    listType.add(jsonObject.getString("type"));
                    listUnit.add(jsonObject.getString("unit"));
                    View view = LayoutInflater.from(mCurrentActivity).inflate(R.layout.dongtai_tianjia_view, null);
                    TextView tvName = (TextView) view.findViewById(R.id.tv_name);
                    EditText etInput = (EditText) view.findViewById(R.id.et_input);
                    etInput.setInputType(EditorInfo.TYPE_CLASS_PHONE);
                    if (!TextUtils.isEmpty(jsonObject.getString("unit").toString().trim())) {
                        tvName.setText(jsonObject.getString("name") + "(" + jsonObject.getString("unit") + ")");
                    } else {
                        tvName.setText(jsonObject.getString("name"));
                    }
                    if (mAReportItem != null) {
                        if (!TextUtils.isEmpty(mAReportItem.itemExtraContent)) {
                            etInput.setText(jsonObject.getString("value"));
                        }
                    }
                    clTvList.add(etInput);
                    yanShouHolder.llContainer.addView(view, i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            yanShouHolder.llContainer.setVisibility(View.GONE);
        }
        yanShouHolder.setHolder(yanShouHolder, acceptCardItem, btYanShou, ivOther);
        yanShouDialog.show();
    }

    class YanShouDialogHolder {
        private AcceptCardItem mAcceptItem;
        private YanShouDialogHolder holder;
        @ViewInject(R.id.ll_container)
        private LinearLayout llContainer;
        @ViewInject(R.id.rb_yes)
        private CustomRadioButton rbYes;
        @ViewInject(R.id.rb_no)
        private CustomRadioButton rbNo;
        @ViewInject(R.id.et_input_problem)
        private EditText etProblem;

        Button btYanShou;
        ImageView ivOther;

        private void setHolder(YanShouDialogHolder holder, AcceptCardItem mAcceptItem, Button btYanShou, ImageView ivOther) {
            this.mAcceptItem = mAcceptItem;
            this.holder = holder;
            this.btYanShou = btYanShou;
            this.ivOther = ivOther;
        }

        @Event({R.id.btn_finish, R.id.rb_no, R.id.rb_yes, R.id.btn_cancle})
        private void onViewClick(View view) {
            switch (view.getId()) {
                case R.id.btn_finish:
                    if (!saveCliaoYanShou(holder, mAcceptItem)) {
                        CToast.showLong(getActivity(), "请填写完所有信息");
                        return;
                    }
                    yanShouDialog.dismiss();
                    break;
                case R.id.rb_no:
                    rbNo.setChecked(true);
                    rbYes.setChecked(false);
                    break;
                case R.id.rb_yes:
                    rbYes.setChecked(true);
                    rbNo.setChecked(false);
                    break;
                case R.id.btn_cancle:
                    yanShouDialog.dismiss();
                default:
                    break;
            }

        }
    }

    /**
     * 材料验收保存项
     */
    private AcceptReportItem aReportItem;

    private boolean saveCliaoYanShou(YanShouDialogHolder holder, AcceptCardItem mAcceptItem) {
        try {
            aReportItem = CustomApplication.getYanShouDbManager().selector(AcceptReportItem.class).where(AcceptReportItem.REPORT_ITEM_ID, "=", mAcceptItem.itemId).and(AcceptReportItem.REPORT_ID, "=", reportId).findFirst();
            if (aReportItem == null) {
                aReportItem = new AcceptReportItem();
                aReportItem.isNormal = -2;
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        aReportItem.reportItemId = mAcceptItem.itemId;
        aReportItem.reportId = reportId;
        aReportItem.standerItemId = mAcceptItem.cardId;
        aReportItem.standerItemName = mAcceptItem.itemContent;
        aReportItem.isParent = 0;
        if (holder.rbYes.isChecked()) {
            aReportItem.isNormal = 0;
            holder.btYanShou.setBackgroundResource(R.drawable.grass_green_button_background_selector);
            holder.btYanShou.setText("合格");
        } else if (holder.rbNo.isChecked()) {
            aReportItem.isNormal = -1;
            holder.btYanShou.setBackgroundResource(R.drawable.red_button_background_selector);
            holder.btYanShou.setText("不合格");
        }
        aReportItem.itemQuestionRemark = holder.etProblem.getText().toString();
        String result = getExtraContent();
        if ("".equals(result)) {
            aReportItem.itemExtraContent = "";
        } else {
            aReportItem.itemExtraContent = result;
        }
        try {
            CustomApplication.getYanShouDbManager().saveOrUpdate(aReportItem);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 将验收需要手动填写的具体内容转化为json格式，
     *
     * */
    private String getExtraContent() {
        JSONArray jsonarray = new JSONArray();//json数组，里面包含的内容为pet的所有对象
        AcceptReportItem aReportItem = new AcceptReportItem();
        AcceptReportItem.ItemExtraContent asInfor = aReportItem.new ItemExtraContent();
        for (int i = 0; i < clTvList.size(); i++) {
            try {
                JSONObject jsonObj = new JSONObject();//pet对象，json形式
                jsonObj.put("name", listName.get(i));//向pet对象里面添加值
                jsonObj.put("type", listType.get(i));
                jsonObj.put("unit", listUnit.get(i));
                if (TextUtils.isEmpty(clTvList.get(i).getText().toString())) {
                    return "";
                }
                jsonObj.put("value", clTvList.get(i).getText().toString());
                // 把每个数据当作一对象添加到数组里
                jsonarray.put(jsonObj);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return jsonarray.toString();
    }

    /**
     * 材料验收整个item点击查看详情内容
     **/
    @Override
    public void onitemClick(AcceptCardItem acceptCardItem, int i, int total, int groupPosition) {
        if (!TextUtils.isEmpty(acceptCardItem.itemContent)) {
            showInformationDialog(acceptCardItem, i, total);
        }

    }

    private Dialog mDialog;
    private DialogHoler holder = null;
    /**
     * 点击验收大项下面的具体小项整个item显示详情
     *
     */
    private void showInformationDialog(AcceptCardItem acceptCardItem, int i, int total) {
        int dialogWidth = ScreenUtils.getScreenWidth(mCurrentActivity) * 4 / 5;
        if (holder == null) {
            holder = new DialogHoler();
        }
        if (mDialog == null) {

            mDialog = DialogUtils.createDialog(mCurrentActivity, null, R.layout.dialog_item_more_information, holder, dialogWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        mDialog.setCanceledOnTouchOutside(true);
        holder.tvDialogTitle.setText("验收标准及检查方式");
        holder.tvCheck.setText(acceptCardItem.itemCheckStyle);
        holder.tvStandard.setText(acceptCardItem.itemStander);
        mDialog.show();
    }

    class DialogHoler {
        @ViewInject(R.id.btn_finish)
        private Button btFinish;

        @ViewInject(R.id.tv_title)
        private TextView tvDialogTitle;
        @ViewInject(R.id.tv_check_method_content)
        private TextView tvCheck;
        @ViewInject(R.id.tv_yanshou_standard_content)
        private TextView tvStandard;

        @Event({R.id.btn_finish})
        private void onViewClick(View view) {
            switch (view.getId()) {
                case R.id.btn_finish:
                    mDialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    }

    private String recordName;
    private String videoName;
    private ArrayList<String> picList;
    private String picName;
    private HashMap<AcceptCardItem, String> ivMap = new HashMap<AcceptCardItem, String>();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case Config.QIAN_MING_REQUEST://签名请求返回
                File file = new File(Config.SIGN_PICTURE_FOLDER + qmPath);
                if (file.exists()) {
                    Bitmap bm = BitmapFactory.decodeFile(Config.SIGN_PICTURE_FOLDER + qmPath);
                    imgView.setVisibility(View.VISIBLE);
                    imgView.setImageBitmap(bm);
                    try {
                        aReportItem = CustomApplication.getYanShouDbManager().selector(AcceptReportItem.class).where(AcceptReportItem.REPORT_ITEM_ID, "=", mAccpCardItem.itemId).and(AcceptReportItem.REPORT_ID, "=", reportId).findFirst();
                        if (aReportItem == null) {
                            aReportItem = new AcceptReportItem();
                        }
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    aReportItem.itemImages = qmPath;
                    aReportItem.itemSign = qmPath;
                    aReportItem.reportItemId = mAccpCardItem.itemId;
                    aReportItem.reportId = reportId;
                    aReportItem.standerItemId = mAccpCardItem.cardId;
                    aReportItem.standerItemName = mAccpCardItem.itemContent;
                    aReportItem.isParent = 1;
                    saveData(aReportItem);
                }


                break;
            case Config.PAIZHAO_LUXIANG_REQUSET://拍照、录像、录音返回
                recordName = data.getStringExtra(Config.AUDIO_NAME);
                videoName = data.getStringExtra(Config.VIDEO_NAME);
                picList = data.getStringArrayListExtra(Config.PIC_NAME);
                if (picList != null && picList.size() > 0 || !TextUtils.isEmpty(recordName) || !TextUtils.isEmpty(videoName)) {
                    picName = StringUtils.ArrayListToString(picList);
                    ivTakePic.setImageResource(R.mipmap.icon_addshow);
                } else {
                    ivTakePic.setImageResource(R.mipmap.icon_notshow);
                }
                break;
            default:
                break;
        }
    }

    public void saveData(Object object) {
        try {
            CustomApplication.getYanShouDbManager().saveOrUpdate(object);

        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示大图片
     *
     * @param position
     */
    public void showImageDetails(Activity context, int position, String qmPath, boolean isShowDelete) {
        ArrayList<String> mImageUrlList = new ArrayList<String>();
        mImageUrlList.add(qmPath);
        Intent intent = new Intent(getActivity(), ImageDetailsActivity.class);
        intent.putExtra(Config.CURRENT_IMAGE_POSITION, position);
        if (mImageUrlList != null) {
            intent.putStringArrayListExtra(Config.IMAGEURL_LIST, mImageUrlList);
        }
        intent.putExtra(Config.IS_SHOW_PHOTO_FLAG, isShowDelete);
        context.startActivityForResult(intent, Config.CANCEL_RESULT_LOAD_IMAGE);
    }

}
