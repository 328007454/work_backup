package com.cnksi.defect;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.cnksi.common.listener.ItemClickListener;

/**
 * Created by Mr.K on 2018/5/25.
 */

public class PopWindowCustom implements ItemClickListener {


    @Override
    public void itemClick(View v, Object o, int position) {

    }

    @Override
    public void itemLongClick(View v, Object o, int position) {

    }


    public static class PopWindowBuilder {
        private RecyclerView recyclerView;

        private void setRecyclerView(RecyclerView view){
            this.recyclerView = view;
        }

//        private void setRecyclerAdater(RecyclerA )


    }
}
