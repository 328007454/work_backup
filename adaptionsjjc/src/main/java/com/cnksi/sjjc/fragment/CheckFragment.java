package com.cnksi.sjjc.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.cnksi.core.fragment.BaseCoreFragment;
import com.cnksi.core.utils.CoreConfig;
import com.cnksi.core.utils.RelayoutUtil;
import com.cnksi.sjjc.Config;
import com.cnksi.sjjc.R;
import com.cnksi.sjjc.bean.EvaluationItem;
import com.cnksi.sjjc.bean.EvaluationItemReport;
import com.cnksi.sjjc.bean.InputValue;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ksi-android on 2016/5/11.
 */
public class CheckFragment extends BaseCoreFragment {

    /**
     * 检查方式
     */
    @ViewInject(R.id.tv_jcfs)
    private TextView tvJcfs;
    /**
     * 扣分原则
     */
    @ViewInject(R.id.tv_kfyz)
    private TextView tvKfyz;
    @ViewInject(R.id.rb_yes)
    private  RadioButton rbYes;
    @ViewInject(R.id.rb_no)
    private RadioButton rbNo;
    /**
     * 备注
     */
    @ViewInject(R.id.et_bz)
    private EditText etBz;
    /**
     * 扣分
     */
    @ViewInject(R.id.et_kf)
    private EditText etKf;

    @ViewInject(R.id.ll_jcjg)
    private LinearLayout llJcjg;
    private  EvaluationItem currentEvaluation;
    private EvaluationItemReport itemReport;
    List<InputValue> inputValues=new ArrayList<InputValue>();
    List<EditViewHolder> editViewHolders=new ArrayList<EditViewHolder>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check, container, false);
        x.view().inject(this,view);
        initData();
        return view;
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected void initUI() {

    }

    @Override
    protected void initData() {
        currentEvaluation = (EvaluationItem) getArguments().getSerializable(Config.DATA);
        itemReport=(EvaluationItemReport)getArguments().getSerializable(Config.DATA1);
        if (itemReport!=null)
        {
            if (!TextUtils.isEmpty(itemReport.itemExtra))
            {
                inputValues= JSON.parseArray(itemReport.itemExtra,InputValue.class);
            }else{
                if (!TextUtils.isEmpty(currentEvaluation.itemCheckExtra))
                {
                    inputValues= JSON.parseArray(currentEvaluation.itemCheckExtra,InputValue.class);
                }
            }
            if (itemReport.isNormal==0)
            {
                rbYes.setChecked(true);
            }else{
                rbNo.setChecked(true);
            }
            etKf.setText(itemReport.discountScore+"");
            etBz.setText(itemReport.itemRemark);
        }else{
            rbYes.setChecked(true);
        }
        tvJcfs.setText(currentEvaluation.itemCheckStyle);
        tvKfyz.setText(currentEvaluation.itemCheckStander);
        if (!TextUtils.isEmpty(currentEvaluation.itemCheckOption)){
            String[] str=currentEvaluation.itemCheckOption.split(CoreConfig.COMMA_SEPARATOR);
            if (str.length>1)
            {
                rbYes.setText(str[0]);
                rbNo.setText(str[1]);
            }
        }
        initJcjgUI();
    }

    private  void initJcjgUI()
    {

        for (InputValue inputValue:inputValues)
        {
            View v= LayoutInflater.from(getActivity()).inflate(R.layout.layout_jyhpj_edittext1,null);
            EditViewHolder holder=new EditViewHolder();
            RelayoutUtil.reLayoutViewHierarchy(v);
            x.view().inject(holder,v);
            holder.name.setText(inputValue.name+"("+inputValue.unit+")");
            holder.input.setText(inputValue.value);
            editViewHolders.add(holder);
            llJcjg.addView(v);
        }
    }

    class EditViewHolder{
        @ViewInject(R.id.tv_name)
        TextView name;
        @ViewInject(R.id.et_input)
        EditText input;
    }

    public EvaluationItemReport getSaveData()
    {
        EvaluationItemReport bean=new EvaluationItemReport();
        String score=etKf.getText().toString();
        if (TextUtils.isEmpty(score))
            bean.discountScore=0;
        else
            bean.discountScore=Float.valueOf(score);
        int count=inputValues.size();
        for (int i=0;i<count;i++)
        {
            inputValues.get(i).value=editViewHolders.get(i).input.getText().toString();
        }
        if (count>0)
        {
            bean.itemExtra= JSONArray.toJSONString(inputValues);
        }
        bean.isNormal=rbYes.isChecked()?0:-1;
        bean.itemRemark=etBz.getText().toString();
        return bean;
    }

}
