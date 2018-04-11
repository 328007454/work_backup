package com.cnksi.bdzinspection.adapter.base;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int spacingLR;
    private int spacingTB;
    private boolean includeEdge;

    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacingLR= spacing;
        this.spacingTB=spacing;
        this.includeEdge = includeEdge;
    }

    public GridSpacingItemDecoration(int spanCount, int spacingLR, int spacingTB, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacingLR = spacingLR;
        this.spacingTB = spacingTB;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item column

        if (includeEdge) {
            outRect.left = spacingLR - column * spacingLR / spanCount; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * spacingLR / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

            if (position < spanCount) { // top edge
                outRect.top = spacingTB;
            }
            outRect.bottom = spacingTB; // item bottom
        } else {
            outRect.left = column * spacingLR / spanCount; // column * ((1f / spanCount) * spacing)
            outRect.right = spacingLR - (column + 1) * spacingLR / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount) {
                outRect.top = spacingTB; // item top
            }
        }
    }


}