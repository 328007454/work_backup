package com.cnksi.bdzinspection.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by han on 2017/3/11.
 */

public class GridLayoutItemSpacing extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int horizantalSpacing;
    private boolean includeEdge;

    public GridLayoutItemSpacing(int spanCount, int horizantalSpacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.horizantalSpacing = horizantalSpacing;
        this.includeEdge = includeEdge;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item column

        if (includeEdge) {
            if (position == 0) {
                outRect.left = horizantalSpacing - column * horizantalSpacing / spanCount; // horizantalSpacing - column * ((1f / spanCount) * horizantalSpacing)
            } else {
                outRect.left = (horizantalSpacing - 21) - (column * (horizantalSpacing - 21)) / spanCount; // horizantalSpacing - column * ((1f / spanCount) * horizantalSpacing)
            }
            outRect.right = (column + 1) * (horizantalSpacing - 21) / spanCount; // (column + 1) * ((1f / spanCount) * horizantalSpacing)

            outRect.top = 0;
            outRect.bottom = 0; // item bottom
        } else {
            outRect.left = column * horizantalSpacing / spanCount; // column * ((1f / spanCount) * horizantalSpacing)
            outRect.right = horizantalSpacing - (column + 1) * horizantalSpacing / spanCount; // horizantalSpacing - (column + 1) * ((1f /    spanCount) * horizantalSpacing)
            if (position >= spanCount) {
                outRect.top = horizantalSpacing; // item top
            }
        }
    }
}
