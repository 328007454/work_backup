package com.cnksi.defect.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;

import com.cnksi.core.adapter.BaseAdapter;
import com.cnksi.defect.databinding.AdapterDefectTypeBinding;

import java.util.Collection;

/**
 * Created by han on 2017/4/25.
 */

public class DefectTypeAdapter extends BaseAdapter<String> {
    public DefectTypeAdapter(Context context, Collection data, int layoutId) {
        super(context, data, layoutId);
    }


    @Override
    public void convert(ViewDataBinding dataBinding, String item, int position) {
        AdapterDefectTypeBinding binding = (AdapterDefectTypeBinding) dataBinding;
        binding.defectTypeName.setText(item);
    }
}
