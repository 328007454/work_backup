package com.cnksi.sjjc.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;

import com.cnksi.sjjc.databinding.PeopeNameBinding;

import java.util.List;

/**
 * Created by han on 2016/8/10.
 */
public class AddPeopleAdapter extends SimpleBindingAdatpter<String> {

    private PeopeNameBinding nameBinding;
    public AddPeopleAdapter(Context context,List<String> list,int layoutId){
        super(context,list,layoutId);
    }

    @Override
    public void convert(ViewDataBinding dataBinding, String item, int position) {

        setNameBinding((PeopeNameBinding) dataBinding);
        nameBinding.peopeName.setText(item);
    }

    public void setNameBinding(PeopeNameBinding nameBinding){
        this.nameBinding = nameBinding;
    }

    public PeopeNameBinding getNameBinding(){
        return  nameBinding;
    }
}
