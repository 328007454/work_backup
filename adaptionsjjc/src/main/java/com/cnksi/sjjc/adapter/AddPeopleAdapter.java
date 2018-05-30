package com.cnksi.sjjc.adapter;

import android.content.Context;

import com.cnksi.common.base.BaseBindingAdapter;
import com.cnksi.sjjc.databinding.PeopeNameBinding;

import java.util.List;

/**
 * Created by han on 2016/8/10.
 */
public class AddPeopleAdapter extends BaseBindingAdapter<PeopeNameBinding,String> {


    public AddPeopleAdapter(Context context,List<String> list,int layoutId){
        super(context,list,layoutId);
    }

    @Override
    public void convert(PeopeNameBinding dataBinding, String item, int position) {
        dataBinding.peopeName.setText(item);
    }


}
