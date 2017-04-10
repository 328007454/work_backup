package com.cnksi.sjjc.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.cnksi.core.utils.CToast;
import com.cnksi.core.utils.PreferencesUtils;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.CustomApplication;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.adapter.TypeAdapter;
import com.cnksi.sjjc.bean.TJWT;
import com.cnksi.sjjc.enmu.InspectionType;
import com.cnksi.sjjc.inter.ItemClickListener;
import com.cnksi.sjjc.service.TJWTService;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 每种类型列表
 */
public class TypeListActivity extends BaseActivity {

    private InspectionType mInspectionType;

    @ViewInject(R.id.type_list)
    private ListView typeListView;

    private List<String> data;
    private TypeAdapter adapter;

    /**图解五通title*/
    private List<String> titleList = new ArrayList<>();
    /**图解五通数据集*/
    private List<TJWT> tjwtList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setChildView(R.layout.activity_type_list);
        x.view().inject(_this);
        String mInspectionValue = getIntent().getStringExtra(Config.CURRENT_INSPECTION_TYPE_NAME);
        mInspectionType = InspectionType.get(mInspectionValue);

        initUI();
        initData();

    }

    private void initUI() {
        tvTitle.setText(mInspectionType.value);
    }

    private void initData() {
        data = new ArrayList<String>();
        adapter = new TypeAdapter(this, data, R.layout.item_type, mInspectionType);
        typeListView.setAdapter(adapter);
        List<String> typeList = null;
        switch (mInspectionType) {
            case SBXS:
                typeList = Arrays.asList(getResources().getStringArray(R.array.SBXS));
                break;
            case GZP:
                typeList = Arrays.asList(getResources().getStringArray(R.array.GZP));
                break;
            case SBJC:
                typeList = Arrays.asList(getResources().getStringArray(R.array.SBJC));
                break;
            case switchover:
                typeList = Arrays.asList(getResources().getStringArray(R.array.DQSY));
                break;
            case maintenance:
                typeList = Arrays.asList(getResources().getStringArray(R.array.DQWH));
                break;
            case exclusive:
                typeList = Arrays.asList(getResources().getStringArray(R.array.ZXXS));
                break;
            case JYHYS:
                typeList = Arrays.asList(getResources().getStringArray(R.array.JYHYS));
                break;
            case JYHPJ:
                typeList = Arrays.asList(getResources().getStringArray(R.array.JYHPJ));
                break;
            case TJWT: //图解五通
                tjwtList = TJWTService.getInstance().findAllTJWT();
                if(tjwtList != null && tjwtList.size() != 0){
                    for(TJWT tjwt : tjwtList){
                        titleList.add(tjwt.name);
                    }
                }
                typeList = titleList;
                break;
            case operation:

        }
        if (null != typeList && !typeList.isEmpty()) {
            data.clear();
            data.addAll(typeList);
            adapter.notifyDataSetChanged();
        }

        adapter.setItemClickListener(new ItemClickListener<String>() {
            @Override
            public void itemClick(View v, String s, int position) {
                int index=s.lastIndexOf(" ");

                int maxPosition = Integer.valueOf(s.substring(index+1,s.length()));
                s=s.substring(0,index);
                if (position >= maxPosition) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            CToast.showShort(_this, "该功能暂未激活");
                        }
                    });
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra(Config.CURRENT_LOGIN_USER,  PreferencesUtils.get(_this, Config.CURRENT_LOGIN_USER, ""));
                intent.putExtra(Config.CURRENT_LOGIN_ACCOUNT, PreferencesUtils.get(_this, Config.CURRENT_LOGIN_ACCOUNT, ""));
                ComponentName componentName;
                switch (mInspectionType) {
                    case SBXS:
                        CustomApplication.closeDbConnection();
                        componentName = new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.TaskRemindActivity");
                        //正常巡视
                        if ("正常巡视".equals(s)) {
                            intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.day.name());
                            intent.setComponent(componentName);
                        }
                        //特殊巡视
                        else if ("特殊巡视".equals(s)) {
                            intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.special.name());
                            intent.setComponent(componentName);
                        } else if ("全面巡视".equals(s)) {
                            intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.full.name());
                            intent.setComponent(componentName);
                        }
                        //例行巡视
                        else if ("例行巡视".equals(s)) {
                            intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.routine.name());
                            intent.setComponent(componentName);
                        } else if ("熄灯巡视".equals(s)) {
                            intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.special_xideng.name());
                            intent.setComponent(componentName);
                        } else if ("夜间巡视".equals(s)) {
                            intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.special_nighttime.name());
                            intent.setComponent(componentName);
                        }
//                        else if("故障巡视".equals(s)){
//                            intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.special_guzhang.name());
//                            intent.setComponent(componentName);
//                        }
                        else {
                            return;
                        }
                        break;
                    case SBJC:
                        intent.setClass(_this, TaskRemindActivity.class);
                        if (position == 10) {
                            intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.SBJC_01.name());
                        } else if (position == 11) {
                            intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.SBJC_02.name());
                        } else if (position == 0) {
                            intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.SBJC_03.name());
                        } else if (position == 1) {
                            intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.SBJC_04.name());
                        } else if (position == 2) {
                            intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.SBJC_05.name());
                        } else if (position == 3) {
                            intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.SBJC_12.name());
                        } else if (position == 4) {
                            intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.SBJC_09.name());
                        } else if (position == 6) {
                            intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.SBJC_10.name());
                        } else if (position == 7) {
                            intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.SBJC_11.name());
                        } else if (position == 5) {
                            intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.SBJC_06.name());
                        } else if (position == 9) {
                            intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.SBJC_07.name());
                        } else if (position == 8) {
                            intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.SBJC_08.name());
                        } else {
                            return;
                        }
                        break;
                    case switchover:
                        return;
                    case maintenance:
                        componentName = new ComponentName("com.cnksi.bdzinspection", "com.cnksi.bdzinspection.activity.TaskRemindActivity");
                        //全站卫生清洁
                        if (position == 0) {
                            intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.maintenance_01.name());
                            intent.setComponent(componentName);
                        }
                        //交、直流空开、保险检查
                        else if (position == 1) {
                            intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.maintenance_02.name());
                            intent.setComponent(componentName);
                        }
                        //全站照明检查
                        else if (position == 2) {
                            intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.maintenance_03.name());
                            intent.setComponent(componentName);
                        }
                        //消防设施检查、清洁
                        else if (position == 3) {
                            intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.maintenance_04.name());
                            intent.setComponent(componentName);
                        }
                        //蓄电池清扫
                        else if (position == 4) {
                            intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.maintenance_05.name());
                            intent.setComponent(componentName);
                        }
                        //备品、备件、工器具、仪表清洁
                        else if (position == 5) {
                            intent.putExtra(Config.CURRENT_INSPECTION_TYPE_NAME, InspectionType.maintenance_06.name());
                            intent.setComponent(componentName);
                        } else {
                            return;
                        }
                        break;
                    case exclusive:
                        return;
                    case JYHYS:
                        if (position == 0) {
                            intent.setClass(_this, QinYouShiByqYanShouActivity.class);
                            startActivity(intent);
                        }
                        return;
                    case JYHPJ:
                        if (position == 0) intent.setClass(_this, JYHPJActivity.class);
                        else return;
                        break;
                    case GZP:
                        if (position == 0)
                            intent.setClass(_this, WorkPlanInformationActivity.class);
                        else return;
                        break;
                    case TJWT: //图解五通
                        intent.setClass(_this,TJWTActivity.class);
                        intent.putExtra("title",titleList.get(position));
                        intent.putExtra("pic",tjwtList.get(position).pic);
                        break;
                }
                startActivity(intent);
            }

            @Override
            public void itemLongClick(View v, String s, int position) {

            }
        });

    }
}
